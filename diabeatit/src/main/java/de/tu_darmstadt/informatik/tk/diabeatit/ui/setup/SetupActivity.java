package de.tu_darmstadt.informatik.tk.diabeatit.ui.setup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import de.tu_darmstadt.informatik.tk.diabeatit.R;

public class SetupActivity
        extends AppCompatActivity
        implements SensorSelection.OnFragmentInteractionListener,
                   SensorSettings.OnFragmentInteractionListener,
                    WelcomeButton.OnFragmentInteractionListener {

    TextView title;
    TextView explanation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        WelcomeButton settings = new WelcomeButton();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.setup_step_frame, settings);
        ft.commit();

        title = findViewById(R.id.setup_title);
        explanation = findViewById(R.id.setup_explanation);

        setTitle("Setup");

    }

    @Override
    public void onFragmentInteraction(Fragment frm) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.setup_step_frame, frm);
        ft.commit();

        title.setText("Richte deinen Sensor ein");
        explanation.setVisibility(View.INVISIBLE);
    }

    @Override
    public void startSetup() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.setup_step_frame, new SensorSelection());
        ft.commit();
        title.setText("Wähle deinen Sensor aus");
        explanation.setVisibility(View.INVISIBLE);
    }
}
