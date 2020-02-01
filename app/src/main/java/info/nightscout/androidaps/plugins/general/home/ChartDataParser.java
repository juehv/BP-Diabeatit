package info.nightscout.androidaps.plugins.general.home;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.Series;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import info.nightscout.androidaps.Constants;
import info.nightscout.androidaps.data.Profile;
import info.nightscout.androidaps.db.BgReading;
import info.nightscout.androidaps.logging.L;
import info.nightscout.androidaps.plugins.general.overview.graphExtensions.DataPointWithLabelInterface;
import info.nightscout.androidaps.plugins.general.overview.graphExtensions.PointsWithLabelGraphSeries;
import info.nightscout.androidaps.plugins.iob.iobCobCalculator.IobCobCalculatorPlugin;
import info.nightscout.androidaps.utils.Round;

public class ChartDataParser {
    private static Logger log = LoggerFactory.getLogger(L.HOME);

    private GraphView graph;

    public double maxY = Double.MIN_VALUE;
    public double minY = Double.MAX_VALUE;
    private List<BgReading> bgReadingsArray;
    private String units;
    private List<Series> series = new ArrayList<>();

    private IobCobCalculatorPlugin iobCobCalculatorPlugin;

    public ChartDataParser(GraphView graph) {
        this.graph = graph;
        this.iobCobCalculatorPlugin = IobCobCalculatorPlugin.getPlugin();
    }

    public static List<BgReading> getDummyData() {
        final long SECOND = 1000;
        final long MINUTE = 60 * SECOND;
        final long HOUR = 60 * MINUTE;

        ArrayList<BgReading> readings = new ArrayList<>();
        readings.add(new BgReading().date(DateTime.now().getMillis() - 5 * HOUR).value(110));
        readings.add(new BgReading().date(DateTime.now().getMillis() - 4 * HOUR).value(120));
        readings.add(new BgReading().date(DateTime.now().getMillis() - 3 * HOUR).value(130));
        readings.add(new BgReading().date(DateTime.now().getMillis() - 2 * HOUR).value(140));
        readings.add(new BgReading().date(System.currentTimeMillis() - HOUR).value(130));
        return readings;
    }

    public void clearSeries() {
        series.clear();
    }

    public void addBgReadings(long fromTime, long toTime, double lowLine, double highLine, List<BgReading> predictions) {
        double maxBgValue = Double.MIN_VALUE;
        //bgReadingsArray = MainApp.getDbHelper().getBgreadingsDataFromTime(fromTime, true);
        bgReadingsArray = iobCobCalculatorPlugin.getBgReadings();
        List<DataPointWithLabelInterface> bgListArray = new ArrayList<>();

        if (bgReadingsArray == null || bgReadingsArray.size() == 0) {
            if (L.isEnabled(L.OVERVIEW))
                log.debug("No BG data.");
            maxY = 10;
            minY = 0;
            bgReadingsArray = getDummyData();
            // return;
        }

        for (BgReading bg : bgReadingsArray) {
            if (bg.date < fromTime || bg.date > toTime) continue;
            if (bg.value > maxBgValue) maxBgValue = bg.value;
            bgListArray.add(bg);
        }
        if (predictions != null) {
            Collections.sort(predictions, (o1, o2) -> Double.compare(o1.getX(), o2.getX()));
            for (BgReading prediction : predictions) {
                if (prediction.value >= 40)
                    bgListArray.add(prediction);
            }
        }

        // maxBgValue = Profile.fromMgdlToUnits(maxBgValue, units);
        // maxBgValue = units.equals(Constants.MGDL) ? Round.roundTo(maxBgValue, 40d) + 80 : Round.roundTo(maxBgValue, 2d) + 4;
        if (highLine > maxBgValue) maxBgValue = highLine;
        // int numOfVertLines = units.equals(Constants.MGDL) ? (int) (maxBgValue / 40 + 1) : (int) (maxBgValue / 2 + 1);
        int numOfVertLines = (int) maxBgValue / 2 + 1;

        DataPointWithLabelInterface[] bg = new DataPointWithLabelInterface[bgListArray.size()];
        bg = bgListArray.toArray(bg);


        maxY = maxBgValue;
        minY = 0;
        // set manual y bounds to have nice steps
        graph.getGridLabelRenderer().setNumVerticalLabels(numOfVertLines);

        series.add(new PointsWithLabelGraphSeries<>(bg));
    }

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
