package caseyuhrig.swing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class MinimizeMaximizeJFrameHandler {

    private static final Logger LOG = LogManager.getLogger(MinimizeMaximizeJFrameHandler.class);

    private boolean wasMaximized = false;


    public static void injectMinimizeMaximizeHandler(final JFrame frame, final JScrollPane scrollPane) {
        new MinimizeMaximizeJFrameHandler().handleMinimizeMaximize(frame, scrollPane);
    }

    public void handleMinimizeMaximize(final JFrame frame, final JScrollPane scrollPane) {
        //final JScrollPane scrollPane = (JScrollPane) frame.getRootPane().getComponent(0);
        //final JScrollPane scrollPane = (JScrollPane) frame.getComponent(0);
        //final JScrollPane scrollPane = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, frame);
        //if (scrollPane == null) {
        //    LOG.error("No JScrollPane found in JFrame.");
        //    throw new UncheckedException("No JScrollPane found in JFrame.");
        //}
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(final ComponentEvent e) {
                final Insets insets = frame.getInsets();
                if ((frame.getExtendedState() & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH) {
                    //System.out.println("JFrame is maximized");
                    final GraphicsDevice device = frame.getGraphicsConfiguration().getDevice();
                    final Rectangle usableBounds = device.getDefaultConfiguration().getBounds();
                    final Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(device.getDefaultConfiguration());
                    usableBounds.x += screenInsets.left;
                    usableBounds.y += screenInsets.top;
                    usableBounds.width -= (screenInsets.left + screenInsets.right);
                    usableBounds.height -= (screenInsets.top + screenInsets.bottom);
                    final int usableWidth = usableBounds.width;
                    final int usableHeight = usableBounds.height;
                    final int offset = insets.top - insets.bottom;
                    //System.out.println("offset1: " + offset);
                    scrollPane.setSize(usableWidth, usableHeight - offset); //24);
                    frame.revalidate();
                    frame.repaint();
                    if (LOG.isDebugEnabled()) {
                        final Insets insets2 = frame.getInsets();
                        LOG.debug("frame insets2: {}", insets2);
                    }
                    if (!wasMaximized) {
                        wasMaximized = true;
                    }
                } else {
                    //System.out.println("JFrame is not maximized");
                    final int widthOffset = insets.left + insets.right; // 14?
                    final int heightOffset = insets.top + insets.bottom;
                    //System.out.println("offset2: " + widthOffset);
                    scrollPane.setSize(frame.getWidth() - widthOffset, frame.getHeight() - heightOffset);
                    if (wasMaximized) {
                        wasMaximized = false;
                        frame.revalidate();
                        frame.repaint();
                    }
                }
            }
        });
    }
}
