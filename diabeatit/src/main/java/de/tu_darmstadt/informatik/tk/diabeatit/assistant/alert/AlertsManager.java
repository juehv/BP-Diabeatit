package de.tu_darmstadt.informatik.tk.diabeatit.assistant.alert;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import de.tu_darmstadt.informatik.tk.diabeatit.R;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

public class AlertsManager {

  private final Context CONTEXT;
  private List<Alert> alerts = new ArrayList<Alert>();

  private RecyclerView recycler;
  private RecyclerView.LayoutManager layoutManager;
  private RecyclerView.Adapter alertAdapter;

  public AlertsManager(Context context, RecyclerView recycler) {

    CONTEXT = context;
	this.recycler = recycler;

	layoutManager = new LinearLayoutManager(CONTEXT);
	alertAdapter = new AlertAdapter(CONTEXT, alerts);

	this.recycler.setLayoutManager(layoutManager);
	this.recycler.setAdapter(alertAdapter);
	this.recycler.setItemAnimator(new SlideInLeftAnimator());

  }

  public void setAlerts(List<Alert> data) {

    alerts.clear();
    alerts.addAll(data);

    alertAdapter.notifyDataSetChanged();

  }

  public void addAlert(Alert alert) {

    if (alerts.contains(alert)) return;

    alerts.add(alert);
    alerts.sort((alert0, alert1) -> alert1.URGENCY.getPriority() - alert0.URGENCY.getPriority());

    alertAdapter.notifyItemInserted(alerts.indexOf(alert));

  }

  public void removeAlert(Alert alert) {

    if (!alerts.contains(alert)) return;

    int index = alerts.indexOf(alert);
    alerts.remove(index);

    alertAdapter.notifyItemRemoved(index);

  }

  public void clearAlerts() {

	alerts.clear();

    //alertAdapter.notifyItemRangeRemoved(0, alertAdapter.getItemCount()); // -- does not work
	alertAdapter.notifyDataSetChanged();

  }

}

class AlertAdapter extends RecyclerView.Adapter<AlertAdapter.AlertViewHolder> {

  private final Context CONTEXT;
  private List<Alert> alerts;

  public static class AlertViewHolder extends RecyclerView.ViewHolder {

	public CardView card;

	public AlertViewHolder(CardView card) {

	  super(card);
	  this.card = card;

	}

  }

  public AlertAdapter(Context context, List<Alert> alerts) {

    CONTEXT = context;
	this.alerts = alerts;

  }

  @Override
  public AlertAdapter.AlertViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

	CardView card = (CardView) LayoutInflater.from(parent.getContext())
			.inflate(R.layout.assistant_card, parent, false);

	AlertViewHolder holder = new AlertViewHolder(card);
	return holder;

  }

  @Override
  public void onBindViewHolder(AlertViewHolder holder, int position) {

	Alert alert = alerts.get(position);

	/* Get view/layout elements */
	CardView card = holder.card;

	ViewStub labelV = card.findViewById(R.id.card_label_stub);
	ImageView iconV = card.findViewById(R.id.card_icon);
	TextView titleV = card.findViewById(R.id.card_title);
	TextView descV = card.findViewById(R.id.card_description);

	/* Replace label */
	labelV.setLayoutResource(alert.URGENCY.getLabel());
	labelV.inflate();

	/* Set icon and text */
	iconV.setImageDrawable(alert.ICON);
	titleV.setText(alert.title);
	descV.setText(Html.fromHtml(alert.desc, Html.FROM_HTML_MODE_COMPACT));

  }

  @Override
  public int getItemCount() { return alerts.size(); }

}