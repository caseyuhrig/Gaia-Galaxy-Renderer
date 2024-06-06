package caseyuhrig.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ScrollPanePanner {


    private Point mousePoint;

    public void enablePanning(final JScrollPane scrollPane) {
        //System.out.println("enablePanning Thread Name: " + Thread.currentThread().getName());
        final JViewport viewport = scrollPane.getViewport();
        final JComponent content = (JComponent) viewport.getView();
        content.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(final MouseEvent e) {
                //System.out.println("mousePressed: " + content.getClass().getName());
                mousePoint = e.getPoint();
                // set the cursor to grab hand or panning cursor
                //content.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                //e.getComponent().getParent().setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                SwingUtilities.getRootPane(content).setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            }

            @Override
            public void mouseReleased(final MouseEvent e) {
                //mousePoint = null;
                // set the cursor back to default
                //content.setCursor(Cursor.getDefaultCursor());
                SwingUtilities.getRootPane(content).setCursor(Cursor.getDefaultCursor());
            }
        });
        content.addMouseMotionListener(new MouseAdapter() {
            private static final int THRESHOLD = 5; // Adjust this value as needed
            private final Point lastUpdatePoint = null;

            @Override
            public void mouseDragged(final MouseEvent e) {
                final Point currentPoint = e.getPoint();
                //if (lastUpdatePoint == null) {
                //    lastUpdatePoint = currentPoint;
                //    return;
                //}

                final int deltaX = mousePoint.x - currentPoint.x;
                final int deltaY = mousePoint.y - currentPoint.y;

                //System.out.println("---------------------");
                //System.out.println("Current point: " + currentPoint + " Mouse point: " + mousePoint + " DeltaX: " + deltaX + " DeltaY: " + deltaY);
                //System.out.println("Thread Name: " + Thread.currentThread().getName());
                //Thread.dumpStack();

                // Check if the mouse movement exceeds the threshold
                //if (Math.abs(deltaX) < THRESHOLD && Math.abs(deltaY) < THRESHOLD) {
                //    return;
                //}

                final Point viewPosition = viewport.getViewPosition();
                int newX = viewPosition.x + deltaX;
                int newY = viewPosition.y + deltaY;

                // Check if the new view position is within valid bounds
                final Dimension viewSize = viewport.getViewSize();
                final Dimension extentSize = viewport.getExtentSize();
                final int maxX = viewSize.width - extentSize.width;
                final int maxY = viewSize.height - extentSize.height;
                newX = Math.max(0, Math.min(newX, maxX));
                newY = Math.max(0, Math.min(newY, maxY));

                viewport.setViewPosition(new Point(newX, newY));
                //mousePoint = currentPoint;
                //lastUpdatePoint = currentPoint;
            }
        });
    }
}
