/*
 * MinoTopiaCore
 * Copyright (C) 2013 - 2017 Philipp Nowak (https://github.com/xxyy) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package li.l1t.mtc.module.lanatus.pex.bulk;

import li.l1t.common.lib.com.mojang.api.profiles.HttpProfileRepository;
import li.l1t.common.lib.com.mojang.api.profiles.Profile;
import li.l1t.common.sql.sane.SaneSql;
import li.l1t.lanatus.api.LanatusClient;
import li.l1t.mtc.hook.XLoginHook;
import li.l1t.mtc.logging.LogManager;
import li.l1t.mtc.module.lanatus.pex.LanatusAccountMigrator;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

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
    private final XLoginProfileImporter importer;
    private final XLoginHook xLogin;

    public UsernameOnlyMigrationTask(Collection<PexImportUser> workQueue, int usersPerExecution, LocalDate referenceTimestamp,
                                     XLoginHook xLogin, LanatusClient lanatus, SaneSql sql) {
        this.workQueue = new LinkedList<>(workQueue);
        this.usersPerExecution = usersPerExecution;
        this.referenceTimestampUnix = referenceTimestamp.atStartOfDay(ZoneId.systemDefault()).toInstant().getEpochSecond();
        migrator = new LanatusAccountMigrator(lanatus);
        migrator.registerMigrationProduct();
        this.xLogin = xLogin;
        importer = new XLoginProfileImporter(xLogin, sql);
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
            Optional<UUID> optionalId = figureOutUniqueId(user);
            if (optionalId.isPresent()) {
                UUID playerId = optionalId.get();
                if(!importer.isKnownToXLogin(playerId)) {
                    importer.createXLoginProfile(playerId, user.getUserName());
                }
                migrator.migrateIfNecessary(user.getHandle(), playerId);
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
