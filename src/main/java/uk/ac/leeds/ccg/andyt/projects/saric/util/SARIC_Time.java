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
        implements Serializable, Comparable {

    private int HOUR_OF_DAY;
    private int MINUTE;
    private int SECOND;

    protected SARIC_Time() {
        super();
    }

    public SARIC_Time(SARIC_Environment se) {
        super(se);
    }

    public SARIC_Time(SARIC_Time t) {
        this(t.se,
                t.calendar.get(Calendar.YEAR),
                t.calendar.get(Calendar.MONTH),
                t.calendar.get(Calendar.DAY_OF_MONTH),
                t.calendar.get(Calendar.HOUR_OF_DAY),
                t.calendar.get(Calendar.MINUTE),
                t.calendar.get(Calendar.SECOND));
    }

    public SARIC_Time(SARIC_Date d) {
        this(d.se,
                d.calendar.get(Calendar.YEAR),
                d.calendar.get(Calendar.MONTH),
                d.calendar.get(Calendar.DAY_OF_MONTH),
                0,
                0,
                0);
//                d.calendar.get(Calendar.HOUR_OF_DAY),
//                d.calendar.get(Calendar.MINUTE),
//                d.calendar.get(Calendar.SECOND));
    }

    public SARIC_Time(
            SARIC_Environment se,
            int year,
            int month,
            int dayOfMonth,
            int hourOfDay,
            int minuteOfHour,
            int secondOfMinute) {
        this(se);
        calendar.set(
                year,
                month,
                dayOfMonth,
                hourOfDay,
                minuteOfHour,
                secondOfMinute);
        normalise();
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
        super(se);
        String[] splitT;
        splitT = s.split(ss.string_T);
        //super(split[0]);
        String[] split;
        split = splitT[0].split(ss.symbol_minus);
        YEAR = new Integer(split[0]);
        String s_0;
        s_0 = ss.symbol_0;
        String s2;
        s2 = split[1];
        if (s2.startsWith(s_0)) {
            s2 = s2.substring(1);
        }
        MONTH = new Integer(s2);
        s2 = split[2];
        if (s2.startsWith(s_0)) {
            s2 = s2.substring(1);
        }
        DAY_OF_MONTH = new Integer(s2);
        HOUR_OF_DAY = 0;
        MINUTE = 0;
        SECOND = 0;
        if (splitT.length == 2) {
            split = splitT[1].split(ss.symbol_colon);
            s2 = split[0];
            if (s2.startsWith(s_0)) {
                s2 = s2.substring(1);
            }
            HOUR_OF_DAY = new Integer(s2);
            s2 = split[1];
            if (s2.startsWith(s_0)) {
                s2 = s2.substring(1);
            }
            MINUTE = new Integer(s2);
        }
        calendar.set(
                YEAR,
                MONTH,
                DAY_OF_MONTH,
                HOUR_OF_DAY,
                MINUTE,
                SECOND);
    }

    public void setHourOfDay(int hour) {
        HOUR_OF_DAY = hour;
        calendar.set(Calendar.HOUR_OF_DAY, hour);
    }

    public void setMinuteOfHour(int minute) {
        MINUTE = minute;
        calendar.set(Calendar.MINUTE, minute);
    }

//    public void setSecondOfMinute(int second) {
//        calendar.set(Calendar.SECOND, second);
//    }
//    public void setMillisecond(int millisecond) {
//        calendar.set(Calendar.MILLISECOND, millisecond);
//    }
    public void addMinutes(int minutes) {
        calendar.add(Calendar.MINUTE, minutes);
        normalise();
    }

    public void addHours(int hours) {
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        normalise();
    }

    private void normalise() {
        YEAR = calendar.get(Calendar.YEAR);
        MONTH = calendar.get(Calendar.MONTH);
        DAY_OF_MONTH = calendar.get(Calendar.DAY_OF_MONTH);
        HOUR_OF_DAY = calendar.get(Calendar.HOUR_OF_DAY);
        MINUTE = calendar.get(Calendar.MINUTE);
        SECOND = calendar.get(Calendar.SECOND);
    }

    public SARIC_Date getDate() {
        SARIC_Date result;
        result = new SARIC_Date(this);
        return result;
    }

    public String getDateString() {
        return super.toString();
    }

    /**
     *
     * @return YYYY-MM-DDTHH:MM:SSZ
     */
    public String toFormattedString0() {
        return toString(
                ss.symbol_minus,
                ss.string_T,
                ss.symbol_colon,
                ss.string_Z);
    }

    /**
     *
     * @return YYYY-MM-DDTHH_MM_SSZ
     */
    public String toFormattedString1() {
        return toString(
                ss.symbol_minus,
                ss.string_T,
                ss.symbol_underscore,
                ss.string_Z);
    }

    public String toFormattedString2() {
        return toString(
                ss.emptyString,
                ss.emptyString,
                ss.emptyString,
                ss.emptyString);
    }

    public String getYYYYMMDDHHMM() {
        String result;
        result = getYYYY() + super.getMM() + getDD() + getHH() + getMM();
        return result;
    }

    public String getHH() {
        String result = "";
        if (HOUR_OF_DAY < 10) {
            result += ss.symbol_0;
        }
        result += Integer.toString(HOUR_OF_DAY);
        return result;
    }

    /**
     * Not to be confused with SARIC_YearMonth.getMM() which gets the months
     * rather than the minutes!
     *
     * @return
     */
    @Override
    public String getMM() {
        String result = "";
        if (MINUTE < 10) {
            result += ss.symbol_0;
        }
        result += Integer.toString(MINUTE);
        return result;
    }

    public String getSS() {
        String result = "";
        if (SECOND < 10) {
            result += ss.symbol_0;
        }
        result += Integer.toString(SECOND);
        return result;
    }

    @Override
    public String toString() {
        return toString(
                ss.symbol_minus,
                ss.string_T,
                ss.symbol_colon,
                ss.emptyString);
    }

    public String toString(
            String dateComponentDelimitter,
            String dateTimeDivider,
            String timeComponentDivider,
            String resultEnding) {
        String result;
        result = super.toString(dateComponentDelimitter);
        result += dateTimeDivider;
        result += getHH();
        result += timeComponentDivider;
        result += getMM();
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
                if (calendar.get(Calendar.DAY_OF_MONTH) == t.calendar.get(Calendar.DAY_OF_MONTH)) {
                    if (calendar.get(Calendar.HOUR_OF_DAY) == t.calendar.get(Calendar.HOUR_OF_DAY)) {
                        if (calendar.get(Calendar.MONTH) == t.calendar.get(Calendar.MONTH)) {
                            if (calendar.get(Calendar.MINUTE) == t.calendar.get(Calendar.MINUTE)) {
                                if (calendar.get(Calendar.YEAR) == t.calendar.get(Calendar.YEAR)) {
                                    if (calendar.get(Calendar.SECOND) == t.calendar.get(Calendar.SECOND)) {
                                        return true;
                                    }
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
        hash = 29 * hash + this.YEAR;
        hash = 29 * hash + this.MONTH;
        hash = 29 * hash + this.DAY_OF_MONTH;
        hash = 29 * hash + this.HOUR_OF_DAY;
        hash = 29 * hash + this.MINUTE;
        hash = 29 * hash + this.SECOND;
        return hash;
    }

//    @Override
//    public int hashCode() {
//        int hash = 3;
//        hash = 89 * hash + (this.calendar != null ? this.calendar.hashCode() : 0);
//        return hash;
//    }
    public int compareTo(Object o) {
        if (o == null) {
            return 1;
        }
        if (o instanceof SARIC_Time) {
            SARIC_Time t;
            t = (SARIC_Time) o;
            if (calendar.get(Calendar.YEAR) > t.calendar.get(Calendar.YEAR)) {
                return 1;
            } else {
                if (calendar.get(Calendar.YEAR) < t.calendar.get(Calendar.YEAR)) {
                    return -1;
                } else {
                    if (calendar.get(Calendar.MONTH) > t.calendar.get(Calendar.MONTH)) {
                        return 1;
                    } else {
                        if (calendar.get(Calendar.MONTH) < t.calendar.get(Calendar.MONTH)) {
                            return -1;
                        } else {
                            if (calendar.get(Calendar.DAY_OF_MONTH) > t.calendar.get(Calendar.DAY_OF_MONTH)) {
                                return 1;
                            } else {
                                if (calendar.get(Calendar.DAY_OF_MONTH) < t.calendar.get(Calendar.DAY_OF_MONTH)) {
                                    return -1;
                                } else {
                                    if (calendar.get(Calendar.HOUR_OF_DAY) > t.calendar.get(Calendar.HOUR_OF_DAY)) {
                                        return 1;
                                    } else {
                                        if (calendar.get(Calendar.HOUR_OF_DAY) < t.calendar.get(Calendar.HOUR_OF_DAY)) {
                                            return -1;
                                        } else {
                                            if (calendar.get(Calendar.MINUTE) > t.calendar.get(Calendar.MINUTE)) {
                                                return 1;
                                            } else {
                                                if (calendar.get(Calendar.MINUTE) < t.calendar.get(Calendar.MINUTE)) {
                                                    return -1;
                                                } else {
                                                    if (calendar.get(Calendar.SECOND) > t.calendar.get(Calendar.SECOND)) {
                                                        return 1;
                                                    } else {
                                                        if (calendar.get(Calendar.SECOND) < t.calendar.get(Calendar.SECOND)) {
                                                            return -1;
                                                        } else {
                                                            return 0;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            return 1;
        }
    }
}
