package caseyuhrig.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;

public class UxUtils {

    public static void centerFrame(final Window frame) {
        // Get the screen dimensions considering the taskbar
        final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final GraphicsConfiguration gc = ge.getDefaultScreenDevice().getDefaultConfiguration();
        final Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(gc);
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        final int taskbarHeight = screenInsets.bottom;
        final int usableHeight = screenSize.height - taskbarHeight;

        // Calculate the new location of the frame
        final int x = (screenSize.width - frame.getWidth()) / 2;
        final int y = (usableHeight - frame.getHeight()) / 2;

        frame.setLocation(x, y);
    }


    public static void setSizes(final JComponent component, final Dimension size) {
        component.setPreferredSize(size);
        component.setMinimumSize(size);
        component.setMaximumSize(size);
        component.setSize(size);
    }


    public static void setSizes(final JComponent component, final int width, final int height) {
        final var size = new Dimension(width, height);
        setSizes(component, size);
    }


    public static void injectPanning(final JScrollPane scrollPane) {
        new ScrollPanePanner().enablePanning(scrollPane);
    }


    public static JComponent injectMouseInactivityHandler(final JComponent component) {
        final int inactivityTime = 5000;
        // Step 1: Create a transparent cursor
        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        final Dimension dim = toolkit.getBestCursorSize(1, 1);
        final Image image = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_ARGB);
        final Cursor transparentCursor = toolkit.createCustomCursor(image, new Point(0, 0), "transparentCursor");

        // Step 2: Implement a Timer
        final Timer timer = new Timer(inactivityTime, e -> component.setCursor(transparentCursor));
        timer.setRepeats(false);

        // Step 3: Add Mouse Motion Listener
        component.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(final MouseEvent e) {
                // Reset the timer and restore the cursor
                timer.restart();
                component.setCursor(Cursor.getDefaultCursor());
            }
        });
        return component;
    }


    public static void passMouseEvents(final JComponent source, final JComponent target) {
        source.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(final MouseEvent e) {
                target.dispatchEvent(SwingUtilities.convertMouseEvent(e.getComponent(), e, target));
            }

            @Override
            public void mouseDragged(final MouseEvent e) {
                target.dispatchEvent(SwingUtilities.convertMouseEvent(e.getComponent(), e, target));
            }

            @Override
            public void mouseEntered(final MouseEvent e) {
                target.dispatchEvent(SwingUtilities.convertMouseEvent(e.getComponent(), e, target));
            }

            @Override
            public void mouseExited(final MouseEvent e) {
                target.dispatchEvent(SwingUtilities.convertMouseEvent(e.getComponent(), e, target));
            }
        });
        source.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                target.dispatchEvent(SwingUtilities.convertMouseEvent(e.getComponent(), e, target));
            }

            @Override
            public void mousePressed(final MouseEvent e) {
                target.dispatchEvent(SwingUtilities.convertMouseEvent(e.getComponent(), e, target));
            }

            @Override
            public void mouseReleased(final MouseEvent e) {
                target.dispatchEvent(SwingUtilities.convertMouseEvent(e.getComponent(), e, target));
            }
        });
        source.addMouseWheelListener(event -> {
            target.dispatchEvent(SwingUtilities.convertMouseEvent(event.getComponent(), event, target));
        });
    }
}
