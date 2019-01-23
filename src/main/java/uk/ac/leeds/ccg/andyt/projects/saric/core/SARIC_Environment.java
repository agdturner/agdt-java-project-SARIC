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
package uk.ac.leeds.ccg.andyt.projects.saric.core;

import java.io.File;
import uk.ac.leeds.ccg.andyt.generic.core.Generic_Environment;
import uk.ac.leeds.ccg.andyt.generic.time.Generic_Time;
import uk.ac.leeds.ccg.andyt.geotools.core.Geotools_Environment;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.projects.saric.data.catchment.SARIC_Teifi;
import uk.ac.leeds.ccg.andyt.projects.saric.data.catchment.SARIC_Wissey;
import uk.ac.leeds.ccg.andyt.projects.saric.data.metoffice.datapoint.SARIC_MetOfficeParameters;
import uk.ac.leeds.ccg.andyt.projects.saric.io.SARIC_Files;
import uk.ac.leeds.ccg.andyt.vector.core.Vector_Environment;

/**
 *
 * @author geoagdt
 */
public class SARIC_Environment {

    public Generic_Environment ge;
    public SARIC_Strings Strings;
    public SARIC_Files Files;
    
    public final SARIC_MetOfficeParameters MetOfficeParameters;
    public final Grids_Environment Grids_Env;
    public final Vector_Environment Vector_Env;
    public final Geotools_Environment Geotools_Env;

    Generic_Time Time;
    SARIC_Wissey Wissey;
    SARIC_Teifi Teifi;

    public SARIC_Environment(Generic_Environment ge, File dataDir) {
        this.ge = ge;
        Strings = new SARIC_Strings();
        Files = new SARIC_Files(Strings, dataDir);
        MetOfficeParameters = new SARIC_MetOfficeParameters(this);
        Grids_Env = new Grids_Environment(Files.getGeneratedDataGridsDir());
        Vector_Env = new Vector_Environment(Grids_Env);
        Geotools_Env = new Geotools_Environment();
    }

    public void setTime(Generic_Time time) {
        Time = time;
    }

    public SARIC_MetOfficeParameters getMetOfficeParameters() {
        return MetOfficeParameters;
    }

    public Generic_Time getTime() {
        return Time;
    }

    public SARIC_Wissey getWissey() {
        if (Wissey == null) {
            Wissey = new SARIC_Wissey(this);
        }
        return Wissey;
    }

    public SARIC_Teifi getTeifi() {
        if ( Teifi == null) {
            Teifi = new SARIC_Teifi(this);
        }
        return Teifi;
    }
    
}
