package de.tu_darmstadt.informatik.tk.diabeatit.data.SportsSources;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import de.tu_darmstadt.informatik.tk.diabeatit.data.DataManager;
import de.tu_darmstadt.informatik.tk.diabeatit.data.DataSource;
import de.tu_darmstadt.informatik.tk.diabeatit.data.SportsEntry;

public class ManualSportsSource
        implements DataSource<SportsEntry> {

    private final static String CLASSNAME = ManualSportsSource.class.getCanonicalName();
    // Intents
    public final static String ACTION_NEW_MANUAL_ENTRY = CLASSNAME + ".NEW_ENTRY";
    public final static String EXTRA_TIMESTAMP_FROM = CLASSNAME + ".TIMESTAMP_FROM";
    public final static String EXTRA_TIMESTAMP_UNTIL = CLASSNAME + ".TIMESTAMP_UNTIL";
    public final static String EXTRA_LEVEL = CLASSNAME + ".LEVEL";
    public final static String EXTRA_NOTES = CLASSNAME + ".NOTES";


    @Override
    public boolean advancedFilteringSupported() {
        return false;
    }

    @Override
    public List<SportsEntry> handleNewData(Context context, Intent intent) {
        List<SportsEntry> entries = new ArrayList<>();
        Bundle b = intent.getExtras();
        if (b == null) return null;

        long tsFrom = b.getLong(EXTRA_TIMESTAMP_FROM);
        long tsUntil = b.getLong(EXTRA_TIMESTAMP_UNTIL);
        int level = b.getInt(EXTRA_LEVEL);
        String notes = b.getString(EXTRA_NOTES);

        SportsEntry entry = new SportsEntry();
        entry.timestampFrom = tsFrom;
        entry.timestampUntil = tsUntil;
        entry.level = SportsEntry.Level.fromInt(level);
        entry.notes = notes;

        Log.d("DATA", String.format("Got sports of level %d, from %d to %d", level, tsFrom, tsUntil));

        entries.add(entry);

        return entries;
    }

    @Override
    public IntentFilter getIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_NEW_MANUAL_ENTRY);
        return filter;
    }

    @Override
    public void onRegister(Context context, DataManager<SportsEntry> manager) {
        Log.d(CLASSNAME, "Registered");
    }

    @Override
    public void onUnregister(Context context, DataManager<SportsEntry> manager) {
        Log.d(CLASSNAME, "UNREGISTERED");
    }

    public static Intent createProperIntent(long tsFrom, long tsUntil, int level, String notes) {
        Intent i = new Intent(ACTION_NEW_MANUAL_ENTRY);
        i.putExtra(EXTRA_TIMESTAMP_FROM, tsFrom);
        i.putExtra(EXTRA_TIMESTAMP_UNTIL, tsUntil);
        i.putExtra(EXTRA_LEVEL, level);
        i.putExtra(EXTRA_NOTES, notes);

        return i;
    }
}
