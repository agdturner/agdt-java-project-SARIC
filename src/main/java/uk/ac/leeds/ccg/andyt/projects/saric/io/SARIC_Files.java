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
    protected File CatchmentBoundariesDataDir;
    protected File CEHDataDir;
    protected File MetOfficeDataDir;
    protected File MetOfficeConfigDir;
    protected File MetOfficeAPIKeyFile;
    
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
        String userDir;
        userDir = System.getProperty("user.dir");
        DataDirectory = new File(
                userDir,
                name);
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
    
    public File getMetOfficeAPIKeyFile() {
        if (MetOfficeAPIKeyFile == null) {
            MetOfficeAPIKeyFile = new File(
                getMetOfficeConfigDir(),
                "MetOfficeAPIKey.txt");
        }
        return MetOfficeAPIKeyFile;
    }
    
    public File getMetOfficeConfigDir() {
        if (MetOfficeConfigDir == null) {
        MetOfficeConfigDir = new File(
                getMetOfficeDataDir(),
                "config");
        }
        return MetOfficeConfigDir;
    }
    
    public File getMetOfficeDataDir() {
        if (MetOfficeDataDir == null) {
        MetOfficeDataDir = new File(
                DataDirectory,
                "MetOffice");
        }
        return MetOfficeDataDir;
    }
    
    public File getCEHDataDir() {
        if (CEHDataDir == null) {
        CEHDataDir = new File(
                DataDirectory,
                "CEH");
        }
        return CEHDataDir;
    }
    
    public File getCatchmentBoundariesDataDir() {
        if (CatchmentBoundariesDataDir == null) {
        CatchmentBoundariesDataDir = new File(
                DataDirectory,
                "CatchmentBoundaries");
        }
        return CatchmentBoundariesDataDir;
    }
}
