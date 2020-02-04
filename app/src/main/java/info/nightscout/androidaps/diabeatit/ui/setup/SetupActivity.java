package info.nightscout.androidaps.diabeatit.ui.setup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import info.nightscout.androidaps.R;

public class SetupActivity
        extends AppCompatActivity {

    protected TextView title, explanation;
    protected Button startSetup, showSetup, returnHome;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        title = findViewById(R.id.setup_title);
        explanation = findViewById(R.id.setup_explanation);
        startSetup = (Button) findViewById(R.id.start_setup_button);
        showSetup = (Button) findViewById(R.id.show_setup_button);
        returnHome = (Button) findViewById(R.id.return_to_home_screen);

        startSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SetupActivity.this, ActualSetup.class));
            }
        });

        showSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SetupActivity.this, SetupInsight.class));
            }
        });

        returnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SetupActivity.this, HomeActivity.class));
            }
        });

        setTitle("Setup");

    }


}
