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
package uk.ac.leeds.ccg.andyt.projects.saric.data.catchment;

import java.math.BigDecimal;
import uk.ac.leeds.ccg.andyt.agdtgeotools.AGDT_Shapefile;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Environment;
import uk.ac.leeds.ccg.andyt.vector.geometry.Vector_Envelope2D;

/**
 *
 * @author geoagdt
 */
public class SARIC_Teifi extends SARIC_Catchment {

    protected SARIC_Teifi(){}
    
    public SARIC_Teifi(SARIC_Environment se) {
        super(se, "Teifi");
    }

    /**
     * From the National River Flow Archive.
     * http://nrfa.ceh.ac.uk/data/station/spatial/62001
     * @return 
     */
    public AGDT_Shapefile getNRFAAGDT_Shapefile(){
        return getNRFAAGDT_Shapefile("62001.shp");
    }
    
    /**
     * Provided by Dwr Cymru.
     * @return 
     */
    public AGDT_Shapefile getWaterCompanyAGDT_Shapefile(){
        return getAGDT_Shapefile("WW_area.shp");
    }
    
    /**
     * Teifi Bounding Box: MinX 218749.5025726173; MaxX 279871.8842591159; MinY
     * 231291.52626209427; MaxY 270891.8510279902.
     *
     * @return
     */
    public Vector_Envelope2D getBounds() {
        return getBounds(
                new BigDecimal("218749.5025726173"),
                new BigDecimal("231291.52626209427"),
                new BigDecimal("279871.8842591159"),
                new BigDecimal("270891.8510279902"));
    }
    
    /**
     * Teifi Bounding Box: MinX 218000; MaxX 280000; MinY
     * 231000; MaxY 271000.
     * 
     * @return
     */
    public Vector_Envelope2D get1KMGridBounds() {
        return getBounds(
                new BigDecimal("218000"),
                new BigDecimal("231000"),
                new BigDecimal("280000"),
                new BigDecimal("271000"));
    }

}
