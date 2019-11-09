package de.tu_darmstadt.informatik.tk.diabeatit.data.BGSources;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import de.tu_darmstadt.informatik.tk.diabeatit.data.BgDataManager;
import de.tu_darmstadt.informatik.tk.diabeatit.data.BgDataSource;
import de.tu_darmstadt.informatik.tk.diabeatit.data.BgReading;

public class EversenseBgSource implements BgDataSource {
    // Intents
    public final static String ACTION_NEW_READING = "com.senseonics.AndroidAPSEventSubscriber.BROADCAST";
    public final static String EXTRA_GLUCOSE_LEVELS = "glucoseLevels";
    public final static String EXTRA_GLUCOSE_RECORD_NUMBERS = "glucoseRecordNumbers";
    public final static String EXTRA_GLUCOSE_TIMESTAMPS = "glucoseTimestamps";

    @Override
    public boolean advancedFilteringSupported() {
        return false;
    }

    @Override
    public List<BgReading> handleNewData(Intent intent) {
        ArrayList<BgReading> readings = new ArrayList<>();

        Bundle b = intent.getExtras();
        if (b == null) return readings;

        // NOTE: This type of source can also generate "calibration" events, but we dont handle these
        //       yet.

        if (b.containsKey(EXTRA_GLUCOSE_LEVELS)) {
            int[] levels = b.getIntArray(EXTRA_GLUCOSE_LEVELS);
            int[] recordNumbers = b.getIntArray(EXTRA_GLUCOSE_RECORD_NUMBERS);
            long[] timestamps = b.getLongArray(EXTRA_GLUCOSE_TIMESTAMPS);

            for (int i = 0; i < levels.length; i++) {
                BgReading reading = new BgReading();
                reading.rawValue = levels[i];
                reading.timestamp = timestamps[i];
                reading.source = BgReading.Source.SENSOR;
                reading.rawUnit = BgReading.Unit.MGDL;

                readings.add(reading);
            }
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
    public void onRegister(BgDataManager manager) {

    }

    @Override
    public void onUnregister(BgDataManager manager) {

    }
}
