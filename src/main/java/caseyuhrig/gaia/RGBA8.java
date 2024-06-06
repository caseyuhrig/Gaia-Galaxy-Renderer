package caseyuhrig.gaia;

import java.awt.*;

public class RGBA8 {

    public static final RGBA8 CLEAR = new RGBA8(0, 0, 0, 0);
    public static final RGBA8 BLACK = new RGBA8(0, 0, 0, 255);
    public static final RGBA8 WHITE = new RGBA8(255, 255, 255, 255);

    public int red;
    public int green;
    public int blue;
    public int alpha;


    public RGBA8(final int color) {
        this.alpha = (color >> 24) & 0xFF;
        this.red = (color >> 16) & 0xFF;
        this.green = (color >> 8) & 0xFF;
        this.blue = color & 0xFF;
    }

    public RGBA8(final int red, final int green, final int blue, final int alpha) {
        this.red = Math.max(Math.min(red, 255), 0);
        this.green = Math.max(Math.min(green, 255), 0);
        this.blue = Math.max(Math.min(blue, 255), 0);
        this.alpha = Math.max(Math.min(alpha, 255), 0);
    }


    public RGBA8(final int red, final int green, final int blue) {
        this(red, green, blue, 255);
    }


    public RGBA8(final double red, final double green, final double blue) {
        this(red, green, blue, 1.0);
    }


    public RGBA8(final double red, final double green, final double blue, final double alpha) {
        this((int) (red * 255.0), (int) (green * 255.0), (int) (blue * 255.0), (int) (alpha * 255.0));
    }

    public RGBA8(final double[] rgb) {
        this(rgb[0], rgb[1], rgb[2]);
    }


    public double[] rgb() {
        return new double[]{red / 255.0, green / 255.0, blue / 255.0};
    }


    public RGBA8 log10Scale(final double factor) {
        final double r = red / 255.0;
        final double g = green / 255.0;
        final double b = blue / 255.0;
        final double logScaleFactor = 50.0; // Adjust for desired intensity stretching
        final int R = (int) (Math.log10(r * logScaleFactor + 1.0) * 255.0 / Math.log10(1.0 + logScaleFactor));
        final int G = (int) (Math.log10(g * logScaleFactor + 1.0) * 255.0 / Math.log10(1.0 + logScaleFactor));
        final int B = (int) (Math.log10(b * logScaleFactor + 1.0) * 255.0 / Math.log10(1.0 + logScaleFactor));
        return new RGBA8(R, G, B, 255);
    }

    public RGBA8 scale(final double factor) {
        final int R = (int) (red * factor);
        final int G = (int) (green * factor);
        final int B = (int) (blue * factor);
        return new RGBA8(R, G, B, 255);
    }

    // calculate the distance between two colors
    public double distance(final RGBA8 color) {
        final double redDistance = red - color.red;
        final double greenDistance = green - color.green;
        final double blueDistance = blue - color.blue;
        final double alphaDistance = alpha - color.alpha;
        return Math.sqrt(redDistance * redDistance + greenDistance * greenDistance + blueDistance * blueDistance + alphaDistance * alphaDistance);
    }

    /**
     * Inverts the color by subtracting each color component from 255.
     *
     * @return The inverted color
     */
    public RGBA8 invert() {
        return new RGBA8(255 - red, 255 - green, 255 - blue, alpha);
    }

    public RGBA8 average(final RGBA8 color) {
        return new RGBA8((red + color.red) / 2, (green + color.green) / 2, (blue + color.blue) / 2, (alpha + color.alpha) / 2);
    }

    public static RGBA8 brighter(final RGBA8 color1, final RGBA8 color2) {
        final double luminance1 = calculateLuminance(color1);
        final double luminance2 = calculateLuminance(color2);
        if (luminance1 > luminance2) {
            return color1;
        } else {
            return color2;
        }
    }

    public static double normalize(final double value, final double min, final double max) {
        return (value - min) / (max - min);
    }

    private static double calculateLuminance(final RGBA8 color) {
        double red = color.red / 255.0;
        double green = color.green / 255.0;
        double blue = color.blue / 255.0;

        // Gamma correction
        red = (red <= 0.03928) ? red / 12.92 : Math.pow((red + 0.055) / 1.055, 2.4);
        green = (green <= 0.03928) ? green / 12.92 : Math.pow((green + 0.055) / 1.055, 2.4);
        blue = (blue <= 0.03928) ? blue / 12.92 : Math.pow((blue + 0.055) / 1.055, 2.4);

        // Calculate luminance
        final double luminance = 0.2126 * red + 0.7152 * green + 0.0722 * blue;

        return luminance;
    }

