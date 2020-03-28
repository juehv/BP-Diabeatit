package info.nightscout.androidaps.diabeatit.ui.log;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

import java.time.Instant;

public abstract class LogEvent {

	@PrimaryKey
	public long LogEventId;
	@ColumnInfo(name = "title")
	public final int TITLE;
	@ColumnInfo(name = "icon")
	public final int ICON;
	@ColumnInfo(name = "timestamp")
	public final Instant TIMESTAMP;

	public LogEvent(int title, int icon, Instant timestamp) {

		TITLE = title;
		ICON = icon;
		TIMESTAMP = timestamp;

	}

	public abstract void createLayout(Context context, RelativeLayout root);

}