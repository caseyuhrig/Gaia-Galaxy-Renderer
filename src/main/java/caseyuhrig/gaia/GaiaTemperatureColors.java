package caseyuhrig.gaia;

public class GaiaTemperatureColors {

    // Constants
    // Boltzmann constant (KB) in J/K, J = Joules, K = Kelvin
    private static final double KB = 1.380649E-23;
    // Planck constant (H) in J*s, J = Joules, s = seconds
    private static final double H = 6.62607015E-34;
    // Speed of light (C) in m/s, m = meters, s = seconds
    private static final double C = 299792458.0;
    private static final int WAVELENGTH_INCREMENTS = 200;

    // Gaia G-band limits
    private static final double GAIA_GBAND_LONG_LIMIT = 1050; // Example value
    private static final double GAIA_GBAND_SHORT_LIMIT = 330; // Example value

    // Arrays to store RGB values
    private static final double[] rgbRed = new double[32768];
    private static final double[] rgbGreen = new double[32768];
    private static final double[] rgbBlue = new double[32768];

    public static final State state = new State();


    public static int initRGBTables() {
        final Config config = new Config();
        // Set example config values
        config.cameraWbEnable = true;
        config.cameraWbTemp = 6500;
        config.cameraColorSaturation = 1.0;
        config.redFilterLongLimit = 700;
        config.redFilterShortLimit = 600;
        config.greenFilterLongLimit = 550;
        config.greenFilterShortLimit = 500;
        config.blueFilterLongLimit = 450;
        config.blueFilterShortLimit = 400;

        return initRGBTables(config, state);
    }


