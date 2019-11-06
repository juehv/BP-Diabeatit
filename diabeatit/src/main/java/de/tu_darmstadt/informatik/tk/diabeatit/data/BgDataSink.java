package de.tu_darmstadt.informatik.tk.diabeatit.data;

/**
 * A point to report new readings
 */
public interface BgDataSink {
    /** Report a new reading to the application */
    public void reportNewReading(BgReading reading);
    /** Register with a manager */
    public void register(BgDataManager manager);
    /** Unregister with a manager */
    public void unregister(BgDataManager manager);
}
