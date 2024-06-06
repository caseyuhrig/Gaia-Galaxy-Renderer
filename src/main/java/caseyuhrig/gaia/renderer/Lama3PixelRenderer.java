package caseyuhrig.gaia.renderer;

import caseyuhrig.gaia.RGBA8;
import caseyuhrig.gaia.RenderingData;

public class Lama3PixelRenderer extends PixelRenderer {

    private final RGBA8[][] samples;
    private final int[][] counts;


    public Lama3PixelRenderer(final int width, final int height, final int scale) {
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

        if (data.phot_g_mean_mag == null || data.phot_bp_mean_mag == null || data.phot_rp_mean_mag == null) {
            return new RGBA8(0, 0, 0, 255);
        }
        final double bp = data.phot_bp_mean_mag;
        final double rp = data.phot_rp_mean_mag;
        final double g = data.phot_g_mean_mag;

        // Calculate color index (Bp-Rp)
        final double colorIndex = bp - rp;

        // Estimate color based on color index (simplified approach)
        double red = 0;
        double green = 0;
        double blue = 0;
        if (colorIndex < 0.5) { // Blue-white stars
            blue = 1;
            green = 1 - colorIndex;
            red = 0;
        } else if (colorIndex < 1.5) { // White-yellow stars
            green = 1;
            red = colorIndex - 0.5;
            blue = 0;
        } else { // Red stars
            red = 1;
            green = 1 - (colorIndex - 1.5);
            blue = 0;
        }

        // Scale RGB values based on brightness (g_mean_mag)
        final double brightness = Math.pow(2.5, -g) * 1000000; // Convert magnitude to brightness
        //System.out.println("Brightness: " + brightness);
        red *= brightness;
        green *= brightness;
        blue *= brightness;

        // Normalize and scale to 0-255 range
        final int r = (int) (red * 255);
        final int g2 = (int) (green * 255);
        final int b = (int) (blue * 255);

        final var color = new RGBA8(r, g2, b, 255);

        //return new int[]{r, g, b};

        try {
            samples[x][y].red += color.red;
            samples[x][y].green += color.green;
            samples[x][y].blue += color.blue;
        } catch (final Exception e) {
            final var message = "Index: " + x + "x" + y + " out of bounds: " + samples.length + "x" + samples[0].length + " " + e.getLocalizedMessage();
            System.err.println(message);
            throw new IndexOutOfBoundsException(message);
        }

        //System.out.println("1----------> " + x + "x" + y + " Count: " + counts[x][y] + " Alpha: " + samples[x][y].alpha);

        counts[x][y]++;
        //samples[x][y].alpha++;

        //if (samples[x][y].alpha != counts[x][y]) {
        //    System.out.println("2----------> " + x + "x" + y + "  Count: " + counts[x][y] + " Alpha: " + samples[x][y].alpha);
        //    throw new IllegalStateException("Alpha and count mismatch");
        //}


        //final double count = getMaxSamples(); // /4
        final double count = counts[x][y];
        //final double count = 500;
        final int red2 = (int) Math.round((double) samples[x][y].red / count);
        final int green2 = (int) Math.round((double) samples[x][y].green / count);
        final int blue2 = (int) Math.round((double) samples[x][y].blue / count);
        final var result = new RGBA8(red2, green2, blue2, 255);

        //System.out.println("Red: " + red2 + " Green: " + green2 + " Blue: " + blue2);

        return result;
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
        System.out.println("Lama3 Pixel Renderer");
        System.out.println("Max Samples: " + getMaxSamples());
        System.out.println("Maximum Count: " + getMaximumCount());
    }

}
