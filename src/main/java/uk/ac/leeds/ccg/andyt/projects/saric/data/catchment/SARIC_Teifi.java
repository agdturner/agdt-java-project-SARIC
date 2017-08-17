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

import java.io.File;
import java.math.BigDecimal;
import uk.ac.leeds.ccg.andyt.agdtgeotools.AGDT_Shapefile;
import uk.ac.leeds.ccg.andyt.generic.math.Generic_BigDecimal;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Grid2DSquareCellDouble;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Grid2DSquareCellDoubleFactory;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Environment;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Object;
import uk.ac.leeds.ccg.andyt.vector.geometry.Vector_Envelope2D;
import uk.ac.leeds.ccg.andyt.vector.geometry.Vector_Point2D;

/**
 *
 * @author geoagdt
 */
public class SARIC_Teifi extends SARIC_Catchment {

    public SARIC_Teifi(SARIC_Environment se) {
        super(se);
    }

    public AGDT_Shapefile getNRFAAGDT_Shapefile(){
        AGDT_Shapefile result;
        String name = "62001.shp";
        result = getAGDT_Shapefile(name);
        return result;
    }
    
    public AGDT_Shapefile getWWAGDT_Shapefile(){
        AGDT_Shapefile result;
        String name = "WW_area.shp";
        result = getAGDT_Shapefile(name);
        return result;
    }
    
    public AGDT_Shapefile getAGDT_Shapefile(String name){
        AGDT_Shapefile result;
        File dir = new File(
                sf.getInputDataCatchmentBoundariesDir(),
                "Teifi");
        result = getAGDT_Shapefile(name, dir);
        return result;
    }
    
    /**
     * Teifi Bounding Box: MinX 218749.5025726173; MaxX 279871.8842591159; MinY
     * 231291.52626209427; MaxY 270891.8510279902.
     *
     * @return
     */
    public Vector_Envelope2D getBounds() {
        Vector_Envelope2D result;
        Vector_Point2D aPoint;
        aPoint = new Vector_Point2D(
                se.getVector_Environment(),
                new BigDecimal("218749.5025726173"),
                new BigDecimal("231291.52626209427"));
        Vector_Point2D bPoint;
        bPoint = new Vector_Point2D(
                se.getVector_Environment(),
                new BigDecimal("279871.8842591159"),
                new BigDecimal("270891.8510279902"));
        result = new Vector_Envelope2D(aPoint, bPoint);
        return result;
    }
    
    /**
     * Teifi Bounding Box: MinX 218000; MaxX 280000; MinY
     * 231000; MaxY 271000.
     * @return
     */
    public Vector_Envelope2D get1KMGridBounds() {
        Vector_Envelope2D result;
        Vector_Point2D aPoint;
        aPoint = new Vector_Point2D(
                se.getVector_Environment(),
                new BigDecimal("218000"),
                new BigDecimal("231000"));
        Vector_Point2D bPoint;
        bPoint = new Vector_Point2D(
                se.getVector_Environment(),
                new BigDecimal("280000"),
                new BigDecimal("271000"));
        result = new Vector_Envelope2D(aPoint, bPoint);
        return result;
    }
    
    /**
     * Teifi Bounding Box: MinX 218749.5025726173; MaxX 279871.8842591159; MinY
     * 231291.52626209427; MaxY 270891.8510279902.
     *
     * @return
     */
    public Grids_Grid2DSquareCellDouble get1KMGrid() {
        Grids_Grid2DSquareCellDouble result;
        Vector_Envelope2D bounds;
        bounds = get1KMGridBounds();
        Grids_Grid2DSquareCellDoubleFactory f;
        File dir;
        dir = new File(
                sf.getGeneratedDataCatchmentBoundariesDir(),
                "Teifi");
        f = se.getGrids_Environment().get_Grid2DSquareCellProcessor()._Grid2DSquareCellDoubleFactory;
        BigDecimal[] dimensions;
        dimensions = new BigDecimal[5];
        dimensions[0] = new BigDecimal("1000"); //Cellsize
        dimensions[1] = bounds._xmin; //XMIN
        dimensions[2] = bounds._ymin; //YMIN
        dimensions[3] = bounds._xmax; //XMAX
        dimensions[4] = bounds._ymax; //YMAX
        long nrows;
        long ncols;
        nrows = Generic_BigDecimal.divideNoRounding(dimensions[3].subtract(dimensions[1]), dimensions[0]).longValueExact();
        ncols = Generic_BigDecimal.divideNoRounding(dimensions[4].subtract(dimensions[2]), dimensions[0]).longValueExact();
        result = f.create(dir, nrows, ncols, dimensions, ge, true);
        return result;
    }

}
