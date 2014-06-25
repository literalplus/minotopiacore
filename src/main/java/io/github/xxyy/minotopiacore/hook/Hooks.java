package io.github.xxyy.minotopiacore.hook;

import java.lang.reflect.InvocationTargetException;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Helps managing hooks.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 9.6.14
 */
public final class Hooks {
    public static final String HOOK_IMPL_PACKAGE_NAME = Hooks.class.getPackage().getName() + ".impl";

    private Hooks() {
    }

    public static <T> T tryHook(HookWrapper wrapper) {
        return tryHook(String.format("%s.%sImpl", HOOK_IMPL_PACKAGE_NAME, wrapper.getClass().getSimpleName()), wrapper);
    }

    @SuppressWarnings("unchecked")
    public static <T> T tryHook(String hookClassName, HookWrapper wrapper) {
        T hookInstance = null;

        try {
            hookInstance = tryHook((Class<T>) Class.forName(hookClassName), wrapper);
        } catch (ClassNotFoundException | NoClassDefFoundError | NoSuchMethodError e) {
            e.printStackTrace();
            wrapper.getPlugin().getLogger().warning("Plugin hook failed: "+hookClassName);
        }

        return hookInstance;
    }

    public static <T> T tryHook(Class<T> hookClass, HookWrapper wrapper) {
        T hookInstance = null;

        try {
            hookInstance = hookClass.getConstructor(wrapper.getClass()).newInstance(wrapper);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            wrapper.getPlugin().getLogger().warning("Could not initialise plugin hook: " + hookClass.getName());
        }

        return hookInstance;
    }

    public static class Unsafe {
        public static <T, R> R safeCall(Function<T, R> func, T param, R def, Runnable onError) {
            try {
                return func.apply(param);
            } catch(Exception | Error e) {
                if(onError != null) {
                    onError.run();
                }
                return def;
            }
        }

        public static <T, U, R> R safeCall(BiFunction<T, U, R> func, T param, U param2, R def, Runnable onError) {
            try {
                return func.apply(param, param2);
            } catch(Exception | Error e) {
                if(onError != null) {
                    onError.run();
                }
                return def;
            }
        }

        public static <R> R safeCall(Supplier<R> func, R def, Runnable onError) {
            try {
                return func.get();
            } catch(Exception | Error e) {
                if(onError != null) {
                    onError.run();
                }
                return def;
            }
        }

        public static void safeCall(Runnable func, Runnable onError) {
            try {
                func.run();
            } catch(Exception | Error e) {
                if(onError != null) {
                    onError.run();
                }
            }
        }
    }
}
