package caseyuhrig.image;

public class ColorConverter {

    /**
     * Converts CMY color values to RGB color values.
     *
     * @param c The cyan component (0.0 to 1.0).
     * @param m The magenta component (0.0 to 1.0).
     * @param y The yellow component (0.0 to 1.0).
     * @return The RGB color values as an array [r, g, b] (0.0 to 1.0).
     */
    public static double[] cmyToRgb(final double c, final double m, final double y) {
        final double r = 1.0 - c;
        final double g = 1.0 - m;
        final double b = 1.0 - y;
        return new double[]{r, g, b};
    }


    /**
     * Converts CMYK color values to RGB color values.
     *
     * @param c The cyan component (0.0 to 1.0).
     * @param m The magenta component (0.0 to 1.0).
     * @param y The yellow component (0.0 to 1.0).
     * @param k The key/black component (0.0 to 1.0).
     * @return The RGB color values as an array [r, g, b] (0.0 to 1.0).
     */
    public static double[] cmykToRgb(final double c, final double m, final double y, final double k) {
        final double r = 1.0 - Math.min(1.0, c * (1.0 - k) + k);
        final double g = 1.0 - Math.min(1.0, m * (1.0 - k) + k);
        final double b = 1.0 - Math.min(1.0, y * (1.0 - k) + k);
        return new double[]{r, g, b};
    }


    /**
     * Converts RGB color values to CMY color values.
     *
     * @param r The red component (0.0 to 1.0).
     * @param g The green component (0.0 to 1.0).
     * @param b The blue component (0.0 to 1.0).
     * @return The CMY color values as an array [c, m, y] (0.0 to 1.0).
     */
    public static double[] rgbToCmy(final double r, final double g, final double b) {
        final double c = 1.0 - r;
        final double m = 1.0 - g;
        final double y = 1.0 - b;
        return new double[]{c, m, y};
    }

    /**
     * Converts RGB color values to CMYK color values.
     *
     * @param r The red component (0.0 to 1.0).
     * @param g The green component (0.0 to 1.0).
     * @param b The blue component (0.0 to 1.0).
     * @return The CMYK color values as an array [c, m, y, k] (0.0 to 1.0).
     */
    public static double[] rgbToCmyk(final double r, final double g, final double b) {
        final double k = 1.0 - Math.max(r, Math.max(g, b));
        if (k == 1.0) { // Black color
            return new double[]{0.0, 0.0, 0.0, 1.0};
        }
        final double c = (1.0 - r - k) / (1.0 - k);
        final double m = (1.0 - g - k) / (1.0 - k);
        final double y = (1.0 - b - k) / (1.0 - k);
        return new double[]{c, m, y, k};
    }

}
