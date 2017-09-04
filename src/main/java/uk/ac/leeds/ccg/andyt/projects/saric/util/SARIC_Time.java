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

public class SARIC_Time
        implements Serializable, Comparable {

    private final Calendar calendar;
    private int YEAR;
    private int MONTH;
    private int DAY_OF_MONTH;
    private int HOUR_OF_DAY;
    private int MINUTE;
    private int SECOND;

    public SARIC_Time() {
        calendar = Calendar.getInstance();
    }

    public SARIC_Time(SARIC_Time t) {
        this(t.calendar.get(Calendar.YEAR),
                t.calendar.get(Calendar.MONTH),
                t.calendar.get(Calendar.DAY_OF_MONTH),
                t.calendar.get(Calendar.HOUR),
                t.calendar.get(Calendar.MINUTE),
                t.calendar.get(Calendar.SECOND));
    }

    public SARIC_Time(
            int year,
            int month,
            int dayOfMonth,
            int hourOfDay,
            int minuteOfHour,
            int secondOfMinute) {
        this();
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
     * Expects s to be of the form "YYYY-MM-DD" or "YYYY-MM-DDTHH_MM_SSZ"
     *
     * @param s
     */
    public SARIC_Time(String s) {
        this();
        String[] splitT;
        splitT = s.split("T");
        String[] split;
        split = splitT[0].split("-");
        YEAR = new Integer(split[0]);
        String s2;
        s2 = split[1];
        if (s2.startsWith("0")) {
            s2 = s2.substring(1);
        }
        MONTH = new Integer(s2);
        s2 = split[2];
        if (s2.startsWith("0")) {
            s2 = s2.substring(1);
        }
        DAY_OF_MONTH = new Integer(s2);
        HOUR_OF_DAY = 0;
        MINUTE = 0;
        SECOND = 0;
        if (splitT.length == 2) {
            split = splitT[1].split("_");
            s2 = split[0];
            if (s2.startsWith("0")) {
                s2 = s2.substring(1);
            }
            HOUR_OF_DAY = new Integer(s2);
            s2 = split[1];
            if (s2.startsWith("0")) {
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

    public void setYear(int year) {
        YEAR = year;
        calendar.set(Calendar.YEAR, year);
    }

    public void setMonth(int month) {
        MONTH = month;
        calendar.set(Calendar.MONTH, month);
    }

    public int getDayOfMonth() {
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public void setDayOfMonth(int day) {
        DAY_OF_MONTH = day;
        calendar.set(Calendar.DAY_OF_MONTH, day);
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
        calendar.add(Calendar.HOUR, hours);
        normalise();
    }

    public void addDays(int days) {
        calendar.add(Calendar.DAY_OF_YEAR, days);
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

    @Override
    public String toString() {
        int year;
        year = calendar.get(Calendar.YEAR);
        int month;
        month = calendar.get(Calendar.MONTH);
        int day;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour;
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute;
        minute = calendar.get(Calendar.MINUTE);
        int second;
        second = calendar.get(Calendar.SECOND);
        String result;
        result = Integer.toString(year);
        result += "-";
        if (month < 10) {
            result += "0";
        }
        result += Integer.toString(month);
        result += "-";
        if (day < 10) {
            result += "0";
        }
        result += Integer.toString(day);
        result += "T";
        if (hour < 10) {
            result += "0";
        }
        result += Integer.toString(hour);
        result += ":";
        if (minute < 10) {
            result += "0";
        }
        result += Integer.toString(minute);
        result += ":";
        if (second < 10) {
            result += "0";
        }
        result += Integer.toString(second);
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
