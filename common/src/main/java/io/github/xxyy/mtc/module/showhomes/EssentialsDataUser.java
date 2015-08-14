package io.github.xxyy.mtc.module.showhomes;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import lombok.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "uuid")
@ToString(exclude = {"homes", "file"})
public final class EssentialsDataUser implements Iterable<Home> {

    @NonNull
    private final UUID uuid;
    @NonNull
    private final String lastName;
    @NonNull
    private final File file;
    @NonNull
    private List<Home> homes = new ArrayList<>();

    /**
     * Reads homes of the given essentials userdata file
     *
     * @param userdataFile the file to read the homes from
     * @return a new UserHomes object containing the read data
     */
    public static EssentialsDataUser readHomes(File userdataFile) {
        try {
            String fileName = userdataFile.getName();
            UUID uuid = UUID.fromString(fileName.substring(0, fileName.length() - ".yml".length()));

            YamlConfiguration cfg = YamlConfiguration.loadConfiguration(userdataFile);

        String lastAccountName = cfg.getString("lastAccountName");
        if (lastAccountName==null || lastAccountName.isEmpty()) {
            lastAccountName = "*****unknown*****";
        }

            EssentialsDataUser essentialsDataUser = new EssentialsDataUser(uuid, lastAccountName, userdataFile);

            ConfigurationSection homesSection = cfg.getConfigurationSection("homes");
            if (homesSection == null) { //homes is not a required value in Essentials user files
                return essentialsDataUser;
            }
            Set<String> homeNames = homesSection.getKeys(false);
            final List<Home> homes = new ArrayList<>(homeNames.size());

            for (String homeName : homeNames) {
                ConfigurationSection homeSection = homesSection.getConfigurationSection(homeName);
                String worldName = homeSection.getString("world");
                World world = Bukkit.getWorld(worldName);
                if (world == null) {
                    ShowHomes.getInstance().getLogger()
                        .warning("Could not find world '" + worldName + "' defined in home '" + homeName +
                            "' by user " + lastAccountName + " (UUID: " + uuid + ")");
                    continue;
                }
                Location loc = new Location(
                    world,
                    homeSection.getDouble("x"),
                    homeSection.getDouble("y"),
                    homeSection.getDouble("z"),
                    (float) homeSection.getDouble("yaw"),
                    (float) homeSection.getDouble("pitch")
                );
                homes.add(new Home(essentialsDataUser, loc, homeName));
            }
            essentialsDataUser.homes = homes;
            return essentialsDataUser;
        } catch (Exception ex) {
            ShowHomes.handleException(new Exception("readHomes(" + userdataFile.getName() + ")", ex));
            return null;
        }
    }

    /**
     * Deletes a home of this user.
     *
     * @param executor the player who executed the command
     * @param homeName the home to delete
     */
    public void removeHome(@NonNull Player executor, @NonNull String homeName) {
        try {
            Player aim = Bukkit.getPlayer(uuid);
            if (aim != null && aim.isOnline()) {
                executor.performCommand("/delhome " + aim.getName() + ':' + homeName);
                return;
            }
            if (!executor.hasPermission("essentials.delhome.others")) {
                executor.sendMessage("§cDu hast keine Berechtigung, Homes anderer Spieler zu entfernen!");
                return;
            }
            YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
            if (cfg.isConfigurationSection("homes." + homeName)) {
                cfg.set("homes." + homeName, null);
                try {
                    cfg.save(file);
                } catch (IOException ex) {
                    ShowHomes.getInstance().getLogger()
                        .severe("Could not save userdata file of " + lastName + " (UUID: " + uuid + "): ");
                    ex.printStackTrace();
                    executor.sendMessage("§cEs ist ein Fehler aufgetreten, der Home konnte nicht gelöscht werden.");
                    return;
                }
                Iterator<Home> it = homes.iterator();
                int count = 0;
                while (it.hasNext()) {
                    Home home = it.next();
                    if (home.getName().equalsIgnoreCase(homeName)) {
                        it.remove();
                        count++;
                    }
                }
                if (count > 1) {
                    ShowHomes.getInstance().getLogger()
                        .warning("Found " + count + " home classes with the name '" + homeName + "' " +
                            "for player " + lastName + " (UUID: " + uuid + ")");
                }
                executor.sendMessage("§aDer Home " + homeName + " von " + lastName + " wurde erfolgreich gelöscht.");
                return;
            }
            executor.sendMessage("§cDer Spieler " + lastName + " hat kein Home " + homeName + "!");
        } catch (Exception ex) {
            ShowHomes.handleException(new Exception("removeHome(" + executor + ", " + homeName + ")", ex));
        }
    }

