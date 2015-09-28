package io.github.xxyy.mtc.module.peace;

import io.github.xxyy.mtc.logging.LogManager;
import org.apache.logging.log4j.Logger;
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
        boolean initiatorPeace = initiator.getPeaceWithInternal().contains(targetUuid);
        if (CONSISTENCY_CHECKS) {
            PeaceInfo target = manager.get(targetUuid);
            boolean targetPeace = target.getPeaceWithInternal().contains(initiator.getUuid());
            if (initiatorPeace && targetPeace) {
                return true;
            }
            if (!initiatorPeace && !targetPeace) {
                return false;
            }
            if (initiatorPeace) {
                LOG.warn("Data inconsistency found! " + initiator + " has peace with " + target + ", but not the other way round! Setting second to have peace with first.");
                target.getPeaceWithInternal().add(initiator.getUuid());
                target.setDirty();
            } else {
                LOG.warn("Data inconsistency found! " + target + " has peace with " + initiator + ", but not the other way round! Setting second to have peace with first.");

                initiator.getPeaceWithInternal().add(targetUuid);
                initiator.setDirty();
            }
            return true;
        }
        return initiatorPeace;
    }

    public static boolean isRequestSent(@NotNull PeaceInfoManager manager, @NotNull PeaceInfo initiator, @NotNull UUID targetUuid) {
        boolean initiatorSent = initiator.getRequestsSentInternal().contains(targetUuid);
        if (CONSISTENCY_CHECKS) {
            PeaceInfo target = manager.get(targetUuid);
            boolean targetGot = target.getRequestsGotInternal().contains(initiator.getUuid());
            if (initiatorSent && targetGot) {
                return true;
            }
            if (!initiatorSent && !targetGot) {
                return false;
            }
            if (initiatorSent) { //TODO log inconsistency
                target.getRequestsGotInternal().add(initiator.getUuid());
                target.setDirty();
            } else {
                initiator.getRequestsSentInternal().add(targetUuid);
                initiator.setDirty();
            }
            return true;
        }
        return initiatorSent;
    }


    public static boolean isRequestRecieved(@NotNull PeaceInfoManager manager, @NotNull PeaceInfo initiator, @NotNull UUID targetUuid) {
        boolean initiatorGot = initiator.getRequestsGotInternal().contains(targetUuid);
        if (CONSISTENCY_CHECKS) {
            PeaceInfo target = manager.get(targetUuid);
            boolean targetSent = target.getRequestsSentInternal().contains(initiator.getUuid());
            if (initiatorGot && targetSent) {
                return true;
            }
            if (!initiatorGot && !targetSent) {
                return false;
            }
            if (initiatorGot) { //TODO log inconsistency
                target.getRequestsSentInternal().add(initiator.getUuid());
                target.setDirty();
            } else {
                initiator.getRequestsSentInternal().add(targetUuid);
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
        initiator.getRequestsSentInternal().add(target.getUuid());
        target.getRequestsGotInternal().add(initiator.getUuid());

        initiator.setDirty();
        target.setDirty();

        module.getMessenger().notifyRequestSent(initiator, target.getUuid());
        return true;
    }
}
