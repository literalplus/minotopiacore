package io.github.xxyy.mtc.module.peace;

import io.github.xxyy.mtc.logging.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PlayerPeaceRelation {

    /**
     * Whether to check data for consistency
     */
    private static final boolean CONSISTENCY_CHECKS = true;
    private static final Logger LOG = LogManager.getLogger(PlayerPeaceRelation.class);

    private PlayerPeaceRelation() {
        throw new UnsupportedOperationException();
    }

    public static boolean areInPeace(@NotNull PeaceInfoManager manager, @NotNull PeaceInfo initiator, @NotNull UUID targetUuid) {
        boolean initiatorPeace = initiator.getPeaceWith().contains(targetUuid);
        if (CONSISTENCY_CHECKS) {
            PeaceInfo target = manager.get(targetUuid);
            boolean targetPeace = target.getPeaceWith().contains(initiator.getUuid());
            if (initiatorPeace && targetPeace) {
                return true;
            }
            if (!initiatorPeace && !targetPeace) {
                return false;
            }
            if (initiatorPeace) { // targetPeace = false
                LOG.warn("Data inconsistency found! " + initiator + " has peace with " + target + ", but not the other way round! Setting second to have peace with first.");
                target.getPeaceWith().add(initiator.getUuid());
                target.setDirty();
            } else { // initiatorPeace = false, targetPeace = true
                LOG.warn("Data inconsistency found! " + target + " has peace with " + initiator + ", but not the other way round! Setting second to have peace with first.");

                initiator.getPeaceWith().add(targetUuid);
                initiator.setDirty();
            }
            return true;
        }
        return initiatorPeace;
    }

    public static boolean isRequestSent(@NotNull PeaceInfoManager manager, @NotNull PeaceInfo initiator, @NotNull UUID targetUuid) {
        boolean initiatorSent = initiator.getRequestsSent().contains(targetUuid);
        if (CONSISTENCY_CHECKS) {
            PeaceInfo target = manager.get(targetUuid);
            boolean targetGot = target.getRequestsGot().contains(initiator.getUuid());
            if (initiatorSent && targetGot) {
                return true;
            }
            if (!initiatorSent && !targetGot) {
                return false;
            }
            if (initiatorSent) { // targetGot = false
                LOG.warn("Data inconsistency found! " + initiator + " has sent a request to " + target + ", but request target got no request! Setting second to have recieved a request from first.");
                target.getRequestsGot().add(initiator.getUuid());
                target.setDirty();
            } else { // initiatorSent = false, targetGot = true
                LOG.warn("Data inconsistency found! " + target + " has sent a request to " + initiator + ", but request target got no request! Setting second to have recieved a request from first.");
                initiator.getRequestsSent().add(targetUuid);
                initiator.setDirty();
            }
            return true;
        }
        return initiatorSent;
    }


    public static boolean isRequestRecieved(@NotNull PeaceInfoManager manager, @NotNull PeaceInfo initiator, @NotNull UUID targetUuid) {
        boolean initiatorGot = initiator.getRequestsGot().contains(targetUuid);
        if (CONSISTENCY_CHECKS) {
            PeaceInfo target = manager.get(targetUuid);
            boolean targetSent = target.getRequestsSent().contains(initiator.getUuid());
            if (initiatorGot && targetSent) {
                return true;
            }
            if (!initiatorGot && !targetSent) {
                return false;
            }
            if (initiatorGot) { // targetSent = false
                LOG.warn("Data inconsistency found! " + initiator + " has got a request from " + target + ", but request origin has not sent! Setting second to have sent a request to first.");
                target.getRequestsSent().add(initiator.getUuid());
                target.setDirty();
            } else { //initiatorGot = false, targetSent = true
                LOG.warn("Data inconsistency found! " + target + " has got a request from " + initiator + ", but request origin has not sent! Setting second to have sent a request to first.");
                initiator.getRequestsSent().add(targetUuid);
                initiator.setDirty();
            }
            return true;
        }
        return initiatorGot;
    }
}
