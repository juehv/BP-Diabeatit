package info.nightscout.androidaps.diabeatit.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

import java.util.Set;

import info.nightscout.androidaps.R;
import info.nightscout.androidaps.data.Profile;
import info.nightscout.androidaps.db.BgReading;
import info.nightscout.androidaps.db.DatabaseHelper;
import info.nightscout.androidaps.plugins.configBuilder.ProfileFunctions;
import info.nightscout.androidaps.plugins.insulin.BolusCalculator;
import info.nightscout.androidaps.plugins.insulin.BolusCalculatorBuilder;
import info.nightscout.androidaps.setupwizard.SetupWizardActivity;

public class BolusCalculatorFragment extends Fragment implements View.OnClickListener{
    BolusCalculatorViewModel viewModel;
    BolusCalculator calc;

    TextView bolusText;
    EditText carbs;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewModel = ViewModelProviders.of(this).get(BolusCalculatorViewModel.class);
        View root = inflater.inflate(R.layout.fragment_bolus_calculator, container, false);
        //final TextView textView = root.findViewById(R.id.text_home);

        // we need to get a profile
        if (ProfileFunctions.getInstance().getProfile() == null) {
            Intent i = new Intent(getContext(), SetupWizardActivity.class);
            startActivity(i);
            return root;
        }

        final Button buttonMoreValues = root.findViewById(R.id.button_more_values);
        buttonMoreValues.setOnClickListener(this);

        final Button buttonExplanation = root.findViewById(R.id.button_bolus_explanation);
        buttonExplanation.setOnClickListener(this);

        bolusText = root.findViewById(R.id.textView_bolus);
        carbs = root.findViewById(R.id.editText_carbs);

        carbs.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                onCarbsChanged();

                return false;
            }
        });

        setupCalculator();

        updateText(calc);

        return root;
    }

    private void onCarbsChanged() {
        try {
            Double d = Double.parseDouble(carbs.getText().toString());
            calc.setCarbs(d.intValue());
        } catch (Exception e) {
            // ...
        }
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
        bolusText.setText(String.format("%f", src.getCalculatedTotalInsulin()));
    }

    @Override
    public void onClick(View v) {
        FragmentManager manager = getFragmentManager();
        switch(v.getId())
        {
            case R.id.button_more_values:
                new BolusCalculatorMoreValuesDialog().show(manager, "BolusCalculatorMoreValuesDialog");
                break;
            case R.id.button_bolus_explanation:
                new BolusCalculatorExplanationDialog().show(manager, "BolusCalculatorExplanationDialog");
                break;

        }
    }
}
