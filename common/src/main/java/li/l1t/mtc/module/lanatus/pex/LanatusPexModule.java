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

package li.l1t.mtc.module.lanatus.pex;

import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.api.module.inject.ExternalDependencies;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.misc.ClearCacheBehaviour;
import li.l1t.mtc.module.ConfigurableMTCModule;
import li.l1t.mtc.module.lanatus.base.MTCLanatusClient;
import li.l1t.mtc.module.lanatus.pex.bulk.BulkMigrationCommand;
import li.l1t.mtc.module.lanatus.pex.listener.ApplicableRanksFilterListener;
import li.l1t.mtc.module.lanatus.pex.listener.PostPurchaseRankApplier;
import li.l1t.mtc.module.lanatus.pex.listener.PrePurchaseRankSanityCheckListener;
import li.l1t.mtc.module.lanatus.pex.product.SqlPexProductRepository;
import org.bukkit.configuration.ConfigurationSection;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.util.Map;

/**
 * Handles integration of Lanatus with PermissionsEx, a permissions system for Bukkit. Also offers a limited way to
 * convert existing PermissionsEx data into Lanatus data.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-10-30
 */
@ExternalDependencies("ru.tehkode")
public class LanatusPexModule extends ConfigurableMTCModule {
    public static final String LANATUS_MODULE_NAME = "mtc-lapex";
    private static final String GROUP_MAPPING_PATH = "lanatus-to-pex-group-mapping";
    private static final String ALLOW_BULK_CONVERSION_PATH = "allow-permissions-yml-conversion-do-not-touch";
    private static final String ALLOW_JOIN_CONVERSION_PATH = "allow-join-pex-to-lanatus-conversion";
    public static final String AUTORANK_OPTION_NAME = "lanatus-autorank";
    public static final String AUTOCONVERT_OPTION_NAME = "lanatus-autoconvert";
    private final GroupMapping groupMapping = new GroupMapping();
    // Note: This field is required for the module loading to fail if PEx is not present
    private PermissionManager permissionManager = PermissionsEx.getPermissionManager();
    @InjectMe(failSilently = true)
    private MTCLanatusClient lanatus;
    @InjectMe
    private SqlPexProductRepository pexProductRepository;
    private boolean allowBulkConversion = false;
    private boolean allowAutomaticConversion = false;

    public LanatusPexModule() {
        super("LanatusPex", "modules/lanatus-pex.cfg.yml", ClearCacheBehaviour.RELOAD, true);
    }

    @Override
    public void enable(MTCPlugin plugin) throws Exception {
        super.enable(plugin);
        registerListener(new GroupChangeJoinListener(groupMapping, permissionManager, lanatus));
        registerListener(new PrePurchaseRankSanityCheckListener(pexProductRepository));
        registerListener(new PostPurchaseRankApplier(pexProductRepository, getPlugin().getServer()));
        registerListener(new ApplicableRanksFilterListener(pexProductRepository));
        if (isAllowAutomaticConversion()) {
            registerListener(new GroupMigrationJoinListener(permissionManager, lanatus));
        }
        if (isAllowBulkConversion()) {
            registerCommand(new BulkMigrationCommand(this, permissionManager, lanatus), "labulkmigrate");
        }
    }

    @Override
    protected void reloadImpl() {
        configuration.options().header("Map your Lanatus groups to their PEx equivalents here.\nNote that this will only touch PEx groups with the '" + AUTORANK_OPTION_NAME + "' option set.").copyDefaults(true);
        if (!configuration.isConfigurationSection(GROUP_MAPPING_PATH)) {
            ConfigurationSection section = configuration.createSection(GROUP_MAPPING_PATH);
            section.addDefault("some-lanatus-group", "corresponding-pex-group");
        }
        configuration.addDefault(ALLOW_BULK_CONVERSION_PATH, false);
        configuration.addDefault(ALLOW_JOIN_CONVERSION_PATH, false);
        Map<String, Object> groupConfig = configuration.getConfigurationSection(GROUP_MAPPING_PATH).getValues(false);
        groupMapping.clear();
        groupConfig.entrySet().forEach(e -> groupMapping.mapLanatusToPexGroup(e.getKey(), String.valueOf(e.getValue())));
        allowBulkConversion = configuration.getBoolean(ALLOW_BULK_CONVERSION_PATH);
        allowAutomaticConversion = configuration.getBoolean(ALLOW_JOIN_CONVERSION_PATH);
        save();
    }

    @Override
    public void clearCache(boolean forced, MTCPlugin plugin) {
        if (forced) {
            pexProductRepository.clearCache();
        }
    }

    public boolean isAllowBulkConversion() {
        return allowBulkConversion;
    }

    public boolean isAllowAutomaticConversion() {
        return allowAutomaticConversion;
    }

    public void disableBulkConversion() {
        allowBulkConversion = false;
        configuration.set(ALLOW_BULK_CONVERSION_PATH, false);
        save();
    }
}
