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

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.TreeSet;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Environment;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Object;
import uk.ac.leeds.ccg.andyt.generic.time.Generic_Time;
import uk.ac.leeds.ccg.andyt.vector.geometry.Vector_Envelope2D;

/**
 *
 * @author geoagdt
 */
public class SARIC_MetOfficeParameters extends SARIC_Object {

    /**
     * https://gis.stackexchange.com/questions/29671/mathematics-behind-converting-scale-to-resolution
     * http://www.opengeospatial.org/standards/wmts document states, "The tile
     * matrix set that has scale values calculated based on the dpi defined by
     * OGC specification (dpi assumes 0.28mm as the physical distance of a
     * pixel)." Therefore 0.28 * ScaleDenominator / 1000 is the cellsize in
     * meters.
     */
    public BigDecimal DefaultOGCWMTSResolution = new BigDecimal("0.00028"); // 0.28 mm (defined as the cellsize in mm in the OGC WTMS Specification) converted into meters
    public BigDecimal TwoFiveSix = new BigDecimal("256");

    Vector_Envelope2D bounds; 
    //BigDecimal[] dimensions; // Cellsize and overall bounding box dimensions.
    //ArrayList<String> times;
    TreeSet<Generic_Time> times;
    String layerName;

    private HashMap<String, SARIC_MetOfficeLayerParameters> metOfficeLayerParameters;

    public SARIC_MetOfficeParameters() {
    }

    public SARIC_MetOfficeParameters(SARIC_Environment env) {
        this.se = env;
    }

    public int getNrows(String key) {
        return getMetOfficeLayerParameters().get(key).getNrows();
    }

    public void setNrows(String key, int nrows) {
        getMetOfficeLayerParameters().get(key).setNrows(nrows);
    }

    public int getNcols(String key) {
        return getMetOfficeLayerParameters().get(key).getNcols();
    }

    public void setNcols(String key, int ncols) {
        getMetOfficeLayerParameters().get(key).setNcols(ncols);
    }

    public Vector_Envelope2D getBounds() {
        return bounds;
    }

    public void setBounds(Vector_Envelope2D bounds) {
        this.bounds = bounds;
    }

    public HashMap<Integer, Vector_Envelope2D> getTileBounds(String key) {
        return getMetOfficeLayerParameters().get(key).getTileBounds();
    }

    public Vector_Envelope2D getTileBounds(String key, int row, int col) {
        return getMetOfficeLayerParameters().get(key).getTileBounds(row, col);
    }

//    public ArrayList<String> getTimes() {
//        return times;
//    }
    public TreeSet<Generic_Time> getTimes() {
        return times;
    }

//    public void setTimes(ArrayList<String> times) {
//        this.times = times;
//    }
    public void setTimes(TreeSet<Generic_Time> times) {
        this.times = times;
    }

    public String getLayerName() {
        return layerName;
    }

    public void setLayerName(String layerName) {
        this.layerName = layerName;
    }

    /**
     * @return the parameters
     */
    public HashMap<String, SARIC_MetOfficeLayerParameters> getMetOfficeLayerParameters() {
        if (metOfficeLayerParameters == null) {
            metOfficeLayerParameters = new HashMap<>();
        }
        return metOfficeLayerParameters;
    }

}