    public static int initRGBTables(final Config config, final State state) {
        double temp;
        final double redWbFactor;
        final double greenWbFactor;
        final double blueWbFactor;
        double colorMax;
        double colorMin;
        double colorMid;
        double wavelengthStart;
        double wavelengthEnd;
        double wavelength;
        double specificIntensity;
        double gbandIntensity;
        double redIntensity;
        double greenIntensity;
        double blueIntensity;
        final double wavelengthIncrement;

        // Determine wavelength scan range and increment
        wavelengthStart = GAIA_GBAND_LONG_LIMIT;
        wavelengthStart = Math.max(wavelengthStart, config.redFilterLongLimit);
        wavelengthStart = Math.max(wavelengthStart, config.greenFilterLongLimit);
        wavelengthStart = Math.max(wavelengthStart, config.blueFilterLongLimit);

        wavelengthEnd = GAIA_GBAND_SHORT_LIMIT;
        wavelengthEnd = Math.min(wavelengthEnd, config.redFilterShortLimit);
        wavelengthEnd = Math.min(wavelengthEnd, config.greenFilterShortLimit);
        wavelengthEnd = Math.min(wavelengthEnd, config.blueFilterShortLimit);

        wavelengthIncrement = (wavelengthStart - wavelengthEnd) / (double) WAVELENGTH_INCREMENTS;

        // Calculate white balance factors
        temp = config.cameraWbEnable ? config.cameraWbTemp : 4300;

        gbandIntensity = 0.0;
        redIntensity = 0.0;
        greenIntensity = 0.0;
        blueIntensity = 0.0;

        for (wavelength = wavelengthStart; wavelength >= wavelengthEnd; wavelength -= wavelengthIncrement) {
            specificIntensity = 1.0 / (Math.pow(wavelength * 1.0E-9, 5.0) *
                    (Math.exp(H * C / (wavelength * 1.0E-9 * KB * temp)) - 1));

            if (wavelength <= GAIA_GBAND_LONG_LIMIT && wavelength >= GAIA_GBAND_SHORT_LIMIT) {
                gbandIntensity += specificIntensity * GaiaPassbands.getGaiaTransmissivityG((int) (wavelength + 0.5));
            }
            if (wavelength <= config.redFilterLongLimit && wavelength >= config.redFilterShortLimit) {
                redIntensity += specificIntensity;
            }
            if (wavelength <= config.greenFilterLongLimit && wavelength >= config.greenFilterShortLimit) {
                greenIntensity += specificIntensity;
            }
            if (wavelength <= config.blueFilterLongLimit && wavelength >= config.blueFilterShortLimit) {
                blueIntensity += specificIntensity;
            }
        }

        if (config.cameraWbEnable) {
            redWbFactor = gbandIntensity / redIntensity;
            greenWbFactor = gbandIntensity / greenIntensity;
            blueWbFactor = gbandIntensity / blueIntensity;
        } else {
            redWbFactor = gbandIntensity / greenIntensity;
            greenWbFactor = gbandIntensity / greenIntensity;
            blueWbFactor = gbandIntensity / greenIntensity;
        }

        // Calculate RGB values for each integer Kelvin temperature from 0 - 32767K
        for (int i = 0; i < 32768; i++) {
            temp = i;
            gbandIntensity = 0.0;
            redIntensity = 0.0;
            greenIntensity = 0.0;
            blueIntensity = 0.0;

            for (wavelength = wavelengthStart; wavelength >= wavelengthEnd; wavelength -= wavelengthIncrement) {
                specificIntensity = 1.0 / (Math.pow(wavelength * 1.0E-9, 5.0) *
                        (Math.exp(H * C / (wavelength * 1.0E-9 * KB * temp)) - 1));

                if (wavelength <= GAIA_GBAND_LONG_LIMIT && wavelength >= GAIA_GBAND_SHORT_LIMIT) {
                    gbandIntensity += specificIntensity * GaiaPassbands.getGaiaTransmissivityG((int) (wavelength + 0.5));
                }
                if (wavelength <= config.redFilterLongLimit && wavelength >= config.redFilterShortLimit) {
                    redIntensity += specificIntensity;
                }
                if (wavelength <= config.greenFilterLongLimit && wavelength >= config.greenFilterShortLimit) {
                    greenIntensity += specificIntensity;
                }
                if (wavelength <= config.blueFilterLongLimit && wavelength >= config.blueFilterShortLimit) {
                    blueIntensity += specificIntensity;
                }
            }

            if (gbandIntensity != 0.0) {
                redIntensity = redWbFactor * redIntensity / gbandIntensity;
                greenIntensity = greenWbFactor * greenIntensity / gbandIntensity;
                blueIntensity = blueWbFactor * blueIntensity / gbandIntensity;
            }

            colorMax = Math.max(redIntensity, Math.max(greenIntensity, blueIntensity));
            colorMin = Math.min(redIntensity, Math.min(greenIntensity, blueIntensity));
            colorMid = (colorMax + colorMin) / 2.0;

            redIntensity = colorMid + (config.cameraColorSaturation * (redIntensity - colorMid));
            redIntensity = Math.max(0, redIntensity);

            greenIntensity = colorMid + (config.cameraColorSaturation * (greenIntensity - colorMid));
            greenIntensity = Math.max(0, greenIntensity);

            blueIntensity = colorMid + (config.cameraColorSaturation * (blueIntensity - colorMid));
            blueIntensity = Math.max(0, blueIntensity);

            rgbRed[i] = redIntensity;
            rgbGreen[i] = greenIntensity;
            rgbBlue[i] = blueIntensity;
        }

        state.setRgbRed(rgbRed);
        state.setRgbGreen(rgbGreen);
        state.setRgbBlue(rgbBlue);

        return 0;
    }


    // Mock classes for config and state
    public static class Config {
        public boolean cameraWbEnable;
        public double cameraWbTemp;
        public double cameraColorSaturation;
        public double redFilterLongLimit;
        public double redFilterShortLimit;
        public double greenFilterLongLimit;
        public double greenFilterShortLimit;
        public double blueFilterLongLimit;
        public double blueFilterShortLimit;
    }

    public static class State {
        private double[] rgbRed;
        private double[] rgbGreen;
        private double[] rgbBlue;

        public double[] getRgbRed() {
            return rgbRed;
        }

        public void setRgbRed(final double[] rgbRed) {
            this.rgbRed = rgbRed;
        }

        public double[] getRgbGreen() {
            return rgbGreen;
        }

        public void setRgbGreen(final double[] rgbGreen) {
            this.rgbGreen = rgbGreen;
        }

        public double[] getRgbBlue() {
            return rgbBlue;
        }

        public void setRgbBlue(final double[] rgbBlue) {
            this.rgbBlue = rgbBlue;
        }
    }
}
