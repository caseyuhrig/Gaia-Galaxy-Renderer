package caseyuhrig.gaia.renderer;

import caseyuhrig.gaia.GaiaTemperatureColors;
import caseyuhrig.gaia.GraphicUtils;
import caseyuhrig.gaia.RGBA8;
import caseyuhrig.gaia.RenderingData;
import caseyuhrig.image.FluxToColor;
import caseyuhrig.image.GammaCorrection;

import java.awt.*;
import java.awt.image.BufferedImage;

import static caseyuhrig.gaia.GaiaConst.*;


public class UberPixelRenderer extends PixelRenderer {

    private final int[][] counts;
    private final double[][][] colors;
    //private final double[][][] image;


    public UberPixelRenderer(final int width, final int height, final int scale) {
        super(width, height, scale);
        // zero out the counts
        counts = new int[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                counts[x][y] = 0;
            }
        }
        // zero out the colors
        colors = new double[width][height][3];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                colors[x][y] = new double[]{0, 0, 0};
            }
        }
        // zero out the image
        /*
        image = new double[width][height][3];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image[x][y] = new double[]{0, 0, 0};
            }
        }
        */
        maxSamples = 1807; // scale 1
        //maxSamples = 150; // scale 4

        GaiaTemperatureColors.initRGBTables();
    }


    final double av = 0.5; // Example extinction value
    final double exposure = 1.0; // Example exposure value
    final double saturationFactor = 2.5; // Example saturation factor (50%)
    //final double sunAngle = 45.0; // Example angle between Sun and star in degrees
    final double[] sunColor = {1.0, 0.95, 0.9}; // Example Sun color (normalized RGB)

    public final static double flux_to_vega = 5.3095E-11; // approximate conversion factor for phot_g_mean_flux to intensity relative to Vega


    @Override
    public RGBA8 renderPixel(final int x, final int y, final RenderingData data) {
        /*
        if (data.phot_g_mean_flux == null || data.phot_rp_mean_flux == null || data.phot_bp_mean_flux == null || data.teff_gspphot == null || data.parallax == null) {
            return null;
        }
        if (data.bp_g == null || data.g_rp == null || data.bp_rp == null) {
            return null;
        }
        */

        counts[x][y]++;

        if (counts[x][y] > maxSamples) {
            System.out.println("Max samples: " + counts[x][y]);
            maxSamples = counts[x][y];
        }

        //final double maxSamples = this.maxSamples;

        //bsr_config->red_filter_long_limit=705.0;
        //bsr_config->red_filter_short_limit=550.0;
        //bsr_config->green_filter_long_limit=600.0;
        //bsr_config->green_filter_short_limit=445.0;
        //bsr_config->blue_filter_long_limit=465.0;
        //bsr_config->blue_filter_short_limit=395.0;

        final int density = counts[x][y] / maxSamples;

        final double red = normalize(data.phot_rp_mean_mag, min_phot_rp_mean_mag, max_phot_rp_mean_mag);
        final double green = normalize(data.phot_g_mean_mag, min_phot_g_mean_mag, max_phot_g_mean_mag);
        final double blue = normalize(data.phot_bp_mean_mag, min_phot_bp_mean_mag, max_phot_bp_mean_mag);


        //final double[] bpRgb = FluxToColor.fluxToRgb(new double[]{red}, 330.0, 680.0);
        //final double[] gRgb = FluxToColor.fluxToRgb(new double[]{green}, 330.0, 1050.0);
        //final double[] rpRgb = FluxToColor.fluxToRgb(new double[]{blue}, 640.0, 1050.0);
        final double[] bpRgb = FluxToColor.fluxToRgb(new double[]{red}, 550.0, 705.0);
        final double[] gRgb = FluxToColor.fluxToRgb(new double[]{green}, 445.0, 600.0);
        final double[] rpRgb = FluxToColor.fluxToRgb(new double[]{blue}, 395.0, 465.0);

        //final double r = data.phot_rp_mean_mag / data.phot_g_mean_mag;
        //final double b = data.phot_bp_mean_mag / data.phot_g_mean_mag;
        //final double g = b / r;

        double r = data.phot_rp_mean_flux / data.phot_g_mean_flux;
        double b = data.phot_bp_mean_flux / data.phot_g_mean_flux;
        double g = b / r;

        final int temp = data.teff_gspphot.intValue();
        if (temp < 32768) {
            final double rr = GaiaTemperatureColors.state.getRgbRed()[temp];
            final double gg = GaiaTemperatureColors.state.getRgbGreen()[temp];
            final double bb = GaiaTemperatureColors.state.getRgbBlue()[temp];

            final double linear_intensity = data.phot_g_mean_flux * flux_to_vega;
            //System.out.println("linear_intensity: " + linear_intensity);
            //final double magnitude = -2.5 * Math.log10(linear_intensity); // some stars have blank phot_G_mean_magnitude so we derive from the more reliable flux column
            //System.out.println("magnitude: " + magnitude);
            //final double star_r2 = Math.pow(10.0, (data.phot_g_mean_mag - data.ag_gspphot) / 2.5);
            //final double distance = 1000.0 / data.parallax;
            //final double linear_1pc_intensity = (float) (linear_intensity * Math.pow(distance, 2.0));
            //final double linear_intensity = linear_1pc_intensity / star_r2;
            //linear_intensity_undimmed=pow(100.0, (-(magnitude - data.ag_gspphot) / 5.0));

            //r = linear_intensity * rr;
            //g = linear_intensity * gg;
            //b = linear_intensity * bb;

            r = rr + green + r;
            g = gg + green + g;
            b = bb + green + b;

            // orig. c code
            //r = (linear_intensity * bsr_state -> rgb_red[color_temperature]);
            //g = (linear_intensity * bsr_state -> rgb_green[color_temperature]);
            //b = (linear_intensity * bsr_state -> rgb_blue[color_temperature]);

        } else {
            r = 1.0;
            g = 1.0;
            b = 1.0;
        }

        //final double[] rgb = average(rpRgb, gRgb, bpRgb);
        //final double[] rgb = new double[]{bpRgb[0], gRgb[1], rpRgb[2]};
        //final double[] rgb = normalize(new double[]{r, g, b});
        final double[] rgb = new double[]{r, g, b};
        //final double[] rgb = FluxToColor.fluxToRgb(new double[]{red, green, blue});
        //final double[] rgb = new double[]{red, green, blue}; //ColorConverter.cmyToRgb(blue, red, green);


        for (int i = 0; i < 3; i++) {
            colors[x][y][i] += rgb[i]; // * 6.0 * grad2.rgb()[i] * growth * 2.0; //out2[i]; //rgb[i];
        }
        //colors[x][y] = rgb;

        final double[] c9 = divide(colors[x][y], maxSamples);

        //final double[] gradient = GradientPalette.densityPalette(maxSamples).get(counts[x][y]);
        //final double[] gradColor = multiply(gradient, growth);
        //final double[] tempColor = divide(colors[x][y], maxSamples);
        //final double[] c9 = multiply(multiply(gradColor, tempColor), 30.0); // scale 1 = 4.0, scale 4 = 30.0

        //final double[] c = GammaCorrection.srgbGammaCorrect(divide(colors[x][y], maxSamples));
        final double[] c = GammaCorrection.srgbGammaCorrect(c9);

        //image[x][y] = c;

        //final double[] c = GammaCorrection.inverseGammaCorrect(c9);
        //final double[] c = GammaCorrection.gammaExpansion(c9, 2.2); // 2.2 > 1.2 > 0.8 less bright than sRGB gamma correction
        //final double[] c = GammaCorrection.gammaCompression(c9, 2.2);

        //final double[] colorAfterExtinction = DustExtinction.applyDustExtinction(c, av);
        //final double[] colorAfterRayleigh = LightScattering.applyRayleighScattering(colorAfterExtinction, distance);
        //final double[] colorAfterMie = LightScattering.applyMieScattering(colorAfterRayleigh, distance);
        //final double[] colorAfterSunInfluence = SunInfluence.calculateSunlightInfluence(colorAfterMie, sunColor, sunAngle);

        //final RGBA8 c1 = new RGBA8(c);

        //final double[] c3 = GalaxyRenderer.applyColorWeights(new double[]{red, green, blue}, GalaxyRenderer.WEIGHTS1);

        //final RGBA8 c2 = c1.log10Scale(20);

        return new RGBA8(c);
    }


    @Override
    public void printStatistics() {
        System.out.println("Post post-processing image...");
        double maxColor = 0.0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                final double[] c = colors[x][y];
                for (int i = 0; i < 3; i++) {
                    if (c[i] > maxColor) {
                        maxColor = c[i];
                    }
                }
            }
        }
        final double ms = maxSamples;
        final var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D imageG = image.createGraphics();
        imageG.setColor(Color.BLACK);
        imageG.fillRect(0, 0, width, height);
        imageG.dispose();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                final double[] c = colors[x][y];
                final double r = c[0] / maxColor;
                final double g = c[1] / maxColor;
                final double b = c[2] / maxColor;
                final double[] c2 = GammaCorrection.srgbGammaCorrect(new double[]{r, g, b});
                final var color = new RGBA8(c2).argb();
                image.setRGB(x, y, color);
            }
        }
        // create a new image file
        final String namePrefix = "gaia-" + this.getClass().getSimpleName() + "-post";
        GraphicUtils.saveImage(image, "C:/tmp", namePrefix, ".png");
        System.out.println("Post Image saved to file.");

        System.out.println(getClass().getSimpleName() + " Statistics");
        System.out.println("-------------------------------------------------------");
    /*
        try {
            output.flush();
        } catch (final Throwable throwable) {
            System.err.println(throwable.getLocalizedMessage());
            throwable.printStackTrace(System.err);
        }
        try {
            output.close();
        } catch (final Throwable throwable) {
            System.err.println(throwable.getLocalizedMessage());
            throwable.printStackTrace(System.err);
        }
     */
    }
}
