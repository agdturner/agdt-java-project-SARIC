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
package uk.ac.leeds.ccg.andyt.projects.saric.data.metoffice.datapoint;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.leeds.ccg.andyt.generic.io.Generic_StaticIO;
import uk.ac.leeds.ccg.andyt.generic.utilities.Generic_Time;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Environment;
import uk.ac.leeds.ccg.andyt.projects.saric.io.SARIC_Files;
import uk.ac.leeds.ccg.andyt.vector.core.Vector_Environment;
import uk.ac.leeds.ccg.andyt.vector.geometry.Vector_Envelope2D;
import uk.ac.leeds.ccg.andyt.vector.geometry.Vector_Point2D;
import uk.ac.leeds.ccg.andyt.web.WebScraper;

/**
 *
 * @author geoagdt
 */
public class SARIC_MetOfficeScraper extends WebScraper implements Runnable {

    /**
     * For convenience.
     */
    SARIC_Files files;
    SARIC_Environment se;

    Vector_Environment ve;

    // Special strings
    String symbol_ampersand;
    String symbol_backslash;
    String symbol_questionmark;
    String symbol_equals;

    // Normal strings
    String string_3hourly;
    String string_all;
    String string_capabilities;
    String string_datatype;
    String string_key;
    String string_layer;
    String string_png;
    String string_res;
    String string_sitelist;
    String string_val;
    String string_wxfcs;
    String string_wxobs;
    String string_xml;

    // Variables
    String path;

    /**
     * To be read in from a file rather than being hard coded to avoid sharing
     * the key online.
     */
    String API_KEY;

    String BASE_URL = "http://datapoint.metoffice.gov.uk/public/data/";

    String name;
    boolean Observations;
    boolean Forecasts;
    boolean TileFromWMTSService;
    boolean ForecastsTileFromWMTSService;
    boolean ObservationsTileFromWMTSService;
    boolean ObservationsSiteList;
    boolean ForecastsSiteList;
    boolean ObservationsForSite;
    boolean ForecastsForSite;
    boolean overwrite;

    /**
     * If overwrite is true then an attempt is made to get new data and
     * overwrite. Otherwise the file is left in place and the method simply
     * returns.
     */
    long timeDelay;

    protected SARIC_MetOfficeScraper() {
    }

