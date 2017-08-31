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

package li.l1t.mtc.module.fulltag.model;

import com.google.common.base.Preconditions;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;

import javax.annotation.Nonnull;
import java.util.Arrays;

/**
 * Represents a type of item that may be fully enchanted.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2015-08-29
 */
public enum FullPart {
    CHESTPLATE(Material.DIAMOND_CHESTPLATE, "Chest"),
    LEGGINGS(Material.DIAMOND_LEGGINGS, "Hose"),
    BOOTS(Material.DIAMOND_BOOTS, "Schuhe"),
    HELMET(Material.DIAMOND_HELMET, "Helm"),
    SWORD(Material.DIAMOND_SWORD, "Schwert"),
    BOW(Material.BOW, "Bogen");

    private final Material material;
    private final String alias;

    FullPart(Material material, String alias) {
        this.material = material;
        this.alias = alias;
    }

    /**
     * Attempts to find a full part from an input string. Input may be the material name or the enum
     * constant id. Additionally, if any enum constant name starts with the input, that is returned.
     * (the first one) Casing is ignored.
     *
     * @param input the input to find a part with
     * @return the discovered part or null if there is no such part
     */
    public static FullPart fromString(@Nonnull String input) {
        Preconditions.checkNotNull(input, "input");
        FullPart part = Arrays.stream(FullPart.values())
                .filter(n -> input.equalsIgnoreCase(n.getMaterial().name()) ||
                        n.name().toLowerCase().startsWith(input.toLowerCase()) ||
                        n.getAlias().toLowerCase().startsWith(input.toLowerCase()))
                .findFirst().orElse(null);

        if (part == null && StringUtils.isNumeric(input)) {
            int partId = Integer.parseInt(input);
            if (partId > 0 && partId < FullPart.values().length) {
                part = FullPart.values()[partId];
            }
        }

        return part;
    }

    /**
     * @return the material represented by this part
     */
    public Material getMaterial() {
        return material;
    }

    /**
     * @return the human-readable alias of this part
     */
    public String getAlias() {
        return alias;
    }
}
