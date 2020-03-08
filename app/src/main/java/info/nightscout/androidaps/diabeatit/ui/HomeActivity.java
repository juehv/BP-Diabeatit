package info.nightscout.androidaps.diabeatit.ui;

import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import info.nightscout.androidaps.MainApp;
import info.nightscout.androidaps.db.BgReading;
import info.nightscout.androidaps.diabeatit.assistant.notification.NotificationStore;
import info.nightscout.androidaps.diabeatit.predictions.PredictionsPlugin;
import info.nightscout.androidaps.diabeatit.service.ForegroundService;
import info.nightscout.androidaps.R;
import info.nightscout.androidaps.diabeatit.assistant.alert.Alert;
import info.nightscout.androidaps.diabeatit.assistant.alert.AlertStore;
import info.nightscout.androidaps.diabeatit.assistant.alert.AlertStoreListener;
import info.nightscout.androidaps.diabeatit.assistant.alert.AlertsManager;
import info.nightscout.androidaps.diabeatit.ui.home.ChartDataParser;
import info.nightscout.androidaps.diabeatit.ui.home.HomeFragment;
import info.nightscout.androidaps.setupwizard.SetupWizardActivity;

public class HomeActivity extends AppCompatActivity {

	private AppBarConfiguration mAppBarConfiguration;
	private FloatingActionsMenu entryMenu;
	private HomeFragment homeFrag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.d_activity_home);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		getSystemService(NotificationManager.class).cancelAll();
		NotificationStore.reset();

		setupManualEntry();
		setupAssistant();
		setupDrawer();

		Intent serviceIntent = new Intent(this, ForegroundService.class);
		startForegroundService(serviceIntent);

		Intent intent = getIntent();
		if (intent != null && intent.getAction() != null && intent.getAction().equals("info.nightscout.androidaps.OPEN_ASSISTANT"))
			expandAssistant();

	}

	private void setupManualEntry() {

		entryMenu = findViewById(R.id.manual_entry_fab_menu);
		FloatingActionButton manualInsulinButton = findViewById(R.id.fab_manual_insulin);
		FloatingActionButton manualCarbsButton = findViewById(R.id.fab_manual_carbs);
		FloatingActionButton manualSportsButton = findViewById(R.id.fab_manual_sports);

		manualInsulinButton.setOnClickListener(v -> {
			startActivity(new Intent(HomeActivity.this, ManualInsulinEntryActivity.class));
			entryMenu.collapseImmediately();
		});

		manualCarbsButton.setOnClickListener(v -> {
			startActivity(new Intent(HomeActivity.this, ManualCarbsEntryActivity.class));
			entryMenu.collapseImmediately();
		});

		manualSportsButton.setOnClickListener(v -> {
			startActivity(new Intent(HomeActivity.this, ManualSportsEntryActivity.class));
			entryMenu.collapseImmediately();
		});

	}

	private void expandAssistant() {

		View nestedScrollView = findViewById(R.id.assistant_scrollview);
		final BottomSheetBehavior assistant = BottomSheetBehavior.from(nestedScrollView);
		final RelativeLayout assistantPeek = findViewById(R.id.assistant_peek);
		final RelativeLayout assistantPeekAlt = findViewById(R.id.assistant_peek_alt);

		entryMenu.setVisibility(View.GONE);
		entryMenu.collapseImmediately();

		assistant.setState(BottomSheetBehavior.STATE_EXPANDED);
		assistantPeek.setVisibility(View.GONE);
		assistantPeekAlt.setVisibility(View.VISIBLE);

	}

	private void setupAssistant() {

		View nestedScrollView = findViewById(R.id.assistant_scrollview);
		final BottomSheetBehavior assistant = BottomSheetBehavior.from(nestedScrollView);
		final RelativeLayout assistantPeek = findViewById(R.id.assistant_peek);
		final RelativeLayout assistantPeekAlt = findViewById(R.id.assistant_peek_alt);

		assistant.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
			@Override
			public void onStateChanged(@NonNull View bottomSheet, int newState) {

				entryMenu.setVisibility(newState == BottomSheetBehavior.STATE_COLLAPSED ? View.VISIBLE : View.GONE);
				entryMenu.collapseImmediately();

				assistantPeek.setVisibility(newState == BottomSheetBehavior.STATE_COLLAPSED ? View.VISIBLE : View.GONE);
				assistantPeekAlt.setVisibility(newState != BottomSheetBehavior.STATE_COLLAPSED ? View.VISIBLE : View.GONE);

			}

			@Override
			public void onSlide(@NonNull View view, float v) {}
		});

		assistantPeek.setOnClickListener(view -> assistant.setState(BottomSheetBehavior.STATE_EXPANDED));

		AlertStore.activeAlerts = new AlertsManager(getApplicationContext(), findViewById(R.id.assistant_card_list), findViewById(R.id.alert_cardview));

		Button alertClearB = findViewById(R.id.alert_clear_all);
		TextView alertEmptyT = findViewById(R.id.alert_empty_notice);

		alertClearB.setOnClickListener(view -> AlertStore.clearAlerts());

		Runnable peekUpdater = () -> {

			TextView titleV = assistantPeek.findViewById(R.id.assistant_peek_title);
			TextView descV = assistantPeek.findViewById(R.id.assistant_peek_description);
			ImageView iconV = assistantPeek.findViewById(R.id.assistant_status_icon);

			Alert.Urgency urgency = Arrays.stream(AlertStore.getActiveAlerts()).map(a -> a.URGENCY).reduce((a, b) -> a.getPriority() > b.getPriority() ? a : b).orElse(Alert.Urgency.INFO);
			int amount = (int) Arrays.stream(AlertStore.getActiveAlerts()).filter(a -> a.URGENCY.equals(urgency)).count();

			int color = amount == 0 ? getColor(android.R.color.holo_green_light) : getColor(urgency.getRawColor());
			String title = amount == 0 ? getString(R.string.assistant_peek_title_none) : getString(urgency.getPeekTitle());
			String desc = AlertStore.getActiveAlerts().length + " " + getString(R.string.assistant_peek_description);
			Drawable icon = amount == 0 ? getDrawable(R.drawable.ic_check) : getDrawable(R.drawable.ic_alert);

			assistantPeek.setBackgroundColor(color);
			titleV.setText(title);
			descV.setText(desc);
			iconV.setImageDrawable(icon);

		};

		AlertStore.attachListener(new AlertStoreListener() {

			@Override
			public void onNewAlert(Alert alert) {

				alertClearB.setVisibility(View.VISIBLE);
				alertEmptyT.setVisibility(View.GONE);

				peekUpdater.run();

			}

			@Override
			public void onAlertDismissed(Alert alert) {

				peekUpdater.run();

			}

			@Override
			public void onAlertRestored(Alert alert) {

				onNewAlert(alert);

			}

			@Override
			public void onAlertsCleared() {

				alertClearB.setVisibility(View.GONE);
				alertEmptyT.setVisibility(View.VISIBLE);

				peekUpdater.run();

			}

			@Override
			public void onDataSetInit() {

				int len = AlertStore.getActiveAlerts().length;
				alertClearB.setVisibility(len == 0 ? View.GONE : View.VISIBLE);
				alertEmptyT.setVisibility(len == 0 ? View.VISIBLE : View.GONE);

				peekUpdater.run();

			}

		});

		CardView alertHistoryC = findViewById(R.id.alert_history);
		alertHistoryC.setOnClickListener(view -> startActivity(new Intent(HomeActivity.this, AlertHistoryActivity.class)));

		// TODO Example alerts - Remove
		List<Alert> as = new ArrayList<>();
		as.add(new Alert(Alert.Urgency.URGENT, R.drawable.ic_battery_alert, "Battery low", "The battery is low."));
		as.add(new Alert(Alert.Urgency.INFO, R.drawable.ic_timeline, "Lorem Ipsum", "Lorem Ipsum!"));
		as.add(new Alert(Alert.Urgency.WARNING, R.drawable.ic_bluetooth_disabled, "Multiline", "Line<br>Break"));
		if (AlertStore.getActiveAlerts().length == 0)
			AlertStore.initAlerts(as.toArray(new Alert[0]));

		findViewById(R.id.alert_settings).setOnClickListener(
						view -> {
							Intent intent = new Intent();
							intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
							intent.putExtra("android.provider.extra.APP_PACKAGE", getPackageName());
							startActivity(intent);
						}
		);

	}

	private void setupDrawer() {

		final DrawerLayout drawer = findViewById(R.id.drawer_layout);
		final NavigationView navView = findViewById(R.id.nav_view);

		mAppBarConfiguration = new AppBarConfiguration.Builder(
						R.id.nav_home, R.id.nav_settings, R.id.nav_device_sensor, R.id.nav_device_pump, R.id.nav_device_tracker)
						.setDrawerLayout(drawer)
						.build();

		NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
		NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
		NavigationUI.setupWithNavController(navView, navController);

		navView.setNavigationItemSelectedListener(menuItem -> {

			if (menuItem.getItemId() == R.id.nav_settings)
				startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
			else if (menuItem.getItemId() == R.id.nav_setup)
				startActivity(new Intent(HomeActivity.this, SetupWizardActivity.class));
			else if (menuItem.getItemId() == R.id.nav_home)
				drawer.closeDrawers();
			else if (menuItem.getItemId() == R.id.add_dummy_data)
				addDummyData();
			return true;

		});

	}

	private void addDummyData() {
		final long timespan = 60 * 60 * 1000;
		List<BgReading> data = ChartDataParser.getDummyData(System.currentTimeMillis() - timespan, System.currentTimeMillis());
		for (BgReading r : data) {
			MainApp.getDbHelper().createIfNotExists(r, "DUMMY");
		}

	}

	@Override
	public void onBackPressed() {

		View nestedScrollView = findViewById(R.id.assistant_scrollview);
		final BottomSheetBehavior assistant = BottomSheetBehavior.from(nestedScrollView);

		if (assistant.getState() == BottomSheetBehavior.STATE_EXPANDED)
			assistant.setState(BottomSheetBehavior.STATE_COLLAPSED);
		else
			super.onBackPressed();

	}

	/*
			Closes entry menu when user clicks somewhere else
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {

		if (event.getAction() == MotionEvent.ACTION_DOWN && entryMenu.isExpanded()){

			Rect outRect = new Rect();
			entryMenu.getGlobalVisibleRect(outRect);

			if (!outRect.contains((int )event.getRawX(), (int) event.getRawY()))
				entryMenu.collapse();
		}

		return super.dispatchTouchEvent(event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.d_home, menu);
		return true;
	}

	@Override
	public boolean onSupportNavigateUp() {
		NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
		return NavigationUI.navigateUp(navController, mAppBarConfiguration)
						|| super.onSupportNavigateUp();
	}
}
