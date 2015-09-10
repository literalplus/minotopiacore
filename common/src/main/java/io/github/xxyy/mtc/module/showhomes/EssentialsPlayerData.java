package io.github.xxyy.mtc.module.showhomes;

import io.github.xxyy.common.util.LocationHelper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Represents the homes of a user backed by an Essentials userdata file.
 *
 * @author Janmm14
 */
public final class EssentialsPlayerData {

    @NotNull
    private final ShowHomesModule module;
    @NotNull
    private final UUID uuid;
    @NotNull
    private final String lastName;
    @NotNull
    private final File file;
    @NotNull
    private Set<Home> homes = new HashSet<>();

    private EssentialsPlayerData(@NotNull ShowHomesModule module, @NotNull UUID uuid, @NotNull String lastName, @NotNull File file) {
        this.module = module;
        this.uuid = uuid;
        this.lastName = lastName;
        this.file = file;
    }

    /**
     * Deletes a home of this user.
     *
     * @param executor the player who executed the command
     * @param homeName the home to delete
     */
    public void removeHome(@NotNull Player executor, @NotNull String homeName) throws IOException {
        //TODO notify Essentials cache about changes
        Player target = Bukkit.getPlayer(uuid);
        if (target != null) {
            executor.performCommand("delhome " + target.getName() + ':' + homeName);
            return;
        }
        if (!executor.hasPermission("essentials.delhome.others")) {
            executor.sendMessage("§cDu hast keine Berechtigung, Homes anderer Spieler zu entfernen!");
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        if (!cfg.isConfigurationSection("homes." + homeName)) {
            executor.sendMessage("§cDer Spieler " + lastName + " hat kein Home " + homeName + "!");
            return;
        }
        cfg.set("homes." + homeName, null);
        try {
            cfg.save(file);
        } catch (IOException ex) {
            executor.sendMessage("§cEs ist ein Fehler aufgetreten, der Home konnte nicht gelöscht werden."); //TODO move to caller
        }
        homes.removeIf(home -> home.getName().equalsIgnoreCase(homeName));
        executor.sendMessage("§aDer Home " + homeName + " von " + lastName + " wurde erfolgreich gelöscht.");
    }

    public void setHome(@NotNull Player executor, @NotNull String homeName) {
        setHome(executor, homeName, executor.getLocation());
    }
        /**
         * Sets a home of this user.
         *
         * @param executor the player who executed the command
         * @param homeName the home to set
         * @param loc      the location to set the home to
         */
    public void setHome(@NotNull Player executor, @NotNull String homeName, @NotNull Location loc) {
        //TODO notify Essentials cache about changes
        Player target = Bukkit.getPlayer(uuid);
        if (target != null && target.isOnline()) {
            executor.performCommand("sethome " + target.getName() + ':' + homeName);
            return;
        }
        if (!executor.hasPermission("essentials.sethome.others")) {
            executor.sendMessage("§cDu hast keine Berechtigung, Homes anderer Spieler zu setzen!");
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

        cfg.set("homes." + homeName + ".world", loc.getWorld().getName());
        cfg.set("homes." + homeName + ".x", loc.getX());
        cfg.set("homes." + homeName + ".y", loc.getY());
        cfg.set("homes." + homeName + ".z", loc.getZ());
        cfg.set("homes." + homeName + ".yaw", loc.getYaw());
        cfg.set("homes." + homeName + ".pitch", loc.getPitch());
        try {
            cfg.save(file);
        } catch (IOException ex) {
            module.getPlugin().getLogger()
                    .log(Level.SEVERE, "[ShowHomes] Could not save userdata file of " + lastName + " (UUID: " + uuid + "): ", ex);
            executor.sendMessage("§cEs ist ein Fehler aufgetreten, der Home konnte nicht gesetzt werden.");
            return;
        }
        homes.removeIf(home2 -> {
            if (home2.getName().equalsIgnoreCase(homeName)) {
                home2.getHologram().delete();
                return true;
            }
            return false;
        });
        Home newHome = new Home(this, loc, homeName);
        homes.add(newHome);
        newHome.showHologram(module);
        executor.sendMessage("§aDer Home " + homeName + " von " + lastName + " wurde erfolgreich gesetzt.");
    }

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
        essentialsPlayerData.homes = homes;
        return essentialsPlayerData;
    }

    public static EssentialsPlayerData fromFile(@NotNull ShowHomesModule module, @NotNull UUID uuid) {
        File essentialsUserdataFolder = module.getEssentialsUserdataFolder();
        File yaml = new File(essentialsUserdataFolder, uuid + ".yml");
        return fromFile(module, yaml);
    }

    @NotNull
    public ShowHomesModule getModule() {
        return this.module;
    }

    @NotNull
    public UUID getUuid() {
        return this.uuid;
    }

    @NotNull
    public String getLastName() {
        return this.lastName;
    }

    @NotNull
    public File getFile() {
        return this.file;
    }

    @NotNull
    public Set<Home> getHomes() {
        return this.homes;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof EssentialsPlayerData)) {
            return false;
        }
        final EssentialsPlayerData other = (EssentialsPlayerData) o;
        final Object otherUuid = other.getUuid();
        return this.uuid.equals(otherUuid);
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.uuid.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "showhomes.EssentialsPlayerData(uuid=" + this.uuid + ", lastName=" + this.lastName + ")";
    }
}
