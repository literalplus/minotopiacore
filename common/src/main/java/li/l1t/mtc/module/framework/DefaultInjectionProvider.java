/*
 * MinoTopiaCore
 * Copyright (C) 2013 - 2017 Philipp Nowak (https://github.com/xxyy) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package li.l1t.mtc.module.framework;

import li.l1t.common.sql.SafeSql;
import li.l1t.common.sql.sane.SaneSql;
import li.l1t.mtc.MTC;
import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.api.PlayerGameManager;
import li.l1t.mtc.api.module.ModuleManager;
import li.l1t.mtc.api.module.inject.Injector;
import li.l1t.mtc.hook.VaultHook;
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
        injector.registerInstance(plugin.getVaultHook(), VaultHook.class);
        injector.registerInstance(plugin.getGameManager(), PlayerGameManager.class);
    }
}
