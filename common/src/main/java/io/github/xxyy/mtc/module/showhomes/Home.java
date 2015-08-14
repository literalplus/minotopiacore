package io.github.xxyy.mtc.module.showhomes;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.VisibilityManager;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import lombok.NonNull;
import org.bukkit.Location;

import java.util.Set;
import java.util.UUID;

/**
 * Class representing an Essentials home.
 * @author Janmm14
 */
public class Home {

    private final EssentialsDataUser essentialsDataUser;
    private final Location location;
    private final String name;
    private Hologram hologram;

    public Home(EssentialsDataUser essentialsDataUser, Location location, String name) {
        this.essentialsDataUser = essentialsDataUser;
        this.location = location;
        this.name = name;
    }

    public void showHologram(@NonNull ShowHomesModule module, UUID executor) {
        try {
            showHologram(module);
            module.getHolosByExecutingUser().get(executor).add(this);
        } catch (Exception ex) {
            module.handleException(new Exception("Home#showHologram(module, executor);this:" + this, ex));
        }
    }

    public void showHologram(@NonNull ShowHomesModule module) {
        Location loc = location.clone().add(0, 1.2, 0);
        hologram = HologramsAPI.createHologram(module.getPlugin(), loc);

        Set<UUID> plrsToShow = ShowHomesModule.getPlayersWithShowHomesPermission();

        //set visibility - needs to be first (see that plugins doc)
        VisibilityManager visibilityManager = hologram.getVisibilityManager();
        plrsToShow.stream()
                .map(module.getPlugin().getServer()::getPlayer)
                .forEach(visibilityManager::showTo);
        visibilityManager.setVisibleByDefault(false); // needs to be after setting who can see

        // set touch handler
        TextLine homeNameLine = hologram.appendTextLine("§6Home §c" + name);
        homeNameLine.setTouchHandler(new HomeInfoTouchHandler(this, plrsToShow));

        TextLine homeOwnerLine = hologram.appendTextLine("§6by §c" + essentialsDataUser.getLastName());
        homeOwnerLine.setTouchHandler(homeNameLine.getTouchHandler());

        TextLine uuidLine = hologram.appendTextLine("§6UUID: " + essentialsDataUser.getUuid());
        uuidLine.setTouchHandler(homeNameLine.getTouchHandler());
    }

    /**
     * Deletes the hologram of this home if it was spawned.
     * @return whether the hologram was shown before
     */
    public boolean hideHologram() {
        if (hologram == null) {
            return false;
        }
        hologram.delete();
        hologram = null;
        return true;
    }

    public EssentialsDataUser getEssentialsDataUser() {
        return this.essentialsDataUser;
    }

    public Location getLocation() {
        return this.location;
    }

    public String getName() {
        return this.name;
    }

    public Hologram getHologram() {
        return this.hologram;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Home)) return false;
        final Home other = (Home) o;
        final Object otherName = other.getName();
        return !(this.name == null ? otherName != null : !this.name.equals(otherName));
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + (this.name == null ? 0 : this.name.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "io.github.xxyy.mtc.module.showhomes.Home(essentialsDataUser=" + this.essentialsDataUser + ", location=" + this.location + ", name=" + this.name + ")";
    }
}
