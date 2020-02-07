package info.nightscout.androidaps;

import android.content.Context;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import info.nightscout.androidaps.diabeatit.predictions.SimpleBgReadingsInput;
import info.nightscout.androidaps.diabeatit.predictions.TensorflowPredictionModel;
@RunWith(AndroidJUnit4.class)
public class KiPredictionsInstrumentedTest {
    @Test
    public void openPredictions() throws Exception {
        Context ctx = InstrumentationRegistry.getInstrumentation().getTargetContext();
        TensorflowPredictionModel model = TensorflowPredictionModel.fromAsset(ctx, "ki/model.tflite");
        float[] inputValues = new float[30];
        for (int i = 0; i < inputValues.length; i++) {
            inputValues[i] = 120;
        }
        SimpleBgReadingsInput inputs = new SimpleBgReadingsInput(inputValues);

        float[] predictions = model.predict(inputs);
        Log.w("TEST", String.format("%s: Predictions\n", KiPredictionsInstrumentedTest.class.getSimpleName()));
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < predictions.length; i++) {
            if (i % 3 == 0) {
                Log.w("TEST", buf.toString());
                buf = new StringBuilder();
            }
            float prediction = predictions[i];
            buf.append(String.format("%3f\t", prediction));
        }
        System.err.println();
    }
}
