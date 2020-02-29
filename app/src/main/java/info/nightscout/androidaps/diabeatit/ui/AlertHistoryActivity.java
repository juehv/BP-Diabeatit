package info.nightscout.androidaps.diabeatit.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import info.nightscout.androidaps.R;
import info.nightscout.androidaps.diabeatit.assistant.alert.AlertStore;
import info.nightscout.androidaps.diabeatit.assistant.alert.DismissedAlertsManager;

public class AlertHistoryActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alert_history);

		setTheme(R.style.diabeatit);

		AlertStore.dismissedAlerts = new DismissedAlertsManager(getApplicationContext(), findViewById(R.id.alert_history_layout));

  }

}