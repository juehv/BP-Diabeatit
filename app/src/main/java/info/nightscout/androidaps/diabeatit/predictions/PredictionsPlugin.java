package info.nightscout.androidaps.diabeatit.predictions;

import android.content.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import info.nightscout.androidaps.MainApp;
import info.nightscout.androidaps.db.BgReading;
import info.nightscout.androidaps.db.DatabaseHelper;
import info.nightscout.androidaps.utils.SP;

public class PredictionsPlugin {
    // Settings keys we use. See also xml/d_predictions_prefs.xml
    public final static String PREF_KEY_MODEL_TYPE = "d_selected_model_type";
    public final static String PREF_KEY_KI_MODEL_PATH = "d_ki_model_name";

    public final static String MODEL_TYPE_KI = "ki";
    public final static String MODEL_TYPE_AVGDELTA = "avgdelta";

    private static Logger log = LoggerFactory.getLogger("PREDICTIONS");
    private static PredictionsPlugin instance;

    private PredictionModel predictionModel;

    public static PredictionsPlugin getPlugin() {
        if (instance == null)
            instance = new PredictionsPlugin();
        return instance;
    }

    protected PredictionsPlugin() {
        loadSettings();
    }

    public static void updateFromSettings() {
        instance = new PredictionsPlugin();
    }

    /** Load models as defined in the preferences */
    private void loadSettings() {
        String modelType = SP.getString(PREF_KEY_MODEL_TYPE, MODEL_TYPE_AVGDELTA);
        log.info("Loading model type: {} = {}", PREF_KEY_MODEL_TYPE, modelType);

        if (modelType.equals(MODEL_TYPE_KI)) {
            try {
                loadKiModel();
                return;
            } catch (IOException e) {
                log.error("Failed to load AI model, falling back to {}", MODEL_TYPE_AVGDELTA, e);
                modelType = MODEL_TYPE_AVGDELTA;
            }
        }
        if (modelType.equals(MODEL_TYPE_AVGDELTA)) {
            loadAvgDeltaModel();
        }
    }

    /** Load a KI Model as defined in preferences */
    private void loadKiModel() throws IOException {
        // TODO: From files other than assets
        String path = SP.getString(PREF_KEY_KI_MODEL_PATH, "");

        log.info("Loading AI Model from {}", path);

        Context appCtx = MainApp.instance().getApplicationContext();
        try {
            predictionModel = TensorflowPredictionModel.fromFile(appCtx, path);
        } catch (IOException ex) {
            predictionModel = TensorflowPredictionModel.fromAsset(appCtx, path);
        }
    }

    /** Load the AvgDelta model */
    private void loadAvgDeltaModel() {
        predictionModel = new SlopeBGPredictionModel();
    }

    public float[] getPredictions(long startTime) {
        InterpolationMethod<Double, Double> interpolMethod = new SplineInterpolation();
        PredictionInputs predInputs = new InterpolatedBgReadingsInput(interpolMethod, startTime);
        return predictionModel.predict(predInputs);
    }

    public List<BgReading> getPredictionReadings() {
        BgReading lastBg = DatabaseHelper.lastBg();
        if (lastBg == null) {
            log.warn("Could not find last bg reading");
        }
        long timestamp_base = lastBg != null ? lastBg.date : System.currentTimeMillis();
        for (BgReading reading : MainApp.getDbHelper().getBgreadingsDataFromTime(Instant.now().toEpochMilli() - 1000 * 60 * 60 * 24, true)) {
            if (reading.date > timestamp_base)
                timestamp_base = reading.date;
        }

        final long DELTA_5MIN = 5 * 60 * 1000;
        float[] values = getPredictions(timestamp_base);
        ArrayList<BgReading> readings = new ArrayList<>();

        for (int i = 0; i < values.length; i++) {
            readings.add(new BgReading()
            .date(timestamp_base + i * DELTA_5MIN)
            .value(values[i]));
        }

        return readings;
    }
}
