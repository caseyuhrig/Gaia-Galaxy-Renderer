package caseyuhrig.gaia.renderer;

import caseyuhrig.gaia.RGBA8;
import caseyuhrig.gaia.RenderingData;

public class CIELabPixelRenderer extends PixelRenderer {

    // White reference
    /**
     * see: https://en.wikipedia.org/wiki/CIELAB_color_space#From_CIEXYZ_to_CIELAB[10]
     */
    private static final double REF_X = 95.047; // Observer= 2°, Illuminant= D65

    /**
     * see: https://en.wikipedia.org/wiki/CIELAB_color_space#From_CIEXYZ_to_CIELAB[10]
     */
    private static final double REF_Y = 100.000;

    /**
     * see: https://en.wikipedia.org/wiki/CIELAB_color_space#From_CIEXYZ_to_CIELAB[10]
     */
    private static final double REF_Z = 108.883;

    /**
     * see: https://en.wikipedia.org/wiki/CIELAB_color_space#From_CIEXYZ_to_CIELAB[10]
     */
    private static final double XYZ_m = 7.787037; // match in slope. Note commonly seen 7.787 gives worse results

    /**
     * see: https://en.wikipedia.org/wiki/CIELAB_color_space#From_CIEXYZ_to_CIELAB[10]
     */
    private static final double XYZ_t0 = 0.008856;


    private final RGBA8[][] samples;
    private final int[][] counts;

    private final double min_phot_bp_mean_mag = 2.3980012;
    private final double max_phot_bp_mean_mag = 25.333084;
    private final double min_phot_rp_mean_mag = 1.7436333;
    private final double max_phot_rp_mean_mag = 24.695997;

    private final double min_phot_g_mean_mag = 1.731607;
    private final double max_phot_g_mean_mag = 22.956425;
    // calculated from the data, saved post run.


    public RGBA8 calculateNewColor(final double phot_rp_mean_mag, final double phot_g_mean_mag, final double phot_bp_mean_mag) {
        final double r = normalize(phot_rp_mean_mag, min_phot_rp_mean_mag, max_phot_rp_mean_mag);
        final double g = normalize(phot_g_mean_mag, min_phot_g_mean_mag, max_phot_g_mean_mag);
        final double b = normalize(phot_bp_mean_mag, min_phot_bp_mean_mag, max_phot_bp_mean_mag);

        final int red = (int) Math.round(r * 255);
        final int green = (int) Math.round(g * 255);
        final int blue = (int) Math.round(b * 255);

        final var color = new RGBA8(red, green, blue, 255);
        return color;
    }

    public static double normalize(final double value, final double min, final double max) {
        return (value - min) / (max - min);
    }

    public static RGBA8 magToRGB(final double gMag, final double bpMag, final double rpMag) {
        // 1. Convert magnitudes to relative flux densities
        final double fG = Math.pow(10, -0.4 * gMag);
        final double fBP = Math.pow(10, -0.4 * bpMag);
        final double fRP = Math.pow(10, -0.4 * rpMag);

        // 2. Approximate color using simplified transformations
        final double r = Math.max(0.0, Math.min(1.0, 1.5 - fRP / fG));  // Red
        final double g = Math.max(0.0, Math.min(1.0, 1.8 - (fBP / fG) - (fRP / fG)));  // Green
        final double b = Math.max(0.0, Math.min(1.0, 2.5 - fBP / fG)); // Blue

        // 3. Scale to 0-255 range for RGB
        final int R = (int) (r * 255);
        final int G = (int) (g * 255);
        final int B = (int) (b * 255);

        return new RGBA8(R, G, B, 255);
    }

    private static final double PARALLAX_ERROR_THRESHOLD = 0.1; // Example threshold

