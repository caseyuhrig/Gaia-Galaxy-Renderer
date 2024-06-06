
-- select min(ra), max(ra), min(dec), max(dec) from gaia_dr3_source;

--SET search_path TO myschema, public, pg_catalog;

CREATE ROLE gaia LOGIN CREATEDB PASSWORD 'gaia';

CREATE TABLESPACE gaia_dr3_space LOCATION 'D:/gaia_dr3_db';

CREATE TABLESPACE gaia_tablespace LOCATION 'E:/gaia_db';

CREATE SCHEMA gaia_schema AUTHORIZATION gaia;


CREATE DATABASE gaia_ssd
    WITH ENCODING 'UTF8'
    LC_COLLATE = 'English_United States'
    LC_CTYPE = 'English_United States'
    TABLESPACE gaia_dr3_space;

CREATE DATABASE gaia
    WITH ENCODING 'UTF8'
    LC_COLLATE = 'English_United States'
    LC_CTYPE = 'English_United States'
    TABLESPACE gaia_tablespace;



CREATE OR REPLACE FUNCTION get_table_density(schema_name text, table_name text)
    RETURNS bigint AS
$$
DECLARE
    result bigint;
BEGIN
    SELECT (CASE WHEN c.reltuples < 0 THEN NULL           -- never vacuumed
                 WHEN c.relpages = 0 THEN float8 '0'      -- empty table
                 ELSE c.reltuples / c.relpages END
        * (pg_catalog.pg_relation_size(c.oid)
            / pg_catalog.current_setting('block_size')::int)
               )::bigint
    INTO result
    FROM   pg_catalog.pg_class c
    WHERE  c.oid = (schema_name || '.' || table_name)::regclass;

    RETURN result;
END;
$$ LANGUAGE plpgsql STABLE;


CREATE OR REPLACE FUNCTION get_table_density(table_name text)
    RETURNS bigint AS
$$
BEGIN
    RETURN get_table_density(current_schema(), table_name);
END;
$$ LANGUAGE plpgsql STABLE;


CREATE TABLE source
(
    source_id BIGINT PRIMARY KEY,
    ra          DOUBLE PRECISION,
    dec         DOUBLE PRECISION,
    parallax    DOUBLE PRECISION,
    bp_rp       REAL,
    bp_g        REAL,
    g_rp        REAL
) TABLESPACE gaia_dr3_space;


