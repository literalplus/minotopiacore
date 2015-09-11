package io.github.xxyy.mtc.module.showhomes;

import io.github.xxyy.common.util.LocationHelper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class EssentialsPlayerDataManager {
    /**
     * Reads homes of the given essentials userdata file
     *
     * @param userdataFile the file to read the homes from
     * @return a new EssentialsPlayerData object containing the read data
     */
    public static EssentialsPlayerData fromFile(@NotNull ShowHomesModule module, @NotNull File userdataFile) {
        String fileName = userdataFile.getName();
        UUID uuid = UUID.fromString(fileName.substring(0, fileName.length() - ".yml".length()));

        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(userdataFile);

        String lastAccountName = cfg.getString("lastAccountName");
        if (lastAccountName == null || lastAccountName.isEmpty()) {
            lastAccountName = uuid.toString();
        }

        EssentialsPlayerData essentialsPlayerData = new EssentialsPlayerData(module, uuid, lastAccountName, userdataFile);

        ConfigurationSection homesSection = cfg.getConfigurationSection("homes");
        if (homesSection == null) { //homes is not a required value in Essentials user files
            return essentialsPlayerData;
        }
        Set<String> homeNames = homesSection.getKeys(false);
        final Set<Home> homes = new HashSet<>(homeNames.size());

        for (String homeName : homeNames) {
            ConfigurationSection homeSection = homesSection.getConfigurationSection(homeName);
            String worldName = homeSection.getString("world");
            //ignore homes which world is not loaded currently
            World world = Bukkit.getWorld(worldName);
            if (world == null) {
                module.getPlugin().getLogger()
                        .warning("[ShowHomes] Could not find world '" + worldName + "' defined in home '" + homeName +
                                "' by user " + lastAccountName + " (UUID: " + uuid + ")");
                continue;
            }
            Location loc = LocationHelper.fromConfiguration(homeSection);
            homes.add(new Home(essentialsPlayerData, loc, homeName));
        }
        essentialsPlayerData.setHomes(homes);
        return essentialsPlayerData;
    }

    public static EssentialsPlayerData fromFile(@NotNull ShowHomesModule module, @NotNull UUID uuid) {
        File essentialsUserdataFolder = module.getEssentialsUserdataFolder();
        File yaml = new File(essentialsUserdataFolder, uuid + ".yml");
        return fromFile(module, yaml);
    }
}
