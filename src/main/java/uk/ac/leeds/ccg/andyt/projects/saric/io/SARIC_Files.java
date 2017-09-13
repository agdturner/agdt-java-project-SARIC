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
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Environment;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Object;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Strings;
import uk.ac.leeds.ccg.andyt.projects.saric.data.metoffice.datapoint.SARIC_MetOfficeScraper;

/**
 *
 * @author geoagdt
 */
public class SARIC_Files {

    // For convenience
    SARIC_Strings ss;

    protected File DataDir;
    protected File InputDataDir;
    protected File InputDataCatchmentBoundariesDir;
    protected File InputDataCEHDir;
    protected File InputDataMetOfficeDir;
    protected File InputDataMetOfficeDataPointDir;
    protected File InputDataMetOfficeDataPointInspireDir;
    protected File InputDataMetOfficeDataPointInspireViewDir;
    protected File InputDataMetOfficeDataPointInspireViewWmtsDir;
    protected File InputDataMetOfficeDataPointInspireViewWmtsCapabilitiesFile;
    protected File InputDataMetOfficeDataPointConfigDir;
    protected File InputDataMetOfficeDataPointAPIKeyFile;
    protected File InputDataMetOfficeNimrodDir;
    protected File InputDataOSMDir;
    protected File GeneratedDataDir;
    protected File GeneratedDataCatchmentDir;
    protected File GeneratedDataMetOfficeDir;
    protected File GeneratedDataMetOfficeDataPointDir;
    protected File GeneratedDataMetOfficeDataPointForecastsDir;
    protected File GeneratedDataMetOfficeDataPointForecastsSitesDir;
    protected File GeneratedDataMetOfficeDataPointForecastsSitesInTeifiFile;
    protected File GeneratedDataMetOfficeDataPointForecastsSitesInWisseyFile;
    protected File GeneratedDataMetOfficeDataPointObservationsDir;
    protected File GeneratedDataOSMDir;
    protected File OutputDataDir;
    protected File OutputDataMetOfficeDir;
    protected File OutputDataMetOfficeDataPointDir;

    protected SARIC_Files() {
    }

    public SARIC_Files(String dataDirName) {
        this.ss = new SARIC_Strings();
        setDataDirectory(dataDirName);
    }

