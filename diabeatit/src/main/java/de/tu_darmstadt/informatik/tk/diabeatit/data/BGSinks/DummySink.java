package de.tu_darmstadt.informatik.tk.diabeatit.data.BGSinks;

import android.util.Log;

import de.tu_darmstadt.informatik.tk.diabeatit.data.BgDataManager;
import de.tu_darmstadt.informatik.tk.diabeatit.data.BgDataSink;
import de.tu_darmstadt.informatik.tk.diabeatit.data.BgReading;

public class DummySink implements BgDataSink {

    public BgReading lastReading = null;
    private int totalReadingsReceived = 0;

    public int getTotalReadingsReceived() {
        return totalReadingsReceived;
    }

    @Override
    public void reportNewReading(BgReading reading) {
        Log.i("BGDATA", String.format("Received new BG reading: \t%s", reading));
        lastReading = reading;
        totalReadingsReceived ++;
    }

    @Override
    public void register(BgDataManager manager) {
        Log.d("BGDATA", "Registering dummy sink");
    }

    @Override
    public void unregister(BgDataManager manager) {
        Log.d("BGDATA", "Unregistering dummy sink");
    }
}
