package io.github.xxyy.mtc.module.bounty;

import gnu.trove.map.TObjectFloatMap;
import io.github.xxyy.mtc.misc.ClearCacheBehaviour;
import io.github.xxyy.mtc.yaml.ManagedConfiguration;

import java.io.File;
import java.util.UUID;

public class BountyManager extends ManagedConfiguration {

    private final BountyModule module;
    private TObjectFloatMap<UUID> uuidBountyMap;

    protected BountyManager(BountyModule module, File file) {
        super(file);
        this.module = module;
        setClearCacheBehaviour(ClearCacheBehaviour.SAVE);
    }

}
