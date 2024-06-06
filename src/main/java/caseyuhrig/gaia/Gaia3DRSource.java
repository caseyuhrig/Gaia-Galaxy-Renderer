package caseyuhrig.gaia;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;

// https://github.com/Starlink/starjava/blob/master/ttools/src/main/uk/ac/starlink/ttools/func/Gaia.java
public class Gaia3DRSource {

    public Long solution_id; // Solution Identifier
    public String designation; // Unique source designation (unique across all Data Releases)
    public Long source_id; // Unique source identifier (unique within a particular Data Release)
    public Long random_index; // Random index for use when selecting subsets
    public Double ref_epoch; // Reference epoch
    public Double ra; // Right ascension
    public Float ra_error; // Standard error of right ascension
    public Double dec; // Declination
    public Float dec_error; // Standard error of declination
    public Double parallax; // Parallax
    public Float parallax_error; // Standard error of parallax
    public Float parallax_over_error; // Parallax divided by its standard error
    public Float pm; // Total proper motion
    public Double pmra; // Proper motion in right ascension direction
    public Float pmra_error; // Standard error of proper motion in right ascension direction
    public Double pmdec; // Proper motion in declination direction
    public Float pmdec_error; // Standard error of proper motion in declination direction
    public Float ra_dec_corr; // Correlation between right ascension and declination
    public Float ra_parallax_corr; // Correlation between right ascension and parallax
    public Float ra_pmra_corr; // Correlation between right ascension and proper motion in right ascension
    public Float ra_pmdec_corr; // Correlation between right ascension and proper motion in declination
    public Float dec_parallax_corr; // Correlation between declination and parallax
    public Float dec_pmra_corr; // Correlation between declination and proper motion in right ascension
    public Float dec_pmdec_corr; // Correlation between declination and proper motion in declination
    public Float parallax_pmra_corr; // Correlation between parallax and proper motion in right ascension
    public Float parallax_pmdec_corr; // Correlation between parallax and proper motion in declination
    public Float pmra_pmdec_corr; // Correlation between proper motion in right ascension and proper motion in declination
    public Short astrometric_n_obs_al; // Total number of observations in the along-scan (AL) direction
    public Short astrometric_n_obs_ac; // Total number of observations in the across-scan (AC) direction
    public Short astrometric_n_good_obs_al; // Number of good observations in the along-scan (AL) direction
    public Short astrometric_n_bad_obs_al; // Number of bad observations in the along-scan (AL) direction
    public Float astrometric_gof_al; // Goodness of fit statistic of model wrt along-scan observations
    public Float astrometric_chi2_al; // AL chi-square value
    public Float astrometric_excess_noise; // Excess noise of the source
    public Float astrometric_excess_noise_sig; // Significance of excess noise
    public Byte astrometric_params_solved; // Which parameters have been solved for?
    public Boolean astrometric_primary_flag; // Primary or secondary
    public Float nu_eff_used_in_astrometry; // Effective wavenumber of the source used in the astrometric solution
    public Float pseudocolour; // Astrometrically estimated pseudocolour of the source
    public Float pseudocolour_error; // Standard error of the pseudocolour of the source
    public Float ra_pseudocolour_corr; // Correlation between right ascension and pseudocolour
    public Float dec_pseudocolour_corr; // Correlation between declination and pseudocolour
    public Float parallax_pseudocolour_corr; // Correlation between parallax and pseudocolour
    public Float pmra_pseudocolour_corr; // Correlation between proper motion in right ascension and pseudocolour
    public Float pmdec_pseudocolour_corr; // Correlation between proper motion in declination and pseudocolour
    public Short astrometric_matched_transits; // Matched FOV transits used in the AGIS solution
    public Short visibility_periods_used; // Number of visibility periods used in Astrometric solution
    public Float astrometric_sigma5d_max; // The longest semi-major axis of the 5-d error ellipsoid
    public Short matched_transits; // The number of transits matched to this source
    public Short new_matched_transits; // The number of transits newly incorporated into an existing source in the current cycle
    public Short matched_transits_removed; // The number of transits removed from an existing source in the current cycle
    public Float ipd_gof_harmonic_amplitude; // Amplitude of the IPD GoF versus position angle of scan
    public Float ipd_gof_harmonic_phase; // Phase of the IPD GoF versus position angle of scan
    public Byte ipd_frac_multi_peak; // Percent of successful-IPD windows with more than one peak
    public Byte ipd_frac_odd_win; // Percent of transits with truncated windows or multiple gate
    public Float ruwe; // Renormalised unit weight error
    public Float scan_direction_strength_k1; // Degree of concentration of scan directions across the source
    public Float scan_direction_strength_k2; // Degree of concentration of scan directions across the source
    public Float scan_direction_strength_k3; // Degree of concentration of scan directions across the source
    public Float scan_direction_strength_k4; // Degree of concentration of scan directions across the source
    public Float scan_direction_mean_k1; // Mean position angle of scan directions across the source
    public Float scan_direction_mean_k2; // Mean position angle of scan directions across the source
    public Float scan_direction_mean_k3; // Mean position angle of scan directions across the source
    public Float scan_direction_mean_k4; // Mean position angle of scan directions across the source
    public Boolean duplicated_source; // Source with multiple source identifiers
    public Short phot_g_n_obs; // Number of observations contributing to G photometry
    public Double phot_g_mean_flux; // G-band mean flux
    public Float phot_g_mean_flux_error; // Error on G-band mean flux
    public Float phot_g_mean_flux_over_error; // G-band mean flux divided by its error
    public Float phot_g_mean_mag; // G-band mean magnitude
    public Short phot_bp_n_obs; // Number of observations contributing to BP photometry
    public Double phot_bp_mean_flux; // Integrated BP mean flux
    public Float phot_bp_mean_flux_error; // Error on the integrated BP mean flux
    public Float phot_bp_mean_flux_over_error; // Integrated BP mean flux divided by its error
    public Float phot_bp_mean_mag; // Integrated BP mean magnitude
    public Short phot_rp_n_obs; // Number of observations contributing to RP photometry
    public Double phot_rp_mean_flux; // Integrated RP mean flux
    public Float phot_rp_mean_flux_error; // Error on the integrated RP mean flux
    public Float phot_rp_mean_flux_over_error; // Integrated RP mean flux divided by its error
    public Float phot_rp_mean_mag; // Integrated RP mean magnitude
    public Float phot_bp_rp_excess_factor; // BP/RP excess factor
    public Short phot_bp_n_contaminated_transits; // Number of BP contaminated transits
    public Short phot_bp_n_blended_transits; // Number of BP blended transits
    public Short phot_rp_n_contaminated_transits; // Number of RP contaminated transits
    public Short phot_rp_n_blended_transits; // Number of RP blended transits
    public Byte phot_proc_mode; // Photometry processing mode
    public Float bp_rp; // BP - RP colour
    public Float bp_g; // BP - G colour
    public Float g_rp; // G - RP colour
    public Float radial_velocity; // Radial velocity
    public Float radial_velocity_error; // Radial velocity error
    public Byte rv_method_used; // Method used to obtain the radial velocity
    public Short rv_nb_transits; // Number of transits used to compute the radial velocity
    public Short rv_nb_deblended_transits; // Number of valid transits that have undergone deblending
    public Short rv_visibility_periods_used; // Number of visibility periods used to estimate the radial velocity
    public Float rv_expected_sig_to_noise; // Expected signal to noise ratio in the combination of the spectra used to obtain the radial velocity
    public Float rv_renormalised_gof; // Radial velocity renormalised goodness of fit
    public Float rv_chisq_pvalue; // P-value for constancy based on a chi-squared criterion
    public Float rv_time_duration; // Time coverage of the radial velocity time series
    public Float rv_amplitude_robust; // Total amplitude in the radial velocity time series after outlier removal
    public Float rv_template_teff; // Teff of the template used to compute the radial velocity
    public Float rv_template_logg; // Logg of the template used to compute the radial velocity
    public Float rv_template_fe_h; // [Fe/H] of the template used to compute the radial velocity
    public Short rv_atm_param_origin; // Origin of the atmospheric parameters associated to the template
    public Float vbroad; // Spectral line broadening parameter
    public Float vbroad_error; // Uncertainty on the spectral line broadening
    public Short vbroad_nb_transits; // Number of transits used to compute vbroad
    public Float grvs_mag; // Integrated Grvs magnitude
    public Float grvs_mag_error; // Grvs magnitude uncertainty
    public Short grvs_mag_nb_transits; // Number of transits used to compute Grvs
    public Float rvs_spec_sig_to_noise; // Signal to noise ratio in the mean RVS spectrum
    public String phot_variable_flag; // Photometric variability flag
    public Double l; // Galactic longitude
    public Double b; // Galactic latitude
    public Double ecl_lon; // Ecliptic longitude
    public Double ecl_lat; // Ecliptic latitude
    public Boolean in_qso_candidates; // Flag indicating the availability of additional information in the QSO candidates table
    public Boolean in_galaxy_candidates; // Flag indicating the availability of additional information in the galaxy candidates table
    public Short non_single_star; // Flag indicating the availability of additional information in the various Non-Single Star tables
    public Boolean has_xp_continuous; // Flag indicating the availability of mean BP/RP spectrum in continuous representation for this source
    public Boolean has_xp_sampled; // Flag indicating the availability of mean BP/RP spectrum in sampled form for this source
    public Boolean has_rvs; // Flag indicating the availability of mean RVS spectrum for this source
    public Boolean has_epoch_photometry; // Flag indicating the availability of epoch photometry for this source
    public Boolean has_epoch_rv; // Flag indicating the availability of epoch radial velocity for this source
    public Boolean has_mcmc_gspphot; // Flag indicating the availability of GSP-Phot MCMC samples for this source
    public Boolean has_mcmc_msc; // Flag indicating the availability of MSC MCMC samples for this source
    public Boolean in_andromeda_survey; // Flag indicating that the source is present in the Gaia Andromeda Photometric Survey (GAPS)
    public Float classprob_dsc_combmod_quasar; // Probability from DSC-Combmod of being a quasar
    public Float classprob_dsc_combmod_galaxy; // Probability from DSC-Combmod of being a galaxy
    public Float classprob_dsc_combmod_star; // Probability from DSC-Combmod of being a star
    public Float teff_gspphot; // Effective temperature from GSP-Phot
    public Float teff_gspphot_lower; // Lower confidence level of effective temperature from GSP-Phot
    public Float teff_gspphot_upper; // Upper confidence level of effective temperature from GSP-Phot
    public Float logg_gspphot; // Surface gravity from GSP-Phot
    public Float logg_gspphot_lower; // Lower confidence level of surface gravity from GSP-Phot
    public Float logg_gspphot_upper; // Upper confidence level of surface gravity from GSP-Phot
    public Float mh_gspphot; // Metallicity from GSP-Phot
    public Float mh_gspphot_lower; // Lower confidence level of metallicity from GSP-Phot
    public Float mh_gspphot_upper; // Upper confidence level of metallicity from GSP-Phot
    public Float distance_gspphot; // Distance estimate from GSP-Phot
    public Float distance_gspphot_lower; // Lower confidence level of distance estimate from GSP-Phot
    public Float distance_gspphot_upper; // Upper confidence level of distance estimate from GSP-Phot
    public Float azero_gspphot; // Monochromatic extinction A0 at 547.7nm from GSP-Phot
    public Float azero_gspphot_lower; // Lower confidence level of monochromatic extinction A0 from GSP-Phot
    public Float azero_gspphot_upper; // Upper confidence level of monochromatic extinction A0 from GSP-Phot
    public Float ag_gspphot; // Extinction in G band from GSP-Phot
    public Float ag_gspphot_lower; // Lower confidence level of extinction in G band from GSP-Phot
    public Float ag_gspphot_upper; // Upper confidence level of extinction in G band from GSP-Phot
    public Float ebpminrp_gspphot; // Reddening E(G_BP - G_RP) from GSP-Phot
    public Float ebpminrp_gspphot_lower; // Lower confidence level of reddening E(G_BP - G_RP) from GSP-Phot
    public Float ebpminrp_gspphot_upper; // Upper confidence level of reddening E(G_BP - G_RP) from GSP-Phot
    public String libname_gspphot; // Name of library that achieves the highest mean log-posterior in MCMC samples and was used to derive GSP-Phot parameters in this table

