package info.nightscout.androidaps.plugins.insulin.prediction;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SplineInterpolationTest {
    /*
     * You can visualize this curve, e.g. under https://www.desmos.com/calculator/th2kg7mb1b
     */
    private final static double DELTA = 0.001d;

    private final static double[] XVALUES = { 1d, 2d,  3d, 4d, 5d, 6d, 7d};
    private final static double[] YVALUES = { 0d, 1d, -1d, 2d, 1d, 1d, 0d};

    private SplineInterpolation interpolation;
    
    @Before
    public void setupInterpolation() {
        interpolation = new SplineInterpolation();
        for (int i = 0; i < XVALUES.length; i++) {
            interpolation.addDatapoint(XVALUES[i], YVALUES[i]);
        }
    }

    /** Test that fitting is perfect on the set datapoints. */
    @Test
    public void testPerfectFitOnSetPoints() {
        for (int i = 0; i < XVALUES.length; i++) {
            Assert.assertEquals(interpolation.getValueAt(XVALUES[i]), YVALUES[i], DELTA);
        }
    }

    @Test
    public void testIntermedatePoints() {
        /* TODO: Validity of these points */
        Assert.assertEquals(0.6875d, interpolation.getValueAt(1.5d), DELTA);
    }

    // TODO: We can also test the slopes to be reasonable etc.
}
