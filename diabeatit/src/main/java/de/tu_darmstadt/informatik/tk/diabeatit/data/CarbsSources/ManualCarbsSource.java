package de.tu_darmstadt.informatik.tk.diabeatit.data.CarbsSources;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import de.tu_darmstadt.informatik.tk.diabeatit.data.CarbsEntry;
import de.tu_darmstadt.informatik.tk.diabeatit.data.DataManager;
import de.tu_darmstadt.informatik.tk.diabeatit.data.DataSource;

public class ManualCarbsSource implements DataSource<CarbsEntry> {
    // For intents
    private final static String CLASS_NAME = ManualCarbsSource.class.getCanonicalName();

    public final static String ACTION_NEW_ENTRY = CLASS_NAME + ".NEW_ENTRY";
    public final static String EXTRA_TIMESTAMP = CLASS_NAME + ".TIMESTAMP";
    public final static String EXTRA_CARBS = CLASS_NAME + ".CARBS";

    @Override
    public boolean advancedFilteringSupported() {
        return false;
    }

    @Override
    public List<CarbsEntry> handleNewData(Context context, Intent intent) {
        List<CarbsEntry> entries = new ArrayList<>();

        Bundle b = intent.getExtras();
        if (b == null)
            return null;

        long timestamp = b.getLong(EXTRA_TIMESTAMP);
        double carbs = b.getDouble(EXTRA_CARBS);

        CarbsEntry entry = new CarbsEntry();
        entry.timestamp = timestamp;
        entry.carbs = carbs;

        entries.add(entry);

        return entries;
    }

    @Override
    public IntentFilter getIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_NEW_ENTRY);
        return filter;
    }

    @Override
    public void onRegister(Context context, DataManager<CarbsEntry> manager) {

    }

    @Override
    public void onUnregister(Context context, DataManager<CarbsEntry> manager) {

    }

    public static Intent createIntent(long ts, double carbs) {
        Intent i = new Intent(ACTION_NEW_ENTRY);
        i.putExtra(EXTRA_TIMESTAMP, ts);
        i.putExtra(EXTRA_CARBS, carbs);
        return i;
    }
}
