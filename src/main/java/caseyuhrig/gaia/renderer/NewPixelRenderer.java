package caseyuhrig.gaia.renderer;

import caseyuhrig.gaia.GaiaConst;
import caseyuhrig.gaia.RGBA8;
import caseyuhrig.gaia.RenderingData;

public class NewPixelRenderer extends PixelRenderer {


    private final RGBA8[][] samples;
    private final int[][] counts;
    private int maxCount = 5027; // scale = 1


    public static double normalize(final double value, final double min, final double max) {
        return (value - min) / (max - min);
    }


    public NewPixelRenderer(final int width, final int height, final int scale) {
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

        if (data.phot_bp_mean_mag == null || data.phot_rp_mean_mag == null || data.phot_g_mean_mag == null) {
            return null;
        }
        if (data.bp_g == null || data.g_rp == null || data.bp_rp == null) {
            return null;
        }
        if (data.parallax == null) {
            return null;
        }

        //final double bp_g = normalize(data.bp_g, GaiaConst.min_bp_g, GaiaConst.max_bp_g); // blue-green
        //final double g_rp = normalize(data.g_rp, GaiaConst.min_g_rp, GaiaConst.max_g_rp); // green-red
        //final double bp_rp = normalize(data.bp_rp, GaiaConst.min_bp_rp, GaiaConst.max_bp_rp); // blue-red
        //final var color = new RGBA8(bp_g, g_rp, bp_rp, 1.0);


        final double r = normalize(data.phot_rp_mean_mag, GaiaConst.min_phot_rp_mean_mag, GaiaConst.max_phot_rp_mean_mag);
        final double g = normalize(data.phot_g_mean_mag, GaiaConst.min_phot_g_mean_mag, GaiaConst.max_phot_g_mean_mag);
        //final double g = normalize(data.parallax, GaiaConst.min_parallax, GaiaConst.max_parallax);
        final double b = normalize(data.phot_bp_mean_mag, GaiaConst.min_phot_bp_mean_mag, GaiaConst.max_phot_bp_mean_mag);
        final var color = new RGBA8(r, g, b, 1.0);

        //final var color = new RGBA8(data.bp_g, data.g_rp, data.bp_rp, 1.0).invert();

        //final var color = new RGBA8(data.phot_rp_mean_mag.intValue(), data.phot_g_mean_mag.intValue(), data.phot_bp_mean_mag.intValue(), 255);

        samples[x][y].red += color.red;
        samples[x][y].green += color.green;
        samples[x][y].blue += color.blue;

        counts[x][y]++;

        //final double count = getMaxSamples(); // /4
        //final int count = 6070;

        if (counts[x][y] > maxCount) {
            maxCount = counts[x][y];
            System.out.println("Max Count: " + maxCount);
        }

        final int red = (int) Math.round((double) samples[x][y].red / (double) maxCount);
        final int green = (int) Math.round((double) samples[x][y].green / (double) maxCount);
        final int blue = (int) Math.round((double) samples[x][y].blue / (double) maxCount);

        final var rgba = new RGBA8(red, green, blue, 255);

        return rgba; //.log10Scale(50);

        // if no data, use the pixel density only!
        // TODO see above

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
