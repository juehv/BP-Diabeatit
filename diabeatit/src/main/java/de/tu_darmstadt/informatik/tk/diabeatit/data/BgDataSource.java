package de.tu_darmstadt.informatik.tk.diabeatit.data;

import android.content.Intent;
import android.content.IntentFilter;

import java.util.List;

/**
 * A source of BG related data
 *
 * At this point a copy of the one found in the `app` module.
 * TODO: Refactor when the constraints and design are more clear
 */
public interface BgDataSource {
    boolean advancedFilteringSupported();

    /** Handle new data being broadcast */
    List<BgReading> handleNewData(Intent intent);
    /** Get intent filter for this */
    IntentFilter getIntentFilter();
    /** Registered with a manager */
    public void onRegister(BgDataManager manager);
    /** Unregistered with a manager */
    public void onUnregister(BgDataManager manager);
}
