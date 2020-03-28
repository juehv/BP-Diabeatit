package info.nightscout.androidaps.diabeatit.ui.home;

import android.content.Context;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.util.Log;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.jjoe64.graphview.series.Series;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import info.nightscout.androidaps.MainApp;
import info.nightscout.androidaps.R;
import info.nightscout.androidaps.data.IobTotal;
import info.nightscout.androidaps.data.Profile;
import info.nightscout.androidaps.db.BgReading;
import info.nightscout.androidaps.db.DatabaseHelper;
import info.nightscout.androidaps.diabeatit.predictions.PredictionsPlugin;
import info.nightscout.androidaps.logging.L;
import info.nightscout.androidaps.plugins.aps.openAPSSMB.SMBDefaults;
import info.nightscout.androidaps.plugins.configBuilder.ProfileFunctions;
import info.nightscout.androidaps.plugins.general.overview.graphExtensions.AreaGraphSeries;
import info.nightscout.androidaps.plugins.general.overview.graphExtensions.DataPointWithLabelInterface;
import info.nightscout.androidaps.plugins.general.overview.graphExtensions.DoubleDataPoint;
import info.nightscout.androidaps.plugins.general.overview.graphExtensions.FixedLineGraphSeries;
import info.nightscout.androidaps.plugins.general.overview.graphExtensions.PointsWithLabelGraphSeries;
import info.nightscout.androidaps.plugins.general.overview.graphExtensions.Scale;
import info.nightscout.androidaps.plugins.general.overview.graphExtensions.ScaledDataPoint;
import info.nightscout.androidaps.plugins.general.overview.graphExtensions.TimeAsXAxisLabelFormatter;
import info.nightscout.androidaps.plugins.iob.iobCobCalculator.AutosensData;
import info.nightscout.androidaps.plugins.iob.iobCobCalculator.AutosensResult;
import info.nightscout.androidaps.plugins.iob.iobCobCalculator.IobCobCalculatorPlugin;
import info.nightscout.androidaps.plugins.treatments.Treatment;
import info.nightscout.androidaps.plugins.treatments.TreatmentsPlugin;
import info.nightscout.androidaps.utils.Round;
import kotlin.random.Random;

/**
 * Wrapper class that handles the collection and conversion of data shown in the graph in the
 * HomeActivity
 */
public class ChartDataParser {
    private static Logger log = LoggerFactory.getLogger(L.HOME);

    private GraphView graph;

    private long tsNow = 0;

    public double maxY = Double.MIN_VALUE;
    public double minY = Double.MAX_VALUE;
    private List<BgReading> bgReadingsArray;
    private String units;
    private List<Series> series = new ArrayList<>();

    private PointsWithLabelGraphSeries<DataPointWithLabelInterface> bgSeries;
    private PointsWithLabelGraphSeries<DataPointWithLabelInterface> predSeries;

    /** Creates a new chart data parser
     *
     * @param graph The graph that should be updated
     */
    public ChartDataParser(GraphView graph) {
        this.graph = graph;
    }

    public static List<BgReading> getDummyData(long start, long end) {
        final long SECOND = 1000;
        final long MINUTE = 60 * SECOND;
        final long HOUR = 60 * MINUTE;

        ArrayList<BgReading> readings = new ArrayList<>();

        double lastDelta = 0;
        double lastValue = 120;

        for (long current = start; current < System.currentTimeMillis(); current += 10*60*1000) {
            double delta = Random.Default.nextDouble(-5, 5);
            double value = lastValue
                    + (120 - lastValue) * 0.1
                    + lastDelta/2
                    + Random.Default.nextDouble(-10, 10);
            lastDelta = delta;
            lastValue = value;
            readings.add(new BgReading().date(current).value(value));
        }

        return readings;
    }

    /** Clear any existing series of data */
    public void clearSeries() {
        series.clear();
    }