    public SARIC_MetOfficeScraper(
            SARIC_Environment SARIC_Environment,
            boolean Observation,
            boolean Forecast,
            boolean TileFromWMTSService,
            boolean ObservationsTileFromWMTSService,
            boolean ForecastsTileFromWMTSService,
            boolean ObservationSiteList,
            boolean ForecastSiteList,
            boolean ForecastsForSite,
            boolean ObservationsForSite,
            long timeDelay,
            String name,
            boolean overwrite
    ) {
        this.se = SARIC_Environment;
        this.files = SARIC_Environment.getFiles();
        this.Observations = Observation;
        this.Forecasts = Forecast;
        this.TileFromWMTSService = TileFromWMTSService;
        this.ObservationsTileFromWMTSService = ObservationsTileFromWMTSService;
        this.ForecastsTileFromWMTSService = ForecastsTileFromWMTSService;
        this.ObservationsSiteList = ObservationSiteList;
        this.ForecastsSiteList = ForecastSiteList;
        this.ForecastsForSite = ForecastsForSite;
        this.ObservationsForSite = ObservationsForSite;
        this.timeDelay = timeDelay;
        this.name = name;
        this.overwrite = overwrite;
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

        // Read API_KEY from file
        API_KEY = getAPI_KEY();
        //System.out.println(API_KEY);

        int i = 0;
        while (true) {
            System.out.println("Iteration " + i + " of " + name);
            String layerName;
            if (Observations) {
//                layerName = "ATDNET_Sferics"; // lightening
//                layerName = "SATELLITE_Infrared_Fulldisk";
//                layerName = "SATELLITE_Visible_N_Section";
//                layerName = "SATELLITE_Visible_N_Section";
                layerName = "RADAR_UK_Composite_Highres"; //Rainfall
                getObservationLayer(layerName, overwrite);
            }

            if (Forecasts) {
                layerName = "Precipitation_Rate"; // Rainfall
//                layerName = "Total_Cloud_Cover"; // Cloud
//                layerName = "Total_Cloud_Cover_Precip_Rate_Overlaid"; // Cloud and Rain
                //temperature and pressure also available
                getForecastLayer(layerName, overwrite);
            }

//        // Download three hourly five day forecast for Dunkeswell Aerodrome
//        downloadThreeHourlyFiveDayForecastForDunkeswellAerodrome();
            // Request a tile from the WMTS service
            if (TileFromWMTSService) {
                String tileMatrixSet;
                tileMatrixSet = "EPSG:27700"; // British National Grid
                /**
                 * <TileMatrixSet>
                 * <ows:Identifier>EPSG:27700</ows:Identifier>
                 * <ows:SupportedCRS>urn:ogc:def:crs:EPSG::27700</ows:SupportedCRS>
                 * <TileMatrix>
                 * <ows:Identifier>EPSG:27700:0</ows:Identifier>
                 * <ScaleDenominator>9344354.716796875</ScaleDenominator>
                 * <TopLeftCorner>1393.0196 1230275.0454</TopLeftCorner>
                 * <TileWidth>256</TileWidth>
                 * <TileHeight>256</TileHeight>
                 * <MatrixWidth>1</MatrixWidth>
                 * <MatrixHeight>2</MatrixHeight>
                 * </TileMatrix>
                 * <TileMatrix>
                 * <ows:Identifier>EPSG:27700:1</ows:Identifier>
                 * <ScaleDenominator>4672177.3583984375</ScaleDenominator>
                 * <TopLeftCorner>1393.0196 1230275.0454</TopLeftCorner>
                 * <TileWidth>256</TileWidth>
                 * <TileHeight>256</TileHeight>
                 * <MatrixWidth>2</MatrixWidth>
                 * <MatrixHeight>4</MatrixHeight>
                 * </TileMatrix>
                 * <TileMatrix>
                 * <ows:Identifier>EPSG:27700:2</ows:Identifier>
                 * <ScaleDenominator>2336088.6791992188</ScaleDenominator>
                 * <TopLeftCorner>1393.0196 1230275.0454</TopLeftCorner>
                 * <TileWidth>256</TileWidth>
                 * <TileHeight>256</TileHeight>
                 * <MatrixWidth>4</MatrixWidth>
                 * <MatrixHeight>8</MatrixHeight>
                 * </TileMatrix>
                 * <TileMatrix>
                 * <ows:Identifier>EPSG:27700:3</ows:Identifier>
                 * <ScaleDenominator>1168044.3395996094</ScaleDenominator>
                 * <TopLeftCorner>1393.0196 1230275.0454</TopLeftCorner>
                 * <TileWidth>256</TileWidth>
                 * <TileHeight>256</TileHeight>
                 * <MatrixWidth>8</MatrixWidth>
                 * <MatrixHeight>16</MatrixHeight>
                 * </TileMatrix>
                 * <TileMatrix>
                 * <ows:Identifier>EPSG:27700:4</ows:Identifier>
                 * <ScaleDenominator>584022.1697998047</ScaleDenominator>
                 * <TopLeftCorner>1393.0196 1230275.0454</TopLeftCorner>
                 * <TileWidth>256</TileWidth>
                 * <TileHeight>256</TileHeight>
                 * <MatrixWidth>16</MatrixWidth>
                 * <MatrixHeight>32</MatrixHeight>
                 * </TileMatrix>
                 * <TileMatrix>
                 * <ows:Identifier>EPSG:27700:5</ows:Identifier>
                 * <ScaleDenominator>292011.08489990234</ScaleDenominator>
                 * <TopLeftCorner>1393.0196 1230275.0454</TopLeftCorner>
                 * <TileWidth>256</TileWidth>
                 * <TileHeight>256</TileHeight>
                 * <MatrixWidth>32</MatrixWidth>
                 * <MatrixHeight>64</MatrixHeight>
                 * </TileMatrix>
                 * <TileMatrix>
                 * <ows:Identifier>EPSG:27700:6</ows:Identifier>
                 * <ScaleDenominator>146005.54244995117</ScaleDenominator>
                 * <TopLeftCorner>1393.0196 1230275.0454</TopLeftCorner>
                 * <TileWidth>256</TileWidth>
                 * <TileHeight>256</TileHeight>
                 * <MatrixWidth>64</MatrixWidth>
                 * <MatrixHeight>128</MatrixHeight>
                 * </TileMatrix>
                 * <TileMatrix>
                 * <ows:Identifier>EPSG:27700:6</ows:Identifier>
                 */
                //tileMatrixSet = "EPSG:4326"; // WGS84
                /**
                 * For tileMatrix = EPSG:4326:0 MinY = 48.0 MaxY = 61.0 MinX =
                 * -12.0 MaxX = 5.0 DiffY = 13 DiffX = 17
                 */

                File inspireWMTSCapabilities = getInspireWMTSCapabilities();
                SARIC_MetOfficeParameters p;
                p = new SARIC_MetOfficeParameters();
                SARIC_MetOfficeCapabilitiesXMLDOMReader r;
                r = new SARIC_MetOfficeCapabilitiesXMLDOMReader(se, inspireWMTSCapabilities);
                ve = new Vector_Environment();
                String tileMatrix;
                tileMatrix = tileMatrixSet + ":0";
                BigDecimal cellsize;
                cellsize = r.getCellsize(tileMatrix);
                System.out.println("cellsize " + cellsize);
                int nrows;
                nrows = r.getNrows(tileMatrix);
                int ncols;
                ncols = r.getNcols(tileMatrix);
                Vector_Envelope2D bounds;
                bounds = r.getDimensions(cellsize, nrows, ncols, tileMatrix, p.TwoFiveSix);
                System.out.println(bounds.toString());
                p.setBounds(bounds);
                Vector_Envelope2D wisseyBounds;
                wisseyBounds = se.getWissey().getBounds();
                Vector_Envelope2D teifiBounds;
                teifiBounds = se.getTeifi().getBounds();
                if (ObservationsTileFromWMTSService) {
                    layerName = "RADAR_UK_Composite_Highres";
                    //getAllObservationsTilesFromWMTSService(layerName, tileMatrixSet, p, r, overwrite);
                    getIntersectingObservationsTilesFromWMTSService(layerName, tileMatrixSet, p, r, wisseyBounds, "Wissey", overwrite);
                    getIntersectingObservationsTilesFromWMTSService(layerName, tileMatrixSet, p, r, teifiBounds, "Teifi", overwrite);
                }
                if (ForecastsTileFromWMTSService) {
                    layerName = "Precipitation_Rate";
                    getIntersectingForecastsTilesFromWMTSService(layerName, tileMatrixSet, p, r, wisseyBounds, "Wissey", overwrite);
                    getIntersectingForecastsTilesFromWMTSService(layerName, tileMatrixSet, p, r, teifiBounds, "Teifi", overwrite);
                }
            }

            if (ObservationsSiteList) {
                getObservationsSiteList();
            }

            if (ForecastsSiteList) {
                getForecastsSiteList();
            }

            if (ForecastsForSite) {
                //<Location unitaryAuthArea="Norfolk" region="ee" name="Cromer" longitude="1.3036" latitude="52.9311" id="324251" elevation="15.0"/>
                getForecastsSite(324251);
                //<Location unitaryAuthArea="Powys" region="wl" name="Llanfair Caereinion" longitude="-3.325" latitude="52.6451" id="352338" elevation="139.0"/>
                getForecastsSite(352338);
                /**
                 * <Location unitaryAuthArea="Caerphilly" region="wl" name="Cwmcarn Forest Drive" longitude="-3.1187" latitude="51.6393" id="351139" elevation="128.0"/>
                 * <Location unitaryAuthArea="Cardiff" region="wl" name="Llandaff" longitude="-3.2132" latitude="51.4923" id="352324" elevation="19.0"/>
                 * <Location unitaryAuthArea="Cardiff" region="wl" name="Llanishen" longitude="-3.1892" latitude="51.5291" id="352349" elevation="48.0"/>
                 * <Location unitaryAuthArea="Carmarthenshire" region="wl" name="Llandovery" longitude="-3.8007" latitude="51.9964" id="324262" elevation="65.0"/>
                 * <Location unitaryAuthArea="Carmarthenshire" region="wl" name="National Botanic Garden Of Wales" longitude="-4.1403" latitude="51.8443" id="352741" elevation="54.0"/>
                 * <Location unitaryAuthArea="Ceredigion" region="wl" name="Cardigan" longitude="-4.6597" latitude="52.0834" id="350764" elevation="10.0"/>
                 * <Location unitaryAuthArea="Ceredigion" region="wl" name="Llangybi" longitude="-4.0341" latitude="52.1594" id="352347" elevation="137.0"/>
                 * <Location unitaryAuthArea="Conwy" region="wl" name="Conwy Youth Hostel" longitude="-3.8418" latitude="53.2785" id="351006" elevation="63.0"/>
                 * <Location unitaryAuthArea="Conwy" region="wl" name="Llandudno" longitude="-3.8263" latitude="53.3238" id="352333" elevation="1.0"/>
                 * <Location unitaryAuthArea="Conwy" region="wl" name="Llandudno Ski & Snowboard Centre" longitude="-3.8371" latitude="53.3286" id="352334" elevation="73.0"/>
                 * <Location unitaryAuthArea="Conwy" region="wl" name="Lledr Valley Youth Hostel" longitude="-3.8654" latitude="53.0658" id="352357" elevation="162.0" nationalPark="Snowdonia National Park"/>
                 * <Location unitaryAuthArea="Gwynedd" region="wl" name="Abersoch" longitude="-4.5042" latitude="52.8242" id="324078" elevation="9.0"/>
                 * <Location unitaryAuthArea="Gwynedd" region="wl" name="Llanbedr" longitude="-4.124" latitude="52.802" id="3407" elevation="9.0" nationalPark="Snowdonia National Park"/> 
                 * <Location unitaryAuthArea="Gwynedd" region="wl" name="Tywyn" longitude="-4.086" latitude="52.5867" id="354010" elevation="9.0"/>
                 * <Location unitaryAuthArea="Monmouthshire" region="wl" name="Monmouth Youth Hostel" longitude="-2.7221" latitude="51.8184" id="352666" elevation="33.0"/>
                 * <Location unitaryAuthArea="Monmouthshire" region="wl" name="Usk" longitude="-2.9022" latitude="51.7049" id="354037" elevation="43.0"/>
                 * <Location unitaryAuthArea="Newport" region="wl" name="Newport (Newport)" longitude="-2.9963" latitude="51.5837" id="310113" elevation="46.0"/>
                 * <Location unitaryAuthArea="Newport" region="wl" name="Tredegar House Newport" longitude="-3.0256" latitude="51.5612" id="353971" elevation="9.0"/>
                 * <Location unitaryAuthArea="Pembrokeshire" region="wl" name="Cc2000 Cross Hands" longitude="-4.8083" latitude="51.775" id="350846" elevation="91.0"/>
                 * <Location unitaryAuthArea="Pembrokeshire" region="wl" name="Dale" longitude="-5.164" latitude="51.7072" id="351148" elevation="0.0"/>
                 * <Location unitaryAuthArea="Pembrokeshire" region="wl" name="Narberth" longitude="-4.7425" latitude="51.8" id="352740" elevation="95.0"/>
                 * <Location unitaryAuthArea="Pembrokeshire" region="wl" name="Newport Bay" longitude="-4.8676" latitude="52.0342" id="352812" elevation="0.0"/>
                 * <Location unitaryAuthArea="Pembrokeshire" region="wl" name="Newport Youth Hostel" longitude="-4.8352" latitude="52.0172" id="352814" elevation="23.0" nationalPark="Pembrokeshire Coast National Park"/>
                 * <Location unitaryAuthArea="Pembrokeshire" region="wl" name="Pembroke" longitude="-4.9084" latitude="51.6731" id="310217" elevation="9.0"/>
                 * <Location unitaryAuthArea="Rhondda Cynon Taff" region="wl" name="Treherbert" longitude="-3.5359" latitude="51.6746" id="353974" elevation="183.0"/>
                 * <Location unitaryAuthArea="Vale of Glamorgan" region="wl" name="St-Athan" longitude="-3.44" latitude="51.405" id="3716" elevation="49.0"/>
                 * <Location unitaryAuthArea="Wrexham" region="wl" name="Chirk" longitude="-3.0566" latitude="52.9326" id="350909" elevation="106.0"/>
                 * 
                 */
            }

            if (ObservationsForSite) {
                getObservationsSite(324251); //<Location unitaryAuthArea="Norfolk" region="ee" name="Cromer" longitude="1.3036" latitude="52.9311" id="324251" elevation="15.0"/>
            }
            synchronized (this) {
                try {
                    this.wait(timeDelay);
                } catch (InterruptedException ex) {
                    Logger.getLogger(SARIC_MetOfficeScraper.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("Waited " + Generic_Time.getTime(timeDelay) + ".");
            }
            i++;
        }
    }

    /**
     *
     * @param layerName
     * @param overwrite If overwrite is true then an attempt is made to get new
     * data and overwrite. Otherwise the file is left in place and the method
     * simply returns.
     */
    protected void getObservationLayer(String layerName, boolean overwrite) {
        // Download capabilities document for the observation layers in XML format
        File observationLayerCapabilities;
        observationLayerCapabilities = getObservationsLayerCapabilities();
        ArrayList<String> times;
        times = getObservationsLayerTimes(layerName, observationLayerCapabilities);
        // Download observation web map
        downloadObservationImages(layerName, times, overwrite);
    }

    /**
     * Gets Forecast data for a specific site.
     *
     * @param siteID The ID of the site for which the forecast is got.
     * @return The File where the data is stored.
     */
    protected File getForecastsSite(int siteID) {
        File result;
        String siteID_s;
        siteID_s = Integer.toString(siteID);
        path = getValDataTypePath(getString_xml(), getString_wxfcs())
                + siteID_s;
        String res;
        res = getString_3hourly();
        url = BASE_URL
                + path
                + getSymbol_questionmark()
                + getString_res() + getSymbol_equals() + res
                + getSymbol_ampersand()
                + getString_key() + getSymbol_equals() + API_KEY;
        System.out.println(url);
        result = getXML(siteID_s + res);
        return result;
    }

    /**
     * Gets Forecast data for a specific site.
     *
     * @param siteID The ID of the site for which the forecast is got.
     * @return The File where the data is stored.
     */
    protected File getObservationsSite(int siteID) {
        File result;
        result = null;
//        String siteID_s;
//        siteID_s = Integer.toString(siteID);
//        path = getValDataTypePath(getString_xml(), getString_wxobs())
//                + siteID_s;
//        String res;
//        res = getString_3hourly(); //???
//        url = BASE_URL
//                + path
//                + getSymbol_questionmark()
//                + getString_res() + getSymbol_equals() + res
//                + getSymbol_ampersand()
//                + getString_key() + getSymbol_equals() + API_KEY;
//        result = getXML(siteID_s + res);
        return result;
    }

    /**
     *
     * @param layerName
     * @param overwrite If overwrite is true then an attempt is made to get new
     * data and overwrite. Otherwise the file is left in place and the method
     * simply returns.
     */
    protected void getForecastLayer(
            String layerName,
            boolean overwrite) {
        // Download capabilities document for the forecast layers in XML format
        File forecastLayerCapabilities;
        forecastLayerCapabilities = getForecastsLayerCapabilities();

//        String time;
//        time = "2017-06-15T03:00:00";
//        downloadForecastImages(layerName, time);
        ArrayList<String> times;
        times = getForecastsLayerTimes(layerName, forecastLayerCapabilities);
        /**
         * @TODO Rather than pass in the first time and get the 12 3 hourly
         * forecasts for the next 36 hours based on the first time, we can now
         * pass in the list of times parsed from the Capabilities XML.
         */
        downloadForecastImages(layerName, times.get(0), overwrite);
    }

    /**
     * Get observation site list.
     */
    protected void getObservationsSiteList() {
        getSiteList(getString_wxobs());
    }

    /**
     * Get forecast site list.
     */
    protected void getForecastsSiteList() {
        getSiteList(getString_wxfcs());
    }

    /**
     * Get observation site list.
     *
     * @param obs_or_fcs
     */
    protected void getSiteList(String obs_or_fcs) {
        path = getValDataTypePath(getString_xml(), obs_or_fcs)
                + getString_sitelist();
        url = BASE_URL
                + path
                + getSymbol_questionmark()
                + getString_key() + getSymbol_equals() + API_KEY;
        File dir;
        dir = new File(
                files.getInputDataMetOfficeDataPointDir(),
                path);
        dir.mkdirs();
        File xml;
        xml = new File(dir,
                getString_sitelist() + "." + getString_xml());
        getXML(url, xml);
    }

    /**
     *
     * @param dataType Either "xml" or "json".
     * @param obs_or_fcs Either "wxobs" or "wxfcs".
     * @return
     */
    public String getValDataTypePath(String dataType, String obs_or_fcs) {
        return getString_val() + getSymbol_backslash()
                + obs_or_fcs + getSymbol_backslash()
                + getString_all() + getSymbol_backslash()
                + dataType + getSymbol_backslash();
    }

    /**
     * Get times from observationLayerCapabilities
     *
     * @param layerName
     * @param xml
     * @return
     */
    protected ArrayList<String> getForecastsLayerTimes(
            String layerName,
            File xml) {
        ArrayList<String> result;
        SARIC_MetOfficeCapabilitiesXMLDOMReader r;
        r = new SARIC_MetOfficeCapabilitiesXMLDOMReader(se, xml);
        result = r.getForecastTimes(layerName);
        return result;
    }

    /**
     * Get times from observationLayerCapabilities
     *
     * @param layerName
     * @param xml
     * @return
     */
    protected ArrayList<String> getObservationsLayerTimes(
            String layerName,
            File xml) {
        ArrayList<String> result;
        SARIC_MetOfficeCapabilitiesXMLDOMReader r;
        r = new SARIC_MetOfficeCapabilitiesXMLDOMReader(se, xml);
        String nodeName;
        nodeName = "Time";
        result = r.getObservationTimes(layerName, nodeName);
        return result;
    }

    /**
     * Download forecast image.
     *
     * @param layerName
     * @param time
     * @param overwrite If overwrite is true then an attempt is made to get new
     * data and overwrite. Otherwise the file is left in place and the method
     * simply returns.
     */
    protected void downloadForecastImages(
            String layerName,
            String time,
            boolean overwrite) {
        //http://datapoint.metoffice.gov.uk/public/data/layer/wxfcs/{LayerName}/{ImageFormat}?RUN={DefaultTime}Z&FORECAST={Timestep}&key={key}
        String imageFormat;
        imageFormat = getString_png();
        String timeStep;
        String outputFilenameWithoutExtension;
        for (int step = 0; step <= 36; step += 3) {
            timeStep = Integer.toString(step);
            System.out.println("Getting forecast for time " + timeStep);
            path = getString_layer() + getSymbol_backslash()
                    + getString_wxfcs() + getSymbol_backslash()
                    + layerName + getSymbol_backslash()
                    + imageFormat;
            url = BASE_URL
                    + path
                    + getSymbol_questionmark()
                    + "RUN" + getSymbol_equals() + time + "Z"
                    + getSymbol_ampersand() + "FORECAST" + getSymbol_equals() + timeStep
                    + getSymbol_ampersand() + getString_key() + getSymbol_equals() + API_KEY;
            outputFilenameWithoutExtension = layerName + time.replace(':', '_') + timeStep;
            getPNG(outputFilenameWithoutExtension, overwrite);
        }
    }

    /**
     * Download observation web map.
     *
     * @param layerName
     * @param times
     * @param overwrite If overwrite is true then an attempt is made to get new
     * data and overwrite. Otherwise the file is left in place and the method
     * simply returns.
     */
    protected void downloadObservationImages(
            String layerName,
            ArrayList<String> times,
            boolean overwrite) {
        //http://datapoint.metoffice.gov.uk/public/data/layer/wxobs/{LayerName}/{ImageFormat}?TIME={Time}Z&key={key}
        String imageFormat;
        imageFormat = getString_png();
        Iterator<String> ite;
        ite = times.iterator();
        while (ite.hasNext()) {
            String time;
            time = ite.next();
            //System.out.println(time);
            //if (time.contains("00:00")) {
            path = getString_layer() + getSymbol_backslash()
                    + getString_wxobs() + getSymbol_backslash()
                    + layerName + getSymbol_backslash()
                    + imageFormat;
            url = BASE_URL
                    + path
                    + getSymbol_questionmark()
                    + "TIME" + getSymbol_equals() + time + "Z"
                    + getSymbol_ampersand() + getString_key() + getSymbol_equals() + API_KEY;
            String outputFilenameWithoutExtension;
            outputFilenameWithoutExtension = layerName + time.replace(':', '_');
            getPNG(outputFilenameWithoutExtension, overwrite);
            //}
        }
    }

    /**
     * Request an observation tile from the WMTS service
     *
     * @param layerName
     * @param tileMatrixSet
     * @param p
     * @param r
     * @param overwrite If overwrite is true then an attempt is made to get new
     * data and overwrite. Otherwise the file is left in place and the method
     * simply returns.
     */
    protected void getAllObservationsTilesFromWMTSService(
            String layerName,
            String tileMatrixSet,
            SARIC_MetOfficeParameters p,
            SARIC_MetOfficeCapabilitiesXMLDOMReader r,
            boolean overwrite) {
        ArrayList<String> times;
        times = r.getTimesInspireWMTS(layerName);
        p.setTimes(times);
        HashMap<String, SARIC_MetOfficeLayerParameters> parameters;
        parameters = p.getParameters();
        SARIC_MetOfficeLayerParameters lp;
        String tileMatrix;
        for (int matrix = 0; matrix < 7; matrix += 1) {
            //for (int matrix = 2; matrix < 7; matrix += 1) {
            tileMatrix = tileMatrixSet + ":" + matrix; // British National Grid
            //tileMatrix = "EPSG:4326:0"; // WGS84
            lp = parameters.get(tileMatrix);
            if (lp == null) {
                lp = new SARIC_MetOfficeLayerParameters(se, r.getCellsize(tileMatrix), p);
            }
            lp.setNrows(r.getNrows(tileMatrix));
            lp.setNcols(r.getNcols(tileMatrix));
            //http://datapoint.metoffice.gov.uk/public/data/inspire/view/wmts?REQUEST=gettile&LAYER=<layer required>&FORMAT=image/png&TILEMATRIXSET=<projection>&TILEMATRIX=<projection zoom level required>&TILEROW=<tile row required>&TILECOL=<tile column required>&TIME=<time required>&STYLE=<style required>&key=<API key>
            path = "inspire/view/wmts";
            String tileRow;
            String tileCol;
            String time;
            Iterator<String> ite;
            ite = times.iterator();
            while (ite.hasNext()) {
                time = ite.next();
                System.out.println(time);
                for (int row = 0; row < lp.nrows; row++) {
                    for (int col = 0; col < lp.ncols; col++) {
                        tileRow = Integer.toString(row);
                        tileCol = Integer.toString(col);
                        url = BASE_URL
                                + path
                                + "?REQUEST=gettile"
                                + "&LAYER=" + layerName
                                + "&FORMAT=image%2Fpng" //+ "&FORMAT=image/png" // The / character is URL encoded to %2B
                                + "&TILEMATRIXSET=" + tileMatrixSet
                                + "&TILEMATRIX=" + tileMatrix
                                + "&TILEROW=" + tileRow
                                + "&TILECOL=" + tileCol
                                + "&TIME=" + time
                                + "&STYLE=Bitmap%201km%20Blue-Pale%20blue%20gradient%200.01%20to%2032mm%2Fhr" // The + character has been URL encoded to %2B and the / character to %2F
                                + "&key=" + API_KEY;
                        String outputFilenameWithoutExtension;
                        outputFilenameWithoutExtension = layerName + tileMatrix.replace(':', '_') + time.replace(':', '_') + "_" + tileRow + "_" + tileCol;
                        String pathDummy = path;
                        path += "/" + layerName + "/" + tileMatrix.replace(':', '_');
                        getPNG(outputFilenameWithoutExtension, overwrite);
                        path = pathDummy;
                        //break; // For testing
                    }
                }
                //break; // For testing
                //System.exit(0);
            }
        }
    }

    /**
     * Request an observation tile from the WMTS service
     *
     * @param layerName
     * @param tileMatrixSet
     * @param p
     * @param r
     * @param AreaBoundingBox The bounding box of the area for which tiles are
     * requested.
     * @param areaName
     * @param overwrite If overwrite is true then an attempt is made to get new
     * data and overwrite. Otherwise the file is left in place and the method
     * simply returns.
     */
    protected void getIntersectingObservationsTilesFromWMTSService(
            String layerName,
            String tileMatrixSet,
            SARIC_MetOfficeParameters p,
            SARIC_MetOfficeCapabilitiesXMLDOMReader r,
            Vector_Envelope2D AreaBoundingBox,
            String areaName,
            boolean overwrite) {
        System.out.println("AreaBoundingBox " + AreaBoundingBox);
        ArrayList<String> times;
        times = r.getTimesInspireWMTS(layerName);
        p.setTimes(times);
        HashMap<String, SARIC_MetOfficeLayerParameters> parameters;
        parameters = p.getParameters();
        SARIC_MetOfficeLayerParameters lp;
        String tileMatrix;
//        BigDecimal[] tileDimensions;
        Vector_Envelope2D tileBounds;
        int matrix = 4;
        //for (matrix = 0; matrix < 7; matrix += 1) {
        for (matrix = 0; matrix < 5; matrix += 1) {
            tileMatrix = tileMatrixSet + ":" + matrix; // British National Grid
            //tileMatrix = "EPSG:4326:0"; // WGS84
            //System.out.println("tileMatrix " + tileMatrix);
            lp = parameters.get(tileMatrix);
            if (lp == null) {
                lp = new SARIC_MetOfficeLayerParameters(se, r.getCellsize(tileMatrix), p);
                System.out.println("Cellsize " + lp.getCellsize());
            }
            lp.setNrows(r.getNrows(tileMatrix));
            lp.setNcols(r.getNcols(tileMatrix));
            //http://datapoint.metoffice.gov.uk/public/data/inspire/view/wmts?REQUEST=gettile&LAYER=<layer required>&FORMAT=image/png&TILEMATRIXSET=<projection>&TILEMATRIX=<projection zoom level required>&TILEROW=<tile row required>&TILECOL=<tile column required>&TIME=<time required>&STYLE=<style required>&key=<API key>
            //http://datapoint.metoffice.gov.uk/public/data/inspire/view/wmts?REQUEST=gettile&LAYER=Precipitation_Rate&FORMAT=image/png&TILEMATRIXSET=EPSG:4258&TILEMATRIX=EPSG:4258:0&TILEROW=0&TILECOL=0&DIM_RUN=2013-11-20T03:00:00Z&DIM_FORECAST=%2B9&STYLE=Bitmap%20Blue-Pale%20blue%20gradient%200.01%20to%20greater%20than%2032mm%2Fhr&key=<API key>Note that the + character has been URL encoded to %2B and the / character to %2F
            path = "inspire/view/wmts";
            String tileRow;
            String tileCol;
            String time;
            Iterator<String> ite;
            ite = times.iterator();
            while (ite.hasNext()) {
                time = ite.next();
                System.out.println(time);
                for (int row = 0; row < lp.nrows; row++) {
                    //System.out.println("row " + row);
                    for (int col = 0; col < lp.ncols; col++) {
                        //System.out.println("col " + col);
                        tileBounds = lp.getTileBounds(row, col);
                        boolean intersects;
                        intersects = tileBounds.getIntersects(AreaBoundingBox);
                        if (intersects) {
                            System.out.println("Intersection in row " + row + ", col " + col);
                            System.out.println(tileBounds.toString());
                            tileRow = Integer.toString(row);
                            tileCol = Integer.toString(col);
                            url = BASE_URL
                                    + path
                                    + "?REQUEST=gettile"
                                    + "&LAYER=" + layerName
                                    + "&FORMAT=image%2Fpng" //+ "&FORMAT=image/png" // The / character is URL encoded to %2B
                                    + "&TILEMATRIXSET=" + tileMatrixSet
                                    + "&TILEMATRIX=" + tileMatrix
                                    + "&TILEROW=" + tileRow
                                    + "&TILECOL=" + tileCol
                                    + "&TIME=" + time
                                    + "&STYLE=Bitmap%201km%20Blue-Pale%20blue%20gradient%200.01%20to%2032mm%2Fhr" // The + character has been URL encoded to %2B and the / character to %2F
                                    + "&key=" + API_KEY;
                            //http://datapoint.metoffice.gov.uk/public/data/inspire/view/wmts?REQUEST=gettile&LAYER=Precipitation_Rate&FORMAT=image/png&TILEMATRIXSET=EPSG:4258&TILEMATRIX=EPSG:4258:0&TILEROW=0&TILECOL=0
                            String outputFilenameWithoutExtension;
                            outputFilenameWithoutExtension = layerName + tileMatrix.replace(':', '_') + time.replace(':', '_') + "_" + tileRow + "_" + tileCol;
                            String pathDummy = path;
                            path += "/" + areaName + "/" + layerName + "/" + tileMatrix.replace(':', '_') + "/" + time.replace(':', '_');
                            getPNG(outputFilenameWithoutExtension, overwrite);
                            path = pathDummy;
                        }
                        //break; // For testing
                    }
                }
                //break; // For testing
                //System.exit(0);
            }
        }
        //System.exit(0);
    }

    /**
     * Request an observation tile from the WMTS service
     *
     * @param layerName
     * @param tileMatrixSet
     * @param p
     * @param r
     * @param AreaBoundingBox The bounding box of the area for which tiles are
     * requested.
     * @param areaName
     * @param overwrite If overwrite is true then an attempt is made to get new
     * data and overwrite. Otherwise the file is left in place and the method
     * simply returns.
     */
    protected void getIntersectingForecastsTilesFromWMTSService(
            String layerName,
            String tileMatrixSet,
            SARIC_MetOfficeParameters p,
            SARIC_MetOfficeCapabilitiesXMLDOMReader r,
            Vector_Envelope2D AreaBoundingBox,
            String areaName,
            boolean overwrite) {
        System.out.println("AreaBoundingBox " + AreaBoundingBox);
        ArrayList<String> times;
        times = r.getTimesInspireWMTS(layerName);
        p.setTimes(times);
        HashMap<String, SARIC_MetOfficeLayerParameters> parameters;
        parameters = p.getParameters();
        SARIC_MetOfficeLayerParameters lp;
        String tileMatrix;
//        BigDecimal[] tileDimensions;
        Vector_Envelope2D tileBounds;
        int matrix = 4;
        //for (matrix = 0; matrix < 7; matrix += 1) {
        for (matrix = 0; matrix < 5; matrix += 1) {
            tileMatrix = tileMatrixSet + ":" + matrix; // British National Grid
            //tileMatrix = "EPSG:4326:0"; // WGS84
            //System.out.println("tileMatrix " + tileMatrix);
            lp = parameters.get(tileMatrix);
            if (lp == null) {
                lp = new SARIC_MetOfficeLayerParameters(se, r.getCellsize(tileMatrix), p);
                System.out.println("Cellsize " + lp.getCellsize());
            }
            lp.setNrows(r.getNrows(tileMatrix));
            lp.setNcols(r.getNcols(tileMatrix));
            //http://datapoint.metoffice.gov.uk/public/data/inspire/view/wmts?REQUEST=gettile&LAYER=<layer required>&FORMAT=image/png&TILEMATRIXSET=<projection>&TILEMATRIX=<projection zoom level required>&TILEROW=<tile row required>&TILECOL=<tile column required>&TIME=<time required>&STYLE=<style required>&key=<API key>
            //http://datapoint.metoffice.gov.uk/public/data/inspire/view/wmts?REQUEST=gettile&LAYER=Precipitation_Rate&FORMAT=image/png&TILEMATRIXSET=EPSG:4258&TILEMATRIX=EPSG:4258:0&TILEROW=0&TILECOL=0&DIM_RUN=2013-11-20T03:00:00Z&DIM_FORECAST=%2B9&STYLE=Bitmap%20Blue-Pale%20blue%20gradient%200.01%20to%20greater%20than%2032mm%2Fhr&key=<API key>Note that the + character has been URL encoded to %2B and the / character to %2F
            path = "inspire/view/wmts";
            String tileRow;
            String tileCol;
            String time = null;
            Iterator<String> ite;
            ite = times.iterator();
            while (ite.hasNext()) {
                time = ite.next();
            }
            System.out.println(time);
            for (int forecastTime = 0; forecastTime <= 36; forecastTime += 3) {
                for (int row = 0; row < lp.nrows; row++) {
                    //System.out.println("row " + row);
                    for (int col = 0; col < lp.ncols; col++) {
                        //System.out.println("col " + col);
                        tileBounds = lp.getTileBounds(row, col);
                        boolean intersects;
                        intersects = tileBounds.getIntersects(AreaBoundingBox);
                        if (intersects) {
                            System.out.println("Intersection in row " + row + ", col " + col);
                            System.out.println(tileBounds.toString());
                            tileRow = Integer.toString(row);
                            tileCol = Integer.toString(col);
                            //http://datapoint.metoffice.gov.uk/public/data/inspire/view/wmts?REQUEST=gettile&LAYER=Precipitation_Rate&FORMAT=image/png&TILEMATRIXSET=EPSG:4258&TILEMATRIX=EPSG:4258:0&TILEROW=0&TILECOL=0
                            // &DIM_RUN=2013-11-20T03:00:00Z&DIM_FORECAST=%2B9&STYLE=Bitmap%20Blue-Pale%20blue%20gradient%200.01%20to%20greater%20than%2032mm%2Fhr&key=<API key>Note that the + character has been URL encoded to %2B and the / character to %2F

                            url = BASE_URL
                                    + path
                                    + "?REQUEST=gettile"
                                    + "&LAYER=" + layerName
                                    + "&FORMAT=image%2Fpng" //+ "&FORMAT=image/png" // The / character is URL encoded to %2B
                                    + "&TILEMATRIXSET=" + tileMatrixSet
                                    + "&TILEMATRIX=" + tileMatrix
                                    + "&TILEROW=" + tileRow
                                    + "&TILECOL=" + tileCol
                                    + "&DIM_RUN=" + time
                                    + "&DIM_FORECAST=%2B" + forecastTime
                                    //                                + "&STYLE=Bitmap%201km%20Blue-Pale%20blue%20gradient%200.01%20to%2032mm%2Fhr" 
                                    + "&STYLE=Bitmap%20Blue-Pale%20blue%20gradient%200.01%20to%20greater%20than%2032mm%2Fhr" // The + character has been URL encoded to %2B and the / character to %2F
                                    + "&key=" + API_KEY;

                            String outputFilenameWithoutExtension;
                            outputFilenameWithoutExtension = layerName + tileMatrix.replace(':', '_') + time.replace(':', '_') + "_" + tileRow + "_" + tileCol;
                            String pathDummy = path;
                            path += "/" + areaName + "/" + layerName + "/" + tileMatrix.replace(':', '_') + "/" + time.replace(':', '_');
                            getPNG(outputFilenameWithoutExtension, overwrite);
                            path = pathDummy;
                        }
                        //break; // For testing
                    }
                }
                //  }
                //break; // For testing
                //System.exit(0);
            }
        }
        //System.exit(0);
    }

    /**
     * Download capabilities document for inspire WMTS in XML format.
     *
     * @return
     */
    protected File getInspireWMTSCapabilities() {
        //http://datapoint.metoffice.gov.uk/public/data/inspire/view/wmts?REQUEST=getcapabilities&key=<API key>
        path = "inspire/view/wmts";
        url = BASE_URL
                + path
                + "?REQUEST=get" + getString_capabilities()
                + getSymbol_ampersand()
                + getString_key() + getSymbol_equals() + API_KEY;
        File result = getXML(getString_capabilities());
        return result;
    }

    /**
     * Download capabilities document for current WMTS observation layer in XML
     * format
     *
     * @return
     */
    protected File getObservationsLayerCapabilities() {
        // http://datapoint.metoffice.gov.uk/public/data/layer/wxobs/all/xml/capabilities?key=<API key>
        File result;
        path = getString_layer() + getSymbol_backslash()
                + getString_wxobs() + getSymbol_backslash()
                + getString_all() + getSymbol_backslash()
                + getString_xml() + getSymbol_backslash();
        url = BASE_URL
                + path
                + getString_capabilities() + getSymbol_questionmark()
                + getString_key() + getSymbol_equals() + API_KEY;
        result = getXML(getString_capabilities());
        return result;
    }

    /**
     * Download capabilities document for the forecast layers in XML format
     *
     * @return
     */
    protected File getForecastsLayerCapabilities() {
        // http://datapoint.metoffice.gov.uk/public/data/layer/wxfcs/all/xml/capabilities?key=<API key>
        File result;
        path = getString_layer() + getSymbol_backslash()
                + getString_wxfcs() + getSymbol_backslash()
                + getString_all() + getSymbol_backslash()
                + getString_xml() + getSymbol_backslash();
        url = BASE_URL
                + path
                + getString_capabilities() + getSymbol_questionmark()
                + getString_key() + getSymbol_equals() + API_KEY;
        result = getXML(getString_capabilities());
        return result;
    }

    protected File getXML(String name) {
        File outputDir;
        outputDir = new File(
                files.getInputDataMetOfficeDataPointDir(),
                path);
        outputDir.mkdirs();
        File xml;
        xml = Generic_StaticIO.createNewFile(
                outputDir,
                name + "." + getString_xml());
        getXML(url,
                xml);
        return xml;
    }

    /**
     *
     * @param outputFilenameWithoutExtension
     * @param overwrite If overwrite is true then an attempt is made to get new
     * data and overwrite. Otherwise the file is left in place and the method
     * simply returns.
     */
    protected void getPNG(
            String outputFilenameWithoutExtension,
            boolean overwrite) {
        File outputDir;
        outputDir = new File(
                files.getInputDataMetOfficeDataPointDir(),
                path);
        outputDir.mkdirs();
        File png;
        png = Generic_StaticIO.createNewFile(
                outputDir,
                outputFilenameWithoutExtension + "." + getString_png());
        getPNG(url,
                png);
    }

    /**
     * Download three hourly five day forecast for Dunkeswell Aerodrome
     */
    protected void downloadThreeHourlyFiveDayForecastForDunkeswellAerodrome() {
        // http://datapoint.metoffice.gov.uk/public/data/val/wxfcs/all/xml/3840?res=3hourly&key=01234567-89ab-cdef-0123-456789abcdef
        path = getString_val() + getSymbol_backslash()
                + getString_wxfcs() + getSymbol_backslash()
                + getString_all() + getSymbol_backslash()
                + getString_xml() + getSymbol_backslash()
                + "3840";
        url = BASE_URL
                + path + getSymbol_questionmark()
                + "res" + getSymbol_equals() + "3hourly&"
                + getString_key() + getSymbol_equals() + API_KEY;
        getXML("test");
    }

    /**
     * Read Met Office DataPoint API Key from MetOfficeAPIKey.txt file.
     *
     * @return
     */
    public String getAPI_KEY() {
        File f;
        f = files.getInputDataMetOfficeDataPointAPIKeyFile();
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
                int noOfBytes;
                while ((noOfBytes = bis.read(b)) != -1) {
                    bos.write(b, 0, noOfBytes);
                }
            } catch (final IOException ioe) {
                ioe.printStackTrace(System.err);
            } finally {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace(System.err);
                }
                if (bos != null) {
                    try {
                        bos.close();
                    } catch (IOException e) {
                        e.printStackTrace(System.err);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace(System.err);
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

    // Special symbols
    public String getSymbol_ampersand() {
        if (symbol_ampersand == null) {
            symbol_ampersand = "&";
        }
        return symbol_ampersand;
    }

    public String getSymbol_backslash() {
        if (symbol_backslash == null) {
            symbol_backslash = "/";
        }
        return symbol_backslash;
    }

    public String getSymbol_questionmark() {
        if (symbol_questionmark == null) {
            symbol_questionmark = "?";
        }
        return symbol_questionmark;
    }

    public String getSymbol_equals() {
        if (symbol_equals == null) {
            symbol_equals = "=";
        }
        return symbol_equals;
    }

    public String getString_3hourly() {
        if (string_3hourly == null) {
            string_3hourly = "3hourly";
        }
        return string_3hourly;
    }

    public String getString_all() {
        if (string_all == null) {
            string_all = "all";
        }
        return string_all;
    }

    public String getString_capabilities() {
        if (string_capabilities == null) {
            string_capabilities = "capabilities";
        }
        return string_capabilities;
    }

    public String getString_datatype() {
        if (string_datatype == null) {
            string_datatype = "datatype";
        }
        return string_datatype;
    }

    public String getString_key() {
        if (string_key == null) {
            string_key = "key";
        }
        return string_key;
    }

    public String getString_layer() {
        if (string_layer == null) {
            string_layer = "layer";
        }
        return string_layer;
    }

    public String getString_png() {
        if (string_png == null) {
            string_png = "png";
        }
        return string_png;
    }

    public String getString_res() {
        if (string_res == null) {
            string_res = "res";
        }
        return string_res;
    }

    public String getString_sitelist() {
        if (string_sitelist == null) {
            string_sitelist = "sitelist";
        }
        return string_sitelist;
    }

    public String getString_val() {
        if (string_val == null) {
            string_val = "val";
        }
        return string_val;
    }

    public String getString_wxfcs() {
        if (string_wxfcs == null) {
            string_wxfcs = "wxfcs";
        }
        return string_wxfcs;
    }

    public String getString_wxobs() {
        if (string_wxobs == null) {
            string_wxobs = "wxobs";
        }
        return string_wxobs;
    }

    public String getString_xml() {
        if (string_xml == null) {
            string_xml = "xml";
        }
        return string_xml;
    }
}
