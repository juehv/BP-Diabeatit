package de.tu_darmstadt.informatik.tk.diabeatit.data.BGSources;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import de.tu_darmstadt.informatik.tk.diabeatit.data.DataManager;
import de.tu_darmstadt.informatik.tk.diabeatit.data.DataSource;
import de.tu_darmstadt.informatik.tk.diabeatit.data.BgReading;

public class DexcomBgSource implements DataSource<BgReading> {
    // Intents
    public final static String ACTION_NEW_READING = "com.dexcom.cgm.EXTERNAL_BROADCAST";
    public final static String BUNDLE_GLUCOSE_VALUES = "glucoseValues";
    public final static String EXTRA_GLUCOSE_VALUE = "glucoseValue";
    public final static String EXTRA_TREND_ARROW = "trendArrow";
    public final static String EXTRA_TIMESTAMP = "timestamp";

    @Override
    public boolean advancedFilteringSupported() {
        return false;
    }

    @Override
    public List<BgReading> handleNewData(Context context, Intent intent) {
        Bundle b = intent.getBundleExtra(BUNDLE_GLUCOSE_VALUES);
        if (b == null)
            return null;
        ArrayList<BgReading> readings = new ArrayList<>();

        for (int i = 0; i < b.size(); i++) {
            Bundle v = b.getBundle(Integer.toString(i));
            if (v == null)
                continue;
            BgReading reading = new BgReading();
            reading.source = BgReading.Source.SENSOR;
            reading.rawValue = v.getDouble(EXTRA_GLUCOSE_VALUE);
            reading.rawUnit = BgReading.Unit.MGDL;
            reading.timestamp = v.getLong(EXTRA_TIMESTAMP) * 1000;
            readings.add(reading);
        }

        return readings;
    }

    @Override
    public IntentFilter getIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_NEW_READING);
        return filter;
    }

    @Override
    public void onRegister(Context context, DataManager manager) {
        // TODO: We might need to ask for permissions here..
        //       See original source.
    }

    @Override
    public void onUnregister(Context context, DataManager manager) {

    }
}
