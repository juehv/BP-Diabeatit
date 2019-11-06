package de.tu_darmstadt.informatik.tk.diabeatit.data.BGSources;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import de.tu_darmstadt.informatik.tk.diabeatit.data.BgDataManager;
import de.tu_darmstadt.informatik.tk.diabeatit.data.BgDataSource;
import de.tu_darmstadt.informatik.tk.diabeatit.data.BgReading;

/**
 * A source that interacts with the XDrip app
 */
public class XDripBGSource implements BgDataSource {
    // used intents
    public final static String ACTION_NEW_BG_ESTIMATE = "com.eveningoutpost.dexdrip.BgEstimate";
    public final static String EXTRA_BG_ESIMATE = "com.eveningoutpost.dexdrip.Extras.BgEstimate";
    public final static String EXTRA_BG_SLOPE_NAME = "com.eveningoutpost.dexdrip.Extras.BgSlopeName";
    public final static String EXTRA_TIMESTAMP = "com.eveningoutpost.dexdrip.Extras.Time";
    public final static String EXTRA_RAW = "com.eveningoutpost.dexdrip.Extras.Raw";
    public final static String XDRIP_DATA_SOURCE_DESCRIPTION = "com.eveningoutpost.dexdrip.Extras.SourceDesc";

    @Override
    public boolean advancedFilteringSupported() {
        // in the original code this is true if the source is either of
        // - "G5 Native"
        // - "G6 Native"
        return false;
    }

    @Override
    public List<BgReading> handleNewData(Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle == null) return null;

        BgReading reading = new BgReading();

        reading.rawValue = bundle.getDouble(EXTRA_BG_ESIMATE);
        reading.direction = bundle.getString(EXTRA_BG_SLOPE_NAME);
        reading.timestamp = bundle.getLong(EXTRA_TIMESTAMP);
        // not sure what the RAW value is, and not sure if we'd need the SOURCE either.

        ArrayList<BgReading> results = new ArrayList<BgReading>();
        results.add(reading);
        return results;
    }

    @Override
    public IntentFilter getIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_NEW_BG_ESTIMATE);
        return filter;
    }

    @Override
    public void onRegister(BgDataManager manager) {
    }

    @Override
    public void onUnregister(BgDataManager manager) {
    }
}
