package info.nightscout.androidaps.diabeatit.predictions;

import android.content.Context;

import java.util.Calendar;
import java.util.List;

import info.nightscout.androidaps.MainApp;
import info.nightscout.androidaps.db.BgReading;
import info.nightscout.androidaps.db.DatabaseHelper;
import info.nightscout.androidaps.plugins.iob.iobCobCalculator.IobCobCalculatorPlugin;

public class InterpolatedBgReadingsInput implements PredictionInputs {
    /* for time calculations */
    private final static long SECOND = 1000;
    private final static long MINUTE = 60 * SECOND;
    private final static long HOUR = 60 * MINUTE;

    private final static long DATAPOINT_INTERVAL = 5 * MINUTE;
    private final static long DATAPOINT_COUNT = 30;

    private InterpolationMethod<Double, Double> interpolation;
    // private Context ctx;
    private boolean isSetup;
    private long currentTimestamp;

    public InterpolatedBgReadingsInput(InterpolationMethod<Double, Double> method, long startTimestamp) {
        interpolation = method;
        isSetup = false;
        currentTimestamp = startTimestamp;
    }

    private void setupInterpolation() {
        if (isSetup) return;

        DatabaseHelper dbh = MainApp.getDbHelper();

        long startTimestamp = currentTimestamp - ((DATAPOINT_COUNT + 1) * DATAPOINT_INTERVAL);

        List<BgReading> readings = IobCobCalculatorPlugin.getPlugin().getBgReadings();
        List<BgReading> dbReadings = MainApp.getDbHelper().getAllBgreadingsDataFromTime(startTimestamp, true);
        readings.sort((r1, r2) -> Long.compare(r1.date, r2.date));
        for (BgReading r : readings) {
            interpolation.addDatapoint(r.getX(), r.getY());
        }

        isSetup = true;
    }

    @Override
    public float[] getInputs() {
        setupInterpolation();

        float[] inputs = new float[(int) DATAPOINT_COUNT];

        for (int i = 0; i < DATAPOINT_COUNT; i++) {
            long ts = currentTimestamp - ((DATAPOINT_COUNT - i) * DATAPOINT_INTERVAL);
            inputs[i] = (float) (double)interpolation.getValueAt((double) ts);
        }

        return inputs;
    }
}
