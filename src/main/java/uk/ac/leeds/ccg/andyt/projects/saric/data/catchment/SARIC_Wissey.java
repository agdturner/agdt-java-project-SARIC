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
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Environment;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Object;
import uk.ac.leeds.ccg.andyt.vector.geometry.Vector_Envelope2D;
import uk.ac.leeds.ccg.andyt.vector.geometry.Vector_Point2D;

/**
 *
 * @author geoagdt
 */
public class SARIC_Wissey extends SARIC_Object {

    private SARIC_Wissey() {
    }

    public SARIC_Wissey(SARIC_Environment se) {
        super(se);
    }

    /**
     * Wissey Bounding Box: MinX 562996.9681000011; MaxX 599975.0000299839; MinY
     * 288600.00000000186; MaxY 313620.0.
     *
     * @return
     */
    public Vector_Envelope2D getBounds() {
        Vector_Envelope2D result;
        Vector_Point2D aPoint;
        aPoint = new Vector_Point2D(
                se.getVector_Environment(),
                new BigDecimal("562996.9681000011"),
                new BigDecimal("288600.00000000186"));
        Vector_Point2D bPoint;
        bPoint = new Vector_Point2D(
                se.getVector_Environment(),
                new BigDecimal("599975.0000299839"),
                new BigDecimal("313620.0"));
        result = new Vector_Envelope2D(aPoint, bPoint);
        return result;
    }
    
}