    /**
     * Average the given colors together and return the result.
     *
     * @param colors The colors to average
     * @return The average color
     */
    public static RGBA8 average(final RGBA8... colors) {
        int r = 0;
        int g = 0;
        int b = 0;
        int a = 0;
        for (final var color : colors) {
            r += color.red;
            g += color.green;
            b += color.blue;
            a += color.alpha;
        }
        return new RGBA8(r / colors.length, g / colors.length, b / colors.length, a / colors.length);
    }

    public static RGBA8 ofTemperature(final double temperature) {
        final double temp = temperature / 100.0;

        double red, green, blue;

        if (temp <= 66) {
            red = 255;
        } else {
            red = temp - 60;
            red = 329.698727446 * Math.pow(red, -0.1332047592);
            red = Math.max(0, Math.min(255, red));
        }

        if (temp <= 66) {
            green = temp;
            green = 99.4708025861 * Math.log(green) - 161.1195681661;
            green = Math.max(0, Math.min(255, green));
        } else {
            green = temp - 60;
            green = 288.1221695283 * Math.pow(green, -0.0755148492);
            green = Math.max(0, Math.min(255, green));
        }

        if (temp >= 66) {
            blue = 255;
        } else if (temp <= 19) {
            blue = 0;
        } else {
            blue = temp - 10;
            blue = 138.5177312231 * Math.log(blue) - 305.0447927307;
            blue = Math.max(0, Math.min(255, blue));
        }
        return new RGBA8((int) red, (int) green, (int) blue, 255);
    }

    public int argb() {
        this.red = Math.max(Math.min(red, 255), 0);
        this.green = Math.max(Math.min(green, 255), 0);
        this.blue = Math.max(Math.min(blue, 255), 0);
        this.alpha = Math.max(Math.min(alpha, 255), 0);
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }


    public RGBA8 increaseContrast(final double contrast) {
        // Convert the color components to the range of 0.0 to 1.0
        final double normalizedRed = red / 255.0;
        final double normalizedGreen = green / 255.0;
        final double normalizedBlue = blue / 255.0;

        // Apply the contrast adjustment formula
        final double adjustedRed = Math.pow(normalizedRed, 1.0 / contrast);
        final double adjustedGreen = Math.pow(normalizedGreen, 1.0 / contrast);
        final double adjustedBlue = Math.pow(normalizedBlue, 1.0 / contrast);

        // Convert the adjusted color components back to the range of 0 to 255
        final int newRed = (int) (adjustedRed * 255);
        final int newGreen = (int) (adjustedGreen * 255);
        final int newBlue = (int) (adjustedBlue * 255);

        return new RGBA8(newRed, newGreen, newBlue, alpha);
    }

    public RGBA8 brighten(final float factor) {
        final float[] hsb = new float[3];
        Color.RGBtoHSB(red, green, blue, hsb);

        hsb[2] = hsb[2] * factor; //Math.min(1.0f, hsb[2] * factor);

        final int brightenedRGB = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
        final int brightenedColor = (alpha << 24) | (brightenedRGB & 0x00FFFFFF);
        // break out the channels
        final int brightenedRed = (brightenedColor >> 16) & 0xFF;
        final int brightenedGreen = (brightenedColor >> 8) & 0xFF;
        final int brightenedBlue = brightenedColor & 0xFF;

        return new RGBA8(brightenedRed, brightenedGreen, brightenedBlue, alpha);
    }

    public RGBA8 darken(final float threshold, final float darkenFactor) {

        final float[] hsv = new float[3];
        Color.RGBtoHSB(red, green, blue, hsv);

        final float brightness = hsv[2];

        if (brightness < threshold) {
            // Darken the color if it's below the threshold
            hsv[2] *= darkenFactor;
        }

        final int darkenedRGB = Color.HSBtoRGB(hsv[0], hsv[1], hsv[2]);
        final int darkenedColor = (alpha << 24) | (darkenedRGB & 0x00FFFFFF);
        // break out the channels
        final int darkenedRed = (darkenedColor >> 16) & 0xFF;
        final int darkenedGreen = (darkenedColor >> 8) & 0xFF;
        final int darkenedBlue = darkenedColor & 0xFF;

        return new RGBA8(darkenedRed, darkenedGreen, darkenedBlue, alpha);
    }

    /**
     * Saturates the color by increasing the saturation of the color's hue.
     *
     * @param factor The factor by which to saturate the color (0.0 to 1.0)
     * @return The saturated color
     */
    public RGBA8 saturate(final float factor) {
        final float[] hsv = new float[3];
        Color.RGBtoHSB(red, green, blue, hsv);

        hsv[1] = hsv[1] * factor; //Math.min(1.0f, hsv[1] * factor);

        final int saturatedRGB = Color.HSBtoRGB(hsv[0], hsv[1], hsv[2]);
        final int saturatedColor = (alpha << 24) | (saturatedRGB & 0x00FFFFFF);
        // break out the channels
        final int saturatedRed = (saturatedColor >> 16) & 0xFF;
        final int saturatedGreen = (saturatedColor >> 8) & 0xFF;
        final int saturatedBlue = saturatedColor & 0xFF;

        return new RGBA8(saturatedRed, saturatedGreen, saturatedBlue, alpha);
    }

