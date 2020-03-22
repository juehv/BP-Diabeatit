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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.text.DateFormat;
import java.time.Instant;
import java.util.Calendar;

import info.nightscout.androidaps.R;

import static info.nightscout.androidaps.diabeatit.ui.ManualCarbsEntryActivity.REQUEST_IMAGE_CAPTURE;

public class ManualNoteActivity extends AppCompatActivity {
    private EditText notes;
    private ImageView picture;
    private Button takePicture;
    private Button selectTimestamp;
    private Button enter;

    private Calendar timestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_note);

        notes = findViewById(R.id.edit_text_notes);
        picture = findViewById(R.id.iv_picture);
        takePicture = findViewById(R.id.btn_take_picture);
        selectTimestamp = findViewById(R.id.btn_timestamp);
        enter = findViewById(R.id.button_enter);

        takePicture.setOnClickListener(this::onTakePictureClick);
        selectTimestamp.setOnClickListener(this::onSelectTimestampClick);
        enter.setOnClickListener(this::onEnterClick);

        resetTimestamp();
        updateTexts();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE:
                if (resultCode == RESULT_OK) {
                    assert data != null;
                    Bundle extras = data.getExtras();
                    Bitmap imgBitmap = (Bitmap) extras.get("data");
                    picture.setImageBitmap(imgBitmap);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private void resetTimestamp() {
        timestamp = new Calendar.Builder()
                .setInstant(Instant.now().toEpochMilli())
                .setTimeZone(Calendar.getInstance().getTimeZone())
                .build();
    }

    private void updateTexts() {
        String ts = DateFormat.getDateTimeInstance().format(timestamp.getTime());

        String buttonHtml = String.format("<b>Timestamp</b><br /><small>%s</small>", ts);
        selectTimestamp.setText(Html.fromHtml(buttonHtml));
    }

    private void onTakePictureClick(View sender) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } else {
            Log.w("UI", "Could not take picture");
        }
    }


    private void onSelectTimestampClick(View sender) {
        promptDate();
    }

    private void onEnterClick(View sender) {
        // TODO: enter the data tho
        finish();
    }

    private void promptDate() {
        DatePickerDialog diag = new DatePickerDialog(this,
                android.R.style.Theme_DeviceDefault_Light_Dialog_Alert,
                (v, y, m, d) -> {
                    timestamp.set(Calendar.YEAR, y);
                    timestamp.set(Calendar.MONTH, m);
                    timestamp.set(Calendar.DAY_OF_MONTH, d);
                    promptTime();
                },
                timestamp.get(Calendar.YEAR),
                timestamp.get(Calendar.MONTH),
                timestamp.get(Calendar.DAY_OF_MONTH));
        diag.show();
    }

    private void promptTime() {
        TimePickerDialog diag = new TimePickerDialog(this,
                android.R.style.Theme_DeviceDefault_Light_Dialog_Alert,
                (v, h, m) -> {
                    timestamp.set(Calendar.HOUR_OF_DAY, h);
                    timestamp.set(Calendar.MINUTE, m);
                    updateTexts();
                },
                timestamp.get(Calendar.HOUR_OF_DAY),
                timestamp.get(Calendar.MINUTE),
                true);
        diag.show();
    }
}
