package io.github.xxyy.mtc.module.peace;

import io.github.xxyy.mtc.MTC;
import io.github.xxyy.mtc.misc.ClearCacheBehaviour;
import io.github.xxyy.mtc.module.ConfigurableMTCModule;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PeaceModule extends ConfigurableMTCModule {

    public static final String NAME = "Peace";
    private PeaceInfoManager peaceInfoManager;
    private PeaceMessenger messenger;

    public PeaceModule() {
        super(NAME, "modules/peace.cfg.yml", ClearCacheBehaviour.RELOAD);
    }

    @Override
    public void enable(MTC plugin) throws Exception {
        super.enable(plugin);
        peaceInfoManager = new PeaceInfoManager(this);
        messenger = new PeaceMessenger(this);
        plugin.setExecAndCompleter(new CommandPeace(this), "frieden");
    }

    @Override
    public void disable(MTC plugin) {
        super.disable(plugin);
        plugin.setExecAndCompleter(null, "frieden");
        peaceInfoManager.flushAll();
        peaceInfoManager.syncStop();
        peaceInfoManager = null;
        messenger.stopAll();
    }

    @Override
    public void clearCache(boolean forced, MTC plugin) {
        peaceInfoManager.flushAll();
    }

    @Override
    protected void reloadImpl() {//TODO implement
    }

    public PeaceInfoManager getPeaceInfoManager() {
        return peaceInfoManager;
    }

    /**
     * TODO fill javadoc
     * Abbreviation method for peace checks
     *
     * @param first
     * @param second
     * @return
     * @see PlayerPeaceRelation#areInPeace(PeaceInfoManager, PeaceInfo, UUID)
     */
    public boolean areInPeace(Player first, Player second) {
        return PlayerPeaceRelation.areInPeace(peaceInfoManager, peaceInfoManager.get(first.getUniqueId()), second.getUniqueId());
    }

    public PeaceMessenger getMessenger() {
        return messenger;
    }
}
