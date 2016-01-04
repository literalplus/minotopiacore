package io.github.xxyy.mtc.module.villagertradepermission;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Villager;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class VillagerInfo implements ConfigurationSerializable {
    private static final String PROFESSION_PATH = "profession";
    private static final String DISPLAY_NAME_PATH = "displayName";
    private static final String LOCATION_PATH = "location";
    private static final String PERMISSION_PATH = "permission";

    @NotNull
    private final Location location;
    @NotNull
    private final Villager.Profession profession;
    @Nullable
    private final String displayName;
    @Nullable
    private String permission = null;

    public VillagerInfo(@NotNull Location location, @NotNull Villager.Profession profession, @Nullable String displayName) {
        this.location = location;
        this.profession = profession;
        this.displayName = displayName;
    }

    public VillagerInfo(@NotNull Location location, @NotNull Villager.Profession profession, @Nullable String displayName, @Nullable String permission) {
        this.location = location;
        this.profession = profession;
        this.displayName = displayName;
        this.permission = permission;
    }

    @NotNull
    public Location getLocation() {
        return location;
    }

    @NotNull
    public Villager.Profession getProfession() {
        return profession;
    }

    @Nullable
    public String getDisplayName() {
        return displayName;
    }

    @Nullable
    public String getPermission() {
        return permission;
    }

    public void setPermission(@Nullable String permission) {
        this.permission = permission;
    }

    @SuppressWarnings("RedundantIfStatement")
    public boolean matches(@NotNull Villager villager) {
        if (villager.getProfession() != profession) {
            return false;
        }
        if (villager.getLocation() != location) {
            return false;
        }
        if (!Objects.equals(displayName, villager.getCustomName())) {
            return false;
        }
        return true;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put(PROFESSION_PATH, profession.name());
        map.put(DISPLAY_NAME_PATH, displayName);
        map.put(LOCATION_PATH, location);
        map.put(PERMISSION_PATH, permission);
        return map;
    }

    @SuppressWarnings("unused")
    public static VillagerInfo deserialize(Map<String, Object> map) {
        String professionString = (String) map.get(PROFESSION_PATH);
        Villager.Profession profession = Villager.Profession.valueOf(professionString);
        String displayName = (String) map.get(DISPLAY_NAME_PATH);
        Location location = (Location) map.get(LOCATION_PATH);
        String permission = (String) map.get(PERMISSION_PATH);
        return new VillagerInfo(location, profession, displayName);
    }

    public static VillagerInfo createNewBy(@NotNull Villager villager) {
        return new VillagerInfo(villager.getLocation(), villager.getProfession(), villager.getCustomName());
    }

    @SuppressWarnings("SimplifiableIfStatement") //cause it actually makes the code nearly unreadable
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VillagerInfo)) return false;

        VillagerInfo that = (VillagerInfo) o;

        if (!location.equals(that.location)) return false;
        if (profession != that.profession) return false;
        return Objects.equals(displayName, that.getDisplayName());
    }

    @Override
    public int hashCode() {
        int result = location.hashCode();
        result = 97 * result + profession.hashCode();
        result = 97 * result + (displayName != null ? displayName.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "VillagerInfo{" +
            "location=" + location +
            ", profession=" + profession +
            ", displayName='" + displayName + '\'' +
            ", permission='" + permission + '\'' +
            '}';
    }
}
