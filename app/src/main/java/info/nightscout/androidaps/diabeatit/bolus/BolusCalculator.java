package info.nightscout.androidaps.diabeatit.bolus;

import android.util.Log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.nightscout.androidaps.MainApp;
import info.nightscout.androidaps.data.IobTotal;
import info.nightscout.androidaps.data.Profile;
import info.nightscout.androidaps.db.BgReading;
import info.nightscout.androidaps.db.DatabaseHelper;
import info.nightscout.androidaps.diabeatit.predictions.PredictionsPlugin;
import info.nightscout.androidaps.interfaces.Constraint;
import info.nightscout.androidaps.logging.L;
import info.nightscout.androidaps.plugins.iob.iobCobCalculator.GlucoseStatus;
import info.nightscout.androidaps.plugins.treatments.TreatmentsPlugin;
import info.nightscout.androidaps.utils.T;

/**
 * Helper class to calculate the recommended bolus based on a number of arguments.
 */
public class BolusCalculator {
    private Logger log = LoggerFactory.getLogger(L.CORE);

    // inputs
    private Profile profile;
    private int carbs;
    private double cob;
    private double bg;
    private double correction;
    private double percentageCorrection = 100.0;

    private boolean useBg;
    private boolean useCob;
    private boolean includeBolusIOB;
    private boolean includeBasalIOB;
    private boolean useSuperBolus;
    // private boolean useTT;
    private boolean useTrend;

    // Intermediate
    private double isf;
    private double ic;
    private GlucoseStatus glucoseStatus;
    private double targetBgLow;
    private double targetBgHigh;
    private double bgDiff;

    private double insulinFromBG;
    private double insulinFromCarbs;
    private double insulinFromBolusIOB;
    private double insulinFromBasalsIOB;
    private double insulinFromCorrection;
    private double insulinFromSuperBolus;
    private double insulinFromCOB;
    private double insulinFromTrend;
    private double trend;

    // Result
    private double calculatedTotalInsulin;
    private double totalBeforePercentageAdjustment;
    private double insulinAfterConstraints;
    private double carbsEquivalent;

    // Callbacks
    private OnCalculatedListener onCalculatedListener;

    /**
     * Create a new {@link BolusCalculator} instance with the neccessary parameters
     * Use a {@link BolusCalculatorBuilder} for a more convenient interface.
     */
    BolusCalculator(Profile profile, int carbs, double cob, double bg, double correction) {
        this.profile = profile;
        this.carbs = carbs;
        this.cob = cob;
        this.bg = bg;
        this.correction = correction;

        calculate();
    }

    /** (Re)calculate the values */
    private void calculate() {
        isf = profile.getIsf();
        targetBgLow = profile.getTargetLow();
        targetBgHigh = profile.getTargetHigh();

        calcualateInsulinFromBG();
        glucoseStatus = GlucoseStatus.getGlucoseStatusData();
        calculateInsulinFromTrend();
        calculateInsulinFromCarbs();
        calculateInsulinFromCob();
        calculateInsulinFromIob();
        calculateInsulinFromCorrection();
        calculateInsulinFromSuperBolus();

        calculatedTotalInsulin = insulinFromBG
                + insulinFromTrend
                + insulinFromCarbs
                + insulinFromBolusIOB
                + insulinFromBasalsIOB
                + insulinFromCorrection
                + insulinFromSuperBolus
                + insulinFromCOB;

        totalBeforePercentageAdjustment = calculatedTotalInsulin;
        if (calculatedTotalInsulin > 0) {
            calculatedTotalInsulin = calculatedTotalInsulin * percentageCorrection / 100.0;
        }

        if (calculatedTotalInsulin < 0) {
            carbsEquivalent = (-calculatedTotalInsulin) * ic;
            calculatedTotalInsulin = 0.0d;
        }

        insulinAfterConstraints = MainApp.getConstraintChecker()
                .applyBolusConstraints(new Constraint<>(calculatedTotalInsulin)).value();

        onCalculated();
    }

    /** Calculate the part of the bolus that is based on the blood glucose levels */
    private void calcualateInsulinFromBG() {
        insulinFromBG = 0;
        if (useBg && bg > 0) {
            // TODO: Inclusive interval on both ends here, is this okay?
            if (bg >= targetBgLow && bg <= targetBgHigh) {
                bgDiff = 0.0d;
            } else if (bg <= targetBgLow) {
                bgDiff = bg - targetBgLow;
            } else { // bg >= targetBgHigh
                bgDiff = bg - targetBgHigh;
            }
            insulinFromBG = bgDiff / isf;
        }
    }

