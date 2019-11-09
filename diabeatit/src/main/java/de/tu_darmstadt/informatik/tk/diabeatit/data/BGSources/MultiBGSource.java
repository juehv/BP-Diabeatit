package de.tu_darmstadt.informatik.tk.diabeatit.data.BGSources;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.tu_darmstadt.informatik.tk.diabeatit.data.BgDataManager;
import de.tu_darmstadt.informatik.tk.diabeatit.data.BgDataSource;
import de.tu_darmstadt.informatik.tk.diabeatit.data.BgReading;


/** A source that combines multiple sources  */
public class MultiBGSource implements BgDataSource {
    private boolean currentlyRegistered;
    private ArrayList<BgDataSource> sources;

    public MultiBGSource() {
        sources = new ArrayList<>();
        currentlyRegistered = false;
    }

    /**
     * Add a new source.
     *
     * @param src A BgDataSource to register
     * @throws UnsupportedOperationException if this source is currently registered with a BgDataManager
     */
    public void addSource(BgDataSource src) {
        if (currentlyRegistered) {
            throw new UnsupportedOperationException("Cannot modify sources while actively registered");
        }

        if (!sources.contains(src)) {
            sources.add(src);
        }
    }

    /**
     * Remove a previously registered source.
     *
     * @param src A BgDataSource to remove
     * @throws UnsupportedOperationException if this source is currently registered with a BgDataManager
     */
    public void removeSource(BgDataSource src) {
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
    public Iterator<BgDataSource> getSources() {
        return sources.iterator();
    }

    @Override
    public boolean advancedFilteringSupported() {
        return false;
    }

    @Override
    public List<BgReading> handleNewData(Intent intent) {
        // XXX: This only checks actions so far -- this might not suffice later on
        ArrayList<BgReading> readings = new ArrayList<>();
        for (BgDataSource src : sources) {
            IntentFilter filter = src.getIntentFilter();
            if (filter.matchAction(intent.getAction())) {
                List<BgReading> sourceReadings = src.handleNewData(intent);
                readings.addAll(sourceReadings);
            }
        }
        return readings;
    }

    @Override
    public IntentFilter getIntentFilter() {
        IntentFilter filter = new IntentFilter();

        for (BgDataSource src : sources) {
            filter = combineIntentFilters(filter, src.getIntentFilter());
        }

        return filter;
    }

    /** Combines two IntentFilters into a new filter */
    private static IntentFilter combineIntentFilters(IntentFilter a, IntentFilter b) {
        IntentFilter combined = new IntentFilter(a);

        String temp = "";

        // add actions
        for (Iterator<String> it = b.actionsIterator(); it.hasNext(); temp = it.next()) {
            if (!combined.hasAction(temp))
                combined.addAction(temp);
        }
        // add categories
        for (Iterator<String> it = b.categoriesIterator(); it.hasNext(); temp = it.next()) {
            if (!combined.hasCategory(temp)) {
                combined.addCategory(temp);
            }
        }
        // add schemes
        for (Iterator<String> it = b.schemesIterator(); it.hasNext(); temp = it.next()) {
            if (!combined.hasDataScheme(temp)) {
                combined.addDataScheme(temp);
            }
        }

        // TODO: add  missing stuff:
        //  - authorities
        //  - paths

        return combined;
    }

    @Override
    public void onRegister(BgDataManager manager) {
        currentlyRegistered = true;
        for (BgDataSource src : sources) {
            src.onRegister(manager);
        }
    }

    @Override
    public void onUnregister(BgDataManager manager) {
        for (BgDataSource src : sources) {
            src.onUnregister(manager);
        }
        currentlyRegistered = false;
    }
}
