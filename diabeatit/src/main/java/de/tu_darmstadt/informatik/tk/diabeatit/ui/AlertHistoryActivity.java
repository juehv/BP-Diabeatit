package de.tu_darmstadt.informatik.tk.diabeatit.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import de.tu_darmstadt.informatik.tk.diabeatit.R;
import de.tu_darmstadt.informatik.tk.diabeatit.assistant.alert.DismissedAlertsManager;
import de.tu_darmstadt.informatik.tk.diabeatit.assistant.alert.Global;

public class AlertHistoryActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_alert_history);

	Global.dismissedAlerts.init(getApplicationContext(), findViewById(R.id.alert_history_layout));

  }

}