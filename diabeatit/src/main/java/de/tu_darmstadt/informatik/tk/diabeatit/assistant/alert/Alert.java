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

    INFO(1, R.layout.label_info),
	WARNING(2, R.layout.label_warning),
	URGENT(3, R.layout.label_urgent);

    private int priority, label;

    Urgency(int p, int l) {
      priority = p;
      label = l;
	}

	public int getPriority() { return priority; }
	public int getLabel() { return label; }

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