    public static RGBA8 magToRGB2(double gMag, double bpMag, double rpMag, final double parallax, final double parallaxError) {

        final double background = 200.0; // Example background value
        // 0. Background Subtraction
        gMag = gMag - background;
        bpMag = bpMag - background;
        rpMag = rpMag - background;

        // 1. Convert magnitudes to relative flux densities
        final double fG = Math.pow(10, -0.4 * gMag);
        final double fBP = Math.pow(10, -0.4 * bpMag);
        final double fRP = Math.pow(10, -0.4 * rpMag);

        // 2. Estimate stellar temperature with parallax correction
        final double bpMinusRp = bpMag - rpMag;
        double temperature = 0.0;

        if (parallaxError > PARALLAX_ERROR_THRESHOLD) {
            // Handle the case of unreliable parallax
            // You might return a default color (e.g., white) here
            //    System.out.println("Warning: Parallax error is high - color might be inaccurate");
            return new RGBA8(255, 255, 255, 255);
        } else {
            // Simplified temperature estimation with parallax adjustment
            temperature = 4500.0 / (0.92 * bpMinusRp + 1.7) + 1400
                    + 500.0 * (1.0 / parallax - 1.0);

            // Adjust temperature uncertainty based on parallaxError
            final double tempUncertainty = 500.0 * (parallaxError / parallax);
            temperature += Math.random() * (2 * tempUncertainty) - tempUncertainty;
        }

        // 3. Color transformation based on temperature
        double r = 0.0;
        double g = 0.0;
        double b = 0.0;

        if (temperature <= 4000) {
            r = 1.0;
        } else if (temperature <= 7500) {
            r = (temperature - 4000.0) / 3500.0;
            g = 1.0;
        } else {
            b = (temperature - 7500.0) / 15000.0;
            // makes all the little stars show up
            return new RGBA8(r, g, b, 1);
        }


        // 4. Scale to 0-255 range for RGB
        /*
        final int R = (int) (r * 255);
        final int G = (int) (g * 255);
        final int B = (int) (b * 255);

        return new RGBA8(R, G, B, 255);
         */
        // 5. Apply log scaling and adjust range for visual appeal
        //final RGBA8 out = log10Scale(r, g, b, 50.0);

        // 6. Adjust intensity for better visibility
        //final double intensityFactor = 5.0; //2.5; // Adjust for desired intensity
        //out.red = (int) Math.round((double) out.red * intensityFactor);
        //out.green = (int) Math.round((double) out.green * intensityFactor);
        //out.blue = (int) Math.round((double) out.blue * intensityFactor);
        //return out;
        //return adjustColor(new RGBA8(r, g, b, 1), 0.5, 0.0, -0.25);
        return new RGBA8(r, g, b, 1);
        //}
    }

    public static RGBA8 adjustColor(final RGBA8 color, final double redFactor, final double greenFactor, final double blueFactor) {
        int red = color.red;
        int green = color.green;
        int blue = color.blue;

        // Adjust red and green values to increase blue/magenta tone
        red = (int) (red * (1 - redFactor));
        green = (int) (green * (1 - greenFactor));
        blue = (int) (blue * (1 - blueFactor));

        // Ensure the adjusted values are within the valid range [0, 255]
        red = Math.min(255, Math.max(0, red));
        green = Math.min(255, Math.max(0, green));
        green = 0;
        blue = Math.min(255, Math.max(0, blue));

        return new RGBA8(red, green, blue);
    }

    public int convertLabToRGB(final double gMag, final double rpMag, final double bpMag) {

        // Define the white point using CIE standard illuminant E
        final double[] illuminantE = {1.0, 1.0, 1.0};

        // Define the minimum and maximum values for each magnitude
        //final double minG = 10.0;
        //final double maxG = 20.0;
        //final double minBP = 10.0;
        //final double maxBP = 20.0;
        //final double minRP = 10.0;
        //final double maxRP = 20.0;

        // Normalize the magnitudes
        final double normalizedG = (gMag - min_phot_g_mean_mag) / (max_phot_g_mean_mag - min_phot_g_mean_mag);
        final double normalizedBP = (bpMag - min_phot_bp_mean_mag) / (max_phot_bp_mean_mag - min_phot_bp_mean_mag);
        final double normalizedRP = (rpMag - min_phot_rp_mean_mag) / (max_phot_rp_mean_mag - min_phot_rp_mean_mag);

        // Convert normalized magnitudes to CIE L*a*b* color space
        final double[] lab = {
                normalizedG * 100.0,
                (normalizedBP - 0.5) * 256.0,
                (normalizedRP - 0.5) * 256.0
        };

        // Convert CIE L*a*b* to sRGB color space
        final int argb = convertCIELabToARGBTest((int) lab[0], (int) lab[2], (int) lab[1]);

        // Create a Color object from the RGB values
        //final Color color = new Color(rgb[0], rgb[1], rgb[2]);

        return argb;
    }

