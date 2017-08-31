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

package li.l1t.mtc.module.fulltag.dist;

import li.l1t.mtc.module.fulltag.model.FullData;

/**
 * Signals that a full item scheduled for distribution could not be distributed.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 10/09/15
 */
public class FullDistributionException extends Exception {
    private final FullData fullData;

    public FullDistributionException(FullData data, String message) {
        this(data, message, null);
    }

    public FullDistributionException(FullData data, String message, Throwable cause) {
        super(message, cause);
        this.fullData = data;
    }

    /**
     * @return the full data that failed distribution
     */
    public FullData getFullData() {
        return fullData;
    }
}
