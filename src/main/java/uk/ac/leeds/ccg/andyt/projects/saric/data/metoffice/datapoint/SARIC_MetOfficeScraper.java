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
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.leeds.ccg.andyt.generic.io.Generic_StaticIO;
import uk.ac.leeds.ccg.andyt.generic.utilities.Generic_Time;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Environment;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Strings;
import uk.ac.leeds.ccg.andyt.projects.saric.data.catchment.SARIC_Teifi;
import uk.ac.leeds.ccg.andyt.projects.saric.data.catchment.SARIC_Wissey;
import uk.ac.leeds.ccg.andyt.projects.saric.data.metoffice.datapoint.site.SARIC_Site;
import uk.ac.leeds.ccg.andyt.projects.saric.data.metoffice.datapoint.site.SARIC_SiteHandler;
import uk.ac.leeds.ccg.andyt.projects.saric.io.SARIC_Files;
import uk.ac.leeds.ccg.andyt.projects.saric.util.SARIC_Date;
import uk.ac.leeds.ccg.andyt.projects.saric.util.SARIC_Time;
import uk.ac.leeds.ccg.andyt.vector.core.Vector_Environment;
import uk.ac.leeds.ccg.andyt.vector.geometry.Vector_Envelope2D;
import uk.ac.leeds.ccg.andyt.vector.geometry.Vector_Point2D;
import uk.ac.leeds.ccg.andyt.vector.projection.Vector_OSGBtoLatLon;
import uk.ac.leeds.ccg.andyt.web.WebScraper;

/**
 *
 * @author geoagdt
 */
public class SARIC_MetOfficeScraper extends WebScraper implements Runnable {

    /**
     * For convenience.
     */
    SARIC_Files sf;
    SARIC_Environment se;
    SARIC_Strings ss;

    Vector_Environment ve;

    // Variables
    String path;

    /**
     * To be read in from a file rather than being hard coded to avoid sharing
     * the key online.
     */
    String API_KEY;

    String BASE_URL = "http://datapoint.metoffice.gov.uk/public/data/";

    String name;
    boolean CalculateForecastsSitesInStudyAreas;
    boolean CalculateObservationsSitesInStudyAreas;
    boolean Observations;
    boolean Forecasts;
    boolean TileFromWMTSService;
    boolean ForecastsTileFromWMTSService;
    boolean ObservationsTileFromWMTSService;
    boolean ObservationsSiteList;
    boolean ForecastsSiteList;
    boolean ObservationsForSites;
    boolean ForecastsForSites;
    boolean overwrite;
    /**
     * @param dataType either "xml" or "json"
     */
    String dataType;

    /**
     * If overwrite is true then an attempt is made to get new data and
     * overwrite. Otherwise the file is left in place and the method simply
     * returns.
     */
    long timeDelay;

    protected SARIC_MetOfficeScraper() {
    }

    public SARIC_MetOfficeScraper(
            SARIC_Environment se,
            boolean CalculateForecastsSitesInStudyAreas,
            boolean CalculateObservationsSitesInStudyAreas,
            boolean Observation,
            boolean Forecast,
            boolean TileFromWMTSService,
            boolean ObservationsTileFromWMTSService,
            boolean ForecastsTileFromWMTSService,
            boolean ObservationSiteList,
            boolean ForecastSiteList,
            boolean ForecastsForSites,
            boolean ObservationsForSites,
            long timeDelay,
            String name,
            boolean overwrite,
            String dataType
    ) {
        this.se = se;
        this.sf = se.getFiles();
        this.ss = se.getStrings();
        this.CalculateForecastsSitesInStudyAreas = CalculateForecastsSitesInStudyAreas;
        this.CalculateObservationsSitesInStudyAreas = CalculateObservationsSitesInStudyAreas;
        this.Observations = Observation;
        this.Forecasts = Forecast;
        this.TileFromWMTSService = TileFromWMTSService;
        this.ObservationsTileFromWMTSService = ObservationsTileFromWMTSService;
        this.ForecastsTileFromWMTSService = ForecastsTileFromWMTSService;
        this.ObservationsSiteList = ObservationSiteList;
        this.ForecastsSiteList = ForecastSiteList;
        this.ForecastsForSites = ForecastsForSites;
        this.ObservationsForSites = ObservationsForSites;
        this.timeDelay = timeDelay;
        this.name = name;
        this.overwrite = overwrite;
        this.dataType = dataType;
    }

