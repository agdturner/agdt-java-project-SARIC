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
import uk.ac.leeds.ccg.andyt.generic.time.Generic_Date;

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

    public SARIC_Files(SARIC_Strings s) {
        super(s);
    }

    public SARIC_Files(SARIC_Strings s, File dataDir) {
        super(s, dataDir);
    }

    @Override
    public SARIC_Strings getStrings() {
        return (SARIC_Strings) strings;
    }
    
    /**
     * @param dir
     * @param t
     * @return a directory which is a sub-subdirectory of dir. The first
     * subdirectory is the YYYY-MM. The second is YYYY-MM-DD. If the directory
     * does not exist it is created.
     */
    public File getNestedTimeDirectory(File dir, Generic_Date t) {
        File result = new File(dir, t.getYYYYMM());
        result = new File(result, t.getYYYYMMDD());
        if (!result.exists()) {
            result.mkdirs();
        }
        return result;
    }

    public File getOutputDataMetOfficeDir() {
        if (OutputDataMetOfficeDir == null) {
            OutputDataMetOfficeDir = new File(getOutputDataDir(),
                    getStrings().getS_MetOffice());
        }
        return OutputDataMetOfficeDir;
    }

    public File getOutputDataMetOfficeDataPointDir() {
        if (OutputDataMetOfficeDataPointDir == null) {
            OutputDataMetOfficeDataPointDir = new File(
                    getOutputDataMetOfficeDir(),
                    getStrings().getS_DataPoint());
        }
        return OutputDataMetOfficeDataPointDir;
    }

    public File getOutputDataMetOfficeNimrodDir() {
        if (OutputDataMetOfficeNimrodDir == null) {
            OutputDataMetOfficeNimrodDir = new File(
                    getOutputDataMetOfficeDir(),
                    getStrings().getS_Nimrod());
        }
        return OutputDataMetOfficeNimrodDir;
    }

    public File getGeneratedDataCatchmentBoundariesDir() {
        if (GeneratedDataCatchmentDir == null) {
            GeneratedDataCatchmentDir = new File(
                    getGeneratedDataDir(),
                    getStrings().getS_Catchment());
        }
        return GeneratedDataCatchmentDir;
    }

    public File getGeneratedDataGridsDir() {
        if (GeneratedDataGridsDir == null) {
            GeneratedDataGridsDir = new File(
                    getGeneratedDataDir(),
                    getStrings().getS_Grids());
        }
        return GeneratedDataGridsDir;
    }

    public File getGeneratedDataGridsGridDoubleFactoryDir() {
        if (GeneratedDataGridsGridDoubleFactoryDir == null) {
            GeneratedDataGridsGridDoubleFactoryDir = new File(
                    getGeneratedDataGridsDir(),
                    getStrings().getS_GridDoubleFactory());
        }
        return GeneratedDataGridsGridDoubleFactoryDir;
    }

    public File getGeneratedDataMetOfficeDir() {
        if (GeneratedDataMetOfficeDir == null) {
            GeneratedDataMetOfficeDir = new File(
                    getGeneratedDataDir(),
                    getStrings().getS_MetOffice());
        }
        return GeneratedDataMetOfficeDir;
    }

    public File getGeneratedDataMetOfficeNimrodDir() {
        if (GeneratedDataMetOfficeNimrodDir == null) {
            GeneratedDataMetOfficeNimrodDir = new File(
                    getGeneratedDataMetOfficeDir(),
                    getStrings().getS_Nimrod());
        }
        return GeneratedDataMetOfficeNimrodDir;
    }

    public File getGeneratedDataMetOfficeDataPointDir() {
        if (GeneratedDataMetOfficeDataPointDir == null) {
            GeneratedDataMetOfficeDataPointDir = new File(
                    getGeneratedDataMetOfficeDir(),
                    getStrings().getS_DataPoint());
        }
        return GeneratedDataMetOfficeDataPointDir;
    }

    public File getGeneratedDataMetOfficeDataPointForecastsDir() {
        if (GeneratedDataMetOfficeDataPointForecastsDir == null) {
            GeneratedDataMetOfficeDataPointForecastsDir = new File(
                    getGeneratedDataMetOfficeDataPointDir(),
                    getStrings().getS_Forecasts());
        }
        return GeneratedDataMetOfficeDataPointForecastsDir;
    }

    public File getGeneratedDataMetOfficeDataPointForecastsSitesDir() {
        if (GeneratedDataMetOfficeDataPointForecastsSitesDir == null) {
            GeneratedDataMetOfficeDataPointForecastsSitesDir = new File(
                    getGeneratedDataMetOfficeDataPointForecastsDir(),
                    getStrings().getS_Sites());
        }
        return GeneratedDataMetOfficeDataPointForecastsSitesDir;
    }

    public File getGeneratedDataMetOfficeDataPointForecastsSitesInTeifiFile() {
        if (GeneratedDataMetOfficeDataPointForecastsSitesInTeifiFile == null) {
            GeneratedDataMetOfficeDataPointForecastsSitesInTeifiFile = new File(
                    getGeneratedDataMetOfficeDataPointForecastsSitesDir(),
                    getStrings().getS_Teifi() + ".dat");
        }
        return GeneratedDataMetOfficeDataPointForecastsSitesInTeifiFile;
    }

    public File getGeneratedDataMetOfficeDataPointForecastsSitesInWisseyFile() {
        if (GeneratedDataMetOfficeDataPointForecastsSitesInWisseyFile == null) {
            GeneratedDataMetOfficeDataPointForecastsSitesInWisseyFile = new File(
                    getGeneratedDataMetOfficeDataPointForecastsSitesDir(),
                    getStrings().getS_Wissey() + ".dat");
        }
        return GeneratedDataMetOfficeDataPointForecastsSitesInWisseyFile;
    }

    public File getGeneratedDataMetOfficeDataPointObservationsDir() {
        if (GeneratedDataMetOfficeDataPointForecastsDir == null) {
            GeneratedDataMetOfficeDataPointForecastsDir = new File(
                    getGeneratedDataMetOfficeDataPointDir(),
                    getStrings().getS_Observations());
        }
        return GeneratedDataMetOfficeDataPointForecastsDir;
    }

    public File getGeneratedDataOSMDir() {
        if (GeneratedDataOSMDir == null) {
            GeneratedDataOSMDir = new File(                    getGeneratedDataDir(),
                    getStrings().getS_OSM());
        }
        return GeneratedDataOSMDir;
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
                    getStrings().getS_config());
        }
        return InputDataMetOfficeDataPointConfigDir;
    }

    public File getInputDataMetOfficeDir() {
        if (InputDataMetOfficeDir == null) {
            InputDataMetOfficeDir = new File(                    getInputDataDir(),
                    getStrings().getS_MetOffice());
        }
        return InputDataMetOfficeDir;
    }

    public File getInputDataOSMDir() {
        if (InputDataOSMDir == null) {
            InputDataOSMDir = new File(                    getInputDataDir(),                    getStrings().getS_OSM());
        }
        return InputDataOSMDir;
    }

    public File getInputDataMetOfficeDataPointDir() {
        if (InputDataMetOfficeDataPointDir == null) {
            InputDataMetOfficeDataPointDir = new File(
                    getInputDataMetOfficeDir(),
                    getStrings().getS_DataPoint());
        }
        return InputDataMetOfficeDataPointDir;
    }

    public File getInputDataMetOfficeDataPointInspireDir() {
        if (InputDataMetOfficeDataPointInspireDir == null) {
            InputDataMetOfficeDataPointInspireDir = new File(
                    getInputDataMetOfficeDataPointDir(),
                    getStrings().getS_inspire());
        }
        return InputDataMetOfficeDataPointInspireDir;
    }

    public File getInputDataMetOfficeDataPointInspireViewDir() {
        if (InputDataMetOfficeDataPointInspireViewDir == null) {
            InputDataMetOfficeDataPointInspireViewDir = new File(
                    getInputDataMetOfficeDataPointInspireDir(),
                    getStrings().getS_view());
        }
        return InputDataMetOfficeDataPointInspireViewDir;
    }

    public File getInputDataMetOfficeDataPointInspireViewWmtsDir() {
        if (InputDataMetOfficeDataPointInspireViewWmtsDir == null) {
            InputDataMetOfficeDataPointInspireViewWmtsDir = new File(
                    getInputDataMetOfficeDataPointInspireViewDir(),
                    getStrings().getS_wmts());
        }
        return InputDataMetOfficeDataPointInspireViewWmtsDir;
    }

    public File getInputDataMetOfficeDataPointInspireViewWmtsCapabilitiesFile() {
        if (InputDataMetOfficeDataPointInspireViewWmtsCapabilitiesFile == null) {
            InputDataMetOfficeDataPointInspireViewWmtsCapabilitiesFile = new File(
                    getInputDataMetOfficeDataPointInspireViewWmtsDir(),
                    getStrings().getS_capabilities() + "." + getStrings().getS_xml());
        }
        return InputDataMetOfficeDataPointInspireViewWmtsCapabilitiesFile;
    }

    public File getInputDataMetOfficeNimrodDir() {
        if (InputDataMetOfficeNimrodDir == null) {
            InputDataMetOfficeNimrodDir = new File(getInputDataMetOfficeDir(),
                    getStrings().getS_Nimrod());
        }
        return InputDataMetOfficeNimrodDir;
    }

    public File getInputDataCEHDir() {
        if (InputDataCEHDir == null) {
            InputDataCEHDir = new File(getInputDataDir(), getStrings().getS_CEH());
        }
        return InputDataCEHDir;
    }

    public File getInputDataCatchmentBoundariesDir() {
        if (InputDataCatchmentBoundariesDir == null) {
            InputDataCatchmentBoundariesDir = new File(getInputDataDir(),
                    getStrings().getS_CatchmentBoundaries());
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
        SARIC_Strings s = getStrings();
        return s.getS_val() + s.symbol_forwardslash
                + obs_or_fcs + s.symbol_forwardslash
                + s.getS_all() + s.symbol_forwardslash
                + dataType + s.symbol_forwardslash;
    }
}