    public int convertCIELabToARGBTest(final int cieL, final int cieA, final int cieB) {
        final double X;
        final double Y;
        final double Z;
        {

            double var_Y = ((cieL * 100.0 / 255.0) + 16.0) / 116.0;
            double var_X = cieA / 500.0 + var_Y;
            double var_Z = var_Y - cieB / 200.0;

            var_X = unPivotXYZ(var_X);
            var_Y = unPivotXYZ(var_Y);
            var_Z = unPivotXYZ(var_Z);

            X = REF_X * var_X; // REF_X = 95.047 Observer= 2°, Illuminant= D65
            Y = REF_Y * var_Y; // REF_Y = 100.000
            Z = REF_Z * var_Z; // REF_Z = 108.883

        }

        final double R;
        final double G;
        final double B;
        {
            final double var_X = X / 100; // X = From 0 to REF_X
            final double var_Y = Y / 100; // Y = From 0 to REF_Y
            final double var_Z = Z / 100; // Z = From 0 to REF_Y

            double var_R = var_X * 3.2406 + var_Y * -1.5372 + var_Z * -0.4986;
            double var_G = var_X * -0.9689 + var_Y * 1.8758 + var_Z * 0.0415;
            double var_B = var_X * 0.0557 + var_Y * -0.2040 + var_Z * 1.0570;

            var_R = pivotRGB(var_R);
            var_G = pivotRGB(var_G);
            var_B = pivotRGB(var_B);

            R = (var_R * 255);
            G = (var_G * 255);
            B = (var_B * 255);
        }

        return convertRGBtoARGB(R, G, B);
        //return new int[] {(int) R, (int) G, (int) B, 255};
        /*
        int red = (int) Math.round(R);
        int green = (int) Math.round(G);
        int blue = (int) Math.round(B);

        red = Math.min(255, Math.max(0, red));
        green = Math.min(255, Math.max(0, green));
        blue = Math.min(255, Math.max(0, blue));
        final int alpha = 0xff;

        return new int[]{red, green, blue, alpha};
        */
    }

    private int convertRGBtoARGB(final double R, final double G, final double B) {
        int red = (int) Math.round(R);
        int green = (int) Math.round(G);
        int blue = (int) Math.round(B);

        red = Math.min(255, Math.max(0, red));
        green = Math.min(255, Math.max(0, green));
        blue = Math.min(255, Math.max(0, blue));

        final int alpha = 0xff;

        return (alpha << 24) | (red << 16) | (green << 8) | (blue << 0);
    }

    private static double pivotRGB(double n) {
        if (n > 0.0031308) {
            n = 1.055 * Math.pow(n, 1 / 2.4) - 0.055;
        } else {
            n = 12.92 * n;
        }
        return n;
    }

    private static double unPivotRGB(double n) {
        if (n > 0.04045) {
            n = Math.pow((n + 0.055) / 1.055, 2.4);
        } else {
            n = n / 12.92;
        }
        return n;
    }

    private static double pivotXYZ(double n) {
        if (n > XYZ_t0) {
            n = Math.pow(n, 1 / 3.0);
        } else {
            n = XYZ_m * n + 16 / 116.0;
        }
        return n;
    }

    private static double unPivotXYZ(double n) {
        final double nCube = Math.pow(n, 3);
        if (nCube > XYZ_t0) {
            n = nCube;
        } else {
            n = (n - 16 / 116.0) / XYZ_m;
        }
        return n;
    }

