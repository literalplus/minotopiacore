/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.chat.mute.yaml;

import com.google.common.base.Preconditions;
import li.l1t.mtc.module.chat.mute.api.Mute;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Represents mute metadata of a muted player that is stored in a YAML file.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-08-23
 */
public class YamlBackedMute implements ConfigurationSerializable, Mute {
    private static final String PLAYER_ID_PATH = "player-id";
    private static final String UPDATE_TIME_PATH = "update-time";
    private static final String EXPIRY_TIME_PATH = "expiry-time";
    private static final String SOURCE_ID_PATH = "source-id";
    private static final String REASON_PATH = "reason";
    private final UUID playerId;
    private Instant updateTime;
    private Instant expiryTime;
    private UUID sourceId;
    private String reason;

    YamlBackedMute(UUID playerId) {
        this.playerId = Preconditions.checkNotNull(playerId, "playerId");
    }

    public static YamlBackedMute deserialize(Map<String, Object> source) {
        YamlBackedMute result = new YamlBackedMute(UUID.fromString((String) source.get(PLAYER_ID_PATH)));
        result.updateTime = Instant.parse((String) source.get(UPDATE_TIME_PATH));
        result.expiryTime = Instant.parse((String) source.get(EXPIRY_TIME_PATH));
        result.sourceId = UUID.fromString((String) source.get(SOURCE_ID_PATH));
        result.reason = (String) source.get(REASON_PATH);
        return result;
    }

    @Override
    public Map<String, Object> serialize() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(PLAYER_ID_PATH, playerId.toString());
        result.put(UPDATE_TIME_PATH, updateTime.toString());
        result.put(EXPIRY_TIME_PATH, expiryTime.toString());
        result.put(SOURCE_ID_PATH, sourceId.toString());
        result.put(REASON_PATH, reason);
        return result;
    }

    @Override
    public void update(UUID source, Instant expiryTime, String reason) {
        Preconditions.checkNotNull(source, "source");
        Preconditions.checkNotNull(expiryTime, "expiryTime");
        Preconditions.checkNotNull(reason, "reason");
        this.sourceId = source;
        this.expiryTime = expiryTime;
        this.reason = reason;
        this.updateTime = Instant.now();
    }

    @Override
    public boolean hasExpired() {
        return expiryTime.isBefore(Instant.now());
    }

    @Override
    public UUID getPlayerId() {
        return playerId;
    }

    @Override
    public Instant getUpdateTime() {
        return updateTime;
    }

    @Override
    public Instant getExpiryTime() {
        return expiryTime;
    }

    @Override
    public UUID getSourceId() {
        return sourceId;
    }

    @Override
    public String getReason() {
        return reason;
    }
}
