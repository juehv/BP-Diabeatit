package info.nightscout.androidaps.diabeatit.ui.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.jjoe64.graphview.GraphView;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import info.nightscout.androidaps.Config;
import info.nightscout.androidaps.MainApp;
import info.nightscout.androidaps.R;
import info.nightscout.androidaps.data.IobTotal;
import info.nightscout.androidaps.db.BgReading;
import info.nightscout.androidaps.db.DatabaseHelper;
import info.nightscout.androidaps.interfaces.Constraint;
import info.nightscout.androidaps.interfaces.PumpInterface;
import info.nightscout.androidaps.logging.L;
import info.nightscout.androidaps.plugins.aps.loop.APSResult;
import info.nightscout.androidaps.plugins.aps.loop.LoopPlugin;
import info.nightscout.androidaps.plugins.configBuilder.ConfigBuilderPlugin;
import info.nightscout.androidaps.plugins.configBuilder.ProfileFunctions;
import info.nightscout.androidaps.plugins.general.home.ChartDataParser;
import info.nightscout.androidaps.plugins.general.nsclient.data.NSDeviceStatus;
import info.nightscout.androidaps.plugins.general.overview.OverviewPlugin;
import info.nightscout.androidaps.plugins.treatments.TreatmentsPlugin;
import info.nightscout.androidaps.utils.Profiler;
import info.nightscout.androidaps.utils.SP;
import info.nightscout.androidaps.utils.T;
import lecho.lib.hellocharts.model.ChartData;


public class HomeFragment extends Fragment {
    private static Logger log = LoggerFactory.getLogger(L.HOME);

    private HomeViewModel homeViewModel;
    private int rangeToDisplay = 6; // for graph
    private GraphView graph;
    private ChartDataParser data;


    private int numberOfLines = 1;
    private int maxNumberOfLines = 4;
    private int numberOfPoints = 12;

    float[][] randomNumbersTab = new float[maxNumberOfLines][numberOfPoints];

    private boolean hasAxes = true;
    private boolean hasAxesNames = true;
    private boolean hasPoints = true;
    private boolean hasLines = true;
    private boolean isCubic = false;
    private boolean hasLabels = false;

