package info.nightscout.androidaps.diabeatit.ui;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.lang.ref.WeakReference;
import java.util.Arrays;

import info.nightscout.androidaps.diabeatit.StaticData;
import info.nightscout.androidaps.diabeatit.assistant.notification.NotificationStore;
import info.nightscout.androidaps.diabeatit.service.ForegroundService;
import info.nightscout.androidaps.R;
import info.nightscout.androidaps.diabeatit.assistant.alert.Alert;
import info.nightscout.androidaps.diabeatit.assistant.alert.AlertStore;
import info.nightscout.androidaps.diabeatit.assistant.alert.AlertStoreListener;
import info.nightscout.androidaps.diabeatit.assistant.alert.AlertsManager;
import info.nightscout.androidaps.diabeatit.log.LogActivity;
import info.nightscout.androidaps.setupwizard.SetupWizardActivity;

public class HomeActivity extends AppCompatActivity {

	private static WeakReference<HomeActivity> instance;

	public LinearLayout assistantPeekEnveloped;

	private AppBarConfiguration mAppBarConfiguration;
	private FloatingActionsMenu entryMenu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.d_activity_home);
		Toolbar toolbar = findViewById(R.id.toolbar);
		toolbar.setTitle(getResources().getString(R.string.title_activity_home));
		setSupportActionBar(toolbar);

		getSystemService(NotificationManager.class).cancelAll();
		NotificationStore.reset();

		assistantPeekEnveloped = findViewById(R.id.assistant_peek_master);

		setupManualEntry();
		setupAssistant();
		setupDrawer();

		Intent serviceIntent = new Intent(this, ForegroundService.class);
		startForegroundService(serviceIntent);

		Intent intent = getIntent();
		if (intent != null && intent.getAction() != null && intent.getAction().equals(StaticData.ASSISTANT_INTENT_CODE))
			expandAssistant();

		instance = new WeakReference<>(this);

	}

	private void setupManualEntry() {

		entryMenu = findViewById(R.id.manual_entry_fab_menu);
		FloatingActionButton manualInsulinButton = findViewById(R.id.fab_manual_insulin);
		FloatingActionButton manualCarbsButton = findViewById(R.id.fab_manual_carbs);
		FloatingActionButton manualSportsButton = findViewById(R.id.fab_manual_sports);
		FloatingActionButton manualNoteButton = findViewById(R.id.fab_note);

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

		manualNoteButton.setOnClickListener(v -> {
			startActivity(new Intent(HomeActivity.this, ManualNoteActivity.class));
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

		final View nestedScrollView = findViewById(R.id.assistant_scrollview);
		final BottomSheetBehavior assistant = BottomSheetBehavior.from(nestedScrollView);
		final RelativeLayout assistantPeek = findViewById(R.id.assistant_peek);
		final RelativeLayout assistantPeekAlt = findViewById(R.id.assistant_peek_alt);
		final TextView assistantCloseHint = findViewById(R.id.assistant_close_hint);

		assistant.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {

			@Override
			public void onStateChanged(@NonNull View bottomSheet, int newState) {

				entryMenu.setVisibility(newState == BottomSheetBehavior.STATE_COLLAPSED ? View.VISIBLE : View.GONE);
				entryMenu.collapseImmediately();

				assistantPeek.setVisibility(newState == BottomSheetBehavior.STATE_COLLAPSED ? View.VISIBLE : View.GONE);
				assistantPeekAlt.setVisibility(newState != BottomSheetBehavior.STATE_COLLAPSED ? View.VISIBLE : View.GONE);
				assistantCloseHint.setVisibility(newState == BottomSheetBehavior.STATE_EXPANDED ? View.VISIBLE : View.GONE);

				if (StaticData.assistantInhibitClose && newState == BottomSheetBehavior.STATE_DRAGGING)
					assistant.setState(BottomSheetBehavior.STATE_EXPANDED);

				StaticData.assistantInhibitClose = false;

			}

			@Override
			public void onSlide(@NonNull View view, float v) {}

		});

		assistantPeek.setOnClickListener(view -> assistant.setState(BottomSheetBehavior.STATE_EXPANDED));

		AlertStore.activeAlerts = new AlertsManager(this, findViewById(R.id.assistant_card_list), findViewById(R.id.alert_cardview));

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

		findViewById(R.id.alert_settings).setOnClickListener(
						view -> {
							Intent intent = new Intent();
							intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
							intent.putExtra("android.provider.extra.APP_PACKAGE", getPackageName());
							startActivity(intent);
						}
		);

		boolean a = false;

		findViewById(R.id.assistant_card_list).setOnTouchListener(new OnSwipeTouchListener(this) {

			@Override
			public void onSwipeLeft() { StaticData.assistantInhibitClose = true; }

			@Override
			public void onSwipeRight() { StaticData.assistantInhibitClose = true; }

		});

	}

	private void setupDrawer() {

		final DrawerLayout drawer = findViewById(R.id.drawer_layout);
		final NavigationView navView = findViewById(R.id.nav_view);

		mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_assistant, R.id.nav_settings)
						.setDrawerLayout(drawer)
						.build();

		NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
		NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
		NavigationUI.setupWithNavController(navView, navController);

		navView.setNavigationItemSelectedListener(menuItem -> {

			drawer.closeDrawers();

			switch (menuItem.getItemId()) {

				case R.id.nav_assistant:
					expandAssistant();
					break;

				case R.id.nav_settings:
					startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
					break;

				case R.id.nav_setup:
					startActivity(new Intent(HomeActivity.this, SetupWizardActivity.class));
					break;

				case R.id.nav_log:
					startActivity(new Intent(HomeActivity.this, LogActivity.class));
					break;

				case R.id.nav_help_guide:
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(StaticData.HANDBOOK_URL)));
					break;

				case R.id.nav_help_contact_us:
					startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse(StaticData.CONTACT_MAIL)));
					break;

			}

			return true;

		});

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
	public boolean onSupportNavigateUp() {
		NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
		return NavigationUI.navigateUp(navController, mAppBarConfiguration)
						|| super.onSupportNavigateUp();
	}

	public static HomeActivity getInstance() {

		return instance.get();

	}

}

/* Class from https://gist.github.com/nesquena/ed58f34791da00da9751 under MIT license */

class OnSwipeTouchListener implements View.OnTouchListener {

	private GestureDetector gestureDetector;

	public OnSwipeTouchListener(Context c) {
		gestureDetector = new GestureDetector(c, new GestureListener());
	}

	public boolean onTouch(final View view, final MotionEvent motionEvent) {
		return gestureDetector.onTouchEvent(motionEvent);
	}

	private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

		private static final int SWIPE_THRESHOLD = 100;
		private static final int SWIPE_VELOCITY_THRESHOLD = 100;

		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}

		// Determines the fling velocity and then fires the appropriate swipe event accordingly
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			boolean result = false;
			try {
				float diffY = e2.getY() - e1.getY();
				float diffX = e2.getX() - e1.getX();
				if (Math.abs(diffX) > Math.abs(diffY)) {
					if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
						if (diffX > 0) {
							onSwipeRight();
						} else {
							onSwipeLeft();
						}
					}
				} else {
					if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
						if (diffY > 0) {
							onSwipeDown();
						} else {
							onSwipeUp();
						}
					}
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
			return result;
		}
	}

	public void onSwipeRight() {
	}

	public void onSwipeLeft() {
	}

	public void onSwipeUp() {
	}

	public void onSwipeDown() {
	}
}