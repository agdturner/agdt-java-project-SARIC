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
import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import org.apache.xerces.parsers.SAXParser;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Environment;
import uk.ac.leeds.ccg.andyt.projects.saric.io.SARIC_Files;
import uk.ac.leeds.ccg.andyt.projects.saric.util.SARIC_Time;

/**
 *
 * @author geoagdt
 */
public class SARIC_MetOfficeSiteXMLSAXHandler extends DefaultHandler {

    SARIC_Files sf;
    SARIC_Environment se;

    File f;
    SAXParser parser;
    HashMap<SARIC_Time, SARIC_SiteForecastRecord> forecasts;

    public SARIC_MetOfficeSiteXMLSAXHandler(
            SARIC_Environment se,
            File f) {
        this.se = se;
        this.f = f;
        sf = se.getFiles();
        parser = new SAXParser();
        forecasts = new HashMap<SARIC_Time, SARIC_SiteForecastRecord>();
    }

    public static void main(String[] args) {
        try {
            SARIC_Environment se;
            se = new SARIC_Environment("C:/Users/geoagdt/src/projects/saric/data");
            File f = new File(
                    se.getFiles().getInputDataMetOfficeDataPointDir(),
                    "/val/wxfcs/all/xml/sitelist/sitelist.xml");
            SARIC_MetOfficeSiteXMLSAXHandler r;
            r = new SARIC_MetOfficeSiteXMLSAXHandler(se, f);
            r.parser.setContentHandler(r);
            try {
                r.parser.parse(f.toString());
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
            System.out.println(r.sites);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }  // catch exeptions
    }

    public HashSet<SARIC_Site> parse() {
        parser.setContentHandler(this);
        try {
            parser.parse(f.toString());
        } catch (SAXException ex) {
            Logger.getLogger(SARIC_MetOfficeSiteXMLSAXHandler.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace(System.err);
        } catch (IOException ex) {
            Logger.getLogger(SARIC_MetOfficeSiteXMLSAXHandler.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace(System.err);
        }
        return sites;
    }
    
    // override the startElement() method
    @Override
    public void startElement(String uri, String localName,
            String rawName, Attributes attributes) {
        if (rawName.equals("Location")) {
            SARIC_Site site;
            site = new SARIC_Site();
            sites.add(site);
            site.setName(attributes.getValue("name"));
            site.setLongitude(new Double(attributes.getValue("longitude")));
            site.setLatitude(new Double(attributes.getValue("latitude")));
            site.setId(new Integer(attributes.getValue("id")));
            if (attributes.getValue("elevation") != null) {
                site.setElevation(new Double(attributes.getValue("elevation")));
            } else {
                site.setElevation(Double.NaN);
            }
            System.out.println(site.toString());
        }
    }

}
