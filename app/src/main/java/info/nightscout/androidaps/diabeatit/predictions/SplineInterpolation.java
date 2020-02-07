package info.nightscout.androidaps.diabeatit.predictions;

import java.util.ArrayList;
import java.util.List;

import info.nightscout.androidaps.db.BgReading;

public class SplineInterpolation implements InterpolationMethod<Double, Double> {
    private ArrayList<Double> x;
    private ArrayList<Double> y;
    private double[] m;


    public SplineInterpolation() {
        x = new ArrayList<>();
        y = new ArrayList<>();
    }

    public SplineInterpolation(List<BgReading> readings) {
        for (BgReading reading : readings) {
            x.add(reading.getX());
            y.add(reading.getY());
        }
        recalculateParameters();
    }

    private void recalculateParameters() {
        if (this.x.size() <= 2) {
            throw new IllegalArgumentException("There must be at least two control points");
        }

        final int n = x.size();
        double[] d = new double[n - 1];
        double[] m = new double[n];

        // Compute slopes of secant lines between successive points
        for (int i = 0; i < n - 1; i++) {
            double h = x.get(i + 1) - x.get(i);
            if (h <= 0d) {
                throw new IllegalArgumentException("The control points must all have strictly " +
                        "increasing X values.");
            }
            d[i] = (y.get(i + 1) - y.get(i)) / h;
        }

        // Initialize the tangents as the average of the secants
        m[0] = d[0];
        for (int i = 1; i < n - 1; i++) {
            m[i] = (d[i - 1] + d[i]) * 0.5d;
        }
        m[n - 1] = d[n - 2];

        // Update the tangents to preserve monotonicity
        for (int i = 0; i < n - 1; i++) {
            if (d[i] == 0d) { // successive Y values are equal
                m[i] = 0d;
                m[i + 1] = 0d;
            } else {
                double a = m[i] / d[i];
                double b = m[i + 1] / d[i];
                double h = Math.hypot(a, b);
                if (h > 9d) {
                    double t = 3d / h;
                    m[i] = t * a * d[i];
                    m[i + 1] = t * b * d[i];
                }
            }
        }

        this.m = m;
    }

    @Override
    public void addDatapoint(Double x, Double y) {
        this.x.add(x);
        this.y.add(y);
        if (this.x.size() > 2) {
            recalculateParameters();
        }
    }

    @Override
    public Double getValueAt(Double x) {
        // Handle the boundary cases
        final int n = this.x.size();
        if (Double.isNaN(x)) {
            return x;
        }
        if (x <= this.x.get(0)) {
            return y.get(0);
        }
        if (x >= this.x.get(n - 1)) {
            return y.get(n - 1);
        }

        // Find the index `i` of the last point with smaller X.
        // We know this will be within the spline due to the boundary tests
        int i = 0;
        while (x >= this.x.get(i + 1)) {
            i += 1;
            if (x.equals(this.x.get(i))) {
                return y.get(i);
            }
        }

        // Perform the cubic Hermite spline interpolation
        double h = this.x.get(i + 1) - this.x.get(i);
        double t = (x - this.x.get(i)) / h;
        return    (y.get(i)     * (1 + 2 * t) + h * m[i]     *  t     ) * (1 - t)  * (1 - t)
                + (y.get(i + 1) * (3 - 2 * t) + h * m[i + 1] * (t - 1)) *  t       *  t;
    }
}
