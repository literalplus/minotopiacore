/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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
 * Handles integration of Lanatus with PermissionsEx, a permissions system for Bukkit. Also offers a
 * limited way to convert existing PermissionsEx data into Lanatus data.
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
        registerListener(new PostPurchaseRankApplier(pexProductRepository));
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
