package info.nightscout.androidaps.diabeatit.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import info.nightscout.androidaps.MainApp;
import info.nightscout.androidaps.R;
import info.nightscout.androidaps.diabeatit.assistant.alert.Alert;
import info.nightscout.androidaps.diabeatit.assistant.alert.AlertStore;
import info.nightscout.androidaps.diabeatit.assistant.alert.AlertStoreListener;
import info.nightscout.androidaps.diabeatit.assistant.alert.DismissedAlertsManager;

public class AlertHistoryActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
	setContentView(R.layout.d_activity_alert_history);
	getSupportActionBar().setTitle(getResources().getString(R.string.title_activity_alert_history));

	setTheme(R.style.diabeatit);

	AlertStore.dismissedAlerts = new DismissedAlertsManager(this, findViewById(R.id.alert_history_layout));

	  TextView dismissedEmptyT = findViewById(R.id.alert_history_empty_notice);

	  AlertStore.attachListener(new AlertStoreListener() {

		  @Override
		  public void onNewAlert(Alert alert) {
		  }

		  @Override
		  public void onAlertDismissed(Alert alert) {

			  dismissedEmptyT.setVisibility(View.GONE);

		  }

		  @Override
		  public void onAlertRestored(Alert alert) {

			  if (AlertStore.getDismissedAlerts().length == 0)
				  dismissedEmptyT.setVisibility(View.VISIBLE);

		  }

		  @Override
		  public void onAlertsCleared() {
		  }

		  @Override
		  public void onDataSetInit() {

			  dismissedEmptyT.setVisibility(AlertStore.getDismissedAlerts().length == 0 ? View.VISIBLE : View.GONE);

		  }

	  });

  }

	@Override
	public void onBackPressed() {

		finish();

	}
}