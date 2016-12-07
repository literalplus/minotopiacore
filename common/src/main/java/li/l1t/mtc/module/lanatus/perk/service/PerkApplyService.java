/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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
