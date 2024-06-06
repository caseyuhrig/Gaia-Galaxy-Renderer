package caseyuhrig.gaia;

public class FastColor {

    public int argb;

    public float red;
    public float green;
    public float blue;
    public float alpha;

    public FastColor(final int r, final int g, final int b, final int a) {
        argb = (a << 24) | (r << 16) | (g << 8) | b;
        red = r;
        green = g;
        blue = b;
        alpha = a;
    }

    public FastColor(final int r, final int g, final int b) {
        this(r, g, b, 255);
    }

    public void increment() {
        if (red < 255) {
            red += 0.025f;
            green = blue = red;
            argb = (argb & 0xFF00FFFF) | ((int) red << 16);
            argb = (argb & 0xFFFF00FF) | ((int) green << 8);
            argb = (argb & 0xFFFFFF00) | ((int) blue);
        } else if (red >= 255) {
            green -= 0.025f;
            argb = (argb & 0xFFFF00FF) | ((int) green << 8);
        }
    }
}
