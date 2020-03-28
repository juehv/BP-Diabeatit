package info.nightscout.androidaps.diabeatit.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;

import info.nightscout.androidaps.R;
import info.nightscout.androidaps.diabeatit.StaticData;
import info.nightscout.androidaps.diabeatit.predictions.PredictionsPlugin;
import info.nightscout.androidaps.diabeatit.util.FileDownloader;
import info.nightscout.androidaps.utils.SP;

public class DownloadModelActivity extends AppCompatActivity {
    private static Logger log = LoggerFactory.getLogger(DownloadModelActivity.class);

    private TextView link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.d_activity_download_model);

        link = findViewById(R.id.tv_download_link);

        Intent i = getIntent();

        if (Intent.ACTION_VIEW.equals(i.getAction())) {
            Uri uri = i.getData();
            if (uri == null) {
                log.error("Did not receive Intent with URI");
                finish();
                return;
            }

            final Context ctx = this;
            final Toast beginToast = Toast.makeText(ctx, getString(R.string.download_model_start), Toast.LENGTH_SHORT);
            beginToast.show();

            Uri u = uri.buildUpon().scheme("https").build();

            link.setText(u.toString());
            log.info(String.format("Downloading model from '%s'", u));
            FileDownloader.download(HomeActivity.getInstance(), u.toString(), null, new FileDownloader.DownloadCallback() {
                @Override
                public void onDownloadCompleted(String filePath) {

                    SP.putString(PredictionsPlugin.PREF_KEY_AI_MODEL_PATH, filePath);
                    SP.putString(PredictionsPlugin.PREF_KEY_MODEL_TYPE, PredictionsPlugin.MODEL_TYPE_AI);
                    PredictionsPlugin.updateFromSettings();

                    beginToast.cancel();
                    Toast.makeText(ctx, getString(R.string.download_model_finished), Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onDownloadFailed(Exception error) {
                    log.warn("Failed to download model", error);

                    beginToast.cancel();

                    HomeActivity a = HomeActivity.getInstance();
                    if (a == null) Toast.makeText(ctx, getString(R.string.download_model_failed), Toast.LENGTH_LONG).show();
                    else {
                        Snackbar sb = Snackbar.make(a.findViewById(android.R.id.content), getString(R.string.download_model_failed), Snackbar.LENGTH_LONG);
                        sb.setAction(getString(R.string.download_model_failed_action), view -> startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse(String.format(StaticData.ERROR_MAIL, URLEncoder.encode(error.getMessage()))))));
                        sb.show();
                    }
                }
            });

            finish();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
