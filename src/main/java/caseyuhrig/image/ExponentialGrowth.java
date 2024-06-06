package caseyuhrig.image;

public class ExponentialGrowth {

    /**
     * Applies an exponential growth function to the input value and normalizes the output.
     *
     * @param min   The minimum input value.
     * @param max   The maximum input value.
     * @param input The current input value.
     * @return The normalized exponentially scaled output value between 0 and 1.
     */
    public static double exponentialGrowth(final double min, final double max, final double input) {
        if (min == max) {
            throw new IllegalArgumentException("min and max cannot be the same value");
        }
        // Normalize the input to a 0-1 range
        final double normalizedInput = (input - min) / (max - min);
        // Apply exponential function
        final double a = 1; // Scale factor, adjust as needed
        final double k = 2; // Growth rate, adjust as needed
        final double rawOutput = a * Math.exp(k * normalizedInput);
        // Normalize the output to 0-1 range
        final double maxOutput = a * Math.exp(k); // Output when input is max
        return rawOutput / maxOutput;
    }
}
