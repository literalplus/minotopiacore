package io.github.xxyy.mtc.module.bounty;

import io.github.xxyy.mtc.MTC;
import io.github.xxyy.mtc.misc.ClearCacheBehaviour;
import io.github.xxyy.mtc.module.ConfigurableMTCModule;
import org.bukkit.event.HandlerList;

public class BountyModule extends ConfigurableMTCModule {

    public static final String NAME = "Bounty";
    private DeathListener listener;

    public BountyModule() {
        super(NAME, "modules/bounty.cfg.yml", ClearCacheBehaviour.RELOAD);
    }

    @Override
    public boolean canBeEnabled(MTC plugin) {
        if (!plugin.getServer().getPluginManager().isPluginEnabled("Vault")) {
            plugin.getLogger().warning("[BountyModule] BountyModule can't be enabled, because dependency plugin Vault is missing!");
            return false;
        }
        return super.canBeEnabled(plugin);
    }

    @Override
    public void enable(MTC plugin) throws Exception {
        super.enable(plugin);
        listener = new DeathListener(this);
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }

    @Override
    public void disable(MTC plugin) {
        HandlerList.unregisterAll(listener);
    }

    @Override
    protected void reloadImpl() {

    }
}
