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

package li.l1t.mtc.module.villagertradepermission;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Villager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * This class defines how to determine villagers and which permission is required for trading with
 * villagrs matching the criteria. <br><br> Criterias to identitfy villagers (all have to match)
 * <ul> <li>Display name</li> <li>Location</li> <li>Profession</li> </ul>
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
    @Nonnull
    private final Location location;
    @Nonnull
    private final Villager.Profession profession;
    @Nullable
    private final String displayName;
    @Nullable
    private String permission = null;

    public VillagerInfo(@Nonnull Location location, @Nonnull Villager.Profession profession, @Nullable String displayName) {
        this.location = normaliseLocation(location);
        this.profession = profession;
        this.displayName = displayName;
    }

    public VillagerInfo(@Nonnull Location location, @Nonnull Villager.Profession profession, @Nullable String displayName, @Nullable String permission) {
        this.location = normaliseLocation(location);
        this.profession = profession;
        this.displayName = displayName;
        this.permission = permission;
    }

    /**
     * @return A newly created clone of this VillagerInfo's location
     */
    @Nonnull
    public Location getLocation() {
        return location.clone();
    }

    @Nonnull
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
    public boolean matches(@Nonnull Villager villager) {
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
     * This creates a new VillagerInfo by the data from the provided Villager, not taking existing
     * object of that villager into account
     *
     * @param villager the villager to get the data from
     * @return A newly created VillagerInfo object
     * @see VillagerTradePermissionModule#findVillagerInfo(Villager)
     */
    public static VillagerInfo forEntity(@Nonnull Villager villager) {
        return new VillagerInfo(villager.getLocation(), villager.getProfession(), villager.getCustomName());
    }

    @SuppressWarnings("SimplifiableIfStatement")
    //cause it actually makes the code nearly unreadable
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
