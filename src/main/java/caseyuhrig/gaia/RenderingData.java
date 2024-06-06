package caseyuhrig.gaia;

public class RenderingData {

    public Long source_id;
    public Double ra;
    public Double dec;
    public Double parallax;
    public Float parallax_error; // Standard error of parallax
    public Double l; // Galactic longitude
    public Double b; // Galactic latitude

    public Float phot_g_mean_mag; // G-band mean magnitude
    public Double phot_g_mean_flux; // G-band mean flux
    public Short phot_g_n_obs; // Number of observations contributing to G photometry

    public Float phot_bp_mean_mag; // Integrated BP mean magnitude
    public Double phot_bp_mean_flux; // Integrated BP mean flux
    public Short phot_bp_n_obs; // Number of observations contributing to BP photometry

    public Float phot_rp_mean_mag; // Integrated RP mean magnitude
    public Double phot_rp_mean_flux; // Integrated RP mean flux
    public Short phot_rp_n_obs; // Number of observations contributing to RP photometry

    public Float bp_rp; // BP - RP colour
    public Float bp_g; // BP - G colour
    public Float g_rp; // G - RP colour

    public Float teff_gspphot; // Effective temperature from GSP-Phot

    public Float astrometric_excess_noise; // Astrometric excess noise

}
