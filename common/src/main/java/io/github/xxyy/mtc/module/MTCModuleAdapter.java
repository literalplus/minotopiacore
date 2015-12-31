/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module;

import io.github.xxyy.mtc.MTC;
import io.github.xxyy.mtc.api.MTCPlugin;
import io.github.xxyy.mtc.api.module.MTCModule;
import io.github.xxyy.mtc.api.module.ModuleCommand;
import org.bukkit.command.CommandExecutor;

/**
 * Abstract base class for MTC modules. Note when implementing that the Reflection-based loading facility expects your
 * class to have a constructor with no arguments. If you don't provide such, your module won't be able to be loaded
 * automatically.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 22.8.14
 */
public abstract class MTCModuleAdapter implements MTCModule {
    private final String name;
    protected MTC plugin;
    protected boolean enabledByDefault = true;

    /**
     * Creates a new adapter and enables it by default.
     *
     * @param name the name of this module, may not contain spaces
     */
    protected MTCModuleAdapter(String name) {
        this.name = name;
    }

    /**
     * Creates a new adapter.
     *
     * @param name             the name of this module, may not contain spaces
     * @param enabledByDefault whether this module should be enabled by default
     */
    protected MTCModuleAdapter(String name, boolean enabledByDefault) {
        this.name = name;
        this.enabledByDefault = enabledByDefault;
    }

    @Override
    public void enable(MTCPlugin plugin) throws Exception {
        this.plugin = (MTC) plugin; //FIXME: This needs to be removed once we have moved required APIs over
    }

    @Override
    public void disable(MTCPlugin plugin) {

    }

    @Override
    public void clearCache(boolean forced, MTCPlugin plugin) {

    }

    @Override
    public void reload(MTCPlugin plugin) {

    }

    @Override
    public boolean canBeEnabled(MTCPlugin plugin) {
        return plugin.getModuleManager().shouldLoad(this, enabledByDefault);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public final boolean isEnabled() {
        return plugin == null || (plugin.isEnabled() && plugin.getModuleManager().isEnabled(this));
    }

    @Override
    public MTC getPlugin() {
        return plugin;
    }

    /**
     * Registers a command managed by this module with the corresponding server.
     *
     * @param executor the executor for the command
     * @param name     the name used to invoke the command, excluding '/'
     * @param aliases  the aliases alternatively used to access the command
     * @return the registered command
     * @see io.github.xxyy.mtc.api.module.ModuleManager#registerModuleCommand(MTCModule, CommandExecutor, String, String...)
     */
    protected ModuleCommand registerCommand(CommandExecutor executor, String name, String... aliases) {
        return plugin.getModuleManager().registerModuleCommand(this, executor, name, aliases);
    }
}
