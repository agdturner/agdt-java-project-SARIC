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
import uk.ac.leeds.ccg.andyt.generic.io.Generic_Files;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Strings;
import uk.ac.leeds.ccg.andyt.projects.saric.util.SARIC_Date;
import uk.ac.leeds.ccg.andyt.projects.saric.util.SARIC_Time;

/**
 *
 * @author geoagdt
 */
public class SARIC_Files extends Generic_Files {

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
    protected File GeneratedDataCatchmentDir;
    protected File GeneratedDataGridsDir;
    protected File GeneratedDataGridsGridDoubleFactoryDir;
    protected File GeneratedDataMetOfficeDir;
    protected File GeneratedDataMetOfficeDataPointDir;
    protected File GeneratedDataMetOfficeDataPointForecastsDir;
    protected File GeneratedDataMetOfficeDataPointForecastsSitesDir;
    protected File GeneratedDataMetOfficeDataPointForecastsSitesInTeifiFile;
    protected File GeneratedDataMetOfficeDataPointForecastsSitesInWisseyFile;
    protected File GeneratedDataMetOfficeDataPointObservationsDir;
    protected File GeneratedDataMetOfficeNimrodDir;
    protected File GeneratedDataOSMDir;
    protected File OutputDataMetOfficeDir;
    protected File OutputDataMetOfficeDataPointDir;
    protected File OutputDataMetOfficeNimrodDir;

    protected SARIC_Files() {
    }

    public SARIC_Files(String dataDirName) {
        Strings = new SARIC_Strings();
        setDataDirectory(dataDirName);
    }

    public SARIC_Files(SARIC_Strings strings, String dataDirName) {
        Strings = strings;
        setDataDirectory(dataDirName);
    }

    public SARIC_Strings getStrings() {
        return (SARIC_Strings) Strings;
    }

    /**
     * @param dir
     * @param t
     * @return a directory which is a subsubdirectory of dir. The first
     * subdirectory is the YYYY-MM. The second is YYYY-MM-DD. If the directory
     * does not exist it is created.
     */
    public File getNestedTimeDirectory(File dir, SARIC_Date t) {
        File result = new File(dir, t.getYYYYMM());
        result = new File(result, t.getYYYYMMDD());
        if (!result.exists()) {
            result.mkdirs();
        }
        return result;
    }

    public File getOutputDataMetOfficeDir() {
        if (OutputDataMetOfficeDir == null) {
            OutputDataMetOfficeDir = new File(
                    getOutputDataDir(),
                    getStrings().getString_MetOffice());
        }
        return OutputDataMetOfficeDir;
    }

    public File getOutputDataMetOfficeDataPointDir() {
        if (OutputDataMetOfficeDataPointDir == null) {
            OutputDataMetOfficeDataPointDir = new File(
                    getOutputDataMetOfficeDir(),
                    getStrings().getString_DataPoint());
        }
        return OutputDataMetOfficeDataPointDir;
    }

    public File getOutputDataMetOfficeNimrodDir() {
        if (OutputDataMetOfficeNimrodDir == null) {
            OutputDataMetOfficeNimrodDir = new File(
                    getOutputDataMetOfficeDir(),
                    getStrings().getString_Nimrod());
        }
        return OutputDataMetOfficeNimrodDir;
    }

    public File getGeneratedDataCatchmentBoundariesDir() {
        if (GeneratedDataCatchmentDir == null) {
            GeneratedDataCatchmentDir = new File(
                    getGeneratedDataDir(),
                    getStrings().getString_Catchment());
        }
        return GeneratedDataCatchmentDir;
    }

    public File getGeneratedDataGridsDir() {
        if (GeneratedDataGridsDir == null) {
            GeneratedDataGridsDir = new File(
                    getGeneratedDataDir(),
                    getStrings().getString_Grids());
        }
        return GeneratedDataGridsDir;
    }

