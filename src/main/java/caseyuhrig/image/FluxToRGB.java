package caseyuhrig.image;

public class FluxToRGB {

    // Create random weights for XYZ conversion using a random function
    static final double[] xw = new double[]{Math.random(), Math.random(), Math.random()};
    static final double[] yw = new double[]{Math.random(), Math.random(), Math.random()};
    static final double[] zw = new double[]{Math.random(), Math.random(), Math.random()};

    static final double[] xb = new double[]{Math.random() * 0.5, Math.random() * 0.5, Math.random() * 0.5};
    static final double[] yb = new double[]{Math.random() * 0.5, Math.random() * 0.5, Math.random() * 0.5};
    static final double[] zb = new double[]{Math.random() * 0.5, Math.random() * 0.5, Math.random() * 0.5};

    static {
        System.out.println("static final double[] xw = new double[]{" + xw[0] + ", " + xw[1] + ", " + xw[2] + "};");
        System.out.println("static final double[] yw = new double[]{" + yw[0] + ", " + yw[1] + ", " + yw[2] + "};");
        System.out.println("static final double[] zw = new double[]{" + zw[0] + ", " + zw[1] + ", " + zw[2] + "};");

        System.out.println("static final double[] xb = new double[]{" + xb[0] + ", " + xb[1] + ", " + xb[2] + "};");
        System.out.println("static final double[] yb = new double[]{" + yb[0] + ", " + yb[1] + ", " + yb[2] + "};");
        System.out.println("static final double[] zb = new double[]{" + zb[0] + ", " + zb[1] + ", " + zb[2] + "};");
    }

    /*
      Saved output:
        (teal/aqua)
        xw: 0.40396971087812616, 0.3054928227061068, 0.455433848326097
        yw: 0.7530144889134005, 0.3254022916890389, 0.2233389385019796
        zw: 0.14765645510701186, 0.8338424359882407, 0.7245101131691136
        (red/brown)
        xw: 0.11023796231252059, 0.7608675485477389, 0.9604096888547703
        yw: 0.4897321867982779, 0.4141211545334982, 0.1744100742715884
        zw: 0.47235123184245076, 0.192858119035066, 0.2760937648803864

        (yellow)
        xw: 0.9873951867487072, 0.9622122573601868, 0.6966162792969987
        yw: 0.14465232678704898, 0.8433342910862425, 0.6785888157759029
        zw: 0.06558241359806449, 0.3015186596088817, 0.6389036643999357
        xb: 0.16698365647990704, 0.2977687327050288, 0.3151228137152898
        yb: 0.4596276333153247, 0.36382147276651516, 0.45815092412028324
        zb: 0.20528263706688887, 0.12960176798712963, 0.029588779165178436

        (gray)
        xw: 0.7836666668746488, 0.07839742916260706, 0.07863801832801531
        yw: 0.8073230857577881, 0.8584212565221299, 0.08596449488635605
        zw: 0.6304153698530154, 0.9089598727476174, 0.8596943245950825
        xb: 0.3664121598924438, 0.4797719332800154, 0.14692809215771802
        yb: 0.4541358150433325, 0.2688958363017984, 0.25956351023035745
        zb: 0.34041833910728053, 0.08699234037039044, 0.14739281306154306

        xw: 0.8109048114163374, 0.875754219065077, 0.8879238906636262
        yw: 0.2628647104903996, 0.8277734750969149, 0.8385012709184785
        zw: 0.37794878164267076, 0.09389499205783658, 0.3880396764937857

        static final double[] xw = new double[]{0.3757766116997755, 0.9790948176451837, 0.9224631469013722};
        static final double[] yw = new double[]{0.6649727334239177, 0.4865306470438532, 0.8754803489594466};
        static final double[] zw = new double[]{0.6721557099118608, 0.9296447166578884, 0.037533282666888046};

        static final double[] xw = new double[]{0.6771741721736679, 0.8942743072484141, 0.889090771129048};
static final double[] yw = new double[]{0.5102598948739894, 0.3192839734363311, 0.23570028069577176};
static final double[] zw = new double[]{0.6354244011640523, 0.4531625749935855, 0.7945660451829379};

 static final double[][] weights = {
        {0.4124, 0.3576, 0.1805}, // Weights for X
        {0.2126, 0.7152, 0.0722}, // Weights for Y
        {0.0193, 0.1192, 0.9505}  // Weights for Z
    };

static final double[] xw = new double[]{0.8956089676904919, 0.9577296405525212, 0.16467512868928702};
static final double[] yw = new double[]{0.3601584588481066, 0.38859682888516756, 0.9048426003952099};
static final double[] zw = new double[]{0.7633511781200318, 0.07188457306446006, 0.9461656039166897};
     */

