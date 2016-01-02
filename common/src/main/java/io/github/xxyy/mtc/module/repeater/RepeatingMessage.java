/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.repeater;

import org.apache.commons.lang.Validate;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a repeating message, with interval and formatting codes defined.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 30/11/14
 */
public class RepeatingMessage implements ConfigurationSerializable {
    public static final String MESSAGE_PATH = "message";
    public static final String INTERVAL_PATH = "interval";
    public static final String AUTHOR_PATH = "authorid";
    private final String message;
    private final long tickInterval;
    private final UUID author;

    public RepeatingMessage(String message, long tickInterval, UUID author) {
        this.message = message;
        this.tickInterval = tickInterval;
        this.author = author;
    }

    public String getMessage() {
        return message;
    }

    /**
     * @return the interval in message ticks
     * @see io.github.xxyy.mtc.module.repeater.RepeaterModule#SECONDS_PER_TICK
     */
    public long getTickInterval() {
        return tickInterval;
    }

    public long getSecondInterval() {
        return tickInterval * RepeaterModule.SECONDS_PER_TICK;
    }

    public UUID getAuthor() {
        return author;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = new HashMap<>();
        result.put(MESSAGE_PATH, message);
        result.put(INTERVAL_PATH, tickInterval);
        result.put(AUTHOR_PATH, author.toString());
        return result;
    }

    public static RepeatingMessage deserialize(Map<String, Object> input) {
        Validate.isTrue(input.containsKey(MESSAGE_PATH), "Need message!");
        Validate.isTrue(input.containsKey(INTERVAL_PATH), "Need interval!");
        Validate.isTrue(input.containsKey(AUTHOR_PATH), "Need author!");
        Integer interval = (Integer) input.get(INTERVAL_PATH);
        Validate.isTrue(interval > 0, "Invalid must be positive", interval);
        return new RepeatingMessage(input.get(MESSAGE_PATH).toString(), interval,
                UUID.fromString(input.get(AUTHOR_PATH).toString()));
    }

    @Override
    @SuppressWarnings("RedundantIfStatement")
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RepeatingMessage)) return false;

        RepeatingMessage message1 = (RepeatingMessage) o;

        if (tickInterval != message1.tickInterval) return false;
        if (!author.equals(message1.author)) return false;
        if (!message.equals(message1.message)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = message.hashCode();
        result = 31 * result + (int) (tickInterval ^ (tickInterval >>> 32));
        result = 31 * result + author.hashCode();
        return result;
    }
}
