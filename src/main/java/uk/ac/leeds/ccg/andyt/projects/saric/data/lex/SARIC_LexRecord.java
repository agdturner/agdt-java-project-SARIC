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
package uk.ac.leeds.ccg.andyt.projects.saric.data.lex;

/**
 *
 * @author geoagdt
 */
public class SARIC_LexRecord {

    String ID;
    double Easting;
    double Northing;
    double observedRainfall10DaysAgo;
    double observedRainfall9DaysAgo;
    double observedRainfall8DaysAgo;
    double observedRainfall7DaysAgo;
    double observedRainfall6DaysAgo;
    double observedRainfall5DaysAgo;
    double observedRainfall4DaysAgo;
    double observedRainfall3DaysAgo;
    double observedRainfall2DaysAgo;
    double observedRainfallInTheLast24Hours;
    double forecastRainfallInTheNext24Hours;
    double forecastRainfallIn24to48Hours;
    double forecastRainfallIn48to72Hours;
    double forecastRainfallIn72to96Hours;
    double forecastRainfallIn96to120Hours;

    public SARIC_LexRecord(
            String ID,
            double Easting,
            double Northing,
            double observedRainfall10DaysAgo,
            double observedRainfall9DaysAgo,
            double observedRainfall8DaysAgo,
            double observedRainfall7DaysAgo,
            double observedRainfall6DaysAgo,
            double observedRainfall5DaysAgo,
            double observedRainfall4DaysAgo,
            double observedRainfall3DaysAgo,
            double observedRainfall2DaysAgo,
            double observedRainfallInTheLast24Hours,
            double forecastRainfallInTheNext24Hours,
            double forecastRainfallIn24to48Hours,
            double forecastRainfallIn48to72Hours,
            double forecastRainfallIn72to96Hours,
            double forecastRainfallIn96to120Hours) {
        this.ID = ID;
        this.Easting = Easting;
        this.Northing = Northing;
        this.observedRainfall10DaysAgo = observedRainfall10DaysAgo;
        this.observedRainfall9DaysAgo = observedRainfall9DaysAgo;
        this.observedRainfall8DaysAgo = observedRainfall8DaysAgo;
        this.observedRainfall7DaysAgo = observedRainfall7DaysAgo;
        this.observedRainfall6DaysAgo = observedRainfall6DaysAgo;
        this.observedRainfall5DaysAgo = observedRainfall5DaysAgo;
        this.observedRainfall4DaysAgo = observedRainfall4DaysAgo;
        this.observedRainfall3DaysAgo = observedRainfall3DaysAgo;
        this.observedRainfall2DaysAgo = observedRainfall2DaysAgo;
        this.observedRainfallInTheLast24Hours = observedRainfallInTheLast24Hours;
        this.forecastRainfallInTheNext24Hours = forecastRainfallInTheNext24Hours;
        this.forecastRainfallIn24to48Hours = forecastRainfallIn24to48Hours;
        this.forecastRainfallIn48to72Hours = forecastRainfallIn48to72Hours;
        this.forecastRainfallIn72to96Hours = forecastRainfallIn72to96Hours;
        this.forecastRainfallIn96to120Hours = forecastRainfallIn96to120Hours;
    }

    @Override
    public String toString() {
        return "" + ID + "," + Easting + "," + Northing + ","
                + observedRainfall10DaysAgo + ","
                + observedRainfall9DaysAgo + ","
                + observedRainfall8DaysAgo + ","
                + observedRainfall7DaysAgo + ","
                + observedRainfall6DaysAgo + ","
                + observedRainfall5DaysAgo + ","
                + observedRainfall4DaysAgo + ","
                + observedRainfall3DaysAgo + ","
                + observedRainfall2DaysAgo + ","
                + observedRainfallInTheLast24Hours + ","
                + forecastRainfallInTheNext24Hours + ","
                + forecastRainfallIn24to48Hours + ","
                + forecastRainfallIn48to72Hours + ","
                + forecastRainfallIn72to96Hours + ","
                + forecastRainfallIn96to120Hours;
    }

}
