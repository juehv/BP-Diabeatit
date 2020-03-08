package info.nightscout.androidaps.diabeatit.ui.setup;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;

import info.nightscout.androidaps.R;

public class SetupInsight extends AppCompatActivity {

    protected Button backToSetup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_insight);
        backToSetup = (Button) findViewById(R.id.return_to_setup);
        backToSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(SetupInsight.this, SetupActivity.class));
            }
        });
    }

}