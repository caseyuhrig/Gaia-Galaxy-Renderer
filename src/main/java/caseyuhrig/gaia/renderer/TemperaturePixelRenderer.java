package caseyuhrig.gaia.renderer;

import caseyuhrig.gaia.GaiaConst;
import caseyuhrig.gaia.RGBA8;
import caseyuhrig.gaia.RenderingData;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemperaturePixelRenderer extends PixelRenderer {

    // blackbodyColors initialized at the end of the file
    public static final HashMap<Integer, RGBA8> blackbodyColors = new HashMap<>();

    private final RGBA8[][] samples;
    private final int[][] counts;
    // maxCount calculated from the data, saved post run.
    //private int maxCount = 481; // scale = 4
    private int maxCount = 4600; // scale = 1


    public TemperaturePixelRenderer(final int width, final int height, final int scale) {
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
        //processTemperatureColorTable();
    }


    public RGBA8 calculateNewColor(final double phot_rp_mean_mag, final double phot_g_mean_mag, final double phot_bp_mean_mag) {
        final double r = normalize(phot_rp_mean_mag, GaiaConst.min_phot_rp_mean_mag, GaiaConst.max_phot_rp_mean_mag);
        final double g = normalize(phot_g_mean_mag, GaiaConst.min_phot_g_mean_mag, GaiaConst.max_phot_g_mean_mag);
        final double b = normalize(phot_bp_mean_mag, GaiaConst.min_phot_bp_mean_mag, GaiaConst.max_phot_bp_mean_mag);

        final int red = (int) Math.round(r * 255);
        final int green = (int) Math.round(g * 255);
        final int blue = (int) Math.round(b * 255);

        return new RGBA8(red, green, blue, 255);
    }

    public static double normalize(final double value, final double min, final double max) {
        return (value - min) / (max - min);
    }

    private static final double PARALLAX_ERROR_THRESHOLD = 0.75; //0.1; // Example threshold


    @Override
    public RGBA8 renderPixel(final int x, final int y, final RenderingData data) {

        if (data.phot_bp_mean_mag == null || data.phot_rp_mean_mag == null || data.phot_g_mean_mag == null) {
            return null;
        }
        if (data.bp_g == null || data.g_rp == null || data.bp_rp == null) {
            return null;
        }
        if (data.parallax == null) {
            return null;
        }

        //final double temperature = estimateTemperature(data.bp_rp);

        //System.out.println("Temperature: " + temperature);

        //final int temperatureIndex = (int) Math.round(temperature / 100.0) * 100;
        //final RGBA8 color = temeratureTable.get(temperatureIndex);


        //final RGBA8 color = blackbodyColor(temperature);

        //final RGBA8 color = RGBA8.ofTemperature(temperature);

        //final double minTemp = 3000;     // Minimum expected temperature
        //final double maxTemp = 8000;     // Maximum expected temperature
        //final RGBA8 color = temperatureToColor(temperature, minTemp, maxTemp);


        //final Float gMag = data.phot_g_mean_mag;
        //final double bpMag = data.phot_bp_mean_mag;
        //final double rpMag = data.phot_rp_mean_mag;
        //final double parallax = data.parallax;
        //final Float parallaxError = data.parallax_error;

        final RGBA8 color;

        //if (data.parallax_error != null && data.parallax_error > PARALLAX_ERROR_THRESHOLD) {
        //    color = calculateNewColor(data.phot_rp_mean_mag, data.phot_g_mean_mag, data.phot_bp_mean_mag);
        //} else {
        final double bpMinusRp = data.phot_bp_mean_mag - data.phot_rp_mean_mag;
        // Simplified temperature estimation with parallax adjustment
        final double temperature = 4500.0 / (0.92 * bpMinusRp + 1.7) + 1400
                + 500.0 * (1.0 / data.parallax - 1.0);

        // Adjust temperature uncertainty based on parallaxError
        //if (data.parallax_error != null) {
        //    final double tempUncertainty = 500.0 * (data.parallax_error / data.parallax);
        //    temperature += Math.random() * (2 * tempUncertainty) - tempUncertainty;
        //}

        final int alg = 1;

        if (alg == 1) {
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
            color = new RGBA8(r, g, b, 1);

        } else if (alg == 2) {

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


            color = new RGBA8((int) red, (int) green, (int) blue, 255);
        }

        //}

        //if (1 == 1)
        //    return color;

        samples[x][y].red += color.red;
        samples[x][y].green += color.green;
        samples[x][y].blue += color.blue;

        counts[x][y]++;

        //final int count = 481; // scale = 4
        //final int count = counts[x][y];

        if (counts[x][y] > maxCount) {
            maxCount = counts[x][y];
            System.out.println("Max Count: " + maxCount);
        }

        final int red2 = (int) Math.round((double) samples[x][y].red / (double) maxCount);
        final int green2 = (int) Math.round((double) samples[x][y].green / (double) maxCount);
        final int blue2 = (int) Math.round((double) samples[x][y].blue / (double) maxCount);

        return new RGBA8(red2, green2, blue2, 255).log10Scale(50);
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
        System.out.println("Temperature Renderer");
        System.out.println("Max Samples: " + getMaxSamples());
        System.out.println("Maximum Sample Count: " + getMaximumCount());
    }

    // Function to estimate the effective temperature from the BP-RP color index
    public static double estimateTemperature(final double bpRpIndex) {
        // Example coefficients, these are illustrative and not based on actual empirical data
        final double a = 4000;   // Base temperature coefficient
        final double b = -150;   // Linear term coefficient
        final double c = 10;     // Quadratic term coefficient

        // Calculate the temperature using the polynomial relationship
        final double temperature = a + b * bpRpIndex + c * Math.pow(bpRpIndex, 2);
        return temperature;
    }

    // Simple function to map temperature to RGB using a linear scale
    public static RGBA8 temperatureToColor(final double temperature, final double minTemp, final double maxTemp) {
        if (temperature < minTemp || temperature > maxTemp) {
            System.out.println("Temperature out of range: " + temperature + " (" + minTemp + " - " + maxTemp + ")");
        }
        final double normalized = (temperature - minTemp) / (maxTemp - minTemp); // Normalize to 0-1
        final int red = (int) (255 * normalized); // Scale up to 255
        final int blue = 255 - red;
        final int green = 0;
        return new RGBA8(red, green, blue);
    }

    public static RGBA8 blackbodyColor(final double temperature) {
        // Constants for the approximate fit
        final double[] r = {3.2406, -1.5372, -0.4986};
        final double[] g = {-0.9689, 1.8758, 0.0415};
        final double[] b = {0.0557, -0.2040, 1.0570};
        double x, y, z;
        x = y = z = 0;

        // Calculate XYZ from blackbody temperature using approximation
        if (temperature <= 4000) {
            x = 0.27475e9 / (temperature * temperature * temperature) + 0.98598e6 / (temperature * temperature) + 0.75211e3 / temperature + 0.31811;
        } else {
            x = -3.02522e9 / (temperature * temperature * temperature) + 2.10704e6 / (temperature * temperature) + 0.22293e3 / temperature + 0.24039;
        }

        if (temperature <= 2222) {
            y = -1.10638e9 / (temperature * temperature * temperature) + 0.10674e6 / (temperature * temperature) + 0.94611e3 / temperature + 0.14500;
        } else if (temperature <= 4000) {
            y = 3.08116e9 / (temperature * temperature * temperature) - 2.27124e6 / (temperature * temperature) + 0.73150e3 / temperature + 0.24517;
        } else {
            y = 0.33483e9 / (temperature * temperature * temperature) - 0.40925e6 / (temperature * temperature) + 0.79921e3 / temperature + 0.27218;
        }

        z = -7.70000e9 / (temperature * temperature * temperature) + 1.35579e6 / (temperature * temperature) + 0.56048e3 / temperature + 0.17991;

        // Convert XYZ to RGB
        double red = r[0] * x + r[1] * y + r[2] * z;
        double green = g[0] * x + g[1] * y + g[2] * z;
        double blue = b[0] * x + b[1] * y + b[2] * z;

        //System.out.println("RGB: " + red + ", " + green + ", " + blue);

        // Normalize and clamp
        red = Math.max(0, Math.min(255, 255.0 * red));
        green = Math.max(0, Math.min(255, 255.0 * green));
        blue = Math.max(0, Math.min(255, 255.0 * blue));

        return new RGBA8((int) red, (int) green, (int) blue);
    }

    // <span style="background:#ff3300">  1000 K   2deg  0.6499 0.3474  2.472e+06    1.0000 0.0337 0.0000  255  51   0  #ff3300</span>
    private static final Pattern PATTERN = Pattern.compile("^<span.*?>\\s+(\\d+)\\s+.*?(#\\w+)</span>$", Pattern.CASE_INSENSITIVE);


    public HashMap<Integer, RGBA8> processTemperatureColorTable() {
        final var colorTable = new HashMap<Integer, RGBA8>();
        final String address = "http://www.vendian.org/mncharity/dir3/blackbody/UnstableURLs/bbr_color.html";
        //final StringBuilder content = new StringBuilder();
        try {
            final var url = URI.create(address).toURL();
            final var connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            try (final var reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String data;
                int n = 0;
                while ((data = reader.readLine()) != null) {
                    final String line = data.trim();
                    if (!line.startsWith("#")) {
                        final Matcher matcher = PATTERN.matcher(line);
                        if (matcher.matches()) {
                            if (n % 2 == 0) {
                                final int temperature = Integer.parseInt(matcher.group(1));
                                final String hexColor = matcher.group(2);
                                final var color = RGBA8.from(Color.decode(hexColor));
                                System.out.println("blackbodyColors.put(" + temperature + ", new RGBA8(" + color.red + ", " + color.green + ", " + color.blue + ", 255));");
                                colorTable.put(temperature, color);
                            }
                        }
                    }
                    n++;
                }
            }
        } catch (final Exception e) {
            System.err.println(e.getLocalizedMessage());
            e.printStackTrace(System.err);
        }
        return colorTable;
    }

    static {
        blackbodyColors.put(1000, new RGBA8(255, 51, 0, 255));
        blackbodyColors.put(1100, new RGBA8(255, 69, 0, 255));
        blackbodyColors.put(1200, new RGBA8(255, 82, 0, 255));
        blackbodyColors.put(1300, new RGBA8(255, 93, 0, 255));
        blackbodyColors.put(1400, new RGBA8(255, 102, 0, 255));
        blackbodyColors.put(1500, new RGBA8(255, 111, 0, 255));
        blackbodyColors.put(1600, new RGBA8(255, 118, 0, 255));
        blackbodyColors.put(1700, new RGBA8(255, 124, 0, 255));
        blackbodyColors.put(1800, new RGBA8(255, 130, 0, 255));
        blackbodyColors.put(1900, new RGBA8(255, 135, 0, 255));
        blackbodyColors.put(2000, new RGBA8(255, 141, 11, 255));
        blackbodyColors.put(2100, new RGBA8(255, 146, 29, 255));
        blackbodyColors.put(2200, new RGBA8(255, 152, 41, 255));
        blackbodyColors.put(2300, new RGBA8(255, 157, 51, 255));
        blackbodyColors.put(2400, new RGBA8(255, 162, 60, 255));
        blackbodyColors.put(2500, new RGBA8(255, 166, 69, 255));
        blackbodyColors.put(2600, new RGBA8(255, 170, 77, 255));
        blackbodyColors.put(2700, new RGBA8(255, 174, 84, 255));
        blackbodyColors.put(2800, new RGBA8(255, 178, 91, 255));
        blackbodyColors.put(2900, new RGBA8(255, 182, 98, 255));
        blackbodyColors.put(3000, new RGBA8(255, 185, 105, 255));
        blackbodyColors.put(3100, new RGBA8(255, 189, 111, 255));
        blackbodyColors.put(3200, new RGBA8(255, 192, 118, 255));
        blackbodyColors.put(3300, new RGBA8(255, 195, 124, 255));
        blackbodyColors.put(3400, new RGBA8(255, 198, 130, 255));
        blackbodyColors.put(3500, new RGBA8(255, 201, 135, 255));
        blackbodyColors.put(3600, new RGBA8(255, 203, 141, 255));
        blackbodyColors.put(3700, new RGBA8(255, 206, 146, 255));
        blackbodyColors.put(3800, new RGBA8(255, 208, 151, 255));
        blackbodyColors.put(3900, new RGBA8(255, 211, 156, 255));
        blackbodyColors.put(4000, new RGBA8(255, 213, 161, 255));
        blackbodyColors.put(4100, new RGBA8(255, 215, 166, 255));
        blackbodyColors.put(4200, new RGBA8(255, 217, 171, 255));
        blackbodyColors.put(4300, new RGBA8(255, 219, 175, 255));
        blackbodyColors.put(4400, new RGBA8(255, 221, 180, 255));
        blackbodyColors.put(4500, new RGBA8(255, 223, 184, 255));
        blackbodyColors.put(4600, new RGBA8(255, 225, 188, 255));
        blackbodyColors.put(4700, new RGBA8(255, 226, 192, 255));
        blackbodyColors.put(4800, new RGBA8(255, 228, 196, 255));
        blackbodyColors.put(4900, new RGBA8(255, 229, 200, 255));
        blackbodyColors.put(5000, new RGBA8(255, 231, 204, 255));
        blackbodyColors.put(5100, new RGBA8(255, 232, 208, 255));
        blackbodyColors.put(5200, new RGBA8(255, 234, 211, 255));
        blackbodyColors.put(5300, new RGBA8(255, 235, 215, 255));
        blackbodyColors.put(5400, new RGBA8(255, 237, 218, 255));
        blackbodyColors.put(5500, new RGBA8(255, 238, 222, 255));
        blackbodyColors.put(5600, new RGBA8(255, 239, 225, 255));
        blackbodyColors.put(5700, new RGBA8(255, 240, 228, 255));
        blackbodyColors.put(5800, new RGBA8(255, 241, 231, 255));
        blackbodyColors.put(5900, new RGBA8(255, 243, 234, 255));
        blackbodyColors.put(6000, new RGBA8(255, 244, 237, 255));
        blackbodyColors.put(6100, new RGBA8(255, 245, 240, 255));
        blackbodyColors.put(6200, new RGBA8(255, 246, 243, 255));
        blackbodyColors.put(6300, new RGBA8(255, 247, 245, 255));
        blackbodyColors.put(6400, new RGBA8(255, 248, 248, 255));
        blackbodyColors.put(6500, new RGBA8(255, 249, 251, 255));
        blackbodyColors.put(6600, new RGBA8(255, 249, 253, 255));
        blackbodyColors.put(6700, new RGBA8(254, 250, 255, 255));
        blackbodyColors.put(6800, new RGBA8(252, 248, 255, 255));
        blackbodyColors.put(6900, new RGBA8(250, 247, 255, 255));
        blackbodyColors.put(7000, new RGBA8(247, 245, 255, 255));
        blackbodyColors.put(7100, new RGBA8(245, 244, 255, 255));
        blackbodyColors.put(7200, new RGBA8(243, 243, 255, 255));
        blackbodyColors.put(7300, new RGBA8(241, 241, 255, 255));
        blackbodyColors.put(7400, new RGBA8(239, 240, 255, 255));
        blackbodyColors.put(7500, new RGBA8(238, 239, 255, 255));
        blackbodyColors.put(7600, new RGBA8(236, 238, 255, 255));
        blackbodyColors.put(7700, new RGBA8(234, 237, 255, 255));
        blackbodyColors.put(7800, new RGBA8(233, 236, 255, 255));
        blackbodyColors.put(7900, new RGBA8(231, 234, 255, 255));
        blackbodyColors.put(8000, new RGBA8(229, 233, 255, 255));
        blackbodyColors.put(8100, new RGBA8(228, 233, 255, 255));
        blackbodyColors.put(8200, new RGBA8(227, 232, 255, 255));
        blackbodyColors.put(8300, new RGBA8(225, 231, 255, 255));
        blackbodyColors.put(8400, new RGBA8(224, 230, 255, 255));
        blackbodyColors.put(8500, new RGBA8(223, 229, 255, 255));
        blackbodyColors.put(8600, new RGBA8(221, 228, 255, 255));
        blackbodyColors.put(8700, new RGBA8(220, 227, 255, 255));
        blackbodyColors.put(8800, new RGBA8(219, 226, 255, 255));
        blackbodyColors.put(8900, new RGBA8(218, 226, 255, 255));
        blackbodyColors.put(9000, new RGBA8(217, 225, 255, 255));
        blackbodyColors.put(9100, new RGBA8(216, 224, 255, 255));
        blackbodyColors.put(9200, new RGBA8(215, 223, 255, 255));
        blackbodyColors.put(9300, new RGBA8(214, 223, 255, 255));
        blackbodyColors.put(9400, new RGBA8(213, 222, 255, 255));
        blackbodyColors.put(9500, new RGBA8(212, 221, 255, 255));
        blackbodyColors.put(9600, new RGBA8(211, 221, 255, 255));
        blackbodyColors.put(9700, new RGBA8(210, 220, 255, 255));
        blackbodyColors.put(9800, new RGBA8(209, 220, 255, 255));
        blackbodyColors.put(9900, new RGBA8(208, 219, 255, 255));
        blackbodyColors.put(10000, new RGBA8(207, 218, 255, 255));
        blackbodyColors.put(10100, new RGBA8(207, 218, 255, 255));
        blackbodyColors.put(10200, new RGBA8(206, 217, 255, 255));
        blackbodyColors.put(10300, new RGBA8(205, 217, 255, 255));
        blackbodyColors.put(10400, new RGBA8(204, 216, 255, 255));
        blackbodyColors.put(10500, new RGBA8(204, 216, 255, 255));
        blackbodyColors.put(10600, new RGBA8(203, 215, 255, 255));
        blackbodyColors.put(10700, new RGBA8(202, 215, 255, 255));
        blackbodyColors.put(10800, new RGBA8(202, 214, 255, 255));
        blackbodyColors.put(10900, new RGBA8(201, 214, 255, 255));
        blackbodyColors.put(11000, new RGBA8(200, 213, 255, 255));
        blackbodyColors.put(11100, new RGBA8(200, 213, 255, 255));
        blackbodyColors.put(11200, new RGBA8(199, 212, 255, 255));
        blackbodyColors.put(11300, new RGBA8(198, 212, 255, 255));
        blackbodyColors.put(11400, new RGBA8(198, 212, 255, 255));
        blackbodyColors.put(11500, new RGBA8(197, 211, 255, 255));
        blackbodyColors.put(11600, new RGBA8(197, 211, 255, 255));
        blackbodyColors.put(11700, new RGBA8(196, 210, 255, 255));
        blackbodyColors.put(11800, new RGBA8(196, 210, 255, 255));
        blackbodyColors.put(11900, new RGBA8(195, 210, 255, 255));
        blackbodyColors.put(12000, new RGBA8(195, 209, 255, 255));
        blackbodyColors.put(12100, new RGBA8(194, 209, 255, 255));
        blackbodyColors.put(12200, new RGBA8(194, 208, 255, 255));
        blackbodyColors.put(12300, new RGBA8(193, 208, 255, 255));
        blackbodyColors.put(12400, new RGBA8(193, 208, 255, 255));
        blackbodyColors.put(12500, new RGBA8(192, 207, 255, 255));
        blackbodyColors.put(12600, new RGBA8(192, 207, 255, 255));
        blackbodyColors.put(12700, new RGBA8(191, 207, 255, 255));
        blackbodyColors.put(12800, new RGBA8(191, 206, 255, 255));
        blackbodyColors.put(12900, new RGBA8(190, 206, 255, 255));
        blackbodyColors.put(13000, new RGBA8(190, 206, 255, 255));
        blackbodyColors.put(13100, new RGBA8(190, 206, 255, 255));
        blackbodyColors.put(13200, new RGBA8(189, 205, 255, 255));
        blackbodyColors.put(13300, new RGBA8(189, 205, 255, 255));
        blackbodyColors.put(13400, new RGBA8(188, 205, 255, 255));
        blackbodyColors.put(13500, new RGBA8(188, 204, 255, 255));
        blackbodyColors.put(13600, new RGBA8(188, 204, 255, 255));
        blackbodyColors.put(13700, new RGBA8(187, 204, 255, 255));
        blackbodyColors.put(13800, new RGBA8(187, 204, 255, 255));
        blackbodyColors.put(13900, new RGBA8(187, 203, 255, 255));
        blackbodyColors.put(14000, new RGBA8(186, 203, 255, 255));
        blackbodyColors.put(14100, new RGBA8(186, 203, 255, 255));
        blackbodyColors.put(14200, new RGBA8(186, 203, 255, 255));
        blackbodyColors.put(14300, new RGBA8(185, 202, 255, 255));
        blackbodyColors.put(14400, new RGBA8(185, 202, 255, 255));
        blackbodyColors.put(14500, new RGBA8(185, 202, 255, 255));
        blackbodyColors.put(14600, new RGBA8(184, 202, 255, 255));
        blackbodyColors.put(14700, new RGBA8(184, 201, 255, 255));
        blackbodyColors.put(14800, new RGBA8(184, 201, 255, 255));
        blackbodyColors.put(14900, new RGBA8(184, 201, 255, 255));
        blackbodyColors.put(15000, new RGBA8(183, 201, 255, 255));
        blackbodyColors.put(15100, new RGBA8(183, 201, 255, 255));
        blackbodyColors.put(15200, new RGBA8(183, 200, 255, 255));
        blackbodyColors.put(15300, new RGBA8(182, 200, 255, 255));
        blackbodyColors.put(15400, new RGBA8(182, 200, 255, 255));
        blackbodyColors.put(15500, new RGBA8(182, 200, 255, 255));
        blackbodyColors.put(15600, new RGBA8(182, 200, 255, 255));
        blackbodyColors.put(15700, new RGBA8(181, 199, 255, 255));
        blackbodyColors.put(15800, new RGBA8(181, 199, 255, 255));
        blackbodyColors.put(15900, new RGBA8(181, 199, 255, 255));
        blackbodyColors.put(16000, new RGBA8(181, 199, 255, 255));
        blackbodyColors.put(16100, new RGBA8(180, 199, 255, 255));
        blackbodyColors.put(16200, new RGBA8(180, 198, 255, 255));
        blackbodyColors.put(16300, new RGBA8(180, 198, 255, 255));
        blackbodyColors.put(16400, new RGBA8(180, 198, 255, 255));
        blackbodyColors.put(16500, new RGBA8(179, 198, 255, 255));
        blackbodyColors.put(16600, new RGBA8(179, 198, 255, 255));
        blackbodyColors.put(16700, new RGBA8(179, 198, 255, 255));
        blackbodyColors.put(16800, new RGBA8(179, 197, 255, 255));
        blackbodyColors.put(16900, new RGBA8(179, 197, 255, 255));
        blackbodyColors.put(17000, new RGBA8(178, 197, 255, 255));
        blackbodyColors.put(17100, new RGBA8(178, 197, 255, 255));
        blackbodyColors.put(17200, new RGBA8(178, 197, 255, 255));
        blackbodyColors.put(17300, new RGBA8(178, 197, 255, 255));
        blackbodyColors.put(17400, new RGBA8(178, 196, 255, 255));
        blackbodyColors.put(17500, new RGBA8(177, 196, 255, 255));
        blackbodyColors.put(17600, new RGBA8(177, 196, 255, 255));
        blackbodyColors.put(17700, new RGBA8(177, 196, 255, 255));
        blackbodyColors.put(17800, new RGBA8(177, 196, 255, 255));
        blackbodyColors.put(17900, new RGBA8(177, 196, 255, 255));
        blackbodyColors.put(18000, new RGBA8(176, 196, 255, 255));
        blackbodyColors.put(18100, new RGBA8(176, 195, 255, 255));
        blackbodyColors.put(18200, new RGBA8(176, 195, 255, 255));
        blackbodyColors.put(18300, new RGBA8(176, 195, 255, 255));
        blackbodyColors.put(18400, new RGBA8(176, 195, 255, 255));
        blackbodyColors.put(18500, new RGBA8(176, 195, 255, 255));
        blackbodyColors.put(18600, new RGBA8(175, 195, 255, 255));
        blackbodyColors.put(18700, new RGBA8(175, 195, 255, 255));
        blackbodyColors.put(18800, new RGBA8(175, 194, 255, 255));
        blackbodyColors.put(18900, new RGBA8(175, 194, 255, 255));
        blackbodyColors.put(19000, new RGBA8(175, 194, 255, 255));
        blackbodyColors.put(19100, new RGBA8(175, 194, 255, 255));
        blackbodyColors.put(19200, new RGBA8(174, 194, 255, 255));
        blackbodyColors.put(19300, new RGBA8(174, 194, 255, 255));
        blackbodyColors.put(19400, new RGBA8(174, 194, 255, 255));
        blackbodyColors.put(19500, new RGBA8(174, 194, 255, 255));
        blackbodyColors.put(19600, new RGBA8(174, 194, 255, 255));
        blackbodyColors.put(19700, new RGBA8(174, 193, 255, 255));
        blackbodyColors.put(19800, new RGBA8(174, 193, 255, 255));
        blackbodyColors.put(19900, new RGBA8(173, 193, 255, 255));
        blackbodyColors.put(20000, new RGBA8(173, 193, 255, 255));
        blackbodyColors.put(20100, new RGBA8(173, 193, 255, 255));
        blackbodyColors.put(20200, new RGBA8(173, 193, 255, 255));
        blackbodyColors.put(20300, new RGBA8(173, 193, 255, 255));
        blackbodyColors.put(20400, new RGBA8(173, 193, 255, 255));
        blackbodyColors.put(20500, new RGBA8(173, 193, 255, 255));
        blackbodyColors.put(20600, new RGBA8(173, 192, 255, 255));
        blackbodyColors.put(20700, new RGBA8(172, 192, 255, 255));
        blackbodyColors.put(20800, new RGBA8(172, 192, 255, 255));
        blackbodyColors.put(20900, new RGBA8(172, 192, 255, 255));
        blackbodyColors.put(21000, new RGBA8(172, 192, 255, 255));
        blackbodyColors.put(21100, new RGBA8(172, 192, 255, 255));
        blackbodyColors.put(21200, new RGBA8(172, 192, 255, 255));
        blackbodyColors.put(21300, new RGBA8(172, 192, 255, 255));
        blackbodyColors.put(21400, new RGBA8(172, 192, 255, 255));
        blackbodyColors.put(21500, new RGBA8(171, 192, 255, 255));
        blackbodyColors.put(21600, new RGBA8(171, 192, 255, 255));
        blackbodyColors.put(21700, new RGBA8(171, 191, 255, 255));
        blackbodyColors.put(21800, new RGBA8(171, 191, 255, 255));
        blackbodyColors.put(21900, new RGBA8(171, 191, 255, 255));
        blackbodyColors.put(22000, new RGBA8(171, 191, 255, 255));
        blackbodyColors.put(22100, new RGBA8(171, 191, 255, 255));
        blackbodyColors.put(22200, new RGBA8(171, 191, 255, 255));
        blackbodyColors.put(22300, new RGBA8(171, 191, 255, 255));
        blackbodyColors.put(22400, new RGBA8(170, 191, 255, 255));
        blackbodyColors.put(22500, new RGBA8(170, 191, 255, 255));
        blackbodyColors.put(22600, new RGBA8(170, 191, 255, 255));
        blackbodyColors.put(22700, new RGBA8(170, 191, 255, 255));
        blackbodyColors.put(22800, new RGBA8(170, 190, 255, 255));
        blackbodyColors.put(22900, new RGBA8(170, 190, 255, 255));
        blackbodyColors.put(23000, new RGBA8(170, 190, 255, 255));
        blackbodyColors.put(23100, new RGBA8(170, 190, 255, 255));
        blackbodyColors.put(23200, new RGBA8(170, 190, 255, 255));
        blackbodyColors.put(23300, new RGBA8(170, 190, 255, 255));
        blackbodyColors.put(23400, new RGBA8(169, 190, 255, 255));
        blackbodyColors.put(23500, new RGBA8(169, 190, 255, 255));
        blackbodyColors.put(23600, new RGBA8(169, 190, 255, 255));
        blackbodyColors.put(23700, new RGBA8(169, 190, 255, 255));
        blackbodyColors.put(23800, new RGBA8(169, 190, 255, 255));
        blackbodyColors.put(23900, new RGBA8(169, 190, 255, 255));
        blackbodyColors.put(24000, new RGBA8(169, 190, 255, 255));
        blackbodyColors.put(24100, new RGBA8(169, 190, 255, 255));
        blackbodyColors.put(24200, new RGBA8(169, 189, 255, 255));
        blackbodyColors.put(24300, new RGBA8(169, 189, 255, 255));
        blackbodyColors.put(24400, new RGBA8(169, 189, 255, 255));
        blackbodyColors.put(24500, new RGBA8(168, 189, 255, 255));
        blackbodyColors.put(24600, new RGBA8(168, 189, 255, 255));
        blackbodyColors.put(24700, new RGBA8(168, 189, 255, 255));
        blackbodyColors.put(24800, new RGBA8(168, 189, 255, 255));
        blackbodyColors.put(24900, new RGBA8(168, 189, 255, 255));
        blackbodyColors.put(25000, new RGBA8(168, 189, 255, 255));
        blackbodyColors.put(25100, new RGBA8(168, 189, 255, 255));
        blackbodyColors.put(25200, new RGBA8(168, 189, 255, 255));
        blackbodyColors.put(25300, new RGBA8(168, 189, 255, 255));
        blackbodyColors.put(25400, new RGBA8(168, 189, 255, 255));
        blackbodyColors.put(25500, new RGBA8(168, 189, 255, 255));
        blackbodyColors.put(25600, new RGBA8(168, 189, 255, 255));
        blackbodyColors.put(25700, new RGBA8(167, 188, 255, 255));
        blackbodyColors.put(25800, new RGBA8(167, 188, 255, 255));
        blackbodyColors.put(25900, new RGBA8(167, 188, 255, 255));
        blackbodyColors.put(26000, new RGBA8(167, 188, 255, 255));
        blackbodyColors.put(26100, new RGBA8(167, 188, 255, 255));
        blackbodyColors.put(26200, new RGBA8(167, 188, 255, 255));
        blackbodyColors.put(26300, new RGBA8(167, 188, 255, 255));
        blackbodyColors.put(26400, new RGBA8(167, 188, 255, 255));
        blackbodyColors.put(26500, new RGBA8(167, 188, 255, 255));
        blackbodyColors.put(26600, new RGBA8(167, 188, 255, 255));
        blackbodyColors.put(26700, new RGBA8(167, 188, 255, 255));
        blackbodyColors.put(26800, new RGBA8(167, 188, 255, 255));
        blackbodyColors.put(26900, new RGBA8(167, 188, 255, 255));
        blackbodyColors.put(27000, new RGBA8(167, 188, 255, 255));
        blackbodyColors.put(27100, new RGBA8(166, 188, 255, 255));
        blackbodyColors.put(27200, new RGBA8(166, 188, 255, 255));
        blackbodyColors.put(27300, new RGBA8(166, 188, 255, 255));
        blackbodyColors.put(27400, new RGBA8(166, 187, 255, 255));
        blackbodyColors.put(27500, new RGBA8(166, 187, 255, 255));
        blackbodyColors.put(27600, new RGBA8(166, 187, 255, 255));
        blackbodyColors.put(27700, new RGBA8(166, 187, 255, 255));
        blackbodyColors.put(27800, new RGBA8(166, 187, 255, 255));
        blackbodyColors.put(27900, new RGBA8(166, 187, 255, 255));
        blackbodyColors.put(28000, new RGBA8(166, 187, 255, 255));
        blackbodyColors.put(28100, new RGBA8(166, 187, 255, 255));
        blackbodyColors.put(28200, new RGBA8(166, 187, 255, 255));
        blackbodyColors.put(28300, new RGBA8(166, 187, 255, 255));
        blackbodyColors.put(28400, new RGBA8(166, 187, 255, 255));
        blackbodyColors.put(28500, new RGBA8(166, 187, 255, 255));
        blackbodyColors.put(28600, new RGBA8(166, 187, 255, 255));
        blackbodyColors.put(28700, new RGBA8(165, 187, 255, 255));
        blackbodyColors.put(28800, new RGBA8(165, 187, 255, 255));
        blackbodyColors.put(28900, new RGBA8(165, 187, 255, 255));
        blackbodyColors.put(29000, new RGBA8(165, 187, 255, 255));
        blackbodyColors.put(29100, new RGBA8(165, 187, 255, 255));
        blackbodyColors.put(29200, new RGBA8(165, 187, 255, 255));
        blackbodyColors.put(29300, new RGBA8(165, 187, 255, 255));
        blackbodyColors.put(29400, new RGBA8(165, 187, 255, 255));
        blackbodyColors.put(29500, new RGBA8(165, 186, 255, 255));
        blackbodyColors.put(29600, new RGBA8(165, 186, 255, 255));
        blackbodyColors.put(29700, new RGBA8(165, 186, 255, 255));
        blackbodyColors.put(29800, new RGBA8(165, 186, 255, 255));
        blackbodyColors.put(29900, new RGBA8(165, 186, 255, 255));
        blackbodyColors.put(30000, new RGBA8(165, 186, 255, 255));
        blackbodyColors.put(30100, new RGBA8(165, 186, 255, 255));
        blackbodyColors.put(30200, new RGBA8(165, 186, 255, 255));
        blackbodyColors.put(30300, new RGBA8(165, 186, 255, 255));
        blackbodyColors.put(30400, new RGBA8(165, 186, 255, 255));
        blackbodyColors.put(30500, new RGBA8(165, 186, 255, 255));
        blackbodyColors.put(30600, new RGBA8(164, 186, 255, 255));
        blackbodyColors.put(30700, new RGBA8(164, 186, 255, 255));
        blackbodyColors.put(30800, new RGBA8(164, 186, 255, 255));
        blackbodyColors.put(30900, new RGBA8(164, 186, 255, 255));
        blackbodyColors.put(31000, new RGBA8(164, 186, 255, 255));
        blackbodyColors.put(31100, new RGBA8(164, 186, 255, 255));
        blackbodyColors.put(31200, new RGBA8(164, 186, 255, 255));
        blackbodyColors.put(31300, new RGBA8(164, 186, 255, 255));
        blackbodyColors.put(31400, new RGBA8(164, 186, 255, 255));
        blackbodyColors.put(31500, new RGBA8(164, 186, 255, 255));
        blackbodyColors.put(31600, new RGBA8(164, 186, 255, 255));
        blackbodyColors.put(31700, new RGBA8(164, 186, 255, 255));
        blackbodyColors.put(31800, new RGBA8(164, 186, 255, 255));
        blackbodyColors.put(31900, new RGBA8(164, 186, 255, 255));
        blackbodyColors.put(32000, new RGBA8(164, 185, 255, 255));
        blackbodyColors.put(32100, new RGBA8(164, 185, 255, 255));
        blackbodyColors.put(32200, new RGBA8(164, 185, 255, 255));
        blackbodyColors.put(32300, new RGBA8(164, 185, 255, 255));
        blackbodyColors.put(32400, new RGBA8(164, 185, 255, 255));
        blackbodyColors.put(32500, new RGBA8(164, 185, 255, 255));
        blackbodyColors.put(32600, new RGBA8(164, 185, 255, 255));
        blackbodyColors.put(32700, new RGBA8(163, 185, 255, 255));
        blackbodyColors.put(32800, new RGBA8(163, 185, 255, 255));
        blackbodyColors.put(32900, new RGBA8(163, 185, 255, 255));
        blackbodyColors.put(33000, new RGBA8(163, 185, 255, 255));
        blackbodyColors.put(33100, new RGBA8(163, 185, 255, 255));
        blackbodyColors.put(33200, new RGBA8(163, 185, 255, 255));
        blackbodyColors.put(33300, new RGBA8(163, 185, 255, 255));
        blackbodyColors.put(33400, new RGBA8(163, 185, 255, 255));
        blackbodyColors.put(33500, new RGBA8(163, 185, 255, 255));
        blackbodyColors.put(33600, new RGBA8(163, 185, 255, 255));
        blackbodyColors.put(33700, new RGBA8(163, 185, 255, 255));
        blackbodyColors.put(33800, new RGBA8(163, 185, 255, 255));
        blackbodyColors.put(33900, new RGBA8(163, 185, 255, 255));
        blackbodyColors.put(34000, new RGBA8(163, 185, 255, 255));
        blackbodyColors.put(34100, new RGBA8(163, 185, 255, 255));
        blackbodyColors.put(34200, new RGBA8(163, 185, 255, 255));
        blackbodyColors.put(34300, new RGBA8(163, 185, 255, 255));
        blackbodyColors.put(34400, new RGBA8(163, 185, 255, 255));
        blackbodyColors.put(34500, new RGBA8(163, 185, 255, 255));
        blackbodyColors.put(34600, new RGBA8(163, 185, 255, 255));
        blackbodyColors.put(34700, new RGBA8(163, 185, 255, 255));
        blackbodyColors.put(34800, new RGBA8(163, 185, 255, 255));
        blackbodyColors.put(34900, new RGBA8(163, 185, 255, 255));
        blackbodyColors.put(35000, new RGBA8(163, 184, 255, 255));
        blackbodyColors.put(35100, new RGBA8(163, 184, 255, 255));
        blackbodyColors.put(35200, new RGBA8(162, 184, 255, 255));
        blackbodyColors.put(35300, new RGBA8(162, 184, 255, 255));
        blackbodyColors.put(35400, new RGBA8(162, 184, 255, 255));
        blackbodyColors.put(35500, new RGBA8(162, 184, 255, 255));
        blackbodyColors.put(35600, new RGBA8(162, 184, 255, 255));
        blackbodyColors.put(35700, new RGBA8(162, 184, 255, 255));
        blackbodyColors.put(35800, new RGBA8(162, 184, 255, 255));
        blackbodyColors.put(35900, new RGBA8(162, 184, 255, 255));
        blackbodyColors.put(36000, new RGBA8(162, 184, 255, 255));
        blackbodyColors.put(36100, new RGBA8(162, 184, 255, 255));
        blackbodyColors.put(36200, new RGBA8(162, 184, 255, 255));
        blackbodyColors.put(36300, new RGBA8(162, 184, 255, 255));
        blackbodyColors.put(36400, new RGBA8(162, 184, 255, 255));
        blackbodyColors.put(36500, new RGBA8(162, 184, 255, 255));
        blackbodyColors.put(36600, new RGBA8(162, 184, 255, 255));
        blackbodyColors.put(36700, new RGBA8(162, 184, 255, 255));
        blackbodyColors.put(36800, new RGBA8(162, 184, 255, 255));
        blackbodyColors.put(36900, new RGBA8(162, 184, 255, 255));
        blackbodyColors.put(37000, new RGBA8(162, 184, 255, 255));
        blackbodyColors.put(37100, new RGBA8(162, 184, 255, 255));
        blackbodyColors.put(37200, new RGBA8(162, 184, 255, 255));
        blackbodyColors.put(37300, new RGBA8(162, 184, 255, 255));
        blackbodyColors.put(37400, new RGBA8(162, 184, 255, 255));
        blackbodyColors.put(37500, new RGBA8(162, 184, 255, 255));
        blackbodyColors.put(37600, new RGBA8(162, 184, 255, 255));
        blackbodyColors.put(37700, new RGBA8(162, 184, 255, 255));
        blackbodyColors.put(37800, new RGBA8(162, 184, 255, 255));
        blackbodyColors.put(37900, new RGBA8(162, 184, 255, 255));
        blackbodyColors.put(38000, new RGBA8(162, 184, 255, 255));
        blackbodyColors.put(38100, new RGBA8(162, 184, 255, 255));
        blackbodyColors.put(38200, new RGBA8(162, 184, 255, 255));
        blackbodyColors.put(38300, new RGBA8(161, 184, 255, 255));
        blackbodyColors.put(38400, new RGBA8(161, 184, 255, 255));
        blackbodyColors.put(38500, new RGBA8(161, 184, 255, 255));
        blackbodyColors.put(38600, new RGBA8(161, 183, 255, 255));
        blackbodyColors.put(38700, new RGBA8(161, 183, 255, 255));
        blackbodyColors.put(38800, new RGBA8(161, 183, 255, 255));
        blackbodyColors.put(38900, new RGBA8(161, 183, 255, 255));
        blackbodyColors.put(39000, new RGBA8(161, 183, 255, 255));
        blackbodyColors.put(39100, new RGBA8(161, 183, 255, 255));
        blackbodyColors.put(39200, new RGBA8(161, 183, 255, 255));
        blackbodyColors.put(39300, new RGBA8(161, 183, 255, 255));
        blackbodyColors.put(39400, new RGBA8(161, 183, 255, 255));
        blackbodyColors.put(39500, new RGBA8(161, 183, 255, 255));
        blackbodyColors.put(39600, new RGBA8(161, 183, 255, 255));
        blackbodyColors.put(39700, new RGBA8(161, 183, 255, 255));
        blackbodyColors.put(39800, new RGBA8(161, 183, 255, 255));
        blackbodyColors.put(39900, new RGBA8(161, 183, 255, 255));
        blackbodyColors.put(40000, new RGBA8(161, 183, 255, 255));
    }

}