CREATE TABLE source (
        solution_id BIGINT,
        designation TEXT,
        source_id BIGINT,
        random_index BIGINT,
        ref_epoch DOUBLE PRECISION,
        ra DOUBLE PRECISION,
        ra_error REAL,
        dec DOUBLE PRECISION,
        dec_error REAL,
        parallax DOUBLE PRECISION,
        parallax_error REAL,
        parallax_over_error REAL,
        pm REAL,
        pmra DOUBLE PRECISION,
        pmra_error REAL,
        pmdec DOUBLE PRECISION,
        pmdec_error REAL,
        ra_dec_corr REAL,
        ra_parallax_corr REAL,
        ra_pmra_corr REAL,
        ra_pmdec_corr REAL,
        dec_parallax_corr REAL,
        dec_pmra_corr REAL,
        dec_pmdec_corr REAL,
        parallax_pmra_corr REAL,
        parallax_pmdec_corr REAL,
        pmra_pmdec_corr REAL,
        astrometric_n_obs_al SMALLINT,
        astrometric_n_obs_ac SMALLINT,
        astrometric_n_good_obs_al SMALLINT,
        astrometric_n_bad_obs_al SMALLINT,
        astrometric_gof_al REAL,
        astrometric_chi2_al REAL,
        astrometric_excess_noise REAL,
        astrometric_excess_noise_sig REAL,
        astrometric_params_solved SMALLINT,
        astrometric_primary_flag BOOLEAN,
        nu_eff_used_in_astrometry REAL,
        pseudocolour REAL,
        pseudocolour_error REAL,
        ra_pseudocolour_corr REAL,
        dec_pseudocolour_corr REAL,
        parallax_pseudocolour_corr REAL,
        pmra_pseudocolour_corr REAL,
        pmdec_pseudocolour_corr REAL,
        astrometric_matched_transits SMALLINT,
        visibility_periods_used SMALLINT,
        astrometric_sigma5d_max REAL,
        matched_transits SMALLINT,
        new_matched_transits SMALLINT,
        matched_transits_removed SMALLINT,
        ipd_gof_harmonic_amplitude REAL,
        ipd_gof_harmonic_phase REAL,
        ipd_frac_multi_peak SMALLINT,
        ipd_frac_odd_win SMALLINT,
        ruwe REAL,
        scan_direction_strength_k1 REAL,
        scan_direction_strength_k2 REAL,
        scan_direction_strength_k3 REAL,
        scan_direction_strength_k4 REAL,
        scan_direction_mean_k1 REAL,
        scan_direction_mean_k2 REAL,
        scan_direction_mean_k3 REAL,
        scan_direction_mean_k4 REAL,
        duplicated_source BOOLEAN,
        phot_g_n_obs SMALLINT,
        phot_g_mean_flux DOUBLE PRECISION,
        phot_g_mean_flux_error REAL,
        phot_g_mean_flux_over_error REAL,
        phot_g_mean_mag REAL,
        phot_bp_n_obs SMALLINT,
        phot_bp_mean_flux DOUBLE PRECISION,
        phot_bp_mean_flux_error REAL,
        phot_bp_mean_flux_over_error REAL,
        phot_bp_mean_mag REAL,
        phot_rp_n_obs SMALLINT,
        phot_rp_mean_flux DOUBLE PRECISION,
        phot_rp_mean_flux_error REAL,
        phot_rp_mean_flux_over_error REAL,
        phot_rp_mean_mag REAL,
        phot_bp_rp_excess_factor REAL,
        phot_bp_n_contaminated_transits SMALLINT,
        phot_bp_n_blended_transits SMALLINT,
        phot_rp_n_contaminated_transits SMALLINT,
        phot_rp_n_blended_transits SMALLINT,
        phot_proc_mode SMALLINT,
        bp_rp REAL,
        bp_g REAL,
        g_rp REAL,
        radial_velocity REAL,
        radial_velocity_error REAL,
        rv_method_used CHAR,
        rv_nb_transits SMALLINT,
        rv_nb_deblended_transits SMALLINT,
        rv_visibility_periods_used SMALLINT,
        rv_expected_sig_to_noise REAL,
        rv_renormalised_gof REAL,
        rv_chisq_pvalue REAL,
        rv_time_duration REAL,
        rv_amplitude_robust REAL,
        rv_template_teff REAL,
        rv_template_logg REAL,
        rv_template_fe_h REAL,
        rv_atm_param_origin SMALLINT,
        vbroad REAL,
        vbroad_error REAL,
        vbroad_nb_transits SMALLINT,
        grvs_mag REAL,
        grvs_mag_error REAL,
        grvs_mag_nb_transits SMALLINT,
        rvs_spec_sig_to_noise REAL,
        phot_variable_flag TEXT,
        l DOUBLE PRECISION,
        b DOUBLE PRECISION,
        ecl_lon DOUBLE PRECISION,
        ecl_lat DOUBLE PRECISION,
        in_qso_candidates BOOLEAN,
        in_galaxy_candidates BOOLEAN,
        non_single_star SMALLINT,
        has_xp_continuous BOOLEAN,
        has_xp_sampled BOOLEAN,
        has_rvs BOOLEAN,
        has_epoch_photometry BOOLEAN,
        has_epoch_rv BOOLEAN,
        has_mcmc_gspphot BOOLEAN,
        has_mcmc_msc BOOLEAN,
        in_andromeda_survey BOOLEAN,
        classprob_dsc_combmod_quasar REAL,
        classprob_dsc_combmod_galaxy REAL,
        classprob_dsc_combmod_star REAL,
        teff_gspphot REAL,
        teff_gspphot_lower REAL,
        teff_gspphot_upper REAL,
        logg_gspphot REAL,
        logg_gspphot_lower REAL,
        logg_gspphot_upper REAL,
        mh_gspphot REAL,
        mh_gspphot_lower REAL,
        mh_gspphot_upper REAL,
        distance_gspphot REAL,
        distance_gspphot_lower REAL,
        distance_gspphot_upper REAL,
        azero_gspphot REAL,
        azero_gspphot_lower REAL,
        azero_gspphot_upper REAL,
        ag_gspphot REAL,
        ag_gspphot_lower REAL,
        ag_gspphot_upper REAL,
        ebpminrp_gspphot REAL,
        ebpminrp_gspphot_lower REAL,
        ebpminrp_gspphot_upper REAL,
        libname_gspphot TEXT,
        -- screen space variables for rendering
        x DOUBLE PRECISION,
        y DOUBLE PRECISION,
        z DOUBLE PRECISION
);
--ALTER TABLE source SET SCHEMA gaia_dr3;
GRANT ALL ON TABLE source TO gaia;
GRANT ALL ON TABLE source TO public;

