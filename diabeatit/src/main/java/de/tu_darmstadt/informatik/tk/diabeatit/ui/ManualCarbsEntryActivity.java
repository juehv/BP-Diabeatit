package de.tu_darmstadt.informatik.tk.diabeatit.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.text.DateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;

import de.tu_darmstadt.informatik.tk.diabeatit.R;
import de.tu_darmstadt.informatik.tk.diabeatit.data.CarbsSources.ManualCarbsSource;

public class ManualCarbsEntryActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;


    private Button enterButton;
    private EditText carbsText;
    private EditText notes;
    private Button selectTimestampButton;
    private Button selectPictureButton;
    private ImageView imagePreview;

    private Calendar timestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_carbs_entry);

        enterButton = findViewById(R.id.button_enter);
        carbsText = findViewById(R.id.edit_text_carbs);
        notes = findViewById(R.id.edit_text_notes);
        selectTimestampButton = findViewById(R.id.btn_timestamp);
        selectPictureButton = findViewById(R.id.btn_take_picture);
        imagePreview = findViewById(R.id.iv_picture);

        selectTimestampButton.setOnClickListener(v -> selectTimestampButtonOnClick());
        selectPictureButton.setOnClickListener(v -> selectPictureOnClick());
        enterButton.setOnClickListener(v -> enterButtonOnClick());

        resetDate();
        updateText();
    }

    private void selectPictureOnClick() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } else {
            Log.d("UI", "Could not take picture");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE:
                onImageCaptureResult(resultCode, data);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);


        }
    }

    private void onImageCaptureResult(int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imagePreview.setImageBitmap(imageBitmap);
        }
    }

    private void resetDate() {
        timestamp = new Calendar.Builder()
                .setInstant(Instant.now().toEpochMilli())
                .setTimeZone(Calendar.getInstance().getTimeZone())
                .build();
    }

    private void updateText() {
        Date ts = timestamp.getTime();
        String timestamp = DateFormat.getDateTimeInstance().format(ts);
        String btnHtml = String.format("<b>Date and Time</b><br /><small>%s</small>", timestamp);
        selectTimestampButton.setText(Html.fromHtml(btnHtml));
    }

    private void selectTimestampButtonOnClick() {
        DatePickerDialog diag = new DatePickerDialog(this,
                (v, y, m, d) -> {
                    timestamp.set(Calendar.YEAR, y);
                    timestamp.set(Calendar.MONTH, m);
                    timestamp.set(Calendar.DAY_OF_MONTH, d);
                    selectTime();
                },
                timestamp.get(Calendar.YEAR),
                timestamp.get(Calendar.MONTH),
                timestamp.get(Calendar.DAY_OF_MONTH));
        diag.show();
    }

    private void selectTime() {
        TimePickerDialog diag = new TimePickerDialog(this,
                (v, h, m) -> {
                    timestamp.set(Calendar.HOUR, h);
                    timestamp.set(Calendar.MINUTE, m);
                    updateText();
                },
                timestamp.get(Calendar.HOUR),
                timestamp.get(Calendar.MINUTE),
                true);
        diag.show();
    }

    private void enterButtonOnClick() {
        try {
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
