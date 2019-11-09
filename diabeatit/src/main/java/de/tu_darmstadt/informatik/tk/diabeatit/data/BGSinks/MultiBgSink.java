package de.tu_darmstadt.informatik.tk.diabeatit.data.BGSinks;

import java.util.ArrayList;
import java.util.Iterator;

import de.tu_darmstadt.informatik.tk.diabeatit.data.BgDataManager;
import de.tu_darmstadt.informatik.tk.diabeatit.data.BgDataSink;
import de.tu_darmstadt.informatik.tk.diabeatit.data.BgReading;

public class MultiBgSink implements BgDataSink {
    private ArrayList<BgDataSink> sinks;
    private boolean currentlyRegistered;

    public MultiBgSink() {
        sinks = new ArrayList<>();
        currentlyRegistered = false;
    }

    public void addSink(BgDataSink sink) {
        if (currentlyRegistered) {
            throw new UnsupportedOperationException("Cannot modify a MultiBgSink that is currently registered");
        }

        // Don't allow to register the same sink twice
        if (!sinks.contains(sink)) {
            sinks.add(sink);
        }
    }

    public void removeSink(BgDataSink sink) {
        if (currentlyRegistered) {
            throw new UnsupportedOperationException("Cannot modify a MultiBgSink that is currently registered");
        }

        sinks.remove(sink);
    }

    public Iterator<BgDataSink> getSinksIterator() {
        return sinks.iterator();
    }

    @Override
    public void onNewReading(BgReading reading) {
        for (BgDataSink sink : sinks) {
            BgReading r = new BgReading(reading);
            sink.onNewReading(r);
        }
    }

    @Override
    public void onRegister(BgDataManager manager) {
        currentlyRegistered = true;
        for (BgDataSink sink : sinks) {
            sink.onRegister(manager);
        }
    }

    @Override
    public void onUnregister(BgDataManager manager) {
        for (BgDataSink sink : sinks) {
            sink.onUnregister(manager);
        }

        currentlyRegistered = false;
    }
}
