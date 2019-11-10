package de.tu_darmstadt.informatik.tk.diabeatit.ui.setup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;

import de.tu_darmstadt.informatik.tk.diabeatit.R;

public class SetupActivity
        extends AppCompatActivity
        implements SensorSelection.OnFragmentInteractionListener,
                   SensorSettings.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        SensorSelection settings = new SensorSelection();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.setup_step_frame, settings);
        ft.commit();


    }

    @Override
    public void onFragmentInteraction(Fragment frm) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.setup_step_frame, frm);
        ft.commit();
    }
}
