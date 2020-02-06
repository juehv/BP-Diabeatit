package info.nightscout.androidaps.plugins.insulin.prediction;

import android.util.Pair;

public interface PredictionModel {
    /**
     * Run a prediction over the inputs.
     *
     * @param inputs The inputs to predict from.
     * @return An array of predicted values in 5min intervals. The first one being the current value
     */
    double[] predict(PredictionInputs inputs);
}
