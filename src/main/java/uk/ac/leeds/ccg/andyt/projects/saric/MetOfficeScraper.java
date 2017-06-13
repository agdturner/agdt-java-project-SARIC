/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.projects.saric;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import uk.ac.leeds.ccg.andyt.generic.io.Generic_StaticIO;
import uk.ac.leeds.ccg.andyt.web.WebScraper;

/**
 *
 * @author geoagdt
 */
public class MetOfficeScraper extends WebScraper {

    /**
     * Directory where all data files are to be stored.
     */
    File dataDirectory;

    /**
     * To be read in from a file rather than being hard coded to avoid sharing
     * the key online.
     */
    String API_KEY;

    String BASE_URL = "http://datapoint.metoffice.gov.uk/public/data/";

    public static void main(String[] args) {
        new MetOfficeScraper().run();
    }

    public void run() {
        // Set data Directory
        String userDir;
        userDir = System.getProperty("user.dir");
        dataDirectory = new File(
                userDir,
                "data");
        API_KEY = getAPI_KEY();
        //System.out.println(API_KEY);
        String s_questionmark;
        s_questionmark = "?";
        String s_key;
        s_key = "key";
        String s_equals;
        s_equals = "=";
        String s_backslash;
        s_backslash = "/";
        String s_layer;
        s_layer = "layer";
        String s_wxfcs;
        s_wxfcs = "wxfcs";
        String s_all;
        s_all = "all";
        String s_xml;
        s_xml = "xml";
        String s_capabilities;
        s_capabilities = "capabilities";
        // http://datapoint.metoffice.gov.uk/public/data/layer/wxfcs/all/xml/capabilities?key=<API key>
        // http://datapoint.metoffice.gov.uk/public/data/layer/wxfcs/all/xml/capabilities?key=c1804-3077-48cf-a301-f6f95e39679
        String capabilityForTheForecastLayersInXMLFormatURL;
        File outputDir;
        capabilityForTheForecastLayersInXMLFormatURL = BASE_URL
                + s_layer + s_backslash
                + s_wxfcs + s_backslash
                + s_all + s_backslash
                + s_xml + s_backslash
                + s_capabilities + s_questionmark + s_key + s_equals + API_KEY;
        outputDir = new File(
                getMetOfficeDataDirectory(),
                s_layer);
        outputDir = new File(
                outputDir,
                s_wxfcs);
        outputDir = new File(
                outputDir,
                s_all);
        outputDir = new File(
                outputDir,
                s_xml);
        outputDir.mkdirs();
        // http://datapoint.metoffice.gov.uk/public/data/val/wxfcs/all/xml/3840?res=3hourly&key=01234567-89ab-cdef-0123-456789abcdef
        String s_val;
        s_val = "val";
        String threeHourlyFiveDayForecastForDunkeswellAerodrome;
        threeHourlyFiveDayForecastForDunkeswellAerodrome = BASE_URL
                + s_val + s_backslash
                + s_wxfcs + s_backslash
                + s_all + s_backslash
                + s_xml + s_backslash
                + "3840" + s_questionmark + "res=3hourly&key" + s_equals + API_KEY;
        
        File xml;
        xml = Generic_StaticIO.createNewFile(
                outputDir,
                s_capabilities + "." + s_xml);
        getXML(threeHourlyFiveDayForecastForDunkeswellAerodrome,
                xml);
//        getXML(capabilityForTheForecastLayersInXMLFormatURL,
//                xml);
    }

    File getMetOfficeDataDirectory() {
        return new File(dataDirectory, "MetOffice");
    }

    /**
     * Read Met Office DataPoint API Key from MetOfficeAPIKey.txt file.
     *
     * @return
     */
    public String getAPI_KEY() {
        File configDir;
        configDir = new File(
                dataDirectory,
                "config");
        File f;
        f = new File(
                configDir,
                "MetOfficeAPIKey.txt");
        ArrayList<String> l;
        l = Generic_StaticIO.readIntoArrayList_String(f);
        return l.get(0);
//        return "<" + l.get(0) + ">";
    }

    /**
     *
     * @param url The url request.
     * @param f The file written to.
     */
    public void getXML(
            String url,
            File f) {
        HttpURLConnection connection;
        BufferedReader br;
        boolean append;
        append = false;
        BufferedWriter bw;
        bw = Generic_StaticIO.getBufferedWriter(f, append);
        String line;
        try {
            connection = getOpenHttpURLConnection(url);
            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                String message = url + " connection.getResponseCode() "
                        + responseCode
                        + " see http://en.wikipedia.org/wiki/List_of_HTTP_status_codes";
                if (responseCode == 301 || responseCode == 302 || responseCode == 303
                        || responseCode == 403 || responseCode == 404) {
                    message += " and http://en.wikipedia.org/wiki/HTTP_";
                    message += Integer.toString(responseCode);
                }
                throw new Error(message);
            }
            br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            try {
                while ((line = br.readLine()) != null) {
                    bw.append(line);
                    bw.newLine();
                }
            } catch (IOException e) {
                //e.printStackTrace(System.err);
            }
            br.close();
            bw.close();
            //}
        } catch (IOException e) {
            //e.printStackTrace(System.err);
        }
    }
}
