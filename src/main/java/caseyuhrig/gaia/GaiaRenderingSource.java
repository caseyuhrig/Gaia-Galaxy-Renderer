package caseyuhrig.gaia;

import java.sql.ResultSet;

public class GaiaRenderingSource extends RenderingData {


    public GaiaRenderingSource(final ResultSet rs) {
        try {
            this.source_id = rs.getObject("source_id", Long.class);
            this.ra = rs.getObject("ra", Double.class);
            this.dec = rs.getObject("dec", Double.class);
            this.parallax = rs.getObject("parallax", Double.class);
            this.parallax_error = rs.getObject("parallax_error", Float.class);
            this.l = rs.getObject("l", Double.class);
            this.b = rs.getObject("b", Double.class);

            this.phot_g_mean_mag = rs.getObject("phot_g_mean_mag", Float.class);
            this.phot_g_mean_flux = rs.getObject("phot_g_mean_flux", Double.class);
            this.phot_g_n_obs = rs.getObject("phot_g_n_obs", Short.class);

            this.phot_bp_mean_mag = rs.getObject("phot_bp_mean_mag", Float.class);
            this.phot_bp_mean_flux = rs.getObject("phot_bp_mean_flux", Double.class);
            this.phot_bp_n_obs = rs.getObject("phot_bp_n_obs", Short.class);

            this.phot_rp_mean_mag = rs.getObject("phot_rp_mean_mag", Float.class);
            this.phot_rp_mean_flux = rs.getObject("phot_rp_mean_flux", Double.class);
            this.phot_rp_n_obs = rs.getObject("phot_rp_n_obs", Short.class);

            this.bp_rp = rs.getObject("bp_rp", Float.class);
            this.bp_g = rs.getObject("bp_g", Float.class);
            this.g_rp = rs.getObject("g_rp", Float.class);

            this.teff_gspphot = rs.getObject("teff_gspphot", Float.class);

            this.astrometric_excess_noise = rs.getObject("astrometric_excess_noise", Float.class);
        } catch (final Throwable e) {
            throw new RuntimeException(e.getLocalizedMessage(), e);
        }
    }

    public static String getFieldNames() {
        return "source_id, ra, dec, parallax, parallax_error, l, b, phot_g_mean_mag, phot_g_mean_flux, phot_g_n_obs, phot_bp_mean_mag, phot_bp_mean_flux, phot_bp_n_obs, phot_rp_mean_mag, phot_rp_mean_flux, phot_rp_n_obs, bp_rp, bp_g, g_rp, teff_gspphot, astrometric_excess_noise";
    }
}
