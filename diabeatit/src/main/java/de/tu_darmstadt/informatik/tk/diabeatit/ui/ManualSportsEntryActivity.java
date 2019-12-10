package de.tu_darmstadt.informatik.tk.diabeatit.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.security.Key;
import java.sql.Time;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import de.tu_darmstadt.informatik.tk.diabeatit.R;
import de.tu_darmstadt.informatik.tk.diabeatit.data.SportsEntry;
import de.tu_darmstadt.informatik.tk.diabeatit.data.SportsSources.ManualSportsSource;

public class ManualSportsEntryActivity extends AppCompatActivity {

    EditText timestampFrom;
    EditText timestampUntil;
    EditText notes;
    ToggleButton buttonLight;
    ToggleButton buttonMedium;
    ToggleButton buttonHeavy;
    Button selectTimestampFrom;
    Button selectTimestampUntil;
    Button enterButton;

    int fromYear, fromMonth, fromDayOfMonth, fromHour, fromMinute;
    int untilYear, untilMonth, untilDayOfMonth, untilHour, untilMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_sports_entry);

        resetFromTimestamp();
        resetUntilTimestamp();

        timestampFrom = findViewById(R.id.edit_text_timestamp_from);
        timestampUntil = findViewById(R.id.edit_text_timestamp_until);
        notes = findViewById(R.id.edit_text_notes);
        buttonLight = findViewById(R.id.button_light);
        buttonMedium = findViewById(R.id.button_medium);
        buttonHeavy = findViewById(R.id.button_heavy);
        selectTimestampFrom = findViewById(R.id.button_select_from);
        selectTimestampUntil = findViewById(R.id.button_select_until);
        enterButton = findViewById(R.id.button_enter);

        buttonLight.setOnCheckedChangeListener((v, c) -> buttonLightOnCheckChanged(c));
        buttonMedium.setOnCheckedChangeListener((v, c) -> buttonMediumOnCheckChanged(c));
        buttonHeavy.setOnCheckedChangeListener((v, c) -> buttonHeavyOnCheckChanged(c));
        selectTimestampFrom.setOnClickListener(v -> selectTimestampFromOnClick());
        selectTimestampUntil.setOnClickListener(v -> selectTimestampUntilOnClick());
        enterButton.setOnClickListener(v -> enterButtonOnClick());

        buttonMedium.setChecked(true);

        updateTexts();
    }

    private void resetFromTimestamp() {
        Calendar cal = Calendar.getInstance();
        fromYear = cal.get(Calendar.YEAR);
        fromMonth = cal.get(Calendar.MONTH);
        fromDayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        fromHour = cal.get(Calendar.HOUR_OF_DAY);
        fromMinute = cal.get(Calendar.MINUTE);
    }

    private void resetUntilTimestamp() {
        Calendar cal = Calendar.getInstance();
        untilYear = cal.get(Calendar.YEAR);
        untilMonth = cal.get(Calendar.MONTH);
        untilDayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        untilHour = cal.get(Calendar.HOUR_OF_DAY);
        untilMinute = cal.get(Calendar.MINUTE);
    }

    private void updateTexts() {
        Date from = new Date();
        from.setYear(fromYear - 1900);
        from.setMonth(fromMonth);
        from.setDate(fromDayOfMonth);
        from.setHours(fromHour);
        from.setMinutes(fromMinute);
        from.setSeconds(0);
        String f = DateFormat.getDateTimeInstance().format(from);
        timestampFrom.setText(f);

        Date until = new Date();
        until.setYear(untilYear - 1900);
        until.setMonth(untilMonth);
        until.setDate(untilDayOfMonth);
        until.setHours(untilHour);
        until.setMinutes(untilMinute);
        until.setSeconds(0);
        String u = DateFormat.getDateTimeInstance().format(until);
        timestampUntil.setText(u);
    }

    private void buttonLightOnCheckChanged(boolean isChecked) {
        if (isChecked) {
            // only let one button be checked
            buttonMedium.setChecked(false);
            buttonHeavy.setChecked(false);
        } else {
            if (!buttonMedium.isChecked() && !buttonHeavy.isChecked()) {
                // at least one button needs to be checked
                buttonLight.setChecked(true);
            }
        }
    }

    private void buttonMediumOnCheckChanged(boolean isChecked) {
        if (isChecked) {
            // only let one button at once be checked
            buttonLight.setChecked(false);
            buttonHeavy.setChecked(false);
        } else {
            // at least one button needs to be checked
            if (!buttonLight.isChecked() && !buttonHeavy.isChecked()) {
                buttonMedium.setChecked(true);
            }
        }
    }

    private void buttonHeavyOnCheckChanged(boolean isChecked) {
        if (isChecked) {
            // only one button can be checked
            buttonLight.setChecked(false);
            buttonMedium.setChecked(false);
        } else {
            // at least one button needs to be checked
            if (!buttonLight.isChecked() && !buttonMedium.isChecked()) {
                buttonHeavy.setChecked(true);
            }
        }
    }

    private void selectTimestampFromOnClick() {
        DatePickerDialog picker = new DatePickerDialog(
                this,
                (v, y, m, d) -> {
                    fromYear = y;
                    fromMonth = m;
                    fromDayOfMonth = d;
                    updateTexts();
                    pickFromTime();
                },
                fromYear, fromMonth, fromDayOfMonth);
        picker.show();
    }

    private void pickFromTime() {
        TimePickerDialog picker = new TimePickerDialog(this,
                (v, h, m) -> {
                    fromHour = h;
                    fromMinute = m;
                    updateTexts();
                },
                fromHour, fromMinute, true);
        picker.show();
    }

    private void selectTimestampUntilOnClick() {
        DatePickerDialog picker = new DatePickerDialog(this,
                (v, y, m, d) -> {
                    untilYear = y;
                    untilMonth = m;
                    untilDayOfMonth = d;
                    updateTexts();
                    pickUntilTime();
                },
                untilYear, untilMonth, untilDayOfMonth);
        picker.show();
    }

    private void pickUntilTime() {
        TimePickerDialog picker = new TimePickerDialog(this,
                (v, h, m) -> {
                    untilHour = h;
                    untilMinute = m;
                    updateTexts();
                },
                untilHour, untilMinute, true);
        picker.show();
    }

    private void enterButtonOnClick() {
        try {
            Calendar from = new Calendar.Builder()
                    .setFields(
                            Calendar.YEAR, fromYear,
                            Calendar.MONTH, fromMonth,
                            Calendar.DAY_OF_MONTH, fromDayOfMonth,
                            Calendar.HOUR_OF_DAY, fromHour,
                            Calendar.MINUTE, fromMinute
                    )
                    .setTimeZone(Calendar.getInstance().getTimeZone())
                    .build();
            long tsFrom = from.toInstant().toEpochMilli();

            Calendar until = new Calendar.Builder()
                    .setFields(
                            Calendar.YEAR, untilYear,
                            Calendar.MONTH, untilMonth,
                            Calendar.DAY_OF_MONTH, untilDayOfMonth,
                            Calendar.HOUR_OF_DAY, untilHour,
                            Calendar.MINUTE, untilMinute)
                    .setTimeZone(Calendar.getInstance().getTimeZone())
                    .build();
            long tsUntil = until.toInstant().toEpochMilli();

            SportsEntry.Level level;

            if (buttonLight.isChecked()) {
                level = SportsEntry.Level.LOW;
            } else if (buttonMedium.isChecked()) {
                level = SportsEntry.Level.MEDIUM;
            } else if (buttonHeavy.isChecked()) {
                level = SportsEntry.Level.HIGH;
            } else {
                throw new UnsupportedOperationException();
            }

            String notes = this.notes.getText().toString().trim();

            Intent i = ManualSportsSource.createProperIntent(tsFrom, tsUntil, level.toInt(), notes);

            getApplicationContext().sendBroadcast(i);
            finish();
        } catch (Exception ex) {
            Toast t = Toast.makeText(this, String.format("Unhandled Exception: %s", ex.toString()), Toast.LENGTH_LONG);
            t.show();
        }
    }
}