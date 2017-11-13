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
        forecasts = new HashMap<>();
    }

    public HashMap<SARIC_Time, SARIC_SiteForecastRecord> parse() {
        parser.setContentHandler(this);
        try {
            parser.parse(f.toString());
        } catch (SAXException | IOException ex) {
            Logger.getLogger(SARIC_MetOfficeSiteXMLSAXHandler.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace(System.err);
        }
        return forecasts;
    }
    
    protected SARIC_Time t0;
    protected SARIC_Time t1;
    protected SARIC_Time t2;
    protected int rep;
    protected int daysToAdd;
    protected int minutesToAdd;
    boolean inRepElement = false;
    SARIC_SiteForecastRecord rec;
            
            
    // override the startElement() method
    @Override
    public void startElement(String uri, String localName,
            String rawName, Attributes attributes) {
        //System.out.println("rawName " + rawName);
        if (rawName.equalsIgnoreCase("DV")) {
            String time;
            time = attributes.getValue("dataDate");
            t0 = new SARIC_Time(se, time);
            //t0 = new SARIC_Time(se, time);
        }
        if (rawName.equalsIgnoreCase("Period")) {
            String time;
            time = attributes.getValue("value");
            SARIC_Time t;
            t1 = new SARIC_Time(se, time.substring(0, time.length() - 1));
            SARIC_Time t00;
            t00 = new SARIC_Time(t0);
            t00.setTime(0, 0, 0);
            daysToAdd = 0;
            while (t00.compareTo(t1) != 0) {
                t00.addDays(1);
                daysToAdd++;
            }
        }
        if (rawName.equalsIgnoreCase("Rep")) {
            inRepElement = true;
            t2 = new SARIC_Time(t1);
//            int minutes;
//            minutes = new Integer(attributes.getValue(rawName));
//            t.addMinutes(minutes);
            int weatherType;
            weatherType = new Integer(attributes.getValue("W"));
            int precipitationProbability;
            precipitationProbability = new Integer(attributes.getValue("Pp"));
            rec = new SARIC_SiteForecastRecord();
            rec.setWeatherType(weatherType);
            rec.setPrecipitationProbability(precipitationProbability);
        }
    }
    
    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        if(qName.equalsIgnoreCase("Rep")) {
            t2.addDays(daysToAdd);
            t2.addMinutes(minutesToAdd);
            forecasts.put(t2, rec);
        }
        inRepElement = false;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (inRepElement) {
            minutesToAdd = new Integer(new String(ch, start, length));
        }
    }
}
