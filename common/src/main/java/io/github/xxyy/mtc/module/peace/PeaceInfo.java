package io.github.xxyy.mtc.module.peace;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class PeaceInfo {

    @NotNull
    private final UUID ofUuid;
    @NotNull
    private final List<UUID> friends;

    public PeaceInfo(@NotNull UUID ofUuid, @NotNull List<UUID> friends) {
        this.ofUuid = ofUuid;
        this.friends = friends;
    }
}
