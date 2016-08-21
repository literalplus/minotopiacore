/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.hook;

import li.l1t.mtc.hook.impl.Hook;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;

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

    public static <T extends Hook> T tryHook(HookWrapper wrapper) {
        return tryHook(String.format("%s.%sImpl", HOOK_IMPL_PACKAGE_NAME, wrapper.getClass().getSimpleName()), wrapper);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Hook> T tryHook(String hookClassName, HookWrapper wrapper) {
        T hookInstance = null;

        try {
            hookInstance = tryHook((Class<T>) Class.forName(hookClassName), wrapper);
        } catch (ClassNotFoundException | NoClassDefFoundError | NoSuchMethodError e) {
            e.printStackTrace();
            wrapper.getPlugin().getLogger().warning("Plugin hook failed: " + hookClassName);
        }

        return hookInstance;
    }

    public static <T extends Hook> T tryHook(Class<T> hookClass, HookWrapper wrapper) {
        T hookInstance = null;

        try {
            hookInstance = hookClass.getConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            wrapper.getPlugin().getLogger().warning("Could not initialise plugin hook: " + hookClass.getName());
        }

        if (hookInstance != null && hookInstance.canHook(wrapper)) {
            try {
                hookInstance.hook(wrapper);
            } catch (Throwable e) {
                wrapper.getPlugin().getLogger().log(Level.FINE, null, e);
                wrapper.getPlugin().getLogger().warning("Failed to hook " + wrapper.getClass().getSimpleName() + ": " + e.getMessage());
            }
        } else {
            wrapper.getPlugin().getLogger().warning("Failed to hook " + wrapper.getClass().getSimpleName() + " - Please check that the plugin you want to hook into is enabled!");
        }

        return hookInstance;
    }

    public static <T> T setupProvider(Class<T> providerClass, Plugin plugin) {
        T provider = null;

        try {
            RegisteredServiceProvider<T> rsp = plugin.getServer().getServicesManager().getRegistration(providerClass);
            if (rsp == null) {
                plugin.getLogger().info("No " + providerClass.getSimpleName() + " provider found to hook into!");
                return null;
            }
            provider = rsp.getProvider();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (provider == null) {
            plugin.getLogger().warning("Failed to hook provider " + providerClass.getName() + "!");
        }

        return provider;
    }

    public static boolean isPluginLoaded(HookWrapper wrapper, String pluginName) {
        if (wrapper.getPlugin().getServer().getPluginManager().getPlugin(pluginName) == null) {
            wrapper.getPlugin().getLogger().info("Plugin " + pluginName + " is required for " + wrapper.getClass().getSimpleName() + ", but couldn't be found!");
            return false;
        }
        return true;
    }

    @Deprecated //This is um..unnecessarily complicating stuff
    public static class Unsafe {
        public static <T, R> R safeCall(Function<T, R> func, T param, R def, Runnable onError) {
            try {
                return func.apply(param);
            } catch (Exception | Error e) {
                if (onError != null) {
                    onError.run();
                }
                return def;
            }
        }

        public static <T, U, R> R safeCall(BiFunction<T, U, R> func, T param, U param2, R def, Runnable onError) {
            try {
                return func.apply(param, param2);
            } catch (Exception | Error e) {
                if (onError != null) {
                    onError.run();
                }
                return def;
            }
        }

        public static <R> R safeCall(Supplier<R> func, R def, Runnable onError) {
            try {
                return func.get();
            } catch (Exception | Error e) {
                if (onError != null) {
                    onError.run();
                }
                return def;
            }
        }

        public static void safeCall(Runnable func, Runnable onError) {
            try {
                func.run();
            } catch (Exception | Error e) {
                if (onError != null) {
                    onError.run();
                }
            }
        }
    }
}
