/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.clan;

import com.mongodb.MongoClient;

import io.github.xxyy.mtc.MTC;
import io.github.xxyy.mtc.misc.ClearCacheBehaviour;
import io.github.xxyy.mtc.module.ConfigurableMTCModule;
import io.github.xxyy.mtc.module.clan.impl.XClanManager;

/**
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 04/02/15
 */
public class ClanModule extends ConfigurableMTCModule {
    public static final int BASE_CLAN_MEMBER_LIMIT = 10;
    public static final String NAME = "Clan";
    private MongoClient mongo;
    private XClanManager manager;

    public ClanModule() {
        super(NAME, "modules/clan/config.yml", ClearCacheBehaviour.RELOAD);
    }

    @Override
    public void enable(MTC plugin) throws Exception {
        super.enable(plugin);

        mongo = new MongoClient(configuration.getString("mongo.url"));

        manager = new XClanManager(this);
    }

    @Override
    public void disable(MTC plugin) {
        super.disable(plugin);
        mongo.close();
    }

    @Override
    protected void reloadImpl() {
        configuration.addDefault("mongo.url", "mongodb://localhost:27017");
    }

    public MongoClient getMongo() {
        return mongo;
    }

    public XClanManager getManager() {
        return manager;
    }
}