    public RGBA8 enhanceContrast(final double contrastFactor) {
        final int newRed = enhanceContrast(red, contrastFactor);
        final int newGreen = enhanceContrast(green, contrastFactor);
        final int newBlue = enhanceContrast(blue, contrastFactor);
        return new RGBA8(newRed, newGreen, newBlue, alpha);
    }

    private static int enhanceContrast(final int color, final double contrastFactor) {
        double enhanced = (color / 255.0 - 0.5) * contrastFactor + 0.5;
        enhanced = Math.max(0.0, Math.min(1.0, enhanced));
        return (int) Math.round(enhanced * 255);
    }


    // adjustSaturation function that works with values between 0 and 255
    public RGBA8 adjustSaturation(final double saturationFactor) {
        // Convert RGB to HSL
        final double[] hsl = rgbToHsl(red / 255.0, green / 255.0, blue / 255.0);

        // Adjust the saturation
        hsl[1] *= saturationFactor;
        if (hsl[1] > 1.0f) {
            hsl[1] = 1.0f;
        }

        // Convert HSL back to RGB
        final double[] adjustedRgb = hslToRgb(hsl[0], hsl[1], hsl[2]);
        return new RGBA8(adjustedRgb[0] * 255, adjustedRgb[1] * 255, adjustedRgb[2] * 255, alpha);
    }

    private static double[] adjustSaturation(final double[] rgb, final double saturationFactor) {
        // Convert RGB to HSL
        final double[] hsl = rgbToHsl(rgb[0], rgb[1], rgb[2]);

        // Adjust the saturation
        hsl[1] *= saturationFactor;
        if (hsl[1] > 1.0f) {
            hsl[1] = 1.0f;
        }

        // Convert HSL back to RGB
        final double[] adjustedRgb = hslToRgb(hsl[0], hsl[1], hsl[2]);
        return adjustedRgb;
    }

    // Convert RGB to HSL
    private static double[] rgbToHsl(double r, double g, double b) {
        r = clamp(r, 0, 1);
        g = clamp(g, 0, 1);
        b = clamp(b, 0, 1);

        final double max = Math.max(r, Math.max(g, b));
        final double min = Math.min(r, Math.min(g, b));
        double h;
        final double s;
        final double l;
        l = (max + min) / 2.0f;

        if (max == min) {
            h = s = 0.0f; // achromatic
        } else {
            final double d = max - min;
            s = l > 0.5f ? d / (2.0f - max - min) : d / (max + min);

            if (max == r) {
                h = (g - b) / d + (g < b ? 6.0f : 0.0f);
            } else if (max == g) {
                h = (b - r) / d + 2.0f;
            } else {
                h = (r - g) / d + 4.0f;
            }

            h /= 6.0f;
        }

        return new double[]{h, s, l};
    }

    // Convert HSL to RGB
    private static double[] hslToRgb(final double h, final double s, final double l) {
        final double r;
        final double g;
        final double b;

        if (s == 0.0f) {
            r = g = b = l; // achromatic
        } else {
            final double q = l < 0.5f ? l * (1.0 + s) : l + s - l * s;
            final double p = 2.0 * l - q;
            r = hueToRgb(p, q, h + 1.0f / 3.0f);
            g = hueToRgb(p, q, h);
            b = hueToRgb(p, q, h - 1.0f / 3.0f);
        }

        return new double[]{r, g, b};
    }

    private static double hueToRgb(final double p, final double q, double t) {
        if (t < 0.0) t += 1.0;
        if (t > 1.0) t -= 1.0;
        if (t < 1.0 / 6.0) return p + (q - p) * 6.0 * t;
        if (t < 1.0 / 2.0) return q;
        if (t < 2.0 / 3.0) return p + (q - p) * (2.0 / 3.0 - t) * 6.0;
        return p;
    }

    private static double clamp(final double value, final double min, final double max) {
        return Math.max(min, Math.min(max, value));
    }

    private static int clamp(final int value, final int min, final int max) {
        return Math.max(min, Math.min(max, value));
    }


    public RGBA8 copy() {
        return new RGBA8(red, green, blue, alpha);
    }

    public RGBA32 toRGBA32() {
        final double r = (double) red / 255.0;
        final double g = (double) green / 255.0;
        final double b = (double) blue / 255.0;
        final double a = (double) alpha / 255.0;
        return new RGBA32(r, g, b, a);
    }

    public java.awt.Color toColor() {
        return new Color(red, green, blue, alpha);
    }

    public String toString() {
        return "RGBA8(" + red + ", " + green + ", " + blue + ", " + alpha + ")";
    }

    public static RGBA8 from(final Color color) {
        return new RGBA8(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }
}
