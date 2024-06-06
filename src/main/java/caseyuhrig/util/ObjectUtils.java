package caseyuhrig.util;

import caseyuhrig.lang.UncheckedException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;
import java.util.function.Supplier;


public class ObjectUtils {

    public static <T> T create(final Class<T> clazz, final Object... args) {
        try {
            // Get parameter types from the arguments
            final Class<?>[] parameterTypes = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                parameterTypes[i] = getPrimitiveClass(args[i].getClass());
            }

            // Find the constructor with matching parameter types
            final Constructor<T> constructor = clazz.getConstructor(parameterTypes);

            // Create a new instance using the constructor
            return constructor.newInstance(args);
        } catch (final Throwable throwable) {
            throw new RuntimeException(String.format("Failed to create a new instance of class: %s", clazz.getName()), throwable);
        }
    }

    private static Class<?> getPrimitiveClass(final Class<?> wrapperClass) {
        if (wrapperClass == Integer.class) {
            return int.class;
        } else if (wrapperClass == Double.class) {
            return double.class;
        } else if (wrapperClass == Long.class) {
            return long.class;
        } else if (wrapperClass == Boolean.class) {
            return boolean.class;
        } else if (wrapperClass == Character.class) {
            return char.class;
        } else if (wrapperClass == Float.class) {
            return float.class;
        } else if (wrapperClass == Byte.class) {
            return byte.class;
        } else if (wrapperClass == Short.class) {
            return short.class;
        } else {
            return wrapperClass; // return the same class if it's not a wrapper type
        }
    }


    public static <T> T lazyInit(T object, final Supplier<T> creator) {
        if (object == null) {
            object = creator.get();
        }
        return object;
    }


    public static <T> T lazyInit(T object, final Supplier<T> creator, final Consumer<T> initializer) {
        if (object == null) {
            object = creator.get();
            initializer.accept(object);
        }
        return object;
    }


    public static <T> T lazyInit(final Class<T> clazz, final T object) {
        if (object == null) {
            try {
                return clazz.getDeclaredConstructor().newInstance();
            } catch (final InstantiationException | IllegalAccessException | InvocationTargetException |
                           NoSuchMethodException e) {
                throw new UncheckedException("Failed to create a new instance of class: " + clazz.getName(), e);
            }
        } else {
            return object;
        }
    }
}
