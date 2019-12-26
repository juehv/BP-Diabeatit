package de.tu_darmstadt.informatik.tk.diabeatit.assistant.alert;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import de.tu_darmstadt.informatik.tk.diabeatit.R;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

public class AlertsManager {

  private final Context CONTEXT;
  private List<Alert> alerts = new ArrayList<Alert>();

  private RecyclerView recycler;
  private RecyclerView.LayoutManager layoutManager;
  private AlertAdapter alertAdapter;

  private View alertView;

  private List<AlertManagementListener> listeners;

  public AlertsManager(Context context, RecyclerView recycler, View alertView) {

    CONTEXT = context;
	this.recycler = recycler;
	this.alertView = alertView;

	layoutManager = new LinearLayoutManager(CONTEXT);
	alertAdapter = new AlertAdapter(CONTEXT, alerts);

	this.recycler.setLayoutManager(layoutManager);
	this.recycler.setAdapter(alertAdapter);
	this.recycler.setItemAnimator(new SlideInLeftAnimator());
	new ItemTouchHelper(new SwipeToDismissCallback(alertAdapter, this)).attachToRecyclerView(this.recycler);

	listeners = new ArrayList<AlertManagementListener>();

  }

  public void attachListener(AlertManagementListener listener) {

	listeners.add(listener);

  }

  public void detachListener(AlertManagementListener listener) {

	listeners.remove(listener);

  }

  public void setAlerts(List<Alert> data) {

    alerts.clear();
    alerts.addAll(data);
    alerts.sort((alert0, alert1) -> alert1.URGENCY.getPriority() - alert0.URGENCY.getPriority());

    alertAdapter.notifyDataSetChanged();

    for (AlertManagementListener l : listeners) l.onAlertAdded(alerts.size());

  }

  public void addAlert(Alert alert) {

    if (alerts.contains(alert)) return;

    alerts.add(alert);
    alerts.sort((alert0, alert1) -> alert1.URGENCY.getPriority() - alert0.URGENCY.getPriority());

    alertAdapter.notifyItemInserted(alerts.indexOf(alert));

	for (AlertManagementListener l : listeners) l.onAlertAdded(alerts.size());

  }

  public void removeAlert(Alert alert) {

    if (!alerts.contains(alert)) return;

    int index = alerts.indexOf(alert);
    alerts.remove(index);

    alertAdapter.notifyItemRemoved(index);

    if (alerts.isEmpty())
	  for (AlertManagementListener l : listeners) l.onAlertsCleared();

  }

  public void clearAlerts() {

	alerts.clear();

    //alertAdapter.notifyItemRangeRemoved(0, alertAdapter.getItemCount()); // -- does not work
	alertAdapter.notifyDataSetChanged();

	for (AlertManagementListener l : listeners) l.onAlertsCleared();

  }

  public View getAlertView() {

    return alertView;

  }

}

class SwipeToDismissCallback extends ItemTouchHelper.SimpleCallback{

  private AlertAdapter adapter;
  private AlertsManager mgr;

  private Alert lastRemoved;

  public SwipeToDismissCallback(AlertAdapter adapter, AlertsManager mgr) {

	super(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
	this.adapter = adapter;
	this.mgr = mgr;

  }

  @Override
  public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
	return false;
  }

  @Override
  public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

    Alert toRemove = adapter.alerts.get(viewHolder.getAdapterPosition());
    mgr.removeAlert(toRemove);
    lastRemoved = toRemove;

    showUndoDialog();

  }

  private void showUndoDialog() {

	Snackbar snackbar = Snackbar.make(mgr.getAlertView(), R.string.alert_undo_text, Snackbar.LENGTH_LONG);
	snackbar.setAction(R.string.alert_undo_action, v -> mgr.addAlert(lastRemoved));
	snackbar.show();

  }

}

class AlertAdapter extends RecyclerView.Adapter<AlertAdapter.AlertViewHolder> {

  private final Context CONTEXT;
  public List<Alert> alerts;

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

	TextView labelV = card.findViewById(R.id.card_label);
	ImageView iconV = card.findViewById(R.id.card_icon);
	TextView titleV = card.findViewById(R.id.card_title);
	TextView descV = card.findViewById(R.id.card_description);

	/* Replace label */
	labelV.setText(CONTEXT.getString(alert.URGENCY.getStringId()));
	labelV.setBackground(CONTEXT.getDrawable(alert.URGENCY.getBackground()));

	/* Set icon and text */
	iconV.setImageDrawable(alert.ICON);
	titleV.setText(alert.title);
	descV.setText(Html.fromHtml(alert.desc, Html.FROM_HTML_MODE_COMPACT));

  }

  @Override
  public int getItemCount() { return alerts.size(); }

}