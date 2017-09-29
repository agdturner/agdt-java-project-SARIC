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
package uk.ac.leeds.ccg.andyt.projects.saric.data.metoffice.datapoint.site;

import java.io.File;
import java.util.HashSet;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Environment;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Object;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Strings;
import uk.ac.leeds.ccg.andyt.projects.saric.io.SARIC_Files;

/**
 *
 * @author geoagdt
 */
public class SARIC_SiteHandler extends SARIC_Object {

    // For convenience
    SARIC_Files files;
    SARIC_Strings strings;

    HashSet<SARIC_Site> forecastsSites;
    HashSet<SARIC_Site> observationsSites;

    private SARIC_SiteHandler() {
    }

    public SARIC_SiteHandler(SARIC_Environment se) {
        super(se);
        files = se.getFiles();
        strings = se.getStrings();
    }

    /**
     * 
     * @param time Expecting "3hourly" or "daily".
     * @return 
     */
    public HashSet<SARIC_Site> getForecastsSites(String time) {
        if (forecastsSites == null) {
            String type;
            type = strings.getString_wxfcs();
            String filename;
            filename = strings.getString_sitelist()
                    + strings.symbol_dot + strings.getString_xml();
            String path;
            path = strings.getString_val() + strings.symbol_backslash
                    + type + strings.symbol_backslash
                    + strings.getString_all() + strings.symbol_backslash
                    + strings.getString_xml() + strings.symbol_backslash
                    + strings.getString_sitelist() + strings.symbol_backslash
                    + time + strings.symbol_backslash ;
            File dir;
            dir = new File(
                    files.getInputDataMetOfficeDataPointDir(),
                    path);
            File f;
            f = new File(
                    dir,
                    filename);
            SARIC_MetOfficeSiteListXMLSAXHandler r;
            r = new SARIC_MetOfficeSiteListXMLSAXHandler(se, f);
            forecastsSites = r.parse();
        }
        return forecastsSites;
    }

    public HashSet<SARIC_Site> getObservationsSites() {
        if (observationsSites == null) {
            String type;
            type = strings.getString_wxobs();
            String filename;
            filename = strings.getString_sitelist()
                    + strings.symbol_dot + strings.getString_xml();
            String path;
            path = strings.getString_val() + strings.symbol_backslash
                    + type + strings.symbol_backslash
                    + strings.getString_all() + strings.symbol_backslash
                    + strings.getString_xml() + strings.symbol_backslash
                    + strings.getString_sitelist() + strings.symbol_backslash;
            File dir;
            dir = new File(
                    files.getInputDataMetOfficeDataPointDir(),
                    path);
            File f;
            f = new File(
                    dir,
                    filename);
            SARIC_MetOfficeSiteListXMLSAXHandler r;
            r = new SARIC_MetOfficeSiteListXMLSAXHandler(se, f);
            observationsSites = r.parse();
        }
        return observationsSites;
    }

}
