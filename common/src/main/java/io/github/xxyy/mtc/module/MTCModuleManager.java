/*
 * Copyright (c) 2013-2016.
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
import io.github.xxyy.mtc.api.command.CommandRegistrationManager;
import io.github.xxyy.mtc.api.module.MTCModule;
import io.github.xxyy.mtc.api.module.ModuleCommand;
import io.github.xxyy.mtc.api.module.ModuleManager;
import io.github.xxyy.mtc.misc.ClearCacheBehaviour;
import io.github.xxyy.mtc.module.command.MTCModuleCommand;
import io.github.xxyy.mtc.yaml.ManagedConfiguration;
import org.bukkit.command.CommandExecutor;
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
public class MTCModuleManager implements ModuleManager {
    private final MTC plugin;
    private final ModuleLoader loader = new ModuleLoader(this);
    private final Map<Class<? extends MTCModule>, MTCModule> enabledModules = new HashMap<>();
    private final CommandRegistrationManager commandRegistrationManager = new CommandRegistrationManager();
    private final ManagedConfiguration enabledModulesConfig;

    /**
     * Constructs a new module manager.
     *
     * @param plugin     the plugin to manage modules for
     * @param dataFolder the data folder where configuration can be stored
     */
    public MTCModuleManager(MTC plugin, File dataFolder) {
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

    @Override
    public Collection<MTCModule> getEnabledModules() {
        return ImmutableSet.copyOf(enabledModules.values());
    }

    @Override
    public boolean isEnabled(@NotNull MTCModule module) {
        Preconditions.checkNotNull(module, "module");
        return module.equals(enabledModules.get(module.getClass()));
    }

    @Override
    public boolean isEnabled(@NotNull Class<? extends MTCModule> clazz) {
        Preconditions.checkNotNull(clazz, "clazz");
        return getModule(clazz) != null;
    }

    @Override
    public MTCModule getModule(String moduleName) {
        return enabledModules.values().stream()
                .filter(m -> m.getName().equalsIgnoreCase(moduleName))
                .findFirst().orElse(null);
    }

    @Override
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
        return new Reflections("io.github.xxyy.mtc.module").getSubTypesOf(MTCModule.class).stream()
                .filter(clazz -> !Modifier.isAbstract(clazz.getModifiers()))
                .collect(Collectors.toList());
    }

    @Override
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

    @Override
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

    @Override
    public boolean shouldLoad(MTCModule module, boolean def) {
        //TODO: Legacy conversion code - Remove after 2016-02-28
        String legacyPath = "enable." + module.getName();
        String path = "enable." + module.getClass().getSimpleName();
        if (plugin.getConfig().contains(legacyPath)) {
            def = plugin.getConfig().getBoolean(legacyPath, def); //soft conversion from legacy format
            plugin.getConfig().set(legacyPath, null);
            enabledModulesConfig.set(path, def);
        }

        enabledModulesConfig.addDefault(path, def);
        return enabledModulesConfig.getBoolean(path);
    }

    @Override
    public ModuleCommand registerModuleCommand(MTCModule module, CommandExecutor executor, String name,
                                               String... aliases) {
        Preconditions.checkNotNull(module, "module");
        Preconditions.checkNotNull(executor, "executor");
        Preconditions.checkNotNull(name, "name");
        Preconditions.checkNotNull(aliases, "aliases");

        MTCModuleCommand command = new MTCModuleCommand(module, name, executor, aliases);

        commandRegistrationManager.registerCommand(command);

        return command;
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

    @Override
    public MTC getPlugin() {
        return plugin;
    }
}
