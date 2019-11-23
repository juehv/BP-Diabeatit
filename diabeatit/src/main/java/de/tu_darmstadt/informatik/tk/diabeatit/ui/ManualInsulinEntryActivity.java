package de.tu_darmstadt.informatik.tk.diabeatit.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.IntSummaryStatistics;

import de.tu_darmstadt.informatik.tk.diabeatit.R;
import de.tu_darmstadt.informatik.tk.diabeatit.data.BolusSources.ManualBolusSource;

public class ManualInsulinEntryActivity extends AppCompatActivity {

    private Button enterButton;
    private Button setDateTimeButton;
    private TextView dateTimeText;
    private EditText amountEditText;
    private EditText notesText;

    private int year;
    private int month;
    private int dayOfMonth;
    private int hour;
    private int minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_insulin_entry);

        setDateTimeButton = findViewById(R.id.button_select_date);
        dateTimeText = findViewById(R.id.edit_text_date_time);
        enterButton = findViewById(R.id.button_enter);
        amountEditText = findViewById(R.id.edit_text_bolus);
        notesText = findViewById(R.id.edit_text_notes);

        setDateTimeButton.setOnClickListener(v -> setDateTimeButtonClick());
        enterButton.setOnClickListener(v -> enterButtonClick());

        resetToCurrentTime();
        updateTexts();
    }

    private void setDateTimeButtonClick() {
        DatePickerDialog diag = new DatePickerDialog(this,
                ((v, y, m, d) -> {
                    setDate(y, m, d);
                    selectTime();
                }),
                year, month, dayOfMonth);
        diag.show();
    }

    private void resetToCurrentTime() {
        Calendar cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        hour = cal.get(Calendar.HOUR);
        minute = cal.get(Calendar.MINUTE);
    }

    private void updateTexts() {
        Date d = new Date();
        d.setYear(year - 1900);
        d.setMonth(month);
        d.setDate(dayOfMonth);
        d.setHours(hour);
        d.setMinutes(minute);
        d.setSeconds(0);
        String s = DateFormat.getDateTimeInstance().format(d);
        dateTimeText.setText(s);
    }

    private void setDate(int year, int month, int dayOfMonth) {
        Log.d("UI", String.format("Got Date: %04d-%02d-%02d", year, month, dayOfMonth));

        this.year = year;
        this.month = month;
        this.dayOfMonth = dayOfMonth;
        updateTexts();
    }

    private void selectTime() {
          TimePickerDialog diag = new TimePickerDialog(this,
                (v, h, m) -> setTime(h, m),
                hour, minute, true);
        diag.show();
    }

    private void setTime(int hour, int minute) {
        Log.d("UI", String.format("Selected Time: %02d:%02d", hour, minute));

        this.hour = hour;
        this.minute = minute;
        updateTexts();
    }

    private void enterButtonClick() {
        try {
            Date d = new Date();
            d.setYear(year);
            d.setMonth(month);
            d.setDate(dayOfMonth);
            d.setHours(hour);
            d.setMinutes(minute);
            long timestamp = d.getTime();
            String amountString = amountEditText.getText().toString().trim();
            double amount = (double) Double.parseDouble(amountString); // TODO test
            String notes = notesText.getText().toString();

            Intent i = ManualBolusSource.createIntent(timestamp, amount, notes);
            getApplicationContext().sendBroadcast(i);
        }
        catch (Exception ex) {
            Toast t = Toast.makeText(this,
                    String.format("Exception: %s", ex.toString()),
                    Toast.LENGTH_LONG);
            t.show();
        }
    }
}
