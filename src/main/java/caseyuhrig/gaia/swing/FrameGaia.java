package caseyuhrig.gaia.swing;

import caseyuhrig.LoggingUtils;
import caseyuhrig.gaia.LB_ScreenCoordinates;
import caseyuhrig.gaia.RenderListener;
import caseyuhrig.gaia.ScreenCoordinates;
import caseyuhrig.gaia.renderer.*;
import caseyuhrig.swing.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

/**
 * TODO
 * (1) Make the image pannable and zoomable
 * (2) Add coordinate overlay
 * <p>
 * https://www.eso.org/public/images/eso0932a/
 * https://astronomy.stackexchange.com/questions/46664/straightforward-conversion-from-gaia-gdr3-photometry-to-rgb-values
 * https://github.com/Starlink/starjava
 * https://github.com/color4j/color4j/blob/master/colorimetry/src/main/java/org/color4j/colorimetry/encodings/CIELab.java
 * List of color temperatures and RGB HEX values
 * http://www.vendian.org/mncharity/dir3/blackbody/UnstableURLs/bbr_color.html
 * https://archive.stsci.edu/hlsp/atlas-refcat2
 */
public class FrameGaia extends JFrame {

    static {
        LoggingUtils.configureLogging();
    }

    private static final Logger LOG = LogManager.getLogger(FrameGaia.class);