    public SARIC_Files(SARIC_Strings ss, String dataDirName) {
        this.ss = ss;
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
                    ss.getString_output());
        }
        return OutputDataDir;
    }

    public File getOutputDataMetOfficeDir() {
        if (OutputDataMetOfficeDir == null) {
            OutputDataMetOfficeDir = new File(
                    getOutputDataDir(),
                    ss.getString_MetOffice());
        }
        return OutputDataMetOfficeDir;
    }

    public File getOutputDataMetOfficeDataPointDir() {
        if (OutputDataMetOfficeDataPointDir == null) {
            OutputDataMetOfficeDataPointDir = new File(
                    getOutputDataMetOfficeDir(),
                    ss.getString_DataPoint());
        }
        return OutputDataMetOfficeDataPointDir;
    }

    public File getGeneratedDataDir() {
        if (GeneratedDataDir == null) {
            GeneratedDataDir = new File(
                    getDataDir(),
                    ss.getString_Generated());
        }
        return GeneratedDataDir;
    }

    public File getGeneratedDataCatchmentBoundariesDir() {
        if (GeneratedDataCatchmentDir == null) {
            GeneratedDataCatchmentDir = new File(
                    getGeneratedDataDir(),
                    ss.getString_Catchment());
        }
        return GeneratedDataCatchmentDir;
    }

    public File getGeneratedDataMetOfficeDir() {
        if (GeneratedDataMetOfficeDir == null) {
            GeneratedDataMetOfficeDir = new File(
                    getGeneratedDataDir(),
                    ss.getString_MetOffice());
        }
        return GeneratedDataMetOfficeDir;
    }

    public File getGeneratedDataMetOfficeDataPointDir() {
        if (GeneratedDataMetOfficeDataPointDir == null) {
            GeneratedDataMetOfficeDataPointDir = new File(
                    getGeneratedDataDir(),
                    ss.getString_DataPoint());
        }
        return GeneratedDataMetOfficeDataPointDir;
    }

    public File getGeneratedDataMetOfficeDataPointForecastsDir() {
        if (GeneratedDataMetOfficeDataPointForecastsDir == null) {
            GeneratedDataMetOfficeDataPointForecastsDir = new File(
                    getGeneratedDataMetOfficeDataPointDir(),
                    ss.getString_Forecasts());
        }
        return GeneratedDataMetOfficeDataPointForecastsDir;
    }
    
    public File getGeneratedDataMetOfficeDataPointForecastsSitesDir() {
        if (GeneratedDataMetOfficeDataPointForecastsSitesDir == null) {
            GeneratedDataMetOfficeDataPointForecastsSitesDir = new File(
                    getGeneratedDataMetOfficeDataPointForecastsDir(),
                    ss.getString_Sites());
        }
        return GeneratedDataMetOfficeDataPointForecastsSitesDir;
    }
    
    public File getGeneratedDataMetOfficeDataPointForecastsSitesInTeifiFile() {
        if (GeneratedDataMetOfficeDataPointForecastsSitesInTeifiFile == null) {
            GeneratedDataMetOfficeDataPointForecastsSitesInTeifiFile = new File(
                    getGeneratedDataMetOfficeDataPointForecastsSitesDir(),
                    ss.getString_Teifi() + ".dat");
        }
        return GeneratedDataMetOfficeDataPointForecastsSitesInTeifiFile;
    }    

    public File getGeneratedDataMetOfficeDataPointForecastsSitesInWisseyFile() {
        if (GeneratedDataMetOfficeDataPointForecastsSitesInWisseyFile == null) {
            GeneratedDataMetOfficeDataPointForecastsSitesInWisseyFile = new File(
                    getGeneratedDataMetOfficeDataPointForecastsSitesDir(),
                    ss.getString_Wissey() + ".dat");
        }
        return GeneratedDataMetOfficeDataPointForecastsSitesInWisseyFile;
    }    

    public File getGeneratedDataMetOfficeDataPointObservationsDir() {
        if (GeneratedDataMetOfficeDataPointForecastsDir == null) {
            GeneratedDataMetOfficeDataPointForecastsDir = new File(
                    getGeneratedDataMetOfficeDataPointDir(),
                    ss.getString_Observations());
        }
        return GeneratedDataMetOfficeDataPointForecastsDir;
    }

    public File getGeneratedDataOSMDir() {
        if (GeneratedDataOSMDir == null) {
            GeneratedDataOSMDir = new File(
                    getGeneratedDataDir(),
                    ss.getString_OSM());
        }
        return GeneratedDataOSMDir;
    }
    public File getInputDataDir() {
        if (InputDataDir == null) {
            InputDataDir = new File(
                    getDataDir(),
                    ss.getString_input());
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
                    ss.getString_config());
        }
        return InputDataMetOfficeDataPointConfigDir;
    }

    public File getInputDataMetOfficeDir() {
        if (InputDataMetOfficeDir == null) {
            InputDataMetOfficeDir = new File(
                    getInputDataDir(),
                    ss.getString_MetOffice());
        }
        return InputDataMetOfficeDir;
    }
    
    public File getInputDataOSMDir() {
        if (InputDataOSMDir == null) {
            InputDataOSMDir = new File(
                    getInputDataDir(),
                    ss.getString_OSM());
        }
        return InputDataOSMDir;
    }

    public File getInputDataMetOfficeDataPointDir() {
        if (InputDataMetOfficeDataPointDir == null) {
            InputDataMetOfficeDataPointDir = new File(
                    getInputDataMetOfficeDir(),
                    ss.getString_DataPoint());
        }
        return InputDataMetOfficeDataPointDir;
    }

    public File getInputDataMetOfficeDataPointInspireDir() {
        if (InputDataMetOfficeDataPointInspireDir == null) {
            InputDataMetOfficeDataPointInspireDir = new File(
                    getInputDataMetOfficeDataPointDir(),
                    ss.getString_inspire());
        }
        return InputDataMetOfficeDataPointInspireDir;
    }

    public File getInputDataMetOfficeDataPointInspireViewDir() {
        if (InputDataMetOfficeDataPointInspireViewDir == null) {
            InputDataMetOfficeDataPointInspireViewDir = new File(
                    getInputDataMetOfficeDataPointInspireDir(),
                    ss.getString_view());
        }
        return InputDataMetOfficeDataPointInspireViewDir;
    }

    public File getInputDataMetOfficeDataPointInspireViewWmtsDir() {
        if (InputDataMetOfficeDataPointInspireViewWmtsDir == null) {
            InputDataMetOfficeDataPointInspireViewWmtsDir = new File(
                    getInputDataMetOfficeDataPointInspireViewDir(),
                    ss.getString_wmts());
        }
        return InputDataMetOfficeDataPointInspireViewWmtsDir;
    }

    public File getInputDataMetOfficeDataPointInspireViewWmtsCapabilitiesFile() {
        if (InputDataMetOfficeDataPointInspireViewWmtsCapabilitiesFile == null) {
            InputDataMetOfficeDataPointInspireViewWmtsCapabilitiesFile = new File(
                    getInputDataMetOfficeDataPointInspireViewWmtsDir(),
                    ss.getString_capabilities() + "." + ss.getString_xml());
        }
        return InputDataMetOfficeDataPointInspireViewWmtsCapabilitiesFile;
    }

    public File getInputDataMetOfficeNimrodDir() {
        if (InputDataMetOfficeNimrodDir == null) {
            InputDataMetOfficeNimrodDir = new File(
                    getInputDataMetOfficeDir(),
                    ss.getString_Nimrod());
        }
        return InputDataMetOfficeNimrodDir;
    }

    public File getInputDataCEHDir() {
        if (InputDataCEHDir == null) {
            InputDataCEHDir = new File(
                    getInputDataDir(),
                    ss.getString_CEH());
        }
        return InputDataCEHDir;
    }

    public File getInputDataCatchmentBoundariesDir() {
        if (InputDataCatchmentBoundariesDir == null) {
            InputDataCatchmentBoundariesDir = new File(
                    getInputDataDir(),
                    ss.getString_CatchmentBoundaries());
        }
        return InputDataCatchmentBoundariesDir;
    }

    /**
     *
     * @param dataType Expecting either "xml" or "json".
     * @param obs_or_fcs Expecting either "wxobs" or "wxfcs".
     * @return
     */
    public String getValDataTypePath(String dataType, String obs_or_fcs) {
        return ss.getString_val() + ss.getSymbol_backslash()
                + obs_or_fcs + ss.getSymbol_backslash()
                + ss.getString_all() + ss.getSymbol_backslash()
                + dataType + ss.getSymbol_backslash();
    }
}
