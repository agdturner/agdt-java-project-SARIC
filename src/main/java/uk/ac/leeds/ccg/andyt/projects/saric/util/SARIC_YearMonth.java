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
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Object;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Strings;

/**
 *
 * @author geoagdt
 */
public class SARIC_YearMonth
        extends SARIC_Object
        implements Serializable, Comparable<SARIC_YearMonth> {

    // For convenience
    protected transient SARIC_Strings Strings;

    protected Calendar _Calendar;
    protected int Year;
    protected int Month;

    public SARIC_YearMonth(
            SARIC_Environment se){
        Strings = se.getStrings();
        _Calendar = Calendar.getInstance();
        norm();
    }
    
    public SARIC_YearMonth(
            SARIC_Environment se,
            SARIC_YearMonth t) {
        this(se, t.Year, t.Month);
    }

    public SARIC_YearMonth(
            SARIC_Environment se,
            int year,
            int month) {
        this(se);
        Year = year;
        Month = month;
        init();
    }

    private void init() {
        _Calendar.set(Calendar.YEAR, Year);
        _Calendar.set(Calendar.MONTH, Month);
        norm();
    }
    
    /**
     * Expects s to be of the form "YYYY-MM"
     *
     * @param se
     * @param s
     */
    public SARIC_YearMonth(
            SARIC_Environment se,
            String s) {
        this(se);
        String[] split;
        split = s.split("-");
        Year = new Integer(split[0]);
        String s2;
        s2 = split[1];
        if (s2.startsWith("0")) {
            s2 = s2.substring(1);
        }
        Month = new Integer(s2);
        init();
    }

    public void setYear(int year) {
        Year = year;
        _Calendar.set(Calendar.YEAR, year);
    }

    public void setMonth(int month) {
        Month = month;
        _Calendar.set(Calendar.MONTH, month);
    }

    protected void normalise() {
        norm();
    }
    
    private void norm() {
        Year = _Calendar.get(Calendar.YEAR);
        Month = _Calendar.get(Calendar.MONTH);
    }

    /**
     * Returns true iff t is the same day as this.
     *
     * @param t
     * @return
     */
    public boolean isSameDay(SARIC_YearMonth t) {
        if (this.Month == t.Month) {
            if (this.Year == t.Year) {
                return true;
            }
        }
        return false;
    }

    /**
     * Assume the year has 4 digits.
     *
     * @return
     */
    public String getYYYY() {
        return Integer.toString(Year);
    }

    /**
     * The month always has 2 characters. 01 is January 02 is February ... 12 is
     * December
     *
     * @return
     */
    public String getMM() {
        String result = "";
        if (Month < 10) {
            result = Strings.symbol_0;
        }
        result += Integer.toString(Month);
        return result;
    }

    @Override
    public String toString() {
        return getYYYYMM();
    }

    /**
     *
     * @return String representing year and month in YYYY-MM format
     */
    public String getYYYYMM() {
        return getYYYYMM("-");
    }

    public String getYYYYMM(String delimeter) {
        String result;
        result = getYYYY();
        result += delimeter;
        result += getMM();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof SARIC_YearMonth) {
            if (this == o) {
                return true;
            }
            SARIC_YearMonth d;
            d = (SARIC_YearMonth) o;
            if (hashCode() == d.hashCode()) {
                if (Month == d.Month) {
                    if (Year == d.Year) {
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
        hash = 73 * hash + Year;
        hash = 73 * hash + Month;
        return hash;
    }

    @Override
    public int compareTo(SARIC_YearMonth t) {
        if (Year > t.Year) {
            return 1;
        } else {
            if (Year < t.Year) {
                return -1;
            } else {
                if (Month > t.Month) {
                    return 1;
                } else {
                    if (Month < t.Month) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            }
        }
    }
}
