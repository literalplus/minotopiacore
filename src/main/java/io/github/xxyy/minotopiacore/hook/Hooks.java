package io.github.xxyy.minotopiacore.hook;

import java.lang.reflect.InvocationTargetException;

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
}
