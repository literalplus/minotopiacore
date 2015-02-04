/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.clan;

import io.github.xxyy.mtc.misc.ClearCacheBehaviour;
import io.github.xxyy.mtc.module.ConfigurableMTCModule;

/**
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 04/02/15
 */
public class ClanModule extends ConfigurableMTCModule {
    public static final String NAME = "Clan";
    public static final int BASE_CLAN_MEMBER_LIMIT = 10;

    public ClanModule() {
        super(NAME, "modules/clan/config.yml", ClearCacheBehaviour.RELOAD);
    }

    @Override
    protected void reloadImpl() {

    }
}
