/*
 * MinoTopiaCore
 * Copyright (C) 2013 - 2017 Philipp Nowak (https://github.com/xxyy) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package li.l1t.mtc.module.vote.reward.loader;

import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.api.misc.Cache;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.logging.LogManager;
import li.l1t.mtc.misc.CacheHelper;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Loads rewards from a directory of configuration files. Keeps backups of working files to ensure smooth operation in
 * case of configuration errors.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-01-12
 */
public class RewardConfigs implements Cache {
    private static final Logger LOGGER = LogManager.getLogger(RewardConfigs.class);
    private static final String REWARD_FILE_EXTENSION = ".rewards.yml";
    private static final String BACKUP_FILE_EXTENSION = ".bkp.yml";
    private final File directory;
    private final Map<String, RewardConfig> serviceConfigs = new HashMap<>();

    @InjectMe
    public RewardConfigs(MTCPlugin plugin) throws IOException {
        this(new File(plugin.getDataFolder() + "/modules/vote/"));
    }

    public RewardConfigs(File directory) throws IOException {
        this.directory = directory;
        if(!directory.exists()) {
            Files.createDirectories(directory.toPath());
        }
        CacheHelper.registerCache(this);
    }

    public Optional<RewardConfig> findConfig(String serviceName) {
        return Optional.ofNullable(serviceConfigs.get(serviceName));
    }

    public Stream<RewardConfig> configs() {
        return serviceConfigs.values().stream();
    }

    public void loadAll() {
        File[] files = directory.listFiles(file -> file.getName().endsWith(REWARD_FILE_EXTENSION));
        if (files == null) {
            throw new IllegalStateException("I/O error reading file list from " + directory.getAbsolutePath());
        }
        serviceConfigs.clear();
        for (File file : files) {
            String serviceName = file.getName().replaceAll(Pattern.quote(REWARD_FILE_EXTENSION), "");
            RewardConfig config;
            try {
                config = loadSingle(file, serviceName);
                backupConfig(config, serviceName);
            } catch (Exception e) {
                LOGGER.error(
                        "Failed to load reward config at {} (discovered service name {})",
                        file.getAbsolutePath(), serviceName
                );
                LOGGER.error("Strack Trace: ", e);
                config = attemptLoadBackupConfig(serviceName);
            }
            if(config != null) {
                //config.trySave(); // <-- removes comments, which is not acceptable; disabled for now
                serviceConfigs.put(serviceName, config);
            }
        }
    }

    private RewardConfig loadSingle(File file, String serviceName) throws IOException {
        RewardConfig config = new RewardConfig(file, serviceName);
        config.load(file);
        return config;
    }

    private RewardConfig attemptLoadBackupConfig(String serviceName) {
        File backupFile = getBackupFilePath(serviceName);
        if(backupFile.exists()) {
            try {
                LOGGER.warn("Loading backup config from {}...", backupFile);
                return loadSingle(backupFile, serviceName);
            } catch (Exception e) {
                LOGGER.error("Failed to load backup config {}", backupFile.getAbsolutePath());
                LOGGER.error("Stack Trace: ", e);
            }
        } else {
            LOGGER.error("No backup config present, service rewards will be unavailable! (looked at {})", backupFile.getAbsolutePath());
        }
        return null;
    }

    private File getBackupFilePath(String serviceName) {
        return new File(directory, serviceName + BACKUP_FILE_EXTENSION);
    }

    private void backupConfig(RewardConfig config, String serviceName) {
        File backupFile = getBackupFilePath(serviceName);
        try {
            config.save(backupFile);
        } catch (Exception e) {
            LOGGER.warn("Failed to save backup config to {}", backupFile.getAbsolutePath());
            LOGGER.warn("Stack Trace:", e);
        }
    }

    @Override
    public void clearCache(boolean forced, MTCPlugin plugin) {
        if(forced) {
            loadAll();
        }
    }
}
