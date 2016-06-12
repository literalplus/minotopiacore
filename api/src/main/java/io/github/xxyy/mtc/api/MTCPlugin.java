/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.api;

import org.bukkit.plugin.Plugin;

import io.github.xxyy.common.sql.SpigotSql;
import io.github.xxyy.common.xyplugin.XyPluggable;
import io.github.xxyy.mtc.api.module.ModuleManager;
import io.github.xxyy.mtc.api.module.inject.Injectable;

/**
 * A plugin instance used by MTC to interface with the Bukkit API.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2015-12-30
 */
public interface MTCPlugin extends Plugin, XyPluggable, Injectable {

    /**
     * @return the module manager used by this plugin
     */
    ModuleManager getModuleManager();

    /**
     * @return the database manager used by this plugin
     */
    SpigotSql getSql();
}
