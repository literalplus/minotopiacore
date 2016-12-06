/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.lanatus.perk.api;

import li.l1t.mtc.module.lanatus.perk.repository.PerkMeta;

/**
 * Creates perks based on type and metadata.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-06
 */
public interface PerkFactory {
    Perk createPerk(PerkMeta meta);
}
