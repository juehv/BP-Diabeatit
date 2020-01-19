package info.nightscout.androidaps.ui.setup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import info.nightscout.androidaps.R;

public class ActualSetup extends AppCompatActivity {

    protected TextView headline;
    protected Button prev, next, save;
    protected int counter;
    protected RadioGroup radioProfil, radioInsulin, radioBG, radioPump;
    protected String[] headline_artikel = new String[] {"Profil", "Insulintyp", "BZ-Quelle", "Pumpe"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actual_setup);
        headline = (TextView) findViewById(R.id.setup_headline);
        headline.setText(headline_artikel[0]);
        radioProfil = (RadioGroup) findViewById(R.id.profil);
        radioInsulin = (RadioGroup) findViewById(R.id.insulintyp);
        radioBG = (RadioGroup) findViewById(R.id.bg_source);
        radioPump = (RadioGroup) findViewById(R.id.pump);
        radioInsulin.setVisibility(View.INVISIBLE);
        radioBG.setVisibility((View.INVISIBLE));
        radioPump.setVisibility(View.INVISIBLE);
        save = (Button) findViewById(R.id.save_button);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ActualSetup.this, SetupActivity.class));
            }
        });
        save.setVisibility(View.INVISIBLE);
    }

    public void next(View view) {
        if (counter != 3) counter++;
            headline.setText(headline_artikel[counter]);

            switch (counter) {
                case 1:
                    radioProfil.setVisibility(View.INVISIBLE);
                    radioInsulin.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    radioInsulin.setVisibility(View.INVISIBLE);
                    radioBG.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    radioBG.setVisibility(View.INVISIBLE);
                    radioPump.setVisibility(View.VISIBLE);
                    save.setVisibility(View.VISIBLE);
                    break;
                default:
            }
    }

    public void prev(View view) {
        if (counter != 0) counter--;
            headline.setText(headline_artikel[counter]);

            switch (counter) {
                case 0:
                    radioProfil.setVisibility(View.VISIBLE);
                    radioInsulin.setVisibility(View.INVISIBLE);
                    break;
                case 1:
                    radioInsulin.setVisibility(View.VISIBLE);
                    radioBG.setVisibility(View.INVISIBLE);
                    break;
                case  2:
                    radioBG.setVisibility(View.VISIBLE);
                    radioPump.setVisibility(View.INVISIBLE);
                    save.setVisibility(View.INVISIBLE);
                    break;
                default:
            }
    }

    public void checkButton(View view) {

    }
}
