/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.clan.impl;

import org.bson.BsonDocument;
import org.bson.BsonDouble;
import org.bson.BsonInt32;
import org.bson.BsonValue;

import io.github.xxyy.mtc.module.clan.api.ClanOptions;
import io.github.xxyy.mtc.module.clan.api.MongoStoreable;

import java.util.Collections;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Stores some metadata related to clans, such as stats, coffer balance, etc.
 * Also offers to store arbitrary data as Strings as well.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 06/02/15
 */
public class XClanOptions implements ClanOptions, MongoStoreable {
    private static final String COFFER_BALANCE_PATH = "bank";
    private static final String LEVEL_PATH = "level";
    private static final String KILLS_PATH = "kills";
    private static final String DEATHS_PATH = "deaths";

    private final XClan clan;
    private boolean dirty = false;

    private BsonDocument mongo;
    private double cofferBalance;
    private int level;
    private int kills;
    private int deaths;

    public XClanOptions(XClan clan) {
        this.clan = clan;
    }

    @Override
    public XClan getClan() {
        return clan;
    }

    @Override
    public BsonDocument asBsonDocument() {
        return mongo; //yolo
    }

    @Override
    public BsonValue get(String key) {
        return mongo.get(key);
    }

    @Override
    public BsonValue getOrDefault(String key, BsonValue def) {
        return isSet(key) ? get(key) : def;
    }

    @Override
    public boolean isSet(String key) {
        return mongo.containsKey(key);
    }

    @Override
    public void put(String key, BsonValue value) {
        mongo.put(key, value);
        setDirty(true);
    }

    @Override
    public int getInt(String key) {
        if (!mongo.containsKey(key)) {
            return 0;
        }
        BsonValue val = mongo.get(key);
        if (val == null || !val.isInt32()) {
            return 0;
        } else {
            return val.asInt32().intValue();
        }
    }

    @Override
    public <T extends BsonValue> T getIfIs(String key, Class<T> clazz, T def) { //Hmmmmm maybe we should move this to a utility class or so, although that would probably be implemented as a Mongo wrapper.
        if (isSet(key)) {
            Object val = get(key);
            if (val == null || clazz.isAssignableFrom(val.getClass())) {
                //noinspection unchecked
                return (T) val;
            }
        }
        return def;
    }

    @Override
    public <T extends BsonValue> void putIf(String key, T value, Predicate<T> predicate) {
        if (predicate.test(value)) {
            put(key, value);
        }
    }

    public Map<String, Object> asMap() {
        return Collections.unmodifiableMap(mongo); //fuk teh polis
    }


    @Override
    public BsonValue asMongo() {
        Predicate<BsonDouble> doubleNotZero = d -> d.doubleValue() != 0d;
        Predicate<BsonInt32> intNotZero = d -> d.intValue() != 0;
        putIf(COFFER_BALANCE_PATH, new BsonDouble(cofferBalance), doubleNotZero);
        putIf(LEVEL_PATH, new BsonInt32(level), intNotZero);
        putIf(KILLS_PATH, new BsonInt32(kills), intNotZero);
        putIf(DEATHS_PATH, new BsonInt32(deaths), intNotZero);
        setDirty(false);
        return mongo;
    }

    @Override
    public void fromMongo(BsonValue dbObject) {
        mongo = dbObject.asDocument();

        cofferBalance = getOrDefault("bank", new BsonDouble(0d)).asDouble().doubleValue();
        level = getInt("level");
        kills = getInt("kills");
        deaths = getInt("deaths");
        setDirty(false);
    }

    ///////////////////////////////// STANDARDISED VALUES //////////////////////////////////////////////////////////////


    public double getCofferBalance() {
        return cofferBalance;
    }

    public void setCofferBalance(double cofferBalance) {
        this.cofferBalance = cofferBalance;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    public boolean setDirty(boolean dirty) {
        this.dirty = dirty;
        clan.setDirty(dirty || clan.isDirty());
        return this.dirty;
    }
}
