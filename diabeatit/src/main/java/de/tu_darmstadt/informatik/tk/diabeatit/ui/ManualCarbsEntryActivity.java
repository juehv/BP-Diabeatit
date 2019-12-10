package de.tu_darmstadt.informatik.tk.diabeatit.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import de.tu_darmstadt.informatik.tk.diabeatit.R;
import de.tu_darmstadt.informatik.tk.diabeatit.data.CarbsSources.ManualCarbsSource;

public class ManualCarbsEntryActivity extends AppCompatActivity {

    private Button enterButton;
    private Button selectTimestampButton;
    private EditText timestampText;
    private EditText carbsText;
    private EditText notes;

    private int year, month, dayOfMonth, hour, minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_carbs_entry);

        enterButton = findViewById(R.id.button_enter);
        selectTimestampButton = findViewById(R.id.button_select_timestamp);
        timestampText = findViewById(R.id.edit_text_timestamp);
        carbsText = findViewById(R.id.edit_text_carbs);
        notes = findViewById(R.id.edit_text_notes);

        enterButton.setOnClickListener(v -> enterButtonOnClick());
        selectTimestampButton.setOnClickListener(v -> selectTimestampButtonOnClick());

        resetDate();
        updateText();
    }

    private void resetDate() {
        Calendar c = Calendar.getInstance();

        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);
    }

    private void updateText() {
        Date d = new Date();

        d.setYear(year);
        d.setMonth(month);
        d.setDate(dayOfMonth);
        d.setHours(hour);
        d.setMinutes(minute);
        d.setSeconds(0);

        String date = DateFormat.getDateTimeInstance().format(d);
        timestampText.setText(date);
    }

    private void selectTimestampButtonOnClick() {
        DatePickerDialog diag = new DatePickerDialog(this,
                (v, y, m, d) -> {
                    year = y;
                    month = m;
                    dayOfMonth = d;
                    selectTime();
                }, year, month, dayOfMonth);
        diag.show();
    }

    private void selectTime() {
        TimePickerDialog diag = new TimePickerDialog(this,
                (v, h, m) -> {
                    hour = h;
                    month = m;
                    updateText();
                }, hour, month, true);
        diag.show();
    }

    private void enterButtonOnClick() {
        try {
            Calendar timestamp = new Calendar.Builder()
                    .set(Calendar.YEAR, year)
                    .set(Calendar.MONTH, month)
                    .set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    .set(Calendar.HOUR_OF_DAY, hour)
                    .set(Calendar.MINUTE, minute)
                    .setTimeZone(Calendar.getInstance().getTimeZone())
                    .build();
            double carbs = Double.parseDouble(carbsText.getText().toString());

            Intent i = ManualCarbsSource.createIntent(timestamp.toInstant().toEpochMilli(), carbs);
            getApplicationContext().sendBroadcast(i);
            finish();
        } catch (Exception ex) {
            Toast t = Toast.makeText(this, String.format("Unhandled exception %s", ex.toString()), Toast.LENGTH_LONG);
            t.show();
        }
    }
}
