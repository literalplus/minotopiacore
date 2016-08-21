/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.api.module;

import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.api.command.CommandBehaviour;
import li.l1t.mtc.api.module.inject.Injector;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

/**
 * Manages laoding and enabling of MTC modules as well as command registration.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2015-12-31
 */
public interface ModuleManager {
    /**
     * @return a view of the set of currently enabled modules
     */
    Collection<MTCModule> getEnabledModules();

    /**
     * Checks whether a module instance is enabled currently.
     *
     * @param module the module to check
     * @return whether that instance is enabled currently
     */
    boolean isEnabled(@Nonnull MTCModule module);

    /**
     * Checks whether a module instance is enabled currently.
     *
     * @param clazz the module to check
     * @return whether that instance is enabled currently
     */
    boolean isEnabled(@Nonnull Class<? extends MTCModule> clazz);

    /**
     * Retrieves a module instance by a name.
     *
     * @param moduleName the name to seek
     * @return a module by that name or null if there is no such module
     */
    MTCModule getModule(String moduleName);

    /**
     * Retrieves a module instance by class.
     *
     * @param clazz the class to seek
     * @return a module of that type or null if there is no such module
     */
    <T extends MTCModule> T getModule(Class<T> clazz);

    /**
     * Attempts to load MTC modules from a list of classes. If any error occurs, it will be logged
     * to {@link Plugin#getLogger()}.
     *
     * @param moduleClasses the classes to be loaded
     */
    void load(List<Class<? extends MTCModule>> moduleClasses);

    /**
     * Changes a module's enable state. If the module is being enabled, it will be injected into
     * other modules that depend on it and also its dependencies will be instantiated, if possible.
     * If it is being disabled, all injections are undone. {@link MTCModule#canBeEnabled(MTCPlugin)}
     * is respected.
     *
     * @param module  the module to enable
     * @param enabled the new enable state
     * @return a list of MTC modules whose states have changed as result of this method call or null
     * if an error occurred
     */
    @Nullable
    List<MTCModule> setEnabled(MTCModule module, boolean enabled);

    /**
     * Checks if a module should load according to the administrator's choice defined in a config
     * file.
     *
     * @param module the module to check
     * @param def    the default value if there is no choice defined
     * @return whether given module should be loaded
     */
    boolean shouldLoad(MTCModule module, boolean def);

    /**
     * Registers a command managed by a MTC module with the corresponding server's command map.
     *
     * @param module   the module to register the command for
     * @param executor the executor for the command
     * @param name     the name of the command, used to invoke it, ewithout '/' at the start
     * @param aliases  alias names of the command that can be used alternatively
     * @return the created command, for modification and adding of {@link CommandBehaviour}s.
     */
    ModuleCommand registerModuleCommand(MTCModule module, CommandExecutor executor, String name,
                                        String... aliases);

    /**
     * @return the plugin this manager belongs to
     */
    MTCPlugin getPlugin();

    /**
     * @return the injector associated with this manager
     */
    Injector getInjector();

    /**
     * @return the dependency manager associated with this manager
     */
    DependencyManager getDependencyManager();
}
