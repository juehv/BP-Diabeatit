package de.tu_darmstadt.informatik.tk.diabeatit;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.ImageButton;

import de.tu_darmstadt.informatik.tk.diabeatit.ui.ManualCarbsEntryActivity;
import de.tu_darmstadt.informatik.tk.diabeatit.ui.ManualInsulinEntryActivity;
import de.tu_darmstadt.informatik.tk.diabeatit.ui.ManualSportsEntryActivity;
import de.tu_darmstadt.informatik.tk.diabeatit.ui.setup.SetupActivity;

public class HomeActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private FloatingActionsMenu entryMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // FAB

        entryMenu = (FloatingActionsMenu) findViewById(R.id.manual_entry_fab_menu);
        FloatingActionButton manualInsulinButton = (FloatingActionButton) findViewById(R.id.fab_manual_insulin);
        FloatingActionButton manualCarbsButton = (FloatingActionButton) findViewById(R.id.fab_manual_carbs);
        FloatingActionButton manualSportsButton = (FloatingActionButton) findViewById(R.id.fab_manual_sports);

        // set the onClickListeners, so we change the Activity on click.
        manualInsulinButton.setOnClickListener((v) ->
                startActivity(new Intent(HomeActivity.this, ManualInsulinEntryActivity.class)));
        manualCarbsButton.setOnClickListener((v) ->
                startActivity(new Intent(HomeActivity.this, ManualCarbsEntryActivity.class)));
        manualSportsButton.setOnClickListener((v) ->
                startActivity(new Intent(HomeActivity.this, ManualSportsEntryActivity.class)));

        // Assistant

        View nestedScrollView = (View) findViewById(R.id.assistant_scrollview);
        final BottomSheetBehavior assistant = BottomSheetBehavior.from(nestedScrollView);
        final ImageButton assistant_slide = (ImageButton) findViewById(R.id.assistant_slide_button);

        assistant.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(View bottomSheet, int newState) {

                entryMenu.setVisibility(newState == BottomSheetBehavior.STATE_COLLAPSED ? View.VISIBLE : View.GONE);
                entryMenu.collapseImmediately();

                if (newState == BottomSheetBehavior.STATE_COLLAPSED)
                    assistant_slide.setImageDrawable(assistant_slide.getContext().getDrawable(R.drawable.ic_slideup));

                if (newState == BottomSheetBehavior.STATE_EXPANDED)
                    assistant_slide.setImageDrawable(assistant_slide.getContext().getDrawable(R.drawable.ic_slidedown));

            }

            @Override
            public void onSlide(View view, float v) {}
        });

        ImageButton slide_btn = (ImageButton) findViewById(R.id.assistant_slide_button);

        slide_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                assistant.setState(assistant.getState() == BottomSheetBehavior.STATE_EXPANDED ? BottomSheetBehavior.STATE_COLLAPSED : BottomSheetBehavior.STATE_EXPANDED);

            }
        });

        // Drawer

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        final NavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_settings, R.id.nav_device_sensor, R.id.nav_device_pump, R.id.nav_device_tracker)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                if (menuItem.getItemId() == R.id.nav_settings)
                    startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
                else if (menuItem.getItemId() == R.id.nav_setup)
                    startActivity(new Intent(HomeActivity.this, SetupActivity.class));
                else if (menuItem.getItemId() == R.id.nav_home)
                    drawer.closeDrawers();
                return true;
            }
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
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}