package caseyuhrig.graphics;

import javax.swing.*;
import java.awt.*;

public class FrameColorFactory extends JFrame {

    private JPanel contentPanel;

    private JTabbedPane tabbedPane;

    private JPanel colorMixerPanel;


    public FrameColorFactory() {
        setTitle("Color Factory");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        setContentPane(getContentPanel());
    }


    public GridBagConstraints gbc(final int gridx, final int gridy) {
        return new GridBagConstraints(gridx, gridy, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0);
    }

    private JPanel getContentPanel() {
        if (contentPanel == null) {
            contentPanel = new JPanel();
            contentPanel.setLayout(new GridBagLayout());
            contentPanel.add(getTabbedPane(), gbc(0, 0));
        }
        return contentPanel;
    }


    public JTabbedPane getTabbedPane() {
        if (tabbedPane == null) {
            tabbedPane = new JTabbedPane();
            tabbedPane.addTab("Mixer", getPanelColorMixer());
        }
        return tabbedPane;
    }


    private JPanel getPanelColorMixer() {
        if (colorMixerPanel == null) {
            colorMixerPanel = new JPanel();
            colorMixerPanel.setLayout(new GridBagLayout());
        }
        return colorMixerPanel;
    }

    public static void main(final String[] args) {
        SwingUtilities.invokeLater(() -> {
            final FrameColorFactory frame = new FrameColorFactory();
            frame.setVisible(true);
        });
    }
}
