package de.tu_darmstadt.informatik.tk.diabeatit.data.BGSources;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import de.tu_darmstadt.informatik.tk.diabeatit.data.BgDataManager;
import de.tu_darmstadt.informatik.tk.diabeatit.data.BgDataSource;
import de.tu_darmstadt.informatik.tk.diabeatit.data.BgReading;

/**
 * Provides an interface to interact with the broadcasts sent by a
 * Tomato (MiaoMiao) source.
 */
public class TomatoBGSource implements BgDataSource {
    // Intents
    public final static String ACTION_NEW_ESTIMATE = "com.fanqies.tomatofn.BgEstimate";
    public final static String EXTRA_BG_ESTIMATE = "com.fanqies.tomatofn.Extras.BgEstimate";
    public final static String EXTRA_TIME = "com.fanqies.tomatofn.Extras.Time";

    @Override
    public boolean advancedFilteringSupported() {
        return false;
    }

    @Override
    public List<BgReading> handleNewData(Context context, Intent intent) {
        ArrayList<BgReading> results = new ArrayList<>();

        Bundle b = intent.getExtras();
        if (b == null) return null;

        BgReading reading = new BgReading();

        reading.rawValue = b.getDouble(EXTRA_BG_ESTIMATE);
        reading.timestamp = b.getLong(EXTRA_TIME);
        reading.source = BgReading.Source.SENSOR;

        results.add(reading);

        return results;
    }

    @Override
    public IntentFilter getIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_NEW_ESTIMATE);
        return filter;
    }

    @Override
    public void onRegister(Context context, BgDataManager manager) {
    }

    @Override
    public void onUnregister(Context context, BgDataManager manager) {
    }
}
