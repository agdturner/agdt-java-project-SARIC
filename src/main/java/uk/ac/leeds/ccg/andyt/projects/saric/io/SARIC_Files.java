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
import java.io.IOException;
import uk.ac.leeds.ccg.andyt.generic.io.Generic_Files;
import uk.ac.leeds.ccg.andyt.generic.time.Generic_Date;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Strings;

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

    public SARIC_Files(File dataDir) throws IOException {
        super(dataDir);
    }

    /**
     * @param dir
     * @param t
     * @return a directory which is a sub-subdirectory of dir. The first
     * subdirectory is the YYYY-MM. The second is YYYY-MM-DD. If the directory
     * does not exist it is created.
     */
    public File getNestedTimeDirectory(File dir, Generic_Date t) {
        File r = new File(dir, t.getYYYYMM());
        r = new File(r, t.getYYYYMMDD());
        if (!r.exists()) {
            r.mkdirs();
        }
        return r;
    }

    public File getOutputDataMetOfficeDir() {
        if (OutputDataMetOfficeDir == null) {
            OutputDataMetOfficeDir = new File(getOutputDir(),
                    SARIC_Strings.s_MetOffice);
        }
        return OutputDataMetOfficeDir;
    }

    public File getOutputDataMetOfficeDataPointDir() {
        if (OutputDataMetOfficeDataPointDir == null) {
            OutputDataMetOfficeDataPointDir = new File(
                    getOutputDataMetOfficeDir(), SARIC_Strings.s_DataPoint);
        }
        return OutputDataMetOfficeDataPointDir;
    }

    public File getOutputDataMetOfficeNimrodDir() {
        if (OutputDataMetOfficeNimrodDir == null) {
            OutputDataMetOfficeNimrodDir = new File(getOutputDataMetOfficeDir(),
                    SARIC_Strings.s_Nimrod);
        }
        return OutputDataMetOfficeNimrodDir;
    }

    public File getGeneratedDataCatchmentBoundariesDir() {
        if (GeneratedDataCatchmentDir == null) {
            GeneratedDataCatchmentDir = new File(getGeneratedDir(),
                    SARIC_Strings.s_Catchment);
        }
        return GeneratedDataCatchmentDir;
    }

    public File getGeneratedDataGridsDir() {
        if (GeneratedDataGridsDir == null) {
            GeneratedDataGridsDir = new File(getGeneratedDir(),
                    SARIC_Strings.s_Grids);
        }
        return GeneratedDataGridsDir;
    }

    public File getGeneratedDataGridsGridDoubleFactoryDir() {
        if (GeneratedDataGridsGridDoubleFactoryDir == null) {
            GeneratedDataGridsGridDoubleFactoryDir = new File(
                    getGeneratedDataGridsDir(),
                    SARIC_Strings.s_GridDoubleFactory);
        }
        return GeneratedDataGridsGridDoubleFactoryDir;
    }

    public File getGeneratedDataMetOfficeDir() {
        if (GeneratedDataMetOfficeDir == null) {
            GeneratedDataMetOfficeDir = new File(getGeneratedDir(),
                    SARIC_Strings.s_MetOffice);
        }
        return GeneratedDataMetOfficeDir;
    }

    public File getGeneratedDataMetOfficeNimrodDir() {
        if (GeneratedDataMetOfficeNimrodDir == null) {
            GeneratedDataMetOfficeNimrodDir = new File(
                    getGeneratedDataMetOfficeDir(), SARIC_Strings.s_Nimrod);
        }
        return GeneratedDataMetOfficeNimrodDir;
    }

    public File getGeneratedDataMetOfficeDataPointDir() {
        if (GeneratedDataMetOfficeDataPointDir == null) {
            GeneratedDataMetOfficeDataPointDir = new File(
                    getGeneratedDataMetOfficeDir(), SARIC_Strings.s_DataPoint);
        }
        return GeneratedDataMetOfficeDataPointDir;
    }

    public File getGeneratedDataMetOfficeDataPointForecastsDir() {
        if (GeneratedDataMetOfficeDataPointForecastsDir == null) {
            GeneratedDataMetOfficeDataPointForecastsDir = new File(
                    getGeneratedDataMetOfficeDataPointDir(),
                    SARIC_Strings.s_Forecasts);
        }
        return GeneratedDataMetOfficeDataPointForecastsDir;
    }

    public File getGeneratedDataMetOfficeDataPointForecastsSitesDir() {
        if (GeneratedDataMetOfficeDataPointForecastsSitesDir == null) {
            GeneratedDataMetOfficeDataPointForecastsSitesDir = new File(
                    getGeneratedDataMetOfficeDataPointForecastsDir(),
                    SARIC_Strings.s_Sites);
        }
        return GeneratedDataMetOfficeDataPointForecastsSitesDir;
    }

    public File getGeneratedDataMetOfficeDataPointForecastsSitesInTeifiFile() {
        if (GeneratedDataMetOfficeDataPointForecastsSitesInTeifiFile == null) {
            GeneratedDataMetOfficeDataPointForecastsSitesInTeifiFile = new File(
                    getGeneratedDataMetOfficeDataPointForecastsSitesDir(),
                    SARIC_Strings.s_Teifi + ".dat");
        }
        return GeneratedDataMetOfficeDataPointForecastsSitesInTeifiFile;
    }

    public File getGeneratedDataMetOfficeDataPointForecastsSitesInWisseyFile() {
        if (GeneratedDataMetOfficeDataPointForecastsSitesInWisseyFile == null) {
            GeneratedDataMetOfficeDataPointForecastsSitesInWisseyFile = new File(
                    getGeneratedDataMetOfficeDataPointForecastsSitesDir(),
                    SARIC_Strings.s_Wissey + ".dat");
        }
        return GeneratedDataMetOfficeDataPointForecastsSitesInWisseyFile;
    }

    public File getGeneratedDataMetOfficeDataPointObservationsDir() {
        if (GeneratedDataMetOfficeDataPointForecastsDir == null) {
            GeneratedDataMetOfficeDataPointForecastsDir = new File(
                    getGeneratedDataMetOfficeDataPointDir(),
                    SARIC_Strings.s_Observations);
        }
        return GeneratedDataMetOfficeDataPointForecastsDir;
    }

    public File getGeneratedDataOSMDir() {
        if (GeneratedDataOSMDir == null) {
            GeneratedDataOSMDir = new File(getGeneratedDir(),
                    SARIC_Strings.s_OSM);
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
                    SARIC_Strings.s_config);
        }
        return InputDataMetOfficeDataPointConfigDir;
    }

    public File getInputDataMetOfficeDir() {
        if (InputDataMetOfficeDir == null) {
            InputDataMetOfficeDir = new File(getInputDir(),
                    SARIC_Strings.s_MetOffice);
        }
        return InputDataMetOfficeDir;
    }

    public File getInputDataOSMDir() {
        if (InputDataOSMDir == null) {
            InputDataOSMDir = new File(getInputDir(), SARIC_Strings.s_OSM);
        }
        return InputDataOSMDir;
    }

    public File getInputDataMetOfficeDataPointDir() {
        if (InputDataMetOfficeDataPointDir == null) {
            InputDataMetOfficeDataPointDir = new File(
                    getInputDataMetOfficeDir(), SARIC_Strings.s_DataPoint);
        }
        return InputDataMetOfficeDataPointDir;
    }

    public File getInputDataMetOfficeDataPointInspireDir() {
        if (InputDataMetOfficeDataPointInspireDir == null) {
            InputDataMetOfficeDataPointInspireDir = new File(
                    getInputDataMetOfficeDataPointDir(), 
                    SARIC_Strings.s_inspire);
        }
        return InputDataMetOfficeDataPointInspireDir;
    }

    public File getInputDataMetOfficeDataPointInspireViewDir() {
        if (InputDataMetOfficeDataPointInspireViewDir == null) {
            InputDataMetOfficeDataPointInspireViewDir = new File(
                    getInputDataMetOfficeDataPointInspireDir(),
                   SARIC_Strings.s_view);
        }
        return InputDataMetOfficeDataPointInspireViewDir;
    }

    public File getInputDataMetOfficeDataPointInspireViewWmtsDir() {
        if (InputDataMetOfficeDataPointInspireViewWmtsDir == null) {
            InputDataMetOfficeDataPointInspireViewWmtsDir = new File(
                    getInputDataMetOfficeDataPointInspireViewDir(),
                    SARIC_Strings.s_wmts);
        }
        return InputDataMetOfficeDataPointInspireViewWmtsDir;
    }

    public File getInputDataMetOfficeDataPointInspireViewWmtsCapabilitiesFile() {
        if (InputDataMetOfficeDataPointInspireViewWmtsCapabilitiesFile == null) {
            InputDataMetOfficeDataPointInspireViewWmtsCapabilitiesFile = new File(
                    getInputDataMetOfficeDataPointInspireViewWmtsDir(),
                    SARIC_Strings.s_capabilities + "." + SARIC_Strings.s_xml);
        }
        return InputDataMetOfficeDataPointInspireViewWmtsCapabilitiesFile;
    }

    public File getInputDataMetOfficeNimrodDir() {
        if (InputDataMetOfficeNimrodDir == null) {
            InputDataMetOfficeNimrodDir = new File(getInputDataMetOfficeDir(),
                    SARIC_Strings.s_Nimrod);
        }
        return InputDataMetOfficeNimrodDir;
    }

    public File getInputDataCEHDir() {
        if (InputDataCEHDir == null) {
            InputDataCEHDir = new File(getInputDir(), SARIC_Strings.s_CEH);
        }
        return InputDataCEHDir;
    }

    public File getInputDataCatchmentBoundariesDir() {
        if (InputDataCatchmentBoundariesDir == null) {
            InputDataCatchmentBoundariesDir = new File(getInputDir(),
                    SARIC_Strings.s_CatchmentBoundaries);
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
         return SARIC_Strings.s_val + SARIC_Strings.symbol_forwardslash
                + obs_or_fcs + SARIC_Strings.symbol_forwardslash
                + SARIC_Strings.s_all + SARIC_Strings.symbol_forwardslash
                + dataType + SARIC_Strings.symbol_forwardslash;
    }
}