    public File getGeneratedDataGridsGridDoubleFactoryDir() {
        if (GeneratedDataGridsGridDoubleFactoryDir == null) {
            GeneratedDataGridsGridDoubleFactoryDir = new File(
                    getGeneratedDataGridsDir(),
                    getStrings().getString_GridDoubleFactory());
        }
        return GeneratedDataGridsGridDoubleFactoryDir;
    }

    public File getGeneratedDataMetOfficeDir() {
        if (GeneratedDataMetOfficeDir == null) {
            GeneratedDataMetOfficeDir = new File(
                    getGeneratedDataDir(),
                    getStrings().getString_MetOffice());
        }
        return GeneratedDataMetOfficeDir;
    }

    public File getGeneratedDataMetOfficeNimrodDir() {
        if (GeneratedDataMetOfficeNimrodDir == null) {
            GeneratedDataMetOfficeNimrodDir = new File(
                    getGeneratedDataMetOfficeDir(),
                    getStrings().getString_Nimrod());
        }
        return GeneratedDataMetOfficeNimrodDir;
    }

    public File getGeneratedDataMetOfficeDataPointDir() {
        if (GeneratedDataMetOfficeDataPointDir == null) {
            GeneratedDataMetOfficeDataPointDir = new File(
                    getGeneratedDataMetOfficeDir(),
                    getStrings().getString_DataPoint());
        }
        return GeneratedDataMetOfficeDataPointDir;
    }

    public File getGeneratedDataMetOfficeDataPointForecastsDir() {
        if (GeneratedDataMetOfficeDataPointForecastsDir == null) {
            GeneratedDataMetOfficeDataPointForecastsDir = new File(
                    getGeneratedDataMetOfficeDataPointDir(),
                    getStrings().getString_Forecasts());
        }
        return GeneratedDataMetOfficeDataPointForecastsDir;
    }

    public File getGeneratedDataMetOfficeDataPointForecastsSitesDir() {
        if (GeneratedDataMetOfficeDataPointForecastsSitesDir == null) {
            GeneratedDataMetOfficeDataPointForecastsSitesDir = new File(
                    getGeneratedDataMetOfficeDataPointForecastsDir(),
                    getStrings().getString_Sites());
        }
        return GeneratedDataMetOfficeDataPointForecastsSitesDir;
    }

    public File getGeneratedDataMetOfficeDataPointForecastsSitesInTeifiFile() {
        if (GeneratedDataMetOfficeDataPointForecastsSitesInTeifiFile == null) {
            GeneratedDataMetOfficeDataPointForecastsSitesInTeifiFile = new File(
                    getGeneratedDataMetOfficeDataPointForecastsSitesDir(),
                    getStrings().getString_Teifi() + ".dat");
        }
        return GeneratedDataMetOfficeDataPointForecastsSitesInTeifiFile;
    }

    public File getGeneratedDataMetOfficeDataPointForecastsSitesInWisseyFile() {
        if (GeneratedDataMetOfficeDataPointForecastsSitesInWisseyFile == null) {
            GeneratedDataMetOfficeDataPointForecastsSitesInWisseyFile = new File(
                    getGeneratedDataMetOfficeDataPointForecastsSitesDir(),
                    getStrings().getString_Wissey() + ".dat");
        }
        return GeneratedDataMetOfficeDataPointForecastsSitesInWisseyFile;
    }

    public File getGeneratedDataMetOfficeDataPointObservationsDir() {
        if (GeneratedDataMetOfficeDataPointForecastsDir == null) {
            GeneratedDataMetOfficeDataPointForecastsDir = new File(
                    getGeneratedDataMetOfficeDataPointDir(),
                    getStrings().getString_Observations());
        }
        return GeneratedDataMetOfficeDataPointForecastsDir;
    }

    public File getGeneratedDataOSMDir() {
        if (GeneratedDataOSMDir == null) {
            GeneratedDataOSMDir = new File(
                    getGeneratedDataDir(),
                    getStrings().getString_OSM());
        }
        return GeneratedDataOSMDir;
    }

