package io.github.xxyy.mtc.module.peace;

import io.github.xxyy.mtc.MTC;
import io.github.xxyy.mtc.misc.ClearCacheBehaviour;
import io.github.xxyy.mtc.module.ConfigurableMTCModule;
import org.bukkit.entity.Player;

public class PeaceModule extends ConfigurableMTCModule {

    public static final String NAME = "Peace";
    private PeaceInfoManager peaceInfoManager;

    public PeaceModule() {
        super(NAME, "modules/peace.cfg.yml", ClearCacheBehaviour.RELOAD);
    }

    @Override
    public void enable(MTC plugin) throws Exception {
        super.enable(plugin);
        peaceInfoManager = new PeaceInfoManager(this);
        plugin.setExecAndCompleter(new LegacyCommandPeace(), "frieden");
    }

    @Override
    public void disable(MTC plugin) {
        super.disable(plugin);
        plugin.setExecAndCompleter(null, "frieden");
        peaceInfoManager.flushAll();
        peaceInfoManager = null;
    }

    @Override
    public void clearCache(boolean forced, MTC plugin) {
        peaceInfoManager.flushAll(); //TODO save cache or not on clearCache?
    }

    @Override
    protected void reloadImpl() {//TODO implement
    }

    public PeaceInfoManager getPeaceInfoManager() {
        return peaceInfoManager;
    }

    public boolean areInPeace(Player plrDamager, Player plr) {
        return peaceInfoManager.get(plrDamager.getUniqueId())
                .getPeaceWith().contains(plr.getUniqueId());
    }
}
