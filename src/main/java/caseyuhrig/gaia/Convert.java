package caseyuhrig.gaia;

public class Convert {

    public static Long toLong(final String value) {
        try {
            return Long.parseLong(value);
        } catch (final NumberFormatException e) {
            throw new NumberFormatException("Failed to convert the text '" + value + "' to Long.");
        }
    }

    public static Object convert(final String value, final Class<?> type) {
        //System.out.println("type = " + type.getName());
        if (value == null || "null".equals(value) || value.isEmpty())
            return null;
        if (type == Long.TYPE || type == Long.class) {
            try {
                return toLong(value);
            } catch (final NumberFormatException e) {
                throw new NumberFormatException("Failed to convert the text '" + value + "' to Long.");
            }
        } else if (type == Double.TYPE || type == Double.class) {
            return Double.parseDouble(value);
        } else if (type == Float.TYPE || type == Float.class) {

            return Float.parseFloat(value);
        } else if (type == Short.TYPE || type == Short.class) {
            return Short.parseShort(value);
        } else if (type == Byte.TYPE || type == Byte.class) {
            return Byte.parseByte(value);
        } else if (type == Boolean.TYPE || type == Boolean.class) {
            final var result = value.replaceAll("\"", "").toLowerCase().trim();
            if ("no".equals(result) || "false".equals(result) || "f".equals(result) || "0".equals(result)) {
                return false;
            } else if ("yes".equals(result) || "true".equals(result) || "t".equals(result) || "1".equals(result)) {
                return true;
            }
            throw new RuntimeException("Failed to convert the text '" + value + "' to Boolean.");
        } else if (type == Integer.TYPE || type == Integer.class) {
            return Integer.parseInt(value);
        } else if (type == String.class) {
            return value;
        }
        throw new RuntimeException("No conversion found for type " + type.getName());
    }
}
