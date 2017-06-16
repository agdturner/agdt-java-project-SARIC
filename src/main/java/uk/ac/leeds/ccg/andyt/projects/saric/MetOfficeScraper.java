/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.projects.saric;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import uk.ac.leeds.ccg.andyt.generic.io.Generic_StaticIO;
import uk.ac.leeds.ccg.andyt.generic.io.Generic_XMLDOMReader;
import uk.ac.leeds.ccg.andyt.generic.utilities.Generic_Time;
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

    // Special strings
    String s_ampersand;
    String s_backslash;
    String s_questionmark;
    String s_equals;

    // Normal strings
    String s_all;
    String s_capabilities;
    String s_key;
    String s_layer;
    String s_png;
    String s_val;
    String s_wxfcs;
    String s_wxobs;
    String s_xml;

    // Variables
    String path;

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
        // Set conmnection rate
        /**
         * For the purposes of this DataPoint Fair Use Policy, the Fair Use
         * Limits shall be defined as follows:
         *
         * You may make no more than 5000 data requests per day; and You may
         * make no more than 100 data requests per minute. Usage above this
         * limit is available by purchasing a Paid Data Plan. You may purchase a
         * Paid Data Plan by contacting: enquiries@metoffice.gov.uk. The current
         * price for the Paid Data Plan is £1,500 per annum, exclusive of Value
         * Added Tax. The Met Office reserves the right to adjust the price of
         * the Paid Data Plan.
         *
         * Should you exceed one or more of the Fair Use Limits without having a
         * Paid Data Plan in place, you agree that the Met Office shall be
         * entitled to take either of the following measures:
         *
         * Contact you to discuss how you might reduce your data usage; or
         * Invoice you for a Paid Data Plan.
         */
        int permittedConnectionsPerHour;
        permittedConnectionsPerHour = 100 * 60;
        permittedConnectionRate = permittedConnectionsPerHour / (double) Generic_Time.MilliSecondsInHour;

        // Set data Directory
        setDataDirectory("data");
        // Read API_KEY from file
        API_KEY = getAPI_KEY();
        //System.out.println(API_KEY);

//        getObserved();
//        getForecast();
        
