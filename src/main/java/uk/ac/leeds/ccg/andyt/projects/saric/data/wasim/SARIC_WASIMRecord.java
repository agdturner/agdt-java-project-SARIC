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
package uk.ac.leeds.ccg.andyt.projects.saric.data.wasim;

/**
 *
 * @author geoagdt
 */
public class SARIC_WASIMRecord {

    long ID;
    double Easting;
    double Northing;
    int daysSinceLast2mmRainfall;
    double accumulatedRainfallOverTheLast10Days;
    double forecastRainfallInTheNext24Hours;
    double forecastRainfallIn24to48Hours;
    double forecastRainfallIn48to72Hours;
    double forecastRainfallIn72to96Hours;
    double forecastRainfallIn96to120Hours;

    public SARIC_WASIMRecord(
            long ID,
            double Easting,
            double Northing,
            int daysSinceLast2mmRainfall,
            double accumulatedRainfallOverTheLast10Days,
            double forecastRainfallInTheNext24Hours,
            double forecastRainfallIn24to48Hours,
    double forecastRainfallIn48to72Hours,
    double forecastRainfallIn72to96Hours,
    double forecastRainfallIn96to120Hours) {
        this.ID = ID;
        this.Easting = Easting;
        this.Northing = Northing;
        this.daysSinceLast2mmRainfall = daysSinceLast2mmRainfall;
        this.accumulatedRainfallOverTheLast10Days = accumulatedRainfallOverTheLast10Days;
        this.forecastRainfallInTheNext24Hours = forecastRainfallInTheNext24Hours;
        this.forecastRainfallIn24to48Hours =  forecastRainfallIn24to48Hours;
    this.forecastRainfallIn48to72Hours =  forecastRainfallIn48to72Hours;
    this.forecastRainfallIn72to96Hours =  forecastRainfallIn72to96Hours;
    this.forecastRainfallIn96to120Hours =  forecastRainfallIn96to120Hours;
    }

    @Override
    public String toString() {
        return "" + ID + "," + Easting + "," + Northing + ","
                + daysSinceLast2mmRainfall + "," 
                + accumulatedRainfallOverTheLast10Days + "," 
                + forecastRainfallInTheNext24Hours + "," 
                + forecastRainfallIn24to48Hours + "," 
                + forecastRainfallIn48to72Hours + "," 
                + forecastRainfallIn72to96Hours + "," 
                + forecastRainfallIn96to120Hours; 
    }
    
}
