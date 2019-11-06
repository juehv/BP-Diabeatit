package de.tu_darmstadt.informatik.tk.diabeatit.data.BGSources;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

import de.tu_darmstadt.informatik.tk.diabeatit.data.BgDataManager;
import de.tu_darmstadt.informatik.tk.diabeatit.data.BgDataSource;
import de.tu_darmstadt.informatik.tk.diabeatit.data.BgReading;

public class GimpBGSource implements BgDataSource {
    // Intents
    public final static String ACTION_NEW_MEASUREMENT = "it.ct.glicemia.ACTION_GLUCOSE_MEASURED";
    public final static String EXTRA_VALUE = "mySGV";
    public final static String EXTRA_TIMESTAMP = "myTimestamp";
    public final static String EXTRA_DIRECTION = "myTrend";

    @Override
    public boolean advancedFilteringSupported() {
        return false;
    }

    @Override
    public List<BgReading> handleNewData(Intent intent) {
        ArrayList<BgReading> results = new ArrayList<>();

        Bundle b = intent.getExtras();
        if (b == null) return null;

        BgReading reading = new BgReading();
        reading.source = BgReading.Source.SENSOR;
        reading.rawValue = b.getDouble(EXTRA_VALUE);
        reading.timestamp = b.getLong(EXTRA_TIMESTAMP);
        reading.direction = b.getString(EXTRA_DIRECTION);

        results.add(reading);

        return results;
    }

    @Override
    public IntentFilter getIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_NEW_MEASUREMENT);
        return filter;
    }

    @Override
    public void register(BgDataManager manager) {

    }

    @Override
    public void unregister(BgDataManager manager) {

    }
}
