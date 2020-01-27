package info.nightscout.androidaps.plugins.general.home;

import com.jjoe64.graphview.series.Series;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import info.nightscout.androidaps.Constants;
import info.nightscout.androidaps.data.Profile;
import info.nightscout.androidaps.db.BgReading;
import info.nightscout.androidaps.logging.L;
import info.nightscout.androidaps.plugins.configBuilder.ProfileFunctions;
import info.nightscout.androidaps.plugins.general.overview.graphExtensions.DataPointWithLabelInterface;
import info.nightscout.androidaps.plugins.iob.iobCobCalculator.IobCobCalculatorPlugin;
import info.nightscout.androidaps.utils.Round;
import lecho.lib.hellocharts.model.ComboLineColumnChartData;
import lecho.lib.hellocharts.view.ComboLineColumnChartView;

public class ChartDataParser {
    private static Logger log = LoggerFactory.getLogger(L.HOME);

    private ComboLineColumnChartView chart;
    private ComboLineColumnChartData data;

    public double maxY = Double.MIN_VALUE;
    public double minY = Double.MAX_VALUE;
    private List<BgReading> bgReadingsArray;
    private String units;
    private List<Series> series = new ArrayList<>();

    private IobCobCalculatorPlugin iobCobCalculatorPlugin;

    public ChartDataParser(ComboLineColumnChartView chart, IobCobCalculatorPlugin iobCobCalculatorPlugin) {
        units = ProfileFunctions.getInstance().getProfileUnits();
        this.chart = chart;
        this.iobCobCalculatorPlugin = iobCobCalculatorPlugin;
    }

    public void addBgReadings(long fromTime, long toTime, double lowLine, double highLine, List<BgReading> predictions) {
        double maxBgValue = Double.MIN_VALUE;
        //bgReadingsArray = MainApp.getDbHelper().getBgreadingsDataFromTime(fromTime, true);
        bgReadingsArray = iobCobCalculatorPlugin.getBgReadings();
        bgReadingsArray = iobCobCalculatorPlugin.getBucketedData();
        
        List<DataPointWithLabelInterface> bgListArray = new ArrayList<>();

        if (bgReadingsArray == null || bgReadingsArray.size() == 0) {
            if (L.isEnabled(L.OVERVIEW))
                log.debug("No BG data.");
            maxY = 10;
            minY = 0;
            return;
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

        maxBgValue = Profile.fromMgdlToUnits(maxBgValue, units);
        maxBgValue = units.equals(Constants.MGDL) ? Round.roundTo(maxBgValue, 40d) + 80 : Round.roundTo(maxBgValue, 2d) + 4;
        if (highLine > maxBgValue) maxBgValue = highLine;
        int numOfVertLines = units.equals(Constants.MGDL) ? (int) (maxBgValue / 40 + 1) : (int) (maxBgValue / 2 + 1);

        DataPointWithLabelInterface[] bg = new DataPointWithLabelInterface[bgListArray.size()];
        bg = bgListArray.toArray(bg);


        maxY = maxBgValue;
        minY = 0;
        // set manual y bounds to have nice steps
        //graph.getGridLabelRenderer().setNumVerticalLabels(numOfVertLines);

        //addSeries(new PointsWithLabelGraphSeries<>(bg));
    }
}
