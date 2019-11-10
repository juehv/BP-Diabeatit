package de.tu_darmstadt.informatik.tk.diabeatit.data.BGSources;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import de.tu_darmstadt.informatik.tk.diabeatit.data.BgDataManager;
import de.tu_darmstadt.informatik.tk.diabeatit.data.BgDataSource;
import de.tu_darmstadt.informatik.tk.diabeatit.data.BgReading;

/**
 * A dummy source that can be triggered by broadcasting an intent of ACTION_DUMMYSOURCE_TRIGGER
 */
public class DummySource implements BgDataSource {
    // List of intents
    /** Broadcasting an Intent with this implies a DummySource should trigger a reading */
    public static final String ACTION_DUMMYSOURCE_TRIGGER = DummySource.class.getCanonicalName() + ".TRIGGER";


    /**
     * The template for the readings created by this source. Note that the timestamp will be
     * overwritten by the current system time.
     */
    public BgReading dummyReading;

    public DummySource() {
        dummyReading = new BgReading();
        dummyReading.rawValue = 100;
        dummyReading.direction = "NOT_COMPUTABLE";
        dummyReading.source = BgReading.Source.EXTERNAL;
    }

    @Override
    public boolean advancedFilteringSupported() {
        return false;
    }

    @Override
    public List<BgReading> handleNewData(Context context, Intent intent) {
        ArrayList<BgReading> list = new ArrayList<>();

        BgReading reading = new BgReading();

        reading.timestamp = Instant.now().toEpochMilli();
        reading.rawValue = dummyReading.rawValue;
        reading.rawUnit = dummyReading.rawUnit;
        reading.source = dummyReading.source;

        list.add(reading);

        return list;
    }

    @Override
    public IntentFilter getIntentFilter() {
        // not sure which intents we should use here for a dummy source, really.
        // anything *could* go, but we need to be able to trigger it.
        IntentFilter filter = new IntentFilter();
        // For debugging
        filter.addAction(ACTION_DUMMYSOURCE_TRIGGER);
        return filter;
    }

    @Override
    public void onRegister(Context context, BgDataManager manager) {
        Log.d("BGDATA", "DummySource registering with manager");
    }

    @Override
    public void onUnregister(Context context, BgDataManager manager) {
        Log.d("BGDATA", "DummySource unregistering with manager");
    }
}