    public static double[] fluxToRGB(final double normRP, final double normG, final double normBP) {
        //final double[] xyz = fluxToXYZ(normRP, normG, normBP);
        final double[] xyz = random1FluxToXYZ(normRP, normG, normBP);
        final double[] rgb = xyzToRGB(xyz);
        return new double[]{clamp(rgb[0], 0, 1), clamp(rgb[1], 0, 1), clamp(rgb[2], 0, 1)};
    }

    public static double[] normalize(final double[] values) {
        final double[] result = new double[values.length];
        double total = 0;
        for (final double value : values) {
            total += value;
        }
        for (int i = 0; i < values.length; i++) {
            result[i] = values[i] / total;
        }
        return result;
    }

    public static double[] fluxDirectToRGB(final double normG, final double normBP, final double normRP) {
        // Adjusted weights for direct conversion to RGB
        final double r = 0.5 * normRP + 0.4 * normG + 0.1 * normBP;
        final double g = 0.3 * normRP + 0.5 * normG + 0.2 * normBP;
        final double b = 0.2 * normRP + 0.2 * normG + 0.6 * normBP;

        return new double[]{clamp(r, 0, 1), clamp(g, 0, 1), clamp(b, 0, 1)};
    }

    // Convert flux to CIE XYZ
    private static double[] fluxToXYZ(final double normRP, final double normG, final double normBP) {
        // Adjusted weights to achieve desired color balance
        final double x = 0.4124 * normRP + 0.3576 * normG + 0.1805 * normBP;
        final double y = 0.2126 * normRP + 0.7152 * normG + 0.0722 * normBP;
        final double z = 0.0193 * normRP + 0.1192 * normG + 0.9505 * normBP;
        //final double x = 0.49 * normRP + 0.31 * normG + 0.20 * normBP;
        //final double y = 0.17697 * normRP + 0.61240 * normG + 0.01063 * normBP;
        //final double z = 0.1 * normRP + 0.1 * normG + 0.99 * normBP;

        return new double[]{x, y, z};
    }


    private static double[] random1FluxToXYZ(final double normRP, final double normG, final double normBP) {
        // Adjusted weights to achieve desired color balance
        final double x = xw[0] * normRP + xw[1] * normG + xw[2] * normBP;
        final double y = yw[0] * normRP + yw[1] * normG + yw[2] * normBP;
        final double z = zw[0] * normRP + zw[1] * normG + zw[2] * normBP;

        return new double[]{x, y, z};
    }


    private static double[] random2FluxToXYZ(final double normRP, final double normG, final double normBP) {
        // Adjusted weights to achieve desired color balance
        final double x = (xw[0] * normRP + xb[0]) + (xw[1] * normG + xb[1]) + (xw[2] * normBP + xb[2]);
        final double y = (yw[0] * normRP + yb[0]) + (yw[1] * normG + yb[1]) + (yw[2] * normBP + yb[2]);
        final double z = (zw[0] * normRP + zb[0]) + (zw[1] * normG + zb[1]) + (zw[2] * normBP + zb[2]);

        return new double[]{x, y, z};
    }

    // Convert XYZ to RGB
    private static double[] xyzToRGB(final double[] xyz) {
        // Convert XYZ to linear sRGB
        final double rLinear = 3.2406 * xyz[0] - 1.5372 * xyz[1] - 0.4986 * xyz[2];
        final double gLinear = -0.9689 * xyz[0] + 1.8758 * xyz[1] + 0.0415 * xyz[2];
        final double bLinear = 0.0557 * xyz[0] - 0.2040 * xyz[1] + 1.0570 * xyz[2];

        // Apply gamma correction to convert linear sRGB to sRGB
        final double r = gammaCorrect(rLinear);
        final double g = gammaCorrect(gLinear);
        final double b = gammaCorrect(bLinear);

        return new double[]{r, g, b};
    }

    // Normalize flux values
    public static double[] normalizeFlux(final double rpFlux, final double gFlux, final double bpFlux) {
        final double totalFlux = gFlux + bpFlux + rpFlux;
        return new double[]{rpFlux / totalFlux, gFlux / totalFlux, bpFlux / totalFlux};
    }

    private static double gammaCorrect(final double value) {
        return value <= 0.0031308 ? 12.92 * value : 1.055 * Math.pow(value, 1.0 / 2.4) - 0.055;
    }

    private static double clamp(final double value, final double min, final double max) {
        return Math.max(min, Math.min(max, value));
    }
}
