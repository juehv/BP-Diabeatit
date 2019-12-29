package info.nightscout.androidaps.plugins.insulin;

import info.nightscout.androidaps.data.Profile;

/**
 * Abstraction over the various models used to predict the BG value 15 minutes into the future
 */
public interface BGPredictionModel {
    /** Returns the delta to the current BG value 15 minutes into the future in mgdl */
    double get15minDelta(Profile profile);
}
