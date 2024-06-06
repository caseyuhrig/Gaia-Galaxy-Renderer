package caseyuhrig.image;

import caseyuhrig.gaia.RenderingData;

public class GalaxyRenderer {
    public static double[] calculateObservedColor(final RenderingData data) {

        // Calculate distance in light years
        //final double distance = parallaxDistance(data.parallax);

        //final double sunAngle = GalacticCoordinates.calculateAngle(data.l, data.b);

        // print the flux values on one line
        //System.out.println("rpFlux: " + rpFlux + " gFlux: " + gFlux + " bpFlux: " + bpFlux);

        // Step -1: Normalize the flux values
        //final double[] normFlux = FluxToRGB.normalizeFlux(rpFlux, gFlux, bpFlux);
        //final double[] fluxColor = new double[]{rpFlux, gFlux, bpFlux};
        //final double[] fluxColor = new double[]{rpFlux, bpFlux, gFlux};

        //final double[] normColor = FluxToRGB.normalize(normFlux);

        // Step 0: Calculate the observed color of a star using the flux values
        //final double[] fluxColor = FluxToRGB.fluxToRGB(normFlux[0], normFlux[1], normFlux[2]);

        //final double bp_rp = normalize(data.bp_rp, GaiaConst.min_bp_rp, GaiaConst.max_bp_rp);
        //final double g_rp = normalize(data.g_rp, GaiaConst.min_g_rp, GaiaConst.max_g_rp);
        //final double bp_g = normalize(data.bp_g, GaiaConst.min_bp_g, GaiaConst.max_bp_g);
        //final double[] bandColor = new double[]{bp_g, g_rp, bp_rp};


        // Step 1: Calculate intrinsic color
        final double[] temperatureColor = PlanckianLocus.colorTemperatureToRGB(data.teff_gspphot);

        //final double[] blendedColor = ColorBlender.blendColors(fluxColor, temperatureColor, 0.5);

        //final double[] blendedColor = FluxToRGB.normalize(new double[]{fluxColor[0] * temperatureColor[0], fluxColor[1] * temperatureColor[1], fluxColor[2] * temperatureColor[2]});


        // Step 2: Apply dust extinction
        //final double[] colorAfterExtinction = DustExtinction.applyDustExtinction(blendedColor, av);

        // Step 3: Apply Rayleigh scattering
        //final double[] colorAfterRayleigh = LightScattering.applyRayleighScattering(colorAfterExtinction, distance);

        // Step 4: Apply Mie scattering
        //final double[] colorAfterMie = LightScattering.applyMieScattering(colorAfterRayleigh, distance);

        //colorAfterMie[0] = colorAfterMie[0] * 2.5;
        //colorAfterMie[1] = colorAfterMie[1] * 2.5;
        //colorAfterMie[2] = colorAfterMie[2] * 2.5;

        // Step 5: Apply Sun's influence
        //final double[] colorAfterSunInfluence = SunInfluence.calculateSunlightInfluence(colorAfterMie, sunColor, sunAngle);

        // Step 6: Adjust saturation
        //final double[] colorAfterSaturation = ColorAdjustment.adjustSaturation(colorAfterMie, saturationFactor);

        // Step 7: Apply tone mapping with exposure
        //final double[] colorAfterToneMapping = ToneMapping.applyToneMapping(colorAfterSaturation, exposure);


        return temperatureColor;
    }

    public static double[][] WEIGHTS1 = {
            {0.4124, 0.3576, 0.1805},
            {0.2126, 0.4152, 0.0722},
            {0.0193, 0.1192, 0.9505}
    };

    public static double[] applyColorWeights(final double[] rgb, final double[][] w) {
        // Adjusted weights for direct conversion to RGB
        //final double r = 0.5 * normRP + 0.4 * normG + 0.1 * normBP;
        //final double g = 0.3 * normRP + 0.5 * normG + 0.2 * normBP;
        //final double b = 0.2 * normRP + 0.2 * normG + 0.6 * normBP;

        final double r = w[0][0] * rgb[0] + w[1][0] * rgb[1] + w[2][0] * rgb[2];
        final double g = w[0][1] * rgb[0] + w[1][1] * rgb[1] + w[2][1] * rgb[2];
        final double b = w[0][2] * rgb[0] + w[1][2] * rgb[1] + w[2][2] * rgb[2];

        return new double[]{clamp(r, 0, 1), clamp(g, 0, 1), clamp(b, 0, 1)};
    }

    private static double clamp(final double value, final double min, final double max) {
        return Math.max(min, Math.min(max, value));
    }

}
