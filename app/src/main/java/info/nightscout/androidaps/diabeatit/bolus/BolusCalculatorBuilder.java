package info.nightscout.androidaps.diabeatit.bolus;

import info.nightscout.androidaps.data.Profile;
import info.nightscout.androidaps.diabeatit.predictions.PredictionInputs;
import info.nightscout.androidaps.diabeatit.predictions.PredictionModel;

/**
 * Builder class to make creating a {@link BolusCalculator} more convenient.
 */
public class BolusCalculatorBuilder {
    // TODO: Better defaults!
    private Profile profile = null;
    private int carbs = 0;
    private double cob = 0.0d;
    private double bg = 0.0d;
    private double correction = 0.0d;

    private boolean useBg = true;
    private boolean useCob = true;
    private boolean includeBolusIOB = true;
    private boolean includeBasalIOB = true;
    private boolean useTrend = true;

    /** Set the profile used in the {@link BolusCalculator}
     *
     * @param profile   Profile to use
     */
    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    /** Set the amount of carbs
     *
     * @param carbs     Amount of carbs in gram
     */
    public void setCarbs(int carbs) {
        this.carbs = carbs;
    }

    /** Set the amount of carbs still in the system (Carbs-on-Board)
     *
     * @param cob   Amount of carbs remaining in the system in gram
     */
    public void setCOB(double cob) {
        this.cob = cob;
    }

    /** Set the current blood glucose level
     *
     * @param bg    Blood glucose level in mg/dl
     */
    public void setBG(double bg) {
        this.bg = bg;
    }

    /** Set the correction on the insulin
     *
     * @param correction    Correction to apply
     */
    public void setCorrection(double correction) {
        this.correction = correction;
    }

    /** Set a flag to set if the blood glucose level should be considered in the calculations
     *
     * @param flag  {@code true} if the blood glucose level should be considered in the calculation
     *              {@code false} otherwise
     */
    public void setUseBg(boolean flag) {
        useBg = flag;
    }

    /** Set a flag if the remaining carbs in the system should be considered in the calculations
     *
     * @param flag  {@code true} if the CoB value should be considered
     *              {@code false} otherwise
     */
    public void setUseCob(boolean flag) {
        useCob = flag;
    }

    /** Set a flag representing if the bolus insulin-on-board should be considered in the calculation
     *
     * @param flag {@code true} if the IoB value should be considered,
     *             {@code false} otherwise
     */
    public void setIncludeBolusIOB(boolean flag) {
        includeBolusIOB = flag;
    }

    /** Set a flag representing if the basal IoB value should be considered in the calculation
     *
     * @param flag {@code true} if the basal IoB value should be considered in the calculation, or
     *             {@code false} otherwise
     */
    public void setIncludeBasalIOB(boolean flag) {
        includeBasalIOB = flag;
    }

    /** Set a flag representing if the predicted blood glucose levels should be considered in the
     * calculation
     *
     * @param flag {@code true} if the calculation should include the blood glucose trend, or
     *             {@code false} if not.
     */
    public void setUseTrend(boolean flag) {
        useTrend = flag;
    }

    /** Build a new {@link BolusCalculator} from the supplied values
     *
     * @return A new {@link BolusCalculator} that has the provided values set
     */
    public BolusCalculator build() {
        if (profile == null) return null;
        BolusCalculator calc = new BolusCalculator(profile, carbs, cob, bg, correction);
        calc.setUseBg(useBg);
        calc.setUseCob(useCob);
        calc.setIncludeBolusIob(includeBolusIOB);
        calc.setIncludeBasalIOB(includeBasalIOB);
        calc.setUseTrend(useTrend);
        return calc;
    }
}