CREATE INDEX gaia_source_source_id_idx ON source (source_id);




-- 155 fields

"INSERT INTO gaia_dr3_source ("
    "solution_id, designation, source_id, random_index, ref_epoch, ra, ra_error, dec, dec_error, parallax, "
    "parallax_error, parallax_over_error, pm, pmra, pmra_error, pmdec, pmdec_error, ra_dec_corr, ra_parallax_corr, "
    "ra_pmra_corr, ra_pmdec_corr, dec_parallax_corr, dec_pmra_corr, dec_pmdec_corr, parallax_pmra_corr, "
    "parallax_pmdec_corr, pmra_pmdec_corr, astrometric_n_obs_al, astrometric_n_obs_ac, astrometric_n_good_obs_al, "
    "astrometric_n_bad_obs_al, astrometric_gof_al, astrometric_chi2_al, astrometric_excess_noise, "
    "astrometric_excess_noise_sig, astrometric_params_solved, astrometric_primary_flag, nu_eff_used_in_astrometry, "
    "pseudocolour, pseudocolour_error, ra_pseudocolour_corr, dec_pseudocolour_corr, parallax_pseudocolour_corr, "
    "pmra_pseudocolour_corr, pmdec_pseudocolour_corr, astrometric_matched_transits, visibility_periods_used, "
    "astrometric_sigma5d_max, matched_transits, new_matched_transits, matched_transits_removed, "
    "ipd_gof_harmonic_amplitude, ipd_gof_harmonic_phase, ipd_frac_multi_peak, ipd_frac_odd_win, ruwe, "
    "scan_direction_strength_k1, scan_direction_strength_k2, scan_direction_strength_k3, scan_direction_strength_k4, "
    "scan_direction_mean_k1, scan_direction_mean_k2, scan_direction_mean_k3, scan_direction_mean_k4, "
    "duplicated_source, phot_g_n_obs, phot_g_mean_flux, phot_g_mean_flux_error, phot_g_mean_flux_over_error, "
    "phot_g_mean_mag, phot_bp_n_obs, phot_bp_mean_flux, phot_bp_mean_flux_error, phot_bp_mean_flux_over_error, "
    "phot_bp_mean_mag, phot_rp_n_obs, phot_rp_mean_flux, phot_rp_mean_flux_error, phot_rp_mean_flux_over_error, "
    "phot_rp_mean_mag, phot_bp_rp_excess_factor, phot_bp_n_contaminated_transits, phot_bp_n_blended_transits, "
    "phot_rp_n_contaminated_transits, phot_rp_n_blended_transits, phot_proc_mode, bp_rp, bp_g, g_rp, "
    "radial_velocity, radial_velocity_error, rv_method_used, rv_nb_transits, rv_nb_deblended_transits, "
    "rv_visibility_periods_used, rv_expected_sig_to_noise, rv_renormalised_gof, rv_chisq_pvalue, rv_time_duration, "
    "rv_amplitude_robust, rv_template_teff, rv_template_logg, rv_template_fe_h, rv_atm_param_origin, vbroad, "
    "vbroad_error, vbroad_nb_transits, grvs_mag, grvs_mag_error, grvs_mag_nb_transits, rvs_spec_sig_to_noise, "
    "phot_variable_flag, l, b, ecl_lon, ecl_lat, in_qso_candidates, in_galaxy_candidates, non_single_star, "
    "has_xp_continuous, has_xp_sampled, has_rvs, has_epoch_photometry, has_epoch_rv, has_mcmc_gspphot, "
    "has_mcmc_msc, in_andromeda_survey, classprob_dsc_combmod_quasar, classprob_dsc_combmod_galaxy, "
    "classprob_dsc_combmod_star, teff_gspphot, teff_gspphot_lower, teff_gspphot_upper, logg_gspphot, "
    "logg_gspphot_lower, logg_gspphot_upper, mh_gspphot, mh_gspphot_lower, mh_gspphot_upper, distance_gspphot, "
    "distance_gspphot_lower, distance_gspphot_upper, azero_gspphot, azero_gspphot_lower, azero_gspphot_upper, "
    "ag_gspphot, ag_gspphot_lower, ag_gspphot_upper, ebpminrp_gspphot, ebpminrp_gspphot_lower, ebpminrp_gspphot_upper, "
    "libname_gspphot, x, y, z) "
