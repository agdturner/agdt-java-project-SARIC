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
import uk.ac.leeds.ccg.andyt.agdtgeotools.AGDT_Geotools;
import uk.ac.leeds.ccg.andyt.agdtgeotools.AGDT_Shapefile;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Environment;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Object;
import uk.ac.leeds.ccg.andyt.projects.saric.io.SARIC_Files;

/**
 *
 * @author geoagdt
 */
public abstract class SARIC_Catchment extends SARIC_Object {

    // For convenience
    SARIC_Files sf;
    Grids_Environment ge;

    private SARIC_Catchment() {
    }

    public SARIC_Catchment(SARIC_Environment se) {
        super(se);
        sf = se.getFiles();
        ge = se.getGrids_Environment();
    }

    public AGDT_Shapefile getAGDT_Shapefile(String name, File dir) {
        AGDT_Shapefile result;
        File f = AGDT_Geotools.getShapefile(dir, name, false);
        result = new AGDT_Shapefile(f);
        return result;
    }
}
