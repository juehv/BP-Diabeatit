package info.nightscout.androidaps.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.DialogFragment;

import info.nightscout.androidaps.R;


public class BolusCalculatorMoreValuesDialog  extends DialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.dialog_bolus_calculator_more_values, container, false);
        getDialog().setTitle("test");
        final Button buttonSave = root.findViewById(R.id.button_ok);
        buttonSave.setOnClickListener(this::onClickSave);

        final Button buttonCancel = root.findViewById(R.id.button_cancel);
        buttonCancel.setOnClickListener(this::onClickCancel);

        return root;
    }

    public void onClickSave(View v) {
        dismiss();
    }

    public void onClickCancel(View v) {
        dismiss();
    }
}
