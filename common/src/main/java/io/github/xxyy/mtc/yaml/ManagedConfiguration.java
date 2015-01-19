/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.yaml;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import io.github.xxyy.lib.guava17.io.Files;
import io.github.xxyy.mtc.MTC;
import io.github.xxyy.mtc.misc.CacheHelper;
import io.github.xxyy.mtc.misc.ClearCacheBehaviour;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.logging.Level;

/**
 * Represents a YAML configuration file managed by MTC with nice capabilities like automatic saving, etc. This keeps
 * a single File object to which changes can easily be saved to and loaded from. Static load methods also provide
 * extended syntax error fallback. (i.e. making backups of invalid files)
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 19/01/15
 */
public class ManagedConfiguration extends YamlConfiguration implements CacheHelper.Cache {
    private static final Consumer<ManagedConfiguration> NOOP_CONSUMER = (mc) -> {
    }; //formatter puts this on the next line and I can't really blame it tbh
    private final File file;
    private Consumer<ManagedConfiguration> loadHandler = NOOP_CONSUMER;
    private Consumer<ManagedConfiguration> saveHandler = NOOP_CONSUMER;
    private ClearCacheBehaviour clearCacheBehaviour;
    private Exception error;

    private ManagedConfiguration(File file) {
        this.file = file;
    }


    @Override
    public void loadFromString(String contents) throws InvalidConfigurationException {
        setError(null);
        super.loadFromString(contents);
        loadHandler.accept(this);
    }

    @Override
    public String saveToString() {
        String result = super.saveToString();
        saveHandler.accept(this);
        return result;
    }

    /**
     * Saves this configuration to its corresponding file.
     *
     * @throws IOException If an error occurs saving the file
     */
    public void save() throws IOException {
        save(file);
    }

    public boolean trySave() {
        try {
            save();
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Couldn't save managed configuration to " + file.getAbsolutePath() + "!");
            return false;
        }
        return true;
    }

    public boolean tryLoad() {
        try {
            load(file);
        } catch (FileNotFoundException ex) {
            Bukkit.getLogger().info(String.format("Attempted to load configuration from %s, but file didn't exist!", file.getAbsolutePath()));
            setError(ex);
            return false;
        } catch (IOException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + file, ex);
            setError(ex);
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method also tries to save a backup file if the loaded file contains any syntax errors and wioll print the location to console.
     *
     * @param file the file to load from
     * @throws IOException If an error occurs loading the file
     */
    public void load(File file) throws IOException {
        try {
            super.load(file);
        } catch (InvalidConfigurationException ex) { //Handle backups
            try {
                File backupFile = new File(file.getAbsolutePath(), file.getName() + ".mtcbak");
                Files.copy(file, backupFile);
                Bukkit.getLogger().log(Level.SEVERE, String.format("Invalid configuration syntax detected for %s! Backup is available at %s",
                        file, backupFile.getName()), ex);
            } catch (IOException e) {
                Bukkit.getLogger().log(Level.SEVERE, "Failed to save backup for invalid configuration file at " + file + "!", ex);
            }
            setError(ex);
        }
    }

    @Override
    public void clearCache(boolean forced, MTC plugin) {
        switch (clearCacheBehaviour) {
            case RELOAD:
                tryLoad();
                break;
            case SAVE:
                trySave();
                break;
            case RELOAD_ON_FORCED:
                if (forced) {
                    tryLoad();
                } else {
                    trySave();
                }
                break;
            case NOTHING:
                break;
            default:
                throw new UnsupportedOperationException("ClearCacheBehaviour of " + clearCacheBehaviour.name() + " not supported!");
        }
    }


    public ClearCacheBehaviour getClearCacheBehaviour() {
        return clearCacheBehaviour;
    }

    public void setClearCacheBehaviour(ClearCacheBehaviour clearCacheBehaviour) {
        this.clearCacheBehaviour = clearCacheBehaviour;
    }

    /**
     * @return the exception encountered while loading this configuration, if any
     */
    public Exception getError() {
        return error;
    }

    protected void setError(Exception error) {
        this.error = error;
    }

    public void setLoadHandler(Consumer<ManagedConfiguration> loadHandler) {
        this.loadHandler = loadHandler;
    }

    public void setSaveHandler(Consumer<ManagedConfiguration> saveHandler) {
        this.saveHandler = saveHandler;
    }

    /**
     * Creates a new {@link ManagedConfiguration}, loading from the given file.
     * </p><p>
     * Any errors loading the Configuration will be logged and available at {@link #getError()}.
     * If the specified input is not a valid config, a blank config will be
     * returned.
     * </p>
     * The encoding used may follow the system dependent default.
     *
     * @param file      Input file
     * @param behaviour what to do on a cache clear
     * @return Resulting configuration
     * @throws IllegalArgumentException Thrown if file is null or it didn't exist and couldn't be created
     */
    public static ManagedConfiguration fromFile(File file, ClearCacheBehaviour behaviour) {
        Validate.notNull(file, "File cannot be null");

        if (!file.exists()) {
            if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
                throw new IllegalStateException("Couldn't create managed config file's parent dirs for some reason: " + file.getAbsolutePath()); //Sometimes I hate Java's backwards compat
            }
            try {
                if (!file.createNewFile()) {
                    throw new IOException("Couldn't create managed config file for some reason: " + file.getAbsolutePath());
                }
            } catch (IOException e) {
                throw new IllegalStateException("Caught IOException", e);
            }
        }

        ManagedConfiguration config = new ManagedConfiguration(file);
        config.setClearCacheBehaviour(behaviour);
        config.tryLoad();

        return config;
    }

    /**
     * Creates a new {@link ManagedConfiguration}, loading from the given file path relative to the given plugin's data folder.
     * </p><p>
     * Any errors loading the Configuration will be logged and available at {@link #getError()}.
     * If the specified input is not a valid config, a blank config will be
     * returned.
     * </p>
     * The encoding used may follow the system dependent default.
     *
     * @param filePath  Input file path, relative to the plugin's data folder
     * @param behaviour what to do on a cache clear
     * @param plugin    the plugin whose data folder to use
     * @return Resulting configuration
     * @throws IllegalArgumentException Thrown if file is null or it didn't exist and couldn't be created
     */
    public static ManagedConfiguration fromDataFolderPath(String filePath, ClearCacheBehaviour behaviour, MTC plugin) {
        File file = new File(plugin.getDataFolder(), filePath);
        return fromFile(file, behaviour);
    }
}
