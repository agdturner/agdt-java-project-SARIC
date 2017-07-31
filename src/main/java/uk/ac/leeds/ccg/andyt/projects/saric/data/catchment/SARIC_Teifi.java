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
public class SARIC_Teifi extends SARIC_Object {

    private SARIC_Teifi() {
    }

    public SARIC_Teifi(SARIC_Environment se) {
        super(se);
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

}
