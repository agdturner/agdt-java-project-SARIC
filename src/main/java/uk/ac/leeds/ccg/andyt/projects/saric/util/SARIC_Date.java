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
import java.time.Month;
import java.time.YearMonth;
import java.util.Objects;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Environment;

/**
 *
 * @author geoagdt
 */
public class SARIC_Date extends SARIC_YearMonth implements Serializable {

    public LocalDate LD;

    public SARIC_Date(SARIC_Environment se) {
        super(se);
    }

    public SARIC_Date(SARIC_Date d) {
        this(d.se, d.LD);
    }

    public SARIC_Date(SARIC_Time t) {
        this(t.se, t.LDT.toLocalDate());
    }

    public SARIC_Date(SARIC_Environment se, LocalDate d) {
        super(se, YearMonth.from(d));
        LD = d;
    }

    public SARIC_Date(SARIC_Environment se, int year, int month, int day) {
        super(se, YearMonth.of(year, month));
        LD = LocalDate.of(year, month, day);
    }

    /**
     * Expects s to be of the form "YYYY-MM-DD"
     *
     * @param se
     * @param s
     */
    public SARIC_Date(SARIC_Environment se, String s) {
        super(se, s);
        String[] split;
        split = s.split("-");
        String s2;
        s2 = split[2];
        s2 = s2.substring(0, 2);
        if (s2.startsWith("0")) {
            s2 = s2.substring(1);
        }
        LD = LocalDate.of(YM.getYear(), YM.getMonth(), new Integer(s2));
    }

    public void addDays(int days) {
        LD = LD.plusDays(days);
        super.YM = YearMonth.from(LD);
    }

    /**
     * Returns true iff t is the same day as this.
     *
     * @param t
     * @return
     */
    public boolean isSameDay(SARIC_Date t) {
        return LD.isEqual(t.LD);
    }

    public String getDD() {
        String result = "";
        int dayOfMonth = LD.getDayOfMonth();
        if (dayOfMonth < 10) {
            result += Strings.symbol_0;
        }
        result += Integer.toString(dayOfMonth);
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
                return this.isSameDay(d);
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(LD);
        return hash;
    }

    @Override
    public int compareTo(SARIC_YearMonth t) {
        SARIC_Date d = (SARIC_Date) t;
        if (LD.isAfter(d.LD)) {
            return 1;
        } else {
            if (LD.isBefore(d.LD)) {
                return -1;
            } else {
                return 0;
            }
        }
    }
}
