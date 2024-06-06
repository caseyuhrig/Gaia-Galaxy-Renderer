package caseyuhrig.image;

public class WavelengthToRGB {

    /**
     * Converts a wavelength in the visible spectrum to an approximate RGB color.
     *
     * @param wavelength The wavelength in nanometers (380-750 nm).
     * @return An array of RGB values [r, g, b] where each component is in the range 0.0 to 1.0.
     */
    public static double[] wavelengthToRgb(final double wavelength) {
        double r = 0, g = 0, b = 0;

        if (wavelength >= 380 && wavelength <= 440) {
            r = -(wavelength - 440) / (440 - 380);
            g = 0;
            b = 1;
        } else if (wavelength >= 440 && wavelength <= 490) {
            r = 0;
            g = (wavelength - 440) / (490 - 440);
            b = 1;
        } else if (wavelength >= 490 && wavelength <= 510) {
            r = 0;
            g = 1;
            b = -(wavelength - 510) / (510 - 490);
        } else if (wavelength >= 510 && wavelength <= 580) {
            r = (wavelength - 510) / (580 - 510);
            g = 1;
            b = 0;
        } else if (wavelength >= 580 && wavelength <= 645) {
            r = 1;
            g = -(wavelength - 645) / (645 - 580);
            b = 0;
        } else if (wavelength >= 645 && wavelength <= 750) {
            r = 1;
            g = 0;
            b = 0;
        }

        // Adjust intensity for wavelength outside visible range
        double factor = 1.0;
        if (wavelength > 700) {
            factor = 0.3 + 0.7 * (750 - wavelength) / (750 - 700);
        } else if (wavelength < 420) {
            factor = 0.3 + 0.7 * (wavelength - 380) / (420 - 380);
        }

        r = Math.pow(r * factor, 0.8);
        g = Math.pow(g * factor, 0.8);
        b = Math.pow(b * factor, 0.8);

        return new double[]{r, g, b};
    }
}
