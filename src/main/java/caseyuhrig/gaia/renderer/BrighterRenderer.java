package caseyuhrig.gaia.renderer;

import caseyuhrig.gaia.RGBA8;
import caseyuhrig.gaia.RenderingData;


public class BrighterRenderer extends MagPixelRenderer {

    private final RGBA8[][] pixels;


    public BrighterRenderer(final int width, final int height, final int scale) {
        super(width, height, scale);
        pixels = new RGBA8[width][height];
        // zero out the colors
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                pixels[x][y] = new RGBA8(0, 0, 0, 0);
            }
        }
    }

    @Override
    public RGBA8 renderPixel(final int x, final int y, final RenderingData data) {

        final var color = super.renderPixel(x, y, data);

        pixels[x][y].red += color.red;
        pixels[x][y].green += color.green;
        pixels[x][y].blue += color.blue;

        final int count = 500;
        final RGBA8 out = new RGBA8(pixels[x][y].red / count, pixels[x][y].green / count, pixels[x][y].blue / count, 255);

        return out;
    }


    @Override
    public void printStatistics() {
        System.out.println("Brighter Renderer");
        System.out.println("Max Samples: " + getMaxSamples());
    }
}
