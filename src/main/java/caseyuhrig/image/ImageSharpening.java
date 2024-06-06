package caseyuhrig.image;

import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

public class ImageSharpening {

    public static BufferedImage sharpen(final BufferedImage image) {
        // Define a sharpening kernel
        final float[] sharpenKernel = {
                0.0f, -1.0f, 0.0f,
                -1.0f, 5.0f, -1.0f,
                0.0f, -1.0f, 0.0f
        };

        // Create a Kernel object
        final Kernel kernel = new Kernel(3, 3, sharpenKernel);

        // Create a ConvolveOp object
        final ConvolveOp convolveOp = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);

        // Create an empty image with the same dimensions and type as the original
        final BufferedImage sharpenedImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());

        // Apply the convolution operation to the image
        convolveOp.filter(image, sharpenedImage);

        return sharpenedImage;
    }

}