"VALUES ("
    "$1, $2, $3, $4, $5, $6, $7, $8, $9, $10, "
    "$11, $12, $13, $14, $15, $16, $17, $18, $19, $20, "
    "$21, $22, $23, $24, $25, $26, $27, $28, $29, $30, "
    "$31, $32, $33, $34, $35, $36, $37, $38, $39, $40, "
    "$41, $42, $43, $44, $45, $46, $47, $48, $49, $50, "
    "$51, $52, $53, $54, $55, $56, $57, $58, $59, $60, "
    "$61, $62, $63, $64, $65, $66, $67, $68, $69, $70, "
    "$71, $72, $73, $74, $75, $76, $77, $78, $79, $80, "
    "$81, $82, $83, $84, $85, $86, $87, $88, $89, $90, "
    "$91, $92, $93, $94, $95, $96, $97, $98, $99, $100, "
    "$101, $102, $103, $104, $105, $106, $107, $108, $109, $110, "
    "$111, $112, $113, $114, $115, $116, $117, $118, $119, #120, "
    "$121, $122, $123, $124, $125, $126, $127, $128, $129, $130, "
    "$131, $132, $133, $134, $135, $136, $137, $138, $139, $140, "
    "$141, $142, $143, $144, $145, $146, $147, $148, $149, $150, "
    "$151, $152, $153, $154, $155"
")";

