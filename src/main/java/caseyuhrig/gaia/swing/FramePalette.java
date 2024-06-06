package caseyuhrig.gaia.swing;

import caseyuhrig.gaia.GradientPalette;

import javax.swing.*;
import java.awt.*;

public class FramePalette extends JFrame {

    private JPanel contentPanel;

    private PalettePanel palettePanel;

    public FramePalette() {
        super("Palette");
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setSize(1600, 1200);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setVisible(true);
        setContentPane(getContentPanel());
    }

    public static void main(final String[] args) {
        new FramePalette();
    }


    public JPanel getContentPanel() {
        if (contentPanel == null) {
            contentPanel = new JPanel();
            contentPanel.setLayout(new GridBagLayout());
            //contentPanel.add(new PalettePanel(Colors.palette1), new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            //contentPanel.add(getPalettePanel(), new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            contentPanel.add(new GradientPanel(), new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            contentPanel.add(new PalettePanel(GradientPalette.densityPalette(99 + 1)), new GridBagConstraints(0, 3, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            //contentPanel.add(new PalettePanel(PaletteUtils.sortByBrightness(Colors.palette)), new GridBagConstraints(0, 4, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        }
        return contentPanel;
    }

    //public PalettePanel getPalettePanel() {
    //     return new PalettePanel(Colors.palette);
    //}
}
