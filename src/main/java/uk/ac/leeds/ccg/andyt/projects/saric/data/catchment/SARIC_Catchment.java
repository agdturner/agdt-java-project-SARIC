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
import uk.ac.leeds.ccg.andyt.agdtgeotools.AGDT_Geotools;
import uk.ac.leeds.ccg.andyt.agdtgeotools.AGDT_Shapefile;
import uk.ac.leeds.ccg.andyt.generic.math.Generic_BigDecimal;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Grid2DSquareCellDouble;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Grid2DSquareCellDoubleFactory;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Environment;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Object;
import uk.ac.leeds.ccg.andyt.projects.saric.io.SARIC_Files;
import uk.ac.leeds.ccg.andyt.vector.geometry.Vector_Envelope2D;
import uk.ac.leeds.ccg.andyt.vector.geometry.Vector_Point2D;

/**
 *
 * @author geoagdt
 */
public abstract class SARIC_Catchment extends SARIC_Object {

    // For convenience
    SARIC_Files sf;
    Grids_Environment ge;

    String catchmentName;

    protected SARIC_Catchment() {
    }

    public SARIC_Catchment(
            SARIC_Environment se,
            String catchmentName) {
        super(se);
        sf = se.getFiles();
        ge = se.getGrids_Environment();
        this.catchmentName = catchmentName;
    }

    public AGDT_Shapefile getAGDT_Shapefile(String name, File dir) {
        AGDT_Shapefile result;
        File f = AGDT_Geotools.getShapefile(dir, name, false);
        result = new AGDT_Shapefile(f);
        return result;
    }

    public AGDT_Shapefile getNRFAAGDT_Shapefile(String name) {
        AGDT_Shapefile result;
        result = getAGDT_Shapefile(name);
        return result;
    }

    public abstract AGDT_Shapefile getWaterCompanyAGDT_Shapefile();

    public AGDT_Shapefile getAGDT_Shapefile(String name) {
        AGDT_Shapefile result;
        File dir = new File(
                sf.getInputDataCatchmentBoundariesDir(),
                catchmentName);
        result = SARIC_Catchment.this.getAGDT_Shapefile(name, dir);
        return result;
    }

    public abstract Vector_Envelope2D getBounds();

    public Vector_Envelope2D getBounds(
            BigDecimal xmin,
            BigDecimal ymin,
            BigDecimal xmax,
            BigDecimal ymax) {
        Vector_Envelope2D result;
        Vector_Point2D aPoint;
        aPoint = new Vector_Point2D(
                se.getVector_Environment(),
                xmin,
                ymin);
        Vector_Point2D bPoint;
        bPoint = new Vector_Point2D(
                se.getVector_Environment(),
                xmax,
                ymax);
        result = new Vector_Envelope2D(aPoint, bPoint);
        return result;
    }

    public abstract Vector_Envelope2D get1KMGridBounds();

    /**
     * https://www.ordnancesurvey.co.uk/resources/maps-and-geographic-resources/the-national-grid.html
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
                catchmentName);
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
        ncols = Generic_BigDecimal.divideNoRounding(dimensions[3].subtract(dimensions[1]), dimensions[0]).longValueExact();
        nrows = Generic_BigDecimal.divideNoRounding(dimensions[4].subtract(dimensions[2]), dimensions[0]).longValueExact();
        result = f.create(dir, nrows, ncols, dimensions, ge, true);
        return result;
    }

    public Vector_Envelope2D getBoundsBuffered(BigDecimal buffer) {
        Vector_Envelope2D bounds;
        bounds = getBounds();
        // Buffer
        if (buffer != null) {
            // Add buffer to bounds
            bounds._xmin = bounds._xmin.subtract(buffer);
            bounds._xmax = bounds._xmax.add(buffer);
            bounds._ymin = bounds._ymin.subtract(buffer);
            bounds._ymax = bounds._ymax.add(buffer);
        }
        return bounds;
    }
}
