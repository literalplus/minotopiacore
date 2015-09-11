package io.github.xxyy.mtc.module.showhomes;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.VisibilityManager;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import io.github.xxyy.common.util.LocationHelper;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.UUID;

/**
 * Class representing an Essentials home.
 *
 * @author Janmm14
 */
public class Home {
    @NotNull
    private final EssentialsPlayerData essentialsPlayerData;
    @NotNull
    private Location location;
    @NotNull
    private final String name;
    @Nullable
    private Hologram hologram;

    private boolean modified = false;

    public Home(@NotNull EssentialsPlayerData essentialsPlayerData, @NotNull Location location, @NotNull String name) {
        this.essentialsPlayerData = essentialsPlayerData;
        this.location = location;
        this.name = name;
    }

    public void showHologram(@NonNull ShowHomesModule module, @NotNull UUID executor) {
        showHologram(module);
        module.getHolosByExecutingUser().get(executor).add(this);
    }

    public void showHologram(@NonNull ShowHomesModule module) {
        Location loc = location.clone().add(0, 1.2, 0);
        hologram = HologramsAPI.createHologram(module.getPlugin(), loc);

        Set<UUID> plrsToShow = ShowHomesModule.getPermittedPlayerUUIDs();

        //set visibility - needs to be first (see that plugins doc)
        VisibilityManager visibilityManager = hologram.getVisibilityManager();
        plrsToShow.stream()
                .map(module.getPlugin().getServer()::getPlayer)
                .forEach(visibilityManager::showTo);
        visibilityManager.setVisibleByDefault(false); // needs to be after setting who can see

        // set touch handler
        TextLine homeNameLine = hologram.appendTextLine("§6Home §c" + name);
        homeNameLine.setTouchHandler(new HomeInfoTouchHandler(this, plrsToShow));

        TextLine homeOwnerLine = hologram.appendTextLine("§6by §c" + essentialsPlayerData.getLastName());
        homeOwnerLine.setTouchHandler(homeNameLine.getTouchHandler());

        TextLine uuidLine = hologram.appendTextLine("§6UUID: " + essentialsPlayerData.getUuid());
        uuidLine.setTouchHandler(homeNameLine.getTouchHandler());
    }

    /**
     * Deletes the hologram of this home if it was spawned.
     *
     * @return whether the hologram was shown before
     */
    public boolean hideHologram() {
        if (hologram == null) {
            return false;
        } else if (hologram.isDeleted()) {
            hologram = null;
            return false;
        }
        hologram.delete();
        hologram = null;
        return true;
    }

    public void setLocation(@NotNull Location loc) {
        if (location.equals(loc)) { //prevents unneccessary clone call, TODO Does that ever practically occur?
            return;
        }
        location = loc.clone();
        modified = true;
        essentialsPlayerData.setModified(true);
    }

    public void serialize(@NotNull Configuration cfg) {
        serializeLocation(cfg.getConfigurationSection("homes." + name), location);
    }

    //TODO should this be a method in LocationHelper? (as it can already deserialize this format)
    private static void serializeLocation(@NotNull ConfigurationSection section, @NotNull Location location) {
        section.set("world", location.getWorld().getName());
        section.set("x", location.getX());
        section.set("y", location.getY());
        section.set("z", location.getZ());
        section.set("yaw", location.getYaw());
        section.set("pitch", location.getPitch());
    }

    public static Home deserialize(@NotNull EssentialsPlayerData essentialsPlayerData, @NotNull ConfigurationSection section) {
        String name = section.getName();

        Location loc = LocationHelper.fromConfiguration(section);
        return new Home(essentialsPlayerData, loc, name);
    }

    //------------------ getters, equals, hashCode, toString ------------------
    @NotNull
    public EssentialsPlayerData getEssentialsPlayerData() {
        return this.essentialsPlayerData;
    }

    @NotNull
    public Location getLocation() {
        return this.location.clone();
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    @Nullable
    public Hologram getHologram() {
        return this.hologram;
    }

    public boolean isModified() {
        return modified;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Home)) return false;
        final Home other = (Home) o;
        final Object otherName = other.getName();
        return this.name.equals(otherName);
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + (this.name.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "showhomes.Home(essentialsDataUser=" + this.essentialsPlayerData + ", location=" + this.location + ", name=" + this.name + ")";
    }


}
