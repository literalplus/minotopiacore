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

package li.l1t.mtc.module.vote.sql.vote;

import com.google.common.base.Preconditions;
import li.l1t.common.collections.cache.OptionalCache;
import li.l1t.common.collections.cache.OptionalGuavaCache;
import li.l1t.lanatus.sql.AbstractSqlLanatusRepository;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.module.lanatus.base.MTCLanatusClient;
import li.l1t.mtc.module.vote.api.Vote;
import li.l1t.mtc.module.vote.api.VoteRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

/**
 * A vote repository backed by a JDBC SQL data source.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-28
 */
public class SqlVoteRepository extends AbstractSqlLanatusRepository implements VoteRepository {
    public static final String TABLE_NAME = "mt_main.mtc_vote_vote";
    private final SqlVoteFetcher fetcher;
    private final SqlVoteWriter writer;
    private final OptionalCache<UUID, Vote> voteCache = new OptionalGuavaCache<>();

    @InjectMe
    public SqlVoteRepository(MTCLanatusClient client) {
        super(client);
        fetcher = new SqlVoteFetcher(new SqlVoteCreator(), client.sql());
        writer = new SqlVoteWriter(client.sql());
    }

    @Override
    public Optional<Vote> findVoteById(UUID voteId) {
        return voteCache.getOrCompute(voteId, fetcher::findVoteById);
    }

    @Override
    public Optional<Vote> findLatestVoteByPlayer(UUID playerId) {
        return fetcher.findLatestVoteByPlayerId(playerId);
    }

    @Override
    public Collection<Vote> findVotesFromToday() {
        return fetcher.findVotesFromToday();
    }

    @Override
    public SqlVote createVote(String username, String serviceName, UUID playerId) {
        Preconditions.checkNotNull(username, "username");
        Preconditions.checkNotNull(serviceName, "serviceName");
        SqlVote vote = new SqlVote(
                UUID.randomUUID(), username, serviceName, Instant.now(),
                findStreakLengthByName(username) + 1, playerId
        );
        save(vote);
        return vote;
    }

    private int findStreakLengthByName(String username) {
        return fetcher.findLatestVoteByUserName(username)
                .filter(this::wasTodayOrYesterday)
                .map(Vote::getStreakLength)
                .orElse(0);
    }

    private boolean wasTodayOrYesterday(Vote vote) {
        return vote.getTimestamp().isAfter(
                LocalDate.now().minusDays(1)
                        .atStartOfDay(ZoneId.systemDefault()).toInstant()
        );
    }

    @Override
    public void save(Vote vote) {
        Preconditions.checkNotNull(vote, "vote");
        voteCache.cacheValue(vote.getUniqueId(), vote);
        writer.save(vote);
    }

    @Override
    public void clearCache() {
        voteCache.clear();
    }

    @Override
    public void clearCachesFor(UUID uuid) {
        voteCache.valueStream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(Vote::getPlayerId)
                .filter(uuid::equals)
                .forEach(voteCache::invalidateKey);
    }
}
