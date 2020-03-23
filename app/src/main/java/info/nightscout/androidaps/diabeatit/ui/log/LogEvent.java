package info.nightscout.androidaps.diabeatit.ui.log;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

import java.time.Instant;

public abstract class LogEvent {

	public final int TITLE, ICON;
	public final Instant TIMESTAMP;

	public LogEvent(int title, int icon, Instant timestamp) {

		TITLE = title;
		ICON = icon;
		TIMESTAMP = timestamp;

	}

	public abstract void createLayout(Context context, RelativeLayout root);

}