package info.nightscout.androidaps.plugins.insulin;

import info.nightscout.androidaps.data.Profile;
import info.nightscout.androidaps.plugins.insulin.prediction.PredictionInputs;
import info.nightscout.androidaps.plugins.insulin.prediction.PredictionModel;

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
    private boolean useSuperBolus = false;
    private boolean useTrend = true;

    private PredictionModel predictionModel;
    private PredictionInputs inputs;

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public void setCarbs(int carbs) {
        this.carbs = carbs;
    }

    public void setCOB(double cob) {
        this.cob = cob;
    }

    public void setBG(double bg) {
        this.bg = bg;
    }

    public void setCorrection(double correction) {
        this.correction = correction;
    }

    public void setUseBg(boolean flag) {
        useBg = flag;
    }

    public void setUseCob(boolean flag) {
        useCob = flag;
    }

    public void setIncludeBolusIOB(boolean flag) {
        includeBolusIOB = flag;
    }

    public void setIncludeBasalIOB(boolean flag) {
        includeBasalIOB = flag;
    }

    public void setUseSuperBolus(boolean flag) {
        useSuperBolus = flag;
    }

    public void setUseTrend(boolean flag) {
        useTrend = flag;
    }

    public void setPredictionModel(PredictionModel model) {
        this.predictionModel = model;
    }

    public void setPredictionInputs(PredictionInputs inputs) { this.inputs = inputs; }

    public BolusCalculator build() {
        if (profile == null) return null;
        BolusCalculator calc = new BolusCalculator(profile, carbs, cob, bg, correction);
        calc.setUseBg(useBg);
        calc.setUseCob(useCob);
        calc.setIncludeBolusIob(includeBolusIOB);
        calc.setIncludeBasalIOB(includeBasalIOB);
        calc.setUseSuperBolus(useSuperBolus);
        calc.setUseTrend(useTrend);
        calc.setPredictionModel(predictionModel);
        calc.setPredictionInputs(inputs);
        return calc;
    }
}
