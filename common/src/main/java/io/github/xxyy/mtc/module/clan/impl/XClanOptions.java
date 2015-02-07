/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.clan.impl;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import io.github.xxyy.mtc.module.clan.api.Clan;
import io.github.xxyy.mtc.module.clan.api.ClanOptions;
import io.github.xxyy.mtc.module.clan.api.MongoStoreable;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

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

    private final Clan clan;
    private DBObject mongo;
    private double cofferBalance;
    private int level;
    private int kills;
    private int deaths;

    public XClanOptions(Clan clan) {
        this.clan = clan;
    }

    @Override
    public Clan getClan() {
        return clan;
    }

    @Override
    public Object get(String key) {
        return mongo.get(key);
    }

    @Override
    public <T> T get(String key, Class<T> clazz) throws ClassCastException {
        Object obj = get(key);
        if (obj != null && !clazz.isAssignableFrom(obj.getClass())) {
            throw new ClassCastException("Expected " + key + " to be " + clazz.getName() + ", but was " + obj.getClass() + "!");
        }
        //noinspection unchecked
        return (T) obj;
    }

    @Override
    public <T> T getOrDefault(String key, Class<T> clazz, T def) {
        return isSet(key) ? get(key, clazz) : def;
    }

    @Override
    public Object getOrDefault(String key, Object def) {
        return isSet(key) ? get(key) : def;
    }

    @Override
    public boolean isSet(String key) {
        return mongo.containsField(key);
    }

    @Override
    public void put(String key, Object value) {
        mongo.put(key, value);
    }

    @Override
    public int getInt(String key) {
        return getIfIs(key, Integer.class, 0);
    }

    @Override
    public <T> T getIfIs(String key, Class<T> clazz, T def) { //Hmmmmm maybe we should move this to a utility class or so, although that would probably be implemented as a Mongo wrapper.
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
    public <T> void putIf(String key, T value, Function<T, Boolean> checker) {
        if (checker.apply(value)) {
            mongo.put(key, value);
        }
    }

    public Map<String, Object> asMap() {
        return Collections.unmodifiableMap((BasicDBObject) mongo); //fuk teh polis
    }


    @Override
    public DBObject asMongo() {
        Function<Double, Boolean> doubleNotZero = d -> d != 0d;
        Function<Integer, Boolean> intNotZero = d -> d != 0;
        putIf(COFFER_BALANCE_PATH, cofferBalance, doubleNotZero);
        putIf(LEVEL_PATH, level, intNotZero);
        putIf(KILLS_PATH, kills, intNotZero);
        putIf(DEATHS_PATH, deaths, intNotZero);
        return mongo;
    }

    @Override
    public void fromMongo(DBObject dbObject) {
        mongo = dbObject;

        cofferBalance = getIfIs("bank", Double.class, 0d);
        level = getInt("level");
        kills = getInt("kills");
        deaths = getInt("deaths");
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
}
