package de.tu_darmstadt.informatik.tk.diabeatit.data;

/**
 * A point to report new readings
 */
public interface DataSink<T> {
    /** Report a new reading to the application */
    public void onNewReading(T reading);
    /** Register with a manager */
    public void onRegister(DataManager<T> manager);
    /** Unregister with a manager */
    public void onUnregister(DataManager<T> manager);
}
