/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module;

import io.github.xxyy.mtc.MTC;
import io.github.xxyy.mtc.api.MTCPlugin;
import io.github.xxyy.mtc.misc.ClearCacheBehaviour;
import io.github.xxyy.mtc.yaml.ManagedConfiguration;

import java.util.logging.Level;

/**
 * An abstract base class for MTC modules which maintain their own configuration file. This is intended for only one
 * file with configuration contents. For custom storage in more files, try our sister product {@link io.github.xxyy.mtc.yaml.ManagedConfiguration}.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 22.8.14
 */
public abstract class ConfigurableMTCModule extends MTCModuleAdapter {
    private final ClearCacheBehaviour clearCacheBehaviour;
    private final String filePath;
    protected ManagedConfiguration configuration;

    /**
     * Creates a new configurable module and enables it by default.
     *
     * @param name                the name of this module, may not contain spaces
     * @param filePath            the path of the config file, relative to the plugin's data folder
     * @param clearCacheBehaviour the behaviour of the config file on a cache clear
     */
    protected ConfigurableMTCModule(String name, String filePath, ClearCacheBehaviour clearCacheBehaviour) {
        super(name);
        this.clearCacheBehaviour = clearCacheBehaviour;
        this.filePath = filePath;
    }

    /**
     * Creates a new configurable module.
     *
     * @param name             the name of this module, may not contain spaces
     * @param filePath         the path of the config file, relative to the plugin's data folder
     * @param behaviour        the behaviour of the config file on a cache clear
     * @param enabledByDefault whether the module should be enabled by default
     */
    protected ConfigurableMTCModule(String name, String filePath, ClearCacheBehaviour behaviour, boolean enabledByDefault) {
        super(name, enabledByDefault);
        this.clearCacheBehaviour = behaviour;
        this.filePath = filePath;
    }

    @Override
    public void enable(MTC plugin) throws Exception {
        super.enable(plugin);
        configuration = ManagedConfiguration.fromDataFolderPath(filePath, clearCacheBehaviour, plugin);
        configuration.setLoadHandler(cfg -> reloadImpl());
        reloadImpl();
        if (!configuration.getFile().exists()) {
            configuration.trySave();
        }
    }

    @Override
    public void reload(MTCPlugin plugin) {
        reloadConfig();
    }

    public void reloadConfig() {
        if (!configuration.tryLoad()) {
            plugin.getLogger().log(Level.WARNING, "Unable to load " + getName() + " module config!");
        }
    }

    /**
     * This gets called from {@link #reloadConfig()}, after the config has been updated from the file.
     */
    protected abstract void reloadImpl();

    public void save() {
        configuration.asyncSave(plugin); //Write to the logger itself if it fails
    }

}
