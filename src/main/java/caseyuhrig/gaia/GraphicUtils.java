package caseyuhrig.gaia;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.nio.file.Path;

public class GraphicUtils {

    public static void setPixelFast(final BufferedImage image, final int x, final int y, final int argb) {
        final int[] imageData = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        final int width = image.getWidth();
        imageData[y * width + x] = argb;
    }

    public static void saveImage(final BufferedImage image, final String path) {
        try {
            javax.imageio.ImageIO.write(image, "png", new java.io.File(path));
        } catch (final java.io.IOException e) {
            throw new RuntimeException("Failed saving image: " + e.getMessage(), e);
        }
    }

    public static File saveImage(final BufferedImage image, final String directory, final String prefix, final String ext) {
        final var folder = new File(directory);
        //Path path = Path.of(folder.getAbsolutePath(), prefix + "-" + System.currentTimeMillis() + ext);
        if (!folder.exists()) {
            throw new RuntimeException("Folder does not exist: " + folder.getAbsolutePath());
        }
        if (!folder.isDirectory()) {
            throw new RuntimeException("Not a folder: " + folder.getAbsolutePath());
        }
        final var filename = prefix + "-" + System.currentTimeMillis() + ext;
        final Path path = Path.of(folder.getAbsolutePath(), filename);
        Path newPath = path;
        while (new File(newPath.toString()).exists()) {
            System.out.println("File exists: " + path);
            newPath = Path.of(folder.getAbsolutePath(), prefix + "-" + System.currentTimeMillis() + ext);
        }
        final var outputFile = newPath.toFile();
        //if (!outputFile.canWrite()) {
        //    throw new RuntimeException("Cannot write to file: " + outputFile.getAbsolutePath());
        //}
        System.out.println("Saving image to file: " + outputFile.getAbsolutePath());
        try {
            if (!ImageIO.write(image, "png", outputFile)) {
                System.err.println("Failed to save image to file.");
                throw new RuntimeException("Failed to save image to file: " + outputFile.getAbsolutePath());
            }
        } catch (final Exception e) {
            throw new RuntimeException("Failed to save image to file: " + e.getMessage(), e);
        }
        System.out.println("Image saved to file: " + outputFile.getAbsolutePath());
        return outputFile;
    }

    public static Graphics2D applyHighQualityRenderingHints(final Graphics g) {
        return applyHighQualityRenderingHints((Graphics2D) g);
    }

    public static Graphics2D applyHighQualityRenderingHints(final Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        return g2d;
    }
}
