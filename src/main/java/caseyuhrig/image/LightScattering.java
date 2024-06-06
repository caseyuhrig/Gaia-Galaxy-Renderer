package caseyuhrig.image;

public class LightScattering {
    // Constants for Rayleigh scattering
    private static final double RAYLEIGH_SCALING_FACTOR = 1.0e-4; // Adjust as needed

    // Function for Rayleigh scattering
    public static double[] applyRayleighScattering(final double[] rgb, final double distance) {
        final double wavelengthR = 680.0; // Red wavelength in nm
        final double wavelengthG = 550.0; // Green wavelength in nm
        final double wavelengthB = 450.0; // Blue wavelength in nm

        final double r = rgb[0] * Math.exp(-RAYLEIGH_SCALING_FACTOR * distance / Math.pow(wavelengthR, 4));
        final double g = rgb[1] * Math.exp(-RAYLEIGH_SCALING_FACTOR * distance / Math.pow(wavelengthG, 4));
        final double b = rgb[2] * Math.exp(-RAYLEIGH_SCALING_FACTOR * distance / Math.pow(wavelengthB, 4));

        return new double[]{r, g, b};
    }

    // Constants for Mie scattering
    private static final double MIE_SCALING_FACTOR = 1.0e-3; // Adjust as needed

    // Function for Mie scattering
    public static double[] applyMieScattering(final double[] rgb, final double distance) {
        final double wavelengthR = 680.0; // Red wavelength in nm
        final double wavelengthG = 550.0; // Green wavelength in nm
        final double wavelengthB = 450.0; // Blue wavelength in nm

        final double r = rgb[0] * Math.exp(-MIE_SCALING_FACTOR * distance / wavelengthR);
        final double g = rgb[1] * Math.exp(-MIE_SCALING_FACTOR * distance / wavelengthG);
        final double b = rgb[2] * Math.exp(-MIE_SCALING_FACTOR * distance / wavelengthB);

        return new double[]{r, g, b};
    }
}
