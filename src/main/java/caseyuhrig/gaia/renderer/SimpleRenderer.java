package caseyuhrig.gaia.renderer;

import caseyuhrig.gaia.RGBA32;
import caseyuhrig.gaia.RGBA8;
import caseyuhrig.gaia.RenderingData;


public class SimpleRenderer extends PixelRenderer {

    private final RGBA8[][] pixels;
    private final int[][] counts;


    public SimpleRenderer(final int width, final int height, final int scale) {
        super(width, height, scale);
        pixels = new RGBA8[width][height];
        counts = new int[width][height];
        // zero out the colors
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                pixels[x][y] = new RGBA8(0, 0, 0, 0);
            }
        }
        // zero out the counts
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                counts[x][y] = 0;
            }
        }
    }

    @Override
    public RGBA8 renderPixel(final int x, final int y, final RenderingData data) {

        if (data.phot_g_mean_mag == null || data.phot_bp_mean_mag == null || data.phot_rp_mean_mag == null) {
            return null;
        }

        /*
        final Float gMag = data.phot_g_mean_mag;
        final Float bpMag = data.phot_bp_mean_mag;
        final Float rpMag = data.phot_rp_mean_mag;
        final var color = convertToRGB(gMag, bpMag, rpMag);
        */
        //final var color = convertToRGBSimple(gMag, bpMag, rpMag);

        /*
        final double r = RGBA32.normalize(data.phot_rp_mean_mag, GaiaConst.min_phot_rp_mean_mag, GaiaConst.max_phot_rp_mean_mag);
        final double g = RGBA32.normalize(data.phot_g_mean_mag, GaiaConst.min_phot_g_mean_mag, GaiaConst.max_phot_g_mean_mag);
        final double b = RGBA32.normalize(data.phot_bp_mean_mag, GaiaConst.min_phot_bp_mean_mag, GaiaConst.max_phot_bp_mean_mag);
        final var color = RGBA32.of(r, g, b).invert().toRGBA8().log10Scale(50.0);
        */

        final var color = RGBA32.of(Math.sin(data.phot_rp_mean_mag), Math.sin(data.phot_g_mean_mag), Math.sin(data.phot_bp_mean_mag)).toRGBA8();


        pixels[x][y].red += color.red;
        pixels[x][y].green += color.green;
        pixels[x][y].blue += color.blue;
        counts[x][y]++;

        final int count = counts[x][y];

        final RGBA8 out = new RGBA8(pixels[x][y].red / count, pixels[x][y].green / count, pixels[x][y].blue / count, 255);

        return out;
    }


    @Override
    public void printStatistics() {
        System.out.println("Brighter Renderer");
        System.out.println("Max Samples: " + getMaxSamples());
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

    public static RGBA8 convertToRGBSimple(final double gMag, final double bpMag, final double rpMag) {
        // Define the maximum and minimum magnitudes for each band
        final double maxMag = 20.0;
        final double minMag = 0.0;

        // Normalize the magnitudes to the range [0, 1]
        final double gNorm = (maxMag - gMag) / (maxMag - minMag);
        final double bpNorm = (maxMag - bpMag) / (maxMag - minMag);
        final double rpNorm = (maxMag - rpMag) / (maxMag - minMag);

        // Map the normalized magnitudes to RGB values
        final int red = (int) Math.round(rpNorm * 255);
        final int green = (int) Math.round(gNorm * 255);
        final int blue = (int) Math.round(bpNorm * 255);

        // Create a new Color object with the RGB values
        return new RGBA8(red, green, blue);
    }

}
