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
import java.util.Calendar;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Environment;

public class SARIC_Time extends SARIC_Date
        implements Serializable {

    private int Hour;
    private int Minute;
    private int Second;

    public SARIC_Time(
            SARIC_Environment se) {
        super(se);
        norm();
    }

    public SARIC_Time(SARIC_Time t) {
        this(t.se,
                t.Year,
                t.Month,
                t.DayOfMonth,
                t.Hour,
                t.Minute,
                t.Second);
    }

    public SARIC_Time(SARIC_Date d) {
        this(d.se,
                d.Year,
                d.Month,
                d.DayOfMonth,
                0,
                0,
                0);
    }

    public SARIC_Time(
            SARIC_Environment se,
            int year,
            int month,
            int dayOfMonth,
            int hour,
            int minute,
            int second) {
        super(se, year, month, dayOfMonth);
        Hour = hour;
        Minute = minute;
        Second = second;
        init();
    }

    private void init() {
        _Calendar.set(Calendar.HOUR_OF_DAY, Hour);
        _Calendar.set(Calendar.MINUTE, Minute);
        _Calendar.set(Calendar.SECOND, Second);
    }

    /**
     * Expects s to be of the form "YYYY-MM-DD" or "YYYY-MM-DDTHH:MM:SSZ"
     *
     * @param se
     * @param s
     */
    public SARIC_Time(
            SARIC_Environment se,
            String s) {
        this(se, s, se.getStrings().symbol_minus, se.getStrings().string_T,
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
    public SARIC_Time(
            SARIC_Environment se,
            String s,
            String dateDelimeter,
            String timedateSeparator,
            String timeDelimeter) {
        super(se, s);
        String[] splitT;
        splitT = s.split(timedateSeparator);
        //super(split[0]);
        String[] split;
        String s_0 = Strings.symbol_0;
        String s2;
        if (splitT.length == 2) {
            split = splitT[1].split(timeDelimeter);
            s2 = split[0];
            if (s2.startsWith(s_0)) {
                s2 = s2.substring(1);
            }
            Hour = new Integer(s2);
            s2 = split[1];
            if (s2.startsWith(s_0)) {
                s2 = s2.substring(1);
            }
            Minute = new Integer(s2);
            s2 = split[2];
            s2 = s2.substring(0, s2.length() - 1);
            if (s2.startsWith(s_0)) {
                s2 = s2.substring(1);
            }
            if (s2.length() > 0) {
                Second = new Integer(s2);
            } else {
                Second = 0;
            }
        } else {
            Hour = 0;
            Minute = 0;
        }
        init();
    }

    public void setHourOfDay(int hour) {
        Hour = hour;
        _Calendar.set(Calendar.HOUR_OF_DAY, hour);
    }

    public void setMinuteOfHour(int minute) {
        Minute = minute;
        _Calendar.set(Calendar.MINUTE, minute);
    }

    public void addMinutes(int minutes) {
        _Calendar.add(Calendar.MINUTE, minutes);
        normalise();
    }

    public void addHours(int hours) {
        _Calendar.add(Calendar.HOUR_OF_DAY, hours);
        normalise();
    }

    @Override
    protected void normalise() {
        norm();
        super.normalise();
    }

    private void norm() {
        Hour = _Calendar.get(Calendar.HOUR_OF_DAY);
        Minute = _Calendar.get(Calendar.MINUTE);
        Second = _Calendar.get(Calendar.SECOND);
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
                Strings.string_T,
                Strings.symbol_colon,
                Strings.string_Z);
    }

    /**
     *
     * @return YYYY-MM-DDTHH_MM_SSZ
     */
    public String toFormattedString1() {
        return getYYYYMMDDHHMMSS(
                Strings.symbol_minus,
                Strings.string_T,
                Strings.symbol_underscore,
                Strings.string_Z);
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

    public String getHH() {
        String result = "";
        if (Hour < 10) {
            result += Strings.symbol_0;
        }
        result += Integer.toString(Hour);
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
        if (Minute < 10) {
            result += Strings.symbol_0;
        }
        result += Integer.toString(Minute);
        return result;
    }

    public String getSS() {
        String result = "";
        if (Second < 10) {
            result += Strings.symbol_0;
        }
        result += Integer.toString(Second);
        return result;
    }

    @Override
    public String toString() {
        return getYYYYMMDDHHMMSS();
    }
    
    public String getYYYYMMDDHHMMSS() {
        String result;
        result = super.toString();
        result += Strings.string_T;
        result += getHH();
        result += Strings.symbol_colon;
        result += getMins();
        result += Strings.symbol_colon;
        result += getSS();
        return result;
    }

    public String getYYYYMMDDHHMMSS(
            String dateComponentDelimitter,
            String dateTimeDivider,
            String timeComponentDivider,
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

    @Override
    public boolean equals(Object o) {
        if (o instanceof SARIC_Time) {
            if (this == o) {
                return true;
            }
            SARIC_Time t;
            t = (SARIC_Time) o;
            if (hashCode() == t.hashCode()) {
                if (_Calendar.get(Calendar.DAY_OF_MONTH) == t._Calendar.get(Calendar.DAY_OF_MONTH)) {
                    if (_Calendar.get(Calendar.HOUR_OF_DAY) == t._Calendar.get(Calendar.HOUR_OF_DAY)) {
                        if (_Calendar.get(Calendar.MONTH) == t._Calendar.get(Calendar.MONTH)) {
                            if (_Calendar.get(Calendar.MINUTE) == t._Calendar.get(Calendar.MINUTE)) {
                                if (_Calendar.get(Calendar.YEAR) == t._Calendar.get(Calendar.YEAR)) {
                                    //if (_Calendar.get(Calendar.SECOND) == t._Calendar.get(Calendar.SECOND)) {
                                    return true;
                                    //}
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + this.Year;
        hash = 29 * hash + this.Month;
        hash = 29 * hash + this.DayOfMonth;
        hash = 29 * hash + this.Hour;
        hash = 29 * hash + this.Minute;
        hash = 29 * hash + this.Second;
        return hash;
    }

//    @Override
//    public int hashCode() {
//        int hash = 3;
//        hash = 89 * hash + (this._Calendar != null ? this._Calendar.hashCode() : 0);
//        return hash;
//    }
    @Override
    public int compareTo(SARIC_YearMonth t) {
        int superCompareTo = super.compareTo(t);
        if (superCompareTo == 0) {
            SARIC_Time d = (SARIC_Time) t;
            if (Hour > d.Hour) {
                return 1;
            } else {
                if (Hour < d.Hour) {
                    return -1;
                } else {
                    if (Minute > d.Minute) {
                        return 1;
                    } else {
                        if (Minute < d.Minute) {
                            return -1;
                        } else {
//                            if (Second > d.Second) {
//                                return 1;
//                            } else {
//                                if (Second < d.Second) {
//                                    return -1;
//                                } else {
//                                    return 0;
//                                }
//                            }
                            return 0;
                        }
                    }
                }
            }
        } else {
            return superCompareTo;
        }
    }
}
