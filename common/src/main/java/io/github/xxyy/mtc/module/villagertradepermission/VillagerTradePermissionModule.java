/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.villagertradepermission;

import com.google.common.base.Preconditions;
import io.github.xxyy.mtc.api.MTCPlugin;
import io.github.xxyy.mtc.api.command.CommandBehaviours;
import io.github.xxyy.mtc.misc.ClearCacheBehaviour;
import io.github.xxyy.mtc.module.ConfigurableMTCModule;
import io.github.xxyy.mtc.module.villagertradepermission.actions.ActionManager;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Villager;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A module providing permission-based access to villager trades,
 * but requires fully frozen locations of these villagers which is provided by nms.
 *
 * @author <a href="https://janmm14.de">Janmm14</a>
 */
public class VillagerTradePermissionModule extends ConfigurableMTCModule {
    public static final String COMMAND_PERMISSION = "mtc.vp.cmd";
    private static final String SHORT_NAME = "VillagerTradePermission";
    public static final String NAME = SHORT_NAME + "Module";
    private static final String VILLAGER_SPECIFICATIONS_PATH = "villagerSpecifications";
    private final ActionManager actionManager = new ActionManager(this); //must init here, or NPE in reloadImpl
    private VillagerClickListener listener;
    private Set<VillagerInfo> villagerInfos;
    private VillagerPermissionCommand villagerPermissionCommand;

    protected VillagerTradePermissionModule() {
        super(NAME, "modules/" + SHORT_NAME.toLowerCase() + "/data.yml", ClearCacheBehaviour.SAVE, false);
    }

    @Override
    public void enable(MTCPlugin plugin) throws Exception {
        super.enable(plugin);
        ConfigurationSerialization.registerClass(VillagerInfo.class);
        listener = new VillagerClickListener(this);
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
        actionManager.onEnable();
        villagerPermissionCommand = new VillagerPermissionCommand(this);
        registerCommand(villagerPermissionCommand, "villagerpermission", "vp")
                .behaviour(CommandBehaviours.permissionChecking(COMMAND_PERMISSION));
    }

    @Override
    protected void reloadImpl() {
        actionManager.clearCache();
        configuration.options().copyDefaults(true).copyHeader(true).header("If you edit this file, you need to disable mtc or shut down the server before to circumvent that changes are overwritten.");
        configuration.addDefault(VILLAGER_SPECIFICATIONS_PATH, new ArrayList<VillagerInfo>());
        villagerInfos = null;
        getVillagerInfos();
    }

    @Override
    public void disable(MTCPlugin plugin) {
        HandlerList.unregisterAll(listener);
        actionManager.disable();
        villagerInfos.clear();
        save();
        ConfigurationSerialization.unregisterClass(VillagerInfo.class);
    }

    @Nullable
    public VillagerInfo findVillagerInfo(@NotNull Villager villager) {
        Preconditions.checkNotNull(villager, "villager");

        for (VillagerInfo villagerInfo : getVillagerInfos()) {
            if (villagerInfo.matches(villager)) {
                return villagerInfo;
            }
        }
        return null;
    }

    public ActionManager getActionManager() {
        return actionManager;
    }

    @NotNull
    @SuppressWarnings("unchecked")
    public Set<VillagerInfo> getVillagerInfos() {
        if (villagerInfos != null) {
            return villagerInfos;
        }
        List<VillagerInfo> list = (List<VillagerInfo>) configuration.getList(VILLAGER_SPECIFICATIONS_PATH);
        villagerInfos = new HashSet<>(list);
        return villagerInfos;
    }

    public void setVillagerInfos(@NotNull Set<VillagerInfo> villagerInfos) {
        Preconditions.checkNotNull(villagerInfos, "villagerInfos");

        this.villagerInfos = villagerInfos;
        configuration.set(VILLAGER_SPECIFICATIONS_PATH, new ArrayList<>(villagerInfos));
        save();
    }

    public boolean addVillagerInfo(@NotNull VillagerInfo villagerInfo) {
        Preconditions.checkNotNull(villagerInfo, "villagerInfo");

        Set<VillagerInfo> villagerInfos = getVillagerInfos();
        boolean added = villagerInfos.add(villagerInfo);
        if (added) {
            setVillagerInfos(villagerInfos);
            return true;
        }
        return false;
    }

    public boolean removeVillagerInfo(@NotNull VillagerInfo villagerInfo) {
        Preconditions.checkNotNull(villagerInfo, "villagerInfo");

        Set<VillagerInfo> villagerInfos = getVillagerInfos();
        boolean removed = villagerInfos.remove(villagerInfo);
        if (removed) {
            setVillagerInfos(villagerInfos);
            return true;
        }
        return false;
    }
}
