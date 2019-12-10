package de.tu_darmstadt.informatik.tk.diabeatit.data.BolusSources;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import de.tu_darmstadt.informatik.tk.diabeatit.data.Bolus;
import de.tu_darmstadt.informatik.tk.diabeatit.data.DataManager;
import de.tu_darmstadt.informatik.tk.diabeatit.data.DataSource;

public class ManualBolusSource implements DataSource<Bolus> {
    // Intent strings
    /** Canonical name of this class */
    public final static String CLASS_NAME = ManualBolusSource.class.getCanonicalName();
    /** An Intent with this Action represents a new manual Bolus entry */
    public final static String ACTION_NEW_MANUAL_BOLUS = CLASS_NAME + ".NEW_READING";
    /**
     * The Extra with this Key has to be a `long`, representing the timestamp in milliseconds since
     * the UNIX Epoch
     */
    public final static String EXTRA_TIMESTAMP = CLASS_NAME + ".TIMESTAMP";
    /**
     * The Extra with this Key has to be a `double` representing the amount of Insulin
     */
    public final static String EXTRA_UNITS = CLASS_NAME + ".UNITS";
    /**
     * The Extra with this Key has to be a `String` representing any additional Notes to the Entry
     */
    public final static String EXTRA_NOTES = CLASS_NAME + ".NOTES";

    @Override
    public boolean advancedFilteringSupported() {
        return false;
    }

    @Override
    public List<Bolus> handleNewData(Context context, Intent intent) {
        ArrayList<Bolus> readings = new ArrayList<>();

        Bundle b = intent.getExtras();
        if (b == null)
            return null;

        Bolus reading = new Bolus();
        reading.timestamp = b.getLong(EXTRA_TIMESTAMP);
        reading.units = b.getDouble(EXTRA_UNITS);
        reading.notes = b.getString(EXTRA_NOTES);

        readings.add(reading);

        return readings;
    }

    @Override
    public IntentFilter getIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_NEW_MANUAL_BOLUS);
        return filter;
    }

    @Override
    public void onRegister(Context context, DataManager<Bolus> manager) {

    }

    @Override
    public void onUnregister(Context context, DataManager<Bolus> manager) {

    }

    public static Intent createIntent(long timestamp, double amount, String notes) {
        Log.d("DATA", String.format("Got Bolus from %f", amount));
        return new Intent(ACTION_NEW_MANUAL_BOLUS)
            .putExtra(EXTRA_TIMESTAMP, timestamp)
            .putExtra(EXTRA_UNITS, amount)
            .putExtra(EXTRA_NOTES, notes);
    }
}
