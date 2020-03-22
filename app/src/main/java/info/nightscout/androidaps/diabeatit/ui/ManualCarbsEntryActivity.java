package info.nightscout.androidaps.diabeatit.ui;

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

import java.text.DateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;

import info.nightscout.androidaps.R;
import info.nightscout.androidaps.diabeatit.ui.home.HomeFragment;
import info.nightscout.androidaps.plugins.treatments.CarbsGenerator;

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
                android.R.style.Theme_DeviceDefault_Light_Dialog_Alert,
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
                android.R.style.Theme_DeviceDefault_Light_Dialog_Alert,
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
        int carbs;
        String notes;
        long timestamp;

        try {
            Double c = Double.parseDouble(carbsText.getText().toString());
            carbs = c.intValue();
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return;
            // TODO: Handle this properly!
        }
        notes = this.notes.getText().toString();
        timestamp = this.timestamp.toInstant().toEpochMilli();

        CarbsGenerator.createCarb(carbs, timestamp, "ManualCarbsActivity", notes);

        // Update GUI
        HomeFragment fragment = HomeFragment.getInstance();
        if (fragment != null) {
            fragment.scheduleUpdateGUI(this.getClass().getCanonicalName());
        }

        finish();
    }
}
