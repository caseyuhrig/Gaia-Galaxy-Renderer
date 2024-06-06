package caseyuhrig.image;

public class SunInfluence {
    public static double[] calculateSunlightInfluence(final double[] starColor, final double[] sunColor, final double angle) {
        // Assuming angle is in degrees, convert to radians
        final double angleRad = Math.toRadians(angle);

        // Calculate the influence of the Sun's light
        final double influenceFactor = Math.max(0, Math.cos(angleRad)); // Simple cosine law for illumination

        // Combine the star's intrinsic color with the Sun's influence
        final double r = starColor[0] + sunColor[0] * influenceFactor;
        final double g = starColor[1] + sunColor[1] * influenceFactor;
        final double b = starColor[2] + sunColor[2] * influenceFactor;

        return new double[]{r, g, b};
    }
}
