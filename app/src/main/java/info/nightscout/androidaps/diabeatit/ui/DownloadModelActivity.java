package info.nightscout.androidaps.diabeatit.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.nightscout.androidaps.MainApp;
import info.nightscout.androidaps.R;
import info.nightscout.androidaps.diabeatit.predictions.PredictionsPlugin;
import info.nightscout.androidaps.diabeatit.util.FileDownloader;
import info.nightscout.androidaps.utils.SP;

public class DownloadModelActivity extends AppCompatActivity {
    private static Logger log = LoggerFactory.getLogger(DownloadModelActivity.class);

    private TextView link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_model);

        link = findViewById(R.id.tv_download_link);

        Intent i = getIntent();

        if (Intent.ACTION_VIEW.equals(i.getAction())) {
            Uri uri = i.getData();
            if (uri == null) {
                log.error("Did not receive Intent with URI");
                finish();
                return;
            }

            Uri u = uri.buildUpon().scheme("https").build();

            link.setText(u.toString());
            log.info(String.format("Downloading model from '%s'", u));
            FileDownloader.download(this, u.toString(), null, new FileDownloader.DownloadCallback() {
                @Override
                public void onDownloadCompleted(String filePath) {
                    SP.putString(PredictionsPlugin.PREF_KEY_KI_MODEL_PATH, filePath);
                    SP.putString(PredictionsPlugin.PREF_KEY_MODEL_TYPE, PredictionsPlugin.MODEL_TYPE_KI);
                    PredictionsPlugin.updateFromSettings();
                }

                @Override
                public void onDownloadFailed(Exception error) {
                    log.warn("Failed to download model", error);
                    // TODO: Not sure what to do here; Probably an alert
                }
            });
            finish();
        }
    }
}
