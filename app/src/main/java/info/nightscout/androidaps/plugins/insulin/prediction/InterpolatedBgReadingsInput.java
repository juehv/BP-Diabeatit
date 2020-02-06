package info.nightscout.androidaps.plugins.insulin.prediction;

import android.content.Context;

import java.util.Calendar;
import java.util.List;

import info.nightscout.androidaps.db.BgReading;
import info.nightscout.androidaps.db.DatabaseHelper;

public class InterpolatedBgReadingsInput implements PredictionInputs {
    /* for time calculations */
    private final static long SECOND = 1000;
    private final static long MINUTE = 60 * SECOND;
    private final static long HOUR = 60 * MINUTE;

    private final static long DATAPOINT_INTERVAL = 5 * MINUTE;
    private final static long DATAPOINT_COUNT = 30;

    private InterpolationMethod<Double, Double> interpolation;
    private Context ctx;
    private boolean isSetup;
    private long currentTimestamp;

    public InterpolatedBgReadingsInput(InterpolationMethod<Double, Double> method, Context ctx) {
        this(method, Calendar.getInstance().getTime().getTime(), ctx);

    }

    public InterpolatedBgReadingsInput(InterpolationMethod<Double, Double> method, long startTimestamp, Context ctx) {
        interpolation = method;
        this.ctx = ctx;
        isSetup = false;
        currentTimestamp = startTimestamp;
    }

    private void setupInterpolation() {
        if (isSetup) return;

        DatabaseHelper dbh = new DatabaseHelper(ctx);

        long startTimestamp = currentTimestamp - ((DATAPOINT_COUNT + 1) * DATAPOINT_INTERVAL);

        List<BgReading> readings = dbh.getBgreadingsDataFromTime(startTimestamp, true);

        for (BgReading r : readings) {
            interpolation.addDatapoint(r.getX(), r.getY());
        }

        isSetup = true;
    }

    @Override
    public double[] getInputs() {
        setupInterpolation();

        double[] inputs = new double[(int) DATAPOINT_COUNT];

        for (int i = 0; i < DATAPOINT_COUNT; i++) {
            long ts = currentTimestamp - ((DATAPOINT_COUNT - i) * DATAPOINT_INTERVAL);
            inputs[i] = interpolation.getValueAt((double) ts);
        }

        return inputs;
    }
}
