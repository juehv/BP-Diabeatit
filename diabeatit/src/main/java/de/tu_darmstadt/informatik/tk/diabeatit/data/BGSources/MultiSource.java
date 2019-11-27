package de.tu_darmstadt.informatik.tk.diabeatit.data.BGSources;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.tu_darmstadt.informatik.tk.diabeatit.data.DataManager;
import de.tu_darmstadt.informatik.tk.diabeatit.data.DataSource;
import de.tu_darmstadt.informatik.tk.diabeatit.data.BgReading;


/** A source that combines multiple sources  */
public class MultiSource<T> implements DataSource<T> {
    private boolean currentlyRegistered;
    private ArrayList<DataSource<T>> sources;

    public MultiSource() {
        sources = new ArrayList<>();
        currentlyRegistered = false;
    }

    /**
     * Add a new source.
     *
     * @param src A DataSource to register
     * @throws UnsupportedOperationException if this source is currently registered with a DataManager
     */
    public MultiSource<T> addSource(DataSource<T> src) {
        if (currentlyRegistered) {
            throw new UnsupportedOperationException("Cannot modify sources while actively registered");
        }

        if (!sources.contains(src)) {
            sources.add(src);
        }

        return this;
    }

    /**
     * Remove a previously registered source.
     *
     * @param src A DataSource to remove
     * @throws UnsupportedOperationException if this source is currently registered with a DataManager
     */
    public void removeSource(DataSource<T> src) {
        if (currentlyRegistered) {
            throw new UnsupportedOperationException("Cannot modify sources while actively registered");
        }

        sources.remove(src);
    }

    /**
     * Get an Iterator over all currently registered sources
     *
     * @return An Iterator over all BgDataSources currently registered
     */
    public Iterator<DataSource<T>> getSources() {
        return sources.iterator();
    }

    @Override
    public boolean advancedFilteringSupported() {
        return false;
    }

    @Override
    public List<T> handleNewData(Context context, Intent intent) {
        // XXX: This only checks actions so far -- this might not suffice later on
        ArrayList<T> readings = new ArrayList<>();
        for (DataSource<T> src : sources) {
            IntentFilter filter = src.getIntentFilter();
            if (filter.matchAction(intent.getAction())) {
                List<T> sourceReadings = src.handleNewData(context, intent);
                readings.addAll(sourceReadings);
            }
        }
        return readings;
    }

    @Override
    public IntentFilter getIntentFilter() {
        IntentFilter filter = new IntentFilter();

        for (DataSource src : sources) {
            filter = combineIntentFilters(filter, src.getIntentFilter());
        }

        return filter;
    }

    /** Combines two IntentFilters into a new filter */
    private static IntentFilter combineIntentFilters(IntentFilter a, IntentFilter b) {
        IntentFilter combined = new IntentFilter(a);

        // add actions
        Iterator<String> it = b.actionsIterator();
        while (it.hasNext()) {
            String action = it.next();
            if (!combined.hasAction(action))
                combined.addAction(action);
        }

        return combined;
    }

    @Override
    public void onRegister(Context context, DataManager<T> manager) {
        currentlyRegistered = true;
        for (DataSource<T> src : sources) {
            src.onRegister(context, manager);
        }
    }

    @Override
    public void onUnregister(Context context, DataManager<T> manager) {
        for (DataSource<T> src : sources) {
            src.onUnregister(context, manager);
        }
        currentlyRegistered = false;
    }
}
