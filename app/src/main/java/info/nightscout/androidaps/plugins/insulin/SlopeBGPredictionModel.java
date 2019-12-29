package info.nightscout.androidaps.plugins.insulin;

import info.nightscout.androidaps.data.Profile;
import info.nightscout.androidaps.plugins.iob.iobCobCalculator.GlucoseStatus;

/**
 * Prediction-Model that uses the short average delta to interpolate. This is the default used in
 * the original AndroidAPS code.
 */
public class SlopeBGPredictionModel implements BGPredictionModel {
    @Override
    public double get15minDelta(Profile profile) {
        GlucoseStatus status = GlucoseStatus.getGlucoseStatusData();
        return status.short_avgdelta * 3;
    }
}
