package de.tu_darmstadt.informatik.tk.diabeatit.assistant.alert;

import android.graphics.drawable.Drawable;

import de.tu_darmstadt.informatik.tk.diabeatit.R;

public class Alert {

  /*
   * The Urgency dictates the design and position of the Alert card, as well as effects like
   * notification and ringtone. It has a comparable priority attribute (higher value -> higher
   * urgency) and the ID of the label drawable.
   */
  public enum Urgency {

    INFO(1, R.string.alert_label_info, R.drawable.label_gray),
	WARNING(2, R.string.alert_label_warning, R.drawable.label_amber),
	URGENT(3, R.string.alert_label_urgent, R.drawable.label_red);

    private int priority, stringId, background;

    Urgency(int p, int s, int b) {
      priority = p;
      stringId = s;
      background = b;
	}

	public int getPriority() { return priority; }
	public int getStringId() { return stringId; }
	public int getBackground() { return background; }

  }

  public final Urgency URGENCY;
  public final Drawable ICON;
  public String title, desc;

  public Alert(Urgency urgency, Drawable icon, String title, String descriptionHtml) {

	URGENCY = urgency;
	ICON = icon;
	this.title = title;
	desc = descriptionHtml;

  }

}

