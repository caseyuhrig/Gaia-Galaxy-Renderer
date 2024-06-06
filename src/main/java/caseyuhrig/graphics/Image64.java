package caseyuhrig.graphics;

import caseyuhrig.gaia.GaiaConst;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Image64 {

    private final double[][][] image;


    public Image64(final int width, final int height) {
        image = new double[width][height][3];
        clear();
    }


    public Image64(final double[][][] image) {
        this.image = image;
    }


    public void set(final double[][][] image) {
        for (int x = 0; x < getWidth(); x++) {
            if (getHeight() >= 0) System.arraycopy(image[x], 0, this.image[x], 0, getHeight());
        }
    }


    public void setRGB(final int x, final int y, final double r, final double g, final double b) {
        image[x][y][0] = r;
        image[x][y][1] = g;
        image[x][y][2] = b;
    }


    public void setRGB(final int x, final int y, final double[] rgb) {
        setRGB(x, y, rgb[0], rgb[1], rgb[2]);
    }


    public double[] getRGB(final int x, final int y) {
        return image[x][y];
    }


    public int getWidth() {
        return image.length;
    }


    public int getHeight() {
        return image[0].length;
    }

    /**
     * This technique involves scaling each pixel value based on its own properties, which can include normalizing
     * pixel values to a common range or standardizing them to have zero mean and unit variance.
     *
     * @return The normalized image (this image).
     */
    public Image64 localNormalization() {
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                image[x][y] = GaiaConst.normalize(image[x][y]);
            }
        }
        return this;
    }


    public Image64 globalNormalization() {
        double max = 0.0;
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                for (int i = 0; i < 3; i++) {
                    if (image[x][y][i] > max) {
                        max = image[x][y][i];
                    }
                }
            }
        }
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                for (int i = 0; i < 3; i++) {
                    image[x][y][i] /= max;
                }
            }
        }
        return this;
    }


    public void clear() {
        for (int x = 0; x < image.length; x++) {
            for (int y = 0; y < image[0].length; y++) {
                image[x][y] = new double[]{0, 0, 0};
            }
        }
    }


    public BufferedImage toImage() {
        final var bufferedImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        final Graphics2D graphics = bufferedImage.createGraphics();
        graphics.setColor(Color.BLACK);
        graphics.fillRect(0, 0, getWidth(), getHeight());
        graphics.dispose();
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                final double[] rgb = image[x][y];
                final int r = (int) (rgb[0] * 255);
                final int g = (int) (rgb[1] * 255);
                final int b = (int) (rgb[2] * 255);
                final int color = (r << 16) | (g << 8) | b;
                bufferedImage.setRGB(x, y, color);
            }
        }
        return bufferedImage;
    }
}
