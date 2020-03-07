package info.nightscout.androidaps.diabeatit.assistant.alert;

import android.app.NotificationManager;
import android.graphics.drawable.Drawable;

import java.util.Date;

import info.nightscout.androidaps.MainApp;
import info.nightscout.androidaps.R;

public class Alert {

	/*
	 * The Urgency dictates the design and position of the Alert card, as well as effects like
	 * notification and ringtone. It has a comparable priority attribute (higher value -> higher
	 * urgency) and the ID of the label drawable.
	 */
	public enum Urgency {

		INFO(1, R.string.alert_label_info, R.drawable.label_gray, R.color.d_info, "info", "General Alerts", NotificationManager.IMPORTANCE_DEFAULT),
		WARNING(2, R.string.alert_label_warning, R.drawable.label_amber, R.color.d_warning, "warning", "Warnings", NotificationManager.IMPORTANCE_HIGH),
		URGENT(3, R.string.alert_label_urgent, R.drawable.label_red, R.color.d_important, "important", "Important Alerts", NotificationManager.IMPORTANCE_HIGH);

		private int priority, stringId, background, rawColor;
		private String channel;

		Urgency(int p, int s, int b, int r, String nId, String nTitle, int nImp) {

			priority = p;
			stringId = s;
			background = b;
			rawColor = r;
			channel = nId;

			NotificationStore.createChannel(nId, nTitle, nTitle, nImp);

		}

		public int getPriority() {
			return priority;
		}

		public int getStringId() {
			return stringId;
		}

		public int getBackground() {
			return background;
		}

		public int getRawColor() {
			return rawColor;
		}

		public String getChannel() {
			return channel;
		}

	}

	private int NOTIFICATION_ID = -1;

	public final Urgency URGENCY;
	public final int ICON_ID;
	public final Drawable ICON;
	public String title, desc;
	public Date timestamp;

	public boolean active = true;
	public boolean notify = true;

	public Alert(Urgency urgency, int icon_id, String title, String descriptionHtml) {

		this(urgency, icon_id, title, descriptionHtml, new Date());

	}

	public Alert(Urgency urgency, int icon_id, String title, String descriptionHtml, Date creation) {

		URGENCY = urgency;
		ICON_ID = icon_id;
		ICON = MainApp.instance().getDrawable(icon_id);
		this.title = title;
		desc = descriptionHtml;
		timestamp = creation;

	}

	public void send() {

		if (!notify || NOTIFICATION_ID >= 0) return;

		NOTIFICATION_ID = NotificationStore.sendNotification(URGENCY.getChannel(), this);

	}

	public void destroy() {

		if (NOTIFICATION_ID < 0) return;

		NotificationStore.removeNotification(NOTIFICATION_ID);
		NOTIFICATION_ID = -1;

	}

	public boolean sent() { return NOTIFICATION_ID >= 0; }

}