package info.nightscout.androidaps.diabeatit.predictions;

import android.content.Context;
import android.content.res.AssetFileDescriptor;

import org.tensorflow.lite.Interpreter;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/** A {@link PredictionModel} that uses a tensor flow model to predict the values */
public class TensorflowPredictionModel implements PredictionModel {
    private final Interpreter interpreter;
    /** Create a new instance of a Tensorflow powered prediction model.
     *
     * @param model The tflite File used as model
     */
    public TensorflowPredictionModel(ByteBuffer model) throws FileNotFoundException {
        this(new Interpreter(model));
    }

	/** Create a new instance of a tensorflow powered prediction model from a byte buffer
	 *
	 * @param	model			A byte buffer of the {@code .tflite}
	 * @param	options			Options for the {@link Interpreter}
	 */
    public TensorflowPredictionModel(ByteBuffer model, Interpreter.Options options) {
        this(new Interpreter(model, options));
    }

	/** Create a new instance of a tensorflow powered prediction model
	 *
	 * @param	interpereter	The tensorflow interpreter to use to make predictions 
	 */
    public TensorflowPredictionModel(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    @Override
    public float[] predict(PredictionInputs inputs) {
        float[] inp = inputs.getInputs();
        float[] outp = new float[13];
        float[][] out = new float[1][];
        out[0] = outp;

        interpreter.run(inp, out);
        return outp;
    }

	/** Create an instance using a tensorflow light model in the assets.
	 *
	 * @param	ctx				The {@link Context} to use for retrieving the asset
	 * @param	modelFilename	Relative path to the model within settings
	 */
    public static TensorflowPredictionModel fromAsset(Context ctx, String modelFilename) throws IOException {
        AssetFileDescriptor fileDescriptor = ctx.getAssets().openFd(modelFilename);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        ByteBuffer buf =  fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        return new TensorflowPredictionModel(buf);
    }

	/** Create an instnace using a tensorflow light model in the file system
	 *
	 * @param	modelFilename	Absolute path to the model in the file system 
	 */
    public static TensorflowPredictionModel fromFile(Context ctx, String modelFilename) throws IOException {
        File f = new File(modelFilename);
        FileInputStream fis = new FileInputStream(f);
        ByteBuffer buf  = fis.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, f.length());
        return new TensorflowPredictionModel(buf);
    }
}
