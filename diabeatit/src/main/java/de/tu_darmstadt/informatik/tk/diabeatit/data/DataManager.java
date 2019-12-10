package de.tu_darmstadt.informatik.tk.diabeatit.data;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;

/**
 * This class is responsible for managing the various BG sources and sinks, as well as to provide a
 * consistent interface the rest of the App can interact with
 */
public class DataManager<T> extends BroadcastReceiver {
    private DataSource<T> currentSource;
    private DataSink<T> currentSink;

    public DataManager(DataSource<T> source, DataSink<T> sink, Context context) {
        this.setSink(sink);
        this.setSource(source, context);
    }

    /**
     * Sets the source of BG Data values. This also re-registers itself as a BroadcastReceiver with
     * the IntentFilter provided by the source
     */
    public void setSource(DataSource<T> source, Context context) {
        if (this.currentSource != null) {
            this.currentSource.onUnregister(context, this);
            if (context != null)
                context.unregisterReceiver(this);
        }
        this.currentSource = source;
        this.currentSource.onRegister(context, this);
        if (context != null)
            context.registerReceiver(this, this.currentSource.getIntentFilter());
    }

    /** Sets the sink for BG Data */
    public void setSink(DataSink<T> sink) {
        if (this.currentSink != null)
            this.currentSink.onUnregister(this);
        this.currentSink = sink;
        this.currentSink.onRegister(this);
    }

    /** Handle a reading not originating from the current source, e.g. a manual entry */
    public void handleReading(T reading) {
        this.currentSink.onNewReading(reading);
    }

    // Gets called whenever an Intent we registered for is being broadcast throughout system
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("DATA", String.format("Received intent %s", intent));

        List<T> readings = this.currentSource.handleNewData(context, intent);

        if (readings != null)
            for(T r : readings) {
                this.currentSink.onNewReading(r);
            }
    }
}
