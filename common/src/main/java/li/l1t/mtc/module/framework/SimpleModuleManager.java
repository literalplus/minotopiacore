/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.framework;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import li.l1t.mtc.MTC;
import li.l1t.mtc.api.command.CommandRegistrationManager;
import li.l1t.mtc.api.module.DependencyManager;
import li.l1t.mtc.api.module.MTCModule;
import li.l1t.mtc.api.module.ModuleCommand;
import li.l1t.mtc.api.module.ModuleManager;
import li.l1t.mtc.api.module.inject.Injector;
import li.l1t.mtc.logging.LogManager;
import li.l1t.mtc.misc.ClearCacheBehaviour;
import li.l1t.mtc.module.command.MTCModuleCommand;
import li.l1t.mtc.yaml.ManagedConfiguration;
import org.apache.logging.log4j.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.Plugin;
import org.reflections.Reflections;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
public class SimpleModuleManager implements ModuleManager {
    private static final Logger LOGGER = LogManager.getLogger(SimpleModuleManager.class);
    private final MTC plugin;
    private final ModuleLoader loader = new ModuleLoader(this);
    private final Injector injector = new SimpleInjector(this);
    private final DependencyManager dependencyManager = new ReflectionDependencyResolver(this);

    private final Map<Class<? extends MTCModule>, MTCModule> enabledModules = new HashMap<>();
    private final CommandRegistrationManager commandRegistrationManager = new CommandRegistrationManager();
    private final ManagedConfiguration enabledModulesConfig;

    /**
     * Constructs a new module manager.
     *
     * @param plugin     the plugin to manage modules for
     * @param dataFolder the data folder where configuration can be stored
     */
    public SimpleModuleManager(MTC plugin, File dataFolder) {
        this.plugin = plugin;
        enabledModulesConfig = ManagedConfiguration.fromFile(
                new File(dataFolder, "enabled_modules.yml"),
                ClearCacheBehaviour.RELOAD_ON_FORCED
        );
        enabledModulesConfig.options()
                .copyDefaults(true)
                .copyHeader(true)
                .header("Choose which modules to load. Note that modules may have dependencies.\n" +
                        "Add command names to override-commands to force MTC to register these\n" +
                        "commands if any module requests them, ignoring any other plugins that\n" +
                        "might have already registered them.");
        enabledModulesConfig.addDefault("override-commands", new ArrayList<>(Collections.singletonList("add-commands-here")));
    }

    @Override
    public Collection<MTCModule> getEnabledModules() {
        return ImmutableSet.copyOf(enabledModules.values());
    }

    @Override
    public boolean isEnabled(@Nonnull MTCModule module) {
        Preconditions.checkNotNull(module, "module");
        return module.equals(enabledModules.get(module.getClass()));
    }

    @Override
    public boolean isEnabled(@Nonnull Class<? extends MTCModule> clazz) {
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
     * Finds module classes in the shipped module package {@code li.l1t.mtc.module}.
     *
     * @return a list of the discovered classes
     */
    public List<Class<? extends MTCModule>> findShippedModules() {
        return new Reflections("li.l1t.mtc.module").getSubTypesOf(MTCModule.class).stream()
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
     * Attempts to enable all loaded modules which are not currently enabled and are ready to be
     * enabled. Errors will be caught on a per-module basis and logged to {@link
     * Plugin#getLogger()}.
     */
    public void enableLoaded() {
        loader.getLoadedModules().stream()
                .filter(m -> m.getInstance().canBeEnabled(plugin))
                .forEach(m -> loader.setEnabled(m, true));
        enabledModulesConfig.trySave();
    }

    @Override
    @Nullable
    public List<MTCModule> setEnabled(MTCModule module, boolean enabled) {
        try {
            return loader.setEnabled(module, enabled);
        } catch (Exception e) {
            LOGGER.error(String.format("Module %s could not be %sabled:",
                    module.getName(), enabled ? "en" : "dis"), e);
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

        if (shouldForceOverrideCommand(name)) {
            commandRegistrationManager.unregisterCommandLabel(getPlugin().getServer(), name);
        }
        commandRegistrationManager.registerCommand(command);

        return command;
    }

    private boolean shouldForceOverrideCommand(String label) {
        return enabledModulesConfig.getStringList("override-commands").contains(label);
    }

    void registerEnabled(MTCModule module, boolean enabled) {
        try {
            if (enabled) {
                module.enable(plugin);
                enabledModules.put(module.getClass(), module);
            } else {
                enabledModules.remove(module.getClass());
                module.disable(plugin);
            }
        } catch (Exception | NoClassDefFoundError e) {
            if (!"ignore".equals(e.getMessage())) { //unit tests, let's pretend it's a feature
                String message = String.format(
                        "Module %s failed to change enabled state to %s",
                        module.getName(), enabled
                );
                LOGGER.error(message, e);
                Command.broadcastCommandMessage(getPlugin().getServer().getConsoleSender(), message);
            }
        }
    }

    @Override
    public MTC getPlugin() {
        return plugin;
    }

    @Override
    public Injector getInjector() {
        return injector;
    }

    @Override
    public DependencyManager getDependencyManager() {
        return dependencyManager;
    }
}
