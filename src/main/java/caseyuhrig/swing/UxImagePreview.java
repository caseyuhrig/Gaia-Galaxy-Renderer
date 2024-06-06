package caseyuhrig.swing;

import caseyuhrig.gaia.GraphicUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class UxImagePreview extends JPanel {

    private static final Logger LOG = LogManager.getLogger(UxImagePreview.class);

    private final static Color WHITE_50 = new Color(255, 255, 255, 128);

    private BufferedImage previewImage;
    private boolean showViewportRect = false;
    private final Rectangle viewportRect = new Rectangle(0, 0, 10, 10);
    private final JScrollPane scrollPane;
    private final Color viewrectColor = new Color(255, 255, 0, 128);


    public UxImagePreview(final JScrollPane scrollPane) {
        super();
        this.scrollPane = scrollPane;
        setDoubleBuffered(true);
        setOpaque(true);
        setBackground(Color.BLACK);
        setBorder(BorderFactory.createLineBorder(WHITE_50, 1));
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(final MouseEvent event) {
                final Rectangle viewRect = scrollPane.getViewport().getViewRect();
                final Dimension largeRect = scrollPane.getViewport().getViewSize();
                final double scaleX = (double) getWidth() / largeRect.getWidth();
                final double scaleY = (double) getHeight() / largeRect.getHeight();

                viewportRect.x = event.getX();
                viewportRect.y = event.getY();
                viewportRect.width = (int) (viewRect.width * scaleX);
                viewportRect.height = (int) (viewRect.height * scaleY);

                SwingUtilities.invokeLater(() -> repaint());
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent event) {
                final Dimension largeRect = scrollPane.getViewport().getViewSize();
                final double scaleX = (double) getWidth() / largeRect.getWidth();
                final double scaleY = (double) getHeight() / largeRect.getHeight();

                final int x = (int) (event.getX() / scaleX);
                final int y = (int) (event.getY() / scaleY);

                scrollPane.getViewport().setViewPosition(new Point(x, y));
            }

            @Override
            public void mouseEntered(final MouseEvent event) {
                showViewportRect = true;
                SwingUtilities.invokeLater(() -> repaint());
            }

            @Override
            public void mouseExited(final MouseEvent event) {
                showViewportRect = false;
                SwingUtilities.invokeLater(() -> repaint());
            }
        });
    }


    @Override
    public void paintComponent(final Graphics g) {
        super.paintComponent(g);
        final var graphics = GraphicUtils.applyHighQualityRenderingHints(g);
        graphics.drawImage(previewImage, 0, 0, this);

        if (showViewportRect) {
            graphics.setColor(WHITE_50);
            graphics.draw(viewportRect);
        }
        final Dimension largeRect = scrollPane.getViewport().getViewSize();
        final Rectangle viewRect = scrollPane.getViewport().getViewRect();
        final double scaleX = (double) getWidth() / largeRect.getWidth();
        final double scaleY = (double) getHeight() / largeRect.getHeight();

        final int x = (int) (viewRect.x * scaleX);
        final int y = (int) (viewRect.y * scaleY);
        final int width = (int) (viewRect.width * scaleX);
        final int height = (int) (viewRect.height * scaleY);

        graphics.setColor(viewrectColor);
        graphics.drawRect(x, y, width, height);
    }


    public void refreshImage(final BufferedImage largeImage) {
        scaleImageInBackground(largeImage, getWidth(), getHeight());
    }


    private void scaleImageInBackground(final BufferedImage largeImage, final int width, final int height) {
        final SwingWorker<BufferedImage, Void> worker = new SwingWorker<>() {
            @Override
            protected BufferedImage doInBackground() {
                // Perform the scaling in a background thread
                return scaleImage(largeImage, width, height);
            }

            @Override
            protected void done() {
                try {
                    previewImage = get();
                    SwingUtilities.invokeLater(() -> repaint());
                } catch (final Exception e) {
                    LOG.error("Failed to scale image. {}", e.getLocalizedMessage(), e);
                }
            }
        };
        worker.execute();
    }


    private BufferedImage scaleImage(final BufferedImage image, final int width, final int height) {
        final var scaledImage = new BufferedImage(width, height, image.getType());
        final Graphics2D graphics = scaledImage.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.drawImage(image, 0, 0, width, height, this);
        graphics.dispose();
        return scaledImage;
    }
}
