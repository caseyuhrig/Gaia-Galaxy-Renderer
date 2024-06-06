package caseyuhrig.gaia.renderer;

import caseyuhrig.gaia.RGBA8;
import caseyuhrig.gaia.RenderingData;

import java.awt.*;
import java.util.Arrays;

public class AnotherPixelRenderer extends PixelRenderer {

    private static final double PARALLAX_ERROR_THRESHOLD = 0.25;

    // Constants for human eye sensitivity
    private static final double GAMMA = 0.8;
    private static final double SCALE_FACTOR = 255;


    //private final double[][] buffer;
    //private final double[][] distances;
    private final int[][] counts;
    private final RGBA8[][] colors;


    public AnotherPixelRenderer(final int width, final int height, final int scale) {
        super(width, height, scale);
        // -1 out the distances
        /*
        distances = new double[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                distances[x][y] = -1.0;
            }
        }
         */
        // zero out bp_rp_buffer
        /*
        buffer = new double[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                buffer[x][y] = 0.0;
            }
        }
         */
        // zero out the counts
        counts = new int[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                counts[x][y] = 0;
            }
        }

        // zero out the colors
        colors = new RGBA8[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                colors[x][y] = new RGBA8(0, 0, 0, 255);
            }
        }
        //maxSamples = 3659;
        //maxSamples = 1225;
        //maxSamples = 1807;
        maxSamples = 1807;
    }


    @Override
    public RGBA8 renderPixel(final int x, final int y, final RenderingData data) {
        if (data.phot_g_mean_flux == null || data.phot_rp_mean_flux == null || data.phot_bp_mean_flux == null || data.teff_gspphot == null || data.parallax == null) {
            return null;
        }


        // Calculate distance in parsecs
        final double distance = parallaxDistance(data.parallax);

        final RGBA8 color = convertToRGB(data.phot_g_mean_flux, data.phot_bp_mean_flux, data.phot_rp_mean_flux, data.teff_gspphot, distance);


        colors[x][y].red += color.red;
        colors[x][y].green += color.green;
        colors[x][y].blue += color.blue;
        counts[x][y]++;

        if (counts[x][y] > maxSamples) {
            System.out.println("Max samples: " + counts[x][y]);
            maxSamples = counts[x][y];
        }

        final RGBA8 c1 = new RGBA8(colors[x][y].red / maxSamples, colors[x][y].green / maxSamples, colors[x][y].blue / maxSamples, 255);
        return c1; //.saturate(0.6f);
    }


    public static RGBA8 convertToRGB(final double gFlux, final double bpFlux, final double rpFlux, final double temperature, final double distance) {
        // Apply logarithmic scaling to flux values
        //gFlux = Math.log1p(gFlux);  // log1p(x) computes log(1 + x)
        //bpFlux = Math.log1p(bpFlux);
        //rpFlux = Math.log1p(rpFlux);

        // Normalize the flux values
        final double totalFlux = gFlux + bpFlux + rpFlux;
        final double normG = gFlux / totalFlux;
        final double normBP = bpFlux / totalFlux;
        final double normRP = rpFlux / totalFlux;

        // Convert normalized flux to CIE XYZ
        final double[] xyz = fluxToXYZ(normG, normBP, normRP);

        // Apply color temperature correction
        final double[] correctedXYZ = applyColorTemperature(xyz, temperature, distance);

        // Convert CIE XYZ to sRGB
        final RGBA8 color = xyzToSRGB(correctedXYZ);

        //final double av = 0.5; // Example extinction value
        //color = applyDustExtinction(color, av);

        // Apply white balance adjustment
        //color = adjustWhiteBalance(color);

        // Apply color mapping
        //color = applyColorMapping(color);

        // Adjust brightness and contrast
        //color = adjustBrightness(color, 1.2);  // Increase brightness by 20%
        //color = adjustContrast(color, 1.5);    // Increase contrast by 50%

        //color.brighten(2.5f);

        color.red = clamp((int) (color.red * 2.5));
        color.green = clamp((int) (color.green * 2.5));
        color.blue = clamp((int) (color.blue * 2.5));

        if (color.green > 0) {
            //final double add = color.green / 2.0;
            //color.red = clamp((int) (color.red + add));
            //color.green /= 2;
            //color.blue = clamp((int) (color.blue + add));

        }


        return color;
    }

    private static double[] applyDustExtinction(final double[] rgb, final double av) {
        final double rv = 3.1; // Average value for Rv
        final double[] extCoeff = {0.574 * Math.pow(5500 / 4400, 1.61), 1.0, 0.442 * Math.pow(1, 1.61)};

        final double r = rgb[0] * Math.pow(10, -0.4 * extCoeff[0] * av / rv);
        final double g = rgb[1] * Math.pow(10, -0.4 * extCoeff[1] * av / rv);
        final double b = rgb[2] * Math.pow(10, -0.4 * extCoeff[2] * av / rv);

        return new double[]{r, g, b};
    }

    private static double[] fluxToXYZ(final double normG, final double normBP, final double normRP) {

        // Adjusted weights to achieve desired color balance
        final double adjustedRP = normRP; // * 1.2; // * 1.0;  // Increase RP influence for red channel
        final double adjustedG = normG; // last * 1.2; // * 0.8;// * 0.7;    // Decrease G influence for green channel
        final double adjustedBP = normBP; // last * 2.5; // * 5.5;// * 1.4;  // Increase BP influence for blue channel

        // Placeholder conversion coefficients (these would need to be determined based on specific flux to XYZ conversion factors)
        final double x = 0.4124 * adjustedRP + 0.3576 * adjustedG + 0.1805 * adjustedBP;
        final double y = 0.2126 * adjustedRP + 0.7152 * adjustedG + 0.0722 * adjustedBP;
        final double z = 0.0193 * adjustedRP + 0.1192 * adjustedG + 0.9505 * adjustedBP;

        return new double[]{x, y, z};
    }

    private static double[] applyColorTemperature(final double[] xyz, final double temperature, final double distance) {
        // Convert temperature to RGB, should be based on Planckian locus.
        double[] rgb = colorTemperatureToRGB(temperature);

        final double av = 0.5; // Example extinction value
        rgb = applyDustExtinction(rgb, av);

        rgb = applyRayleighScattering(rgb, distance);
        rgb = applyMieScattering(rgb, distance);

        //rgb = applyColorMapping(rgb, 0.0125, 0.0, 0.0125);
        //rgb[0] = rgb[0] * 0.3;

        //rgb[1] = rgb[1] * 0.5;

        //rgb[2] = rgb[2] + 0.3;
        // increase all by 0.5
        //for (int i = 0; i < 3; i++) {
        //    rgb[i] = rgb[i] + 0.5;
        //}
        // normalize
        //final double max = Math.max(rgb[0], Math.max(rgb[1], rgb[2]));
        //rgb[0] = rgb[0] / max;
        //rgb[1] = rgb[1] / max;
        //rgb[2] = rgb[2] / max;

        rgb[0] = Math.log10(rgb[0] + 1.0);
        rgb[1] = Math.log10(rgb[1] + 1.0);
        rgb[2] = Math.log10(rgb[2] + 1.0);

        //for (int i = 0; i < 3; i++) rgb[i] *= 2.5;

        rgb = applyToneMapping(rgb, 1.0);

        // Apply temperature correction to XYZ
        final double x = xyz[0] * rgb[0];
        final double y = xyz[1] * rgb[1];
        final double z = xyz[2] * rgb[2];

        return new double[]{x, y, z};
    }

    public static double[] applyColorMapping(final double[] rgb, final double red, final double green, final double blue) {
        //final double[] out = new double[]{0, 0, 0};
        if (1 == 1) {
            rgb[0] = rgb[0] * red;
            rgb[1] = rgb[1] * green;
            rgb[2] = rgb[2] * blue;
        } else {
            rgb[0] = rgb[0] + red;
            rgb[1] = rgb[1] + green;
            rgb[2] = rgb[2] + blue;
        }
        final double exposure = 0.0;
        // get max value
        final double max = Math.max(rgb[0], Math.max(rgb[1], rgb[2]));
        // normalize
        rgb[0] = rgb[0] / max + exposure;
        rgb[1] = rgb[1] / max + exposure;
        rgb[2] = rgb[2] / max + exposure;
        return rgb;
    }

    public static double[] applyToneMapping(final double[] rgb, final double exposure) {
        final double[] mapped = new double[3];

        for (int i = 0; i < 3; i++) {
            final double scaled = rgb[i] * exposure;
            mapped[i] = scaled / (scaled + 1.0);
        }

        return mapped;
    }

    // Constants for Rayleigh scattering
    private static final double RAYLEIGH_SCALING_FACTOR = 1.0e-4; // Adjust as needed

    // Function for Rayleigh scattering
    public static double[] applyRayleighScattering(final double[] rgb, final double distance) {
        final double wavelengthR = 680.0; // Red wavelength in nm
        final double wavelengthG = 550.0; // Green wavelength in nm
        final double wavelengthB = 450.0; // Blue wavelength in nm

        final double r = rgb[0] * Math.exp(-RAYLEIGH_SCALING_FACTOR * distance / Math.pow(wavelengthR, 4));
        final double g = rgb[1] * Math.exp(-RAYLEIGH_SCALING_FACTOR * distance / Math.pow(wavelengthG, 4));
        final double b = rgb[2] * Math.exp(-RAYLEIGH_SCALING_FACTOR * distance / Math.pow(wavelengthB, 4));

        return new double[]{r, g, b};
    }

    // Constants for Mie scattering
    private static final double MIE_SCALING_FACTOR = 1.0e-3; // Adjust as needed

    // Function for Mie scattering
    public static double[] applyMieScattering(final double[] rgb, final double distance) {
        final double wavelengthR = 680.0; // Red wavelength in nm
        final double wavelengthG = 550.0; // Green wavelength in nm
        final double wavelengthB = 450.0; // Blue wavelength in nm

        final double r = rgb[0] * Math.exp(-MIE_SCALING_FACTOR * distance / wavelengthR);
        final double g = rgb[1] * Math.exp(-MIE_SCALING_FACTOR * distance / wavelengthG);
        final double b = rgb[2] * Math.exp(-MIE_SCALING_FACTOR * distance / wavelengthB);

        return new double[]{r, g, b};
    }

    /**
     * Implementation of the Planckian locus to convert color temperature (in Kelvin) to RGB values.
     * The Planckian locus describes the color of a black body at different temperatures.
     *
     * @param temperature the color temperature in Kelvin
     * @return an array of RGB values
     */
    private static double[] colorTemperatureToRGB(final double temperature) {
        final double t = temperature / 100.0;
        double r, g, b;

        // Calculate red
        if (t <= 66) {
            r = 255;
        } else {
            r = t - 60;
            r = 329.698727446 * Math.pow(r, -0.1332047592);
            r = Math.min(255, Math.max(0, r));
        }

        // Calculate green
        if (t <= 66) {
            g = t;
            g = 99.4708025861 * Math.log(g) - 161.1195681661;
            g = Math.min(255, Math.max(0, g));
        } else {
            g = t - 60;
            g = 288.1221695283 * Math.pow(g, -0.0755148492);
            g = Math.min(255, Math.max(0, g));
        }

        // Calculate blue
        if (t >= 66) {
            b = 255;
        } else {
            if (t <= 19) {
                b = 0;
            } else {
                b = t - 10;
                b = 138.5177312231 * Math.log(b) - 305.0447927307;
                b = Math.min(255, Math.max(0, b));
            }
        }

        return new double[]{r / 255.0, g / 255.0, b / 255.0};
    }

    private static RGBA8 xyzToSRGB(final double[] xyz) {
        // Convert XYZ to linear sRGB
        final double rLinear = 3.2406 * xyz[0] - 1.5372 * xyz[1] - 0.4986 * xyz[2];
        final double gLinear = -0.9689 * xyz[0] + 1.8758 * xyz[1] + 0.0415 * xyz[2];
        final double bLinear = 0.0557 * xyz[0] - 0.2040 * xyz[1] + 1.0570 * xyz[2];

        // Apply gamma correction to convert linear sRGB to sRGB
        final int r = (int) (SCALE_FACTOR * gammaCorrect(rLinear));
        final int g = (int) (SCALE_FACTOR * gammaCorrect(gLinear));
        final int b = (int) (SCALE_FACTOR * gammaCorrect(bLinear));

        return new RGBA8(clamp(r), clamp(g), clamp(b));
    }

    private static double gammaCorrect(final double value) {
        return value <= 0.0031308 ? 12.92 * value : 1.055 * Math.pow(value, 1.0 / 2.4) - 0.055;
    }

    private static int clamp(final int value) {
        return Math.min(255, Math.max(0, value));
    }


    private static Color adjustWhiteBalance(final Color color) {
        double r = color.getRed() / 255.0;
        double g = color.getGreen() / 255.0;
        double b = color.getBlue() / 255.0;

        // Compute the average of the RGB components
        final double avg = (r + g + b) / 3.0;

        // Adjust each component to balance the colors
        r = r / avg;
        g = g / avg;
        b = b / avg;

        // Convert back to 0-255 range and create new Color object
        final int red = (int) (r * 255);
        final int green = (int) (g * 255);
        final int blue = (int) (b * 255);

        return new Color(clamp(red), clamp(green), clamp(blue));
    }


    private static Color applyColorMapping(final Color color) {
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();

        // Apply custom color mapping for blue and yellow balance
        red = (int) (red * 1.1);   // Slightly increase red
        green = (int) (green * 0.9); // Slightly decrease green
        blue = (int) (blue * 1.2);  // Increase blue

        return new Color(clamp(red), clamp(green), clamp(blue));
    }

    private static Color adjustBrightness(final Color color, final double factor) {
        final int red = (int) (color.getRed() * factor);
        final int green = (int) (color.getGreen() * factor);
        final int blue = (int) (color.getBlue() * factor);

        return new Color(clamp(red), clamp(green), clamp(blue));
    }

    private static Color adjustContrast(final Color color, final double factor) {
        final int red = (int) ((color.getRed() - 128) * factor + 128);
        final int green = (int) ((color.getGreen() - 128) * factor + 128);
        final int blue = (int) ((color.getBlue() - 128) * factor + 128);

        return new Color(clamp(red), clamp(green), clamp(blue));
    }


    public static double parallaxDistance(final double parallax) {
        return 1.0 / parallax;
    }


    public static double[] normalize(final double[] input) {
        final double[] output = new double[input.length];
        final double max = Arrays.stream(input).max().getAsDouble();
        Arrays.setAll(output, i -> input[i] / max);
        return output;
    }


    public static double[] normalizeXYZ(final double[] XYZ) {
        final double X = XYZ[0];
        final double Y = XYZ[1];
        final double Z = XYZ[2];
        // Find the maximum value among X, Y, and Z
        final double max = Math.max(Math.max(X, Y), Z);

        // Normalize each value
        final double normalizedX = X / max;
        final double normalizedY = Y / max;
        final double normalizedZ = Z / max;

        // Return the normalized XYZ values
        return new double[]{normalizedX, normalizedY, normalizedZ};
    }


    public static double calculateAbsoluteMagnitude(final double phot_g_mean_mag, final double parallax) {
        // Calculate distance in parsecs
        final double distance = 1000 / parallax;
        // Calculate absolute magnitude
        return phot_g_mean_mag - 5 * (Math.log10(distance) - 1);
    }


    public static double calculateLuminosity(final double absMag) {
        // Example calculation
        // You may need to adjust this based on your data and specific formula
        return Math.pow(10, (4.83 - absMag) / 2.5);
    }


    public static String determineSpectralType(final double bp_rp) {
        // Example method
        // You may need additional data or a more complex algorithm to determine spectral type
        if (bp_rp < 1.0) {
            return "O";
        } else if (bp_rp < 2.0) {
            return "B";
        } else {
            return "A";
        }
    }


    public static double normalize(final double value, final double min, final double max) {
        return (value - min) / (max - min);
    }


    @Override
    public void printStatistics() {
        System.out.println(getClass().getSimpleName() + " Statistics");
        System.out.println("-------------------------------------------------------");
    }
}
