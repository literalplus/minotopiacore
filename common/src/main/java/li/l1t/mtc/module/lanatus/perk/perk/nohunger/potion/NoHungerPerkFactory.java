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

package li.l1t.mtc.module.lanatus.perk.perk.nohunger.potion;

import com.google.common.base.Preconditions;
import li.l1t.mtc.module.lanatus.perk.api.Perk;
import li.l1t.mtc.module.lanatus.perk.api.PerkFactory;
import li.l1t.mtc.module.lanatus.perk.repository.PerkMeta;
import org.bukkit.plugin.Plugin;

/**
 * Creates potion perks based on data strings.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-06
 */
public class NoHungerPerkFactory implements PerkFactory {
    private final Plugin plugin;

    public NoHungerPerkFactory(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Perk createPerk(PerkMeta meta) {
        Preconditions.checkNotNull(meta, "meta");
        Preconditions.checkArgument(meta.getType().equals("nohunger"), "expected type nohunger, was: ", meta);
        return new MetadataNoHungerPerk(meta.getProductId(), plugin);
    }
}
