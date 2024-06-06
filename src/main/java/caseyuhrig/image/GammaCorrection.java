package caseyuhrig.image;

public class GammaCorrection {

    public static double standardGammaCorrect(final double value) {
        return value <= 0.0031308 ? 12.92 * value : 1.055 * Math.pow(value, 1.0 / 2.4) - 0.055;
    }

    public static double[] statndardGammaCorrect(final double[] rgb) {
        return new double[]{standardGammaCorrect(rgb[0]), standardGammaCorrect(rgb[1]), standardGammaCorrect(rgb[2])};
    }

    public static double srgbGammaCorrect(final double value) {
        return value <= 0.0031308 ? 12.92 * value : 1.055 * Math.pow(value, 1.0 / 2.4) - 0.055;
    }

    public static double[] srgbGammaCorrect(final double[] rgb) {
        return new double[]{srgbGammaCorrect(rgb[0]), srgbGammaCorrect(rgb[1]), srgbGammaCorrect(rgb[2])};
    }

    public static double inverseGammaCorrect(final double value) {
        return value <= 0.04045 ? value / 12.92 : Math.pow((value + 0.055) / 1.055, 2.4);
    }

    public static double[] inverseGammaCorrect(final double[] rgb) {
        return new double[]{inverseGammaCorrect(rgb[0]), inverseGammaCorrect(rgb[1]), inverseGammaCorrect(rgb[2])};
    }

    public static double gammaCompression(final double value, final double gamma) {
        return Math.pow(value, 1.0 / gamma);
    }

    public static double[] gammaCompression(final double[] rgb, final double gamma) {
        return new double[]{gammaCompression(rgb[0], gamma), gammaCompression(rgb[1], gamma), gammaCompression(rgb[2], gamma)};
    }

    public static double gammaExpansion(final double value, final double gamma) {
        return Math.pow(value, gamma);
    }

    public static double[] gammaExpansion(final double[] rgb, final double gamma) {
        return new double[]{gammaExpansion(rgb[0], gamma), gammaExpansion(rgb[1], gamma), gammaExpansion(rgb[2], gamma)};
    }
}
