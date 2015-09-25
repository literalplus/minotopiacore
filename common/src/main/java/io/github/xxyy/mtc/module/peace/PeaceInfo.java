package io.github.xxyy.mtc.module.peace;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This class contains a uuid of the player and its friends' uuid
 *
 * @author Janmm14
 */
public class PeaceInfo {

    @NotNull
    private final UUID ofUuid;
    @NotNull
    private final List<UUID> peaceWith;

    public PeaceInfo(@NotNull UUID ofUuid, @NotNull List<UUID> peaceWith) {
        this.ofUuid = ofUuid;
        this.peaceWith = peaceWith;
    }

    /**
     * @return the uuid of the player this instance is holding the friends of
     */
    @NotNull
    public UUID getOfUuid() {
        return ofUuid;
    }

    /**
     * @return a copy of the uuid's of the friends of the player associated with this instance, namely see {@link #getOfUuid()}
     */
    @NotNull
    public List<UUID> getPeaceWith() {
        return new ArrayList<>(peaceWith);
    }
}
