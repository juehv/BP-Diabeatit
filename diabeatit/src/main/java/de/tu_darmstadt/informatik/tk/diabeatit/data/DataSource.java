package de.tu_darmstadt.informatik.tk.diabeatit.data;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.List;

/**
 * A source of BG related data
 *
 * At this point a copy of the one found in the `app` module.
 * TODO: Refactor when the constraints and design are more clear
 */
public interface DataSource<T> {
    boolean advancedFilteringSupported();

    /** Handle new data being broadcast */
    List<T> handleNewData(Context context, Intent intent);
    /** Get intent filter for this */
    IntentFilter getIntentFilter();
    /** Registered with a manager */
    public void onRegister(Context context, DataManager<T> manager);
    /** Unregistered with a manager */
    public void onUnregister(Context context, DataManager<T> manager);
}
