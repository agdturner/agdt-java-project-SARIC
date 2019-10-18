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
import java.io.IOException;
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

    public Generic_Environment env;

    public SARIC_Files files;

    public final SARIC_MetOfficeParameters metOfficeParameters;
    public final Grids_Environment gridsEnv;
    public final Vector_Environment vectorEnv;
    public final Geotools_Environment geotoolsEnv;

    Generic_Time time;
    SARIC_Wissey Wissey;
    SARIC_Teifi Teifi;

    public SARIC_Environment(File dataDir) throws IOException  {
            env = new Generic_Environment(dataDir);
            files = new SARIC_Files(dataDir);
            metOfficeParameters = new SARIC_MetOfficeParameters(this);
            gridsEnv = new Grids_Environment(env, files.getGeneratedDataGridsDir());
            vectorEnv = new Vector_Environment(gridsEnv);
            geotoolsEnv = new Geotools_Environment(env, dataDir);
    }

    public void setTime(Generic_Time time) {
        this.time = time;
    }

    public SARIC_MetOfficeParameters getMetOfficeParameters() {
        return metOfficeParameters;
    }

    public Generic_Time getTime() {
        return time;
    }

    public SARIC_Wissey getWissey() {
        if (Wissey == null) {
            Wissey = new SARIC_Wissey(this);
        }
        return Wissey;
    }

    public SARIC_Teifi getTeifi() {
        if (Teifi == null) {
            Teifi = new SARIC_Teifi(this);
        }
        return Teifi;
    }

}
