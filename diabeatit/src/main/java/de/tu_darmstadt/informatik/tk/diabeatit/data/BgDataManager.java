package de.tu_darmstadt.informatik.tk.diabeatit.data;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;

public class BgDataManager extends BroadcastReceiver {
    private BgDataSource currentSource;
    private BgDataSink currentSink;

    public BgDataManager(BgDataSource source, BgDataSink sink, Context context) {
        this.setSink(sink);
        this.setSource(source, context);
    }

    /** Sets the source of BG Data values */
    public void setSource(BgDataSource source, Context context) {
        if (this.currentSource != null) {
            this.currentSource.unregister(this);
            if (context != null)
                context.unregisterReceiver(this);
        }
        this.currentSource = source;
        this.currentSource.register(this);
        if (context != null)
            context.registerReceiver(this, this.currentSource.getIntentFilter());
    }

    /** Sets the sink for BG Data */
    public void setSink(BgDataSink sink) {
        if (this.currentSink != null)
            this.currentSink.unregister(this);
        this.currentSink = sink;
        this.currentSink.register(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("BGDATA", String.format("Received intent %s", intent));

        List<BgReading> readings = this.currentSource.handleNewData(intent);

        if (readings != null)
            for(BgReading r : readings) {
                this.currentSink.reportNewReading(r);
            }
    }
}
