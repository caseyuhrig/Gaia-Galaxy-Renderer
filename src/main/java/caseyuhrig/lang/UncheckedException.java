package caseyuhrig.lang;

public class UncheckedException extends RuntimeException {

    public UncheckedException(final Throwable cause) {
        super(cause);
    }

    public UncheckedException(final String message) {
        super(message);
    }

    public UncheckedException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public UncheckedException(final String message, final Object... args) {
        super(String.format(message, excludeCauseFromArgs(args)), extractCauseFromArgs(args));
    }


    private static Object[] excludeCauseFromArgs(final Object... args) {
        if (args[args.length - 1] instanceof Throwable) {
            final var newArgs = new Object[args.length - 1];
            System.arraycopy(args, 0, newArgs, 0, args.length - 1);
            return newArgs;
        }
        return args;
    }

    private static Throwable extractCauseFromArgs(final Object... args) {
        if (args[args.length - 1] instanceof Throwable) {
            return (Throwable) args[args.length - 1];
        }
        return null;
    }
}
