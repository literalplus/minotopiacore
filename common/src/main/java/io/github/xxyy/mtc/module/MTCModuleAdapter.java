/*
 * Copyright (c) 2013-2014.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module;

import com.google.common.collect.ImmutableList;

import io.github.xxyy.mtc.MTC;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Abstract base class for MTC modules.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 22.8.14
 */
public abstract class MTCModuleAdapter implements MTCModule {
    private static Map<String, MTCModuleAdapter> instances = new HashMap<>();
    private final String name;
    protected MTC plugin;
    protected boolean enabledByDefault = true;

    protected MTCModuleAdapter(String name) {
        this.name = name;
        instances.put(name, this);
    }

    @Override
    public void enable(MTC plugin) {
        this.plugin = plugin;
    }

    @Override
    public void disable(MTC plugin) {

    }

    @Override
    public void clearCache(boolean forced, MTC plugin) {

    }

    @Override
    public void reload(MTC plugin) {

    }

    @Override
    public boolean isEnabled(MTC plugin) {
        plugin.getConfig().addDefault("enable." + name, enabledByDefault);
        return plugin.getConfig().getBoolean("enable." + name);
    }

    @Override
    public String getName() {
        return name;
    }

    public MTC getPlugin() {
        return plugin;
    }

    public static MTCModuleAdapter getInstance(String name) {
        return instances.get(name);
    }

    public static Collection<MTCModule> getInstances() {
        return ImmutableList.copyOf(instances.values());
    }

    public static void forEach(Consumer<MTCModule> consumer) {
        instances.values().forEach(consumer);
    }
}