    private static final ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor();
    private static ScheduledFuture<?> scheduledUpdate = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.d_fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });
        Log.d("MAIN", "Getting graph and creating data");
        graph = root.findViewById(R.id.chart);
        data = new ChartDataParser(graph);
        scheduleUpdateGUI("");
        return root;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Fragment childFragment = new BolusCalculatorFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.bolus_calculator_fragment_container, childFragment).commit();
        scheduleUpdateGUI("onViewCreated");
    }

    public void scheduleUpdateGUI(final String from) {
        class UpdateRunnable implements Runnable {
            public void run() {
                Activity activity = getActivity();
                if (activity != null)
                    activity.runOnUiThread(() -> {
                        updateGUI(from);
                        scheduledUpdate = null;
                    });
            }
        }
        // prepare task for execution in 400 msec
        // cancel waiting task to prevent multiple updates
        if (scheduledUpdate != null)
            scheduledUpdate.cancel(false);
        Runnable task = new UpdateRunnable();
        final int msec = 500;
        scheduledUpdate = worker.schedule(task, msec, TimeUnit.MILLISECONDS);
    }

    @SuppressLint("SetTextI18n")
    public void updateGUI(final String from) {
        if (L.isEnabled(L.OVERVIEW))
            log.debug("updateGUI entered from: " + from);
        final long updateGUIStart = System.currentTimeMillis();

        if (getActivity() == null)
            return;

        BgReading actualBG = DatabaseHelper.actualBg();
        BgReading lastBG = DatabaseHelper.lastBg();

        final PumpInterface pump = ConfigBuilderPlugin.getPlugin().getActivePump();

        final String units = ProfileFunctions.getInstance().getProfileUnits();
        final double lowLine = OverviewPlugin.INSTANCE.determineLowLine(units);
        final double highLine = OverviewPlugin.INSTANCE.determineHighLine(units);

        Constraint<Boolean> closedLoopEnabled = MainApp.getConstraintChecker().isClosedLoopAllowed();

        // iob
        TreatmentsPlugin.getPlugin().updateTotalIOBTreatments();
        TreatmentsPlugin.getPlugin().updateTotalIOBTempBasals();
        final IobTotal bolusIob = TreatmentsPlugin.getPlugin().getLastCalculationTreatments().round();
        final IobTotal basalIob = TreatmentsPlugin.getPlugin().getLastCalculationTempBasals().round();


        final LoopPlugin.LastRun finalLastRun = LoopPlugin.lastRun;
        boolean predictionsAvailable;
        if (Config.APS)
            predictionsAvailable = finalLastRun != null && finalLastRun.request.hasPredictions;
        else if (Config.NSCLIENT)
            predictionsAvailable = true;
        else
            predictionsAvailable = false;
        final boolean finalPredictionsAvailable = predictionsAvailable;

        // ****** GRAPH *******

        new Thread(() -> {
            // allign to hours
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.add(Calendar.HOUR, 1);

            int hoursToFetch;
            long toTime;
            long fromTime;
            long endTime;

            APSResult apsResult = null;

            if (finalPredictionsAvailable && SP.getBoolean("showprediction", false)) {
                if (Config.APS)
                    apsResult = finalLastRun.constraintsProcessed;
                else
                    apsResult = NSDeviceStatus.getAPSResult();
                int predHours = (int) (Math.ceil(apsResult.getLatestPredictionsTime() - System.currentTimeMillis()) / (60 * 60 * 1000));
                predHours = Math.min(2, predHours);
                predHours = Math.max(0, predHours);
                hoursToFetch = rangeToDisplay - predHours;
                toTime = calendar.getTimeInMillis() + 100000; // little bit more to avoid wrong rounding - Graphview specific
                fromTime = toTime - T.hours(hoursToFetch).msecs();
                endTime = toTime + T.hours(predHours).msecs();
            } else {
                hoursToFetch = rangeToDisplay;
                toTime = calendar.getTimeInMillis() + 100000; // little bit more to avoid wrong rounding - Graphview specific
                fromTime = toTime - T.hours(hoursToFetch).msecs();
                endTime = toTime;
            }


            final long now = System.currentTimeMillis();

            //  ------------------ 1st graph
            if (L.isEnabled(L.OVERVIEW))
                Profiler.log(log, from + " - 1st graph - START", updateGUIStart);

            // final ChartDataParser chartDataParser = new ChartDataParser(chart, IobCobCalculatorPlugin.getPlugin());
            // chartDataParser.addBgReadings(fromTime, toTime, lowLine, highLine, null);
            data.clearSeries();
            data.addBgReadings(fromTime, endTime, lowLine, highLine, ChartDataParser.getDummyPredictions());
            data.formatAxis(fromTime, endTime);
            data.addNowLine();
            Log.d("GRAPH", String.format("fromTime=%d endTime=%d toTime=%d", fromTime, endTime, toTime));
/*
            // **** In range Area ****
            graphData.addInRangeArea(fromTime, endTime, lowLine, highLine);

            // **** BG ****
            if (finalPredictionsAvailable && SP.getBoolean("showprediction", false))
                graphData.addBgReadings(fromTime, toTime, lowLine, highLine,
                        apsResult.getPredictions());
            else
                graphData.addBgReadings(fromTime, toTime, lowLine, highLine, null);

            // set manual x bounds to have nice steps
            graphData.formatAxis(fromTime, endTime);

            // Treatments
            graphData.addTreatments(fromTime, endTime);

            if (SP.getBoolean("showactivityprimary", true)) {
                graphData.addActivity(fromTime, endTime, false, 0.8d);
            }

            // add basal data
            if (pump.getPumpDescription().isTempBasalCapable && SP.getBoolean("showbasals", true)) {
                graphData.addBasals(fromTime, now, lowLine / graphData.maxY / 1.2d);
            }

            // add target line
            graphData.addTargetLine(fromTime, toTime, profile);

            // **** NOW line ****
            graphData.addNowLine(now);
*/
            /*
            // ------------------ 2nd graph
            if (L.isEnabled(L.OVERVIEW))
                Profiler.log(log, from + " - 2nd graph - START", updateGUIStart);

            final GraphData secondGraphData = new GraphData(iobGraph, IobCobCalculatorPlugin.getPlugin());

            boolean useIobForScale = false;
            boolean useCobForScale = false;
            boolean useDevForScale = false;
            boolean useRatioForScale = false;
            boolean useDSForScale = false;
            boolean useIAForScale = false;

            if (SP.getBoolean("showiob", true)) {
                useIobForScale = true;
            } else if (SP.getBoolean("showcob", true)) {
                useCobForScale = true;
            } else if (SP.getBoolean("showdeviations", false)) {
                useDevForScale = true;
            } else if (SP.getBoolean("showratios", false)) {
                useRatioForScale = true;
            } else if (SP.getBoolean("showactivitysecondary", false)) {
                useIAForScale = true;
            } else if (SP.getBoolean("showdevslope", false)) {
                useDSForScale = true;
            }

            if (SP.getBoolean("showiob", true))
                secondGraphData.addIob(fromTime, now, useIobForScale, 1d, SP.getBoolean("showprediction", false));
            if (SP.getBoolean("showcob", true))
                secondGraphData.addCob(fromTime, now, useCobForScale, useCobForScale ? 1d : 0.5d);
            if (SP.getBoolean("showdeviations", false))
                secondGraphData.addDeviations(fromTime, now, useDevForScale, 1d);
            if (SP.getBoolean("showratios", false))
                secondGraphData.addRatio(fromTime, now, useRatioForScale, 1d);
            if (SP.getBoolean("showactivitysecondary", true))
                secondGraphData.addActivity(fromTime, endTime, useIAForScale, 0.8d);
            if (SP.getBoolean("showdevslope", false) && MainApp.devBranch)
                secondGraphData.addDeviationSlope(fromTime, now, useDSForScale, 1d);

            // **** NOW line ****
            // set manual x bounds to have nice steps
            secondGraphData.formatAxis(fromTime, endTime);
            secondGraphData.addNowLine(now);

            // do GUI update
            FragmentActivity activity = getActivity();
            if (activity != null) {
                activity.runOnUiThread(() -> {
                    if (SP.getBoolean("showiob", true)
                            || SP.getBoolean("showcob", true)
                            || SP.getBoolean("showdeviations", false)
                            || SP.getBoolean("showratios", false)
                            || SP.getBoolean("showactivitysecondary", false)
                            || SP.getBoolean("showdevslope", false)) {
                        iobGraph.setVisibility(View.VISIBLE);
                    } else {
                        iobGraph.setVisibility(View.GONE);
                    }
                    // finally enforce drawing of graphs
                    graphData.performUpdate();
                    secondGraphData.performUpdate();
                    if (L.isEnabled(L.OVERVIEW))
                        Profiler.log(log, from + " - onDataChanged", updateGUIStart);
                });
            }
            */
            FragmentActivity activity = getActivity();
            if (activity != null) {
                activity.runOnUiThread(() -> {
                    // finally enforce drawing of graphs
                    data.forceUpdate();
                    if (L.isEnabled(L.OVERVIEW))
                        Profiler.log(log, from + " - onDataChanged", updateGUIStart);
                });
            }
        }).start();

        if (L.isEnabled(L.OVERVIEW))
            Profiler.log(log, from, updateGUIStart);
    }

}
