package info.nightscout.androidaps.diabeatit.assistant.alert;

import android.graphics.drawable.Drawable;

import java.util.Date;

import info.nightscout.androidaps.R;

public class Alert {

  /*
   * The Urgency dictates the design and position of the Alert card, as well as effects like
   * notification and ringtone. It has a comparable priority attribute (higher value -> higher
   * urgency) and the ID of the label drawable.
   */
  public enum Urgency {

    INFO(1, R.string.alert_label_info, R.drawable.label_gray, R.color.d_info),
	WARNING(2, R.string.alert_label_warning, R.drawable.label_amber, R.color.d_warning),
	URGENT(3, R.string.alert_label_urgent, R.drawable.label_red, R.color.d_important);

    private int priority, stringId, background, rawColor;

    Urgency(int p, int s, int b, int r) {
      priority = p;
      stringId = s;
      background = b;
      rawColor = r;
	}

	public int getPriority() { return priority; }
	public int getStringId() { return stringId; }
	public int getBackground() { return background; }
	public int getRawColor() { return rawColor; }

  }

  public final Urgency URGENCY;
  public final Drawable ICON;
  public String title, desc;
  public Date timestamp;

  public boolean active = true;

  public Alert(Urgency urgency, Drawable icon, String title, String descriptionHtml) {

	this(urgency, icon, title, descriptionHtml, new Date());

  }

  public Alert(Urgency urgency, Drawable icon, String title, String descriptionHtml, Date creation) {

	URGENCY = urgency;
	ICON = icon;
	this.title = title;
	desc = descriptionHtml;
	timestamp = creation;

  }

}