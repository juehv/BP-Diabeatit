package info.nightscout.androidaps.diabeatit.log.event;

import android.content.Context;
import android.graphics.Bitmap;
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
public class CarbsEvent extends LogEvent {

	@ColumnInfo(name = "image")
	public final Bitmap IMAGE;
	@ColumnInfo(name = "carbs")
	public final int CARBS;
	@ColumnInfo(name = "notes")
	public final String NOTE;

	public CarbsEvent(long logEventId, int TITLE, int ICON, Instant TIMESTAMP, Bitmap IMAGE, int CARBS, String NOTE) {
		super(logEventId, TITLE, ICON, TIMESTAMP);
		this.IMAGE = IMAGE;
		this.CARBS = CARBS;
		this.NOTE = NOTE;
	}

	public CarbsEvent(Instant timestamp, Bitmap image, int carbs, String note) {

		super(R.string.mc_event_title, R.drawable.ic_fab_carbs, timestamp);

		IMAGE = image;
		CARBS = carbs;
		NOTE = note;

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
		imgV.setVisibility(IMAGE != null ? View.VISIBLE : View.GONE);

		root.setBackgroundResource(isSelected ? R.drawable.log_event_selected_background : R.drawable.log_event_background);

		contentV.setText(CARBS + "g");
		noteV.setText(NOTE);

		if (IMAGE != null)
			imgV.setImageBitmap(IMAGE);

	}

}