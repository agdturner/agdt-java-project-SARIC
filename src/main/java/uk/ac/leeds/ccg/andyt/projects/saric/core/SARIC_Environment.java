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
public class SARIC_Environment extends Generic_Environment {

    SARIC_MetOfficeParameters MetOfficeParameters;
    Grids_Environment _Grids_Environment;
    Vector_Environment _Vector_Environment;
    Geotools_Environment _Geotools_Environment;

    SARIC_Time Time;
    SARIC_Wissey Wissey;
    SARIC_Teifi Teifi;

    protected SARIC_Environment() {
    }

    public SARIC_Environment(String dataDir) {
        Strings = new SARIC_Strings();
        Files = new SARIC_Files(getStrings(),dataDir);
        MetOfficeParameters = new SARIC_MetOfficeParameters();
        _Grids_Environment = new Grids_Environment(getFiles().getGeneratedDataGridsDir());
        _Vector_Environment = new Vector_Environment(_Grids_Environment);
        _Geotools_Environment = new Geotools_Environment();
    }

    @Override
    public final SARIC_Files getFiles(){
        return (SARIC_Files) Files;
    }
    
    @Override
    public final SARIC_Strings getStrings(){
        return (SARIC_Strings) Strings;
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

    public Grids_Environment getGrids_Environment() {
        return _Grids_Environment;
    }

    public Vector_Environment getVector_Environment() {
        return _Vector_Environment;
    }
    
    public Geotools_Environment getGeotools_Environment(){
        return _Geotools_Environment;
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
