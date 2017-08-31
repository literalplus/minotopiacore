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

package li.l1t.mtc.module.lanatus.perk.service;

import li.l1t.mtc.api.chat.MessageType;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.module.lanatus.perk.LocalPerkManager;
import li.l1t.mtc.module.lanatus.perk.api.Perk;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * Applies enabled perks to players, sending an additional notification message.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-06
 */
public class PerkApplyService {
    private final LocalPerkManager manager;

    @InjectMe
    public PerkApplyService(LocalPerkManager manager) {
        this.manager = manager;
    }

    public void removeAllPerks(Player player) {
        manager.removeAll(player);
    }

    public void applyEnabledPerks(Player player) {
        Collection<Perk> enabledPerks = manager.applyEnabled(player);
        if (!enabledPerks.isEmpty()) {
            notifyEnabledPerks(player, enabledPerks);
        }
    }

    private void notifyEnabledPerks(Player player, Collection<Perk> enabledPerks) {
        MessageType.RESULT_LINE_SUCCESS.sendTo(player, "§p%d §aPerks aktiviert.", enabledPerks.size());
    }
}
