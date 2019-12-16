package de.tu_darmstadt.informatik.tk.diabeatit.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.DialogFragment;

import de.tu_darmstadt.informatik.tk.diabeatit.R;

public class BolusCalculatorExplanationDialog extends DialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.dialog_bolus_explanation, container, false);
        final Button buttonCancel = root.findViewById(R.id.button_cancel);
        buttonCancel.setOnClickListener(this::onClickCancel);

        return root;
    }

    public void onClickCancel(View v) {
        dismiss();
    }
}
