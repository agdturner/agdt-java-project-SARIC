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
        implements Serializable {

    private final Calendar calendar;
        
    public SARIC_Time() {
        this.calendar = Calendar.getInstance();
    }

    public SARIC_Time(SARIC_Time t) {
        this.calendar = Calendar.getInstance();
        this.calendar.set(
                t.calendar.get(Calendar.YEAR),
                t.calendar.get(Calendar.MONTH),
                t.calendar.get(Calendar.DAY_OF_MONTH),
                t.calendar.get(Calendar.HOUR),
                t.calendar.get(Calendar.MINUTE),
                t.calendar.get(Calendar.SECOND));
    }

    public SARIC_Time(
            int year,
            int month,
            int day,
            int hour,
            int minute,
            int second) {
        calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute, second);
    }

    public void addMinutes(int minutes) {
        calendar.add(Calendar.MINUTE, minutes);
    }

    public void addDays(int days) {
        calendar.add(Calendar.DAY_OF_YEAR, days);
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
        if (day < 10) {
            result += "0";
        }
        result += "-";
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
}
