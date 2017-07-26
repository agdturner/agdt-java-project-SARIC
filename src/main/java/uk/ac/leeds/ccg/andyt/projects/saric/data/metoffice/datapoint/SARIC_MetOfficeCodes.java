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
package uk.ac.leeds.ccg.andyt.projects.saric.data.metoffice.datapoint;

import java.util.HashMap;

/**
 *
 * @author geoagdt
 */
public class SARIC_MetOfficeCodes {

    protected HashMap<String, String> DescriptionCodes;

    public SARIC_MetOfficeCodes() {
    }

    public HashMap<String, String> getDescriptionCodes() {
        if (DescriptionCodes == null) {
            DescriptionCodes = new HashMap<String, String>();
            DescriptionCodes.put("NA", "Not available");
            DescriptionCodes.put("0", "Clear night");
            DescriptionCodes.put("1", "Sunny day");
            DescriptionCodes.put("2", "Partly cloudy (night)");
            DescriptionCodes.put("3", "Partly cloudy (day)");
            DescriptionCodes.put("4", "Not used");
            DescriptionCodes.put("5", "Mist");
            DescriptionCodes.put("6", "Fog");
            DescriptionCodes.put("7", "Cloudy");
            DescriptionCodes.put("8", "Overcast");
            DescriptionCodes.put("9", "Light rain shower (night)");
            DescriptionCodes.put("10", "Light rain shower (day)");
            DescriptionCodes.put("11", "Drizzle");
            DescriptionCodes.put("12", "Light rain");
            DescriptionCodes.put("13", "Heavy rain shower (night)");
            DescriptionCodes.put("14", "Heavy rain shower (day)");
            DescriptionCodes.put("15", "Heavy rain");
            DescriptionCodes.put("16", "Sleet shower (night)");
            DescriptionCodes.put("17", "Sleet shower (day)");
            DescriptionCodes.put("18", "Sleet");
            DescriptionCodes.put("19", "Hail shower (night)");
            DescriptionCodes.put("20", "Hail shower (day)");
            DescriptionCodes.put("21", "Hail");
            DescriptionCodes.put("22", "Light snow shower (night)");
            DescriptionCodes.put("23", "Light snow shower (day)");
            DescriptionCodes.put("24", "Light snow");
            DescriptionCodes.put("25", "Heavy snow shower (night)");
            DescriptionCodes.put("26", "Heavy snow shower (day)");
            DescriptionCodes.put("27", "Heavy snow");
            DescriptionCodes.put("28", "Thunder shower (night)");
            DescriptionCodes.put("29", "Thunder shower (day)");
            DescriptionCodes.put("30", "Thunder");
        }
        return DescriptionCodes;
    }
}
