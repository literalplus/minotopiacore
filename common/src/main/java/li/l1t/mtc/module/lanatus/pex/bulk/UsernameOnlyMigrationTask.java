/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.lanatus.pex.bulk;

import li.l1t.common.lib.com.mojang.api.profiles.HttpProfileRepository;
import li.l1t.common.lib.com.mojang.api.profiles.Profile;
import li.l1t.lanatus.api.LanatusClient;
import li.l1t.mtc.hook.XLoginHook;
import li.l1t.mtc.logging.LogManager;
import li.l1t.mtc.module.lanatus.pex.LanatusAccountMigrator;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.UUID;

/**
 * A task that figures out the unique ids associated with import user names at a given time.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-11-08
 */
class UsernameOnlyMigrationTask extends AbstractPexImportTask {
    private static final Logger LOGGER = LogManager.getLogger(UsernameOnlyMigrationTask.class);
    private final HttpProfileRepository profileRepository = new HttpProfileRepository("minecraft");
    private final Collection<PexImportUser> unmatchedUsers = new LinkedList<>();
    private final LanatusAccountMigrator migrator;
    private final Queue<PexImportUser> workQueue;
    private final int usersPerExecution;
    private final long referenceTimestampUnix;
    private final XLoginHook xLogin;

    public UsernameOnlyMigrationTask(Collection<PexImportUser> workQueue, int usersPerExecution, LocalDate referenceTimestamp, XLoginHook xLogin, LanatusClient lanatus) {
        this.workQueue = new LinkedList<>(workQueue);
        this.usersPerExecution = usersPerExecution;
        this.referenceTimestampUnix = referenceTimestamp.atStartOfDay(ZoneId.systemDefault()).toInstant().getEpochSecond();
        migrator = new LanatusAccountMigrator(lanatus);
        migrator.registerMigrationProduct();
        this.xLogin = xLogin;
    }

    @Override
    public void run() {
        for (int i = 0; i < usersPerExecution; i++) {
            if (workQueue.isEmpty()) {
                tryCancel();
                getFuture().complete(unmatchedUsers);
                return;
            }
            PexImportUser user = workQueue.poll();
            Optional<UUID> uuid = figureOutUniqueId(user);
            if (uuid.isPresent()) {
                if(migrator.migrateIfNecessary(user.getHandle(), user.getUniqueId())) {
                    LOGGER.info("Migrated user {} to Lanatus, assuming UUID {}.", user.getUserName(), user.getUniqueId());
                }
                continue;
            }
            unmatchedUsers.add(user);
        }
    }

    private Optional<UUID> figureOutUniqueId(PexImportUser user) {
        return findMojangUniqueId(user.getUserName())
                .map(Optional::of)
                .orElseGet(() -> findXLoginUniqueId(user.getUserName()));
    }

    private Optional<UUID> findMojangUniqueId(String userName) {
        return Optional.ofNullable(profileRepository.findProfileAtTime(userName, referenceTimestampUnix))
                .map(Profile::getUniqueId);
    }

    private Optional<UUID> findXLoginUniqueId(String userName) {
        List<XLoginHook.Profile> profiles = xLogin.getProfiles(userName);
        if (profiles.size() == 1) {
            XLoginHook.Profile profile = profiles.get(0);
            if (!profile.isPremium()) {
                return Optional.of(profile.getUniqueId());
            }
        }
        return Optional.empty();
    }
}