//        // Download three hourly five day forecast for Dunkeswell Aerodrome
//        downloadThreeHourlyFiveDayForecastForDunkeswellAerodrome();
        // Request a tile from the WMTS service
        getTileFromWMTSService();
    }
    
    protected void getObserved() {
        // Download capabilities document for the observation layers in XML format
        File observationLayerCapabilities;
        observationLayerCapabilities = getObservationLayerCapabilities();

        String layerName;
        
        // Get times from observationLayerCapabilities
        //layerName = "ATDNET_Sferics"; // lightening
        //layerName = "SATELLITE_Infrared_Fulldisk";
        //layerName = "SATELLITE_Visible_N_Section";
        //layerName = "SATELLITE_Visible_N_Section";
        layerName = "RADAR_UK_Composite_Highres"; //Rainfall
        ArrayList<String> times;
        times = getObservationLayerTimes(layerName, observationLayerCapabilities);
        // Download observation web map
        downloadObservationImage(layerName, times);
    }

    protected void getForecast() {
        // Download capabilities document for the forecast layers in XML format
        File forecastLayerCapabilities;
        forecastLayerCapabilities = getForecastLayersCapabilities();
        
        String layerName;
        
        // Download forecast web map
        layerName = "Precipitation_Rate"; // Rainfall
//        layerName = "Total_Cloud_Cover"; // Cloud
//        layerName = "Total_Cloud_Cover_Precip_Rate_Overlaid"; // Cloud and Rain
        // temperature and pressure also available
        downloadForecastImages(layerName);
    }
    
    protected Parameters getParameters(
            String layerName, 
            File xml) {
    CapabilitiesXMLDOMReader r;
        r = new CapabilitiesXMLDOMReader(xml);
        
        Parameters parameters;
        parameters = getParameters(layerName, inspireWMTSCapabilities);
        
    }
    
    /**
     * Get times from observationLayerCapabilities
     *
     * @return
     */
    protected ArrayList<String> getObservationLayerTimes(
            String layerName,
            File xml) {
        ArrayList<String> result;
        CapabilitiesXMLDOMReader r;
        r = new CapabilitiesXMLDOMReader(xml);
        result = r.getTimes(layerName);
        return result;
    }

    /**
     * Download forecast image.
     */
    protected void downloadForecastImages(String layerName) {
        //http://datapoint.metoffice.gov.uk/public/data/layer/wxfcs/{LayerName}/{ImageFormat}?RUN={DefaultTime}Z&FORECAST={Timestep}&key={key}
        String imageFormat;
        imageFormat = getS_png();
        String defaultTime;
        defaultTime = "2017-06-15T03:00:00";
        String timeStep;
        String name;
        for (int step = 0; step <= 36; step += 3) {
            timeStep = Integer.toString(step);
            System.out.println("Getting forecast for time " + timeStep);
            path = getS_layer() + getS_backslash()
                    + getS_wxfcs() + getS_backslash()
                    + layerName + getS_backslash()
                    + imageFormat;
            url = BASE_URL
                    + path
                    + getS_questionmark()
                    + "RUN" + getS_equals() + defaultTime + "Z"
                    + getS_ampersand() + "FORECAST" + getS_equals() + timeStep
                    + getS_ampersand() + getS_key() + getS_equals() + API_KEY;
            name = layerName + defaultTime.replace(':', '_') + timeStep;
            getPNG(name);
        }
    }

    /**
     * Download observation web map.
     */
    protected void downloadObservationImage(
            String layerName,
            ArrayList<String> times) {
        //http://datapoint.metoffice.gov.uk/public/data/layer/wxobs/{LayerName}/{ImageFormat}?TIME={Time}Z&key={key}
        String imageFormat;
        imageFormat = getS_png();
        Iterator<String> ite;
        ite = times.iterator();
        while (ite.hasNext()) {
            String time;
            time = ite.next();
            //System.out.println(time);
            if (time.contains("00:00")) { 
            path = getS_layer() + getS_backslash()
                        + getS_wxobs() + getS_backslash()
                        + layerName + getS_backslash()
                        + imageFormat;
                url = BASE_URL
                        + path
                        + getS_questionmark()
                        + "TIME" + getS_equals() + time + "Z"
                        + getS_ampersand() + getS_key() + getS_equals() + API_KEY;
                String name;
                name = layerName + time.replace(':', '_');
                getPNG(name);
            }
        }
    }

    /**
     * Request an observation tile from the WMTS service
     */
    protected void getTileFromWMTSService() {
        File inspireWMTSCapabilities = getInspireWMTSCapabilities();
        String layerName;
        layerName = "RADAR_UK_Composite_Highres";
        Parameters parameters;
        parameters = getParameters(layerName, inspireWMTSCapabilities);
        
        //http://datapoint.metoffice.gov.uk/public/data/inspire/view/wmts?REQUEST=gettile&LAYER=<layer required>&FORMAT=image/png&TILEMATRIXSET=<projection>&TILEMATRIX=<projection zoom level required>&TILEROW=<tile row required>&TILECOL=<tile column required>&TIME=<time required>&STYLE=<style required>&key=<API key>
        path = "inspire/view/wmts";
        String layer;
        String tileMatrixSet;
        String tileMatrix;
        String tileRow;
        String tileCol;
        String time;
        layer = "RADAR_UK_Composite_Highres";
        tileMatrixSet = "EPSG:27700"; // British National Grid
        //tileMatrixSet = "EPSG:4326"; // WGS84
        tileMatrix = "EPSG:27700:0"; // British National Grid
        //tileMatrix = "EPSG:4326:0"; // WGS84
        tileRow = "0";
        tileCol = "0";
        time = "2017-06-16T03:00:00Z";
                
        url = BASE_URL
                + path
                + "?REQUEST=gettile"
                + "&LAYER=" + layer
                + "&FORMAT=image%2Fpng" //+ "&FORMAT=image/png" // The / character is URL encoded to %2B
                + "&TILEMATRIXSET=" + tileMatrixSet
                + "&TILEMATRIX=" + tileMatrix
                + "&TILEROW=" + tileRow
                + "&TILECOL=" + tileCol
                + "&TIME=" + time
                + "&STYLE=Bitmap%201km%20Blue-Pale%20blue%20gradient%200.01%20to%2032mm%2Fhr" // The + character has been URL encoded to %2B and the / character to %2F
                + "&key=" + API_KEY;
        String name;
        name = layer + tileMatrix.replace(':', '_') + time.replace(':', '_') + "_" + tileRow + "_" + tileCol;
        getPNG(name);
    }

    /**
     * Download capabilities document for inspire WMTS in XML format.
     */
    protected File getInspireWMTSCapabilities() {
        //http://datapoint.metoffice.gov.uk/public/data/inspire/view/wmts?REQUEST=getcapabilities&key=<API key>
        path = "inspire/view/wmts";
        url = BASE_URL
                + path
                + "?REQUEST=get" + getS_capabilities()
                + getS_ampersand()
                + getS_key() + getS_equals() + API_KEY;
        File result = getXML(getS_capabilities());
        return result;
    }

    /**
     * Download capabilities document for current WMTS observation layer in XML
     * format
     */
    protected File getObservationLayerCapabilities() {
        // http://datapoint.metoffice.gov.uk/public/data/layer/wxobs/all/xml/capabilities?key=<API key>
        File result;
        path = getS_layer() + getS_backslash()
                + getS_wxobs() + getS_backslash()
                + getS_all() + getS_backslash()
                + getS_xml() + getS_backslash();
        url = BASE_URL
                + path
                + getS_capabilities() + getS_questionmark()
                + getS_key() + getS_equals() + API_KEY;
        result = getXML(getS_capabilities());
        return result;
    }

    /**
     * Download capabilities document for the forecast layers in XML format
     */
    protected File getForecastLayersCapabilities() {
        // http://datapoint.metoffice.gov.uk/public/data/layer/wxfcs/all/xml/capabilities?key=<API key>
        File result;
        path = getS_layer() + getS_backslash()
                + getS_wxfcs() + getS_backslash()
                + getS_all() + getS_backslash()
                + getS_xml() + getS_backslash();
        url = BASE_URL
                + path
                + getS_capabilities() + getS_questionmark()
                + getS_key() + getS_equals() + API_KEY;
        result = getXML(getS_capabilities());
        return result;
    }

    protected File getXML(String name) {
        File outputDir;
        outputDir = new File(
                getMetOfficeDataDirectory(),
                path);
        outputDir.mkdirs();
        File xml;
        xml = Generic_StaticIO.createNewFile(
                outputDir,
                name + "." + getS_xml());
        getXML(url,
                xml);
        return xml;
    }

    protected void getPNG(String name) {
        File outputDir;
        outputDir = new File(
                getMetOfficeDataDirectory(),
                path);
        outputDir.mkdirs();
        File png;
        png = Generic_StaticIO.createNewFile(
                outputDir,
                name + "." + getS_png());
        getPNG(url,
                png);
    }

    /**
     * Download three hourly five day forecast for Dunkeswell Aerodrome
     */
    protected void downloadThreeHourlyFiveDayForecastForDunkeswellAerodrome() {
        // http://datapoint.metoffice.gov.uk/public/data/val/wxfcs/all/xml/3840?res=3hourly&key=01234567-89ab-cdef-0123-456789abcdef
        path = getS_val() + getS_backslash()
                + getS_wxfcs() + getS_backslash()
                + getS_all() + getS_backslash()
                + getS_xml() + getS_backslash()
                + "3840";
        url = BASE_URL
                + path + getS_questionmark()
                + "res" + getS_equals() + "3hourly&"
                + getS_key() + getS_equals() + API_KEY;
        getXML("test");
    }

    /**
     * return new File(dataDirectory, "MetOffice");
     *
     * @return
     */
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
    public void getPNG(
            String url,
            File f) {
        HttpURLConnection connection;
        BufferedInputStream bis;
        BufferedOutputStream bos;
        bos = Generic_StaticIO.getBufferedOutputStream(f);
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
                /**
                 * responseCode == 400 Bad Request The server cannot or will not
                 * process the request due to an apparent client error (e.g.,
                 * malformed request syntax, size too large, invalid request
                 * message framing, or deceptive request routing).
                 */
                throw new Error(message);
            }
            bis = new BufferedInputStream(connection.getInputStream());
            try {
                int bufferSize = 8192;
                byte[] b = new byte[bufferSize];
                int noOfBytes = 0;
                while ((noOfBytes = bis.read(b)) != -1) {
                    bos.write(b, 0, noOfBytes);
                }
            } catch (final IOException ioe) {
                ioe.printStackTrace();
            } finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (bos != null) {
                    try {
                        bos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } // End of the try - catch finally //
        } catch (IOException e) {
            //e.printStackTrace(System.err);
        }
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
                /**
                 * responseCode == 400 Bad Request The server cannot or will not
                 * process the request due to an apparent client error (e.g.,
                 * malformed request syntax, size too large, invalid request
                 * message framing, or deceptive request routing).
                 */
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

    /**
     * Initialises a data directory with a name given by name.
     *
     * @param name
     */
    protected void setDataDirectory(String name) {
        String userDir;
        userDir = System.getProperty("user.dir");
        dataDirectory = new File(
                userDir,
                name);
        if (!dataDirectory.exists()) {
            boolean successfulCreation;
            successfulCreation = dataDirectory.mkdirs();
            if (!successfulCreation) {
                throw new Error("dataDirectory not created in " + this.getClass().getName() + ".setDataDirectory(String)");
            }
        }
    }

    public String getS_ampersand() {
        if (s_ampersand == null) {
            s_ampersand = "&";
        }
        return s_ampersand;
    }

    public void setS_ampersand(String s_ampersand) {
        this.s_ampersand = s_ampersand;
    }

    public String getS_backslash() {
        if (s_backslash == null) {
            s_backslash = "/";
        }
        return s_backslash;
    }

    public void setS_backslash(String s_backslash) {
        this.s_backslash = s_backslash;
    }

    public String getS_questionmark() {
        if (s_questionmark == null) {
            s_questionmark = "?";
        }
        return s_questionmark;
    }

    public void setS_questionmark(String s_questionmark) {
        this.s_questionmark = s_questionmark;
    }

    public String getS_equals() {
        if (s_equals == null) {
            s_equals = "=";
        }
        return s_equals;
    }

    public void setS_equals(String s_equals) {
        this.s_equals = s_equals;
    }

    public String getS_all() {
        if (s_all == null) {
            s_all = "all";
        }
        return s_all;
    }

    public void setS_all(String s_all) {
        this.s_all = s_all;
    }

    public String getS_capabilities() {
        if (s_capabilities == null) {
            s_capabilities = "capabilities";
        }
        return s_capabilities;
    }

    public void setS_capabilities(String s_capabilities) {
        this.s_capabilities = s_capabilities;
    }

    public String getS_key() {
        if (s_key == null) {
            s_key = "key";
        }
        return s_key;
    }

    public void setS_key(String s_key) {
        this.s_key = s_key;
    }

    public String getS_png() {
        if (s_png == null) {
            s_png = "png";
        }
        return s_png;
    }

    public void setS_png(String s_png) {
        this.s_png = s_png;
    }

    public String getS_layer() {
        if (s_layer == null) {
            s_layer = "layer";
        }
        return s_layer;
    }

    public void setS_layer(String s_layer) {
        this.s_layer = s_layer;
    }

    public String getS_val() {
        if (s_val == null) {
            s_val = "val";
        }
        return s_val;
    }

    public void setS_val(String s_val) {
        this.s_val = s_val;
    }

    public String getS_wxfcs() {
        if (s_wxfcs == null) {
            s_wxfcs = "wxfcs";
        }
        return s_wxfcs;
    }

    public void setS_wxfcs(String s_wxfcs) {
        this.s_wxfcs = s_wxfcs;
    }

    public String getS_wxobs() {
        if (s_wxobs == null) {
            s_wxobs = "wxobs";
        }
        return s_wxobs;
    }

    public void setS_wxobs(String s_wxobs) {
        this.s_wxobs = s_wxobs;
    }

    public String getS_xml() {
        if (s_xml == null) {
            s_xml = "xml";
        }
        return s_xml;
    }

    public void setS_xml(String s_xml) {
        this.s_xml = s_xml;
    }

}