    /** Calculate the part of the bolus that is based on the trend of the blood glucose levels */
    private void calculateInsulinFromTrend() {
        insulinFromTrend = 0.0;
        if (useTrend) {
            BgReading lastBg = DatabaseHelper.lastBg();
            if (lastBg == null) {
                Log.e("BOLUS", "Cannot use trend without readings data.");
                return;
            }
            // TODO Maybe use interpolation to get the 15min trend from the current timestamp instead of the last reading? #
            // For the Bolus we general use 15min predictions.
            // Index 0 is the current time, then 5 minute intevals, so we want the 4th (index 3)
            float[] predictions = PredictionsPlugin.getPlugin().getPredictions(lastBg.date);
            insulinFromTrend = predictions[3] + lastBg.value;
        }
    }

    /** Calculate the part of the bolus that is based on the amount of carbs consumed */
    private void calculateInsulinFromCarbs() {
        ic = profile.getIc();
        insulinFromCarbs = carbs / ic;
    }

    /** Calculate the part of the bolus that is based on the remaining carbs-on-board */
    private void calculateInsulinFromCob() {
        if (useCob) {
            insulinFromCOB = cob / ic;
        } else {
            insulinFromCOB = 0.0d;
        }
    }

    /** Calculate the part of the bolus that is based on the remaining insulin-on-board */
    private void calculateInsulinFromIob() {
        insulinFromBolusIOB = 0.0d;
        insulinFromBasalsIOB = 0.0d;

        TreatmentsPlugin plugin = TreatmentsPlugin.getPlugin();
        plugin.updateTotalIOBTreatments();
        IobTotal bolusIob = plugin.getLastCalculationTreatments().round();
        plugin.updateTotalIOBTempBasals();
        IobTotal basalIob = plugin.getLastCalculationTempBasals().round();

        if (includeBolusIOB) {
            insulinFromBolusIOB = -bolusIob.iob;
        }
        if (includeBasalIOB) {
            insulinFromBasalsIOB = -basalIob.basaliob;
        }
    }

    /** Calculated the part of the bolus that is based no the manual correction given */
    private void calculateInsulinFromCorrection() {
        insulinFromCorrection = correction;
    }

    /** Calculate the part of the bolus that is based on the super bolus */
    private void calculateInsulinFromSuperBolus() {
        insulinFromSuperBolus = 0.0d;

        if (useSuperBolus) {
            insulinFromSuperBolus = profile.getBasal();
            long timeAfter1h = System.currentTimeMillis();
            timeAfter1h += T.hours(1).msecs();
            insulinFromSuperBolus += profile.getBasal(timeAfter1h);
        }
    }

    /**
     * Set the used {@link Profile}, this determines mainly the target area and various factors,
     * such as ISF
     */
    public void setProfile(Profile profile) {
        this.profile = profile;
        calculate();
    }

    /** Get the currently set {@link Profile} */
    public Profile getProfile() {
        return this.profile;
    }

    /** Set the amount of Carbs that are expected to be consumed */
    public void setCarbs(int carbs) {
        this.carbs = carbs;
        calculate();
    }

    /** Get the currently set amount of carbs */
    public int getCarbs() {
        return carbs;
    }

    /** Set the Carbs-on-Board, that is the still remaining Carbs in the system */
    public void setCOB(double cob) {
        this.cob = cob;
        calculate();
    }

    /** Get the currently set Carbs-on-Board */
    public Double getCOB() {
        return cob;
    }

    /** Set the current blood glucose level */
    public void setBG(double bg) {
        this.bg = bg;
        calculate();
    }

    /** Get the currently set blood glucose level */
    public Double getBG() {
        return bg;
    }

    /** Set a manual correction to be applied */
    public void setCorrection(double correction) {
        this.correction = correction;
        calculate();
    }

    /** Get the currently set correction */
    public Double getCorrection() {
        return correction;
    }

    // flags

    /** Get a flag representing if the blood glucose level is being considered in the calculation */
    public boolean getUseBg() {
        return useBg;
    }

    /** Set a flag representing if the blood glucose level should be considered in the calculation */
    public void setUseBg(boolean flag) {
        useBg = flag;
        calculate();
    }

