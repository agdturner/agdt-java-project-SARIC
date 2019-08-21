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

    HashSet<SARIC_Site> forecastsSites;
    HashSet<SARIC_Site> observationsSites;

    public SARIC_SiteHandler(SARIC_Environment se) {
        super(se);
    }

    /**
     *
     * @param time Expecting "3hourly" or "daily".
     * @return
     */
    public HashSet<SARIC_Site> getForecastsSites(String time) {
        if (forecastsSites == null) {
            String type  = SARIC_Strings.s_wxfcs;
            String fn = SARIC_Strings.s_sitelist + SARIC_Strings.symbol_dot
                    + SARIC_Strings.s_xml;
            String s = SARIC_Strings.symbol_forwardslash;
            String path;
            path = SARIC_Strings.s_val + s + type + s + SARIC_Strings.s_all + s
                    + SARIC_Strings.s_xml + s + SARIC_Strings.s_sitelist + s
                    + time + s;
            File dir;
            dir = new File(files.getInputDataMetOfficeDataPointDir(), path);
            File f;
            f = new File(dir, fn);
            SARIC_MetOfficeSiteListXMLSAXHandler r;
            r = new SARIC_MetOfficeSiteListXMLSAXHandler(se, f);
            forecastsSites = r.parse();
        }
        return forecastsSites;
    }

    public HashSet<SARIC_Site> getObservationsSites() {
        if (observationsSites == null) {
            String type;
            type = SARIC_Strings.s_wxobs;
            String filename;
            filename = SARIC_Strings.s_sitelist + SARIC_Strings.symbol_dot 
                    + SARIC_Strings.s_xml;
            String path;
            String s = SARIC_Strings.symbol_forwardslash;
            path = SARIC_Strings.s_val + s + type + s + SARIC_Strings.s_all + s
                    + SARIC_Strings.s_xml + s + SARIC_Strings.s_sitelist + s;
            File dir;
            dir = new File(files.getInputDataMetOfficeDataPointDir(), path);
            File f;
            f = new File(dir, filename);
            SARIC_MetOfficeSiteListXMLSAXHandler r;
            r = new SARIC_MetOfficeSiteListXMLSAXHandler(se, f);
            observationsSites = r.parse();
        }
        return observationsSites;
    }

}
