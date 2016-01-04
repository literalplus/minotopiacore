package io.github.xxyy.mtc.module.villagertradepermission;

import com.google.common.base.Preconditions;
import io.github.xxyy.mtc.api.MTCPlugin;
import io.github.xxyy.mtc.api.command.CommandBehaviours;
import io.github.xxyy.mtc.misc.ClearCacheBehaviour;
import io.github.xxyy.mtc.module.ConfigurableMTCModule;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Villager;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VillagerTradePermissionModule extends ConfigurableMTCModule {
    private static final String SHORT_NAME = "VillagerTradePermission";
    public static final String NAME = SHORT_NAME + "Module";
    private static final String VILLAGER_SPECIFICATIONS_PATH = "villagerSpecifications";
    private VillagerClickListener listener;
    private Set<VillagerInfo> villagerInfos;
    private VillagerPermissionCommand villagerPermissionCommand;

    protected VillagerTradePermissionModule() {
        super(NAME, "modules/" + SHORT_NAME.toLowerCase() + "/data.yml", ClearCacheBehaviour.SAVE);
    }

    @Override
    public void enable(MTCPlugin plugin) throws Exception {
        super.enable(plugin);
        ConfigurationSerialization.registerClass(VillagerInfo.class);
        configuration.options().copyDefaults(true);
        configuration.addDefault(VILLAGER_SPECIFICATIONS_PATH, new ArrayList<VillagerInfo>());
        listener = new VillagerClickListener(this);
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
        villagerPermissionCommand = new VillagerPermissionCommand(this);
        registerCommand(villagerPermissionCommand, "villagerpermission", "vp")
            .behaviour(CommandBehaviours.permissionChecking(villagerPermissionCommand.getPermission()));
    }

    @Override
    protected void reloadImpl() {
        villagerPermissionCommand.clearCache();
        configuration.options().copyDefaults(true);
        configuration.addDefault(VILLAGER_SPECIFICATIONS_PATH, new ArrayList<VillagerInfo>());
        villagerInfos = null;
        getVillagerInfos();
    }

    @Override
    public void disable(MTCPlugin plugin) {
        villagerPermissionCommand.disable();
        villagerPermissionCommand = null;
        villagerInfos.clear();
        villagerInfos = null;
        HandlerList.unregisterAll(listener);
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

    public VillagerPermissionCommand getVillagerPermissionCommand() {
        return villagerPermissionCommand;
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
