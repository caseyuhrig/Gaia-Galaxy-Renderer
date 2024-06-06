package caseyuhrig.image;

public class PlanckianLocus {
    public static double[] colorTemperatureToRGB(final double temperature) {
        final double t = temperature / 100.0;
        double r, g, b;

        if (t <= 66) {
            r = 255;
        } else {
            r = t - 60;
            r = 329.698727446 * Math.pow(r, -0.1332047592);
            r = clamp(r, 0, 255);
        }

        if (t <= 66) {
            g = t;
            g = 99.4708025861 * Math.log(g) - 161.1195681661;
            g = clamp(g, 0, 255);
        } else {
            g = t - 60;
            g = 288.1221695283 * Math.pow(g, -0.0755148492);
            g = clamp(g, 0, 255);
        }

        if (t >= 66) {
            b = 255;
        } else {
            if (t <= 19) {
                b = 0;
            } else {
                b = t - 10;
                b = 138.5177312231 * Math.log(b) - 305.0447927307;
                b = clamp(b, 0, 255);
            }
        }

        return new double[]{r / 255.0, g / 255.0, b / 255.0};
    }

    private static double clamp(final double value, final double min, final double max) {
        return Math.max(min, Math.min(max, value));
    }
}
