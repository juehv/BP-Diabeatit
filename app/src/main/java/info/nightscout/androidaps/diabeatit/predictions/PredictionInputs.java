package info.nightscout.androidaps.diabeatit.predictions;

/**
 * An abstract representation of inputs that may be used within a
 * {@link PredictionModel} .
 */
public interface PredictionInputs {
    /**
     * Get the Inputs to be used in the prediction
	 *
     * @return		An array of inputs in in 5min intervals. The last one is the current value.
     */
    float[] getInputs();
}
