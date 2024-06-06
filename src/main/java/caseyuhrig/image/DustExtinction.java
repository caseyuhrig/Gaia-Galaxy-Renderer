package caseyuhrig.image;

public class DustExtinction {
    public static double[] applyDustExtinction(final double[] rgb, final double av) {
        final double rv = 3.1; // Average value for Rv
        final double[] extCoeff = {0.574 * Math.pow(5500 / 4400, 1.61), 1.0, 0.442 * Math.pow(1, 1.61)};

        final double r = rgb[0] * Math.pow(10, -0.4 * extCoeff[0] * av / rv);
        final double g = rgb[1] * Math.pow(10, -0.4 * extCoeff[1] * av / rv);
        final double b = rgb[2] * Math.pow(10, -0.4 * extCoeff[2] * av / rv);

        return new double[]{r, g, b};
    }
}
