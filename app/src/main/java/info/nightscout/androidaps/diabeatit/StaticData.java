package info.nightscout.androidaps.diabeatit;

/**
 * Collection of constants
 */
public class StaticData {

	/** PIN that when entered into the carbs field of the bolus calculator will open the legacy UI */
	public static final String DEVELOPER_PIN = "0000";
	/**
	 * PIN that when entered into the carbs field of the bolus calculator will add a variety of
	 * dummy data
	 */
	public static final String DUMMYDATA_PIN = "1234";

	/** ID of the foreground service*/
	public static final int FOREGROUND_SERVICE_ID = 1;
	/** Unique ID of the intent generated to restart the app */
	public static final int RESTART_INTENT_ID = 420;

	/** Intent code that is used to open the assistant */
	public static final String ASSISTANT_INTENT_CODE = "info.nightscout.androidaps.OPEN_ASSISTANT";

	/** URL pointing to the user handbook */
	public static final String HANDBOOK_URL = "http://lu-e.de/d"; // TODO Update
	/** Link that is opened when the user tries to contact for help */
	public static final String CONTACT_MAIL = "mailto:bp@lu-e.de"; // TODO Update
	/** Linkt hat is opened when the user tries to send an error report */
	public static final String ERROR_MAIL = CONTACT_MAIL  +"?subject=Stacktrace&body=%s"; // TODO Update

	/** Name of the database managed by Room */
	public static final String ROOM_DATABASE_NAME = "diabeatit";

	/** Stabilize the closing behaviour of the Assistant.*/
	public static boolean assistantInhibitClose = false;

}