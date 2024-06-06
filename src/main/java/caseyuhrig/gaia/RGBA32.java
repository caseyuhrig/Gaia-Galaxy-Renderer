package caseyuhrig.gaia;

public class RGBA32 {

    public double red;
    public double green;
    public double blue;
    public double alpha;


    public RGBA32(final double red, final double green, final double blue, final double alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }


    /**
     * Inverts the color. This is done by subtracting the color from 1.0.
     * Does not affect the alpha channel.
     *
     * @return the inverted color.
     */
    public RGBA32 invert() {
        return new RGBA32(1.0 - red, 1.0 - green, 1.0 - blue, alpha);
    }

    public static RGBA32 of(final double red, final double green, final double blue) {
        return new RGBA32(red, green, blue, 1.0);
    }

    public RGBA8 toRGBA8() {
        final int r = (int) Math.round(red * 255.0);
        final int g = (int) Math.round(green * 255.0);
        final int b = (int) Math.round(blue * 255.0);
        final int a = (int) Math.round(alpha * 255.0);
        return new RGBA8(r, g, b, a);
    }


    public static double normalize(final double value, final double min, final double max) {
        return (value - min) / (max - min);
    }
}
