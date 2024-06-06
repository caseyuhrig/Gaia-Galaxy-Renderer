package caseyuhrig.image;

public class PerlinNoise {
    private static final int[] permutation = {151, 160, 137, 91, 90, 15}; // Permutation array

    public static double perlin(double x, double y, double z) {
        final int X = (int) Math.floor(x) & 255;
        final int Y = (int) Math.floor(y) & 255;
        final int Z = (int) Math.floor(z) & 255;

        x -= Math.floor(x);
        y -= Math.floor(y);
        z -= Math.floor(z);

        final double u = fade(x);
        final double v = fade(y);
        final double w = fade(z);

        final int A = permutation[X] + Y;
        final int AA = permutation[A] + Z;
        final int AB = permutation[A + 1] + Z;
        final int B = permutation[X + 1] + Y;
        final int BA = permutation[B] + Z;
        final int BB = permutation[B + 1] + Z;

        return lerp(w, lerp(v, lerp(u, grad(permutation[AA], x, y, z),
                                grad(permutation[BA], x - 1, y, z)),
                        lerp(u, grad(permutation[AB], x, y - 1, z),
                                grad(permutation[BB], x - 1, y - 1, z))),
                lerp(v, lerp(u, grad(permutation[AA + 1], x, y, z - 1),
                                grad(permutation[BA + 1], x - 1, y, z - 1)),
                        lerp(u, grad(permutation[AB + 1], x, y - 1, z - 1),
                                grad(permutation[BB + 1], x - 1, y - 1, z - 1))));
    }

    private static double fade(final double t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    private static double lerp(final double t, final double a, final double b) {
        return a + t * (b - a);
    }

    private static double grad(final int hash, final double x, final double y, final double z) {
        final int h = hash & 15;
        final double u = h < 8 ? x : y;
        final double v = h < 4 ? y : h == 12 || h == 14 ? x : z;
        return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
    }

    public static void main(final String[] args) {
        final double x = 0.5;
        final double y = 0.5;
        final double z = 0.5;

        final double noiseValue = perlin(x, y, z);
        System.out.println("Perlin Noise Value: " + noiseValue);
    }
}
