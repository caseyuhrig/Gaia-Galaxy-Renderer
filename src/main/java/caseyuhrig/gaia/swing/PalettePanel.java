package caseyuhrig.gaia.swing;

import caseyuhrig.gaia.RGBA8;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.util.List;

public class PalettePanel extends JPanel {

    private final List<double[]> palette;

    private final Color currentColor = Color.BLACK;


    public PalettePanel(final List<double[]> palette) {
        if (palette.size() == 0) {
            throw new IllegalArgumentException("Palette must contain at least one color");
        }
        this.palette = palette;
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(final MouseEvent e) {
                //currentColor = getPanelColorAt(e.getX(), e.getY());
                //System.out.println("Color at (" + e.getX() + "," + e.getY() + "): " + currentColor);
            }
        });
    }

    @Override
    public void paintComponent(final Graphics g) {
        super.paintComponent(g);
        final int width = getWidth();
        final int height = getHeight();
        //final int[] colors = new int[]{0x000000, 0x0000FF, 0x00FF00, 0x00FFFF, 0xFF0000, 0xFF00FF, 0xFFFF00, 0xFFFFFF};
        final int colorWidth = width / palette.size();
        for (int i = 0; i < palette.size(); i++) {
            final double[] color = palette.get(i);
            g.setColor(new RGBA8(color).toColor());
            g.fillRect(i * colorWidth, 0, colorWidth, height);
        }
    }

    private Color getPanelColorAt(final int x, final int y) {
        final BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g2d = image.createGraphics();
        paint(g2d);
        g2d.dispose();
        return new Color(image.getRGB(x, y));
    }
}
