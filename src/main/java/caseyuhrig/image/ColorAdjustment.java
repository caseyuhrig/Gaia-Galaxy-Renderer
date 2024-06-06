package caseyuhrig.image;

import java.awt.*;

public class ColorAdjustment {
    // Function to adjust the saturation of a color
    public static double[] adjustSaturation(final double[] rgb, final double saturationFactor) {
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

    public static void main(final String[] args) {
        // Example RGB color
        final double[] rgb = {0.8, 0.5, 0.2}; // Example color

        // Adjust saturation to 50%
        final double[] adjustedRgb = adjustSaturation(rgb, 0.5);
        System.out.printf("Adjusted RGB: (%.3f, %.3f, %.3f)%n", adjustedRgb[0], adjustedRgb[1], adjustedRgb[2]);

        // Convert adjusted RGB to a Java Color object for visualization
        final Color color = new Color((float) adjustedRgb[0], (float) adjustedRgb[1], (float) adjustedRgb[2]);
        System.out.println("Java Color: " + color);
    }
}
