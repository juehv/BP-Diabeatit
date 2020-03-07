package info.nightscout.androidaps.plugins.insulin;

import android.content.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

import info.nightscout.androidaps.MainApp;
import info.nightscout.androidaps.data.IobTotal;
import info.nightscout.androidaps.data.Profile;
import info.nightscout.androidaps.db.DatabaseHelper;
import info.nightscout.androidaps.diabeatit.predictions.PredictionsPlugin;
import info.nightscout.androidaps.interfaces.Constraint;
import info.nightscout.androidaps.logging.L;
import info.nightscout.androidaps.plugins.insulin.prediction.PredictionInputs;
import info.nightscout.androidaps.plugins.insulin.prediction.PredictionModel;
import info.nightscout.androidaps.plugins.insulin.prediction.SlopeBGPredictionModel;
import info.nightscout.androidaps.plugins.iob.iobCobCalculator.GlucoseStatus;
import info.nightscout.androidaps.plugins.treatments.TreatmentsPlugin;
import info.nightscout.androidaps.utils.T;

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

    BolusCalculator(Profile profile, int carbs, double cob, double bg, double correction) {
        this.profile = profile;
        this.carbs = carbs;
        this.cob = cob;
        this.bg = bg;
        this.correction = correction;

        calculate();
    }

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

    private void calculateInsulinFromTrend() {
        insulinFromTrend = 0.0;
        if (useTrend) {
            if (DatabaseHelper.lastBg() == null) return;
            float[] preds = PredictionsPlugin.getPlugin().getPredictions(Instant.now().toEpochMilli());
            float trend15min = preds[Math.min(3, preds.length - 1)];
            insulinFromTrend = trend / isf;
            /* TODO
            trend = predictionModel.get15minDelta(profile);
            insulinFromTrend = Profile.fromMgdlToUnits(trend, profile.getUnits()) / isf;
             */
        }
    }

    private void calculateInsulinFromCarbs() {
        ic = profile.getIc();
        insulinFromCarbs = carbs / ic;
    }

    private void calculateInsulinFromCob() {
        if (useCob) {
            insulinFromCOB = cob / ic;
        } else {
            insulinFromCOB = 0.0d;
        }
    }

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

    private void calculateInsulinFromCorrection() {
        insulinFromCorrection = correction;
    }

    private void calculateInsulinFromSuperBolus() {
        insulinFromSuperBolus = 0.0d;

        if (useSuperBolus) {
            insulinFromSuperBolus = profile.getBasal();
            long timeAfter1h = System.currentTimeMillis();
            timeAfter1h += T.hours(1).msecs();
            insulinFromSuperBolus += profile.getBasal(timeAfter1h);
        }
    }

    // be able to edit the inputs
    public void setProfile(Profile profile) {
        this.profile = profile;
        calculate();
    }

    public Profile getProfile() {
        return this.profile;
    }

    public void setCarbs(int carbs) {
        this.carbs = carbs;
        calculate();
    }

    public int getCarbs() {
        return carbs;
    }

    public void setCOB(double cob) {
        this.cob = cob;
        calculate();
    }

    public Double getCOB() {
        return cob;
    }

    public void setBG(double bg) {
        this.bg = bg;
        calculate();
    }

    public Double getBG() {
        return bg;
    }

    public void setCorrection(double correction) {
        this.correction = correction;
        calculate();
    }

    public Double getCorrection() {
        return correction;
    }

    // flags

    public boolean getUseBg() {
        return useBg;
    }

    public void setUseBg(boolean flag) {
        useBg = flag;
        calculate();
    }

    public boolean getUseCob() {
        return useCob;
    }

    public void setUseCob(boolean flag) {
        useCob = flag;
        calculate();
    }

    public boolean getIncludeBolusIob() {
        return includeBolusIOB;
    }

    public void setIncludeBolusIob(boolean flag) {
        includeBolusIOB = flag;
        calculate();
    }

    public boolean getIncludeBasalIob() {
        return includeBasalIOB;
    }

    public void setIncludeBasalIOB(boolean flag) {
        includeBasalIOB = flag;
        calculate();
    }

    public boolean getUseSuperBolus() {
        return useSuperBolus;
    }

    public void setUseSuperBolus(boolean flag) {
        useSuperBolus = flag;
        calculate();
    }

    public boolean getUseTrend() {
        return useTrend;
    }

    public void setUseTrend(boolean flag) {
        useTrend = flag;
        calculate();
    }

    // intermediaries

    public double getISF() {
        return isf;
    }

    public double getIC() {
        return ic;
    }

    public GlucoseStatus getGlucoseStatus() {
        return glucoseStatus;
    }

    public double getTargetBgLow() {
        return targetBgLow;
    }

    public double getTargetBgHigh() {
        return targetBgHigh;
    }

    public double getBgDiff() {
        return bgDiff;
    }

    public double getInsulinFromBG() {
        return insulinFromBG;
    }

    public double getInsulinFromCarbs() {
        return insulinFromCarbs;
    }

    public double getInsulinFromBolusIOB() {
        return insulinFromBolusIOB;
    }

    public double getInsulinFromBasalsIOB() {
        return insulinFromBasalsIOB;
    }

    public double getInsulinFromCorrection() {
        return insulinFromCorrection;
    }

    public double getInsulinFromSuperBolus() {
        return insulinFromSuperBolus;
    }

    public double getInsulinFromCOB() {
        return insulinFromCOB;
    }

    public double getInsulinFromTrend() {
        return insulinFromTrend;
    }

    public double getTrend() {
        return trend;
    }

    // outputs

    public double getCalculatedTotalInsulin() {
        return calculatedTotalInsulin;
    }

    public double getTotalBeforePercentageAdjustment() {
        return totalBeforePercentageAdjustment;
    }

    public double getInsulinAfterConstraints() {
        return insulinAfterConstraints;
    }

    public double getCarbsEquivalent() {
        return carbsEquivalent;
    }

    // Callbacks
    public void setOnCalculatedListener(OnCalculatedListener listener) {
        onCalculatedListener = listener;
    }

    // Events
    private void onCalculated() {
        if (onCalculatedListener != null)
            onCalculatedListener.onCalculated(this);
    }

    // Interface for callbacks
    @FunctionalInterface
    public interface OnCalculatedListener {
        void onCalculated(BolusCalculator source);
    }
}
