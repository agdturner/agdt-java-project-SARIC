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

/**
 *
 * @author geoagdt
 */
public class SARIC_YearMonth implements Serializable, Comparable {

    protected final Calendar calendar;
    protected int YEAR;
    protected int MONTH;

    public SARIC_YearMonth() {
        calendar = Calendar.getInstance();
    }

    public SARIC_YearMonth(SARIC_YearMonth t) {
        this(t.calendar.get(Calendar.YEAR),
                t.calendar.get(Calendar.MONTH));
    }

    public SARIC_YearMonth(
            int year,
            int month) {
        this();
        calendar.set(
                Calendar.YEAR,
                year);
        calendar.set(
                Calendar.MONTH,
                month);
        normalise();
    }

    /**
     * Expects s to be of the form "YYYY-MM"
     *
     * @param s
     */
    public SARIC_YearMonth(String s) {
        this();
        String[] split;
        split = s.split("-");
        YEAR = new Integer(split[0]);
        String s2;
        s2 = split[1];
        if (s2.startsWith("0")) {
            s2 = s2.substring(1);
        }
        MONTH = new Integer(s2);
        calendar.set(
                Calendar.YEAR,
                YEAR);
        calendar.set(
                Calendar.MONTH,
                MONTH);
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

    public void addDays(int days) {
        calendar.add(Calendar.DAY_OF_YEAR, days);
        normalise();
    }

    private void normalise() {
        YEAR = calendar.get(Calendar.YEAR);
        MONTH = calendar.get(Calendar.MONTH);
    }

    /**
     * Returns true iff t is the same day as this.
     *
     * @param t
     * @return
     */
    public boolean isSameDay(SARIC_YearMonth t) {
            if (this.MONTH == t.MONTH) {
                if (this.YEAR == t.YEAR) {
                    return true;
                }
            }
        return false;
    }

    /**
     *
     * @return String representing year and month in YYYY-MM format
     */
   @Override
    public String toString() {
        String result;
        int year;
        year = calendar.get(Calendar.YEAR);
        int month;
        month = calendar.get(Calendar.MONTH);
        result = Integer.toString(year);
        result += "-";
        if (month < 10) {
            result += "0";
        }
        result += Integer.toString(month);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof SARIC_Time) {
            if (this == o) {
                return true;
            }
            SARIC_YearMonth d;
            d = (SARIC_YearMonth) o;
            if (hashCode() == d.hashCode()) {
                    if (calendar.get(Calendar.MONTH) == d.calendar.get(Calendar.MONTH)) {
                        if (calendar.get(Calendar.YEAR) == d.calendar.get(Calendar.YEAR)) {
                            return true;
                        }
                    }
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + this.YEAR;
        hash = 73 * hash + this.MONTH;
        return hash;
    }

    @Override
    public int compareTo(Object o) {
        if (o == null) {
            return 1;
        }
        if (o instanceof SARIC_YearMonth) {
            SARIC_YearMonth d;
            d = (SARIC_YearMonth) o;
            if (calendar.get(Calendar.YEAR) > d.calendar.get(Calendar.YEAR)) {
                return 1;
            } else {
                if (calendar.get(Calendar.YEAR) < d.calendar.get(Calendar.YEAR)) {
                    return -1;
                } else {
                    if (calendar.get(Calendar.MONTH) > d.calendar.get(Calendar.MONTH)) {
                        return 1;
                    } else {
                        if (calendar.get(Calendar.MONTH) < d.calendar.get(Calendar.MONTH)) {
                            return -1;
                        } else {
                            return 0;
                        }
                    }
                }
            }
        } else {
            return 1;
        }
    }
}
