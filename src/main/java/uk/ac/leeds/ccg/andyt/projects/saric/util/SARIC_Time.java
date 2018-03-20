/*
 * Copyright (C) 2017 geoagdt.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package uk.ac.leeds.ccg.andyt.projects.saric.util;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Environment;

public class SARIC_Time extends SARIC_Date
        implements Serializable {

    public LocalDateTime LDT;

    public SARIC_Time(SARIC_Environment se) {
        super(se);
    }

    public SARIC_Time(SARIC_Time t) {
        this(t.se, t.LDT);
    }

    public SARIC_Time(SARIC_Date d) {
        this(d.se, LocalDateTime.of(d.LD, LocalTime.of(0, 0)));
    }

    public SARIC_Time(SARIC_Date d, LocalTime t) {
        this(d.se, LocalDateTime.of(d.LD, t));
    }

    public SARIC_Time(SARIC_Environment se, LocalDateTime dt) {
        super(se, LocalDate.from(dt));
        LDT = dt;
    }

    public SARIC_Time(SARIC_Environment se, int year, int month, int day,
            int hour, int minute, int second) {
        super(se, LocalDate.of(year, month, day));
        LDT = LocalDateTime.of(LD, LocalTime.of(hour, minute, second));
    }

    /**
     * Expects s to be of the form "YYYY-MM-DD" or "YYYY-MM-DDTHH:MM:SSZ"
     *
     * @param se
     * @param s
     */
    public SARIC_Time(SARIC_Environment se, String s) {
        this(se, s, se.getStrings().symbol_minus, se.getStrings().s_T,
                se.getStrings().symbol_colon);
    }

    /**
     * Expects s to be of the form "YYYY-MM-DD" or "YYYY-MM-DDTHH:MM:SSZ"
     *
     * @param se
     * @param s
     * @param dateDelimeter
     * @param timedateSeparator
     * @param timeDelimeter
     */
    public SARIC_Time(SARIC_Environment se, String s, String dateDelimeter,
            String timedateSeparator, String timeDelimeter) {
        super(se, s);
        String[] splitT;
        splitT = s.split(timedateSeparator);
        //super(split[0]);
        String[] split;
        String s_0 = Strings.symbol_0;
        String s2;
        int hour;
        int minute;
        int second;
        if (splitT.length == 2) {
            split = splitT[1].split(timeDelimeter);
            s2 = split[0];
            if (s2.startsWith(s_0)) {
                s2 = s2.substring(1);
            }
            hour = new Integer(s2);
            s2 = split[1];
            if (s2.startsWith(s_0)) {
                s2 = s2.substring(1);
            }
            minute = new Integer(s2);
            s2 = split[2];
            s2 = s2.substring(0, s2.length() - 1);
            if (s2.startsWith(s_0)) {
                s2 = s2.substring(1);
            }
            second = 0;
            if (s2.length() > 0) {
                second = new Integer(s2);
            }
        } else {
            hour = 0;
            minute = 0;
            second = 0;
        }
        setTime(hour, minute, second);
    }

    public void addMinutes(int minutes) {
        LDT = LDT.plusMinutes(minutes);
        LD = LDT.toLocalDate();
    }

    public void addHours(int hours) {
        LDT = LDT.plusHours(hours);
        LD = LDT.toLocalDate();
    }

    @Override
    public void addDays(int days) {
        super.addDays(days);
        LDT = LDT.plusDays(days);
    }

    public final void setTime(int hour, int minute, int second) {
        LDT = LocalDateTime.of(LD, LocalTime.of(hour, minute, second));
    }

    public SARIC_Date getDate() {
        SARIC_Date result;
        result = new SARIC_Date(this);
        return result;
    }

    /**
     *
     * @return YYYY-MM-DDTHH:MM:SSZ
     */
    public String toFormattedString0() {
        return getYYYYMMDDHHMMSS(
                Strings.symbol_minus,
                Strings.s_T,
                Strings.symbol_colon,
                Strings.s_Z);
    }

    /**
     *
     * @return YYYY-MM-DDTHH_MM_SSZ
     */
    public String toFormattedString1() {
        return getYYYYMMDDHHMMSS(
                Strings.symbol_minus,
                Strings.s_T,
                Strings.symbol_underscore,
                Strings.s_Z);
    }

    public String toFormattedString2() {
        return getYYYYMMDDHHMMSS(
                Strings.emptyString,
                Strings.emptyString,
                Strings.emptyString,
                Strings.emptyString);
    }

    public String getYYYYMMDDHHMM() {
        String result;
        result = getYYYY() + getMM() + getDD() + getHH() + getMins();
        return result;
    }

    @Override
    public String getDD() {
        String result = "";
        int dayOfMonth = LDT.getDayOfMonth();
        if (dayOfMonth < 10) {
            result += Strings.symbol_0;
        }
        result += Integer.toString(dayOfMonth);
        return result;
    }

    public String getHH() {
        String result = "";
        int hour = LDT.getHour();
        if (hour < 10) {
            result += Strings.symbol_0;
        }
        result += Integer.toString(hour);
        return result;
    }

    /**
     * So as not to confuse with SARIC_YearMonth.getMM() this is called
     * getMins() instead of getMM();
     *
     * @return
     */
    public String getMins() {
        String result = "";
        int minute = LDT.getMinute();
        if (minute < 10) {
            result += Strings.symbol_0;
        }
        result += Integer.toString(minute);
        return result;
    }

    public String getSS() {
        String result = "";
        int second = LDT.getSecond();
        if (second < 10) {
            result += Strings.symbol_0;
        }
        result += Integer.toString(second);
        return result;
    }

    @Override
    public String toString() {
        return getYYYYMMDDHHMMSS();
    }

    public String getYYYYMMDDHHMMSS() {
        String result;
        result = super.toString();
        result += Strings.s_T;
        result += getHH();
        result += Strings.symbol_colon;
        result += getMins();
        result += Strings.symbol_colon;
        result += getSS();
        return result;
    }

    public String getYYYYMMDDHHMMSS(String dateComponentDelimitter,
            String dateTimeDivider, String timeComponentDivider, 
            String resultEnding) {
        String result;
        result = getYYYYMMDD(dateComponentDelimitter);
        result += dateTimeDivider;
        result += getHH();
        result += timeComponentDivider;
        result += getMins();
        result += timeComponentDivider;
        result += getSS();
        result += resultEnding;
        return result;
    }

    /**
     * Returns true iff t is the same day as this.
     *
     * @param t
     * @return
     */
    @Override
    public boolean isSameDay(SARIC_Date t) {
        return LDT.toLocalDate().isEqual(t.LD);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof SARIC_Time) {
            if (this == o) {
                return true;
            }
            SARIC_Time t;
            t = (SARIC_Time) o;
            if (hashCode() == t.hashCode()) {
                return LDT.equals(t.LDT);
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + Objects.hashCode(LDT);
        return hash;
    }

    @Override
    public int compareTo(SARIC_YearMonth t) {
        SARIC_Time d = (SARIC_Time) t;
        if (LDT.isAfter(d.LDT)) {
            return 1;
        } else {
            if (LDT.isBefore(d.LDT)) {
                return -1;
            } else {
                return 0;
            }
        }
    }
}
