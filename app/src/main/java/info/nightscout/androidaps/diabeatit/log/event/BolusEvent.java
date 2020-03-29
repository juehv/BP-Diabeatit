package info.nightscout.androidaps.diabeatit.log.event;

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

import info.nightscout.androidaps.R;
import info.nightscout.androidaps.diabeatit.log.LogEvent;

@Entity
public class BolusEvent extends LogEvent {

	@ColumnInfo(name = "bolus")
	public final double BOLUS;
	@ColumnInfo(name = "note")
	public final String NOTE;


	public BolusEvent(Instant timestamp, double bolus, String note) {

		super(R.string.mi_event_title, R.drawable.ic_fab_insulin, timestamp);

		BOLUS = bolus;
		NOTE = note;

	}

	public BolusEvent(long logEventId, int TITLE, int ICON, Instant TIMESTAMP, double BOLUS, String NOTE) {
		super(logEventId, TITLE, ICON, TIMESTAMP);
		this.BOLUS = BOLUS;
		this.NOTE = NOTE;
	}

	@Override
	public void createLayout(Context context, RelativeLayout root, boolean isSelected) {

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
		noteV.setVisibility(!NOTE.isEmpty() ? View.VISIBLE : View.GONE);
		imgV.setVisibility(View.GONE);

		root.setBackgroundResource(isSelected ? R.drawable.log_event_selected_background : R.drawable.log_event_background);

		contentV.setText(BOLUS + " IE");
		noteV.setText(NOTE);

	}

}