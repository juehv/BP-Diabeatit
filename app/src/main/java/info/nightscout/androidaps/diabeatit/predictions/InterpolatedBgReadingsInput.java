package info.nightscout.androidaps.diabeatit.predictions;

import android.content.Context;

import java.util.Calendar;
import java.util.List;

import info.nightscout.androidaps.MainApp;
import info.nightscout.androidaps.db.BgReading;
import info.nightscout.androidaps.db.DatabaseHelper;
import info.nightscout.androidaps.plugins.iob.iobCobCalculator.IobCobCalculatorPlugin;

/** 
 * Input class consiting of an interpolated curve using the last readings
 */
public class InterpolatedBgReadingsInput implements PredictionInputs {
    // Constants for time calculations, the time in milliseconds the unit represents
    /** Amount of milliseconds in a second */
    private final static long SECOND = 1000;
    /** Amount of milliseconds in a minute */
    private final static long MINUTE = 60 * SECOND;
    /** Amount of milliseconds in an hour */
    private final static long HOUR = 60 * MINUTE;

	/** Interval between datapoints */
    private final static long DATAPOINT_INTERVAL = 5 * MINUTE;
	/** Amount of datapoints to use */
    private final static long DATAPOINT_COUNT = 30;

    /** Interpolation method used */
    private InterpolationMethod<Double, Double> interpolation;
    /** {@code true} if the interpolation is set up */
    private boolean isSetup;
    /** The timestamp that is considered 'now' in the calculations */
    private long currentTimestamp;

	/** Create a new input for the given `startTimestamp` and {@link InterpolationMethod}*/
    public InterpolatedBgReadingsInput(InterpolationMethod<Double, Double> method, long startTimestamp) {
        interpolation = method;
        isSetup = false;
        currentTimestamp = startTimestamp;
    }

	/** 
	 * Set the interpolation up to be ready to use. Since this may be computationally intensive, it
	 * is not done on construction of the object.
	 */
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
