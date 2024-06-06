package caseyuhrig.gaia.renderer;

import caseyuhrig.gaia.RGBA8;
import caseyuhrig.gaia.RenderingData;

public class TemperaturePixelRendererInverse extends PixelRenderer {

    private final RGBA8[][] samples;
    private final int[][] counts;

    // calculated from the data, saved post run.
    //private final int MAX_SAMPLES = 1873;

    public TemperaturePixelRendererInverse(final int width, final int height, final int scale) {
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
        if (data.teff_gspphot == null) {

        } else {
            final float temperature = data.teff_gspphot;
            final var temperatureColor = RGBA8.ofTemperature(temperature).invert();
            //samples[x][y].add(temperatureColor);
            samples[x][y].red += temperatureColor.red;
            samples[x][y].green += temperatureColor.green;
            samples[x][y].blue += temperatureColor.blue;
        }
        //System.out.println("1----------> " + x + "x" + y + " Count: " + counts[x][y] + " Alpha: " + samples[x][y].alpha);

        counts[x][y]++;
        samples[x][y].alpha++;

        if (samples[x][y].alpha != counts[x][y]) {
            System.out.println("2----------> " + x + "x" + y + "  Count: " + counts[x][y] + " Alpha: " + samples[x][y].alpha);
            throw new IllegalStateException("Alpha and count mismatch");
        }
        //final int count = samples[x][y].alpha; //counts[x][y] + 1;
        final int count = getMaxSamples() / 4; //counts[x][y];
        //final int color = (int) Math.round((double) samples[x][y] / (double) MAX_SAMPLES * 255.0);
        //final int red = (int) Math.round((double) samples[x][y].red / (double) count);
        //final int green = (int) Math.round((double) samples[x][y].green / (double) count);
        //final int blue = (int) Math.round((double) samples[x][y].blue / (double) count);
        final int red = samples[x][y].red / count;
        final int green = samples[x][y].green / count;
        final int blue = samples[x][y].blue / count;
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
        System.out.println("Temperature Renderer Inverse");
        System.out.println("Max Samples: " + getMaxSamples());
        System.out.println("Maximum Count: " + getMaximumCount());
    }
}
