package caseyuhrig.image;

import java.awt.*;
import java.awt.image.BufferedImage;

public class BloomEffect {
    public static BufferedImage applyBloom(final BufferedImage image, final double bloomFactor) {
        final int width = image.getWidth();
        final int height = image.getHeight();
        final BufferedImage bloomImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                final Color color = new Color(image.getRGB(x, y));
                final int r = (int) (color.getRed() * bloomFactor);
                final int g = (int) (color.getGreen() * bloomFactor);
                final int b = (int) (color.getBlue() * bloomFactor);
                final int a = color.getAlpha();

                bloomImage.setRGB(x, y, new Color(clamp(r, 0, 255), clamp(g, 0, 255), clamp(b, 0, 255), a).getRGB());
            }
        }

        return bloomImage;
    }

    private static int clamp(final int value, final int min, final int max) {
        return Math.min(max, Math.max(min, value));
    }
}
