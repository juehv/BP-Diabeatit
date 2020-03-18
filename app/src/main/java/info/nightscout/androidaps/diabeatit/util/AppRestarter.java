package info.nightscout.androidaps.diabeatit.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import info.nightscout.androidaps.MainApp;
import info.nightscout.androidaps.R;
import info.nightscout.androidaps.diabeatit.StaticData;
import info.nightscout.androidaps.diabeatit.ui.HomeActivity;

public class AppRestarter {

	public static void restartApp() {

		Context context = MainApp.instance().getApplicationContext();

		Intent initIntent = new Intent(context, HomeActivity.class);
		PendingIntent restartIntent = PendingIntent.getActivity(context, StaticData.RESTART_INTENT_ID, initIntent, PendingIntent.FLAG_CANCEL_CURRENT);

		AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, restartIntent);

		System.exit(0);

	}

}