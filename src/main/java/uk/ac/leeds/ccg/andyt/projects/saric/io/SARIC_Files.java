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
    
    protected File DataDir;
    protected File InputDataDir;
    protected File InputDataCatchmentBoundariesDir;
    protected File InputDataCEHDir;
    protected File InputDataMetOfficeDir;
    protected File InputDataMetOfficeDataPointDir;
    protected File InputDataMetOfficeDataPointConfigDir;
    protected File InputDataMetOfficeDataPointAPIKeyFile;
    protected File InputDataMetOfficeNimrodDir;
    protected File OutputDataDir;
    protected File OutputDataMetOfficeDir;
    protected File OutputDataMetOfficeDataPointDir;
    protected String sOutput = "output";
    protected String sInput = "input";
    protected String sCatchmentBoundaries = "CatchmentBoundaries";
    protected String sCEH = "CEH";
    protected String sConfig = "config";
    protected String sDataPoint = "DataPoint";
    protected String sMetOffice = "MetOffice";
    protected String sNimrod = "Nimrod";
    
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
        DataDir = new File(name);
        if (!DataDir.exists()) {
            boolean successfulCreation;
            successfulCreation = DataDir.mkdirs();
            if (!successfulCreation) {
                throw new Error("The data directory was not created in " 
                        + this.getClass().getName() + ".setDataDirectory(String)");
            }
        }
    }
    
    public File getDataDir() {
        return DataDir;
    }
    
    public File getOutputDataDir() {
        if (OutputDataDir == null) {
            OutputDataDir = new File(
            getDataDir(),
            sOutput);
        }
        return OutputDataDir;
    }

    public File getOutputDataMetOfficeDir() {
        if (OutputDataMetOfficeDir == null) {
            OutputDataMetOfficeDir = new File(
                getOutputDataDir(),
                sMetOffice);
        }
        return OutputDataMetOfficeDir;
    }

    public File getOutputDataMetOfficeDataPointDir() {
        if (OutputDataMetOfficeDataPointDir == null) {
            OutputDataMetOfficeDataPointDir = new File(
                getOutputDataDir(),
                sDataPoint);
        }
        return OutputDataMetOfficeDataPointDir;
    }
    
    public File getInputDataDir() {
        if (InputDataDir == null) {
            InputDataDir = new File(
            getDataDir(),
            sInput);
        }
        return InputDataDir;
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
                sConfig);
        }
        return InputDataMetOfficeDataPointConfigDir;
    }
    
    public File getInputDataMetOfficeDir() {
        if (InputDataMetOfficeDir == null) {
            InputDataMetOfficeDir = new File(
                getInputDataDir(),
                sMetOffice);
        }
        return InputDataMetOfficeDir;
    }
    
    public File getInputDataMetOfficeDataPointDir() {
        if (InputDataMetOfficeDataPointDir == null) {
            InputDataMetOfficeDataPointDir = new File(
                getInputDataMetOfficeDir(),
                sDataPoint);
        }
        return InputDataMetOfficeDataPointDir;
    }
    
    public File getInputDataMetOfficeNimrodDir() {
        if (InputDataMetOfficeNimrodDir == null) {
            InputDataMetOfficeNimrodDir = new File(
                getInputDataMetOfficeDir(),
                sNimrod);
        }
        return InputDataMetOfficeDir;
    }
    
    public File getInputDataCEHDir() {
        if (InputDataCEHDir == null) {
        InputDataCEHDir = new File(
                getInputDataDir(),
                sCEH);
        }
        return InputDataCEHDir;
    }
    
    public File getInputDataCatchmentBoundariesDir() {
        if (InputDataCatchmentBoundariesDir == null) {
        InputDataCatchmentBoundariesDir = new File(
                getInputDataDir(),
                sCatchmentBoundaries);
        }
        return InputDataCatchmentBoundariesDir;
    }
}
