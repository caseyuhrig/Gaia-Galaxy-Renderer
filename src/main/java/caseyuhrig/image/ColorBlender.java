package caseyuhrig.image;

public class ColorBlender {

    /**
     * Blend two RGB colors.
     *
     * @param rgb1   The first RGB color array.
     * @param rgb2   The second RGB color array.
     * @param weight The weight for blending. This determines the influence of each color.
     *               - weight = 0: The final color is completely rgb2 (0% rgb1, 100% rgb2).
     *               - weight = 1: The final color is completely rgb1 (100% rgb1, 0% rgb2).
     *               - weight = 0.7: The final color is 70% rgb1 and 30% rgb2.
     *               - weight = 0.3: The final color is 30% rgb1 and 70% rgb2.
     *               - 0 < weight < 1: The final color is a mix of rgb1 and rgb2 based on the weight.
     * @return The blended RGB color array.
     */
    public static double[] blendColors(final double[] rgb1, final double[] rgb2, final double weight) {
        final double r = (rgb1[0] * weight + rgb2[0] * (1 - weight));
        final double g = (rgb1[1] * weight + rgb2[1] * (1 - weight));
        final double b = (rgb1[2] * weight + rgb2[2] * (1 - weight));

        return new double[]{clamp(r, 0, 1), clamp(g, 0, 1), clamp(b, 0, 1)};
    }

    private static double clamp(final double value, final double min, final double max) {
        return Math.max(min, Math.min(max, value));
    }
}
