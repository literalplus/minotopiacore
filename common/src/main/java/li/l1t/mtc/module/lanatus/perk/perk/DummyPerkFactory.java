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

package li.l1t.mtc.module.lanatus.perk.perk;

import li.l1t.mtc.api.chat.MessageType;
import li.l1t.mtc.module.lanatus.perk.api.Perk;
import li.l1t.mtc.module.lanatus.perk.api.PerkFactory;
import li.l1t.mtc.module.lanatus.perk.repository.PerkMeta;
import org.bukkit.entity.Player;

/**
 * A factory for dummy perks which just send an error message stating that the perk type is unknown.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-06
 */
public class DummyPerkFactory implements PerkFactory {
    public static final DummyPerkFactory INSTANCE = new DummyPerkFactory();

    private DummyPerkFactory() {

    }

    @Override
    public Perk createPerk(PerkMeta meta) {
        return new AbstractPerk(meta.getProductId()) {
            @Override
            public void applyTo(Player player) {
                MessageType.WARNING.sendTo(player,
                        "Unbekannter Perk %s. Bitte wende dich an den Support und gib die Produkt-ID '%s' an.",
                        meta.getType(), getProductId().toString()
                );
            }

            @Override
            public void removeFrom(Player player) {
                //no-op
            }
        };
    }
}
