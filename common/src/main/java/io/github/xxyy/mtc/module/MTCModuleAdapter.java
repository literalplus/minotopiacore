/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module;

import io.github.xxyy.mtc.MTC;

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
    public void enable(MTC plugin) throws Exception {
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
    public boolean canBeEnabled(MTC plugin) {
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
}
