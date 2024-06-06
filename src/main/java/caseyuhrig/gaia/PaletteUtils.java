package caseyuhrig.gaia;

import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PaletteUtils {

    public List<RGBA8> createPalette() throws IOException {
        final List<DoublePoint> colors = new ArrayList<>();

        final BufferedImage image = ImageIO.read(new File("C:\\Users\\casey\\Documents\\source\\gaia\\contrib\\Gaia_s_sky_in_colour_g.jpg"));
        final int width = image.getWidth();
        final int height = image.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                final int pixel = image.getRGB(x, y);
                final Color color = new Color(pixel, true);
                final double[] rgb = new double[]{color.getRed(), color.getGreen(), color.getBlue()};
                final var point = new DoublePoint(rgb);
                //if (!colors.contains(point)) {
                colors.add(point);
                //}
            }
        }

        System.out.println("Colors: " + colors.size());

        System.out.println("Clustering...");

        final int k = 256; // We want 256 colors
        final KMeansPlusPlusClusterer<DoublePoint> clusterer = new KMeansPlusPlusClusterer<>(k);
        final List<CentroidCluster<DoublePoint>> clusteredPoints = clusterer.cluster(colors);

        System.out.println("Creating palette...");

        final List<RGBA8> palette = new ArrayList<>();
        for (final CentroidCluster<DoublePoint> cluster : clusteredPoints) {
            final double[] centroid = cluster.getCenter().getPoint();
            final int red = (int) centroid[0];
            final int green = (int) centroid[1];
            final int blue = (int) centroid[2];
            palette.add(new RGBA8(red, green, blue));
        }

        System.out.println("Sorting palette...");

        palette.sort(new Comparator<RGBA8>() {
            @Override
            public int compare(final RGBA8 c1, final RGBA8 c2) {
                final double lum1 = 0.299 * c1.red + 0.587 * c1.green + 0.114 * c1.blue;
                final double lum2 = 0.299 * c2.red + 0.587 * c2.green + 0.114 * c2.blue;
                return Double.compare(lum1, lum2);
            }
        });

        return palette;
    }

    public static List<RGBA8> sortByHue(final List<RGBA8> palette) {
        // copy palette array into new array, clone the array
        final List<RGBA8> sortedPalette = new ArrayList<>(palette);
        sortedPalette.addAll(palette);
        sortedPalette.sort(new Comparator<RGBA8>() {
            @Override
            public int compare(final RGBA8 c1, final RGBA8 c2) {
                final float[] hsb1 = Color.RGBtoHSB(c1.red, c1.green, c1.blue, null);
                final float[] hsb2 = Color.RGBtoHSB(c2.red, c2.green, c2.blue, null);
                return Float.compare(hsb1[0], hsb2[0]);
            }
        });
        return sortedPalette;
    }

    public static List<double[]> sortByBrightness(final List<double[]> palette) {
        // copy palette array into new array, clone the array
        final List<double[]> sortedPalette = new ArrayList<>(palette);
        sortedPalette.addAll(palette);
        sortedPalette.sort(new Comparator<double[]>() {
            @Override
            public int compare(final double[] d1, final double[] d2) {
                final var c1 = new RGBA8(d1);
                final var c2 = new RGBA8(d2);
                final float[] hsb1 = Color.RGBtoHSB(c1.red, c1.green, c1.blue, null);
                final float[] hsb2 = Color.RGBtoHSB(c2.red, c2.green, c2.blue, null);
                return Float.compare(hsb1[2], hsb2[2]);
            }
        });
        return sortedPalette;
    }

    public static List<RGBA8> sortByCustom1(final List<RGBA8> palette) {
        // copy palette array into new array, clone the array
        final List<RGBA8> sortedPalette = new ArrayList<>(palette);
        sortedPalette.addAll(palette);
        sortedPalette.sort(new Comparator<RGBA8>() {
            @Override
            public int compare(final RGBA8 c1, final RGBA8 c2) {
                final float[] hsb1 = Color.RGBtoHSB(c1.red, c1.green, c1.blue, null);
                final float[] hsb2 = Color.RGBtoHSB(c2.red, c2.green, c2.blue, null);
                // Define blue and yellow ranges
                final boolean isBlue1 = hsb1[0] >= 0.5 && hsb1[0] <= 0.7;
                final boolean isBlue2 = hsb2[0] >= 0.5 && hsb2[0] <= 0.7;
                final boolean isYellow1 = hsb1[0] >= 0.15 && hsb1[0] <= 0.2;
                final boolean isYellow2 = hsb2[0] >= 0.15 && hsb2[0] <= 0.2;

                if (isBlue1 && isBlue2) {
                    // Both are blue; sort by brightness, darkest first
                    return Float.compare(hsb2[2], hsb1[2]);
                } else if (isYellow1 && isYellow2) {
                    // Both are yellow; sort by brightness, darkest first
                    return Float.compare(hsb2[2], hsb1[2]);
                } else if (isBlue1 && isYellow2) {
                    return -1; // Always sort blue before yellow
                } else if (isYellow1 && isBlue2) {
                    return 1; // Always sort yellow after blue
                } else {
                    // Fallback to sorting by hue
                    return Float.compare(hsb1[0], hsb2[0]);
                }
            }
        });
        return sortedPalette;
    }

    public static void main(final String[] args) throws Exception {
        System.out.println("Creating palette...");
        final PaletteUtils paletteUtils = new PaletteUtils();
        final List<RGBA8> palette = paletteUtils.createPalette();
        int n = 0;
        for (final RGBA8 color : palette) {
            //System.out.println(n + ": " + color);
            System.out.println("palette.add(new RGBA8(" + color.red + ", " + color.green + ", " + color.blue + "));");
            n++;
        }
        System.out.println("Done.");
    }
}
