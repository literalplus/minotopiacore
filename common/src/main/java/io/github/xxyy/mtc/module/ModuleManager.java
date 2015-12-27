/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module;

import io.github.xxyy.lib.guava17.base.Preconditions;
import io.github.xxyy.lib.guava17.collect.ImmutableSet;
import io.github.xxyy.lib.intellij_annotations.NotNull;
import io.github.xxyy.lib.intellij_annotations.Nullable;
import io.github.xxyy.mtc.MTC;
import io.github.xxyy.mtc.misc.ClearCacheBehaviour;
import io.github.xxyy.mtc.yaml.ManagedConfiguration;
import org.bukkit.plugin.Plugin;
import org.reflections.Reflections;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Manages MTC modules at runtime.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 18/06/15
 */
public class ModuleManager {
    private final MTC plugin;
    private final ModuleLoader loader = new ModuleLoader(this);
    private final Map<Class<? extends MTCModule>, MTCModule> enabledModules = new HashMap<>();
    private final Reflections reflections = new Reflections("io.github.xxyy.mtc.module");
    private final ManagedConfiguration enabledModulesConfig;

    /**
     * Constructs a new module manager.
     *
     * @param plugin     the plugin to manage modules for
     * @param dataFolder the data folder where configuration can be stored
     */
    public ModuleManager(MTC plugin, File dataFolder) {
        this.plugin = plugin;
        enabledModulesConfig = ManagedConfiguration.fromFile(
                new File(dataFolder, "enabled_modules.yml"),
                ClearCacheBehaviour.SAVE
        );
        enabledModulesConfig.options()
                .copyDefaults(true)
                .copyHeader(true)
                .header("Choose which modules to load. Note that modules may have dependencies.");
    }

    /**
     * @return a view of the set of currently enabled modules
     */
    public Collection<MTCModule> getEnabledModules() {
        return ImmutableSet.copyOf(enabledModules.values());
    }

    /**
     * Checks whether a module instance is enabled currently.
     *
     * @param module the module to check
     * @return whether that instance is enabled currently
     */
    public boolean isEnabled(@NotNull MTCModule module) {
        Preconditions.checkNotNull(module, "module");
        return module.equals(enabledModules.get(module.getClass()));
    }

    /**
     * Checks whether a module instance is enabled currently.
     *
     * @param clazz the module to check
     * @return whether that instance is enabled currently
     */
    public boolean isEnabled(@NotNull Class<? extends MTCModule> clazz) {
        Preconditions.checkNotNull(clazz, "clazz");
        return getModule(clazz) != null;
    }

    /**
     * Retrieves a module instance by a name.
     *
     * @param moduleName the name to seek
     * @return a module by that name or null if there is no such module
     */
    public MTCModule getModule(String moduleName) {
        return enabledModules.values().stream()
                .filter(m -> m.getName().equalsIgnoreCase(moduleName))
                .findFirst().orElse(null);
    }

    /**
     * Retrieves a module instance by class.
     *
     * @param clazz the class to seek
     * @return a module of that type or null if there is no such module
     */
    public <T extends MTCModule> T getModule(Class<T> clazz) {
        //noinspection unchecked
        return (T) enabledModules.get(clazz);
    }

    /**
     * Finds module classes in the shipped module package {@code io.github.xxyy.mtc.module}.
     *
     * @return a list of the discovered classes
     */
    public List<Class<? extends MTCModule>> findShippedModules() {
        return reflections.getSubTypesOf(MTCModule.class).stream()
                .filter(clazz -> !Modifier.isAbstract(clazz.getModifiers()))
                .collect(Collectors.toList());
    }

    /**
     * Attempts to load MTC modules from a list of classes. If any error occurs, it will be logged to
     * {@link Plugin#getLogger()}.
     *
     * @param moduleClasses the classes to be loaded
     */
    public void load(List<Class<? extends MTCModule>> moduleClasses) {
        loader.loadAll(moduleClasses,
                (meta, thrown) ->
                        plugin.getLogger().log(Level.SEVERE,
                                String.format("**** Unable to load %s: ", meta.getClazz().getName()), thrown)
        );
    }

    /**
     * Attempts to enable all loaded modules which are not currently enabled and are ready to be enabled.
     * Errors will be caught on a per-module basis and logged to {@link Plugin#getLogger()}.
     */
    public void enableLoaded() {
        loader.getLoadedModules().stream()
                .filter(m -> m.getModule().canBeEnabled(plugin))
                .forEach(m -> loader.setEnabled(m, true));
        enabledModulesConfig.trySave();
    }

    /**
     * Changes a module's enable state. If the module is being enabled, it will be injected into other modules that
     * depend on it and also its dependencies will be instantiated, if possible. If it is being disabled, all
     * injections are undone. {@link MTCModule#canBeEnabled(MTC)} is respected.
     *
     * @param module  the module to enable
     * @param enabled the new enable state
     * @return a list of MTC modules whose states have changed as result of this method call or null if an error occurred
     */
    @Nullable
    public List<MTCModule> setEnabled(MTCModule module, boolean enabled) {
        try {
            return loader.setEnabled(module, enabled);
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Module " + module.getName() + " could not be " +
                    (enabled ? "en" : "dis") + "abled:", e);
            return null;
        }
    }

    /**
     * Checks if a module should load according to the administrator's choice defined in a config file.
     *
     * @param module the module to check
     * @param def    the default value if there is no choice defined
     * @return whether given module should be loaded
     */
    public boolean shouldLoad(MTCModule module, boolean def) {
        //TODO: Legacy conversion code - Remove after 2016-02-28
        String legacyPath = "enable." + module.getName();
        if (plugin.getConfig().contains(legacyPath)) {
            def = plugin.getConfig().getBoolean(legacyPath, def); //soft conversion from legacy format
            plugin.getConfig().set(legacyPath, null);
        }

        String path = "enable." + module.getClass().getSimpleName();
        enabledModulesConfig.addDefault(path, def);
        return enabledModulesConfig.getBoolean(path);
    }

    void registerEnabled(MTCModule module, boolean enabled) {
        try {
            if (enabled) {
                enabledModules.put(module.getClass(), module);
                module.enable(plugin);
            } else {
                enabledModules.remove(module.getClass());
                module.disable(plugin);
            }
        } catch (Exception | NoClassDefFoundError e) { //occurs at disable when reloading with replaced jar
            plugin.getLogger().log(Level.SEVERE, "Module " + module.getName() + " failed to change enabled state:", e);
        }
    }

    /**
     * @return the plugin this manager belongs to
     */
    public MTC getPlugin() {
        return plugin;
    }
}
