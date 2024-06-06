package caseyuhrig.gaia.renderer;

import caseyuhrig.gaia.*;
import caseyuhrig.image.BloomEffect;
import caseyuhrig.image.ImageSharpening;
import caseyuhrig.util.ObjectUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class RenderRunner implements Runnable {

    private static final Logger LOG = LogManager.getLogger(RenderRunner.class);

    //public final int MAX_SAMPLES = 1873; // 1873 bigScale = 2, 575 bigScale = 4
    //public final long MAX_READINGS = 1811709771L; // 1.8 billion readings

    private Thread thread;
    private boolean done = false;

    public static BufferedImage image;
    private int imageWidth;
    private int imageHeight;
    private final int scale;
    private final ArrayList<RenderListener> listeners = new ArrayList<>();


    public RenderRunner(final int width, final int height, final int scale) {
        imageWidth = width;
        imageHeight = height;
        this.scale = scale;
        resize(imageWidth, imageHeight);
    }


    public void addRenderingListener(final RenderListener listener) {
        listeners.add(listener);
    }


    public void fireRenderingProgressListeners(final double progress, final Rectangle bounds) {
        for (final var listener : listeners) {
            listener.onRenderProgress(progress, bounds);
        }
    }


    public BufferedImage getImage() {
        return image;
    }


    public void resize(final int width, final int height) {
        imageWidth = width;
        imageHeight = height;
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D imageG = image.createGraphics();
        imageG.setColor(Color.BLACK);
        imageG.fillRect(0, 0, width, height);
        imageG.dispose();
    }


    @Override
    public void run() {
        long n = 0L;

        final var renderer = ObjectUtils.create(CleanPixelRenderer.class, imageWidth, imageHeight, scale);

        //final var renderer = new CleanPixelRenderer(imageWidth, imageHeight, scale);
        //final var renderer = new UberPixelRenderer(imageWidth, imageHeight);
        //final var renderer = new AnotherPixelRenderer(imageWidth, imageHeight);

        //final var renderer = new NewPixelRenderer(imageWidth, imageHeight, MAX_SAMPLES);
        //final var renderer = new DensityRenderer(imageWidth, imageHeight);
        //final var renderer = new TemperaturePixelRenderer(imageWidth, imageHeight, MAX_SAMPLES);
        //final var renderer = new TemperaturePixelRendererInverse(imageWidth, imageHeight, MAX_SAMPLES);
        //final var renderer = new MagPixelRenderer(imageWidth, imageHeight);
        //final var renderer = new AwesomePixelRenderer(imageWidth, imageHeight);
        //final var renderer = new CIELabPixelRenderer(imageWidth, imageHeight);
        //final var renderer = new Lama3PixelRenderer(imageWidth, imageHeight, MAX_SAMPLES);
        //final var renderer = new BrighterRenderer(getWidth(), getHeight(), MAX_SAMPLES);
        //final var renderer = new SimpleRenderer(imageWidth, imageHeight, MAX_SAMPLES);

        final var bounds = new BoundsTracker();

        final var screenCoordinates = new LB_ScreenCoordinates(imageWidth, imageHeight);
        try (final var source = new FileGaiaSourceData()) {
            for (final var data : source) {
                if (done) break;

                try {
                    final Point point = screenCoordinates.getScreenCoordinates(data);
                    final int x = point.x;
                    final int y = point.y;
                    final RGBA8 color = renderer.renderPixel(x, y, data);

                    if (color != null) {
                        GraphicUtils.setPixelFast(image, x, y, color.argb());
                        bounds.updateBounds(x, y);
                    }
                } catch (final Exception e) {
                    LOG.error(e.getLocalizedMessage(), e);
                }
                if (n % 200000 == 0) {
                    fireRenderingProgressListeners(source.getProgress(), bounds.getBounds());
                    bounds.resetBounds();
                    final int percent = (int) (source.getProgress() * 100);
                    if (percent % 10 == 0) {
                        if (!GaiaConst.messages.contains("Progress: " + percent + "%")) {
                            LOG.info("Progress: {}%", percent);
                            GaiaConst.messages.add("Progress: " + percent + "%");
                        }
                    }
                }
                n++;
            }
        } catch (final Exception e) {
            throw new RuntimeException("Failed Iterating: " + e.getMessage(), e);
        }

        renderer.printStatistics();

        fireRenderingProgressListeners(1, new BoundsTracker(0, 0, imageWidth, imageHeight).getBounds());

        done = true;
        LOG.info("Done rendering. {}", n);
        GaiaConst.messages.add("Done rendering. " + n);
        final String namePrefix = "gaia-" + renderer.getClass().getSimpleName() + "-" + imageWidth + "x" + imageHeight + "-" + scale;
        GraphicUtils.saveImage(image, "C:/tmp", namePrefix, ".png");
        LOG.info("Image saved to file.");
        GaiaConst.messages.add("Image saved to file.");
        LOG.info("Creating Bloom Image...");
        GaiaConst.messages.add("Creating Bloom Image...");
        GraphicUtils.saveImage(ImageSharpening.sharpen(image), "C:/tmp", namePrefix + "-sharp", ".png");
        GraphicUtils.saveImage(BloomEffect.applyBloom(image, 1.2), "C:/tmp", namePrefix + "-bloom-1_2", ".png");
        GraphicUtils.saveImage(BloomEffect.applyBloom(image, 1.5), "C:/tmp", namePrefix + "-bloom-1_5", ".png");
        GraphicUtils.saveImage(ImageSharpening.sharpen(BloomEffect.applyBloom(image, 1.5)), "C:/tmp", namePrefix + "-bloom-1_5-sharp", ".png");
        LOG.info("Bloom Image saved to file.");
        GaiaConst.messages.add("Bloom Image saved to file.");
    }

    public void start() {
        thread = Thread.ofPlatform().name("Render Thread").start(this);
    }

    public void stop() {
        done = true;
        try {
            LOG.debug("Waiting for thread to finish...");
            if (thread != null) thread.join();
            LOG.debug("Thread finished.");
        } catch (final InterruptedException e) {
            LOG.error(e.getLocalizedMessage(), e);
        }
        done = false;
    }
}
