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

package li.l1t.mtc.module.stack;

import com.google.common.base.Preconditions;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Specifies a type of item by its material, id or data value.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-08-28
 */
@SerializableAs("itemspec")
public class ItemSpec implements ConfigurationSerializable {
    private final Material material;
    private final short damage;

    ItemSpec(Material material, short damage) {
        this.material = Preconditions.checkNotNull(material, "material");
        this.damage = damage;
    }

    @Override
    public Map<String, Object> serialize() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("spec", toString());
        return result;
    }

    @Override
    public String toString() {
        if (damage >= 0) {
            return material.name() + ":" + damage;
        } else {
            return material.name();
        }
    }

    public static ItemSpec fromString(String spec) {
        Preconditions.checkNotNull(spec, "spec");
        Material material;
        short damage = -1;
        if (!spec.contains(":")) {
            material = Material.matchMaterial(spec);
        } else {
            String[] splitSpec = spec.split(":", 2);
            material = Material.matchMaterial(splitSpec[0]);
            damage = Short.valueOf(splitSpec[1]);
        }
        return new ItemSpec(material, damage);
    }

    public static ItemSpec deserialize(Map<String, Object> source) {
        Preconditions.checkNotNull(source, "source");
        Preconditions.checkArgument(source.containsKey("spec"), "source must contain spec key");
        Object specObj = source.get("spec");
        Preconditions.checkArgument(specObj instanceof String, "spec must be string");
        return fromString((String) specObj);
    }

    public boolean matches(ItemStack stack) {
        //noinspection SimplifiableIfStatement
        if (damage >= 0 && stack.getDurability() != damage) {
            return false;
        }
        return material.equals(stack.getType());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemSpec itemSpec = (ItemSpec) o;
        return damage == itemSpec.damage && material == itemSpec.material;

    }

    @Override
    public int hashCode() {
        int result = material.hashCode();
        result = 31 * result + (int) damage;
        return result;
    }
}
