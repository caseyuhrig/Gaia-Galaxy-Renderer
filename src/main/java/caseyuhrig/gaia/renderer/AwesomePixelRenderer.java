package caseyuhrig.gaia.renderer;

import caseyuhrig.gaia.GaiaConst;
import caseyuhrig.gaia.GaiaRenderingSource;
import caseyuhrig.gaia.RGBA8;
import caseyuhrig.gaia.RenderingData;

import java.awt.*;

public class AwesomePixelRenderer extends PixelRenderer {

    private static final double PARALLAX_ERROR_THRESHOLD = 0.25;

    // Gamma correction exponent for sRGB
    //private static final double GAMMA = 2.4;
    private static final double GAMMA = 0.8;


    private final double[][] buffer;
    private final double[][] distances;
    private final int[][] counts;
    private final RGBA8[][] colors;


    public AwesomePixelRenderer(final int width, final int height, final int scale) {
        super(width, height, scale);
        // -1 out the distances
        distances = new double[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                distances[x][y] = -1.0;
            }
        }
        // zero out bp_rp_buffer
        buffer = new double[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                buffer[x][y] = 0.0;
            }
        }
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


    //private static final double min = GaiaConst.min_phot_g_mean_mag;
    //private static final double max = GaiaConst.max_phot_g_mean_mag;

    //private static final double min = GaiaConst.min_phot_g_mean_flux;
    //private static final double max = GaiaConst.max_phot_g_mean_flux;

    private static final double min = GaiaConst.min_teff_gspphot;
    private static final double max = GaiaConst.max_teff_gspphot;

    // Conversion matrix from XYZ to linear RGB
    private static final double[][] XYZ_TO_RGB_MATRIX = {
            {3.2406, -1.5372, -0.4986},
            {-0.9689, 1.8758, 0.0415},
            {0.0557, -0.2040, 1.0570}
    };


    // Function to convert Gaia data to RGB color
    public static RGBA8 gaiaToRGB(final double phot_g_mean_flux, final double phot_rp_mean_flux, final double phot_bp_mean_flux) {
        // Convert Gaia fluxes to XYZ tristimulus values
        final double X = 1.087 * phot_g_mean_flux + 0.273 * phot_rp_mean_flux - 0.161 * phot_bp_mean_flux;
        final double Y = 1.039 * phot_g_mean_flux + 0.111 * phot_rp_mean_flux - 0.051 * phot_bp_mean_flux;
        final double Z = 1.022 * phot_g_mean_flux - 0.001 * phot_rp_mean_flux + 0.001 * phot_bp_mean_flux;

        // Convert XYZ to linear RGB
        final double[] linearRGB = {
                XYZ_TO_RGB_MATRIX[0][0] * X + XYZ_TO_RGB_MATRIX[0][1] * Y + XYZ_TO_RGB_MATRIX[0][2] * Z,
                XYZ_TO_RGB_MATRIX[1][0] * X + XYZ_TO_RGB_MATRIX[1][1] * Y + XYZ_TO_RGB_MATRIX[1][2] * Z,
                XYZ_TO_RGB_MATRIX[2][0] * X + XYZ_TO_RGB_MATRIX[2][1] * Y + XYZ_TO_RGB_MATRIX[2][2] * Z
        };

        // Apply gamma correction for sRGB
        int r = (int) (255 * Math.pow(linearRGB[0], 1.0 / GAMMA));
        int g = (int) (255 * Math.pow(linearRGB[1], 1.0 / GAMMA));
        int b = (int) (255 * Math.pow(linearRGB[2], 1.0 / GAMMA));

        // Clamp RGB values to [0, 255]
        r = Math.max(0, Math.min(255, r));
        g = Math.max(0, Math.min(255, g));
        b = Math.max(0, Math.min(255, b));

        // Pack RGB values into a single integer
        return new RGBA8(r, g, b, 255);
    }

    public static int roundToNearestHundred(final double number) {
        // Divide the number by 100
        final double divided = number / 100.0;
        // Round the result to the nearest integer
        final long rounded = Math.round(divided);
        // Multiply the rounded result by 100
        final int result = (int) rounded * 100;
        return result;
    }

    public RGBA8 timesNormalize(final RGBA8 c1, final RGBA8 c2) {
        final double red = c1.red * c2.red;
        final double green = c1.green * c2.green;
        final double blue = c1.blue * c2.blue;
        // find the max value
        final double max = Math.max(Math.max(red, green), blue);
        // normalize the values
        final double nRed = red / max * 255.0;
        final double nGreen = green / max * 255.0;
        final double nBlue = blue / max * 255.0;
        return new RGBA8((int) nRed, (int) nGreen, (int) nBlue, 255);
    }

    public RGBA8 plusNormalize(final RGBA8 c1, final RGBA8 c2) {
        final double red = (c1.red + c2.red) / 2.0;
        final double green = (c1.green + c2.green) / 2.0;
        final double blue = (c1.blue + c2.blue) / 2.0;
        return new RGBA8((int) red, (int) green, (int) blue, 255);
    }


    public static RGBA8 convertToRGB(final double gFlux, final double bpFlux, final double rpFlux, final double temperature) {
        // Normalize the flux values
        final double totalFlux = gFlux + bpFlux + rpFlux;
        final double normG = gFlux / totalFlux;
        final double normBP = bpFlux / totalFlux;
        final double normRP = rpFlux / totalFlux;

        // Apply gamma correction and convert normalized flux to RGB
        final int red = (int) Math.min(255, Math.max(0, 255 * Math.pow(normRP, GAMMA)));
        final int green = (int) Math.min(255, Math.max(0, 255 * Math.pow(normG, GAMMA)));
        final int blue = (int) Math.min(255, Math.max(0, 255 * Math.pow(normBP, GAMMA)));

        // Apply color temperature correction
        final Color correctedColor = applyColorTemperature(new Color(red, green, blue), temperature);

        return RGBA8.from(correctedColor);
    }

    private static Color applyColorTemperature(final Color color, final double temperature) {
        // Convert temperature to RGB
        final double[] rgb = colorTemperatureToRGB(temperature);

        // Apply the temperature correction to the color
        final int red = (int) Math.min(255, Math.max(0, color.getRed() * rgb[0]));
        final int green = (int) Math.min(255, Math.max(0, color.getGreen() * rgb[1]));
        final int blue = (int) Math.min(255, Math.max(0, color.getBlue() * rgb[2]));

        return new Color(red, green, blue);
    }

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

    @Override
    public RGBA8 renderPixel(final int x, final int y, final RenderingData data) {
/*
        if (data.bp_rp == null || data.phot_g_mean_mag == null || data.parallax == null || data.parallax_error == null) {
            return null;
        }
        if (data.phot_rp_mean_mag == null || data.phot_bp_mean_mag == null) {
            return null;
        }
        if (data.teff_gspphot == null) {
            return null;
        }
*/
        if (data.phot_g_mean_flux == null || data.phot_rp_mean_flux == null || data.phot_bp_mean_flux == null) {
            return null;
        }
        // check the fluxes
        //if (data.phot_g_mean_flux == null || data.phot_rp_mean_flux == null || data.phot_bp_mean_flux == null) {
        //    return null;
        //}
        if (data.teff_gspphot == null) {
            return null;
        }
        //if (data.astrometric_excess_noise == null || data.astrometric_excess_noise > 0) {
        //    return null;
        //}

        final RGBA8 color = convertToRGB(data.phot_g_mean_flux, data.phot_bp_mean_flux, data.phot_rp_mean_flux, data.teff_gspphot);
        // final double gFlux, final double bpFlux, final double rpFlux, final double temperature)

        //final int temp = roundToNearestHundred(data.teff_gspphot);
        //final RGBA8 color = TemperaturePixelRenderer.blackbodyColors.get(temp); // was tempRGB
        //if (color == null) {
        //    System.out.println("No tempRGB for " + temp);
        //    return null;
        //}

        //final double value = data.phot_g_mean_mag;
        //final double value = data.phot_g_mean_flux;
        //final double value = data.teff_gspphot;


        // get color from the temperature and then add the magnitude, divide by 2 to get the rgb value

        //final RGBA8 rgb1 = gaiaToRGB(data.phot_g_mean_flux, data.phot_rp_mean_flux, data.phot_bp_mean_flux);
        //final RGBA8 c2 = new RGBA8((rgb1.red + tempRGB.red) / 2, (rgb1.green + tempRGB.green) / 2, (rgb1.blue + tempRGB.blue) / 2, 255);
        //final RGBA8 c2 = timesNormalize(rgb1, tempRGB);
        //final RGBA8 c2 = plusNormalize(rgb1, tempRGB);

        //System.out.println("c2: " + c2.red + " " + c2.green + " " + c2.blue);

        colors[x][y].red += color.red;
        colors[x][y].green += color.green;
        colors[x][y].blue += color.blue;
        //buffer[x][y] += value;
        counts[x][y]++;

        if (counts[x][y] > maxSamples) {
            System.out.println("Max samples: " + counts[x][y]);
            maxSamples = counts[x][y];
        }

        //final double c = normalize(buffer[x][y] / (double) maxSamples, 0, max); //min, max);

        if (1 == 1) {

            final RGBA8 c1 = new RGBA8(colors[x][y].red / maxSamples, colors[x][y].green / maxSamples, colors[x][y].blue / maxSamples, 255);
            //final RGBA8 c2 = new RGBA8((c1.red + tempRGB.red) / 2, (c1.green + tempRGB.green) / 2, (c1.blue + tempRGB.blue) / 2, 255);

            return c1;
            //return new RGBA8(colors[x][y].red / maxSamples, colors[x][y].green / maxSamples, colors[x][y].blue / maxSamples, 255);
            //return new RGBA8(colors[x][y].red / counts[x][y], colors[x][y].green / counts[x][y], colors[x][y].blue / counts[x][y], 255);
            //return new RGBA8(c, c, c, 1.0);
        }

        //final double bp_rp = data.bp_rp; //1.5; // Example value, replace with actual data
        //final double phot_g_mean_mag = data.phot_g_mean_mag; //10.0; // Example value, replace with actual data
        //final double parallax = data.parallax; //0.01; // Example value, replace with actual data

        // Calculate absolute magnitude
        final double absMag = calculateAbsoluteMagnitude(data.phot_g_mean_mag, data.parallax);

        // Calculate luminosity
        final double luminosity = calculateLuminosity(absMag);

        // Determine spectral type (you may need additional data for this step)
        //final String spectralType = determineSpectralType(bp_rp);

        // Estimate color temperature based on spectral type
        //final double colorTemperature = estimateColorTemperature(spectralType);
        final double colorTemperature = estimateColorTemperature2(data.bp_rp, data.parallax, data.parallax_error);

        // Convert to CIE XYZ
        final double[] XYZ = convertToXYZ(luminosity, colorTemperature);

        //System.out.println("XYZ: " + XYZ[0] + " " + XYZ[1] + " " + XYZ[2]);

        //final double[] nXYZ = normalizeXYZ(XYZ);
        final double[] nXYZ = XYZ;

        final double[] rgb = convertXYZtoRGB(nXYZ[0], nXYZ[1], nXYZ[2]);

        final double scale = 12.0;
        final double[] rgb2 = new double[]{rgb[0] * scale, rgb[1] * scale, rgb[2] * scale};

        //if (rgb[0] != 0 && rgb[1] != 0 && rgb[2] != 0)
        //System.out.println("RGB: " + rgb[0] + " " + rgb[1] + " " + rgb[2]);

        final var rgba = new RGBA8(rgb2[0], rgb2[1], rgb2[2], 1.0);
        //final var rgba = new RGBA8(rgb[0], rgb[1], rgb[2], 1.0);
        //final var rgba = new RGBA8(rgb[0] * 12, rgb[1] * 12, rgb[2] * 12, 1.0);

        //System.out.println("Alpha: " + rgba.alpha + " Red: " + rgba.red + " Green: " + rgba.green + " Blue: " + rgba.blue);

        if (rgba.red == 0 && rgba.green == 0 && rgba.blue == 0) {
            return null;
        }

        //if (rgba.red == 430 && rgba.green == 172 && rgba.blue == 87) {
        //    return null;
        //}
        //System.out.println("RGBA: " + rgba.red + " " + rgba.green + " " + rgba.blue + " " + rgba.alpha);

        //final RGBA8 rgba = calculateNewColor(data.phot_rp_mean_mag, data.phot_g_mean_mag, data.phot_bp_mean_mag);
        //final RGBA8 rgba = calcColor(data);
        //final RGBA8 rgba = calcColor2(data);

        //if (1 == 1) return rgba;


        final double previousDistance = distances[x][y];
        if (previousDistance == -1.0) {
            distances[x][y] = calculateDistance(data.parallax);
            return rgba;
        } else {
            final double currentDistance = calculateDistance(data.parallax);
            if (currentDistance < previousDistance) {
                distances[x][y] = currentDistance;
                return rgba;
            }
        }
        return null;

    }

    public RGBA8 calcColor(final GaiaRenderingSource data) {
        final double bpMinusRp = data.phot_bp_mean_mag - data.phot_rp_mean_mag;
        // Simplified temperature estimation with parallax adjustment
        final double temperature = 4500.0 / (0.92 * bpMinusRp + 1.7) + 1400
                + 500.0 * (1.0 / data.parallax - 1.0);

        // Adjust temperature uncertainty based on parallaxError
        //if (data.parallax_error != null) {
        //    final double tempUncertainty = 500.0 * (data.parallax_error / data.parallax);
        //    temperature += Math.random() * (2 * tempUncertainty) - tempUncertainty;
        //}


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
            //return new RGBA8(r, g, b, 1);
        }
        final var color = new RGBA8(r, g, b, 1);
        return color;
    }

    public RGBA8 calcColor2(final GaiaRenderingSource data) {
        final double bpMinusRp = data.phot_bp_mean_mag - data.phot_rp_mean_mag;
        // Simplified temperature estimation with parallax adjustment
        final double temperature = 4500.0 / (0.92 * bpMinusRp + 1.7) + 1400
                + 500.0 * (1.0 / data.parallax - 1.0);

        // Adjust temperature uncertainty based on parallaxError
        //if (data.parallax_error != null) {
        //    final double tempUncertainty = 500.0 * (data.parallax_error / data.parallax);
        //    temperature += Math.random() * (2 * tempUncertainty) - tempUncertainty;
        //}

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


        final var color = new RGBA8((int) red, (int) green, (int) blue, 255);

        return color;
    }

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


    public static double calculateDistance(final double parallax) {
        return 1.0 / parallax;
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


    public static double estimateColorTemperature(final String spectralType) {
        // Example method
        // You may need a more sophisticated method to estimate color temperature
        switch (spectralType) {
            case "O":
                return 20000; // Example value for O-type stars
            case "B":
                return 10000; // Example value for B-type stars
            case "A":
                return 7500; // Example value for A-type stars
            default:
                return 6500; // Default value for other types
        }
    }


    public static double estimateColorTemperature2(final double bp_rp, final double parallax, final double parallax_error) {

        // Simplified temperature estimation with parallax adjustment
        double temperature = 4500.0 / (0.92 * bp_rp + 1.7) + 1400
                + 500.0 * (1.0 / parallax - 1.0);
        //if (parallax_error > PARALLAX_ERROR_THRESHOLD) {
        //    temperature += 500.0 * (PARALLAX_ERROR_THRESHOLD - parallax_error);
        //}
        // Adjust temperature based on parallaxError
        if (parallax_error > PARALLAX_ERROR_THRESHOLD) {
            // Adjust temperature uncertainty based on parallaxError
            final double tempUncertainty = 500.0 * (parallax_error / parallax);
            temperature += Math.random() * (2 * tempUncertainty) - tempUncertainty;
        }
        return temperature;
    }


    public static double[] convertToXYZ(final double luminosity, final double colorTemperature) {
        // Example conversion
        // You may need a more accurate method for converting to XYZ
        final double X = 0.4124 * luminosity * Math.pow(colorTemperature / 100, -2.640);
        final double Y = 0.3576 * luminosity * Math.pow(colorTemperature / 100, -2.640);
        final double Z = 0.1805 * luminosity * Math.pow(colorTemperature / 100, -2.640);
        return new double[]{X, Y, Z};
    }


    public static double[] convertXYZtoRGB(final double X, final double Y, final double Z) {
        // Example conversion matrix (you may need a different one)
        final double[][] M = {
                {3.2406, -1.5372, -0.4986},
                {-0.9689, 1.8758, 0.0415},
                {0.0557, -0.2040, 1.0570}
        };

        // Calculate RGB values
        final double R = M[0][0] * X + M[0][1] * Y + M[0][2] * Z;
        final double G = M[1][0] * X + M[1][1] * Y + M[1][2] * Z;
        final double B = M[2][0] * X + M[2][1] * Y + M[2][2] * Z;

        return new double[]{R, G, B};
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
