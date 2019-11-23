package de.tu_darmstadt.informatik.tk.diabeatit;

import android.app.Application;
import android.content.Context;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import de.tu_darmstadt.informatik.tk.diabeatit.data.Bolus;
import de.tu_darmstadt.informatik.tk.diabeatit.data.BolusSources.ManualBolusSource;
import de.tu_darmstadt.informatik.tk.diabeatit.data.DummySink;
import de.tu_darmstadt.informatik.tk.diabeatit.data.BGSources.ManualBgSource;
import de.tu_darmstadt.informatik.tk.diabeatit.data.BGSources.MultiSource;
import de.tu_darmstadt.informatik.tk.diabeatit.data.BgReading;
import de.tu_darmstadt.informatik.tk.diabeatit.data.DataManager;

public class MainApp extends Application {
    private DataManager<BgReading> bgDataManager;
    private DataManager<Bolus> bolusDataManager;

    @Override
    public void onCreate() {
        super.onCreate();

        Context ctx = getApplicationContext();

        bgDataManager = new DataManager<>(
                new MultiSource<BgReading>()
                    .addSource(new ManualBgSource()),
                new DummySink<BgReading>(),
                ctx);

        bolusDataManager = new DataManager<Bolus>(
                new MultiSource<Bolus>()
                    .addSource(new ManualBolusSource()),
                new DummySink<Bolus>(),
                ctx);
    }
}
