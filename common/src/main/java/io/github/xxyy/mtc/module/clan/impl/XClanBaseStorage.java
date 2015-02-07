/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.clan.impl;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
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
        return Collections.unmodifiableMap(bases);
    }

    @Override
    public void setBase(String key, Location location) {
        bases.put(key, location);
    }

    @Override
    public boolean deleteBase(String key) {
        return bases.remove(key) != null;
    }

    @Override
    public Clan getClan() {
        return clan;
    }


    @Override
    public DBObject asMongo() {
        return bases.entrySet().stream()
                .map(this::serialiseLocation)
                .collect(Collectors.toCollection(BasicDBList::new));
    }

    @Override
    public void fromMongo(DBObject dbObject) {
        if (!(dbObject instanceof BasicDBList)) {
            clan.getManager().getModule().getPlugin().getLogger().info("Invalid base meta for clan " + clan.getId() + " - ignoring!");
            return;
        }

        ((BasicDBList) dbObject).stream()
                .filter(this::isValidLocation)
                .map(obj -> (DBObject) obj)
                .forEach(obj -> bases.put(obj.get("name").toString(), deserialiseLocation(obj)));
    }


    private DBObject serialiseLocation(Map.Entry<String, Location> entry) {
        Location loc = entry.getValue();
        return new BasicDBObject("name", entry.getKey())
                .append("x", loc.getX())
                .append("y", loc.getY())
                .append("z", loc.getZ())
                .append("pitch", loc.getPitch())
                .append("yaw", loc.getYaw())
                .append("world", loc.getWorld().getName());
    }

    private Location deserialiseLocation(DBObject dbObject) { //assumes #isValidLocation(obj) has already been checked
        World world = Bukkit.getWorld(dbObject.get("world").toString());
        Location loc = new XyLocation(world, (Double) dbObject.get("x"), (Double) dbObject.get("y"), (Double) dbObject.get("z"));
        if (allAre(dbObject, Float.class, "pitch", "yaw")) {
            loc.setPitch((Float) dbObject.get("pitch"));
            loc.setYaw((Float) dbObject.get("yaw"));
        }
        return loc;
    }

    private boolean isValidLocation(Object obj) { //checks if an object is a valid serialised location
        return obj instanceof DBObject &&
                containsAll(((DBObject) obj), "world", "_id") &&
                allAre(((DBObject) obj), Double.class, "x", "y", "z") &&
                Bukkit.getWorld(((DBObject) obj).get("world").toString()) != null;
    }

    private boolean allAre(DBObject dbObject, Class<?> clazz, String... keys) {
        return containsAll(dbObject, keys) &&
                Arrays.stream(keys)
                        .map(dbObject::get)
                        .filter(Objects::nonNull)
                        .map(Object::getClass)
                        .allMatch(clazz::isAssignableFrom);
    }

    private boolean containsAll(DBObject dbObject, String... keys) {
        return Arrays.stream(keys).allMatch(dbObject::containsField);
    }
}
