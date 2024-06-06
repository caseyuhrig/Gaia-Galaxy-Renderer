package caseyuhrig.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;

import static java.awt.event.MouseWheelEvent.WHEEL_UNIT_SCROLL;


public class UxImage extends JPanel {

    public BufferedImage image = null;


    // TODO, Make this an ImagePanel class
    public UxImage(final BufferedImage image) {
        super();
        final var thisPanel = this;

        this.image = image;
        setDoubleBuffered(true);
        resizeFrame(image.getWidth(), image.getHeight());
        // 1, 1920x1080
        // 2, 3840x2160 <-- default
        // 3, 5760x3240
        // 4, 7680x4320
        //resize(getWidth(), getHeight());
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(final ComponentEvent event) {
                // we don't want to alter the image size if the frame is resized.
                //resizeFrame(getWidth(), getHeight());
            }
        });
        addMouseWheelListener(event -> {
            final double rotation = event.getPreciseWheelRotation();
            final int amount = event.getScrollAmount() * 20;
            //final int units = event.getScrollType();
            //final int button = event.getButton();
            final int modifiers = event.getModifiersEx();

            if (modifiers == WHEEL_UNIT_SCROLL) {
                if (rotation > 0 || rotation < 0) {
                    final var parent = SwingUtilities.getAncestorOfClass(JScrollPane.class, thisPanel);
                    if (parent instanceof final JScrollPane scrollPane) {
                        final var current = scrollPane.getVerticalScrollBar().getValue();
                        scrollPane.getVerticalScrollBar().setValue(current + (int) rotation * amount);
                    } else {
                        System.out.println("No parent scroll pane found.");
                    }
                } else {
                    System.out.println("No mouse wheel rotation detected.");
                }
            } else if (modifiers == 64) { // horizontal scroll wheel (undocumented?)
                if (rotation > 0 || rotation < 0) {
                    final var parent = SwingUtilities.getAncestorOfClass(JScrollPane.class, thisPanel);
                    if (parent instanceof final JScrollPane scrollPane) {
                        final var current = scrollPane.getHorizontalScrollBar().getValue();
                        scrollPane.getHorizontalScrollBar().setValue(current + (int) -rotation * amount);
                    } else {
                        System.out.println("No parent scroll pane found.");
                    }
                } else {
                    System.out.println("No mouse wheel rotation detected.");
                }
            }
        });

    }


    public void setImage(final BufferedImage image) {
        this.image = image;
        resizeFrame(image.getWidth(), image.getHeight());
    }


    private void resizeFrame(final int width, final int height) {
        final var size = new Dimension(width, height);
        setSize(size);
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        //renderer.stop();
        //System.out.println("Resizing panel to " + getWidth() + "x" + getHeight() + " pixels.");
        //renderer.resize(width, height);
        //renderer.start();
    }

    @Override
    public void paintComponent(final Graphics g) {
        if (image != null) {
            final Rectangle clip = g.getClipBounds();
            final int x = clip.x;
            final int y = clip.y;
            final int w = clip.width;
            final int h = clip.height;
            g.drawImage(image, x, y, x + w, y + h, x, y, x + w, y + h, this);
        }
    }
}
