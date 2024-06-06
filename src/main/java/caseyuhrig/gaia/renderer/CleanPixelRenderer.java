package caseyuhrig.gaia.renderer;

import caseyuhrig.gaia.GaiaConst;
import caseyuhrig.gaia.GradientPalette;
import caseyuhrig.gaia.RGBA8;
import caseyuhrig.gaia.RenderingData;
import caseyuhrig.image.ExponentialGrowth;
import caseyuhrig.image.GammaCorrection;
import caseyuhrig.image.PlanckianLocus;

import static caseyuhrig.gaia.GaiaConst.divide;
import static caseyuhrig.gaia.GaiaConst.multiply;


public class CleanPixelRenderer extends PixelRenderer {

    private final int[][] counts;
    private final double[][][] colors;
    //private final double[][][] buffer;

    //private final DataOutputStream output;


    public CleanPixelRenderer(final int width, final int height, final int scale) {
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
        // zero out the buffer
        /*
        buffer = new double[width][height][3];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                buffer[x][y] = new double[]{0, 0, 0};
            }
        }
        */
        maxSamples = switch (scale) {
            case 1 -> 1807;
            case 2 -> 516;
            case 3 -> 248;
            case 4 -> 150;
            case 5 -> 99;
            default -> throw new IllegalArgumentException("Invalid scale: " + scale);
        };
        //maxSamples = 1807; // scale 1
        //maxSamples = 516; // scale 2
        //maxSamples = 248; // scale 3
        //maxSamples = 150; // scale 4
        /*
        try {
            output = new DataOutputStream(new FileOutputStream("d:/gaia.dat"));
        } catch (final Throwable throwable) {
            throw new RuntimeException(throwable.getLocalizedMessage(), throwable);
        }
        */
    }


    final double av = 0.5; // Example extinction value
    final double exposure = 1.0; // Example exposure value
    final double saturationFactor = 2.5; // Example saturation factor (50%)
    //final double sunAngle = 45.0; // Example angle between Sun and star in degrees
    final double[] sunColor = {1.0, 0.95, 0.9}; // Example Sun color (normalized RGB)


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
        /*
        try {
            TODO forgot a couple fields... create a standalone class to write the data.

            output.writeDouble(data.l);
            output.writeDouble(data.b);
            output.writeDouble(data.phot_rp_mean_flux);
            output.writeDouble(data.phot_g_mean_flux);
            output.writeDouble(data.phot_bp_mean_flux);

            output.writeFloat(data.phot_rp_mean_mag);
            output.writeFloat(data.phot_g_mean_mag);
            output.writeFloat(data.phot_bp_mean_mag);

            output.writeDouble(data.bp_rp);
            output.writeDouble(data.g_rp);
            output.writeDouble(data.bp_g);

            output.writeDouble(data.teff_gspphot);
            output.writeDouble(data.parallax);

        } catch (final Throwable throwable) {
            throw new RuntimeException(throwable.getLocalizedMessage(), throwable);
        }
        */


        counts[x][y]++;

        if (counts[x][y] > maxSamples) {
            System.out.println("Max samples: " + counts[x][y]);
            maxSamples = counts[x][y];
        }

        //final double maxSamples = this.maxSamples;


        final int density = counts[x][y] / maxSamples;

        /*
        final double[] rgb = GalaxyRenderer.calculateObservedColor(data);

        //rgb[0] *= 2.0;
        //rgb[1] *= 2.0;
        //rgb[2] *= 2.0;

        //rgb[0] = Math.log10(rgb[0] + 1.0);
        //rgb[1] = Math.log10(rgb[1] + 1.0);
        //rgb[2] = Math.log10(rgb[2] + 1.0);

        rgb[0] = Math.log1p(rgb[0] + 1.0);
        rgb[1] = Math.log1p(rgb[1] + 1.0);
        rgb[2] = Math.log1p(rgb[2] + 1.0);

        final double scale1 = 1.5;

        rgb[0] *= scale1;
        rgb[1] *= scale1;
        rgb[2] *= scale1;

        final double scale2 = 6.0;

        final double[] negativeBlue = new double[]{0.5, 0.7, 1.0};
        final double[] yellow = new double[]{1.0, 1.0, 0.0};

        final double[] grad = Gradient.interpolateRgb(negativeBlue, yellow, density);


        rgb[0] = rgb[0] * (grad[0] * density) * scale2;
        rgb[1] = rgb[1] * (grad[1] * density) * scale2;
        rgb[2] = rgb[2] * (grad[2] * density) * scale2;

        final double[] out1 = normalize(rgb);

        final double[] out2 = new double[3];

        out2[0] = max(out1[0], rgb[0]);
        out2[1] = max(out1[1], rgb[1]);
        out2[2] = max(out1[2], rgb[2]);
*/
        final double[] temperatureColor = PlanckianLocus.colorTemperatureToRGB(data.teff_gspphot);

        //System.out.println(data.teff_gspphot);

        //System.out.println("Density: " + density + " RGB: " + rgb[0] + " " + rgb[1] + " " + rgb[2]);

        final double growth = ExponentialGrowth.exponentialGrowth(0, maxSamples, density);

        for (int i = 0; i < 3; i++) {
            colors[x][y][i] += temperatureColor[i]; // * 6.0 * grad2.rgb()[i] * growth * 2.0; //out2[i]; //rgb[i];
        }
        //colors[x][y] = rgb;

        final double[] gradient = GradientPalette.densityPalette(maxSamples).get(counts[x][y]);
        final double[] gradColor = multiply(gradient, growth);
        final double[] tempColor = divide(colors[x][y], maxSamples);
        final double factor = switch (scale) {
            case 1 -> 8.0;
            case 2 -> 15.0;
            case 3 -> 22.0;
            case 4 -> 30.0;
            case 5 -> 30.0;
            default -> throw new IllegalArgumentException("Invalid scale: " + scale);
        };
        // scale 1 = 8.0/4.0, scale 2 = 15.0, scale 4 = 30.0
        final double[] c9 = multiply(multiply(gradColor, tempColor), factor);

        //final var c = c9;
        //buffer[x][y] = c9;

        //final double[] c = GammaCorrection.srgbGammaCorrect(divide(colors[x][y], maxSamples));

        final double[] c = GammaCorrection.srgbGammaCorrect(c9);
        //final double[] c = GammaCorrection.srgbGammaCorrect(gradient);
        //final double[] c = temperatureColor;

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
        GaiaConst.messages.add("Post post-processing image...");

        //final BufferedImage image = new Image64(buffer).globalNormalization().toImage();
        //final String namePrefix = "gaia-" + this.getClass().getSimpleName() + "-gnorm";
        //GraphicUtils.saveImage(image, "C:/tmp", namePrefix, ".png");
        //System.out.println("Post Image saved to file.");

        //System.out.println(getClass().getSimpleName() + " Statistics");
        //System.out.println("-------------------------------------------------------");
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
