package info.nightscout.androidaps.diabeatit.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.TextView;

import info.nightscout.androidaps.R;

public class DownloadModelActivity extends AppCompatActivity {

    private TextView link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_model);

        link = findViewById(R.id.tv_download_link);

        Intent i = getIntent();

        if (Intent.ACTION_VIEW.equals(i.getAction())) {
            Uri uri = i.getData();
            System.out.printf("%s%n", uri);

            Uri u = uri.buildUpon().scheme("https").build();

            link.setText(u.toString());
        }
    }
}
