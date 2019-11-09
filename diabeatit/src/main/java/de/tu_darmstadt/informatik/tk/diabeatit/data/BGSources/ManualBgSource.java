package de.tu_darmstadt.informatik.tk.diabeatit.data.BGSources;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.AndroidRuntimeException;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import de.tu_darmstadt.informatik.tk.diabeatit.data.BgDataManager;
import de.tu_darmstadt.informatik.tk.diabeatit.data.BgDataSource;
import de.tu_darmstadt.informatik.tk.diabeatit.data.BgReading;

/**
 * A BgDataSource that responds to manual entries
 */
public class ManualBgSource implements BgDataSource {
    // intents
    /** Represents a new manual reading */
    public final static String ACTION_NEW_MANUAL_BG_READING = ManualBgSource.class.getCanonicalName() + ".NEW_READING";
    /** Extra data representing the reading. Must be double */
    public final static String EXTRA_BG_READING = ManualBgSource.class.getCanonicalName() + ".BG_READING";
    /**
     * Extra data representing the unit. Must be a String with the value being either of "mg/dl" or
     * "Mmol/L".
     */
    public final static String EXTRA_UNIT = ManualBgSource.class.getCanonicalName() + ".UNIT";
    /**
     * Extra data representing the timestamp the reading was made. Must be a long value of the ms
     * since the unix epoch.
     */
    public final static String EXTRA_TIMESTAMP = ManualBgSource.class.getCanonicalName() + ".TIMESTAMP";


    /**
     * Create a proper Intent to be consumed by a ManualBgSource
     * @param reading The raw reading, is assumed to be in mg/dl
     * @param timestamp The timestamp of the reading in ms since the unix epoch
     * @return A new Intent that will be handled by a ManualBgSource
     */
    public static Intent createIntent(double reading, long timestamp) {
        return createIntent(reading, timestamp, BgReading.Unit.MGDL);
    }

    /**
     * Create a proper Intent to be consumed by a ManualBgSource
     * @param reading The read value
     * @param timestamp The timestamp of the reading, in ms since the unix epoch
     * @param unit The Unit of the reading
     * @return A new Intent that will be handled by a ManualBgSource
     */
    public static Intent createIntent(double reading, long timestamp, BgReading.Unit unit) {
        Intent i = new Intent(ACTION_NEW_MANUAL_BG_READING);
        i.putExtra(EXTRA_BG_READING, reading);
        i.putExtra(EXTRA_UNIT, unit.toString());
        i.putExtra(EXTRA_TIMESTAMP, timestamp);
        return i;
    }

    @Override
    public boolean advancedFilteringSupported() {
        return false;
    }

    @Override
    public List<BgReading> handleNewData(Intent intent) {
        Bundle b = intent.getExtras();
        if (b == null) {
            Log.w("BGDATA", "ManualBgSource: Received Intent with proper action but no extras");
            return null;
        }

        ArrayList<BgReading> readings = new ArrayList<>();
        BgReading reading = new BgReading();

        double readingValue = b.getDouble(EXTRA_BG_READING);
        String unit = b.getString(EXTRA_UNIT);
        long timestamp = b.getLong(EXTRA_TIMESTAMP);

        reading.rawValue = readingValue;
        if (unit.toLowerCase().equals("mg/dl")) {
            reading.rawUnit = BgReading.Unit.MGDL;
        } else if (unit.toLowerCase().equals("mmol/l")) {
            reading.rawUnit = BgReading.Unit.MMOLL;
        } else {
            Log.w("BGDATA", "ManualBgSource: Got garbage unit " + unit + " assuming mg/dl.");
            reading.rawUnit = BgReading.Unit.MGDL;
        }
        reading.timestamp = timestamp;
        reading.source = BgReading.Source.MANUAL;

        readings.add(reading);
        return readings;
    }

    @Override
    public IntentFilter getIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_NEW_MANUAL_BG_READING);
        return filter;
    }

    @Override
    public void onRegister(BgDataManager manager) {
    }

    @Override
    public void onUnregister(BgDataManager manager) {
    }
}
