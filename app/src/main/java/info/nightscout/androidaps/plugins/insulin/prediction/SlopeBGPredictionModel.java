package info.nightscout.androidaps.plugins.insulin.prediction;

import android.content.Context;
import android.provider.ContactsContract;

import info.nightscout.androidaps.data.Profile;
import info.nightscout.androidaps.db.BgReading;
import info.nightscout.androidaps.db.DatabaseHelper;
import info.nightscout.androidaps.plugins.iob.iobCobCalculator.GlucoseStatus;

/**
 * Prediction-Model that uses the short average delta to interpolate. This is the default used in
 * the original AndroidAPS code.
 */
public class SlopeBGPredictionModel implements PredictionModel {
    private final static int PREDICTION_SIZE = 3;

    public SlopeBGPredictionModel() {
    }


    @Override
    public double[] predict(PredictionInputs inputs) {
        // The method used here is a short average delta
        // short_deltas are calculated from everything ~5-15 minutes ago
        double[] in = inputs.getInputs();
        double delta = 0;
        // get the average delta of the points 15 to 5 min ago
        for (int i = in.length - 4; i < in.length - 1; i++) {
            delta += in[i - 1] - in[i];
        }
        delta /= 3;

        double[] outp = new double[PREDICTION_SIZE];
        for (int i = 0; i < outp.length; i++) {
            outp[i] = (i + 1) * delta;
        }

        return outp;
    }
}