    // screen space variables for rendering
    public Double x, y, z;

    public static final String[] names = {"solution_id", "designation", "source_id", "random_index", "ref_epoch", "ra", "ra_error", "dec", "dec_error", "parallax", "parallax_error", "parallax_over_error", "pm", "pmra", "pmra_error", "pmdec", "pmdec_error", "ra_dec_corr", "ra_parallax_corr", "ra_pmra_corr", "ra_pmdec_corr", "dec_parallax_corr", "dec_pmra_corr", "dec_pmdec_corr", "parallax_pmra_corr", "parallax_pmdec_corr", "pmra_pmdec_corr", "astrometric_n_obs_al", "astrometric_n_obs_ac", "astrometric_n_good_obs_al", "astrometric_n_bad_obs_al", "astrometric_gof_al", "astrometric_chi2_al", "astrometric_excess_noise", "astrometric_excess_noise_sig", "astrometric_params_solved", "astrometric_primary_flag", "nu_eff_used_in_astrometry", "pseudocolour", "pseudocolour_error", "ra_pseudocolour_corr", "dec_pseudocolour_corr", "parallax_pseudocolour_corr", "pmra_pseudocolour_corr", "pmdec_pseudocolour_corr", "astrometric_matched_transits", "visibility_periods_used", "astrometric_sigma5d_max", "matched_transits", "new_matched_transits", "matched_transits_removed", "ipd_gof_harmonic_amplitude", "ipd_gof_harmonic_phase", "ipd_frac_multi_peak", "ipd_frac_odd_win", "ruwe", "scan_direction_strength_k1", "scan_direction_strength_k2", "scan_direction_strength_k3", "scan_direction_strength_k4", "scan_direction_mean_k1", "scan_direction_mean_k2", "scan_direction_mean_k3", "scan_direction_mean_k4", "duplicated_source", "phot_g_n_obs", "phot_g_mean_flux", "phot_g_mean_flux_error", "phot_g_mean_flux_over_error", "phot_g_mean_mag", "phot_bp_n_obs", "phot_bp_mean_flux", "phot_bp_mean_flux_error", "phot_bp_mean_flux_over_error", "phot_bp_mean_mag", "phot_rp_n_obs", "phot_rp_mean_flux", "phot_rp_mean_flux_error", "phot_rp_mean_flux_over_error", "phot_rp_mean_mag", "phot_bp_rp_excess_factor", "phot_bp_n_contaminated_transits", "phot_bp_n_blended_transits", "phot_rp_n_contaminated_transits", "phot_rp_n_blended_transits", "phot_proc_mode", "bp_rp", "bp_g", "g_rp", "radial_velocity", "radial_velocity_error", "rv_method_used", "rv_nb_transits", "rv_nb_deblended_transits", "rv_visibility_periods_used", "rv_expected_sig_to_noise", "rv_renormalised_gof", "rv_chisq_pvalue", "rv_time_duration", "rv_amplitude_robust", "rv_template_teff", "rv_template_logg", "rv_template_fe_h", "rv_atm_param_origin", "vbroad", "vbroad_error", "vbroad_nb_transits", "grvs_mag", "grvs_mag_error", "grvs_mag_nb_transits", "rvs_spec_sig_to_noise", "phot_variable_flag", "l", "b", "ecl_lon", "ecl_lat", "in_qso_candidates", "in_galaxy_candidates", "non_single_star", "has_xp_continuous", "has_xp_sampled", "has_rvs", "has_epoch_photometry", "has_epoch_rv", "has_mcmc_gspphot", "has_mcmc_msc", "in_andromeda_survey", "classprob_dsc_combmod_quasar", "classprob_dsc_combmod_galaxy", "classprob_dsc_combmod_star", "teff_gspphot", "teff_gspphot_lower", "teff_gspphot_upper", "logg_gspphot", "logg_gspphot_lower", "logg_gspphot_upper", "mh_gspphot", "mh_gspphot_lower", "mh_gspphot_upper", "distance_gspphot", "distance_gspphot_lower", "distance_gspphot_upper", "azero_gspphot", "azero_gspphot_lower", "azero_gspphot_upper", "ag_gspphot", "ag_gspphot_lower", "ag_gspphot_upper", "ebpminrp_gspphot", "ebpminrp_gspphot_lower", "ebpminrp_gspphot_upper", "libname_gspphot", "x", "y", "z"};
    private static final String columnNames = "solution_id, designation, source_id, random_index, ref_epoch, ra, ra_error, dec, dec_error, parallax, "
            + "parallax_error, parallax_over_error, pm, pmra, pmra_error, pmdec, pmdec_error, ra_dec_corr, ra_parallax_corr, "
            + "ra_pmra_corr, ra_pmdec_corr, dec_parallax_corr, dec_pmra_corr, dec_pmdec_corr, parallax_pmra_corr, "
            + "parallax_pmdec_corr, pmra_pmdec_corr, astrometric_n_obs_al, astrometric_n_obs_ac, astrometric_n_good_obs_al, "
            + "astrometric_n_bad_obs_al, astrometric_gof_al, astrometric_chi2_al, astrometric_excess_noise, "
            + "astrometric_excess_noise_sig, astrometric_params_solved, astrometric_primary_flag, nu_eff_used_in_astrometry, "
            + "pseudocolour, pseudocolour_error, ra_pseudocolour_corr, dec_pseudocolour_corr, parallax_pseudocolour_corr, "
            + "pmra_pseudocolour_corr, pmdec_pseudocolour_corr, astrometric_matched_transits, visibility_periods_used, "
            + "astrometric_sigma5d_max, matched_transits, new_matched_transits, matched_transits_removed, "
            + "ipd_gof_harmonic_amplitude, ipd_gof_harmonic_phase, ipd_frac_multi_peak, ipd_frac_odd_win, ruwe, "
            + "scan_direction_strength_k1, scan_direction_strength_k2, scan_direction_strength_k3, scan_direction_strength_k4, "
            + "scan_direction_mean_k1, scan_direction_mean_k2, scan_direction_mean_k3, scan_direction_mean_k4, "
            + "duplicated_source, phot_g_n_obs, phot_g_mean_flux, phot_g_mean_flux_error, phot_g_mean_flux_over_error, "
            + "phot_g_mean_mag, phot_bp_n_obs, phot_bp_mean_flux, phot_bp_mean_flux_error, phot_bp_mean_flux_over_error, "
            + "phot_bp_mean_mag, phot_rp_n_obs, phot_rp_mean_flux, phot_rp_mean_flux_error, phot_rp_mean_flux_over_error, "
            + "phot_rp_mean_mag, phot_bp_rp_excess_factor, phot_bp_n_contaminated_transits, phot_bp_n_blended_transits, "
            + "phot_rp_n_contaminated_transits, phot_rp_n_blended_transits, phot_proc_mode, bp_rp, bp_g, g_rp, "
            + "radial_velocity, radial_velocity_error, rv_method_used, rv_nb_transits, rv_nb_deblended_transits, "
            + "rv_visibility_periods_used, rv_expected_sig_to_noise, rv_renormalised_gof, rv_chisq_pvalue, rv_time_duration, "
            + "rv_amplitude_robust, rv_template_teff, rv_template_logg, rv_template_fe_h, rv_atm_param_origin, vbroad, "
            + "vbroad_error, vbroad_nb_transits, grvs_mag, grvs_mag_error, grvs_mag_nb_transits, rvs_spec_sig_to_noise, "
            + "phot_variable_flag, l, b, ecl_lon, ecl_lat, in_qso_candidates, in_galaxy_candidates, non_single_star, "
            + "has_xp_continuous, has_xp_sampled, has_rvs, has_epoch_photometry, has_epoch_rv, has_mcmc_gspphot, "
            + "has_mcmc_msc, in_andromeda_survey, classprob_dsc_combmod_quasar, classprob_dsc_combmod_galaxy, "
            + "classprob_dsc_combmod_star, teff_gspphot, teff_gspphot_lower, teff_gspphot_upper, logg_gspphot, "
            + "logg_gspphot_lower, logg_gspphot_upper, mh_gspphot, mh_gspphot_lower, mh_gspphot_upper, distance_gspphot, "
            + "distance_gspphot_lower, distance_gspphot_upper, azero_gspphot, azero_gspphot_lower, azero_gspphot_upper, "
            + "ag_gspphot, ag_gspphot_lower, ag_gspphot_upper, ebpminrp_gspphot, ebpminrp_gspphot_lower, ebpminrp_gspphot_upper, "
            + "libname_gspphot, x, y, z";

