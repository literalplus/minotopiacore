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
