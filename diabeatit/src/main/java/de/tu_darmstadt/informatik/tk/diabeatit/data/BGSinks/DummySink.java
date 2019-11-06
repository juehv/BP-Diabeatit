package de.tu_darmstadt.informatik.tk.diabeatit.data.BGSinks;

import android.util.Log;

import de.tu_darmstadt.informatik.tk.diabeatit.data.BgDataManager;
import de.tu_darmstadt.informatik.tk.diabeatit.data.BgDataSink;
import de.tu_darmstadt.informatik.tk.diabeatit.data.BgReading;

/**
 * A dummy sink for testing purposes that only remembers the amount of as well as the last reading
 * it received.
 */
public class DummySink implements BgDataSink {
    /** The last reading received by this sink */
    public BgReading lastReading = null;
    private int totalReadingsReceived = 0;

    /** Gets the total number of readings received by this sink so far */
    public int getTotalReadingsReceived() {
        return totalReadingsReceived;
    }

    @Override
    public void onNewReading(BgReading reading) {
        Log.i("BGDATA", String.format("Received new BG reading: \t%s", reading));
        lastReading = reading;
        totalReadingsReceived ++;
    }

    @Override
    public void onRegister(BgDataManager manager) {
        Log.d("BGDATA", "Registering dummy sink");
    }

    @Override
    public void onUnregister(BgDataManager manager) {
        Log.d("BGDATA", "Unregistering dummy sink");
    }
}
