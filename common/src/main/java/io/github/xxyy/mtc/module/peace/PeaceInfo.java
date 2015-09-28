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
    private final UUID uuid;
    @NotNull
    private final List<UUID> peaceWith;
    @NotNull
    private final List<UUID> requestsGot;
    @NotNull
    private final List<UUID> requestsSent;
    private boolean dirty;

    public PeaceInfo(@NotNull UUID uuid, @NotNull List<UUID> peaceWith, @NotNull List<UUID> requestsGot, @NotNull List<UUID> requestsSent) {
        this.uuid = uuid;
        this.peaceWith = peaceWith;
        this.requestsGot = requestsGot;
        this.requestsSent = requestsSent;
    }

    /**
     * @return the uuid of the player this instance is holding the friends of
     */
    @NotNull
    public UUID getUuid() {
        return uuid;
    }

    /**
     * Returns a copy, fele free to edit that list
     *
     * @return uuid's of the friends of the player associated with this instance, namely see {@link #getUuid()}
     */
    @NotNull
    public List<UUID> getPeaceWith() {
        return new ArrayList<>(peaceWith);
    }

    /**
     * This method exists to get the peace info without the overhead of copying the list.
     *
     * @return uuid's of the friends of the player associated with this instance, namely see {@link #getUuid()}
     */
    @NotNull
    List<UUID> getPeaceWithInternal() {
        return peaceWith;
    }

    @NotNull
    public List<UUID> getRequestsGot() {
        return new ArrayList<>(requestsGot);
    }

    @NotNull
    public List<UUID> getRequestsGotInternal() {
        return requestsGot;
    }

    @NotNull
    public List<UUID> getRequestsSent() {
        return new ArrayList<>(requestsSent);
    }

    @NotNull
    public List<UUID> getRequestsSentInternal() {
        return requestsSent;
    }

    public boolean isDirty() {
        return dirty;
    }

    void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    void setDirty() {
        this.dirty = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PeaceInfo)) {
            return false;
        }
        PeaceInfo peaceInfo = (PeaceInfo) o;
        return uuid.equals(peaceInfo.uuid);

    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public String toString() {
        return "PeaceInfo{" + "uuid=" + uuid + ", dirty=" + dirty + '}';
    }
}
