package caseyuhrig.gaia.renderer;

import caseyuhrig.gaia.GaiaRenderingSource;
import caseyuhrig.gaia.GradientPalette;
import caseyuhrig.gaia.RGBA8;
import caseyuhrig.gaia.RenderingData;

public class DensityRenderer extends PixelRenderer {

    private final int[][] samples;


    public DensityRenderer(final int width, final int height, final int scale) {
        super(width, height, scale);
        samples = new int[width][height];
        // zero out the colors
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                samples[x][y] = 0;
            }
        }
        maxSamples = 1635;
    }

    private boolean hasEverything(final GaiaRenderingSource data) {
        return data.phot_bp_mean_mag != null && data.phot_rp_mean_mag != null && data.phot_g_mean_mag != null &&
                data.bp_g != null && data.g_rp != null && data.bp_rp != null && data.parallax != null;
    }

    @Override
    public RGBA8 renderPixel(final int x, final int y, final RenderingData data) {

        //if (hasEverything(data)) {
        //    return null;
        //}

        samples[x][y]++;
        //final double maxSamples = getMaxSamples();
        //final double sampleCount = 14602; // 10000
        //final int color = (int) Math.round((double) samples[x][y] / (double) MAX_SAMPLES);
        //final int color = (int) Math.round((double) samples[x][y] / sampleCount * 255.0);
        //final int color = (int) Math.round((double) samples[x][y] / 500.0 * 255.0);
        //final var rgba = new RGBA8(color, color, color, 255);
        //rgba.misc1 = samples[x][y];

        final int count = samples[x][y];
        final int maxCount = checkMaxSamples(maxSamples);

        final var rgba = GradientPalette.densityPalette(maxCount).get(count);
        return new RGBA8(rgba);
    }


    @Override
    public void printStatistics() {
        System.out.println("Density Renderer " + width + "x" + height);
        System.out.println("Max Samples: " + maxSamples);
        System.out.println("Maximum Sample Count: " + getMaximumSampleCount());
    }


    public int getMaximumSampleCount() {
        int max = 0;
        for (int x = 0; x < samples.length; x++) {
            for (int y = 0; y < samples[x].length; y++) {
                if (samples[x][y] > max) {
                    max = samples[x][y];
                }
            }
        }
        return max;
    }


}
