package caseyuhrig.image;

public class GalacticCoordinates {

    // Convert degrees to radians
    private static double degreesToRadians(final double degrees) {
        return degrees * Math.PI / 180.0;
    }

    // Calculate the angle between the Sun and a star
    public static double calculateAngle(final double l, final double b) {
        // Convert l and b from degrees to radians
        final double lRad = degreesToRadians(l);
        final double bRad = degreesToRadians(b);

        // Calculate cos(theta)
        final double cosTheta = Math.cos(bRad) * Math.cos(lRad);

        // Calculate theta (in radians) and convert to degrees
        final double thetaRad = Math.acos(cosTheta);
        final double thetaDeg = Math.toDegrees(thetaRad);

        return thetaDeg;
    }

}
