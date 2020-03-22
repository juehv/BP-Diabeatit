package info.nightscout.androidaps.diabeatit.predictions;

/** 
 * An abstract model of predicting values based on a set of inputs
 */
public interface PredictionModel {
    /**
     * Run a prediction over the inputs.
     *
     * @param inputs The inputs to predict from.
     * @return An array of predicted values in 5min intervals. The first one being the current value
     */
    float[] predict(PredictionInputs inputs);
}
