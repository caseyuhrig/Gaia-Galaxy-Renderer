package caseyuhrig.gaia.renderer;

import caseyuhrig.gaia.RGBA8;
import caseyuhrig.gaia.RenderingData;

import java.util.HashMap;

public abstract class PixelRenderer {

    protected static final HashMap<String, Integer> MAX_SAMPLES = new HashMap<>();

    static {
        //MAX_SAMPLES.put("MAX_SAMPLES_DENSITYRENDERER_3840x2160", 3911);
        //MAX_SAMPLES.put("MAX_SAMPLES_DENSITYRENDERER_3840x2160", 4863);
        //MAX_SAMPLES.put("MAX_SAMPLES_DENSITYRENDERER_3840x2160", 8161); // only showing bad data (needs to be confirmed)
        MAX_SAMPLES.put("MAX_SAMPLES_DENSITYRENDERER_15360x8640", 1602); // all data (needs to be confirmed)
        MAX_SAMPLES.put("MAX_SAMPLES_DENSITYRENDERER_7680x4320", 4405); // all data (needs to be confirmed)
    }

    protected final int width;
    protected final int height;
    protected final int scale;
    protected int maxSamples = 0;


    public PixelRenderer(final int width, final int height, final int scale) {
        this.width = width;
        this.height = height;
        this.scale = scale;
        if (MAX_SAMPLES.containsKey(getSamplesKey())) {
            this.maxSamples = MAX_SAMPLES.get(getSamplesKey());
        }
    }

    public int getMaxSamples() {
        return maxSamples;
    }

    public int checkMaxSamples(final int samples) {
        if (samples > maxSamples) {
            maxSamples = samples;
            //System.out.println(getSamplesKey() + " = " + maxSamples + ";");
            MAX_SAMPLES.put(getSamplesKey(), maxSamples);
            System.out.println("MAX_SAMPLES.put(\"" + getSamplesKey() + "\", " + maxSamples + ");");
        }
        return maxSamples;
    }

    public String getSamplesKey() {
        return "MAX_SAMPLES_" + getClass().getSimpleName().toUpperCase() + "_" + width + "x" + height;
    }

    public abstract RGBA8 renderPixel(int x, int y, RenderingData data);

    public abstract void printStatistics();
}
