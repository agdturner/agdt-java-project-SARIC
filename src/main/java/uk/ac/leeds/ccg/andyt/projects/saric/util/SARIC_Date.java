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

/**
 *
 * @author geoagdt
 */
public class SARIC_Date
        extends SARIC_YearMonth
        implements Serializable {

    protected int DayOfMonth;

    public SARIC_Date(
            SARIC_Environment se){
        super(se);
        norm();
    }
    
    public SARIC_Date(
            SARIC_Date d) {
        this(d.se,
                d.Year,
                d.Month,
                d.DayOfMonth);
    }

    public SARIC_Date(
            SARIC_Environment se,
            int year,
            int month,
            int dayOfMonth) {
        super(se, year, month);
        DayOfMonth = dayOfMonth;
        init();
    }

    private void init() {
        _Calendar.set(Calendar.DAY_OF_MONTH, DayOfMonth);
    }

    /**
     * Expects s to be of the form "YYYY-MM-DD"
     *
     * @param se
     * @param s
     */
    public SARIC_Date(
            SARIC_Environment se,
            String s) {
        super(se, s);
        String[] split;
        split = s.split("-");
        String s2;
        s2 = split[2];
        s2 = s2.substring(0, 2);
        if (s2.startsWith("0")) {
            s2 = s2.substring(1);
        }
        DayOfMonth = new Integer(s2);
        init();
    }

    public void addDays(int days) {
        _Calendar.add(Calendar.DAY_OF_YEAR, days);
        normalise();
    }

    @Override
    protected void normalise() {
        super.normalise();
        norm();
    }
    
    private void norm() {
        DayOfMonth = _Calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * Returns true iff t is the same day as this.
     *
     * @param t
     * @return
     */
    public boolean isSameDay(SARIC_Date t) {
        if (this.DayOfMonth == t.DayOfMonth) {
            if (this.Month == t.Month) {
                if (this.Year == t.Year) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getDD() {
        String result = "";
        if (DayOfMonth < 10) {
            result += Strings.symbol_0;
        }
        result += Integer.toString(DayOfMonth);
        return result;
    }

    @Override
    public String toString() {
        return getYYYYMMDD();
    }
    
    /**
     * @return A String representation of this in the format YYYY-MM-DD.
     */
    public String getYYYYMMDD() {
        return getYYYYMMDD("-");
    }

    /**
     * @param dateComponentDelimitter String used to separateComponents of the
     * date.
     * @return A String representation of this in the format YYYY-MM-DD where
     * the - is replaced by dateComponentDelimitter.
     */
    public String getYYYYMMDD(String dateComponentDelimitter) {
        String result;
        result = getYYYYMM(dateComponentDelimitter);
        result += dateComponentDelimitter;
        result += getDD();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof SARIC_Date) {
            if (this == o) {
                return true;
            }
            SARIC_Date d;
            d = (SARIC_Date) o;
            if (hashCode() == d.hashCode()) {
                if (DayOfMonth == d.DayOfMonth) {
                    if (Month == d.Month) {
                        if (Year == d.Year) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + this.Year;
        hash = 53 * hash + this.Month;
        hash = 53 * hash + this.DayOfMonth;
        return hash;
    }

    @Override
    public int compareTo(SARIC_YearMonth t) {
        SARIC_Date d = (SARIC_Date) t;
        int superCompareTo = super.compareTo(t);
        if (superCompareTo == 0) {
            if (DayOfMonth > d.DayOfMonth) {
                return 1;
            } else {
                if (DayOfMonth < d.DayOfMonth) {
                    return -1;
                } else {
                    return 0;
                }
            }
        } else {
            return superCompareTo;
        }
    }
}
