package io.github.xxyy.mtc.module;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import io.github.xxyy.mtc.MTC;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

/**
 * An abstract base class for MTC modules which maintain their own configuration file.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 22.8.14
 */
public abstract class ConfigurableMTCModule extends MTCModuleAdapter {
    protected YamlConfiguration configuration;
    protected final ClearCacheBehaviour clearCacheBehaviour;
    protected final String configFilePath;
    protected File configFile;

    public ConfigurableMTCModule(String name, String filePath, ClearCacheBehaviour clearCacheBehaviour) {
        super(name);
        configFilePath = filePath;
        this.clearCacheBehaviour = clearCacheBehaviour;
    }

    @Override
    public void enable(MTC plugin) {
        super.enable(plugin);
        configFile = new File(plugin.getDataFolder(), configFilePath);
        if (!configFile.exists()) {
            if (!configFile.getParentFile().mkdirs()) {
                throw new IllegalStateException("Couldn't create " + getName() + " module config file's parent dirs for some reason: " + configFile.getAbsolutePath()); //Sometimes I hate Java's backwards compat
            }
            try {
                if (!configFile.createNewFile()) {
                    throw new IOException("Couldn't create " + getName() + " module config file for some reason: " + configFile.getAbsolutePath());
                }
            } catch (IOException e) {
                throw new IllegalStateException("Caught IOException", e);
            }
        }
        configuration = YamlConfiguration.loadConfiguration(configFile);

        reloadConfig();
    }

    @Override
    public void clearCache(boolean forced, MTC plugin) {
        switch (clearCacheBehaviour) {
            case RELOAD:
                reloadConfig();
                break;
            case SAVE:
                save();
                break;
            case RELOAD_ON_FORCED:
                if (forced) {
                    reloadConfig();
                } else {
                    save();
                }
        }
    }

    @Override
    public void reload(MTC plugin) {
        reloadConfig();
    }

    public void reloadConfig() {
        try {
            configuration.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            plugin.getLogger().log(Level.WARNING, "Unable to load " + getName() + " module config: ", e);
        }

        reloadImpl();
    }

    /**
     * This gets called from {@link #reloadConfig()}, after the config has been updated from the file.
     */
    protected abstract void reloadImpl();

    public void save() {
        try {
            configuration.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Unable to save " + getName() + " module config: ", e);
        }
    }

    public enum ClearCacheBehaviour {
        /**
         * Reloads the config file upon any cache clear.
         */
        RELOAD,
        /**
         * Saves the config file upon any cache clear.
         */
        SAVE,
        /**
         * Saves the config file for normal cache clears and reloads it
         * for forced ones.
         */
        RELOAD_ON_FORCED,
        /**
         * Does nothing on cache clear.
         */
        NOTHING
    }
}
