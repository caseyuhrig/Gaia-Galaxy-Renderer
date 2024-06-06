package caseyuhrig.gaia;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;

public class GradientPalette {

    private static final Logger LOG = LogManager.getLogger(GradientPalette.class);

    public static ArrayList<GradientEntry> gradient = new ArrayList<>();

    static {
        //gradient.add(new GradientEntry(new RGBA8(0, 6, 11), 0.0));
        gradient.add(new GradientEntry(new double[]{0.5, 0.7, 1.0}, 0.0));
        gradient.add(new GradientEntry(new RGBA8(114, 116, 129).rgb(), 0.4)); //  0.3  0.6
        gradient.add(new GradientEntry(new RGBA8(209, 148, 126).rgb(), 0.65)); // 0.65 0.85
        //gradient.add(new GradientEntry(new RGBA8(255, 187, 4), 0.65));
        gradient.add(new GradientEntry(new RGBA8(235, 191, 192).rgb(), 0.85)); // pink'ish
        //gradient.add(new GradientEntry(new RGBA8(235, 30, 30), 0.85)); // pink'ish
        gradient.add(new GradientEntry(new RGBA8(255, 255, 255).rgb(), 1.0));
    }


    private static final HashMap<Integer, ArrayList<double[]>> palettes = new HashMap<>();


    private GradientPalette() {
        /*
        gradient.add(new GradientEntry(new RGBA8(0, 6, 11), 0.0));
        gradient.add(new GradientEntry(new RGBA8(114, 116, 129), 0.25)); //  0.3  0.6

        gradient.add(new GradientEntry(new RGBA8(209, 148, 126), 0.5)); // 0.65 0.85
        gradient.add(new GradientEntry(new RGBA8(108, 11, 169), 0.6)); // #6C0BA9 purple
        gradient.add(new GradientEntry(new RGBA8(235, 191, 192), 0.7)); // pink'ish
        gradient.add(new GradientEntry(new RGBA8(235, 191, 192).brighten(0.8f), 0.95));
        gradient.add(new GradientEntry(new RGBA8(255, 255, 255), 1.0));
        */
    }


    public static ArrayList<double[]> createPalette(final ArrayList<GradientEntry> gradient, final int numColors) {
        final var palette = new ArrayList<double[]>();
        final int numStops = gradient.size();

        for (int i = 0; i < numColors; i++) {
            final double position = (double) i / (numColors - 1);
            int index = 0;

            while (index < numStops - 1 && position > gradient.get(index + 1).position) {
                index++;
            }

            final GradientEntry leftStop = gradient.get(index);
            final GradientEntry rightStop = gradient.get(index + 1);

            final double t = (position - leftStop.position) / (rightStop.position - leftStop.position);

            final double r = (rightStop.color[0] * t + leftStop.color[0] * (1 - t));
            final double g = (rightStop.color[1] * t + leftStop.color[1] * (1 - t));
            final double b = (rightStop.color[2] * t + leftStop.color[2] * (1 - t));

            palette.add(new double[]{r, g, b});
        }
        return palette;
    }


    public static ArrayList<double[]> densityPalette(final int samples) {
        final var palette = palettes.get(samples);
        if (palette == null) {
            LOG.info("Creating new palette for {} samples", samples);
            //palette = createPalette(gradient, 10808); //DensityRenderer.MAX_SAMPLES);
            final var newPalette = createPalette(gradient, samples + 1);
            palettes.put(samples, newPalette);
            return newPalette;
        }
        return palette;
    }


    public record GradientEntry(double[] color, double position) {
    }
}