    public File getInputDataDir() {
        if (InputDataDir == null) {
            InputDataDir = new File(
                    getDataDir(),
                    getStrings().getString_input());
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
                    getStrings().getString_config());
        }
        return InputDataMetOfficeDataPointConfigDir;
    }

    public File getInputDataMetOfficeDir() {
        if (InputDataMetOfficeDir == null) {
            InputDataMetOfficeDir = new File(
                    getInputDataDir(),
                    getStrings().getString_MetOffice());
        }
        return InputDataMetOfficeDir;
    }

    public File getInputDataOSMDir() {
        if (InputDataOSMDir == null) {
            InputDataOSMDir = new File(
                    getInputDataDir(),
                    getStrings().getString_OSM());
        }
        return InputDataOSMDir;
    }

    public File getInputDataMetOfficeDataPointDir() {
        if (InputDataMetOfficeDataPointDir == null) {
            InputDataMetOfficeDataPointDir = new File(
                    getInputDataMetOfficeDir(),
                    getStrings().getString_DataPoint());
        }
        return InputDataMetOfficeDataPointDir;
    }

    public File getInputDataMetOfficeDataPointInspireDir() {
        if (InputDataMetOfficeDataPointInspireDir == null) {
            InputDataMetOfficeDataPointInspireDir = new File(
                    getInputDataMetOfficeDataPointDir(),
                    getStrings().getString_inspire());
        }
        return InputDataMetOfficeDataPointInspireDir;
    }

    public File getInputDataMetOfficeDataPointInspireViewDir() {
        if (InputDataMetOfficeDataPointInspireViewDir == null) {
            InputDataMetOfficeDataPointInspireViewDir = new File(
                    getInputDataMetOfficeDataPointInspireDir(),
                    getStrings().getString_view());
        }
        return InputDataMetOfficeDataPointInspireViewDir;
    }

    public File getInputDataMetOfficeDataPointInspireViewWmtsDir() {
        if (InputDataMetOfficeDataPointInspireViewWmtsDir == null) {
            InputDataMetOfficeDataPointInspireViewWmtsDir = new File(
                    getInputDataMetOfficeDataPointInspireViewDir(),
                    getStrings().getString_wmts());
        }
        return InputDataMetOfficeDataPointInspireViewWmtsDir;
    }

    public File getInputDataMetOfficeDataPointInspireViewWmtsCapabilitiesFile() {
        if (InputDataMetOfficeDataPointInspireViewWmtsCapabilitiesFile == null) {
            InputDataMetOfficeDataPointInspireViewWmtsCapabilitiesFile = new File(
                    getInputDataMetOfficeDataPointInspireViewWmtsDir(),
                    getStrings().getString_capabilities() + "." + getStrings().getString_xml());
        }
        return InputDataMetOfficeDataPointInspireViewWmtsCapabilitiesFile;
    }

    public File getInputDataMetOfficeNimrodDir() {
        if (InputDataMetOfficeNimrodDir == null) {
            InputDataMetOfficeNimrodDir = new File(
                    getInputDataMetOfficeDir(),
                    getStrings().getString_Nimrod());
        }
        return InputDataMetOfficeNimrodDir;
    }

    public File getInputDataCEHDir() {
        if (InputDataCEHDir == null) {
            InputDataCEHDir = new File(
                    getInputDataDir(),
                    getStrings().getString_CEH());
        }
        return InputDataCEHDir;
    }

    public File getInputDataCatchmentBoundariesDir() {
        if (InputDataCatchmentBoundariesDir == null) {
            InputDataCatchmentBoundariesDir = new File(
                    getInputDataDir(),
                    getStrings().getString_CatchmentBoundaries());
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
        SARIC_Strings strings;
        strings = getStrings();
        return strings.getString_val() + strings.symbol_backslash
                + obs_or_fcs + strings.symbol_backslash
                + strings.getString_all() + strings.symbol_backslash
                + dataType + strings.symbol_backslash;
    }
}
