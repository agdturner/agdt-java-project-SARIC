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
package uk.ac.leeds.ccg.andyt.projects.saric.io;

import java.io.File;

/**
 *
 * @author geoagdt
 */
public class SARIC_Files {
    
    protected File DataDirectory;
    protected File InputDataDirectory;
    protected File InputDataCatchmentBoundariesDir;
    protected File InputDataCEHDir;
    protected File InputDataMetOfficeDir;
    protected File InputDataMetOfficeDataPointDir;
    protected File InputDataMetOfficeDataPointConfigDir;
    protected File InputDataMetOfficeDataPointAPIKeyFile;
    protected File InputDataMetOfficeNimrodDir;
    
    protected SARIC_Files(){}

    public SARIC_Files(String dataDirName) {
        setDataDirectory(dataDirName);
    }
    
    /**
     * Initialises a data directory with a name given by name.
     *
     * @param name
     */
    public final void setDataDirectory(String name) {
//        String userDir;
//        userDir = System.getProperty("user.dir");
//        DataDirectory = new File(
//                userDir,
//                name);
        DataDirectory = new File(name);
        if (!DataDirectory.exists()) {
            boolean successfulCreation;
            successfulCreation = DataDirectory.mkdirs();
            if (!successfulCreation) {
                throw new Error("dataDirectory not created in " + this.getClass().getName() + ".setDataDirectory(String)");
            }
        }
    }
    
    public File getDataDirectory() {
        return DataDirectory;
    }
    
    public File getInputDataDirectory() {
        if (InputDataDirectory == null) {
            InputDataDirectory = new File(
            getDataDirectory(),
            "input");
        }
        return InputDataDirectory;
    }
    
    public File getInputDataMetOfficeDataPointAPIKeyFile() {
        if (InputDataMetOfficeDataPointAPIKeyFile == null) {
            InputDataMetOfficeDataPointAPIKeyFile = new File(
                getInputDataMetOfficeDataPointConfigDir(),
                "MetOfficeDataPointAPIKey.txt");
        }
        return InputDataMetOfficeDataPointAPIKeyFile;
    }
    
    public File getInputDataMetOfficeDataPointConfigDir() {
        if (InputDataMetOfficeDataPointConfigDir == null) {
        InputDataMetOfficeDataPointConfigDir = new File(
                getInputDataMetOfficeDataPointDir(),
                "config");
        }
        return InputDataMetOfficeDataPointConfigDir;
    }
    
    public File getInputDataMetOfficeDir() {
        if (InputDataMetOfficeDir == null) {
            InputDataMetOfficeDir = new File(
                getInputDataDirectory(),
                "MetOffice");
        }
        return InputDataMetOfficeDir;
    }
    
    public File getInputDataMetOfficeDataPointDir() {
        if (InputDataMetOfficeDataPointDir == null) {
            InputDataMetOfficeDataPointDir = new File(
                getInputDataMetOfficeDir(),
                "DataPoint");
        }
        return InputDataMetOfficeDataPointDir;
    }
    
    public File getInputDataMetOfficeNimrodDir() {
        if (InputDataMetOfficeNimrodDir == null) {
            InputDataMetOfficeNimrodDir = new File(
                getInputDataMetOfficeDir(),
                "Nimrod");
        }
        return InputDataMetOfficeDir;
    }
    
    public File getInputDataCEHDir() {
        if (InputDataCEHDir == null) {
        InputDataCEHDir = new File(
                getInputDataDirectory(),
                "CEH");
        }
        return InputDataCEHDir;
    }
    
    public File getInputDataCatchmentBoundariesDir() {
        if (InputDataCatchmentBoundariesDir == null) {
        InputDataCatchmentBoundariesDir = new File(
                getInputDataDirectory(),
                "CatchmentBoundaries");
        }
        return InputDataCatchmentBoundariesDir;
    }
}
