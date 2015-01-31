/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module;

import com.google.common.collect.ImmutableList;

import io.github.xxyy.mtc.MTC;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Abstract base class for MTC modules. Note when implementing that the Reflection-based loading facility expects your
 * class to have a constructor with no arguments. If you don't provide such, your module won't be able to be loaded
 * automatically.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 22.8.14
 */
public abstract class MTCModuleAdapter implements MTCModule {
    private static Map<String, MTCModuleAdapter> instances = new HashMap<>();
    private final String name;
    protected MTC plugin;
    protected boolean enabledByDefault = true;

    protected MTCModuleAdapter(String name) {
        this.name = name;
        instances.put(name, this);
    }

    @Override
    public void enable(MTC plugin) {
        this.plugin = plugin;
    }

    @Override
    public void disable(MTC plugin) {

    }

    @Override
    public void clearCache(boolean forced, MTC plugin) {

    }

    @Override
    public void reload(MTC plugin) {

    }

    @Override
    public boolean isEnabled(MTC plugin) {
        plugin.getConfig().addDefault("enable." + name, enabledByDefault);
        return plugin.getConfig().getBoolean("enable." + name);
    }

    @Override
    public String getName() {
        return name;
    }

    public MTC getPlugin() {
        return plugin;
    }

    public static MTCModuleAdapter getInstance(String name) {
        return instances.get(name);
    }

    public static Collection<MTCModule> getInstances() {
        return ImmutableList.copyOf(instances.values());
    }

    public static void forEach(Consumer<MTCModule> consumer) {
        instances.values().forEach(consumer);
    }

    /**
     * Attempts to load MTC module from a list of classes. If any of the classes do not implement {@link io.github.xxyy.mtc.module.MTCModule},
     * they will not be loaded and an error message will be logged to {@code plugin}'s logger.
     * @param plugin the plugin requesting this load operation
     * @param moduleClasses the classes to be loaded
     * @return a list of all created instances
     * @see #load(io.github.xxyy.mtc.MTC, Class)
     */
    public static List<MTCModule> load(MTC plugin, Class... moduleClasses) {
        //noinspection unchecked
        return Stream.of(moduleClasses)
                .map((c) -> load(plugin, c)) //This method checks if the class implements MTCModule and returns null otherwise, logging an error.
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Attempts to instantiate and load a MTC module, logging an error message to {@code plugin}'s logger if any error occurs.
     * @param plugin the plugin requesting this operation
     * @param moduleClass the class of the module to load
     * @return the module instance created, {@code null} otherwise.
     */
    public static MTCModule load(MTC plugin, Class<? extends MTCModule> moduleClass) {
        if (!MTCModule.class.isAssignableFrom(moduleClass)) {
            plugin.getLogger().severe("**** Unable to load " + moduleClass.getName() + " - Does not implement MTCModule!");
            return null;
        }

        try {
            Constructor<? extends MTCModule> constructor = moduleClass.getConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (NoSuchMethodException e) {
            plugin.getLogger().severe("**** Unable to load " + moduleClass.getName() + " - Modules must specify a default constructor!");
        } catch (InvocationTargetException e) {
            plugin.getLogger().log(Level.SEVERE, "**** Unable to load " + moduleClass.getName() + " - Exception thrown from constructor: ", e.getCause());
        } catch (InstantiationException e) {
            plugin.getLogger().log(Level.SEVERE, "**** Unable to load " + moduleClass.getName() + " - Cannot instantiate abstract classes: ", e.getCause());
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "**** Unable to load" + moduleClass.getName() + " - Unhandled Exception: ", e);
        }

        return null;
    }
}
