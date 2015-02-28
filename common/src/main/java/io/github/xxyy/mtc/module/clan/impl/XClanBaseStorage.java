/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.clan.impl;

import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonDouble;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import io.github.xxyy.common.misc.XyLocation;
import io.github.xxyy.mtc.module.clan.api.Clan;
import io.github.xxyy.mtc.module.clan.api.ClanBaseStorage;
import io.github.xxyy.mtc.module.clan.api.MongoStoreable;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Stores bases of a clan.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 06/02/15
 */
public class XClanBaseStorage implements ClanBaseStorage, MongoStoreable {
    public static final String MAIN_BASE_KEY = "base";
    private final XClan clan;
    private final Map<String, Location> bases = new HashMap<>();
    private final Map<String, Location> immutableBases = Collections.unmodifiableMap(bases);
    private boolean dirty = false;

    public XClanBaseStorage(XClan clan) {
        this.clan = clan;
    }

    @Override
    public Location getMainBase() {
        return hasBase(MAIN_BASE_KEY) ?
                getBase(MAIN_BASE_KEY) :
                bases.values().stream().findFirst().orElse(null);
    }

    @Override
    public void setMainBase(Location location) {
        setBase(MAIN_BASE_KEY, location);
    }

    @Override
    public Location getBase(String key) {
        return bases.get(key);
    }

    @Override
    public boolean hasBase(String key) {
        return bases.containsKey(key);
    }

    @Override
    public Map<String, Location> getBases() {
        return immutableBases;
    }

    @Override
    public void setBase(String key, Location location) {
        bases.put(key, location);
        setDirty(true);
    }

    private boolean setDirty(boolean dirty) {
        this.dirty = dirty;
        clan.setDirty(clan.isDirty() || dirty);
        return this.dirty;
    }

    @Override
    public boolean deleteBase(String key) {
        return setDirty(bases.remove(key) != null || dirty);
    }

    @Override
    public Clan getClan() {
        return clan;
    }


    @Override
    public BsonValue asMongo() {
        setDirty(false);
        return bases.entrySet().stream()
                .map(this::serialiseLocation)
                .collect(Collectors.toCollection(BsonArray::new));
    }

    @Override
    public void fromMongo(BsonValue dbObject) {
        dbObject.asArray().stream()
                .map(BsonValue::asDocument)
                .filter(this::isValidLocation)
                .forEach(obj -> bases.put(obj.get("name").toString(), deserialiseLocation(obj)));
        setDirty(false);
    }


    private BsonValue serialiseLocation(Map.Entry<String, Location> entry) {
        Location loc = entry.getValue();
        return new BsonDocument("name", new BsonString(entry.getKey()))
                .append("x", new BsonDouble(loc.getX()))
                .append("y", new BsonDouble(loc.getY()))
                .append("z", new BsonDouble(loc.getZ()))
                .append("pitch", new BsonDouble(loc.getPitch()))
                .append("yaw", new BsonDouble(loc.getYaw()))
                .append("world", new BsonString(loc.getWorld().getName()));
    }

    private Location deserialiseLocation(BsonDocument doc) { //assumes #isValidLocation(obj) has already been checked
        World world = Bukkit.getWorld(doc.getString("world").getValue());
        Location loc = new XyLocation(world,
                doc.getDouble("x").doubleValue(),
                doc.getDouble("y").doubleValue(),
                doc.getDouble("z").doubleValue());
        if (containsAll(doc, "pitch", "yaw")) {
            loc.setPitch((float) doc.getDouble("pitch").doubleValue());
            loc.setYaw((float) doc.getDouble("yaw").doubleValue());
        }
        return loc;
    }

    private boolean isValidLocation(BsonDocument doc) { //checks if an object is a valid serialised location
        return containsAll(doc, "world", "_id") &&
                allAre(doc, Double.class, "x", "y", "z") &&
                Bukkit.getWorld(doc.get("world").toString()) != null;
    }

    private boolean allAre(BsonDocument dbObject, Class<?> clazz, String... keys) {
        return containsAll(dbObject, keys) &&
                Arrays.stream(keys)
                        .map(dbObject::get)
                        .filter(Objects::nonNull)
                        .map(Object::getClass)
                        .allMatch(clazz::isAssignableFrom);
    }

    private boolean containsAll(BsonDocument dbObject, String... keys) {
        return Arrays.stream(keys).allMatch(dbObject::containsKey);
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }
}