    public static void main(final String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                new FrameGaia();
            } catch (final Throwable throwable) {
                LOG.error(throwable.getLocalizedMessage(), throwable);
            }
        });
    }


    private JLayeredPane layers;
    private JScrollPane scrollPane;
    private UxImage imagePanel;
    private UxImagePreview previewPanel;
    private JPopupMenu popupMenu;
    private OverlayPanel overlayPanel;
    private JProgressBar progressBar;
    private JComboBox<Class<? extends PixelRenderer>> rendererComboBox;

    private RenderRunner renderer = null;
    private final ScreenCoordinates screenCoordinates;
    private Point2D.Double coordinates = new Point2D.Double(0, 0);

    // TODO - Set the size based on the resolution of the monitor.  See below.
    private final int scale = 2;
    private final int largeWidth = 3840 * scale;
    private final int largeHeight = 2160 * scale;


    public FrameGaia() {
        super();
        setTitle("Milky Way Galaxy");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // TODO - Set the size based on the resolution of the monitor
        setSize(1920, 1080);
        setBackground(Color.BLACK);
        add(getLayers());
        UxUtils.centerFrame(this);
        setVisible(true);

        final var that = this;

        screenCoordinates = new LB_ScreenCoordinates(largeWidth, largeHeight);

        MinimizeMaximizeJFrameHandler.injectMinimizeMaximizeHandler(this, getScrollPane());

        getRenderRunner().start();
    }


    public RenderRunner getRenderRunner() {
        if (renderer == null) {
            renderer = new RenderRunner(largeWidth, largeHeight, scale);
            renderer.addRenderingListener(new RenderListener() {
                @Override
                public void onRenderProgress(final double progress, final Rectangle bounds) {
                    SwingUtilities.invokeLater(() -> {
                        repaint();
                    });
                    previewPanel.refreshImage(renderer.getImage());
                }
            });
            renderer.addRenderingListener(
                    (progress, bounds) -> {
                        getOverlayPanel().setRenderProgress(progress);
                    }
            );
        }
        return renderer;
    }


    public UxImage getImagePanel() {
        if (imagePanel == null) {
            imagePanel = new UxImage(getRenderRunner().getImage());
            imagePanel.addPropertyChangeListener("coordinates", event -> {
                final var point = (Point2D.Double) event.getNewValue();
                coordinates = screenCoordinates.getDataCoordinates((int) point.getX(), (int) point.getY());
            });
            imagePanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(final MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        getPopupMenu().show(e.getComponent(), e.getX(), e.getY());
                    }
                }

                @Override
                public void mouseReleased(final MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        getPopupMenu().show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            });
            imagePanel.addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseMoved(final MouseEvent event) {
                    final Point2D.Double point = screenCoordinates.getDataCoordinates(event.getX(), event.getY());
                    //System.out.println("l,b " + point);
                    //final var event = new PropertyChangeEvent(this, "coordinates", null, point);
                    //fireCoordinatesChanged(event);
                    firePropertyChange("coordinates", null, point);
                }
            });
        }
        return imagePanel;
    }


    public OverlayPanel getOverlayPanel() {
        if (overlayPanel == null) {
            overlayPanel = new OverlayPanel();
        }
        return overlayPanel;
    }


    public JLayeredPane getLayers() {
        if (layers == null) {
            layers = new JLayeredPane();
            layers.add(getScrollPane(), JLayeredPane.DEFAULT_LAYER);
            layers.add(getOverlayPanel(), JLayeredPane.PALETTE_LAYER);
        }
        return layers;
    }


    public JScrollPane getScrollPane() {
        if (scrollPane == null) {
            scrollPane = new JScrollPane();
            scrollPane.setLocation(1, 1);
            scrollPane.setPreferredSize(new Dimension(1920, 1080));
            scrollPane.setSize(new Dimension(1900, 1000));
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            scrollPane.setViewportView(UxUtils.injectMouseInactivityHandler(getImagePanel()));
            //scrollPane.setBackground(Color.BLACK);
            scrollPane.setVisible(true);
            UxUtils.injectPanning(scrollPane);
            scrollPane.getViewport().addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(final ComponentEvent e) {
                    final JViewport viewport = scrollPane.getViewport();
                    getOverlayPanel().setBounds(viewport.getBounds());
                }
            });
        }
        return scrollPane;
    }


    public JPopupMenu getPopupMenu() {
        if (popupMenu == null) {
            popupMenu = new JPopupMenu();
            final var menuItem = new JMenuItem("Palette...");
            menuItem.addActionListener(e -> {
                SwingUtilities.invokeLater(FramePalette::new);
            });
            popupMenu.add(menuItem);
            final var menuItem2 = new JMenuItem("Exit");
            menuItem2.addActionListener(e -> {
                System.exit(0);
            });
            popupMenu.add(menuItem2);
        }
        return popupMenu;
    }


    public JComboBox<Class<? extends PixelRenderer>> getRendererComboBox() {
        if (rendererComboBox == null) {
            rendererComboBox = new JComboBox<>();
            rendererComboBox.addItem(CleanPixelRenderer.class);
            rendererComboBox.addItem(AnotherPixelRenderer.class);
            rendererComboBox.addItem(AwesomePixelRenderer.class);
            rendererComboBox.addActionListener(e -> {
                if (rendererComboBox.getSelectedItem() instanceof final PixelRenderer selected) {
                    System.out.println("Selected: " + selected.getClass().getName());
                    //getRenderRunner().setPixelRenderer(selected);
                }
            });
        }
        return rendererComboBox;
    }


    public class OverlayPanel extends JPanel {


        private final Insets I_2_2_2_2 = new Insets(2, 2, 2, 2);
        private final Insets I_2_10_2_10 = new Insets(2, 10, 2, 10);
        private final Insets I_0_10_2_10 = new Insets(0, 10, 2, 10);
        private final Insets I_10_10_0_10 = new Insets(10, 10, 2, 10);
        private final Insets I_2_10_2_2 = new Insets(2, 10, 2, 2);
        //private static final String fontName = "Times New Roman";
        private static final String fontName = "Noto Sans";
        private static final Font fontTimesBold48 = new Font(fontName, Font.BOLD, 48);
        private static final Font fontTimesBold24 = new Font(fontName, Font.BOLD, 24);
        private static final Font fontTimesBold12 = new Font(fontName, Font.BOLD, 12);


        // -- can't decide on a font! ;)
        private static final String[] fontNames = new String[]{
                "Orbitron Regular",
                "Cascadia Code",
                "Consolas",
                "Courier New",
                "Lucida Console",
                "Monospaced",
                "Source Code Pro",
                "JetBrains Mono",
                "Fira Code",
                "Terminus",
                "DejaVu Sans Mono",
                "Trebuchet MS",
                "Arial",
                "Verdana",
                "Tahoma"
        };


        public OverlayPanel() {
            setLayout(new GridBagLayout());
            setOpaque(false);
            setDoubleBuffered(true);

            // Insets: top, left, bottom, right

            final var label1 = new JLabel("Gaia DR3 Galaxy Renderer");
            label1.setFont(fontTimesBold24);
            label1.setForeground(new Color(255, 255, 255, 128));
            add(label1, gbc(0, 0, 0, 0, 3, I_10_10_0_10));
            //add(sp(), gbc(2, 0, 1, 0, 2, I_2_2_2_2));

            final var label = new JLabel("Milky Way");
            label.setFont(fontTimesBold48);
            label.setForeground(new Color(255, 255, 255, 128));
            add(label, gbc(0, 1, 0, 0, 3, new Insets(-20, 10, 2, 10))); //I_0_10_2_10));
            //add(label, gbc(0, 1, 0, 0, 2, I_0_10_2_10));
            //add(sp(), gbc(2, 1, 1, 0, 2, I_2_2_2_2));

            add(getPreviewPanel(), gbc(0, 2, 0, 0, 3, I_2_10_2_2));
            //add(sp(), gbc(2, 2, 1, 0, 2, I_2_2_2_2));

            add(getRendererComboBox(), gbc(0, 3, 0, 0, 1, I_2_10_2_2));
            add(getProgressBar(), gbc(1, 3, 0, 0, 1, I_2_2_2_2));
            final var renderButton = new JButton("Render");
            add(renderButton, gbc(2, 3, 0.0, 0, 1, I_2_2_2_2));
            add(sp(), gbc(3, 3, 1, 0, 1, I_2_2_2_2));

            add(sp(), gbc(0, 4, 0, 1, 1, I_2_2_2_2));
            //add(sp(), gbc(1, 4, 0, 0, 1, I_2_2_2_2));
            //add(sp(), gbc(2, 4, 1, 1, 1, I_2_2_2_2));
            //add(sp(), gbc(3, 4, 1, 1, 1, I_2_10_2_10));


            //UxUtils.injectMouseInactivityHandler(this);
        }


        public UxImagePreview getPreviewPanel() {
            if (previewPanel == null) {
                // TODO - Get these values from the size of the scroll pane viewport and large image size
                // width should be proportional to the width of the scroll pane viewport
                // height should be proportional to the height of the large image and width from above
                final int previewWidth = 640;
                final int previewHeight = 360;
                previewPanel = new UxImagePreview(getScrollPane());
                UxUtils.setSizes(previewPanel, previewWidth, previewHeight);
            }
            return previewPanel;
        }


        public JProgressBar getProgressBar() {
            if (progressBar == null) {
                progressBar = new UxProgressBar(0, 100);
                progressBar.setStringPainted(true);
                progressBar.setValue(0);
                progressBar.setString("0%");
            }
            return progressBar;
        }


        public void setRenderProgress(final double renderProgress) {
            final JProgressBar progressBar = getProgressBar();
            progressBar.setValue((int) (renderProgress * 100));
            progressBar.setString(String.format("%.2f%%", renderProgress * 100.0));
        }


        private GridBagConstraints gbc(final int gridx, final int gridy, final double weightx, final double weighty, final int gridwidth, final Insets insets) {
            return new GridBagConstraints(gridx, gridy, gridwidth, 1, weightx, weighty, GridBagConstraints.CENTER, GridBagConstraints.BOTH, insets, 0, 0);
        }


        private JPanel sp() {
            final var spacerPanel = new JPanel();
            spacerPanel.setOpaque(false);
            //spacerPanel.setBackground(Color.GREEN);
            return spacerPanel;
        }
    }
}
