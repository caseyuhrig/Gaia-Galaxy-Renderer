package caseyuhrig.gaia.swing;

import caseyuhrig.gaia.GradientPalette;
import caseyuhrig.gaia.RGBA8;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class GradientPanel extends JPanel {

    public ArrayList<double[]> palette;

    public GradientPanel() {
        palette = GradientPalette.densityPalette(1000);
    }

    @Override
    public void paintComponent(final Graphics g) {
        super.paintComponent(g);
        final int width = getWidth();
        final int height = getHeight();
        final double colorWidth = (double) width / (double) palette.size();
        for (double i = 0; i < palette.size(); i++) {
            final double[] color = palette.get((int) i);
            g.setColor(new RGBA8(color).toColor());
            g.fillRect((int) (i * colorWidth), 0, Math.max((int) colorWidth, 1), height);
        }
    }


}