    public void run() {
        File dir;
        SARIC_SiteHandler sh;
        HashSet<SARIC_Site> sites;
        sh = new SARIC_SiteHandler(se);
        if (CalculateForecastsSitesInStudyAreas) {
            dir = sf.getGeneratedDataMetOfficeDataPointForecastsDir();
            String time;
            //time = ss.getString_daily();
            time = ss.getString_3hourly();
            sites = sh.getForecastsSites(time);
            calculateSitesInStudyAreas(sites, dir, null);
        }

        if (CalculateObservationsSitesInStudyAreas) {
            dir = sf.getGeneratedDataMetOfficeDataPointObservationsDir();
            BigDecimal buffer;
//            buffer = new BigDecimal(20000.0d);
//            buffer = new BigDecimal(30000.0d);
//            buffer = new BigDecimal(40000.0d);
            buffer = new BigDecimal(60000.0d);
            sites = sh.getObservationsSites();
            calculateSitesInStudyAreas(sites, dir, buffer);
        }

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

        String lastSiteObservationsTime0 = null;

        int i = 0;
        while (true) {
            System.out.println("Iteration " + i + " of " + name);
            String layerName;
            if (Observations) {
//                layerName = "ATDNET_Sferics"; // lightening
//                layerName = "SATELLITE_Infrared_Fulldisk";
//                layerName = "SATELLITE_Visible_N_Section";
//                layerName = "SATELLITE_Visible_N_Section";
                layerName = ss.getString_RADAR_UK_Composite_Highres(); //Rainfall
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
                    layerName = ss.getString_RADAR_UK_Composite_Highres();
                    //getAllObservationsTilesFromWMTSService(layerName, tileMatrixSet, p, r, overwrite);
                    getIntersectingObservationsTilesFromWMTSService(
                            layerName, tileMatrixSet, p, r, wisseyBounds, ss.getString_Wissey(), overwrite);
                    getIntersectingObservationsTilesFromWMTSService(
                            layerName, tileMatrixSet, p, r, teifiBounds, ss.getString_Teifi(), overwrite);
                }
                if (ForecastsTileFromWMTSService) {
                    layerName = "Precipitation_Rate";
                    getIntersectingForecastsTilesFromWMTSService(
                            layerName, tileMatrixSet, p, r, wisseyBounds, ss.getString_Wissey(), overwrite);
                    getIntersectingForecastsTilesFromWMTSService(
                            layerName, tileMatrixSet, p, r, teifiBounds, ss.getString_Teifi(), overwrite);
                }
            }

            if (ObservationsSiteList) {
                getObservationsSiteList();
            }

            if (ForecastsSiteList) {
                boolean ForecastsForSitesDaily;
                boolean ForecastsForSites3Hourly;
                ForecastsForSitesDaily = true;
                ForecastsForSites3Hourly = true;
                String time;
                if (ForecastsForSitesDaily) {
                    time = ss.getString_daily();
                    getForecastsSiteCapabilities(time);
                    getForecastsSiteList(time);
                }
                if (ForecastsForSites3Hourly) {
                    time = ss.getString_3hourly();
                    getForecastsSiteCapabilities(time);
                    getForecastsSiteList(time);
                }
            }

            if (ForecastsForSites) {
                // Switches
                boolean ForecastsForSites3Hourly;
                boolean ForecastsForSitesDaily;
                ForecastsForSites3Hourly = true;
//                ForecastsForSites3Hourly = false;
//                ForecastsForSitesDaily = true;
                ForecastsForSitesDaily = false;
                String time;
                File forecastsSiteCapabilities;
                String[] timeRange;
                if (ForecastsForSites3Hourly) {
                    time = ss.getString_3hourly();
                    forecastsSiteCapabilities = getForecastsSiteCapabilities(time);
                    timeRange = getTimeRange(forecastsSiteCapabilities);
                    getForecastsForSites(ss.getString_Wissey(), null, time, timeRange[0]);
                    getForecastsForSites(ss.getString_Teifi(), null, time, timeRange[0]);
                }
                if (ForecastsForSitesDaily) {
                    time = ss.getString_daily();
                    forecastsSiteCapabilities = getForecastsSiteCapabilities(time);
                    timeRange = getTimeRange(forecastsSiteCapabilities);
                    getForecastsForSites(ss.getString_Wissey(), null, time, timeRange[0]);
                    getForecastsForSites(ss.getString_Teifi(), null, time, timeRange[0]);
                }
            }

            if (ObservationsForSites) {
                /**
                 * The UK observations data feeds provide access to the hourly
                 * weather observations made over the 24 hour period preceding
                 * the time at which the web service was last updated.
                 * Observation data is only collected at some of the sites for
                 * which forecasts are provided.
                 */
                File observationsSiteCapabilities;
                observationsSiteCapabilities = getObservationsSiteCapabilities();
                String[] timeRange;
                timeRange = getTimeRange(observationsSiteCapabilities);
                BigDecimal buffer;
//            buffer = new BigDecimal(20000.0d);
//            buffer = new BigDecimal(30000.0d);
//            buffer = new BigDecimal(40000.0d);
                buffer = new BigDecimal(60000.0d);
                if (lastSiteObservationsTime0 == null) {
                    lastSiteObservationsTime0 = timeRange[2];
                    getObservationsForSites(ss.getString_Wissey(), buffer, timeRange[0]);
                    getObservationsForSites(ss.getString_Teifi(), buffer, timeRange[0]);
                } else {
                    if (lastSiteObservationsTime0.equalsIgnoreCase(timeRange[2])) {
                        // Do nothing as we already have all the data.
                    } else {
                        getObservationsForSites(ss.getString_Wissey(), buffer, timeRange[0]);
                        getObservationsForSites(ss.getString_Teifi(), buffer, timeRange[0]);
                    }
                }
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
        TreeSet<SARIC_Time> times;
        times = getObservationsLayerTimes(layerName, observationLayerCapabilities);
        // Download observation web map
        downloadObservationImages(layerName, times, overwrite);
    }

    /**
     * Gets Forecast data for a specific site.
     *
     * @param siteID The ID of the site for which the forecast is requested.
     * @return The File where the data is stored.
     */
    protected File getForecastsSite(int siteID, String res, String timeRange) {
        return getForecastsSite(Integer.toString(siteID), res, timeRange);
    }

    /**
     * Gets Forecast data for a specific site or for all sites.
     *
     * @param siteID The ID of the site for which the forecast is requested. If
     * siteID.equalsIgnoreCase("all") then forecasts for all sites are returned.
     * @param res
     * @param timeRange
     * @return The File where the data is stored.
     */
    protected File getForecastsSite(String siteID, String res, String timeRange) {
        File result;
        path = sf.getValDataTypePath(dataType, ss.getString_wxfcs())
                + siteID;
        url = BASE_URL
                + path
                + ss.symbol_questionmark
                + ss.getString_res() + ss.symbol_equals + res
                + ss.symbol_ampersand
                + ss.getString_key() + ss.symbol_equals + API_KEY;
        //System.out.println(url);
        // Reset path
        String currentTime;
        currentTime = Generic_Time.getDateAndTimeHourDir();
        path = sf.getValDataTypePath(dataType, ss.getString_wxfcs())
                + ss.getString_site() + ss.symbol_backslash
                + res + ss.symbol_backslash
                + currentTime + ss.symbol_backslash
                + siteID;
        result = getXML(siteID + res, -1, timeRange);
        return result;
    }

    /**
     * Gets Observation data for a specific site.
     *
     * @param siteID The ID of the site for which the observation is requested.
     * @return The File where the data is stored.
     */
    protected File getObservationsSite(int siteID, String timeRange) {
        return getObservationsSite(Integer.toString(siteID), timeRange);
    }

    /**
     * Gets Observation data for a specific site.
     *
     * @param siteID The ID of the site for which the observation is requested.
     * If siteID.equalsIgnoreCase("all") then forecasts for all sites are
     * returned.
     * @param timeRange
     * @return The File where the data is stored.
     */
    protected File getObservationsSite(String siteID, String timeRange) {
        File result;
        path = sf.getValDataTypePath(dataType, ss.getString_wxobs())
                + siteID;
        String res;
        res = ss.getString_hourly();
        url = BASE_URL
                + path
                + ss.symbol_questionmark
                + ss.getString_res() + ss.symbol_equals + res
                + ss.symbol_ampersand
                + ss.getString_key() + ss.symbol_equals + API_KEY;
        result = getXML(siteID + res, 1, timeRange);
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
        getSiteList(ss.getString_wxobs(), null);
    }

    /**
     * Get forecast site list.
     *
     * @param time Expect "3hourly" or "daily".
     */
    protected void getForecastsSiteList(String time) {
        getSiteList(ss.getString_wxfcs(), time);
    }

    /**
     * Get forecast site list.
     *
     * @param type Expected either "wxfcs" or "wxobs".
     * @param time Expected null for observations, or "daily" or "3hourly" for
     * forecasts.
     */
    protected void getSiteList(String type, String time) {
        path = sf.getValDataTypePath(dataType, type)
                + ss.getString_sitelist();
        //http://datapoint.metoffice.gov.uk/public/data/val/wxobs/all/xml/sitelist?key=382c1804-3077-48cf-a301-f6f95e396794
        //http://datapoint.metoffice.gov.uk/public/data/val/wxfcs/all/xml/sitelist?res=daily&key=382c1804-3077-48cf-a301-f6f95e396794
        //http://datapoint.metoffice.gov.uk/public/data/val/wxfcs/all/xml/sitelist?res=3hourly&key=382c1804-3077-48cf-a301-f6f95e396794
        url = BASE_URL
                + path
                + ss.symbol_questionmark;
        if (time != null) {
            url += ss.getString_res() + ss.symbol_equals + time + ss.symbol_ampersand;
        }
        url += ss.getString_key() + ss.symbol_equals + API_KEY;
        File dir;
        dir = new File(
                sf.getInputDataMetOfficeDataPointDir(),
                path);
        if (time != null) {
            dir = new File(
                    dir,
                    time);
        }
        dir.mkdirs();
        File xml;
        xml = new File(dir,
                ss.getString_sitelist() + "." + dataType);
        getXML(url, xml);
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
    protected TreeSet<SARIC_Time> getObservationsLayerTimes(
            String layerName,
            File xml) {
        TreeSet<SARIC_Time> result;
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
        imageFormat = ss.getString_png();
        String timeStep;
        String outputFilenameWithoutExtension;
        for (int step = 0; step <= 36; step += 3) {
            timeStep = Integer.toString(step);
            System.out.println("Getting forecast for time " + timeStep);
            path = ss.getString_layer() + ss.symbol_backslash
                    + ss.getString_wxfcs() + ss.symbol_backslash
                    + layerName + ss.symbol_backslash
                    + imageFormat;
            url = BASE_URL
                    + path
                    + ss.symbol_questionmark
                    + "RUN" + ss.symbol_equals + time + "Z"
                    + ss.symbol_ampersand + "FORECAST" + ss.symbol_equals + timeStep
                    + ss.symbol_ampersand + ss.getString_key() + ss.symbol_equals + API_KEY;
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
            TreeSet<SARIC_Time> times,
            boolean overwrite) {
        //http://datapoint.metoffice.gov.uk/public/data/layer/wxobs/{LayerName}/{ImageFormat}?TIME={Time}Z&key={key}
        String imageFormat;
        imageFormat = ss.getString_png();
        SARIC_Time time;
        Iterator<SARIC_Time> ite;
        ite = times.iterator();
        while (ite.hasNext()) {
            time = ite.next();
            //System.out.println(time);
            //if (time.contains("00:00")) {
            path = ss.getString_layer() + ss.symbol_backslash
                    + ss.getString_wxobs() + ss.symbol_backslash
                    + layerName + ss.symbol_backslash
                    + imageFormat;
            url = BASE_URL
                    + path
                    + ss.symbol_questionmark
                    + "TIME" + ss.symbol_equals + time + ss.string_Z
                    + ss.symbol_ampersand + ss.getString_key() + ss.symbol_equals + API_KEY;
            String outputFilenameWithoutExtension;
            outputFilenameWithoutExtension = layerName + time.toFormattedString0();
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
//        ArrayList<String> times;
//        times = r.getTimesInspireWMTS(layerName);
        TreeSet<SARIC_Time> times;
        times = r.getTimesInspireWMTS(layerName);
        p.setTimes(times);
        HashMap<String, SARIC_MetOfficeLayerParameters> metOfficeLayerParameters;
        metOfficeLayerParameters = p.getMetOfficeLayerParameters();
        SARIC_MetOfficeLayerParameters lp;
        String tileMatrix;
        String tileMatrixFormatted;
        for (int matrix = 0; matrix < 7; matrix += 1) {
            //for (int matrix = 2; matrix < 7; matrix += 1) {
            tileMatrix = tileMatrixSet + ":" + matrix; // British National Grid
            tileMatrixFormatted = tileMatrix.replace(':', '_');
            //tileMatrix = "EPSG:4326:0"; // WGS84
            lp = metOfficeLayerParameters.get(tileMatrix);
            if (lp == null) {
                lp = new SARIC_MetOfficeLayerParameters(se, r.getCellsize(tileMatrix), p);
            }
            lp.setNrows(r.getNrows(tileMatrix));
            lp.setNcols(r.getNcols(tileMatrix));
            //http://datapoint.metoffice.gov.uk/public/data/inspire/view/wmts?REQUEST=gettile&LAYER=<layer required>&FORMAT=image/png&TILEMATRIXSET=<projection>&TILEMATRIX=<projection zoom level required>&TILEROW=<tile row required>&TILECOL=<tile column required>&TIME=<time required>&STYLE=<style required>&key=<API key>
            path = "inspire/view/wmts";
            String tileRow;
            String tileCol;
            //String time;
            SARIC_Time time;
            String dateString;
            String timeformatted;
            String yearMonth;
            Iterator<SARIC_Time> ite;
            ite = times.iterator();
//            Iterator<String> ite;
//            ite = times.iterator();
            while (ite.hasNext()) {
                time = ite.next();
                System.out.println(time);
                timeformatted = time.toFormattedString0();
                dateString = time.getDateString();
                yearMonth = time.getYearMonth();
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
                        outputFilenameWithoutExtension = layerName + "_"
                                + tileMatrixFormatted + "_"
                                + timeformatted + "_"
                                + tileRow + "_" + tileCol;
                        String pathDummy = path;
                        path += "/" + layerName
                                + "/" + tileMatrixFormatted
                                + "/" + yearMonth
                                + "/" + dateString
                                + "/" + timeformatted;
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
        TreeSet<SARIC_Time> times;
        times = r.getTimesInspireWMTS(layerName);
        p.setTimes(times);
        HashMap<String, SARIC_MetOfficeLayerParameters> metOfficeLayerParameters;
        metOfficeLayerParameters = p.getMetOfficeLayerParameters();
        SARIC_MetOfficeLayerParameters lp;
        String tileMatrix;
//        BigDecimal[] tileDimensions;
        Vector_Envelope2D tileBounds;
        String tileMatrixFormatted;
        String tileRow;
        String tileCol;
        SARIC_Time time;
        String dateString;
        String timeformatted;
        String yearMonth;
        Iterator<SARIC_Time> ite;
        int matrix = 4;
        //for (matrix = 0; matrix < 7; matrix += 1) {
        //for (matrix = 0; matrix < 5; matrix += 1) {
        tileMatrix = tileMatrixSet + ":" + matrix; // British National Grid
        tileMatrixFormatted = tileMatrix.replace(':', '_');

        //tileMatrix = "EPSG:4326:0"; // WGS84
        //System.out.println("tileMatrix " + tileMatrix);
        lp = metOfficeLayerParameters.get(tileMatrix);
        if (lp == null) {
            lp = new SARIC_MetOfficeLayerParameters(se, r.getCellsize(tileMatrix), p);
            System.out.println("Cellsize " + lp.getCellsize());
        }
        lp.setNrows(r.getNrows(tileMatrix));
        lp.setNcols(r.getNcols(tileMatrix));
        //http://datapoint.metoffice.gov.uk/public/data/inspire/view/wmts?REQUEST=gettile&LAYER=<layer required>&FORMAT=image/png&TILEMATRIXSET=<projection>&TILEMATRIX=<projection zoom level required>&TILEROW=<tile row required>&TILECOL=<tile column required>&TIME=<time required>&STYLE=<style required>&key=<API key>
        //http://datapoint.metoffice.gov.uk/public/data/inspire/view/wmts?REQUEST=gettile&LAYER=Precipitation_Rate&FORMAT=image/png&TILEMATRIXSET=EPSG:4258&TILEMATRIX=EPSG:4258:0&TILEROW=0&TILECOL=0&DIM_RUN=2013-11-20T03:00:00Z&DIM_FORECAST=%2B9&STYLE=Bitmap%20Blue-Pale%20blue%20gradient%200.01%20to%20greater%20than%2032mm%2Fhr&key=<API key>Note that the + character has been URL encoded to %2B and the / character to %2F
        path = "inspire/view/wmts";
        ite = times.iterator();
        while (ite.hasNext()) {
            time = ite.next();
            System.out.println(time);
            timeformatted = time.toFormattedString0();
            dateString = time.getDateString();
            yearMonth = time.getYearMonth();
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
                        outputFilenameWithoutExtension = layerName + "_"
                                + tileMatrixFormatted + "_"
                                + timeformatted + "_"
                                + tileRow + "_" + tileCol;
                        String pathDummy = path;
                        path += "/" + areaName
                                + "/" + layerName
                                + "/" + tileMatrixFormatted
                                + "/" + yearMonth
                                + "/" + dateString
                                + "/" + timeformatted;
                        getPNG(outputFilenameWithoutExtension, overwrite);
                        path = pathDummy;
                    }
                    //break; // For testing
                }
            }
            //break; // For testing
            //System.exit(0);
        }
        //}
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
        TreeSet<SARIC_Time> times;
        times = r.getTimesInspireWMTS(layerName);
        p.setTimes(times);
        HashMap<String, SARIC_MetOfficeLayerParameters> metOfficeLayerParameters;
        metOfficeLayerParameters = p.getMetOfficeLayerParameters();
        SARIC_MetOfficeLayerParameters lp;
        String tileMatrix;
//        BigDecimal[] tileDimensions;
        Vector_Envelope2D tileBounds;
        String tileMatrixFormatted;
        String timeFormatted;
        String dateString;
        String yearMonth;
        int matrix = 4;
        //for (matrix = 0; matrix < 7; matrix += 1) {
        //for (matrix = 0; matrix < 5; matrix += 1) {
        tileMatrix = tileMatrixSet + ":" + matrix; // British National Grid
        tileMatrixFormatted = tileMatrix.replace(':', '_');
        //tileMatrix = "EPSG:4326:0"; // WGS84
        //System.out.println("tileMatrix " + tileMatrix);
        lp = metOfficeLayerParameters.get(tileMatrix);
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
        SARIC_Time time;
        Iterator<SARIC_Time> ite;
        ite = times.iterator();
        while (ite.hasNext()) {
            time = ite.next();
            System.out.println(time);
            timeFormatted = time.toFormattedString1();
            dateString = time.getDateString();
            yearMonth = time.getYearMonth();
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
                            outputFilenameWithoutExtension = layerName
                                    + "_" + tileMatrixFormatted
                                    + "_" + timeFormatted
                                    + "_" + tileRow + "_" + tileCol;
                            String pathDummy = path;
                            path += "/" + areaName
                                    + "/" + layerName
                                    + "/" + tileMatrixFormatted
                                    + "/" + yearMonth
                                    + "/" + dateString
                                    + "/" + timeFormatted
                                    + "/" + forecastTime;
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
        //}
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
                + "?REQUEST=get" + ss.getString_capabilities()
                + ss.symbol_ampersand
                + ss.getString_key() + ss.symbol_equals + API_KEY;
        File result = getXML(ss.getString_capabilities(), -1, null);
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
        setPath(ss.getString_val(), ss.getString_wxobs(), dataType);
        addCapabilitiesToPath();
        return getCapabilities(-1);
    }

    /**
     * Download capabilities document for the observations sites in XML format
     *
     * @return
     */
    protected File getObservationsSiteCapabilities() {
        setPath(ss.getString_val(), ss.getString_wxobs(), dataType);
        addCapabilitiesToPath();
        path += ss.getString_res() + ss.symbol_equals + ss.getString_hourly() 
                + ss.symbol_ampersand;
        return getCapabilities(0);
    }

    /**
     * Download capabilities document for current WMTS observation layer in XML
     * format
     *
     * @param parsePath This will determine if path is parsed and how. The path
     * can be parsed and amended to write out data in a specific location.
     * @return
     */
    protected File getCapabilities(int parsePath) {
        File result;
        url = BASE_URL
                + path
                + ss.getString_key() + ss.symbol_equals + API_KEY;
        result = getXML(ss.getString_capabilities(), parsePath, null);
        return result;
    }

    /**
     * Download capabilities document for the forecast layers in XML format
     *
     * @return
     */
    protected File getForecastsLayerCapabilities() {
        // http://datapoint.metoffice.gov.uk/public/data/layer/wxfcs/all/xml/capabilities?key=<API key>
        setPath(ss.getString_val(), ss.getString_wxfcs(), dataType);
        addCapabilitiesToPath();
        return getCapabilities(-1);
    }

    /**
     * Download capabilities document for the forecast sites in XML format
     *
     * @param time Expecting "daily" or 3hourly
     * @return
     */
    protected File getForecastsSiteCapabilities(String time) {
        // http://datapoint.metoffice.gov.uk/public/data/val/wxfcs/all/xml/capabilities?res=3hourly&key=01234567-89ab-cdef-0123-456789abcdef
        setPath(ss.getString_val(), ss.getString_wxfcs(), dataType);
        addCapabilitiesToPath();
        path += ss.getString_res() + ss.symbol_equals + time + ss.symbol_ampersand;
        return getCapabilities(0);
    }

    protected void setPath(
            String val_Or_layer,
            String wxfcs_Or_Wxobs,
            String dataType) {
        path = val_Or_layer + ss.symbol_backslash
                + wxfcs_Or_Wxobs + ss.symbol_backslash
                + ss.getString_all() + ss.symbol_backslash
                + dataType + ss.symbol_backslash;
    }

    public String getParsedPath0() {
        String result;
        String[] parts;
        parts = path.split("capabilities\\?res=");
        result = parts[0] + parts[1].substring(0, parts[1].length() - 1);
        return result;
    }

    public String getParsedPath1() {
        String result;
        String[] parts;
        parts = path.split(ss.getString_xml());
        result = parts[0] + ss.getString_xml() + ss.symbol_backslash
                + ss.getString_site() + ss.symbol_backslash + parts[1];
        return result;
    }

    protected void addCapabilitiesToPath() {
        path += ss.getString_capabilities() + ss.symbol_questionmark;
    }

    /**
     *
     * @param name
     * @param parsePath This will determine if path is parsed and how. The path
     * can be parsed and amended to write out data in a specific location. If
     * parsePath == 0 then the path is parsed to ensure the right directories
     * are set up for writing the XML for a get capabilities style request as in
     * pasePath0(). if parsePath == 1 then the path is parsed to add a couple of
     * directories for tidiness and for the time range as in parsePath1().
     * @param endDir If null then nothing is added to the path otherwise endDir
     * is added to the outputDir. path.
     * @return
     */
    protected File getXML(String name, int parsePath, String endDir) {
        File outputDir;
        switch (parsePath) {
            case 0:
                outputDir = new File(
                        sf.getInputDataMetOfficeDataPointDir(),
                        getParsedPath0());
                break;
            case 1:
                outputDir = new File(
                        sf.getInputDataMetOfficeDataPointDir(),
                        getParsedPath1());
                break;
            default:
                outputDir = new File(
                        sf.getInputDataMetOfficeDataPointDir(),
                        path);
                break;
        }
        if (endDir != null) {
            outputDir = new File(
                    outputDir,
                    endDir);
        }
        outputDir.mkdirs();
        File xml;
        xml = new File(
                outputDir,
                name + "." + dataType);
//        xml = Generic_StaticIO.createNewFile(
//                outputDir,
//                name + "." + dataType);
        // http://datapoint.metoffice.gov.uk/public/data/val/wxfcs/all/xml/sitelist?res=daily&key=382c1804-3077-48cf-a301-f6f95e396794
        getXML(url, xml);
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
                sf.getInputDataMetOfficeDataPointDir(),
                path);
        outputDir.mkdirs();
        File png;
        png = new File(
                outputDir,
                outputFilenameWithoutExtension + "." + ss.getString_png());
        if (!overwrite) {
            if (png.exists()) {
                System.out.println("File " + png.toString() + " already exists and is not being overwritten.");
                return; // If the file already exists and we are not in overwrite mode then don't bother getting the data and writing it out.
            }
        }
        getPNG(url,
                png);
    }

    /**
     * Read Met Office DataPoint API Key from MetOfficeAPIKey.txt file.
     *
     * @return
     */
    public String getAPI_KEY() {
        File f;
        f = sf.getInputDataMetOfficeDataPointAPIKeyFile();
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

    /**
     *
     * @param sites
     * @param dir
     * @param buffer
     */
    protected void calculateSitesInStudyAreas(
            HashSet<SARIC_Site> sites,
            File dir,
            BigDecimal buffer) {
        // Wissey
        SARIC_Wissey wissey;
        wissey = new SARIC_Wissey(se);
        Vector_Envelope2D wisseyBounds;
        wisseyBounds = wissey.getBoundsBuffered(buffer);
        // Teifi
        SARIC_Teifi teifi;
        teifi = new SARIC_Teifi(se);
        Vector_Envelope2D teifiBounds;
        teifiBounds = teifi.getBoundsBuffered(buffer);

        HashSet<SARIC_Site> sitesInWissey;
        sitesInWissey = new HashSet<SARIC_Site>();
        HashSet<SARIC_Site> sitesInTeifi;
        sitesInTeifi = new HashSet<SARIC_Site>();
        Iterator<SARIC_Site> ite;
        ite = sites.iterator();
        SARIC_Site site;
        double[] OSGBEastingAndNorthing;
        Vector_Point2D p;
        while (ite.hasNext()) {
            site = ite.next();
            OSGBEastingAndNorthing = Vector_OSGBtoLatLon.latlon2osgb(
                    site.getLatitude(), site.getLongitude());
            p = new Vector_Point2D(ve, OSGBEastingAndNorthing[0], OSGBEastingAndNorthing[1]);
            if (wisseyBounds.getIntersects(p)) {
                sitesInWissey.add(site);
            }
            if (teifiBounds.getIntersects(p)) {
                sitesInTeifi.add(site);
            }
        }
        System.out.println("There are " + sitesInWissey.size() + " sites in the Wissey.");
        System.out.println("There are " + sitesInTeifi.size() + " sites in the Teifi.");
        // Write out sites in the Wissey/Teifi

        File outfile;
        outfile = getSitesFile(ss.getString_Wissey(), buffer, dir);
        Generic_StaticIO.writeObject(sitesInWissey, outfile);
        outfile = getSitesFile(ss.getString_Teifi(), buffer, dir);
        Generic_StaticIO.writeObject(sitesInTeifi, outfile);
    }

    protected File getSitesFile(String name, BigDecimal buffer, File dir) {
        File result;
        if (buffer == null) {
            result = new File(
                    dir,
                    name + "_HashSet_SARIC_Site.dat");
        } else {
            result = new File(
                    dir,
                    name + "_" + buffer.toPlainString() + "_HashSet_SARIC_Site.dat");
        }
        return result;
    }

    protected HashSet<SARIC_Site> getSites(String name, BigDecimal buffer, File dir) {
        HashSet<SARIC_Site> result;
        File f;
        f = getSitesFile(name, buffer, dir);
        result = (HashSet<SARIC_Site>) Generic_StaticIO.readObject(f);
        return result;
    }

    protected void getForecastsForSites(String name, BigDecimal buffer, String res, String timeRange) {
        File dir;
        dir = sf.getGeneratedDataMetOfficeDataPointForecastsDir();
        HashSet<SARIC_Site> sites;
        sites = getSites(name, buffer, dir);
        SARIC_Site site;
        Iterator<SARIC_Site> ite;
        ite = sites.iterator();
        while (ite.hasNext()) {
            site = ite.next();
            getForecastsSite(site.getId(), res, timeRange);
        }
    }

    protected void getObservationsForSites(String name, BigDecimal buffer, String timeRange) {
        File dir;
        dir = sf.getGeneratedDataMetOfficeDataPointObservationsDir();
        HashSet<SARIC_Site> sites;
        sites = getSites(name, buffer, dir);
        SARIC_Site site;
        Iterator<SARIC_Site> ite;
        ite = sites.iterator();
        while (ite.hasNext()) {
            site = ite.next();
            getObservationsSite(site.getId(), timeRange);
        }
    }

    /**
     *
     * @param f
     * @return String[3] where: String[0] is a simplified timeRange; String[1]
     * is the firstTime; String[2] is the lastTime.
     */
    protected String[] getTimeRange(File f) {
        String[] result;
        result = new String[3];
        SARIC_MetOfficeCapabilitiesXMLDOMReader r;
        r = new SARIC_MetOfficeCapabilitiesXMLDOMReader(se, f);
        ArrayList<String> times;
        times = r.getTimes();
        String firstTime;
        String lastTime;
        firstTime = times.get(0);
        lastTime = times.get(times.size() - 1);
        result[0] = firstTime.substring(0, firstTime.length() - 4);
        result[0] += ss.symbol_underscore + ss.symbol_underscore;
        result[0] += lastTime.substring(0, lastTime.length() - 4);
        result[0] = result[0].replaceAll(ss.symbol_colon, ss.symbol_underscore);
        result[1] = firstTime;
        result[2] = lastTime;
        return result;
    }
}
