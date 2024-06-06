package caseyuhrig.gaia;

import java.util.ArrayList;
import java.util.Arrays;

public class GaiaConst {

    /**
     * Minimum and maximum values for the Gaia data set.
     */
    public final static double min_ra = 3.4096239126626443e-07;
    public final static double max_ra = 359.999999939548;
    public final static double min_dec = -89.99287859590359;
    public final static double max_dec = 89.99005196682685;

    public final static double min_parallax = -187.02939637423492;
    public final static double max_parallax = 768.0665391873573;
    public final static double min_parallax_error = 0.0071899574;
    public final static double max_parallax_error = 5.802274;

    public final static double min_bp_rp = -7.3475304;
    public final static double max_bp_rp = 10.193149;
    public final static double min_bp_g = -8.431045;
    public final static double max_bp_g = 7.015131;
    public final static double min_g_rp = -4.8746176;
    public final static double max_g_rp = 11.520412;

    public final static double min_phot_bp_mean_mag = 2.3980012;
    public final static double max_phot_bp_mean_mag = 25.333084;
    public final static double min_phot_rp_mean_mag = 1.7436333;
    public final static double max_phot_rp_mean_mag = 24.695997;
    public final static double min_phot_g_mean_mag = 1.731607;
    public final static double max_phot_g_mean_mag = 22.956425;

    public final static double min_l = 1.0606335061246415e-07;
    public final static double max_l = 359.9999999850258;
    public final static double min_b = -89.99366530605397;
    public final static double max_b = 89.98796453163729;

    public final static double min_pseudocolour = -3.2705555;
    public final static double max_pseudocolour = 5.841313;

    public final static double min_phot_g_mean_flux = 12.370194398444749;
    public final static double max_phot_g_mean_flux = 3822116782.6336956;
    public final static double min_phot_bp_mean_flux = 1.0050400371436263;
    public final static double max_phot_bp_mean_flux = 1500432409.3837109;
    public final static double min_phot_rp_mean_flux = 1.0489614290056928;
    public final static double max_phot_rp_mean_flux = 1591127209.4126902;
    public final static double min_grvs_mag = 2.7579873;
    public final static double max_grvs_mag = 14.099998;
    public final static double min_teff_gspphot = 2501.1814;
    public final static double max_teff_gspphot = 41504.02;
    public final static double min_mh_gspphot = -4.1503;
    public final static double max_mh_gspphot = 0.8;
    public final static double min_ag_gspphot = 0;
    public final static double max_ag_gspphot = 7.4106;

    // Astrometric excess noise
    public final static double min_astrometric_excess_noise = 0;
    public final static double max_astrometric_excess_noise = 1131.5846;

    /**
     * White reference
     * see: https://en.wikipedia.org/wiki/CIELAB_color_space#From_CIEXYZ_to_CIELAB[10]
     */
    public static final double REF_X = 95.047; // Observer= 2°, Illuminant= D65
    public static final double REF_Y = 100.000;
    public static final double REF_Z = 108.883;
    public static final double XYZ_m = 7.787037; // match in slope. Note commonly seen 7.787 gives worse results
    public static final double XYZ_t0 = 0.008856;

    public final static double LIGHT_YEARS_PER_PARSEC = 3.26;

    public final static ArrayList<String> messages = new ArrayList<>();


    public static double parallaxDistance(final double parallax) {
        return 1.0 / parallax * LIGHT_YEARS_PER_PARSEC;
    }


    public static double[] rgb(final double r, final double g, final double b) {
        return new double[]{r, g, b};
    }


    public static double[] divide(final double[] rgb, final double divisor) {
        return new double[]{rgb[0] / divisor, rgb[1] / divisor, rgb[2] / divisor};
    }


    public static double[] multiply(final double[] c1, final double[] c2) {
        return new double[]{c1[0] * c2[0], c1[1] * c2[1], c1[2] * c2[2]};
    }


    public static double[] multiply(final double[] rgb, final double value) {
        return new double[]{rgb[0] * value, rgb[1] * value, rgb[2] * value};
    }


    public static double normalize(final double value, final double min, final double max) {
        return (value - min) / (max - min);
    }


    // average 3 colors together
    public static double[] average(final double[] c1, final double[] c2, final double[] c3) {
        return new double[]{(c1[0] + c2[0] + c3[0]) / 3.0, (c1[1] + c2[1] + c3[1]) / 3.0, (c1[2] + c2[2] + c3[2]) / 3.0};
    }


    /**
     * Maximum normalization of an input.
     *
     * @param input The input to normalize.
     * @return The normalized output.
     */
    public static double[] normalize(final double[] input) {
        final double[] output = new double[input.length];
        final double max = Arrays.stream(input).max().getAsDouble();
        Arrays.setAll(output, i -> input[i] / max);
        return output;
    }


    /**
     * Average normalization of multiple inputs.
     *
     * @param inputs The inputs to normalize.
     * @return The normalized output.
     */
    public static double[] normalize(final double[]... inputs) {
        final int length = inputs[0].length;
        final double[] output = new double[inputs[0].length];
        final double max = inputs.length;
        for (int i = 0; i < inputs.length; i++) {
            for (int j = 0; j < length; j++) {
                output[j] += inputs[i][j];
            }
        }
        for (int j = 0; j < length; j++) {
            output[j] /= max;
        }
        return output;
    }


    /**
     * Clamps a value between a minimum and maximum value.
     * Calls <code>Math.max(min, Math.min(max, value))</code>
     *
     * @param value The value to clamp.
     * @param min   The minimum value.
     * @param max   The maximum value.
     * @return The clamped value.
     */
    public static double clamp(final double value, final double min, final double max) {
        return Math.max(min, Math.min(max, value));
    }

}