    private static final String INSERT_GAIADR3SOURCE_SQL_old = "INSERT INTO source (" +
            columnNames +
            ") VALUES (" +
            String.join(", ", Collections.nCopies(155, "?")) +
            ")";

    private static final String INSERT_GAIADR3SOURCE_SQL = String.format("INSERT INTO source (%s) VALUES (%s)",
            columnNames,
            String.join(", ", Collections.nCopies(155, "?"))
    );

    public static void insertGaia3DRSource(final Connection connection, final Gaia3DRSource gaia3DRSource)
            throws Exception {
        try (final var preparedStatement = connection.prepareStatement(INSERT_GAIADR3SOURCE_SQL)) {
            int n = 1;
            for (final var name : names) {
                final Field field = gaia3DRSource.getClass().getDeclaredField(name);
                preparedStatement.setObject(n, field.get(gaia3DRSource));
                n++;
            }
            preparedStatement.executeUpdate();
        } catch (final SQLException e) {
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
        }
    }

    public static void insertGaia3DRSourceSmall(final Connection connection, final Gaia3DRSource gaia3DRSource)
            throws Exception {
        try (final var preparedStatement = connection.prepareStatement("INSERT INTO source (source_id, ra, dec, parallax, bp_rp, bp_g, g_rp) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
            preparedStatement.setObject(1, get(gaia3DRSource, "source_id"));
            preparedStatement.setObject(2, get(gaia3DRSource, "ra"));
            preparedStatement.setObject(3, get(gaia3DRSource, "dec"));
            preparedStatement.setObject(4, get(gaia3DRSource, "parallax"));
            preparedStatement.setObject(5, get(gaia3DRSource, "bp_rp"));
            preparedStatement.setObject(6, get(gaia3DRSource, "bp_g"));
            preparedStatement.setObject(7, get(gaia3DRSource, "g_rp"));
            preparedStatement.executeUpdate();
        } catch (final SQLException e) {
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
        }
    }

    public static void updateMisc(final Connection connection, final Gaia3DRSource source)
            throws Exception {
        try (final var preparedStatement = connection.prepareStatement("UPDATE source SET in_galaxy_candidates = ? WHERE source_id = ?")) {
            preparedStatement.setObject(1, get(source, "in_galaxy_candidates"));
            preparedStatement.setObject(2, get(source, "source_id"));
            preparedStatement.executeUpdate();
        } catch (final SQLException e) {
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
        }
    }

    public static Object get(final Gaia3DRSource gaia3DRSource, final String name) {
        try {
            final Field field = gaia3DRSource.getClass().getDeclaredField(name);
            return field.get(gaia3DRSource);
        } catch (final NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e.getLocalizedMessage(), e);
        }
    }

    public static boolean sourceExists(final Connection connection, final Long source_id)
            throws Exception {
        try (final var statement = connection.prepareStatement("SELECT source_id FROM source WHERE source_id = ?")) {
            statement.setLong(1, source_id);
            try (final var rs = statement.executeQuery()) {
                return rs.next();
            }
        } catch (final SQLException e) {
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
            throw e;
        }
    }

    public static Gaia3DRSource getSource(final ResultSet rs) {
        final Gaia3DRSource source = new Gaia3DRSource();
        try {
            for (final var name : names) {
                final Field field = source.getClass().getDeclaredField(name);
                field.set(source, rs.getObject(name));
            }
        } catch (final SQLException | NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e.getLocalizedMessage(), e);
        }
        return source;
    }

    void dump() {
        System.out.println("Source ID: " + source_id + "\tRA: " + ra + "\tDec: " + dec + "\tParallax: " + parallax + "\tX: " + x + "\tY: " + y + "\tZ: " + z);
    }
}
