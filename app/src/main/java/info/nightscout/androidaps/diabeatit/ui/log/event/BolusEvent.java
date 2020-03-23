package info.nightscout.androidaps.diabeatit.ui.log.event;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Locale;

import info.nightscout.androidaps.R;
import info.nightscout.androidaps.diabeatit.ui.log.LogEvent;

public class BolusEvent extends LogEvent {

	public final double BOLUS;
	public final String NOTE;

	public BolusEvent(Instant timestamp, double bolus, String note) {

		super(R.string.mi_event_title, R.drawable.ic_fab_insulin, timestamp);

		BOLUS = bolus;
		NOTE = note;

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
		noteV.setVisibility(!NOTE.isEmpty() ? View.VISIBLE : View.GONE);
		imgV.setVisibility(View.GONE);

		contentV.setText(BOLUS + " IE");
		noteV.setText(NOTE);

	}

}