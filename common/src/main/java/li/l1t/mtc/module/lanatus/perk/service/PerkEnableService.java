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

import li.l1t.lanatus.api.product.Product;
import li.l1t.mtc.api.chat.MessageType;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.module.lanatus.base.MTCLanatusClient;
import li.l1t.mtc.module.lanatus.perk.LocalPerkManager;
import li.l1t.mtc.module.lanatus.perk.PerksConfig;
import li.l1t.mtc.module.lanatus.perk.api.Perk;
import li.l1t.mtc.module.lanatus.perk.api.PerkRepository;
import li.l1t.mtc.module.lanatus.perk.repository.PerkMeta;
import li.l1t.mtc.module.lanatus.perk.repository.SqlPerkRepository;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * Enables and disables perks, sending status messages to the player.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-06
 */
public class PerkEnableService {
    private final PerksConfig config;
    private final PerkRepository repository;
    private final LocalPerkManager manager;
    private final MTCLanatusClient lanatus;

    @InjectMe
    public PerkEnableService(PerksConfig config, SqlPerkRepository repository, LocalPerkManager manager, MTCLanatusClient lanatus) {
        this.config = config;
        this.repository = repository;
        this.manager = manager;
        this.lanatus = lanatus;
    }

    public boolean isPerkEnabled(Player player, Perk perk) {
        return repository.isPerkEnabled(player.getUniqueId(), perk.getProductId());
    }

    public boolean enablePerk(Player player, Perk perk) {
        Product product = lanatus.products().findById(perk.getProductId());
        if (!repository.isPerkAvailable(player.getUniqueId(), perk.getProductId())) {
            MessageType.USER_ERROR.sendTo(player, "Diesen Perk besitzt du nicht. (%s§c)", product.getDisplayName());
            return false;
        }
        Collection<PerkMeta> enabled = repository.findEnabledByPlayerId(player.getUniqueId());
        if (enabled.size() >= config.getConcurrentPerkLimit()) {
            MessageType.USER_ERROR.sendTo(player, "Du kannst nicht mehr als %d Perks gleichzeitig aktiviert haben.", config.getConcurrentPerkLimit());
            return false;
        }
        repository.enablePlayerPerk(player.getUniqueId(), perk.getProductId());
        MessageType.RESULT_LINE_SUCCESS.sendTo(player, "Perk §p%s§a aktiviert.", product.getDisplayName());
        manager.reapplyAll(player);
        return true;
    }

    public void disablePerk(Player player, Perk perk) {
        Product product = lanatus.products().findById(perk.getProductId());
        repository.disablePlayerPerk(player.getUniqueId(), perk.getProductId());
        MessageType.RESULT_LINE_SUCCESS.sendTo(player, "Perk §p%s§a deaktiviert.", product.getDisplayName());
        manager.reapplyAll(player);
    }

    public void togglePerk(Player player, Perk perk) {
        if (isPerkEnabled(player, perk)) {
            disablePerk(player, perk);
        } else {
            enablePerk(player, perk);
        }
    }
}
