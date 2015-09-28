package io.github.xxyy.mtc.module.peace;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Handles peace messages and repeatly sends request notifications
 *
 * @author Janmm14
 */
public class PeaceMessenger {

    @NotNull
    private final PeaceModule module;

    public PeaceMessenger(@NotNull PeaceModule module) {
        this.module = module;
    }

    /**
     * Checks if the joined player got requests and starts scheduling messages
     *
     * @param toNotify the player joined
     */
    public void startAllNotifications(Player toNotify) {
        //TODO implement
    }

    /**
     * Stops notifying specified player fully
     * <p>
     * This may be used when the notified player leaves the game
     *
     * @param notified the uuid of the player to stop notifying
     */
    public void stopNotify(UUID notified) {
        //TODO implement
    }

    /**
     * Stops notifying specified player about the request by the requestFrom player
     * <p>
     * This may be used if the notified player accepted the peace
     *
     * @param notified    the uuid of the player to stop notifying
     * @param requestFrom the uuid of the one sent the request
     */
    public void stopNotifyAbout(UUID notified, UUID requestFrom) {
        //TODO implement
    }

    /**
     * Notifies the initiator the request is sent and the target that he got a request if it is online
     *
     * @param initiator the {@link PeaceInfo} of the one sent the request
     * @param target    the uuid of the one targetted by the peace request
     */
    public void notifyRequestSent(PeaceInfo initiator, UUID target) {
        //TODO implement
    }

    @NotNull
    public PeaceModule getModule() {
        return module;
    }
}
