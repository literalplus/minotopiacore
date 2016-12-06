/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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