    /** Get a flag representing if the Carbs-on-Board should be considered in the calculation */
    public boolean getUseCob() {
        return useCob;
    }

    /** Set a flag representing if the Carbs-on-Board should be considered in the calculation */
    public void setUseCob(boolean flag) {
        useCob = flag;
        calculate();
    }

    /** Get a flag representing if the bolus iob should be considered in the calculation */
    public boolean getIncludeBolusIob() {
        return includeBolusIOB;
    }

    /** Set a flag representing if the bolus iob should be considered in the calculation */
    public void setIncludeBolusIob(boolean flag) {
        includeBolusIOB = flag;
        calculate();
    }

    /** Get a flag representing if the basal iob should be considered in the calculation */
    public boolean getIncludeBasalIob() {
        return includeBasalIOB;
    }

    /** Set a flag representing if the basal iob should be considered in the calculation */
    public void setIncludeBasalIOB(boolean flag) {
        includeBasalIOB = flag;
        calculate();
    }

    /** Get a flag representing if the superbolus should be considered in the calculation */
    public boolean getUseSuperBolus() {
        return useSuperBolus;
    }

    /** Set a flag representing if the superbolus should be considered in the calculation */
    public void setUseSuperBolus(boolean flag) {
        useSuperBolus = flag;
        calculate();
    }

    /** Get a flag representing if the Trend should be considered in the calculation */
    public boolean getUseTrend() {
        return useTrend;
    }

    /** Set a flag representing if the Trend should be considered in the calcuation */
    public void setUseTrend(boolean flag) {
        useTrend = flag;
        calculate();
    }

    // intermediaries
    /** Get the determined Insulin Sensitivity Factor */
    public double getISF() {
        return isf;
    }

    public double getIC() {
        return ic;
    }

    /** Get the determined {@link GlucoseStatus} */
    public GlucoseStatus getGlucoseStatus() {
        return glucoseStatus;
    }

    /** Get the lower bound of the targeted blood glucose range*/
    public double getTargetBgLow() {
        return targetBgLow;
    }

    /** Get the higher bound of the targeted blood glucose range*/
    public double getTargetBgHigh() {
        return targetBgHigh;
    }

    /** Get the the difference between the current blood glucose level and the target range */
    public double getBgDiff() {
        return bgDiff;
    }

    /** Get the part of insulin that was calculated based on the blood glucose level */
    public double getInsulinFromBG() {
        return insulinFromBG;
    }

    /** Get the part of insulin that was calculated based on the expected Carb intake*/
    public double getInsulinFromCarbs() {
        return insulinFromCarbs;
    }

    /** Get the part of insulin that was calculated based on the bolus insulin-on-board */
    public double getInsulinFromBolusIOB() {
        return insulinFromBolusIOB;
    }

    /** Get the part of insulin that was calculated based on the basal insulin-on-board */
    public double getInsulinFromBasalsIOB() {
        return insulinFromBasalsIOB;
    }

    /** Get the part of insulin that was calculated based on the manual correction */
    public double getInsulinFromCorrection() {
        return insulinFromCorrection;
    }

    /** Get the part of insulin that was calculated based on the superbolus */
    public double getInsulinFromSuperBolus() {
        return insulinFromSuperBolus;
    }

    /** Get the part of insulin that was calculated based on the remaining Carbs-on-Board */
    public double getInsulinFromCOB() {
        return insulinFromCOB;
    }

    /** Get the part of insulin that was calculated based on the trend of the blood glucose level */
    public double getInsulinFromTrend() {
        return insulinFromTrend;
    }

    /** Get the trend of the blood glucose level */
    public double getTrend() {
        return trend;
    }

    // outputs

    /** Get the total amount of insulin calculated */
    public double getCalculatedTotalInsulin() {
        return calculatedTotalInsulin;
    }

    // Callbacks
    /** Set a callback to whenever the values were (re)caluclated */
    public void setOnCalculatedListener(OnCalculatedListener listener) {
        onCalculatedListener = listener;
    }

    // Events
    /** Called upon finished calculation */
    private void onCalculated() {
        if (onCalculatedListener != null)
            onCalculatedListener.onCalculated(this);
    }

    // Interface for callbacks

    /** Functional interfaces that serves as a Callback whenever the calculation is finished. */
    @FunctionalInterface
    public interface OnCalculatedListener {
        void onCalculated(BolusCalculator source);
    }
}
