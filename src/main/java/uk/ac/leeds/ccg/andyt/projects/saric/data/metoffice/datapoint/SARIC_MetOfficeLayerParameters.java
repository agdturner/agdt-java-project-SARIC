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
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Environment;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Object;
import uk.ac.leeds.ccg.andyt.vector.geometry.Vector_Envelope2D;
import uk.ac.leeds.ccg.andyt.vector.geometry.Vector_Point2D;

/**
 *
 * @author geoagdt
 */
public class SARIC_MetOfficeLayerParameters extends SARIC_Object {

    int nrows;
    int ncols;
    BigDecimal cellsize;
    HashMap<Integer, Vector_Envelope2D> tileBounds;
    SARIC_MetOfficeParameters metOfficeParameters;

    public SARIC_MetOfficeLayerParameters(            SARIC_Environment se,            BigDecimal cellsize,
            SARIC_MetOfficeParameters metOfficeParameters) {
        super(se);
        this.cellsize = cellsize;
        this.metOfficeParameters = metOfficeParameters;
    }

    public int getNrows() {
        return nrows;
    }

    public void setNrows(int nrows) {
        this.nrows = nrows;
    }

    public int getNcols() {
        return ncols;
    }

    public void setNcols(int ncols) {
        this.ncols = ncols;
    }

    public BigDecimal getCellsize() {
        return cellsize;
    }

    public HashMap<Integer, Vector_Envelope2D> getTileBounds() {
        if (tileBounds == null) {
            tileBounds = new HashMap<>();
        }
        return tileBounds;
    }

    public Vector_Envelope2D getTileBounds(int row, int col) {
        Vector_Envelope2D result;
        tileBounds = getTileBounds();
        int pos;
        pos = (row * ncols) + col;
        result = tileBounds.get(pos);
        if (result == null) {
            BigDecimal tileSize;
            tileSize = metOfficeParameters.TwoFiveSix.multiply(cellsize);
            Vector_Point2D p;
            BigDecimal xmin;
            xmin = metOfficeParameters.bounds.XMin.add(
                    new BigDecimal(col).multiply(tileSize));
            BigDecimal xmax;
            xmax = xmin.add(tileSize);
            BigDecimal ymax;
            ymax = metOfficeParameters.bounds.YMax.subtract(
                    new BigDecimal(row).multiply(tileSize));
            BigDecimal ymin;
            ymin = ymax.subtract(tileSize);
            p = new Vector_Point2D(se.vectorEnv, xmin, ymin);
            result = p.getEnvelope2D();
            p = new Vector_Point2D(se.vectorEnv, xmax, ymax);
            result = result.envelope(p.getEnvelope2D());
        }
        return result;
    }

}
