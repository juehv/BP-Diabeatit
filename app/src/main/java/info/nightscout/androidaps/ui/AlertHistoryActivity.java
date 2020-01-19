package info.nightscout.androidaps.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import info.nightscout.androidaps.R;
import info.nightscout.androidaps.assistant.alert.Global;

public class AlertHistoryActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_alert_history);

	Global.dismissedAlerts.init(getApplicationContext(), findViewById(R.id.alert_history_layout));

  }

}