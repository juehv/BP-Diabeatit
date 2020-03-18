package info.nightscout.androidaps.diabeatit.assistant.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.text.Html;

import androidx.core.app.NotificationCompat;

import java.util.HashMap;

import javax.annotation.Nullable;

import info.nightscout.androidaps.MainApp;
import info.nightscout.androidaps.diabeatit.StaticData;
import info.nightscout.androidaps.diabeatit.assistant.alert.Alert;
import info.nightscout.androidaps.diabeatit.ui.HomeActivity;

public class NotificationStore {

	public static final String DEFAULT_CHANNEL_ID = "default";

	private static HashMap<String, NotificationChannel> channels = new HashMap<>();
	private static HashMap<Integer, Alert> activeNotifications = new HashMap<>();

	static {

		createChannel(DEFAULT_CHANNEL_ID, "Default Channel", "Universal Notification Channel", NotificationManager.IMPORTANCE_DEFAULT);

	}

	public static boolean createChannel(String id, String name, String description, int importance) {

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O || channels.containsKey(id)) return false;

		NotificationChannel channel = new NotificationChannel(id, name, importance);
		channel.setDescription(description);

		NotificationManager notificationManager = MainApp.instance().getSystemService(NotificationManager.class);
		notificationManager.createNotificationChannel(channel);

		channels.put(id, channel);
		return true;

	}

	public static Notification createNotification(@Nullable String channel, Alert alert, boolean autoCancel) {

		if (channel == null) channel = DEFAULT_CHANNEL_ID;

		Intent intent = new Intent(MainApp.instance(), HomeActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		intent.setAction(StaticData.ASSISTANT_INTENT_CODE);
		PendingIntent pendingIntent = PendingIntent.getActivity(MainApp.instance(), 0, intent, 0);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(MainApp.instance(), channel)
						.setSmallIcon(alert.ICON_ID)
						.setContentTitle(alert.title)
						.setContentText(Html.fromHtml(alert.desc, Html.FROM_HTML_MODE_COMPACT))
						.setPriority(NotificationCompat.PRIORITY_DEFAULT)
						.setAutoCancel(autoCancel)
						.setContentIntent(pendingIntent)
						.setGroup(channel);

		return builder.build();

	}

	public static int sendNotification(@Nullable String channel, Alert alert) {

		int id = nextId();
		MainApp.instance().getSystemService(NotificationManager.class).notify(id, createNotification(channel, alert, true));

		activeNotifications.put(id, alert);
		return id;

	}

	public static void removeNotification(int id) {

		MainApp.instance().getSystemService(NotificationManager.class).cancel(id);

	}

	public static void reset() {

		activeNotifications.clear();

	}

	private static int nextId() {

		return activeNotifications.keySet().stream().reduce((a, b) -> a > b ? a : b).orElse(1) + 1;

	}

}