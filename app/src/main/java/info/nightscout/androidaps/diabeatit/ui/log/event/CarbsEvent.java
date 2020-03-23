package info.nightscout.androidaps.diabeatit.ui.log.event;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
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

public class CarbsEvent extends LogEvent {

	public final Bitmap IMAGE;
	public final int CARBS;
	public final String NOTE;

	public CarbsEvent(Instant timestamp, Bitmap image, int carbs, String note) {

		super(R.string.mc_event_title, R.drawable.ic_fab_carbs, timestamp);

		IMAGE = image;
		CARBS = carbs;
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
		noteV.setVisibility(NOTE != "" ? View.VISIBLE : View.GONE);
		imgV.setVisibility(View.VISIBLE);

		contentV.setText(CARBS + " kcal");
		noteV.setText(NOTE);

		imgV.setImageBitmap(IMAGE);
		imgV.setMaxHeight(IMAGE.getHeight());

	}

}