    /** Add a series for the blood glucose level readings
     *
     * @param fromTime          Time from which readings will be displayed
     * @param toTime            Time to which readings will be displayed
     * @param lowLine           Lower bound of the target range for the values
     * @param highLine          Upper bound of the target range for the values
     */
    public void addBgReadings(long fromTime, long toTime, double lowLine, double highLine) {
        double maxBgValue = Double.MIN_VALUE;
        bgReadingsArray = IobCobCalculatorPlugin.getPlugin().getBgReadings();
        List<DataPointInterface> bgListArray = new ArrayList<>();

        if (bgReadingsArray == null || bgReadingsArray.size() == 0) {
            if (L.isEnabled(L.OVERVIEW))
                log.debug("No BG data.");
            maxY = 10;
            minY = 0;
            bgReadingsArray = getDummyData(fromTime, toTime);
        }

        for (BgReading bg : bgReadingsArray) {
            if (bg.date < fromTime || bg.date > toTime) continue;
            if (bg.value > maxBgValue) maxBgValue = bg.value;
            bgListArray.add(bg);
        }

        // maxBgValue = Profile.fromMgdlToUnits(maxBgValue, units);
        // maxBgValue = units.equals(Constants.MGDL) ? Round.roundTo(maxBgValue, 40d) + 80 : Round.roundTo(maxBgValue, 2d) + 4;
        if (highLine > maxBgValue) maxBgValue = highLine;
        // int numOfVertLines = units.equals(Constants.MGDL) ? (int) (maxBgValue / 40 + 1) : (int) (maxBgValue / 2 + 1);
        int numOfVertLines = (int) maxBgValue / 2 + 1;

        DataPointInterface[] bg = new DataPointInterface[bgListArray.size()];
        bg = bgListArray.toArray(bg);

        maxY = maxBgValue;
        minY = 0;
        // set manual y bounds to have nice steps
        graph.getGridLabelRenderer().setNumVerticalLabels(numOfVertLines);

        LineGraphSeries<DataPointInterface> bgSeries = new LineGraphSeries<>(bg);
        bgSeries.setColor(graph.getContext().getColor(R.color.graphBgReadingsColor));
        bgSeries.setDrawDataPoints(true);
        bgSeries.setDataPointsRadius(1);
//        bgSeries.setShape(PointsGraphSeries.Shape.POINT);
//        bgSeries.setSize(10);

        series.add(bgSeries);
    }

    /** Add a series for the predictons */
    public void addPredictions() {
        if (DatabaseHelper.lastBg() == null)
            return;
        List<BgReading> readings = IobCobCalculatorPlugin.getPlugin().getBgReadings();
        BgReading lastBg = DatabaseHelper.lastBg();
        List<BgReading> preds;

        if (readings == null || readings.size() == 0) {
            preds = new ArrayList<>();
        } else {
            preds = PredictionsPlugin.getPlugin().getPredictionReadings();
        }

        preds = preds.stream().filter(p -> p.getX() >= tsNow).collect(Collectors.toList());

        for (BgReading r : preds) {
            r.value += lastBg.value;
        }

        DataPointWithLabelInterface[] pred = new DataPointWithLabelInterface[preds.size()];
        pred = preds.toArray(pred);
        // Predictions are offsets!

        LineGraphSeries<DataPointWithLabelInterface> predSeries = new LineGraphSeries<>(pred);
        predSeries.setColor(graph.getContext().getColor(R.color.graphBgPredictionColor));
        predSeries.setDrawDataPoints(true);
        predSeries.setDataPointsRadius(1);
        //predSeries.setShape(PointsGraphSeries.Shape.RECTANGLE);
        //predSeries.setSize(10);

        series.add(predSeries);
    }

    /** Add the icons indicating a bolus event has occured
     *
     * @param fromTime          Start time to add events from
     */
    public void addBolusEvents(long fromTime) {
        List<Treatment> treatments = TreatmentsPlugin.getPlugin().getService().getTreatmentDataFromTime(fromTime, true);

        treatments.sort((t1, t2) -> Long.compare(t1.date, t2.date));
        List<DataPoint> pointsMeal = treatments.stream()
                .filter(t -> t.isValid && t.mealBolus)
                .map(t -> new DataPoint(t.date, 0))
                .collect(Collectors.toList());

        DataPoint[] mealPointsArray = pointsMeal.toArray(new DataPoint[0]);

        List<DataPoint> pointsBolus = treatments.stream()
                .filter(t -> t.isValid && !t.mealBolus)
                .map(t -> new DataPoint(t.date, 0))
                .collect(Collectors.toList());

        DataPoint[] bolusPointsArray = pointsBolus.toArray(new DataPoint[0]);

        PointsGraphSeries<DataPoint> seriesMeal = new PointsGraphSeries<>(mealPointsArray);

        seriesMeal.setColor(MainApp.gc(R.color.graphMealColor));
        seriesMeal.setShape(PointsGraphSeries.Shape.TRIANGLE);
        seriesMeal.setSize(20);

        PointsGraphSeries<DataPoint> seriesBolus = new PointsGraphSeries<>(bolusPointsArray);

        seriesBolus.setColor(MainApp.gc(R.color.graphBolusColor));
        seriesBolus.setShape(PointsGraphSeries.Shape.TRIANGLE);
        seriesBolus.setSize(20);

        this.series.add(seriesMeal);
        this.series.add(seriesBolus);
    }

