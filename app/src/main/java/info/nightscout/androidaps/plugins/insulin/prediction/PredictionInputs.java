package info.nightscout.androidaps.plugins.insulin.prediction;

public interface PredictionInputs {
    /**
     * Get the Inputs to be used in the prediction
     * @return An array of inputs in in 5min intervals. The last one is the current value.
     */
    double[] getInputs();
}
