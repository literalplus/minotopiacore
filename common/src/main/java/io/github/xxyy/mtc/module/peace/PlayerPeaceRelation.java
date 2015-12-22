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
            if (initiatorPeace) {
                LOG.warn("Data inconsistency found! " + initiator + " has peace with " + target + ", but not the other way round! Setting second to have peace with first.");
                target.getPeaceWith().add(initiator.getUuid());
                target.setDirty();
            } else {
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
            if (initiatorSent) { //TODO log inconsistency
                target.getRequestsGot().add(initiator.getUuid());
                target.setDirty();
            } else {
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
            if (initiatorGot) { //TODO log inconsistency
                target.getRequestsSent().add(initiator.getUuid());
                target.setDirty();
            } else {
                initiator.getRequestsSent().add(targetUuid);
                initiator.setDirty();
            }
            return true;
        }
        return initiatorGot;
    }

    /**
     * @return success state
     */
    public boolean sendRequest(@NotNull PeaceModule module, @NotNull PeaceInfo initiator, @NotNull PeaceInfo target) { //TODO implement
        if (isRequestSent(module.getPeaceInfoManager(), initiator, target.getUuid())) {
            return false;
        }
        initiator.getRequestsSent().add(target.getUuid());
        target.getRequestsGot().add(initiator.getUuid());

        initiator.setDirty();
        target.setDirty();

        Player plr = Bukkit.getPlayer(initiator.getUuid());
        if (plr != null) {
            module.getMessenger().notifyRequestSent(plr, target.getUuid());
        }
        return true;
    }
}
