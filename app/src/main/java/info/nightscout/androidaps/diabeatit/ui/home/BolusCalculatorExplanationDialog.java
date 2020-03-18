package info.nightscout.androidaps.diabeatit.ui.home;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import info.nightscout.androidaps.R;
import info.nightscout.androidaps.plugins.insulin.BolusCalculator;

public class BolusCalculatorExplanationDialog extends DialogFragment {

    private BolusCalculator bc;

    public BolusCalculatorExplanationDialog(BolusCalculator bc) {

        this.bc = bc;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.dialog_bolus_explanation, container, false);
        root.findViewById(R.id.button_cancel).setOnClickListener(this::onClickCancel);

        TextView text = root.findViewById(R.id.textView_bolus_explanation);
        text.setText(Html.fromHtml(getString(R.string.bolus_explanation, bc.getInsulinFromTrend(), bc.getInsulinFromCarbs(), bc.getInsulinFromBG()), Html.FROM_HTML_MODE_COMPACT));

        return root;

    }

    private void onClickCancel(View v) {

        dismiss();

    }

}
