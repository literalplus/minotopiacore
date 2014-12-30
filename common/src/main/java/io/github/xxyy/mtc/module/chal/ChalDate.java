/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.chal;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.StringUtils;

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

    public static ChalDate deserialize(String serialized) {
        Validate.isTrue(serialized.contains("-"), "Invalid format: Missing dash: ", serialized);
        String[] parts = serialized.split("-");
        Validate.isTrue(parts.length == 2, "Invalid format: There must be exactly one dash: ", serialized);
        Validate.isTrue(StringUtils.isNumeric(parts[0]), "Part 1 must be numeric: ", serialized);
        Validate.isTrue(StringUtils.isNumeric(parts[1]), "Part 2 must be numeric: ", serialized);
        return new ChalDate(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
    }
}
