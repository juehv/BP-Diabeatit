package info.nightscout.androidaps.diabeatit.predictions;

import android.content.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import info.nightscout.androidaps.MainApp;
import info.nightscout.androidaps.db.BgReading;
import info.nightscout.androidaps.db.DatabaseHelper;
import info.nightscout.androidaps.utils.SP;

/**
 * Plugin that manages predictions, including providing the values as well as loading the appropiate
 * {@link PredictionModel} from the settings stored in the {@link android.content.SharedPreferences}
 */
public class PredictionsPlugin {
    // Settings keys we use. See also xml/d_predictions_prefs.xml
    /** Key of the preference containing the model type */
    public final static String PREF_KEY_MODEL_TYPE = "d_selected_model_type";
    /** Key of the preference containing the path the the ai model */
    public final static String PREF_KEY_AI_MODEL_PATH = "d_ki_model_name";

    /** Value representing an AI model */
    public final static String MODEL_TYPE_AI = "ki";
    /** Value representing a simple averge delta as prediction model */
    public final static String MODEL_TYPE_AVGDELTA = "avgdelta";

    private static Logger log = LoggerFactory.getLogger("PREDICTIONS");
    private static PredictionsPlugin instance;

	// The currently loaded and used PredictionModel
    private PredictionModel predictionModel;

	/** 
	 * Get the instance of this plugin. If there does not exist such an instance, it is created. 
	 *
	 * @return		The instance of the {@link PredictionsPlugin}
	 */
    public static PredictionsPlugin getPlugin() {
        if (instance == null)
            instance = new PredictionsPlugin();
        return instance;
    }

	/** Instanciate a new plugin instance */
    protected PredictionsPlugin() {
        loadSettings();
    }

	/** Update the instance from the settings */
    public static void updateFromSettings() {
        instance = new PredictionsPlugin();
    }

    /** Load models as defined in the preferences */
    private void loadSettings() {
        String modelType = SP.getString(PREF_KEY_MODEL_TYPE, MODEL_TYPE_AVGDELTA);
        log.info("Loading model type: {} = {}", PREF_KEY_MODEL_TYPE, modelType);

        if (modelType.equals(MODEL_TYPE_AI)) {
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
        String path = SP.getString(PREF_KEY_AI_MODEL_PATH, "");

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

	/** Get the predictions for the start time 
	 *
	 * @param	startTime		Starting time of the predictions
	 * @return					An array of predicted values
	 */
    public float[] getPredictions(long startTime) {
        InterpolationMethod<Double, Double> interpolMethod = new SplineInterpolation();
        PredictionInputs predInputs = new InterpolatedBgReadingsInput(interpolMethod, startTime);
        return predictionModel.predict(predInputs);
    }

	/** Get the blood glucose level reading equivalent of the predictions.
	 *
	 * The difference between this method and {@link #getPredictions} is mostly that they are
	 * represented as a {@link List<BgReading>} instead of a set of raw-values.  They values are
     * still offests instead of an absolute value.
     *
	 * @return	A list of {@link BgReading} that represent the predictions
	 */
    public List<BgReading> getPredictionReadings() {
        // get the last known reading value as base
        BgReading lastBg = DatabaseHelper.lastBg();
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
