package info.nightscout.androidaps.plugins.insulin.prediction;

/**
 * Simple readings input, just gives back the data you put in. Mainly meant for debugging
 */
public class SimpleBgReadingsInput implements PredictionInputs {
    private final static int REQUIRED_INPUTS = 30;

    private double[] inputs;

    /**
     * Create a new instance with the given inputs
     * @param inputs The inputs
     */
    public SimpleBgReadingsInput(double[] inputs) {
        if (inputs.length != REQUIRED_INPUTS)
            throw new UnsupportedOperationException(
                    String.format("Invalid amount of inputs, got %d, expected %d",
                            inputs.length,
                            REQUIRED_INPUTS));

        this.inputs = inputs;
    }

    @Override
    public double[] getInputs() {
        return inputs;
    }
}
