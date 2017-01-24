/*
 * Copyright (c) 2013-2017.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.vote.reminder;

import li.l1t.mtc.api.misc.Cache;
import li.l1t.mtc.yaml.ManagedConfiguration;

/**
 * Provides access the the vote reminder configuration.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-01-24
 */
public class ReminderConfig implements Cache {
    private static final String INTERVAL_PATH = "vote-reminder.check-interval-seconds";
    private static final String MESSAGE_PATH = "vote-reminder.message";
    private long checkIntervalSeconds;
    private String message;

    public long getCheckIntervalSeconds() {
        return checkIntervalSeconds;
    }

    public long getCheckIntervalTicks() {
        return getCheckIntervalSeconds() * 20L;
    }

    public String getMessage() {
        return message;
    }

    public void load(ManagedConfiguration config) {
        config.options().copyDefaults(true);
        config.addDefault(INTERVAL_PATH, 15L * 60L);
        config.addDefault(MESSAGE_PATH, "§xDu hast heute noch nicht gevoted. /vote");
        checkIntervalSeconds = config.getLong(INTERVAL_PATH);
        message = config.getString(MESSAGE_PATH);
    }
}
