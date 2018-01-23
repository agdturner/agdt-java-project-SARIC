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

import uk.ac.leeds.ccg.andyt.generic.core.Generic_Environment;
import uk.ac.leeds.ccg.andyt.geotools.core.Geotools_Environment;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
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

    private SARIC_Strings Strings;
    private SARIC_Files Files;
    
    SARIC_MetOfficeParameters MetOfficeParameters;
    Grids_Environment Grids_Env;
    Vector_Environment Vector_Env;
    Geotools_Environment Geotools_Env;

    SARIC_Time Time;
    SARIC_Wissey Wissey;
    SARIC_Teifi Teifi;

    protected SARIC_Environment() {
    }

    public SARIC_Environment(String dataDir) {
        Strings = new SARIC_Strings();
        Files = new SARIC_Files(getStrings(),dataDir);
        MetOfficeParameters = new SARIC_MetOfficeParameters();
        Grids_Env = new Grids_Environment(getFiles().getGeneratedDataGridsDir());
        Vector_Env = new Vector_Environment(Grids_Env);
        Geotools_Env = new Geotools_Environment();
    }

    public final SARIC_Files getFiles(){
        return Files;
    }
    
    public final SARIC_Strings getStrings(){
        return Strings;
    }
    
    public void setTime(SARIC_Time time) {
        Time = time;
    }
    

    public SARIC_MetOfficeParameters getMetOfficeParameters() {
        return MetOfficeParameters;
    }

    public SARIC_Time getTime() {
        return Time;
    }

    public Grids_Environment getGrids_Env() {
        return Grids_Env;
    }

    public Vector_Environment getVector_Env() {
        return Vector_Env;
    }
    
    public Geotools_Environment getGeotools_Env(){
        return Geotools_Env;
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
