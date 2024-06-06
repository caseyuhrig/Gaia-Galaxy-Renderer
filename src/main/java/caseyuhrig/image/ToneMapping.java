package caseyuhrig.image;

public class ToneMapping {
    public static double[] applyToneMapping(final double[] rgb, final double exposure) {
        final double[] mapped = new double[3];

        for (int i = 0; i < 3; i++) {
            final double scaled = rgb[i] * exposure;
            mapped[i] = scaled / (scaled + 1.0);
        }

        return mapped;
    }
}