    /**
     * Sets a home of this user.
     *
     * @param executor the player who executed the command
     * @param homeName the home to set
     * @param loc the location to set the home to, if {@code null}, {@link Player#getLocation()} is used as location
     */
    public void setHome(@NonNull Player executor, @NonNull String homeName, @Nullable Location loc) {
        try {
            Player aim = Bukkit.getPlayer(uuid);
            if (aim != null && aim.isOnline()) {
                executor.performCommand("/sethome " + aim.getName() + ':' + homeName);
                return;
            }
            if (!executor.hasPermission("essentials.sethome.others")) {
                executor.sendMessage("§cDu hast keine Berechtigung, Homes anderer Spieler zu setzen!");
                return;
            }
            YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
            if (cfg.isConfigurationSection("homes." + homeName)) {
                // set loc to executor's position if location is null
                if (loc == null) {
                    loc = executor.getLocation();
                }
                cfg.set("homes." + homeName + ".world", loc.getWorld().getName());
                cfg.set("homes." + homeName + ".x", loc.getX());
                cfg.set("homes." + homeName + ".y", loc.getY());
                cfg.set("homes." + homeName + ".z", loc.getZ());
                cfg.set("homes." + homeName + ".yaw", loc.getYaw());
                cfg.set("homes." + homeName + ".pitch", loc.getPitch());
                try {
                    cfg.save(file);
                } catch (IOException ex) {
                    ShowHomes.getInstance().getLogger()
                        .severe("Could not save userdata file of " + lastName + " (UUID: " + uuid + "): ");
                    ex.printStackTrace();
                    executor.sendMessage("§cEs ist ein Fehler aufgetreten, der Home konnte nicht gesetzt werden.");
                    return;
                }
                Iterator<Home> it = homes.iterator();
                int count = 0;
                while (it.hasNext()) {
                    Home home = it.next();
                    if (home.getName().equalsIgnoreCase(homeName)) {
                        Hologram holo = home.getHologram();
                        if (holo != null) {
                            holo.delete();
                        }
                        it.remove();
                        count++;
                    }
                }
                if (count > 1) {
                    ShowHomes.getInstance().getLogger()
                        .warning("Found " + count + " home objects with the name '" + homeName + "' " +
                            "for player " + lastName + " (UUID: " + uuid + ")");
                }
                Home newHome = new Home(this, loc, homeName);
                homes.add(newHome);
                newHome.showHologram();
                executor.sendMessage("§aDer Home " + homeName + " von " + lastName + " wurde erfolgreich gesetzt.");
                return;
            }
            executor.sendMessage("§cDer Spieler " + lastName + " hat kein Home " + homeName + "!");
        } catch (Exception ex) {
            ShowHomes.handleException(new Exception("setHome(" + executor + ", " + homeName + ", " + loc + ")", ex));
        }
    }

    public static EssentialsDataUser readHomes(@NonNull UUID uuid) {
        try {
            File essentialsUserdataFolder = ShowHomes.getEssentialsUserdataFolder();
            File yaml = new File(essentialsUserdataFolder, uuid + ".yml");
            return readHomes(yaml);
        } catch (Exception ex) {
            ShowHomes.handleException(ex);
            return null;
        }
    }

    /////////////////////////////////// delegating methods to List<Home> homes field ///////////////////////////////////

    @Override
    @NonNull
    public Iterator<Home> iterator() {
        return homes.iterator();
    }

    public Stream<Home> stream() {
        return homes.stream();
    }

    public Stream<Home> parallelStream() {
        return homes.parallelStream();
    }

    @Override
    public void forEach(@NonNull Consumer<? super Home> action) {
        homes.forEach(action);
    }

    @Override
    public Spliterator<Home> spliterator() {
        return homes.spliterator();
    }

    public int amount() {
        return homes.size();
    }
}
