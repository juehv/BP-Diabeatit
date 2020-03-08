package info.nightscout.androidaps.diabeatit.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import info.nightscout.androidaps.R;
import info.nightscout.androidaps.diabeatit.assistant.alert.Alert;
import info.nightscout.androidaps.diabeatit.assistant.alert.NotificationStore;

public class ForegroundService extends Service {

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		Alert alert = new Alert(Alert.Urgency.INFO, R.drawable.ic_cake, "Diabeatit running", "The diabeatit service is online.");

		NotificationStore.createChannel("service", "Keep-Alive", "Foreground Service Notification", NotificationManager.IMPORTANCE_LOW);
		startForeground(1, NotificationStore.createNotification("service", alert, false));

		return START_NOT_STICKY;

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}