/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.vote.sql.vote;

import com.google.common.base.Preconditions;
import li.l1t.common.collections.cache.OptionalCache;
import li.l1t.common.collections.cache.OptionalGuavaCache;
import li.l1t.lanatus.sql.AbstractSqlLanatusRepository;
import li.l1t.lanatus.sql.SqlLanatusClient;
import li.l1t.mtc.module.vote.api.Vote;
import li.l1t.mtc.module.vote.api.VoteRepository;

import java.time.Instant;
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

    public SqlVoteRepository(SqlLanatusClient client) {
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
                .map(Vote::getStreakLength)
                .orElse(0);
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
