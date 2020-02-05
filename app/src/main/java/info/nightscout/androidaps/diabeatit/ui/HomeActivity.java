package info.nightscout.androidaps.diabeatit.ui;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import info.nightscout.androidaps.diabeatit.ui.setup.SetupActivity;
import info.nightscout.androidaps.R;
import info.nightscout.androidaps.diabeatit.assistant.alert.Alert;
import info.nightscout.androidaps.diabeatit.assistant.alert.AlertStore;
import info.nightscout.androidaps.diabeatit.assistant.alert.AlertStoreListener;
import info.nightscout.androidaps.diabeatit.assistant.alert.AlertsManager;

public class HomeActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private FloatingActionsMenu entryMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupManualEntry();
        setupAssistant();
        setupDrawer();

    }

    private void setupManualEntry() {

        entryMenu = findViewById(R.id.manual_entry_fab_menu);
        FloatingActionButton manualInsulinButton = findViewById(R.id.fab_manual_insulin);
        FloatingActionButton manualCarbsButton = findViewById(R.id.fab_manual_carbs);
        FloatingActionButton manualSportsButton = findViewById(R.id.fab_manual_sports);

        manualInsulinButton.setOnClickListener((v) ->
                startActivity(new Intent(HomeActivity.this, ManualInsulinEntryActivity.class)));
        manualCarbsButton.setOnClickListener((v) ->
                startActivity(new Intent(HomeActivity.this, ManualCarbsEntryActivity.class)));
        manualSportsButton.setOnClickListener((v) ->
                startActivity(new Intent(HomeActivity.this, ManualSportsEntryActivity.class)));

    }

    private void setupAssistant() {

        View nestedScrollView = findViewById(R.id.assistant_scrollview);
        final BottomSheetBehavior assistant = BottomSheetBehavior.from(nestedScrollView);
        final RelativeLayout assistantPeek = findViewById(R.id.assistant_peek);
        final RelativeLayout assistantPeekAlt = findViewById(R.id.assistant_peek_alt);

        assistant.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NotNull View bottomSheet, int newState) {

                entryMenu.setVisibility(newState == BottomSheetBehavior.STATE_COLLAPSED ? View.VISIBLE : View.GONE);
                entryMenu.collapseImmediately();

                assistantPeek.setVisibility(newState == BottomSheetBehavior.STATE_COLLAPSED ? View.VISIBLE : View.GONE);
                assistantPeekAlt.setVisibility(newState != BottomSheetBehavior.STATE_COLLAPSED ? View.VISIBLE : View.GONE);

            }

            @Override
            public void onSlide(@NotNull View view, float v) {}
        });

        assistantPeek.setOnClickListener(view -> assistant.setState(BottomSheetBehavior.STATE_EXPANDED));

        AlertStore.activeAlerts = new AlertsManager(getApplicationContext(), findViewById(R.id.assistant_card_list), findViewById(R.id.alert_cardview));

        Button alertClearB = findViewById(R.id.alert_clear_all);
        TextView alertEmptyT = findViewById(R.id.alert_empty_notice);

        alertClearB.setOnClickListener(view -> AlertStore.clearAlerts());

        AlertStore.attachListener(new AlertStoreListener() {

            @Override
            public void onNewAlert(Alert alert) {

                alertClearB.setVisibility(View.VISIBLE);
                alertEmptyT.setVisibility(View.GONE);

            }

            @Override
            public void onAlertDismissed(Alert alert) {}

            @Override
            public void onAlertRestored(Alert alert) {

                onNewAlert(alert);

            }

            @Override
            public void onAlertsCleared() {

                alertClearB.setVisibility(View.GONE);
                alertEmptyT.setVisibility(View.VISIBLE);

            }

            @Override
            public void onDataSetInit() {

                int len = AlertStore.getActiveAlerts().length;
                alertClearB.setVisibility(len == 0 ? View.GONE : View.VISIBLE);
                alertEmptyT.setVisibility(len == 0 ? View.VISIBLE : View.GONE);

            }

        });

        CardView alertSettingsC = findViewById(R.id.alert_settings);
        // TODO Open relevant settings page

        CardView alertHistoryC = findViewById(R.id.alert_history);
        alertHistoryC.setOnClickListener(view -> startActivity(new Intent(HomeActivity.this, AlertHistoryActivity.class)));

        // TODO Example alerts - Remove
        List<Alert> as = new ArrayList<>();
        as.add(new Alert(Alert.Urgency.URGENT, getDrawable(R.drawable.ic_battery_alert), "Battery low", "The battery is low."));
        as.add(new Alert(Alert.Urgency.INFO, getDrawable(R.drawable.ic_timeline), "Lorem Ipsum", "Lorem Ipsum!"));
        as.add(new Alert(Alert.Urgency.WARNING, getDrawable(R.drawable.ic_bluetooth_disabled), "Multiline", "Line<br>Break"));
        AlertStore.initAlerts(as.toArray(new Alert[0]));

        // TODO Testing - Remove
        findViewById(R.id.alert_settings).setOnClickListener(
                view -> AlertStore.newAlert(new Alert(Alert.Urgency.URGENT, getDrawable(R.drawable.ic_face_sad), "BG Alert", "Blood glucose level critical!"))
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
                startActivity(new Intent(HomeActivity.this, SetupActivity.class));
            else if (menuItem.getItemId() == R.id.nav_home)
                drawer.closeDrawers();
            return true;

        });

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