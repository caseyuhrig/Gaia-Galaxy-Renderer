package caseyuhrig.image;

public class FluxToColor {

    public static double[] fluxToRgb(final double[] normalizedFluxValues) {
        final double wavelengthStart = 330.0;
        final double wavelengthEnd = 1050.0;
        final double wavelengthRange = wavelengthEnd - wavelengthStart;

        final double[] averageRgb = {0, 0, 0};
        int validCount = 0;

        for (final double normalizedFlux : normalizedFluxValues) {
            final double wavelength = wavelengthStart + normalizedFlux * wavelengthRange;

            // Convert wavelength to RGB, ignoring wavelengths outside 380-750 nm
            if (wavelength >= 380 && wavelength <= 750) {
                final double[] rgb = ColorUtils.wavelengthToRgb(wavelength);
                for (int i = 0; i < 3; i++) {
                    averageRgb[i] += rgb[i];
                }
                validCount++;
            }
        }

        // Average the RGB values
        if (validCount > 0) {
            for (int i = 0; i < 3; i++) {
                averageRgb[i] /= validCount;
            }
        }

        return averageRgb;
    }


    public static double[] fluxToRgb(final double[] normalizedFluxValues, final double wavelengthStart, final double wavelengthEnd) {
        final double wavelengthRange = wavelengthEnd - wavelengthStart;

        final double[] averageRgb = {0, 0, 0};
        int validCount = 0;

        for (final double normalizedFlux : normalizedFluxValues) {
            final double wavelength = wavelengthStart + normalizedFlux * wavelengthRange;

            // Convert wavelength to RGB, ignoring wavelengths outside 380-750 nm
            if (wavelength >= 380 && wavelength <= 750) {
                final double[] rgb = ColorUtils.wavelengthToRgb(wavelength);
                for (int i = 0; i < 3; i++) {
                    averageRgb[i] += rgb[i];
                }
                validCount++;
            }
        }

        // Average the RGB values
        if (validCount > 0) {
            for (int i = 0; i < 3; i++) {
                averageRgb[i] /= validCount;
            }
        }

        return averageRgb;
    }
}
