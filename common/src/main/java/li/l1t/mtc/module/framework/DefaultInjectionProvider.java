/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.framework;

import li.l1t.common.sql.SafeSql;
import li.l1t.common.sql.sane.SaneSql;
import li.l1t.mtc.MTC;
import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.api.module.ModuleManager;
import li.l1t.mtc.api.module.inject.Injector;
import li.l1t.mtc.hook.XLoginHook;
import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Provides some default injections relevant to MTC modules.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-10-25
 */
public class DefaultInjectionProvider {
    private final MTC plugin;
    private final Injector injector;

    public DefaultInjectionProvider(MTC plugin, Injector injector) {
        this.plugin = plugin;
        this.injector = injector;
    }

    public void registerAll() {
        registerPlugin();
        registerBukkitServices();
        registerSql();
    }

    private void registerSql() {
        injector.registerInstance(plugin.sql(), SaneSql.class);
        injector.registerInstance(plugin.getSql(), SafeSql.class);
    }

    private void registerBukkitServices() {
        injector.registerInstance(plugin.getServer(), Server.class);
    }

    private void registerPlugin() {
        injector.registerInstance(plugin, MTC.class);
        injector.registerInstance(plugin, JavaPlugin.class);
        injector.registerInstance(plugin, MTCPlugin.class);
        injector.registerInstance(plugin.getModuleManager(), ModuleManager.class);
        injector.registerInstance(plugin.getXLoginHook(), XLoginHook.class);
    }
}
