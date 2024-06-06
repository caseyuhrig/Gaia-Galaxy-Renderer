package caseyuhrig.gaia.renderer;

import caseyuhrig.gaia.*;

/**
 * gaia=# SELECT min(ra), max(ra), min(dec), max(dec) from source;
 * min           |       max        |        min         |        max
 * ------------------------+------------------+--------------------+-------------------
 * 3.4096239126626443e-07 | 359.999999939548 | -89.99287859590359 | 89.99005196682685
 * (1 row)
 * <p>
 * gaia=# SELECT min(parallax), max(parallax) from source;
 * min         |        max
 * ---------------------+-------------------
 * -187.02939637423492 | 768.0665391873573
 * (1 row)
 */
public class MagPixelRenderer extends PixelRenderer {

    private final RGBA8[][] samples;
    private final int[][] counts;


    public RGBA8 calculateNewColor(final double phot_rp_mean_mag, final double phot_g_mean_mag, final double phot_bp_mean_mag) {
        final double r = normalize(phot_rp_mean_mag, GaiaConst.min_phot_rp_mean_mag, GaiaConst.max_phot_rp_mean_mag);
        final double g = normalize(phot_g_mean_mag, GaiaConst.min_phot_g_mean_mag, GaiaConst.max_phot_g_mean_mag);
        final double b = normalize(phot_bp_mean_mag, GaiaConst.min_phot_bp_mean_mag, GaiaConst.max_phot_bp_mean_mag);

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

    private static double calculateMeanWavelength(final double[] passband) {
        double sum = 0.0;
        for (final double wavelength : passband) {
            sum += wavelength;
        }
        return sum / passband.length;
    }

    public static RGBA8 convertToRGB(final double gMag, final double bpMag, final double rpMag) {
        // Define the white point using CIE standard illuminant E
        final double[] illuminantE = {1.0, 1.0, 1.0};

        final double gMeanWavelength = 673.0;
        final double bpMeanWavelength = 532.0;
        final double rpMeanWavelength = 797.0;

        // Calculate the mean wavelengths for each passband
        //final double gMeanWavelength = calculateMeanWavelength(gPassband);
        //final double bpMeanWavelength = calculateMeanWavelength(bpPassband);
        //final double rpMeanWavelength = calculateMeanWavelength(rpPassband);

        // Convert magnitudes to fluxes using the mean wavelengths
        final double gFlux = Math.pow(10, -0.4 * gMag);
        final double bpFlux = Math.pow(10, -0.4 * bpMag);
        final double rpFlux = Math.pow(10, -0.4 * rpMag);

        // Calculate the CIE XYZ tristimulus values
        final double X = gFlux * gMeanWavelength + bpFlux * bpMeanWavelength + rpFlux * rpMeanWavelength;
        final double Y = gFlux * gMeanWavelength + bpFlux * bpMeanWavelength + rpFlux * rpMeanWavelength;
        final double Z = gFlux * gMeanWavelength + bpFlux * bpMeanWavelength + rpFlux * rpMeanWavelength;

        // Normalize the XYZ values
        final double sum = X + Y + Z;
        final double x = X / sum;
        final double y = Y / sum;

        // Convert CIE xyY to CIE L*a*b*
        final double L = 116 * Math.pow(y, 1.0 / 3.0) - 16;
        final double a = 500 * (Math.pow(x, 1.0 / 3.0) - Math.pow(y, 1.0 / 3.0));
        final double b = 200 * (Math.pow(y, 1.0 / 3.0) - Math.pow(1 - x - y, 1.0 / 3.0));

        // Convert CIE L*a*b* to sRGB color space
        final RGBA8 rgb = convertCIELabToRGB(L, a, b);
        return rgb;
        // Create a Color object from the RGB values
        //final Color color = new Color(rgb[0], rgb[1], rgb[2]);

        //return color;
    }

    public static RGBA8 convertCIELabToRGB(final double L, final double a, final double bb) {
        final double[] xyz = convertCIELabToXYZ(L, a, bb);
        final double x = xyz[0];
        final double y = xyz[1];
        final double z = xyz[2];

        double r = x * 3.2406 + y * -1.5372 + z * -0.4986;
        double g = x * -0.9689 + y * 1.8758 + z * 0.0415;
        double b = x * 0.0557 + y * -0.2040 + z * 1.0570;

        r = (r > 0.0031308) ? 1.055 * Math.pow(r, 1.0 / 2.4) - 0.055 : 12.92 * r;
        g = (g > 0.0031308) ? 1.055 * Math.pow(g, 1.0 / 2.4) - 0.055 : 12.92 * g;
        b = (b > 0.0031308) ? 1.055 * Math.pow(b, 1.0 / 2.4) - 0.055 : 12.92 * b;

        int red = (int) Math.round(r * 255);
        int green = (int) Math.round(g * 255);
        int blue = (int) Math.round(b * 255);

        red = Math.max(0, Math.min(255, red));
        green = Math.max(0, Math.min(255, green));
        blue = Math.max(0, Math.min(255, blue));

        return new RGBA8(red, green, blue);
    }

    private static double[] convertCIELabToXYZ(final double L, final double a, final double b) {
        double y = (L + 16) / 116;
        double x = a / 500 + y;
        double z = y - b / 200;

        final double x3 = Math.pow(x, 3);
        final double y3 = Math.pow(y, 3);
        final double z3 = Math.pow(z, 3);

        x = (x3 > 0.008856) ? x3 : (x - 16.0 / 116) / 7.787;
        y = (y3 > 0.008856) ? y3 : (y - 16.0 / 116) / 7.787;
        z = (z3 > 0.008856) ? z3 : (z - 16.0 / 116) / 7.787;

        final double[] xyz = {x * 95.047, y * 100.0, z * 108.883};
        return xyz;
    }


    private static final double PARALLAX_ERROR_THRESHOLD = 0.25; //0.1; // Example threshold

    public RGBA8 magToRGB2(final int x, final int y, final GaiaRenderingSource data) {

        final Float gMag = data.phot_g_mean_mag;
        final Float bpMag = data.phot_bp_mean_mag;
        final Float rpMag = data.phot_rp_mean_mag;
        final Double parallax = data.parallax;
        final Float parallaxError = data.parallax_error;

        //final double background = 200.0; // Example background value
        // 0. Background Subtraction
        //gMag = gMag - background;
        //bpMag = bpMag - background;
        //rpMag = rpMag - background;

        // 1. Convert magnitudes to relative flux densities
        //final double fG = Math.pow(10, -0.4 * gMag);
        //final double fBP = Math.pow(10, -0.4 * bpMag);
        //final double fRP = Math.pow(10, -0.4 * rpMag);


        // 2. Estimate stellar temperature with parallax correction

        double temperature = 0.0;

        if (parallaxError == null || parallaxError > PARALLAX_ERROR_THRESHOLD) {
            // Handle the case of unreliable parallax
            // You might return a default color (e.g., white) here
            //    System.out.println("Warning: Parallax error is high - color might be inaccurate");
            //    return new RGBA8(255, 255, 255, 255);
            if (gMag == null || bpMag == null || rpMag == null) {
                //return new RGBA8(0, 0, 0, 255);
                return new RGBA8(0, 0, 0, 255);
                //return Optional.empty();
            }
            //return calculateNewColor(rpMag, gMag, bpMag);
            //return convertToRGB(gMag, bpMag, rpMag);
            //return convertToRGBSimple(gMag, bpMag, rpMag);
            return calculateNewColor(rpMag, gMag, bpMag);
        } else {
            if (bpMag == null || rpMag == null) {
                //return new RGBA8(0, 0, 0, 255);
                return new RGBA8(0, 0, 0, 255);
                //return Optional.empty();
            }
            final double bpMinusRp = bpMag - rpMag;
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
            //return new RGBA8(r, g, b, 1);
            return new RGBA8(r, g, b, 1);
        }

        // 4. Scale to 0-255 range for RGB
        final int R = (int) (r * 255);
        final int G = (int) (g * 255);
        final int B = (int) (b * 255);

        final var out = new RGBA8(R, G, B, 255); //.log10Scale(50.0);
        //return out;
        return out;

        // 5. Apply log scaling and adjust range for visual appeal
        //final RGBA8 out = log10Scale(R, G, B, 50.0);

        // 6. Adjust intensity for better visibility
        //final double intensityFactor = 0.1; //2.5; // Adjust for desired intensity
        //out.red = (int) Math.round((double) out.red * intensityFactor);
        //out.green = (int) Math.round((double) out.green * intensityFactor);
        //out.blue = (int) Math.round((double) out.blue * intensityFactor);
        //return out;
        //return adjustColor(new RGBA8(r, g, b, 1), 0.5, 0.0, -0.25);
        //return new RGBA8(r, g, b, 1);
        //}
    }

    public static RGBA8 log10Scale(final int r, final int g, final int b, final double scale) {
        // Calculate the logarithmic scaling factor
        final double logFactor = Math.log10(1 + scale);

        // Apply logarithmic scaling to each color component
        int scaledR = (int) (r * logFactor);
        int scaledG = (int) (g * logFactor);
        int scaledB = (int) (b * logFactor);

        // Ensure the scaled color components are within the valid range [0, 255]
        scaledR = Math.min(255, Math.max(0, scaledR));
        scaledG = Math.min(255, Math.max(0, scaledG));
        scaledB = Math.min(255, Math.max(0, scaledB));

        // Combine the scaled color components into an ARGB color value
        return new RGBA8(scaledR, scaledG, scaledB, 255);
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

    public MagPixelRenderer(final int width, final int height, final int scale) {
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

        final RGBA8 color;
        if (data.phot_g_mean_mag == null || data.phot_bp_mean_mag == null || data.phot_rp_mean_mag == null || data.parallax == null || data.parallax_error == null) {
            color = new RGBA8(0, 0, 0, 255);
        } else {
            //color = magToRGB(data.phot_g_mean_mag, data.phot_bp_mean_mag, data.phot_rp_mean_mag);
            color = GaiaMagnitudeConverter.calculateColor(data.phot_rp_mean_mag, data.phot_g_mean_mag, data.phot_bp_mean_mag, data.parallax, data.parallax_error);
        }
        //RGBA8 color = null;

        //if (data.parallax == null || data.parallax_error == null || data.phot_g_mean_mag == null || data.phot_bp_mean_mag == null || data.phot_rp_mean_mag == null) {
        //return Optional.empty();
        //    color = new RGBA8(0, 0, 0, 255);
        //} else {
        //final var color = magToRGB2(x, y, data);
        //if (color.isEmpty()) {
        //    return Optional.empty();
        //}
        //color = GaiaMagnitudeConverter.calculateColor(data.phot_rp_mean_mag, data.phot_g_mean_mag, data.phot_bp_mean_mag, data.parallax, data.parallax_error);
        //color = calculateNewColor(data.phot_rp_mean_mag, data.phot_g_mean_mag, data.phot_bp_mean_mag);
        //}

        //final var color = calculateNewColor(phot_rp_mean_mag, phot_g_mean_mag, phot_bp_mean_mag);

        //--> GOOD
        //final var color = magToRGB2(data.phot_g_mean_mag, data.phot_bp_mean_mag, data.phot_rp_mean_mag, data.parallax, data.parallax_error);
        //final var color = GaiaMagnitudeConverter.calculateColor(phot_rp_mean_mag, phot_g_mean_mag, phot_bp_mean_mag, parallax, parallaxError);

        //samples[x][y].add(temperatureColor);
        try {
            final var c = color;
            samples[x][y].red += c.red;
            samples[x][y].green += c.green;
            samples[x][y].blue += c.blue;
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

        final double count = 14602; //getMaxSamples(); // /4
        //final double count = counts[x][y];
        //final double count = 500;
        final int red = (int) Math.round((double) samples[x][y].red / count);
        final int green = (int) Math.round((double) samples[x][y].green / count);
        final int blue = (int) Math.round((double) samples[x][y].blue / count);
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
        System.out.println("Mag Renderer");
        System.out.println("Max Samples: " + getMaxSamples());
        System.out.println("Maximum Sample Count: " + getMaximumCount());
    }
}
