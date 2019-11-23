package de.tu_darmstadt.informatik.tk.diabeatit.data;

import java.util.ArrayList;
import java.util.Iterator;

import de.tu_darmstadt.informatik.tk.diabeatit.data.DataManager;
import de.tu_darmstadt.informatik.tk.diabeatit.data.DataSink;
import de.tu_darmstadt.informatik.tk.diabeatit.data.BgReading;

public class MultiSink<T> implements DataSink<T> {
    private ArrayList<DataSink<T>> sinks;
    private boolean currentlyRegistered;

    public MultiSink() {
        sinks = new ArrayList<>();
        currentlyRegistered = false;
    }

    public void addSink(DataSink<T> sink) {
        if (currentlyRegistered) {
            throw new UnsupportedOperationException("Cannot modify a MultiSink that is currently registered");
        }

        // Don't allow to register the same sink twice
        if (!sinks.contains(sink)) {
            sinks.add(sink);
        }
    }

    public void removeSink(DataSink<T> sink) {
        if (currentlyRegistered) {
            throw new UnsupportedOperationException("Cannot modify a MultiSink that is currently registered");
        }

        sinks.remove(sink);
    }

    public Iterator<DataSink<T>> getSinksIterator() {
        return sinks.iterator();
    }

    @Override
    public void onNewReading(T reading) {
       for (DataSink<T> sink : sinks) {
           sink.onNewReading(reading);
        }
    }

    @Override
    public void onRegister(DataManager<T> manager) {
        currentlyRegistered = true;
        for (DataSink<T> sink : sinks) {
            sink.onRegister(manager);
        }
    }

    @Override
    public void onUnregister(DataManager<T> manager) {
        for (DataSink<T> sink : sinks) {
            sink.onUnregister(manager);
        }

        currentlyRegistered = false;
    }
}
