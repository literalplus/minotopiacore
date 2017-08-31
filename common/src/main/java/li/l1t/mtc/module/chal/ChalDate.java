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

package li.l1t.mtc.module.chal;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

import java.time.LocalDate;

/**
 * A simple implementation for a year-relative date, storing day and month.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 30/11/14
 */
class ChalDate {
    private final int month;
    private final int day;

    ChalDate(int month, int day) {
        this.month = month;
        this.day = day;
    }

    public static ChalDate deserialize(String serialized) {
        Validate.isTrue(serialized.contains("-"), "Invalid format: Missing dash: ", serialized);
        String[] parts = serialized.split("-");
        Validate.isTrue(parts.length == 2, "Invalid format: There must be exactly one dash: ", serialized);
        Validate.isTrue(StringUtils.isNumeric(parts[0]), "Part 1 must be numeric: ", serialized);
        Validate.isTrue(StringUtils.isNumeric(parts[1]), "Part 2 must be numeric: ", serialized);
        return new ChalDate(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public String serialize() {
        return toString();
    }

    public boolean is(LocalDate toCheck) {
        return toCheck.getDayOfMonth() == day &&
                toCheck.getMonthValue() == month;
    }

    public boolean before(LocalDate toCheck) {
        return toCheck.getDayOfMonth() > day &&
                toCheck.getMonthValue() >= month;
    }

    public String toReadable() {
        return day + "." + month + ".";
    }

    @Override
    public String toString() {
        return month + "-" + day;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChalDate)) return false;

        ChalDate chalDate = (ChalDate) o;
        return day == chalDate.day && month == chalDate.month;
    }

    @Override
    public int hashCode() {
        int result = month;
        result = 31 * result + day;
        return result;
    }
}
