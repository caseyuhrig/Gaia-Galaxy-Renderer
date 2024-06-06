package caseyuhrig.image;

public class Gradient {

    /**
     * Interpolates between two RGB values based on the given percentage.
     *
     * @param startRgb   The starting RGB color as an array [r, g, b].
     * @param endRgb     The ending RGB color as an array [r, g, b].
     * @param percentage The interpolation percentage (0.0 to 1.0).
     * @return The interpolated RGB color as an array [r, g, b].
     */
    public static double[] interpolateRgb(final double[] startRgb, final double[] endRgb, final double percentage) {
        final double[] interpolatedRgb = new double[3];

        for (int i = 0; i < 3; i++) {
            interpolatedRgb[i] = startRgb[i] + percentage * (endRgb[i] - startRgb[i]);
        }

        return interpolatedRgb;
    }

    /**
     * Interpolates between two RGB values based on the given position within a range.
     *
     * @param startRgb The starting RGB color as an array [r, g, b].
     * @param endRgb   The ending RGB color as an array [r, g, b].
     * @param min      The minimum value of the range.
     * @param max      The maximum value of the range.
     * @param position The current position within the range.
     * @return The interpolated RGB color as an array [r, g, b].
     */
    public static double[] interpolateRgb(final double[] startRgb, final double[] endRgb, final double min, final double max, final double position) {
        if (min == max) {
            throw new IllegalArgumentException("min and max cannot be the same value");
        }
        final double percentage = (position - min) / (max - min);
        return interpolateRgb(startRgb, endRgb, percentage);
    }

}
