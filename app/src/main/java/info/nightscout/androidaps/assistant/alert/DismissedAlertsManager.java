package info.nightscout.androidaps.assistant.alert;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import info.nightscout.androidaps.R;

public class DismissedAlertsManager {

  private DismissedAlertAdapter alertAdapter = null;

  private List<Alert> alerts = new ArrayList<>();

  public void init(Context context, RecyclerView recycler) {

    //if (alertAdapter != null) return;

	alertAdapter = new DismissedAlertAdapter(context, alerts);

	recycler.setLayoutManager(new LinearLayoutManager(context));
	recycler.setAdapter(alertAdapter);

	alertAdapter.notifyDataSetChanged();

  }

  public void addAlerts(List<Alert> alertList) {

	alerts.addAll(0, alertList);

	if (alertAdapter != null)
	  alertAdapter.notifyItemRangeInserted(0, alertList.size());

  }

  public void addAlert(Alert alert) {

	alerts.add(0, alert);

	if (alertAdapter != null)
	  alertAdapter.notifyItemInserted(0);

  }

  public void clearAlerts() {

	alerts.clear();

	if (alertAdapter != null)
	  alertAdapter.notifyDataSetChanged();

  }

}

class DismissedAlertAdapter extends RecyclerView.Adapter<DismissedAlertAdapter.DismissedAlertViewHolder> {

  private final Context CONTEXT;
  public List<Alert> alerts;

  public static class DismissedAlertViewHolder extends RecyclerView.ViewHolder {

	public RelativeLayout view;

	public DismissedAlertViewHolder(RelativeLayout view) {

	  super(view);
	  this.view = view;

	}

  }

  public DismissedAlertAdapter(Context context, List<Alert> alerts) {

	CONTEXT = context;
	this.alerts = alerts;

  }

  @Override
  public DismissedAlertAdapter.DismissedAlertViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

	RelativeLayout view = (RelativeLayout) LayoutInflater.from(parent.getContext())
			.inflate(R.layout.alert_history_entry, parent, false);

	DismissedAlertViewHolder holder = new DismissedAlertViewHolder(view);
	return holder;

  }

  @Override
  public void onBindViewHolder(DismissedAlertViewHolder holder, int position) {

	Alert alert = alerts.get(position);
	RelativeLayout view = holder.view;

	TextView timeV = view.findViewById(R.id.alert_history_entry_time);
	TextView urgencyV = view.findViewById(R.id.alert_history_entry_urgency);
	TextView titleV = view.findViewById(R.id.alert_history_entry_title);
	TextView descV = view.findViewById(R.id.alert_history_entry_description);

	timeV.setText(new SimpleDateFormat("d.M H:mm").format(alert.timestamp));
	urgencyV.setText(CONTEXT.getString(alert.URGENCY.getStringId()));
	urgencyV.setTextColor(CONTEXT.getColor(alert.URGENCY.getRawColor()));
	titleV.setText(alert.title);
	descV.setText(Html.fromHtml(alert.desc, Html.FROM_HTML_MODE_COMPACT));

  }

  @Override
  public int getItemCount() { return alerts.size(); }

}