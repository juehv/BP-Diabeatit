package info.nightscout.androidaps;

import android.content.Context;

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
        System.err.printf("%s: Predictions\n", KiPredictionsInstrumentedTest.class.getSimpleName());
        for (float prediction : predictions) {
            System.err.printf("%3f  ", prediction);
        }
        System.err.println();
    }
}
