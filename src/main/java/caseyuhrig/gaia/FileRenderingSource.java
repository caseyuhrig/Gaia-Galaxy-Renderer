package caseyuhrig.gaia;

import java.io.DataInputStream;

public class FileRenderingSource extends RenderingData {

    public FileRenderingSource(final DataInputStream input) {
        try {
            this.l = input.readDouble();
            this.b = input.readDouble();
            this.phot_rp_mean_flux = input.readDouble();
            this.phot_g_mean_flux = input.readDouble();
            this.phot_bp_mean_flux = input.readDouble();
            this.phot_rp_mean_mag = (float) input.readDouble();
            this.phot_g_mean_mag = (float) input.readDouble();
            this.phot_bp_mean_mag = (float) input.readDouble();
            this.bp_rp = (float) input.readDouble();
            this.g_rp = (float) input.readDouble();
            this.bp_g = (float) input.readDouble();
            this.teff_gspphot = (float) input.readDouble();
            this.parallax = input.readDouble();


            //this.source_id = rs.getObject("source_id", Long.class);
            //this.ra = rs.getObject("ra", Double.class);
            //this.dec = rs.getObject("dec", Double.class);

            //this.parallax_error = rs.getObject("parallax_error", Float.class);

            //this.astrometric_excess_noise = rs.getObject("astrometric_excess_noise", Float.class);
        } catch (final Throwable e) {
            throw new RuntimeException(e.getLocalizedMessage(), e);
        }
    }

    public static String getFieldNames() {
        return "source_id, ra, dec, parallax, parallax_error, l, b, phot_g_mean_mag, phot_g_mean_flux, phot_g_n_obs, phot_bp_mean_mag, phot_bp_mean_flux, phot_bp_n_obs, phot_rp_mean_mag, phot_rp_mean_flux, phot_rp_n_obs, bp_rp, bp_g, g_rp, teff_gspphot, astrometric_excess_noise";
    }
}
