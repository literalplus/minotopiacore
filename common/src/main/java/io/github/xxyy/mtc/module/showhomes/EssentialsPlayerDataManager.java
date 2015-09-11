package io.github.xxyy.mtc.module.showhomes;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class EssentialsPlayerDataManager {

    @NotNull
    private final ShowHomesModule module;
    private LoadingCache<UUID, EssentialsPlayerData> cache = CacheBuilder.newBuilder()
            .initialCapacity(14000)
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .weakValues()
            .build(new CacheLoader<UUID, EssentialsPlayerData>() {
                @Override
                public EssentialsPlayerData load(@NotNull UUID key) throws Exception {
                    return byUuid(key);
                }
            });

    public EssentialsPlayerDataManager(@NotNull ShowHomesModule module) {
        this.module = module;
    }

    public void clearCache() {
        cache.invalidateAll();
    }

    public EssentialsPlayerData reload(UUID uuid) {
        cache.invalidate(uuid);
        return cache.getUnchecked(uuid);
    }

    public EssentialsPlayerData get(UUID uuid) {
        return cache.getUnchecked(uuid);
    }

    public EssentialsPlayerData getByFile(File userdataFile) {
        UUID uuid = getUuidByFile(userdataFile);
        EssentialsPlayerData essentialsPlayerData = readPlayerData(userdataFile, uuid);
        cache.put(uuid, essentialsPlayerData);
        return essentialsPlayerData;
    }

    /**
     * Reads the UUID out of essentials user data file name.
     * @param userdataFile the file to read the uuid from
     * @return the uuid
     * @throws IllegalArgumentException if the filename without the extension is not a valid uuid
     */
    @NotNull
    private static UUID getUuidByFile(@NotNull File userdataFile) {
        String fileName = userdataFile.getName();
        return UUID.fromString(fileName.substring(0, fileName.length() - ".yml".length()));
    }

    private File getUserdataFile(@NotNull UUID uuid) {
        File essentialsUserdataFolder = module.getEssentialsUserdataFolder();
        return new File(essentialsUserdataFolder, uuid + ".yml");
    }

    private EssentialsPlayerData byUuid(@NotNull UUID uuid) {
        return readPlayerData(getUserdataFile(uuid), uuid);
    }

    private EssentialsPlayerData readPlayerData(@NotNull File userdataFile, @NotNull UUID uuid) {
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(userdataFile);

        String lastAccountName = cfg.getString("lastAccountName");
        if (lastAccountName == null || lastAccountName.isEmpty()) {
            lastAccountName = uuid.toString();
        }

        EssentialsPlayerData essentialsPlayerData = new EssentialsPlayerData(module, uuid, lastAccountName, userdataFile);

        ConfigurationSection homesSection = cfg.getConfigurationSection("homes");
        if (homesSection == null) { //homes is not a required value in Essentials player files
            return essentialsPlayerData;
        }

        //read homes
        Set<String> homeNames = homesSection.getKeys(false);
        final Set<Home> homes = new HashSet<>(homeNames.size());

        for (String homeName : homeNames) {
            ConfigurationSection homeSection = homesSection.getConfigurationSection(homeName);
            homes.add(Home.deserialize(essentialsPlayerData, homeSection));
        }
        essentialsPlayerData.setHomes(homes);
        return essentialsPlayerData;
    }

    @NotNull
    public ShowHomesModule getModule() {
        return module;
    }
}