W.exec_prepared("insert_data", star.solution_id, star.designation, star.source_id, star.random_index, star.ref_epoch, star.ra, star.ra_error, star.dec, star.dec_error, star.parallax, star.parallax_error, star.parallax_over_error, star.pm, star.pmra, star.pmra_error, star.pmdec, star.pmdec_error, star.ra_dec_corr, star.ra_parallax_corr, star.ra_pmra_corr, star.ra_pmdec_corr, star.dec_parallax_corr, star.dec_pmra_corr, star.dec_pmdec_corr, star.parallax_pmra_corr, star.parallax_pmdec_corr, star.pmra_pmdec_corr, star.astrometric_n_obs_al, star.astrometric_n_obs_ac, star.astrometric_n_good_obs_al, star.astrometric_n_bad_obs_al, star.astrometric_gof_al, star.astrometric_chi2_al, star.astrometric_excess_noise, star.astrometric_excess_noise_sig, star.astrometric_params_solved, star.astrometric_primary_flag, star.nu_eff_used_in_astrometry, star.pseudocolour, star.pseudocolour_error, star.ra_pseudocolour_corr, star.dec_pseudocolour_corr, star.parallax_pseudocolour_corr, star.pmra_pseudocolour_corr, star.pmdec_pseudocolour_corr, star.astrometric_matched_transits, star.visibility_periods_used, star.astrometric_sigma5d_max, star.matched_transits, star.new_matched_transits, star.matched_transits_removed, star.ipd_gof_harmonic_amplitude, star.ipd_gof_harmonic_phase, star.ipd_frac_multi_peak, star.ipd_frac_odd_win, star.ruwe, star.scan_direction_strength_k1, star.scan_direction_strength_k2, star.scan_direction_strength_k3, star.scan_direction_strength_k4, star.scan_direction_mean_k1, star.scan_direction_mean_k2, star.scan_direction_mean_k3, star.scan_direction_mean_k4, star.duplicated_source, star.phot_g_n_obs, star.phot_g_mean_flux, star.phot_g_mean_flux_error, star.phot_g_mean_flux_over_error, star.phot_g_mean_mag, star.phot_bp_n_obs, star.phot_bp_mean_flux, star.phot_bp_mean_flux_error, star.phot_bp_mean_flux_over_error, star.phot_bp_mean_mag, star.phot_rp_n_obs, star.phot_rp_mean_flux, star.phot_rp_mean_flux_error, star.phot_rp_mean_flux_over_error, star.phot_rp_mean_mag, star.phot_bp_rp_excess_factor, star.phot_bp_n_contaminated_transits, star.phot_bp_n_blended_transits, star.phot_rp_n_contaminated_transits, star.phot_rp_n_blended_transits, star.phot_proc_mode, star.bp_rp, star.bp_g, star.g_rp, star.radial_velocity, star.radial_velocity_error, star.rv_method_used, star.rv_nb_transits, star.rv_nb_deblended_transits, star.rv_visibility_periods_used, star.rv_expected_sig_to_noise, star.rv_renormalised_gof, star.rv_chisq_pvalue, star.rv_time_duration, star.rv_amplitude_robust, star.rv_template_teff, star.rv_template_logg, star.rv_template_fe_h, star.rv_atm_param_origin, star.vbroad, star.vbroad_error, star.vbroad_nb_transits, star.grvs_mag, star.grvs_mag_error, star.grvs_mag_nb_transits, star.rvs_spec_sig_to_noise, star.phot_variable_flag, star.l, star.b, star.ecl_lon, star.ecl_lat, star.in_qso_candidates, star.in_galaxy_candidates, star.non_single_star, star.has_xp_continuous, star.has_xp_sampled, star.has_rvs, star.has_epoch_photometry, star.has_epoch_rv, star.has_mcmc_gspphot, star.has_mcmc_msc, star.in_andromeda_survey, star.classprob_dsc_combmod_quasar, star.classprob_dsc_combmod_galaxy, star.classprob_dsc_combmod_star, star.teff_gspphot, star.teff_gspphot_lower, star.teff_gspphot_upper, star.logg_gspphot, star.logg_gspphot_lower, star.logg_gspphot_upper, star.mh_gspphot, star.mh_gspphot_lower, star.mh_gspphot_upper, star.distance_gspphot, star.distance_gspphot_lower, star.distance_gspphot_upper, star.azero_gspphot, star.azero_gspphot_lower, star.azero_gspphot_upper, star.ag_gspphot, star.ag_gspphot_lower, star.ag_gspphot_upper, star.ebpminrp_gspphot, star.ebpminrp_gspphot_lower, star.ebpminrp_gspphot_upper, star.libname_gspphot, star.x, star.y, star.z);


DROP ViEW db_locks;
CREATE VIEW db_locks AS SELECT
    pg_database.datname AS database_name,
    pg_class.relname AS table_name,
    pg_locks.*,
    pg_stat_activity.query AS blocking_statement
FROM
    pg_locks, pg_class, pg_database, pg_stat_activity
WHERE
    pg_locks.relation = pg_class.oid AND
    pg_locks.database = pg_database.oid AND
    pg_locks.pid = pg_stat_activity.pid
;

CREATE TABLE source_pixel
(
    id BIGSERIAL PRIMARY KEY,
    resolution_id INTEGER,
    samples INTEGER,
    x INTEGER,
    y INTEGER,
    color1 INTEGER,
    color2 INTEGER,
    color3 INTEGER,
    color4 INTEGER,
    color5 INTEGER
) TABLESPACE gaia_dr3_space;

CREATE INDEX gaia_source_pixel_idx ON source_pixel (resolution_id, x, y);
