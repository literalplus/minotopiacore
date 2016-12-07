/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.lanatus.perk.service;

import li.l1t.lanatus.api.product.Product;
import li.l1t.mtc.api.chat.MessageType;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.module.lanatus.perk.LanatusPerkModule;
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
    private final LanatusPerkModule module;
    private final PerkRepository repository;

    @InjectMe
    public PerkEnableService(LanatusPerkModule module, SqlPerkRepository repository) {
        this.module = module;
        this.repository = repository;
    }

    public boolean enablePerk(Player player, Product product) {
        if (!repository.isPerkAvailable(player.getUniqueId(), product.getUniqueId())) {
            MessageType.USER_ERROR.sendTo(player, "Diesen Perk besitzt du nicht. (%s§c)", product.getDisplayName());
            return false;
        }
        Collection<PerkMeta> enabled = repository.findEnabledByPlayerId(player.getUniqueId());
        if (enabled.size() > module.getConcurrentPerkLimit()) {
            MessageType.USER_ERROR.sendTo(player, "Du kannst nicht mehr als %d Perks gleichzeitig aktiviert haben.", module.getConcurrentPerkLimit());
        }
        repository.enablePlayerPerk(player.getUniqueId(), product.getUniqueId());
        MessageType.RESULT_LINE_SUCCESS.sendTo(player, "Perk §p%s§a aktiviert.", product.getDisplayName());
        return true;
    }

    public void disablePerk(Player player, Product product) {
        repository.disablePlayerPerk(player.getUniqueId(), product.getUniqueId());
        MessageType.RESULT_LINE_SUCCESS.sendTo(player, "Perk §p%s§a deaktiviert.", product.getDisplayName());
    }
}