    public CIELabPixelRenderer(final int width, final int height, final int scale) {
        super(width, height, scale);
        samples = new RGBA8[width][height];
        counts = new int[width][height];
        // zero out the colors
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                samples[x][y] = new RGBA8(0, 0, 0, 0);
                counts[x][y] = 0;
            }
        }
    }

    @Override
    public RGBA8 renderPixel(final int x, final int y, final RenderingData data) {
        if (data.phot_bp_mean_mag == null || data.phot_rp_mean_mag == null || data.phot_g_mean_mag == null || data.parallax == null) {
            return null;
        }
        //final var color = calculateNewColor(data.phot_rp_mean_mag, data.phot_g_mean_mag, data.phot_bp_mean_mag);
        //final var color = magToRGB(phot_g_mean_mag, phot_bp_mean_mag, phot_rp_mean_mag);
        //--> GOOD
        //final var color = magToRGB2(data.phot_g_mean_mag, data.phot_bp_mean_mag, data.phot_rp_mean_mag, data.parallax, data.parallax_error);
        //final var color = GaiaMagnitudeConverter.calculateColor(data.phot_rp_mean_mag, data.phot_g_mean_mag, data.phot_bp_mean_mag, data.parallax, data.parallax_error);
        //final var color = new RGBA8(convertLabToRGB(data.phot_g_mean_mag.intValue(), data.phot_rp_mean_mag.intValue(), data.phot_bp_mean_mag.intValue())).scale(100.0);
        // L A B
        final var cieL = data.phot_g_mean_mag.intValue();
        final var cieA = data.phot_rp_mean_mag.intValue();
        final var cieB = data.phot_bp_mean_mag.intValue();
        final RGBA8 color = new RGBA8(convertCIELabToARGBTest(cieL, cieA, cieB));

        //samples[x][y].add(temperatureColor);
        try {
            samples[x][y].red += color.red;
            samples[x][y].green += color.green;
            samples[x][y].blue += color.blue;
        } catch (final Exception e) {
            final var message = "Index: " + x + "x" + y + " out of bounds: " + samples.length + "x" + samples[0].length + " " + e.getLocalizedMessage();
            System.err.println(message);
            throw new IndexOutOfBoundsException(message);
        }

        //System.out.println("1----------> " + x + "x" + y + " Count: " + counts[x][y] + " Alpha: " + samples[x][y].alpha);

        counts[x][y]++;
        samples[x][y].alpha++;

        if (samples[x][y].alpha != counts[x][y]) {
            System.out.println("2----------> " + x + "x" + y + "  Count: " + counts[x][y] + " Alpha: " + samples[x][y].alpha);
            throw new IllegalStateException("Alpha and count mismatch");
        }
        //final int count = samples[x][y].alpha; //counts[x][y] + 1;
        //final int color = (int) Math.round((double) samples[x][y] / (double) MAX_SAMPLES * 255.0);
        //final int red = (int) Math.round((double) samples[x][y].red / (double) count);
        //final int green = (int) Math.round((double) samples[x][y].green / (double) count);
        //final int blue = (int) Math.round((double) samples[x][y].blue / (double) count);

        final double count = getMaxSamples(); // /4
        //final double count = counts[x][y];
        //final double count = 500;
        final int red = (int) Math.round((double) samples[x][y].red / count);
        final int green = (int) Math.round((double) samples[x][y].green / count);
        final int blue = (int) Math.round((double) samples[x][y].blue / count);


        if (data.parallax != null) {
            // 2. Estimate stellar temperature with parallax correction
            //final double bpMinusRp = bpMag - rpMag;
            final double bpMinusRp = data.phot_bp_mean_mag - data.phot_rp_mean_mag;
            final double parallaxError = data.parallax_error;
            final double parallax = data.parallax;

            double temperature = 0.0;

            if (parallaxError > PARALLAX_ERROR_THRESHOLD) {
                // Handle the case of unreliable parallax
                // You might return a default color (e.g., white) here
                //    System.out.println("Warning: Parallax error is high - color might be inaccurate");
                //return new RGBA8(255, 255, 255, 255);
            } else {
                // Simplified temperature estimation with parallax adjustment
                temperature = 4500.0 / (0.92 * bpMinusRp + 1.7) + 1400
                        + 500.0 * (1.0 / parallax - 1.0);

                // Adjust temperature uncertainty based on parallaxError
                final double tempUncertainty = 500.0 * (parallaxError / parallax);
                temperature += Math.random() * (2 * tempUncertainty) - tempUncertainty;
            }

            // 3. Color transformation based on temperature
            double r = 0.0;
            final double g = 0.0;
            double b = 0.0;

            if (temperature <= 4000) {
                r = 1.0;
                //return new RGBA8(r, g, b, 1);
            } else if (temperature <= 7500) {
                //r = (temperature - 4000.0) / 3500.0;
                //g = 1.0;
            } else {
                b = (temperature - 7500.0) / 15000.0;
                // makes all the little stars show up
                //return new RGBA8(r, g, b, 1);
            }
            //return new RGBA8(r, g, b, 1.0);
        }


        final var result = new RGBA8(red, green, blue, 255);
        //final int color = (int) Math.round((double) samples[x][y] / (double) MAX_SAMPLES * 255.0);
        //return new RGBA8(color, color, color, 255);
        return result; //temperatureColor; //result;
    }

    public int getMaximumCount() {
        int max = 0;
        for (int x = 0; x < counts.length; x++) {
            for (int y = 0; y < counts[x].length; y++) {
                if (counts[x][y] > max) {
                    max = counts[x][y];
                }
            }
        }
        return max;
    }


    @Override
    public void printStatistics() {
        System.out.println("CIELab Renderer");
        System.out.println("Max Samples: " + getMaxSamples());
        System.out.println("Maximum Sample Count: " + getMaximumCount());
    }
}
