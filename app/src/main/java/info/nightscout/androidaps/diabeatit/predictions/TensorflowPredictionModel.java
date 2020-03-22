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

public class TensorflowPredictionModel implements PredictionModel {
    private final Interpreter interpreter;
    /**
     * Create a new instance of a Tensorflow powered prediction model.
     * @param model The tflite File used as model
     */
    public TensorflowPredictionModel(ByteBuffer model) throws FileNotFoundException {
        this(new Interpreter(model));
    }

    public TensorflowPredictionModel(ByteBuffer model, Interpreter.Options options) {
        this(new Interpreter(model, options));
    }

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

    public static TensorflowPredictionModel fromAsset(Context ctx, String modelFilename) throws IOException {
        AssetFileDescriptor fileDescriptor = ctx.getAssets().openFd(modelFilename);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        ByteBuffer buf =  fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        return new TensorflowPredictionModel(buf);
    }

    public static TensorflowPredictionModel fromFile(Context ctx, String modelFilename) throws IOException {
        File f = new File(modelFilename);
        FileInputStream fis = new FileInputStream(f);
        ByteBuffer buf  = fis.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, f.length());
        return new TensorflowPredictionModel(buf);
    }
}
