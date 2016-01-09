/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.villagertradepermission;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Villager;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * This class defines how to determine villagers and which permission is required for trading with villagrs matching the criteria.
 * <br><br>
 * Criterias to identitfy villagers (all have to match)
 * <ul>
 * <li>Display name</li>
 * <li>Location</li>
 * <li>Profession</li>
 * </ul>
 *
 * @author <a href="https://janmm14.de">Janmm14</a>
 */
public class VillagerInfo implements ConfigurationSerializable {
    private static final String PROFESSION_PATH = "profession";
    private static final String DISPLAY_NAME_PATH = "displayName";
    private static final String LOCATION_PATH = "location";
    private static final String PERMISSION_PATH = "permission";

    /**
     * This location should have its yaw and pitch set to 0
     */
    @NotNull
    private final Location location;
    @NotNull
    private final Villager.Profession profession;
    @Nullable
    private final String displayName;
    @Nullable
    private String permission = null;

    public VillagerInfo(@NotNull Location location, @NotNull Villager.Profession profession, @Nullable String displayName) {
        this.location = normaliseLocation(location);
        this.profession = profession;
        this.displayName = displayName;
    }

    public VillagerInfo(@NotNull Location location, @NotNull Villager.Profession profession, @Nullable String displayName, @Nullable String permission) {
        this.location = normaliseLocation(location);
        this.profession = profession;
        this.displayName = displayName;
        this.permission = permission;
    }

    /**
     * @return A newly created clone of this VillagerInfo's location
     */
    @NotNull
    public Location getLocation() {
        return location.clone();
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

    /**
     * Looks whether the given villager matches the criteria described {@link VillagerInfo}
     *
     * @param villager the villager to test for matching
     * @return whether the villager matches all the criterias
     */
    @SuppressWarnings("RedundantIfStatement")
    public boolean matches(@NotNull Villager villager) {
        if (villager.getProfession() != profession) {
            return false;
        }
        if (!normaliseLocationNoCopy(villager.getLocation()).equals(location)) {
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
        return new VillagerInfo(location, profession, displayName, permission);
    }

    /**
     * This creates a new VillagerInfo by the data from the provided Villager,
     * not taking existing object of that villager into account
     *
     * @param villager the villager to get the data from
     * @return A newly created VillagerInfo object
     * @see VillagerTradePermissionModule#findVillagerInfo(Villager)
     */
    public static VillagerInfo forEntity(@NotNull Villager villager) {
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

    private static Location normaliseLocation(Location location) {
        return normaliseLocationNoCopy(location.clone());
    }

    private static Location normaliseLocationNoCopy(Location loc) {
        loc.setYaw(0.0F);
        loc.setPitch(0.0F);
        return loc;
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
