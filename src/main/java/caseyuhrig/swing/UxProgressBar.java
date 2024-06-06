package caseyuhrig.swing;

import caseyuhrig.gaia.GraphicUtils;

import javax.swing.*;
import java.awt.*;

public class UxProgressBar extends JProgressBar {

    public UxProgressBar() {
        // Set some default properties for the progress bar
        //setPreferredSize(new Dimension(300, 30));
        setForeground(Color.YELLOW); // Color of the progress bar fill
        setBackground(Color.GRAY);   // Background color of the progress bar
        setBorder(BorderFactory.createEmptyBorder()); // Remove the default border
        setFocusable(false); // Disable focus
    }


    public UxProgressBar(final int min, final int max) {
        this();
        setMinimum(min);
        setMaximum(max);
    }


    @Override
    protected void paintComponent(final Graphics g) {
        // Use Graphics2D for better control over rendering
        final Graphics2D g2d = GraphicUtils.applyHighQualityRenderingHints(g);

        // Enable anti-aliasing for smoother graphics
        //g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        final int borderRadius = 6; //Math.min(4, getHeight() / 2);

        // fill background with black
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Get the width and height of the progress bar
        final int width = getWidth();
        final int height = getHeight();

        // Draw the background with rounded corners
        g2d.setColor(getBackground());
        g2d.fillRoundRect(1, 1, width - 2, height - 2, borderRadius, borderRadius);

        // Calculate the progress width based on the current value
        final int progressWidth = (int) ((width - 2) * ((double) getValue() / getMaximum()));

        // Draw the progress fill with rounded corners
        g2d.setColor(Color.GREEN);
        g2d.fillRoundRect(1, 1, progressWidth, height - 2, borderRadius, borderRadius);

        // Draw the border with rounded corners
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(1, 1, width - 2, height - 2, borderRadius, borderRadius);

        // Draw the text in the center of the progress bar
        final String text = getValue() + "%";
        final FontMetrics fontMetrics = g2d.getFontMetrics();
        final int stringWidth = fontMetrics.stringWidth(text);
        final int stringHeight = fontMetrics.getAscent();
        final int x = (width - stringWidth) / 2;
        final int y = (height + stringHeight) / 2 - fontMetrics.getDescent();
        g2d.setColor(Color.BLACK);
        g2d.drawString(text, x, y);

        // Dispose the graphics object to release resources
        //g2d.dispose();
    }

    @Override
    protected void paintBorder(final Graphics g) {
        // Do not paint the default border
    }
}
