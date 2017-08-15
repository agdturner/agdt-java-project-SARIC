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
package uk.ac.leeds.ccg.andyt.projects.saric.data.metoffice.datapoint.site;

import java.io.Serializable;

/**
 *
 * @author geoagdt
 */
public class SARIC_Site implements Serializable {
    
//    private String LocationType;
//    private String LocationName;
//    private String Region;
    private String name;
    private double longitude;
    private double latitude;
    private int id;
    private double elevation;
//    private String nationalPark;

    protected SARIC_Site(){}

    @Override
    public String toString() {
        return "name=\"" + name 
                + "\" longitude=\"" + longitude
                + "\" latitude=\"" + latitude
                + "\" id=\"" + id
                + "\" elevation=\"" + elevation;
    }
//    /**
//     * @return the LocationType
//     */
//    public String getLocationType() {
//        return LocationType;
//    }
//
//    /**
//     * @param LocationType the LocationType to set
//     */
//    public void setLocationType(String LocationType) {
//        this.LocationType = LocationType;
//    }
//
//    /**
//     * @return the LocationName
//     */
//    public String getLocationName() {
//        return LocationName;
//    }
//
//    /**
//     * @param LocationName the LocationName to set
//     */
//    public void setLocationName(String LocationName) {
//        this.LocationName = LocationName;
//    }
//
//    /**
//     * @return the Region
//     */
//    public String getRegion() {
//        return Region;
//    }
//
//    /**
//     * @param Region the Region to set
//     */
//    public void setRegion(String Region) {
//        this.Region = Region;
//    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param Name the name to set
     */
    public void setName(String Name) {
        this.name = Name;
    }

    /**
     * @return the longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * @param longitude the longitude to set
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * @return the latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * @param latitude the latitude to set
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the elevation
     */
    public double getElevation() {
        return elevation;
    }

    /**
     * @param elevation the elevation to set
     */
    public void setElevation(double elevation) {
        this.elevation = elevation;
    }
    
    
    
}
