package caseyuhrig.gaia;

public class GaiaMagnitudeConverter {

    private static final double PARALLAX_ERROR_THRESHOLD = 0.1; // Adjust for uncertainty

    public static RGBA8 calculateColor(final double gMag, final double bpMag, final double rpMag, final double parallax, final double parallaxError) {

        // 1. Background Subtraction (Optional, consider image statistics)
        // ... (Implement background subtraction if needed) ...

        // 2. Convert magnitudes to relative flux densities
        final double fG = Math.pow(10, -0.4 * gMag);
        final double fBP = Math.pow(10, -0.4 * bpMag);
        final double fRP = Math.pow(10, -0.4 * rpMag);

        // 3. Estimate stellar temperature (with parallax correction, handle errors)
        final double bpMinusRp = bpMag - rpMag;
        double temperature = 0.0;

        if (parallaxError > PARALLAX_ERROR_THRESHOLD) {
            // Handle unreliable parallax (average, ignore, etc.)
            final var gAbsMag = gMag - 5 * Math.log10(parallax) + 5; // bad parallax, need to get gAbsMag from somewhere or using the spectral type
            //final var gAbsMag = gMag - 5 * Math.log10(100.0 / parallax);
            temperature = estimateTemperatureWithoutParallax(bpMinusRp); // Optional fallback
        } else {
            temperature = 4600.0 / (0.92 * bpMinusRp + 1.7) + 3100
                    + 1000.0 * (1.0 / parallax - 1.0);
        }

        // 4. Color transformation based on temperature (Blackbody Model)
        //final double[] rgb = blackbodySpectrum(temperature);

        // 3. Color transformation based on temperature
        double r = 0.0;
        double g = 0.0;
        double b = 0.0;

        if (temperature <= 4000) {
            r = 1.0;
        } else if (temperature <= 7500) {
            r = (temperature - 4000.0) / 3500.0;
            g = 1.0;
        } else {
            b = (temperature - 7500.0) / 15000.0;
            // makes all the little stars show up
            //return new RGBA8(r, g, b, 1);
        }
        final double[] rgb = {r, g, b};

        // 5a. White point adjustment and gamma correction (optional)
        //rgb = adjustWhitepointAndGamma(rgb); // You might choose to disable this

        // 5b. Apply log scaling and adjust range for visual appeal
        final int R = (int) (Math.log10(1 + 50.0 * rgb[0]) * 255.0 / Math.log10(51.0));
        final int G = (int) (Math.log10(1 + 50.0 * rgb[1]) * 255.0 / Math.log10(51.0));
        final int B = (int) (Math.log10(1 + 50.0 * rgb[2]) * 255.0 / Math.log10(51.0));

        //System.out.println("R: " + R + " G: " + G + " B: " + B);

        return new RGBA8(R, G, B);
    }

    // Optional fallback function (replace with your implementation)
    private static double estimateTemperatureWithoutParallax(final double bpMinusRp) {
        // ... (Use a statistical model or average temperature) ...
        return 5000.0; // Example placeholder
    }

    private static double estimateTemperatureWithoutParallax(final double bpMinusRp, final double gAbsMag, final double l, final double b) {

        // Color-temperature component (adjust as needed)
        final double tempFromColor = 4500.0 + 1500.0 * bpMinusRp;

        // Absolute magnitude adjustment (more luminous = hotter)
        final double tempFromAbsMag = tempFromColor + 100.0 * (5.0 - gAbsMag);

        // Galactic location factor (example, adjust regions and factors)
        final double tempFromLocation;
        if (l < 90 && b > 30) {
            // Assume a region with hotter stars
            tempFromLocation = tempFromAbsMag + 200.0;
        } else {
            tempFromLocation = tempFromAbsMag;
        }

        // Averaging or weighted combination
        final double finalTemp = 0.5 * tempFromLocation + 0.5 * tempFromAbsMag; // Example weights

        return finalTemp;
    }


    public static double[] blackbodySpectrum(final double temperature) {
        final double PEAK_WAVELENGTH_CONSTANT = 2.89777e-3; // meters (Wien's Law constant)
        final double PLANCK_CONSTANT = 6.62607015e-34; // J s (Planck's constant)
        final double BOLTZMANN_CONSTANT = 1.38064852e-23; // J/K (Boltzmann's constant)
        final double SPEED_OF_LIGHT = 2.99792458e8; // m/s (speed of light)

        final double[] rgb = new double[3]; // Initialize for red, green, and blue

        // Loop through visible spectrum wavelengths (example: 400nm to 700nm)
        for (int i = 0; i < 3; i++) {
            final double wavelength = 400.0 + 100.0 * i; // Adjust for your desired range

            // Calculate peak wavelength based on temperature (Wien's Law)
            final double peakWavelength = PEAK_WAVELENGTH_CONSTANT / temperature;

            // Calculate radiance using Planck's Law
            final double radiance = (2.0 * PLANCK_CONSTANT * Math.pow(SPEED_OF_LIGHT, 3)) /
                    (Math.pow(wavelength, 5) * (Math.exp(PLANCK_CONSTANT * SPEED_OF_LIGHT / (wavelength * BOLTZMANN_CONSTANT * temperature)) - 1.0));

            // Simulate human eye sensitivity (approximate weighting function)
            double eyeSensitivity = 0.0;
            if (wavelength >= 380 && wavelength <= 430) {
                eyeSensitivity = (wavelength - 380.0) / (430.0 - 380.0);
            } else if (wavelength >= 430 && wavelength <= 680) {
                eyeSensitivity = 1.0;
            } else if (wavelength >= 680 && wavelength <= 780) {
                eyeSensitivity = (780.0 - wavelength) / (780.0 - 680.0);
            }

            // Apply weighting and normalize (adjust scaling factor as needed)
            rgb[i] = radiance * eyeSensitivity / (Math.max(radiance * eyeSensitivity, 1.0) * 100.0);
        }

        return rgb;
    }

    // Blackbody spectrum function (replace with your implementation or library)
    //private static double[] blackbodySpectrum(final double temperature) {
    // ... (Implement blackbody radiation calculations or use a library) ...
    //    return new double[]{1.0, 1.0, 1.0}; // Example placeholder
    //}
}
