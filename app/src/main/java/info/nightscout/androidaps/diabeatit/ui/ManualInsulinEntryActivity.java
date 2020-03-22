package info.nightscout.androidaps.diabeatit.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.time.Instant;
import java.util.Calendar;

import info.nightscout.androidaps.R;
import info.nightscout.androidaps.data.DetailedBolusInfo;
import info.nightscout.androidaps.db.Source;
import info.nightscout.androidaps.diabeatit.ui.home.HomeFragment;
import info.nightscout.androidaps.plugins.treatments.TreatmentsPlugin;

public class ManualInsulinEntryActivity extends AppCompatActivity {

    private Button enterButton;
    private EditText amountEditText;
    private EditText notesText;

    Button timestampButton;

    Calendar timestamp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.d_activity_manual_insulin_entry);

        // dateTimeText = findViewById(R.id.edit_text_date_time);
        enterButton = findViewById(R.id.button_enter);
        amountEditText = findViewById(R.id.edit_text_bolus);
        notesText = findViewById(R.id.edit_text_notes);
        timestampButton = findViewById(R.id.btn_date_time);

        timestampButton.setOnClickListener(v -> setDateTimeButtonClick());
        enterButton.setOnClickListener(v -> enterButtonClick());

        resetToCurrentTime();
        updateTexts();
    }

    private void setDateTimeButtonClick() {
        DatePickerDialog diag = new DatePickerDialog(this,
                android.R.style.Theme_DeviceDefault_Light_Dialog_Alert,
                ((v, y, m, d) -> {
                    setDate(y, m, d);
                    selectTime();
                }),
                timestamp.get(Calendar.YEAR),
                timestamp.get(Calendar.MONTH),
                timestamp.get(Calendar.DAY_OF_MONTH));
        diag.show();
    }

    private void resetToCurrentTime() {
        timestamp = new Calendar.Builder()
                .setInstant(Instant.now().toEpochMilli())
                .setTimeZone(Calendar.getInstance().getTimeZone())
                .build();
    }

    private void updateTexts() {
        String s = DateFormat.getDateTimeInstance().format(timestamp.getTime());
        String buttonHtml = String.format("<b>Date and Time</b><br /><small>%s</small>", s);
        timestampButton.setText(Html.fromHtml(buttonHtml));

    }

    private void setDate(int year, int month, int dayOfMonth) {
        Log.d("UI", String.format("Got Date: %04d-%02d-%02d", year, month, dayOfMonth));

        timestamp.set(Calendar.YEAR, year);
        timestamp.set(Calendar.MONTH, month);
        timestamp.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        updateTexts();
    }

    private void selectTime() {
        TimePickerDialog diag = new TimePickerDialog(this,
            android.R.style.Theme_DeviceDefault_Light_Dialog_Alert,
            (v, h, m) -> setTime(h, m),
            timestamp.get(Calendar.HOUR_OF_DAY),
            timestamp.get(Calendar.MINUTE),
              true);

        diag.show();
    }

    private void setTime(int hour, int minute) {
        Log.d("UI", String.format("Selected Time: %02d:%02d", hour, minute));

        timestamp.set(Calendar.HOUR_OF_DAY, hour);
        timestamp.set(Calendar.MINUTE, minute);

        updateTexts();
    }

    private void enterButtonClick() {
        double amount;
        String notes;
        long timestamp;
        try {
            amount = Double.parseDouble(amountEditText.getText().toString());
            // TODO: Constraints!
        } catch (NumberFormatException e) {
            // TODO: Properly handle! Maybe set hint text.
            e.printStackTrace();
            return;
        }
        notes = notesText.getText().toString();
        timestamp = this.timestamp.toInstant().toEpochMilli();

        DetailedBolusInfo bolus = new DetailedBolusInfo();
        bolus.insulin = amount;
        bolus.carbs = 0; // XXX
        bolus.date = timestamp;
        bolus.context = this;
        bolus.source = Source.USER;

        TreatmentsPlugin.getPlugin().addToHistoryTreatment(bolus, true);

        HomeFragment homeFragment = HomeFragment.getInstance();
        if (homeFragment != null)
            homeFragment.scheduleUpdateGUI("ManualBolusEntry", 1000);

        finish();
    }
}
