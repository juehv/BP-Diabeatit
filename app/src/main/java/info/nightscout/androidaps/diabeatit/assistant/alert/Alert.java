package info.nightscout.androidaps.diabeatit.assistant.alert;

import android.app.NotificationManager;
import android.graphics.drawable.Drawable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

import info.nightscout.androidaps.MainApp;
import info.nightscout.androidaps.R;
import info.nightscout.androidaps.diabeatit.assistant.notification.NotificationStore;

@Entity
public class Alert {

	/*
	 * The Urgency dictates the design and position of the Alert card, as well as effects like
	 * notification and ringtone. It has a comparable priority attribute (higher value -> higher
	 * urgency) and the ID of the label drawable.
	 */
	public enum Urgency {

		INFO(1, R.string.alert_label_info, R.drawable.label_gray, R.color.d_info, "info", "General Alerts", NotificationManager.IMPORTANCE_DEFAULT, R.string.assistant_peek_title_info),
		WARNING(2, R.string.alert_label_warning, R.drawable.label_amber, R.color.d_warning, "warning", "Warnings", NotificationManager.IMPORTANCE_HIGH, R.string.assistant_peek_title_warning),
		URGENT(3, R.string.alert_label_urgent, R.drawable.label_red, R.color.d_important, "important", "Important Alerts", NotificationManager.IMPORTANCE_HIGH, R.string.assistant_peek_title_urgent);

		private int priority, stringId, background, rawColor, peekTitle;
		private String channel;

		Urgency(int p, int s, int b, int r, String nId, String nTitle, int nImp, int t) {

			priority = p;
			stringId = s;
			background = b;
			rawColor = r;
			channel = nId;
			peekTitle = t;

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

		public int getPeekTitle() { return peekTitle; }

	}

	@PrimaryKey
	public long AlertId;

	private int NOTIFICATION_ID = -1;

	@ColumnInfo(name = "urgency")
	public final Urgency URGENCY;
	@ColumnInfo(name = "icon_id")
	public final int ICON_ID;
	public final Drawable ICON;
	@ColumnInfo(name = "title")
	public String title;
	@ColumnInfo(name = "description")
	public String desc;
	@ColumnInfo(name = "timestamp")
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

		if (!notify) return;

		destroy();
		NOTIFICATION_ID = NotificationStore.sendNotification(URGENCY.getChannel(), this);

	}

	public void destroy() {

		if (NOTIFICATION_ID < 0) return;

		NotificationStore.removeNotification(NOTIFICATION_ID);
		NOTIFICATION_ID = -1;

	}

}