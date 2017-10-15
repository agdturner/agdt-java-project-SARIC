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

import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.process.Grids_Processor;
import uk.ac.leeds.ccg.andyt.projects.saric.data.catchment.SARIC_Teifi;
import uk.ac.leeds.ccg.andyt.projects.saric.data.catchment.SARIC_Wissey;
import uk.ac.leeds.ccg.andyt.projects.saric.data.metoffice.datapoint.SARIC_MetOfficeParameters;
import uk.ac.leeds.ccg.andyt.projects.saric.io.SARIC_Files;
import uk.ac.leeds.ccg.andyt.projects.saric.util.SARIC_Time;
import uk.ac.leeds.ccg.andyt.vector.core.Vector_Environment;

/**
 *
 * @author geoagdt
 */
public class SARIC_Environment {

    SARIC_Files sf;
    SARIC_Strings ss;
    SARIC_MetOfficeParameters metOfficeParameters;
    SARIC_Time time;
    Grids_Environment ge;
    Vector_Environment ve;
    SARIC_Wissey Wissey;
    SARIC_Teifi Teifi;

    protected SARIC_Environment() {
    }

    public SARIC_Environment(String dataDir) {
        ss = new SARIC_Strings();
        sf = new SARIC_Files(ss,dataDir);
        metOfficeParameters = new SARIC_MetOfficeParameters();
        ge = new Grids_Environment(sf.getGeneratedDataGridsDir());
        ve = new Vector_Environment(ge);
    }

    public void setTime(SARIC_Time st) {
        this.time = st;
    }
    
    public SARIC_Files getFiles() {
        return sf;
    }

    public SARIC_Strings getStrings() {
        return ss;
    }

    public SARIC_MetOfficeParameters getMetOfficeParameters() {
        return metOfficeParameters;
    }

    public SARIC_Time getTime() {
        return time;
    }

    public Grids_Environment getGrids_Environment() {
        return ge;
    }

    public Vector_Environment getVector_Environment() {
        return ve;
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
