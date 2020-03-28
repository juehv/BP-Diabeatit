package info.nightscout.androidaps.diabeatit.ui.log.event;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Locale;

import info.nightscout.androidaps.MainActivity;
import info.nightscout.androidaps.MainApp;
import info.nightscout.androidaps.R;
import info.nightscout.androidaps.diabeatit.ui.HomeActivity;
import info.nightscout.androidaps.diabeatit.ui.log.LogEvent;

@Entity
public class SportsEvent extends LogEvent {

	@ColumnInfo(name = "duration")
	public final int DURATION;
	@ColumnInfo(name = "description")
	public final String DESCRIPTION;

	public SportsEvent(long logEventId, int TITLE, int ICON, Instant TIMESTAMP, int DURATION, String DESCRIPTION) {
		super(logEventId, TITLE, ICON, TIMESTAMP);
		this.DURATION = DURATION;
		this.DESCRIPTION = DESCRIPTION;
	}

	public SportsEvent(Instant timestamp, int duration, String description) {

		super(R.string.ms_event_title, R.drawable.ic_fab_sports, timestamp);

		DURATION = duration;
		DESCRIPTION = description;

	}

	@Override
	public void createLayout(Context context, RelativeLayout root) {

		TextView titleV = root.findViewById(R.id.log_event_title);
		ImageView iconV = root.findViewById(R.id.log_event_icon);
		TextView timeV = root.findViewById(R.id.log_event_time);
		TextView contentV = root.findViewById(R.id.log_event_content);
		TextView noteV = root.findViewById(R.id.log_event_note);
		ImageView imgV = root.findViewById(R.id.log_event_picture);

		titleV.setText(TITLE);
		iconV.setImageResource(ICON);
		timeV.setText(new SimpleDateFormat("dd.MM.YYYY HH:mm", Locale.GERMAN).format(Date.from(TIMESTAMP)));

		contentV.setVisibility(View.VISIBLE);
		noteV.setVisibility(View.VISIBLE);
		imgV.setVisibility(View.GONE);

		contentV.setText(context.getString(R.string.ms_event_minutes, DURATION));
		noteV.setText(DESCRIPTION);

	}

}