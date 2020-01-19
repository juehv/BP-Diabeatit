package info.nightscout.androidaps.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

import info.nightscout.androidaps.R;

public class BolusCalculatorFragment extends Fragment implements View.OnClickListener{
    BolusCalculatorViewModel viewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewModel = ViewModelProviders.of(this).get(BolusCalculatorViewModel.class);
        View root = inflater.inflate(R.layout.fragment_bolus_calculator, container, false);
        final TextView textView = root.findViewById(R.id.text_home);

        final Button buttonMoreValues = root.findViewById(R.id.button_more_values);
        buttonMoreValues.setOnClickListener(this);

        final Button buttonExplanation = root.findViewById(R.id.button_bolus_explanation);
        buttonExplanation.setOnClickListener(this);

        return root;
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
