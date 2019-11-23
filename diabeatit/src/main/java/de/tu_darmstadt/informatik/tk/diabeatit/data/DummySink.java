package de.tu_darmstadt.informatik.tk.diabeatit.data;

import android.util.Log;

import de.tu_darmstadt.informatik.tk.diabeatit.data.DataManager;
import de.tu_darmstadt.informatik.tk.diabeatit.data.DataSink;
import de.tu_darmstadt.informatik.tk.diabeatit.data.BgReading;

/**
 * A dummy sink for testing purposes that only remembers the amount of as well as the last reading
 * it received.
 */
public class DummySink<T> implements DataSink<T> {
    /** The last reading received by this sink */
    public T lastReading = null;
    private int totalReadingsReceived = 0;

    /** Gets the total number of readings received by this sink so far */
    public int getTotalReadingsReceived() {
        return totalReadingsReceived;
    }

    @Override
    public void onNewReading(T reading) {
        Log.i("DATA", String.format("Received new reading: \t%s", reading));
        lastReading = reading;
        totalReadingsReceived ++;
    }

    @Override
    public void onRegister(DataManager manager) {
        Log.d("DATA", "Registering dummy sink");
    }

    @Override
    public void onUnregister(DataManager manager) {
        Log.d("DATA", "Unregistering dummy sink");
    }
}
