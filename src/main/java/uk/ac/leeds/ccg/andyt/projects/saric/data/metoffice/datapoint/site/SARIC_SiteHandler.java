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

    public void initForecastsSites() {
        forecastsSites = getSites(strings.getString_wxfcs());
    }

    public void initObservationsSites() {
        observationsSites = getSites(strings.getString_wxobs());
    }

    /**
     *
     * @param type either wxobs or wxfcs
     * @return
     */
    public HashSet<SARIC_Site> getSites(String type) {
        HashSet<SARIC_Site> result;
        String filename;
        filename = strings.getString_sitelist()
                + strings.getSymbol_dot() + strings.getString_xml();
        String path;
        path = strings.getString_val() + strings.getSymbol_backslash()
                + type + strings.getSymbol_backslash()
                + strings.getString_all() + strings.getSymbol_backslash()
                + strings.getString_xml() + strings.getSymbol_backslash()
                + strings.getString_sitelist() + strings.getSymbol_backslash();
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
        result = r.parse();
        return result;
    }

    public HashSet<SARIC_Site> getForecastsSites() {
        if (forecastsSites == null) {
            initForecastsSites();
        }
        return forecastsSites;
    }

    public HashSet<SARIC_Site> getObservationsSites() {
        if (observationsSites == null) {
            initObservationsSites();
        }
        return observationsSites;
    }

}
