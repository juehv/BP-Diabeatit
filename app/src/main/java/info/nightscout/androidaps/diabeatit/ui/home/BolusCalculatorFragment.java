package info.nightscout.androidaps.diabeatit.ui.home;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

import java.util.ArrayList;
import java.util.List;

import info.nightscout.androidaps.MainActivity;
import info.nightscout.androidaps.MainApp;
import info.nightscout.androidaps.R;
import info.nightscout.androidaps.data.Profile;
import info.nightscout.androidaps.db.BgReading;
import info.nightscout.androidaps.db.DatabaseHelper;
import info.nightscout.androidaps.diabeatit.StaticData;
import info.nightscout.androidaps.diabeatit.assistant.alert.Alert;
import info.nightscout.androidaps.diabeatit.assistant.alert.AlertStore;
import info.nightscout.androidaps.diabeatit.ui.HomeActivity;
import info.nightscout.androidaps.diabeatit.ui.log.LogEventStore;
import info.nightscout.androidaps.diabeatit.ui.log.event.SportsEvent;
import info.nightscout.androidaps.interfaces.Constraint;
import info.nightscout.androidaps.plugins.configBuilder.ProfileFunctions;
import info.nightscout.androidaps.plugins.insulin.BolusCalculator;
import info.nightscout.androidaps.plugins.insulin.BolusCalculatorBuilder;
import info.nightscout.androidaps.setupwizard.SetupWizardActivity;

public class BolusCalculatorFragment extends Fragment implements View.OnClickListener{
    BolusCalculatorViewModel viewModel;
    BolusCalculator calc;

    TextView bolusText;
    TextView notes;
    EditText carbs;

    String last = "";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewModel = ViewModelProviders.of(this).get(BolusCalculatorViewModel.class);
        View root = inflater.inflate(R.layout.d_fragment_bolus_calculator, container, false);
        //final TextView textView = root.findViewById(R.id.text_home);

        // we need to get a profile
        if (ProfileFunctions.getInstance().getProfile() == null) {
            Intent i = new Intent(getContext(), SetupWizardActivity.class);
            startActivity(i);
            return root;
        }

        notes = root.findViewById(R.id.bc_limit_exceeded);

        /*
        final Button buttonMoreValues = root.findViewById(R.id.bc_button_more_values);
        buttonMoreValues.setOnClickListener(this);
        */

        final Button buttonExplanation = root.findViewById(R.id.bc_button_bolus_explanation);
        buttonExplanation.setOnClickListener(this);

        bolusText = root.findViewById(R.id.bc_bolus_result);
        carbs = root.findViewById(R.id.bc_carbs_input);

        final HomeFragment homeFragment = HomeFragment.getInstance();
        final HomeActivity homeActivity = HomeActivity.getInstance();
        final LinearLayout extraInput = root.findViewById(R.id.bc_extra_input_layout);

        root.getViewTreeObserver().addOnGlobalLayoutListener(() -> {

            onCarbsChanged();

            if (homeFragment == null || homeActivity == null) return;

            Rect temp = new Rect();
            root.getWindowVisibleDisplayFrame(temp);
            int screenHeight = root.getRootView().getHeight();

            if (temp.bottom < screenHeight * 0.85) {

                if (homeFragment.graph.getVisibility() == View.VISIBLE) {

                    homeFragment.graph.setVisibility(View.GONE);
                    homeActivity.assistantPeekEnveloped.setVisibility(View.GONE);
                    extraInput.setVisibility(View.VISIBLE);

                }

            } else if (homeFragment.graph.getVisibility() != View.VISIBLE) {

                homeFragment.graph.setVisibility(View.VISIBLE);
                homeActivity.assistantPeekEnveloped.setVisibility(View.VISIBLE);
                extraInput.setVisibility(View.GONE);

                carbs.clearFocus();

            }

        });

        carbs.setOnKeyListener((v, keyCode, event) -> {

            onCarbsChanged();
            return false;

        });

        setupCalculator();

        updateText(calc);

        return root;
    }

    private void onCarbsChanged() {

        if (carbs.getText().toString().equals(last))
            return;

        last = carbs.getText().toString();

        if (last.equals(StaticData.DEVELOPER_PIN)) {

            carbs.setText(R.string.developer_mode_opening);

            Intent intent = new Intent(getContext(), HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            startActivity(new Intent(getContext(), MainActivity.class));

        }

        if (last.equals(StaticData.DUMMYDATA_PIN)) {

            carbs.setText("");

            final long timespan = 6 * 60 * 60 * 1000;
            List<BgReading> data = ChartDataParser.getDummyData(System.currentTimeMillis() - timespan, System.currentTimeMillis());

            for (BgReading r : data)
                MainApp.getDbHelper().createIfNotExists(r, "DUMMY");

            HomeFragment frag = HomeFragment.getInstance();
            if (frag != null)
                frag.scheduleUpdateGUI("Dummy data added");

            List<Alert> as = new ArrayList<>();
            as.add(new Alert(Alert.Urgency.URGENT, R.drawable.ic_battery_alert, "Battery low", "The battery is low."));
            as.add(new Alert(Alert.Urgency.INFO, R.drawable.ic_timeline, "Lorem Ipsum", "Lorem Ipsum!"));
            as.add(new Alert(Alert.Urgency.WARNING, R.drawable.ic_bluetooth_disabled, "Multiline", "Line<br>Break"));
            if (AlertStore.getActiveAlerts().length == 0)
                AlertStore.initAlerts(as.toArray(new Alert[0]));

        }

        try {

            Double d = last.length() == 0 ? 0 : Double.parseDouble(last);
            calc.setCarbs(d.intValue());

        } catch (Exception e) {}

    }

    private void setupCalculator() {
        Profile p = ProfileFunctions.getInstance().getProfile();
        BgReading lastBg = DatabaseHelper.lastBg();

        double lastBgVal;

        if (lastBg == null) {
            lastBgVal = 100;
        } else {
            lastBgVal = lastBg.value;
        }

        BolusCalculatorBuilder b = new BolusCalculatorBuilder();
        b.setCarbs(0);
        b.setBG(lastBgVal);
        b.setCorrection(0);
        b.setProfile(p);

        calc = b.build();
        calc.setOnCalculatedListener(this::updateText);
    }

    private void updateText(BolusCalculator src) {
        final double EPSILON = 0.2;
        double unconstrainedValue = src.getCalculatedTotalInsulin();
        double constrainedValue = MainApp.getConstraintChecker()
                .applyBolusConstraints(new Constraint<>(unconstrainedValue))
                .value();

        if (Math.abs(constrainedValue - unconstrainedValue) >= EPSILON) {
            notes.setVisibility(View.VISIBLE);
        } else {
            notes.setVisibility(View.GONE);
        }

        bolusText.setText(String.format("%.2f IE", constrainedValue));
    }

    @Override
    public void onClick(View v) {
        FragmentManager manager = getFragmentManager();
        if (v.getId() == R.id.bc_button_bolus_explanation)
            new BolusCalculatorExplanationDialog(calc).show(manager, "BolusCalculatorExplanationDialog");
    }
}
