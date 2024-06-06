package caseyuhrig.gaia;


/**
 * Table of Gaia DR3 passband transmissivity data.
 * Taken from https://bsrender.io/
 */
public class GaiaPassbands {

    public static double getGaiaTransmissivityG(final int wavelength) {

        // 320-1100nm is the range of the Gaia DR3 passband data file https://www.cosmos.esa.int/web/gaia/dr3-passbands
        if (wavelength < 320) {
            return (0.0);
        } else if (wavelength > 1100) {
            return (0.0);
        }

        final double transmissivity = GaiaTransmissivity.Gaia_DR3_transmissivity_G[wavelength - 320];

        return (transmissivity);
    }

    public static double getGaiaTransmissivityBp(final int wavelength) {

        // 320-1100nm is the range of the Gaia DR3 passband data file https://www.cosmos.esa.int/web/gaia/dr3-passbands
        if (wavelength < 320) {
            return (0.0);
        } else if (wavelength > 1100) {
            return (0.0);
        }

        final double transmissivity = GaiaTransmissivity.Gaia_DR3_transmissivity_bp[wavelength - 320];

        return (transmissivity);
    }

    public static double getGaiaTransmissivityRp(final int wavelength) {

        // 320-1100nm is the range of the Gaia DR3 passband data file https://www.cosmos.esa.int/web/gaia/dr3-passbands
        if (wavelength < 320) {
            return (0.0);
        } else if (wavelength > 1100) {
            return (0.0);
        }

        final double transmissivity = GaiaTransmissivity.Gaia_DR3_transmissivity_rp[wavelength - 320];

        return (transmissivity);
    }
}
