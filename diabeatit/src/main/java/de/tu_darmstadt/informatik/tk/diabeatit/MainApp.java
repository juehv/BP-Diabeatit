package de.tu_darmstadt.informatik.tk.diabeatit;

import android.app.Application;
import android.content.Context;

import de.tu_darmstadt.informatik.tk.diabeatit.data.BGSinks.DummySink;
import de.tu_darmstadt.informatik.tk.diabeatit.data.BGSources.DummySource;
import de.tu_darmstadt.informatik.tk.diabeatit.data.BgDataManager;

public class MainApp extends Application {
    private BgDataManager bgDataManager;

    @Override
    public void onCreate() {
        super.onCreate();

        Context ctx = getApplicationContext();

        bgDataManager = new BgDataManager(new DummySource(), new DummySink(), ctx);
    }
}