    /** Add the curve displaying the insulin on board
     *
     * @param fromTime          Starting time from which to consider the data
     * @param toTime            End time to which data should be displayed
     * @param useForScale       Use this data for scaling
     * @param scale             The scale to apply
     */
    public void addIob(long fromTime, long toTime, boolean useForScale, double scale) {
        FixedLineGraphSeries<ScaledDataPoint> iobSeries;
        List<ScaledDataPoint> iobArray = new ArrayList<>();
        Double maxIobValueFound = Double.MIN_VALUE;
        double lastIob = 0;
        Scale iobScale = new Scale();

        for (long time = fromTime; time <= toTime; time += 5 * 60 * 1000L) {
            Profile profile = ProfileFunctions.getInstance().getProfile(time);
            double iob = 0d;
            if (profile != null)
                iob = IobCobCalculatorPlugin.getPlugin().calculateFromTreatmentsAndTempsSynchronized(time, profile).iob;
            if (Math.abs(lastIob - iob) > 0.02) {
                if (Math.abs(lastIob - iob) > 0.2)
                    iobArray.add(new ScaledDataPoint(time, lastIob, iobScale));
                iobArray.add(new ScaledDataPoint(time, iob, iobScale));
                maxIobValueFound = Math.max(maxIobValueFound, Math.abs(iob));
                lastIob = iob;
            }
        }

        ScaledDataPoint[] iobData = new ScaledDataPoint[iobArray.size()];
        iobData = iobArray.toArray(iobData);
        iobSeries = new FixedLineGraphSeries<>(iobData);
        iobSeries.setDrawBackground(true);
        iobSeries.setBackgroundColor(0x80FFFFFF & MainApp.gc(R.color.graphIobColor)); //50%
        iobSeries.setColor(MainApp.gc(R.color.graphIobColor));
        iobSeries.setThickness(3);

        if (useForScale) {
            maxY = maxIobValueFound;
            minY = -maxIobValueFound;
        }

        iobScale.setMultiplier(maxY * scale / maxIobValueFound);

        this.series.add(iobSeries);
    }

    /** Add the area depicting which blood glucose values are within the target area
     *
     * @param fromTime      Start time from which to display this area
     * @param toTime        End time to which to display the area
     * @param lowLine       Lower bound of the area
     * @param highLine      Upper bound of the area
     */
    public void addInRangeArea(long fromTime, long toTime, double lowLine, double highLine) {
        AreaGraphSeries<DoubleDataPoint> inRangeAreaSeries;

        DoubleDataPoint[] inRangeAreaDataPoints = new DoubleDataPoint[]{
                new DoubleDataPoint(fromTime, lowLine, highLine),
                new DoubleDataPoint(toTime, lowLine, highLine)
        };
        inRangeAreaSeries = new AreaGraphSeries<>(inRangeAreaDataPoints);
        inRangeAreaSeries.setColor(0);
        inRangeAreaSeries.setDrawBackground(true);
        inRangeAreaSeries.setBackgroundColor(MainApp.gc(R.color.graphTargetBgColor));

        series.add(inRangeAreaSeries);
    }

    /** Add the line depicting the current point in time */
    public void addNowLine() {
        long now = Instant.now().toEpochMilli();
        LineGraphSeries<DataPoint> seriesNow;
        DataPoint[] nowPoints = new DataPoint[]{
                new DataPoint(now, 0),
                new DataPoint(now, maxY)
        };

        seriesNow = new LineGraphSeries<>(nowPoints);
        seriesNow.setDrawDataPoints(false);
        // custom paint to make a dotted line
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        paint.setPathEffect(new DashPathEffect(new float[]{10, 20}, 0));
        paint.setColor(Color.BLACK);
        seriesNow.setCustomPaint(paint);

        this.series.add(seriesNow);
        tsNow = now;
    }

    /** Format the axis and labels */
    public void formatAxis(long startTime, long endTime) {
        Context ctx = graph.getContext();

        graph.getViewport().setMaxX(endTime);
        graph.getViewport().setMinX(startTime);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setScalable(true);
        graph.getViewport().setScrollable(true);

        graph.getGridLabelRenderer().setLabelFormatter(new TimeAsXAxisLabelFormatter(ctx.getString(R.string.graphLabelFormat)));
        graph.getGridLabelRenderer().setNumHorizontalLabels(7);
        graph.getGridLabelRenderer().setNumVerticalLabels(7);

        graph.getGridLabelRenderer().setGridColor(ctx.getColor(R.color.graphGridColor));
        graph.getGridLabelRenderer().setVerticalLabelsColor(ctx.getColor(R.color.graphVerticalLabelColor));
        graph.getGridLabelRenderer().setHorizontalLabelsColor(ctx.getColor(R.color.graphHorizontalLabelColor));
        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.BOTH);

        graph.getGridLabelRenderer().reloadStyles();
    }

    /** Force an update and recalculate all series */
    public void forceUpdate() {
        // clear old data
        graph.getSeries().clear();

        // add precalculate series
        for (Series s : this.series) {
            if (!s.isEmpty()) {
                s.onGraphViewAttached(graph);
                graph.getSeries().add(s);
            }
        }

        double step = 1d;
        if (maxY < 1) step = 0.1d;
        graph.getViewport().setMaxY(Round.ceilTo(maxY, step));
        graph.getViewport().setMinY(Round.floorTo(minY, step));
        graph.getViewport().setYAxisBoundsManual(true);

        // draw it
        graph.onDataChanged(false, false);
    }
}
