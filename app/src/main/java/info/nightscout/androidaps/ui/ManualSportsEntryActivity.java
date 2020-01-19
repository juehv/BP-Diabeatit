package info.nightscout.androidaps.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.DateFormat;
import java.time.Instant;
import java.util.Calendar;

import info.nightscout.androidaps.R;

public class ManualSportsEntryActivity extends AppCompatActivity {
    EditText notes;
    ToggleButton buttonLight;
    ToggleButton buttonMedium;
    ToggleButton buttonHeavy;
    Button timestampStartButton;
    Button timestampEndButton;
    Button enterButton;

    Calendar start;
    Calendar end;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_sports_entry);

        resetStart();
        resetEnd();

        notes = findViewById(R.id.edit_text_notes);
        buttonLight = findViewById(R.id.button_light);
        buttonMedium = findViewById(R.id.button_medium);
        buttonHeavy = findViewById(R.id.button_heavy);
        enterButton = findViewById(R.id.button_enter);
        timestampStartButton = findViewById(R.id.btn_timestamp_start);
        timestampEndButton = findViewById(R.id.btn_timestamp_end);

        buttonLight.setOnCheckedChangeListener((v, c) -> buttonLightOnCheckChanged(c));
        buttonMedium.setOnCheckedChangeListener((v, c) -> buttonMediumOnCheckChanged(c));
        buttonHeavy.setOnCheckedChangeListener((v, c) -> buttonHeavyOnCheckChanged(c));
        enterButton.setOnClickListener(v -> enterButtonOnClick());
        timestampStartButton.setOnClickListener(v -> selectTimestampStart());
        timestampEndButton.setOnClickListener(v -> selectTimestampEnd());

        buttonMedium.setChecked(true);

        updateTexts();
    }

    private void resetStart() {
        start = new Calendar.Builder()
                .setInstant(Instant.now().toEpochMilli())
                .setTimeZone(Calendar.getInstance().getTimeZone())
                .build();
    }

    private void resetEnd() {
        end = new Calendar.Builder()
                .setInstant(Instant.now().toEpochMilli())
                .setTimeZone(Calendar.getInstance().getTimeZone())
                .build();
    }

    private void updateTexts() {
        String s = DateFormat.getDateTimeInstance().format(start.getTime());
        String e = DateFormat.getDateTimeInstance().format(end.getTime());


        String startButtonHtml = String.format("<b>Start of activity</b><br /><small>%s</small>", s);
        String endButtonHtml = String.format("<b>End of activity</b><br /><small>%s</small>", e);

        timestampStartButton.setText(Html.fromHtml(startButtonHtml));
        timestampEndButton.setText(Html.fromHtml(endButtonHtml));
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

    private void selectTimestampStart() {
        selectDate(start, () -> selectTime(start, this::updateTexts));
    }

    private void selectTimestampEnd() {
        selectDate(end, () -> selectTime(end, this::updateTexts));
    }

    private void selectDate(Calendar cal, Runnable finished) {
        DatePickerDialog diag = new DatePickerDialog(this,
                (v, y, m, d) -> {
                    cal.set(Calendar.YEAR, y);
                    cal.set(Calendar.MONTH, m);
                    cal.set(Calendar.DAY_OF_MONTH, d);
                    if (finished != null) {
                        finished.run();
                    }
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH));
        diag.show();
    }

    private void selectTime(Calendar cal, Runnable finished) {
        TimePickerDialog diag = new TimePickerDialog(this,
                (v, h, m) -> {
                    cal.set(Calendar.HOUR_OF_DAY, h);
                    cal.set(Calendar.MINUTE, m);
                    if (finished != null) {
                        finished.run();
                    }
                },
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true);
        diag.show();
    }

    private void enterButtonOnClick() {
        /* TODO */
    }
}
