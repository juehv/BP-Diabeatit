package info.nightscout.androidaps.plugins.insulin.prediction;

import org.tensorflow.lite.Interpreter;

import java.io.File;

public class TensorflowPredictionModel implements PredictionModel {
    private final Interpreter interpreter;
    /**
     * Create a new instance of a Tensorflow powered prediction model.
     * @param model The tflite File used as model
     */
    public TensorflowPredictionModel(File model) {
        this(new Interpreter(model));
    }

    public TensorflowPredictionModel(File model, Interpreter.Options options) {
        this(new Interpreter(model, options));
    }

    public TensorflowPredictionModel(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    @Override
    public double[] predict(PredictionInputs inputs) {
        double[] inp = inputs.getInputs();
        double[] outp = new double[13];
        interpreter.run(inp, outp);
        return outp;
    }
}
