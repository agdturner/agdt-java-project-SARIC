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
package uk.ac.leeds.ccg.andyt.projects.saric.process;

import java.awt.Color;
import java.awt.Image;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.PixelGrabber;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Dimensions;
//import uk.ac.leeds.ccg.andyt.grids.core.grid.stats.Grids_AbstractGridNumberStats;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridDouble;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridDoubleFactory;
//import uk.ac.leeds.ccg.andyt.grids.core.grid.stats.Grids_GridDoubleStatsNotUpdated;
import uk.ac.leeds.ccg.andyt.grids.io.Grids_ESRIAsciiGridExporter;
import uk.ac.leeds.ccg.andyt.grids.io.Grids_Files;
import uk.ac.leeds.ccg.andyt.grids.io.Grids_ImageExporter;
import uk.ac.leeds.ccg.andyt.grids.process.Grids_Processor;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Environment;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Object;
import uk.ac.leeds.ccg.andyt.projects.saric.data.catchment.SARIC_Teifi;
import uk.ac.leeds.ccg.andyt.projects.saric.data.catchment.SARIC_Wissey;
import uk.ac.leeds.ccg.andyt.projects.saric.data.metoffice.datapoint.SARIC_MetOfficeCapabilitiesXMLDOMReader;
import uk.ac.leeds.ccg.andyt.projects.saric.data.metoffice.datapoint.SARIC_MetOfficeLayerParameters;
import uk.ac.leeds.ccg.andyt.projects.saric.data.metoffice.datapoint.SARIC_MetOfficeParameters;
import uk.ac.leeds.ccg.andyt.projects.saric.data.metoffice.datapoint.site.SARIC_MetOfficeSiteXMLSAXHandler;
import uk.ac.leeds.ccg.andyt.projects.saric.data.metoffice.datapoint.site.SARIC_Site;
import uk.ac.leeds.ccg.andyt.projects.saric.data.metoffice.datapoint.site.SARIC_SiteForecastRecord;
import uk.ac.leeds.ccg.andyt.generic.time.Generic_Date;
import uk.ac.leeds.ccg.andyt.generic.time.Generic_Time;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Strings;
//import uk.ac.leeds.ccg.andyt.generic.utilities.time.Generic_YearMonth;
import uk.ac.leeds.ccg.andyt.projects.saric.visualisation.SARIC_Colour;
import uk.ac.leeds.ccg.andyt.vector.geometry.Vector_Envelope2D;

/**
 * For processing images and point forecasts into numerical ASCII data and
 * images.
 *
 * @author Andy Turner
 */
public class SARIC_ImageProcessor extends SARIC_Object implements Runnable {

    /**
     * For convenience
     */
    Grids_Environment ge;
    Grids_Processor gp;
    //Grids_GridDoubleFactory gf;
    Grids_ESRIAsciiGridExporter ae;
    Grids_ImageExporter ie;
    File dirIn;
    File dirOut;
    boolean doNonTiledFcs;
    boolean doNonTiledObs;
    boolean doTileFromWMTSService;
    boolean doObservationsTileFromWMTSService;
    boolean doForecastsTileFromWMTSService;
    boolean doWissey;
    boolean doTeifi;
    boolean overwrite;
    /**
     * @param estimateType if estimateType == 1 this is a high estimate,
     * estimateType == -1 this is a low estimate, estimateType == 0 this is a
     * average estimate,
     */
    int estimateType;
    String estimateName;
    /**
     * If outputGreyscale == true then image processing will output grey scale
     * images which are shaded from black to white through the shades of grey.
     */
    boolean outputGreyscale;
    /**
     * If colorDubpication == 0 then only the normal sized colour outputs are
     * generated. However if this is greater than zero then additional colour
     * outputs are produced resampling the grid colorDubpication number of
     * times.
     */
    int colorDubpication;
    /**
     * ouputLargeColorDubpication is true iff colorDuplication >= 1
     */
    boolean ouputLargeColorDubpication;
    Color Blue = Color.decode("#0000FE");
    Color LightBlue = Color.decode("#3265FE");
    Color MuddyGreen = Color.decode("#7F7F00");
    Color Yellow = Color.decode("#FECB00");
    Color Orange = Color.decode("#FE9800");
    Color Red = Color.decode("#FE0000");
    Color Pink = Color.decode("#FE00FE");
    Color PaleBlue = Color.decode("#E5FEFE");

    public SARIC_ImageProcessor(
            SARIC_Environment se,
            File dirIn,
            File dirOut,
            boolean doNonTiledFcs,
            boolean doNonTiledObs,
            boolean doTileFromWMTSService,
            boolean doObservationsTileFromWMTSService,
            boolean doForecastsTileFromWMTSService,
            boolean doWissey,
            boolean doTeifi,
            boolean overwrite,
            int estimateType,
            boolean outputGreyscale,
            int colorDubpication
    ) {
        super(se);
        this.dirIn = dirIn;
        this.dirOut = dirOut;
        this.doNonTiledFcs = doNonTiledFcs;
        this.doNonTiledObs = doNonTiledObs;
        this.doTileFromWMTSService = doTileFromWMTSService;
        this.doObservationsTileFromWMTSService = doObservationsTileFromWMTSService;
        this.doForecastsTileFromWMTSService = doForecastsTileFromWMTSService;
        this.doWissey = doWissey;
        this.doTeifi = doTeifi;
        this.overwrite = overwrite;
        this.estimateType = estimateType;
        estimateName = getEstimateName(estimateType);
        this.outputGreyscale = outputGreyscale;
        this.colorDubpication = colorDubpication;
        ouputLargeColorDubpication = colorDubpication >= 1;
        ge = se.gridsEnv;
        ae = new Grids_ESRIAsciiGridExporter(ge);
        ie = new Grids_ImageExporter(ge);
        gp = ge.getProcessor();
        //init_gf();
    }

//    private void init_gf() {
//        gf = new Grids_GridDoubleFactory(env, gp.GridChunkDoubleFactory,
//                gp.DefaultGridChunkDoubleFactory, -Double.MAX_VALUE,
//                256, 256, new Grids_Dimensions(256, 256),
//                new Grids_GridDoubleStatsNotUpdated(env));
//        gp.GridDoubleFactory = gf;
//    }
    public static String getEstimateName(int estimateType) {
        switch (estimateType) {
            case -1:
                return "l";
            case 0:
                return "m";
            default:
                return "h";
        }
    }

    @Override
    public void run() {

        SARIC_Colour sc;
        sc = new SARIC_Colour(se);

        TreeMap<Double, Color> colorMap;
        colorMap = sc.getColorMap();
        Color ndvColor;
        ndvColor = Color.BLACK;
        HashSet<SARIC_Site> sites;

        String area;
        Grids_GridDoubleFactory f;
        Grids_GridDouble g;

        // Initialisation for Wissey
        Object[] sw1KMGridMaskedToCatchment = null;
        if (doWissey) {
            SARIC_Wissey sw;
            sw = se.getWissey();
            sw1KMGridMaskedToCatchment = sw.get1KMGridMaskedToCatchment();
        }

        // Initialisation for Teifi
        Object[] st1KMGridMaskedToCatchment = null;
        if (doTeifi) {
            SARIC_Teifi st;
            st = se.getTeifi();
            st1KMGridMaskedToCatchment = st.get1KMGridMaskedToCatchment();
        }

        if (doNonTiledFcs) {
            //C:\Users\geoagdt\src\projects\saric\data\input\MetOffice\DataPoint\val\wxfcs\all\xml\site\3hourly\2017-09-04-11
            // Declaration part 1
            /**
             * dates is for storing a set of dates that will be processed. This
             * is initialised in a manual way currently below, but it could also
             * be initialised by looking at what data are stored in a directory.
             */
            Object[] nearestForecastsSitesGridAndFactory;

            if (doWissey) {
                area = SARIC_Strings.s_Wissey;
                SARIC_Wissey sw;
                sw = new SARIC_Wissey(se);
                sites = sw.getForecastsSitesInStudyArea(SARIC_Strings.s_3hourly);
                nearestForecastsSitesGridAndFactory = sw.getNearestForecastsSitesGrid(sites);
                g = (Grids_GridDouble) nearestForecastsSitesGridAndFactory[0];
                f = (Grids_GridDoubleFactory) nearestForecastsSitesGridAndFactory[1];
                processForecastPoints2(area, sites, f, g, colorMap);
            }
            if (doTeifi) {
                area = SARIC_Strings.s_Teifi;
                SARIC_Teifi st;
                st = new SARIC_Teifi(se);
                sites = st.getForecastsSitesInStudyArea(SARIC_Strings.s_3hourly);
                nearestForecastsSitesGridAndFactory = st.getNearestForecastsSitesGrid(sites);
                g = (Grids_GridDouble) nearestForecastsSitesGridAndFactory[0];
                f = (Grids_GridDoubleFactory) nearestForecastsSitesGridAndFactory[1];
                processForecastPoints2(area, sites, f, g, colorMap);
            }
        }
        if (doTileFromWMTSService) {
            // Initial declaration
            File inspireWMTSCapabilities;
            SARIC_MetOfficeParameters p;
            SARIC_MetOfficeCapabilitiesXMLDOMReader r;
            String layerName;
            String tileMatrixSet;
            String tileMatrix;
            HashMap<String, SARIC_MetOfficeLayerParameters> metOfficeLayerParameters;
            BigDecimal cellsize;
            int nrows;
            int ncols;
            Vector_Envelope2D bounds;
            // Initial assignment
            inspireWMTSCapabilities = Files.getInputDataMetOfficeDataPointInspireViewWmtsCapabilitiesFile();
            p = new SARIC_MetOfficeParameters(se);
            r = new SARIC_MetOfficeCapabilitiesXMLDOMReader(se, inspireWMTSCapabilities);
            tileMatrixSet = "EPSG:27700"; // British National Grid

            // Forecasts
            if (doForecastsTileFromWMTSService) {
                layerName = "Precipitation_Rate";
                for (int scale = 4; scale < 5; scale++) {
                    System.out.println("scale " + scale);
                    tileMatrix = tileMatrixSet + ":" + scale;
                    metOfficeLayerParameters = p.getMetOfficeLayerParameters();
                    SARIC_MetOfficeLayerParameters lp;
                    lp = metOfficeLayerParameters.get(tileMatrix);
                    cellsize = r.getCellsize(tileMatrix);
                    if (lp == null) {
                        lp = new SARIC_MetOfficeLayerParameters(se, cellsize, p);
                        lp.setNrows(256);
                        lp.setNcols(256);
                    }
                    nrows = r.getNrows(tileMatrix);
                    ncols = r.getNcols(tileMatrix);
                    bounds = r.getDimensions(cellsize, nrows, ncols, tileMatrix, p.TwoFiveSix);
                    System.out.println(bounds.toString());
                    p.setBounds(bounds);
                    if (doWissey) {
                        area = SARIC_Strings.s_Wissey;
                        f = (Grids_GridDoubleFactory) sw1KMGridMaskedToCatchment[1];
                        g = (Grids_GridDouble) sw1KMGridMaskedToCatchment[0];
                        System.out.println(g.toString());
                        processForecastImages(colorMap, ndvColor, area,
                                scale, layerName, cellsize, p, lp, r, f, g);
                    }
                    if (doTeifi) {
                        area = SARIC_Strings.s_Teifi;
                        f = (Grids_GridDoubleFactory) st1KMGridMaskedToCatchment[1];
                        g = (Grids_GridDouble) st1KMGridMaskedToCatchment[0];
                        processForecastImages(colorMap, ndvColor, area,
                                scale, layerName, cellsize, p, lp, r, f, g);
                    }
                }
            }
            // Observations
            if (doObservationsTileFromWMTSService) {
                layerName = SARIC_Strings.s_RADAR_UK_Composite_Highres;
                for (int scale = 4; scale < 5; scale++) {
                    tileMatrix = tileMatrixSet + ":" + scale;
                    metOfficeLayerParameters = p.getMetOfficeLayerParameters();
                    SARIC_MetOfficeLayerParameters lp;
                    lp = metOfficeLayerParameters.get(tileMatrix);
                    cellsize = r.getCellsize(tileMatrix);
                    if (lp == null) {
                        lp = new SARIC_MetOfficeLayerParameters(se, cellsize, p);
                    }
                    nrows = r.getNrows(tileMatrix); // nrows is the number of rows of tiles.
                    ncols = r.getNcols(tileMatrix); // ncols is the number of columns of tiles.
                    bounds = r.getDimensions(cellsize, nrows, ncols, tileMatrix, p.TwoFiveSix);
                    //System.out.println(bounds.toString());
                    p.setBounds(bounds);
                    if (doWissey) {
                        area = SARIC_Strings.s_Wissey;
                        f = (Grids_GridDoubleFactory) sw1KMGridMaskedToCatchment[1];
                        g = (Grids_GridDouble) sw1KMGridMaskedToCatchment[0];
                        processObservationImages(colorMap, ndvColor, area, scale,
                                layerName, cellsize, p, lp, r, f, g);
                    }
                    if (doTeifi) {
                        area = SARIC_Strings.s_Teifi;
                        f = (Grids_GridDoubleFactory) st1KMGridMaskedToCatchment[1];
                        g = (Grids_GridDouble) st1KMGridMaskedToCatchment[0];
                        processObservationImages(colorMap, ndvColor, area, scale,
                                layerName, cellsize, p, lp, r, f, g);
                    }
                }
            }
        }
    }

    /**
     *
     * @param area
     * @param sites
     * @param nearestForecastsSitesGridAndFactory
     * @param colorMap
     */
    private void processForecastPoints(String area,
            HashSet<SARIC_Site> sites,
            Grids_GridDoubleFactory f, Grids_GridDouble g,
            TreeMap<Double, Color> colorMap) {
        String methodName;
        methodName = "processForecastPoints(...)";
        System.out.println("<" + methodName + ">");
        System.out.println("Area " + area);
        Grids_Files gridf;
        gridf = ge.getFiles();
        File gdir;
        String dataType;
        String path;
        File indir0;
        File indir1;
        File outdir0;
        File outdir1;
        String name;
        // Initialisation part 1
        dataType = SARIC_Strings.s_xml;
        path = Files.getValDataTypePath(dataType, SARIC_Strings.s_wxfcs);
        indir0 = new File(Files.getInputDataMetOfficeDataPointDir(), path);
        System.out.println(indir0);
        indir0 = new File(indir0, SARIC_Strings.s_site + "0");
        /**
         * There is no need to run for daily, it is just for the same time as
         * the 3hourly, but gives lower temporal resolution and greater temporal
         * resolution is more desirable.
         */
        indir0 = new File(indir0, SARIC_Strings.s_3hourly);
        outdir0 = new File(Files.getOutputDataMetOfficeDataPointDir(), path);
        outdir0 = new File(outdir0, area);
        Generic_Date date;
        // Declaration part 2
        Generic_Date date1;
        TreeSet<Generic_Date> dates;
        //double noDataValue1;
        HashMap<SARIC_Site, HashMap<Generic_Time, SARIC_SiteForecastRecord>> forecasts;
        long nrows;
        long ncols;
        HashMap<Generic_Time, SARIC_SiteForecastRecord> forecastsForTime;

        //noDataValue1 = nearestForecastsSitesGrid.getNoDataValue();
        //gf.setNoDataValue(noDataValue1);
        nrows = g.getNRows();
        ncols = g.getNCols();
        Grids_GridDouble forecastsForTime2;
        File[] indirs;
        indirs = indir0.listFiles();
        File[] dirs3;
        if (indirs != null) {
            for (File dirs2 : indirs) {
                dirs3 = dirs2.listFiles();
                for (File dir3 : dirs3) {
                    date = new Generic_Date(se, dir3.getName());
                    indir1 = Files.getNestedTimeDirectory(indir0, date);
                    outdir1 = Files.getNestedTimeDirectory(outdir0, date);
                    outdir1 = new File(outdir1, date + "-00"); // Could iterate through all of these.
                    indir1 = new File(indir1, date + "-00");
                    outdir1.mkdirs();
                    System.out.println("outdir1 " + outdir1);
                    // Initialisation part 2
                    // Process the next n days from time too.
                    int n = 6;
                    dates = new TreeSet<>();
                    for (int i = 0; i < n; i++) {
                        date1 = new Generic_Date(date);
                        date1.addDays(i);
                        dates.add(date1);
                    }
                    Iterator<Generic_Date> iterat;
                    iterat = dates.iterator();
                    while (iterat.hasNext()) {
                        date1 = iterat.next();
                        File file;
                        if (indir1.exists()) {
                            forecasts = new HashMap<>();
                            gdir = gridf.createNewFile(gridf.getGeneratedGridDoubleDir());
                            //forecastsForTime2 = (Grids_GridDouble) gf.create(gdir, nearestForecastsSitesGrid);
                            forecastsForTime2 = (Grids_GridDouble) f.create(gdir, g);
                            name = date + "-00" + "_ForecastFor_" + date1.getYYYYMMDD();
                            file = new File(outdir1, name + ".asc");
                            if (file.exists()) {
                                System.out.println("Output " + file + " already exists!!!");
                            } else {
                                if (!outdir1.exists()) {
                                    outdir1.mkdirs();
                                }
                                double estimate;
                                double ndv = forecastsForTime2.getNoDataValue();
                                double v;

                                Iterator<SARIC_Site> ite;
                                ite = sites.iterator();
                                SARIC_Site site;
                                int siteID;
                                while (ite.hasNext()) {
                                    site = ite.next();
                                    siteID = site.getId();
                                    forecastsForTime = getForecastsForTime(indir1, siteID, dataType);
                                    forecasts.put(site, forecastsForTime);
                                    //System.out.println("SARIC_MetOfficeSiteXMLSAXHandler " + h);

                                    // Get estimate of total rainfall.
                                    estimate = 0.0d;
                                    Iterator<Generic_Time> ite2;
                                    double numberOfEstimates;
                                    numberOfEstimates = 0;
                                    Generic_Time t;
                                    ite2 = forecastsForTime.keySet().iterator();

                                    double normalisedEstimate;
                                    while (ite2.hasNext()) {
                                        t = ite2.next();
                                        if (t.isSameDay(date1)) {
                                            estimate += getEstimateFromPoint(forecastsForTime.get(t));
                                            numberOfEstimates++;
//                                                System.out.println("estimate " + estimate);
//                                                System.out.println("numberOfEstimates " + numberOfEstimates);
//                                                normalisedEstimate = (estimate / numberOfEstimates) * 24;
//                                                System.out.println("normalisedEstimate " + normalisedEstimate);
                                        }
                                    }
                                    /**
                                     * normalisedEstimate gives an estimate of
                                     * the total amount of rainfall in a day.
                                     * This averages all the intensities and
                                     * then multiplies by 24 as there are 24
                                     * hours in the day.
                                     */
                                    if (numberOfEstimates > 0.0d) {
                                        normalisedEstimate = (estimate / numberOfEstimates) * 24;
                                    } else {
                                        normalisedEstimate = 0.0d;
                                    }
                                    System.out.println("noDataValue " + ndv);
                                    for (long row = 0; row < nrows; row++) {
                                        for (long col = 0; col < ncols; col++) {
                                            v = g.getCell(row, col);
                                            if (v != ndv) {
                                                if (v == siteID) {
                                                    forecastsForTime2.setCell(row, col, normalisedEstimate);
                                                }
                                            }
                                        }
                                    }
                                }
                                outputGrid(outdir1, name, forecastsForTime2, Color.BLACK, colorMap);
                            }
                        }
                    }
                }
            }
        }
        System.out.println("</" + methodName + ">");
    }

    protected HashMap<Generic_Time, SARIC_SiteForecastRecord> getForecastsForTime(
            File dir, int siteID, String dataType) {
        HashMap<Generic_Time, SARIC_SiteForecastRecord> forecastsForTime;
        File dir1;
        File dir2;
        File file;
        String[] dirnames;
        String dirname;
        String filename;
        SARIC_MetOfficeSiteXMLSAXHandler h;
        dir1 = new File(dir, "" + siteID);
        System.out.println("indir " + dir1);
        dirnames = dir1.list(); //Sometimes data are missing here!
        if (dirnames != null) {
            dirname = dir1.list()[0]; //Sometimes data are missing here!
            dir2 = new File(dir1, dirname);
            filename = siteID + SARIC_Strings.s_3hourly 
                    + SARIC_Strings.symbol_dot + dataType;
            file = new File(dir2, filename);
            h = new SARIC_MetOfficeSiteXMLSAXHandler(se, file);
            forecastsForTime = h.parse();
            return forecastsForTime;
        }
        return null;
    }

    /**
     *
     * @param areaName
     * @param sites
     * @param nearestForecastsSitesGridAndFactory
     * @param colorMap
     */
    private void processForecastPoints2(String area,
            HashSet<SARIC_Site> sites,
            Grids_GridDoubleFactory f, Grids_GridDouble g,
            TreeMap<Double, Color> colorMap) {
        String methodName;
        methodName = "processForecastPoints(...)";
        System.out.println("<" + methodName + ">");
        System.out.println("Area " + area);
        Grids_Files gridf;
        gridf = ge.getFiles();
        File gdir;
        String dataType;
        String path;
        File indir0;
        File indir1;
        File outdir0;
        File outdir1;
        String name;
        // Initialisation part 1
        dataType = SARIC_Strings.s_xml;
        path = Files.getValDataTypePath(dataType, SARIC_Strings.s_wxfcs);
        indir0 = new File(Files.getInputDataMetOfficeDataPointDir(), path);
        System.out.println(indir0);
        //indir0 = new File(indir0, SARIC_Strings.s_site + "all");
        indir0 = new File(indir0, SARIC_Strings.s_site);
        /**
         * There is no need to run for daily, it is just for the same time as
         * the 3hourly, but gives lower temporal resolution and greater temporal
         * resolution is more desirable.
         */
        indir0 = new File(indir0, SARIC_Strings.s_3hourly);
        outdir0 = new File(Files.getOutputDataMetOfficeDataPointDir(), path);
        outdir0 = new File(outdir0, area);
        outdir0 = new File(outdir0, estimateName);
        Generic_Date date;
        // Declaration part 2
        Generic_Date date1;
        TreeSet<Generic_Date> dates;
        //double noDataValue1;
        long nrows;
        long ncols;
        HashMap<Generic_Time, SARIC_SiteForecastRecord> forecastsForTime;

        double estimate0;
        double estimate1;
        double weight;
        double ndv;
        double v;

        //noDataValue1 = nearestForecastsSitesGrid.getNoDataValue();
        //gf.setNoDataValue(noDataValue1);
        nrows = g.getNRows();
        ncols = g.getNCols();
        Grids_GridDouble forecastsForTime2;
        File[] indirs;
        indirs = indir0.listFiles();
        File[] dirs3;
        if (indirs != null) {
            for (File dirs2 : indirs) {
                dirs3 = dirs2.listFiles();
                for (File dir3 : dirs3) {
                    date = new Generic_Date(se, dir3.getName());
                    indir1 = Files.getNestedTimeDirectory(indir0, date);
                    outdir1 = Files.getNestedTimeDirectory(outdir0, date);
                    outdir1 = new File(outdir1, date + "-00"); // Could iterate through all of these.
                    indir1 = new File(indir1, date + "-00");
                    outdir1.mkdirs();
                    System.out.println("outdir1 " + outdir1);
                    // Initialisation part 2
                    // Process the next n days from time too.
                    int n = 6;
                    dates = new TreeSet<>();
                    for (int i = 0; i < n; i++) {
                        date1 = new Generic_Date(date);
                        date1.addDays(i);
                        dates.add(date1);
                    }
                    Iterator<Generic_Date> iterat;
                    iterat = dates.iterator();
                    while (iterat.hasNext()) {
                        date1 = iterat.next();
                        File file;
                        if (indir1.exists()) {
                            gdir = gridf.createNewFile(gridf.getGeneratedGridDoubleDir());
                            //forecastsForTime2 = (Grids_GridDouble) gf.create(gdir, nearestForecastsSitesGrid);
                            forecastsForTime2 = (Grids_GridDouble) f.create(gdir, g);
                            ndv = forecastsForTime2.getNoDataValue();
                            name = date + "-00" + "_ForecastFor_" + date1.getYYYYMMDD();
                            file = new File(outdir1, name + ".asc");
                            if (file.exists()) {
                                System.out.println("Output " + file + " already exists!!!");
                            } else {
                                if (!outdir1.exists()) {
                                    outdir1.mkdirs();
                                }
                                Iterator<SARIC_Site> ite;
                                ite = sites.iterator();
                                SARIC_Site site;
                                int siteID;
                                while (ite.hasNext()) {
                                    site = ite.next();
                                    siteID = site.getId();
                                    forecastsForTime = getForecastsForTime(indir1, siteID, dataType);
                                    if (forecastsForTime != null) {
                                        //System.out.println("SARIC_MetOfficeSiteXMLSAXHandler " + h);

                                        // Get estimate of total rainfall.
                                        estimate0 = 0.0d;
                                        Iterator<Generic_Time> ite2;
                                        Generic_Time t0 = null;
                                        Generic_Time t1;
                                        ite2 = forecastsForTime.keySet().iterator();

                                        double normalisedEstimate = 0.0d;
                                        while (ite2.hasNext()) {
                                            t0 = ite2.next();
                                            if (t0.isSameDay(date1)) {
                                                estimate0 += getEstimateFromPoint(forecastsForTime.get(t0));
                                            }
                                        }
                                        while (ite2.hasNext()) {
                                            t1 = ite2.next();
                                            if (t1.isSameDay(date1)) {
                                                estimate1 = getEstimateFromPoint(forecastsForTime.get(t1));
                                                weight = Math.abs(t0.differenceInHours(t1)) / 60.0d;
                                                normalisedEstimate += (estimate0 + estimate1) * weight;
                                            }
                                        }
                                        System.out.println("noDataValue " + ndv);
                                        for (long row = 0; row < nrows; row++) {
                                            for (long col = 0; col < ncols; col++) {
                                                v = g.getCell(row, col);
                                                if (v != ndv) {
                                                    if (v == siteID) {
                                                        forecastsForTime2.setCell(row, col, normalisedEstimate);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                outputGrid(outdir1, name, forecastsForTime2, Color.BLACK, colorMap);
                            }
                        }
                    }
                }
            }
        }
        System.out.println("</" + methodName + ">");
    }

    /**
     * For processing forecasts which are generally at 3 hourly intervals.
     *
     * @param colorMap
     * @param ndvColor
     * @param area
     * @param scale
     * @param layerName
     * @param cellsize
     * @param p
     * @param lp
     * @param r
     * @param f 1km masked factory
     * @param g 1km masked grid
     */
    public void processForecastImages(TreeMap<Double, Color> colorMap,
            Color ndvColor, String area, int scale, String layerName,
            BigDecimal cellsize, SARIC_MetOfficeParameters p,
            SARIC_MetOfficeLayerParameters lp,
            SARIC_MetOfficeCapabilitiesXMLDOMReader r,
            Grids_GridDoubleFactory f, Grids_GridDouble g) {
        String methodName;
        methodName = "processForecastImages(...)";
        System.out.println("<" + methodName + ">");
        System.out.println("Area " + area);
        // Initial declaration
        Grids_Files gridf;
        gridf = ge.getFiles();
        File gdir;
        String pathIn;
        String pathOut;
        File outdir0;
        File outdir1;
        File indir0;
        File gridsdir;
        // Initial assignment
        //pathIn = "inspire/view/wmts0/" + area + "/" + layerName + "/EPSG_27700_";
        //pathIn = "inspire/view/wmtsall/" + area + "/" + layerName + "/EPSG_27700_";
        pathIn = "inspire/view/wmts/" + area + "/" + layerName + "/EPSG_27700_";
        //pathOut = "inspire/view/wmts0/" + area + "/" + layerName + "/EPSG_27700_";
        pathOut = "inspire/view/wmts/" + area + "/" + layerName + "/EPSG_27700_";
        System.out.println("scale " + scale);
        indir0 = new File(Files.getInputDataMetOfficeDataPointDir(), pathIn + scale);
        outdir0 = new File(Files.getOutputDataMetOfficeDataPointDir(), pathOut + scale);
        outdir0 = new File(outdir0, estimateName);
        gridsdir = new File(Files.getGeneratedDataGridsDir(), pathOut + scale);

        String dateComponentDelimeter;
        String dateTimeDivider;
        String timeComponentDivider;
        String ending;
        dateComponentDelimeter = "-";
        dateTimeDivider = "T";
        timeComponentDivider = "_";
        ending = "Z";

        // Get all the times to process
        /**
         * Initialise times.
         */
        TreeMap<Generic_Time, TreeSet<Generic_Time>> timesForecast;
        timesForecast = getTimesForecastImages(
                indir0, dateComponentDelimeter, dateTimeDivider,
                timeComponentDivider, ending);

        double weight;
        double x;
        double y;
        long nrows;
        long ncols;
        double ndv;

        Grids_GridDouble g0;
        Grids_GridDouble g1;
        nrows = g.getNRows();
        ncols = g.getNCols();
        ndv = g.getNoDataValue();

        Generic_Time t;
        Generic_Time t0;
        Generic_Time t1;
        Generic_Date d = null;
        Generic_Date d0 = null;
        Generic_Date d1;
        long differenceInMinutes;

        double v;
        double v0;
        double v1;
        double vb;

        String name;
        Iterator<Generic_Time> ite;
        Iterator<Generic_Time> itet;
        TreeSet<Generic_Time> times;
        Grids_GridDouble b1KMGrid;
        String timen;

        String s;
        String name0 = "";

        File file;
        boolean writtenOut;
        writtenOut = true;

        gdir = gridf.createNewFile(gridf.getGeneratedGridDoubleDir());
        b1KMGrid = (Grids_GridDouble) f.create(gdir, g);
        ite = timesForecast.keySet().iterator();
        while (ite.hasNext()) {
            if (!writtenOut) {
                // Write out result
                name = name0 + d0.getYYYYMMDD();
                outdir1 = Files.getNestedTimeDirectory(outdir0, d);
                outputGrid(outdir1, name, b1KMGrid, ndvColor, colorMap);
                b1KMGrid = reinitGrid(b1KMGrid, g, f, gridf);
                writtenOut = true;
            }
            t = ite.next();
            d = t.getDate();
            s = t.getYYYYMMDD();
            name0 = s + "_ForecastFor_";
            times = timesForecast.get(t);
            itet = times.iterator();
            t0 = itet.next();
            d0 = t0.getDate();
            outdir1 = Files.getNestedTimeDirectory(outdir0, d);
            timen = Integer.toString((int) t0.differenceInHours(t));
            boolean initg0;
            initg0 = false;
            g0 = null;
            while (itet.hasNext()) {
                t1 = itet.next();
                d1 = t1.getDate();
                name = name0 + d0.getYYYYMMDD();
                file = new File(outdir1, name + ".asc");
                if (overwrite || !file.exists()) {
                    if (!initg0) {
                        g0 = getGridForecast(f, b1KMGrid, nrows, ncols, ndv,
                                gridf, indir0, gridsdir, t, timen, dateComponentDelimeter,
                                dateTimeDivider, timeComponentDivider, ending, cellsize, lp);
                        initg0 = true;
                    }
                    writtenOut = false;
                    timen = Integer.toString((int) t.differenceInHours(t1));
                    g1 = getGridForecast(f, b1KMGrid, nrows, ncols, ndv,
                            gridf, indir0, gridsdir, t, timen, dateComponentDelimeter,
                            dateTimeDivider, timeComponentDivider, ending, cellsize, lp);
                    differenceInMinutes = Math.abs(t1.differenceInMinutes(t0));
                    weight = differenceInMinutes / 60.0d;
//                if (weight > 1) {
//                    int debug = 1;
//                }
                    for (long row = 0; row < nrows; row++) {
                        y = b1KMGrid.getCellYDouble(row);
                        //y = b1KMGrid.getCellYDouble(row) + halfcellsize; // adding half a cellsize is in an attempt to prevent striping where images join.
                        for (long col = 0; col < ncols; col++) {
                            vb = b1KMGrid.getCell(row, col);
                            if (vb != ndv) {
                                x = b1KMGrid.getCellXDouble(col);
                                //x = b1KMGrid.getCellXDouble(col) + halfcellsize; // adding half a cellsize is in an attempt to prevent striping where images join.
                                v0 = g0.getCell(x, y);
                                v1 = g1.getCell(x, y);
                                if (v0 != ndv && v1 != ndv) {
                                    //System.out.println("Value at (x, y) (" + x + ", " + y + ")= " + v);
                                    if (v0 > 0 || v1 > 0) {
                                        v = (v0 + v1) * weight;
//                                        
//                                      if (v > 100) {
//                                          int debug = 1;
//                                      }
                                        if (v < 0) {
                                            int debug = 1;
                                        }
//                                        
                                        b1KMGrid.addToCell(row, col, v);
                                    }
                                } else {
                                    System.out.println("v0 != ndv && v1 != ndv fdsfddksfd");
                                    int debug = 1;
                                }
                            } else {
                                //System.out.println("Out of study area.");
                            }
                        }
                    }
                    // If the date has changed, then write out result and initialise a new one.
                    if (d0.compareTo(d1) != 0) {
                        // Write out result
                        name = name0 + d0.getYYYYMMDD();
                        outputGrid(outdir1, name, b1KMGrid, ndvColor, colorMap);
                        b1KMGrid = reinitGrid(b1KMGrid, g, f, gridf);
                        writtenOut = true;
                    }
                    ge.removeGrid(g0);
                    g0 = g1;
                }
                d0 = d1;
                t0 = t1;
            }
        }
        System.out.println("</" + methodName + ">");
    }

    /**
     * Initialise times.
     */
    public TreeSet<Generic_Time> getTimesObservationImages(File indir0,
            String dateComponentDelimeter, String dateTimeDivider,
            String timeComponentDivider, String ending) {
        TreeSet<Generic_Time> result;
        result = new TreeSet<>();
        File[] indirs0;
        File[] indirs1;
        File[] indirs2;
        File indir1;
        indirs0 = indir0.listFiles();
        String s;
        for (int i = 0; i < indirs0.length; i++) {
            s = indirs0[i].getName();
            indir1 = new File(indir0, s);
            indirs1 = indir1.listFiles();
            // initialise outdirs
            for (int j = 0; j < indirs1.length; j++) {
                indirs2 = indirs1[j].listFiles();
                for (int k = 0; k < indirs2.length; k++) {
                    Generic_Time t;
                    t = new Generic_Time(se, indirs2[k].getName(),
                            timeComponentDivider, dateTimeDivider,
                            timeComponentDivider);
                    result.add(t);
                    //System.out.println(t.toFormattedString0());
                }
            }
        }
        return result;
    }

    /**
     * Initialise times.
     */
    public TreeMap<Generic_Time, TreeSet<Generic_Time>> getTimesForecastImages(
            File indir0, String dateComponentDelimeter, String dateTimeDivider,
            String timeComponentDivider, String ending) {
        TreeMap<Generic_Time, TreeSet<Generic_Time>> result;
        result = new TreeMap<>();
        File[] indirs0;
        File[] indirs1;
        File[] indirs2;
        File indir1;
        File indir2;
        indirs0 = indir0.listFiles();
        String s;
        for (int i = 0; i < indirs0.length; i++) {
            s = indirs0[i].getName();
            indir1 = new File(indir0, s);
            indirs1 = indir1.listFiles();
            // initialise outdirs
            for (int j = 0; j < indirs1.length; j++) {
                indirs2 = indirs1[j].listFiles();
                for (int k = 0; k < indirs2.length; k++) {
                    Generic_Time t;
                    t = new Generic_Time(se, indirs2[k].getName(),
                            timeComponentDivider, dateTimeDivider,
                            timeComponentDivider);
                    for (int l = 0; l <= 36; l += 3) {
                        indir2 = new File(indirs2[k], "" + l);
                        if (indir2.exists()) {
                            Generic_Time t2;
                            t2 = new Generic_Time(t);
                            t2.addHours(l);
                            TreeSet<Generic_Time> ts;
                            if (result.containsKey(t)) {
                                ts = result.get(t);
                            } else {
                                ts = new TreeSet<>();
                                result.put(t, ts);
                            }
                            ts.add(t2);
                        }
                        //System.out.println(t.toFormattedString0());
                    }
                }
            }
        }
        return result;
    }

    /**
     * For processing observations which are generally at 15 minute intervals.
     *
     * @param colorMap
     * @param ndvColor
     * @param area
     * @param scale
     * @param layerName
     * @param cellsize
     * @param p
     * @param lp
     * @param r
     * @param f 1km masked factory
     * @param g 1km masked grid
     */
    public void processObservationImages(TreeMap<Double, Color> colorMap,
            Color ndvColor, String area, int scale, String layerName,
            BigDecimal cellsize,
            SARIC_MetOfficeParameters p,
            SARIC_MetOfficeLayerParameters lp,
            SARIC_MetOfficeCapabilitiesXMLDOMReader r,
            Grids_GridDoubleFactory f,
            Grids_GridDouble g) {
        String methodName;
        methodName = "processObservationImages(...)";
        System.out.println("<" + methodName + ">");
        System.out.println("Area " + area);
        // Initial declaration
        Grids_Files gridf;
        gridf = ge.getFiles();
        File gdir;
        String pathIn;
        String pathOut;
        File file;
        File outdir0;
        File outdir1;
        File indir0;
        File gridsdir;
        // Initial assignment
        //pathIn = "inspire/view/wmts0/" + area + "/" + layerName + "/EPSG_27700_";
        //pathIn = "inspire/view/wmtsall/" + area + "/" + layerName + "/EPSG_27700_";
        pathIn = "inspire/view/wmts/" + area + "/" + layerName + "/EPSG_27700_";
        //pathOut = "inspire/view/wmts0/" + area + "/" + layerName + "/EPSG_27700_";
        pathOut = "inspire/view/wmts/" + area + "/" + layerName + "/EPSG_27700_";
        System.out.println("scale " + scale);
        indir0 = new File(Files.getInputDataMetOfficeDataPointDir(), pathIn + scale);
        outdir0 = new File(Files.getOutputDataMetOfficeDataPointDir(), pathOut + scale);
        outdir0 = new File(outdir0, estimateName);
        gridsdir = new File(Files.getGeneratedDataGridsDir(), pathOut + scale);

        String dateComponentDelimeter;
        String dateTimeDivider;
        String timeComponentDivider;
        String ending;
        dateComponentDelimeter = "-";
        dateTimeDivider = "T";
        timeComponentDivider = "_";
        ending = "Z";

        /**
         * Initialise times.
         */
        TreeSet<Generic_Time> times;
        times = getTimesObservationImages(indir0, dateComponentDelimeter, dateTimeDivider,
                timeComponentDivider, ending);

        double weight;
        double x;
        double y;
        long nrows;
        long ncols;
        double ndv;
        Grids_GridDouble g0;
        Grids_GridDouble g1;
        nrows = g.getNRows();
        ncols = g.getNCols();
        ndv = g.getNoDataValue();
        Generic_Time t0;
        Generic_Time t1;
        Generic_Date d0;
        Generic_Date d1;
        long differenceInMinutes;
        double v;
        double v0;
        double v1;
        double vb;
        String name;
        Iterator<Generic_Time> itet;
        Grids_GridDouble b1KMGrid;

        gdir = gridf.createNewFile(gridf.getGeneratedGridDoubleDir());
        b1KMGrid = (Grids_GridDouble) f.create(gdir, g);
        itet = times.iterator();
        t0 = itet.next();
        d0 = t0.getDate();
        g0 = getGridObserved(f, b1KMGrid, nrows, ncols, ndv,
                gridf, indir0, gridsdir, t0, dateComponentDelimeter,
                dateTimeDivider, timeComponentDivider, ending, cellsize, lp);
        while (itet.hasNext()) {
            t1 = itet.next();
            d1 = t1.getDate();
            name = layerName;
            outdir1 = Files.getNestedTimeDirectory(outdir0, d0);
            file = new File(outdir1, name + ".asc");
            if (overwrite || !file.exists()) {
                g1 = getGridObserved(f, b1KMGrid, nrows, ncols, ndv,
                        gridf, indir0, gridsdir, t1, dateComponentDelimeter,
                        dateTimeDivider, timeComponentDivider, ending, cellsize, lp);
                differenceInMinutes = Math.abs(t0.differenceInMinutes(t1));
                weight = differenceInMinutes / 60.0d;
//                if (weight > 1) {
//                    int debug = 1;
//                }
                for (long row = 0; row < nrows; row++) {
                    y = b1KMGrid.getCellYDouble(row);
                    //y = b1KMGrid.getCellYDouble(row) + halfcellsize; // adding half a cellsize is in an attempt to prevent striping where images join.
                    for (long col = 0; col < ncols; col++) {
                        vb = b1KMGrid.getCell(row, col);
                        if (vb != ndv) {
                            x = b1KMGrid.getCellXDouble(col);
                            //x = b1KMGrid.getCellXDouble(col) + halfcellsize; // adding half a cellsize is in an attempt to prevent striping where images join.
                            v0 = g0.getCell(x, y);
                            v1 = g1.getCell(x, y);
                            if (v0 != ndv && v1 != ndv) {
                                //System.out.println("Value at (x, y) (" + x + ", " + y + ")= " + v);
                                if (v0 > 0 || v1 > 0) {
                                    v = (v0 + v1) * weight;
//                                        
//                                      if (v > 100) {
//                                          int debug = 1;
//                                      }
                                    if (v < 0) {
                                        int debug = 1;
                                    }
//                                        
                                    b1KMGrid.addToCell(row, col, v);
                                }
                            } else {
                                System.out.println("v0 != noDataValue && v1 != noDataValue fdsfdksfd");
                                int debug = 1;
                            }
                        } else {
                            //System.out.println("Out of study area.");
                        }
                    }
                }
                // If the date has changed, then write out result and initialise a new one.
                if (d0.compareTo(d1) != 0) {
                    // Write out result
                    name = layerName;
                    outdir1 = Files.getNestedTimeDirectory(outdir0, d0);
                    outputGrid(outdir1, name, b1KMGrid, ndvColor, colorMap);
                    b1KMGrid = reinitGrid(b1KMGrid, g, f, gridf);
                }
                ge.removeGrid(g0);
                g0 = g1;
            }
            d0 = d1;
            t0 = t1;
        }
        System.out.println("</" + methodName + ">");
    }

    /**
     *
     * @param g The grid to be reinitialised.
     * @param g0 The grid to reinitialise from.
     * @param f The factory to be used for reinitialisation.
     * @param gridf
     */
    private Grids_GridDouble reinitGrid(Grids_GridDouble g, Grids_GridDouble g0,
            Grids_GridDoubleFactory f, Grids_Files gridf) {
        g.env.removeGrid(g);
        File gdir;
        gdir = gridf.createNewFile(gridf.getGeneratedGridDoubleDir());
        g = (Grids_GridDouble) f.create(gdir, g0);
        return g;
    }

    // Output result grid
    private void outputGrid(File dir, String name, Grids_GridDouble g,
            Color ndvColor, TreeMap<Double, Color> colorMap) {
        File f;
        f = new File(dir, name + ".asc");
        ae.toAsciiFile(g, f);
        if (outputGreyscale) {
            f = new File(dir, name + ".png");
            ie.toGreyScaleImage(g, gp, f, "png");
        }
        f = getOuputGridColorFile(dir, name);
        ie.toColourImage(0, colorMap, ndvColor, g, f, "png");
        if (ouputLargeColorDubpication) {
            f = new File(dir, name + "Color8.png");
            ie.toColourImage(colorDubpication, colorMap, ndvColor, g, f, "png");
        }
    }

    private File getOuputGridColorFile(File dir, String name) {
        File f;
        f = new File(dir, name + "Color.png");
        return f;
    }

    int[] getRowCol(File infile, String indirname) {
        String rowCol;
        rowCol = infile.getName().split(indirname)[1];
        return getRowCol(rowCol);
    }

    int[] getRowCol(String rowCol) {
        int[] result;
        result = new int[2];
        String[] rowColSplit;
        rowColSplit = rowCol.split("_");
        result[0] = new Integer(rowColSplit[1]);
        result[1] = new Integer(rowColSplit[2].substring(0, rowColSplit[2].length() - 4));
        return result;
    }

//    /**
//     *
//     * @param indir
//     * @param gridDir
//     * @param cellsize
//     * @param tileBounds
//     * @param layerName
//     * @param rowColint This has length 2 the first element is the row and the
//     * second the column of the tile.
//     * @param hoome
//     * @return
//     */
//    public Grids_GridDouble getGrid(
//            File indir,
//            File gridDir,
//            BigDecimal cellsize,
//            Vector_Envelope2D tileBounds,
//            boolean hoome) {
//        try {
//            return getGrid(indir, gridDir, cellsize, tileBounds);
//        } catch (OutOfMemoryError e) {
//            if (hoome) {
//                env.clearMemoryReserve();
//                env.swapChunks(hoome);
//                return getGrid(indir, gridDir, cellsize, tileBounds, hoome);
//            } else {
//                throw e;
//            }
//        }
//    }
    /**
     *
     * @param f
     * @param g0
     * @param nrows
     * @param ncols
     * @param noDataValue
     * @param gridf
     * @param indir
     * @param gridDir
     * @param t
     * @param dateComponentDelimeter
     * @param dateTimeDivider
     * @param timeComponentDivider
     * @param ending
     * @param cellsize
     * @param lp
     * @return
     */
    public Grids_GridDouble getGridObserved(Grids_GridDoubleFactory f,
            Grids_GridDouble g0, long nrows, long ncols,
            double noDataValue,
            Grids_Files gridf,
            File indir, File gridDir, Generic_Time t, String dateComponentDelimeter,
            String dateTimeDivider, String timeComponentDivider, String ending,
            BigDecimal cellsize, SARIC_MetOfficeLayerParameters lp) {
        String methodName;
        methodName = "getGridObserved(File,Generic_Time,BigDecimal,Vector_Envelope2D)";
        Grids_GridDouble result;
        System.out.println("<Duplicate a1KMGrid>");
        File gdir;
        gdir = gridf.createNewFile(gridf.getGeneratedGridDoubleDir());
        result = (Grids_GridDouble) f.create(gdir, g0);

        String s;
        String YYYYMMDD;
        YYYYMMDD = t.getYYYYMMDD();
        File dir;
        dir = new File(indir, t.getYYYYMM());
        dir = new File(dir, YYYYMMDD);
        s = t.getYYYYMMDDHHMMSS(dateComponentDelimeter, dateTimeDivider,
                timeComponentDivider, ending);
        dir = new File(dir, s);

        File[] files;
        files = dir.listFiles();
        Vector_Envelope2D tileBounds;
        String rowCol;
        int[] rowColint;

        Grids_GridDouble g;
        double y;
        double x;
        double vb;
        double v;

        for (File file : files) {
            rowCol = file.getName().split(s)[1];
            rowColint = getRowCol(rowCol);
            tileBounds = lp.getTileBounds(rowColint[0], rowColint[1]);
            //boolean hoome = true;
            Image image = null;
            int width;
            int height;
            try {
                image = ImageIO.read(file);
            } catch (IOException io) {
                io.printStackTrace(System.err);
            }
            try {
                // Grab the pixels.
                width = image.getWidth(null);
                height = image.getHeight(null);
                Grids_Dimensions dimensions;
                dimensions = new Grids_Dimensions(tileBounds.XMin, tileBounds.XMax,
                        tileBounds.YMin, tileBounds.YMax, cellsize);
                //g = (Grids_GridDouble) gf.create(gridDir, height, width, dimensions);
                g = (Grids_GridDouble) f.create(gridDir, height, width, dimensions);
                int[] pixels = new int[width * height];
                PixelGrabber pg;
                pg = new PixelGrabber(image, 0, 0, width, height, pixels, 0, width);
                try {
                    pg.grabPixels();
                } catch (InterruptedException ex) {
                    ex.printStackTrace(System.err);
                }
                long row = height - 1;
                long col = 0;
                for (int i = 0; i < pixels.length; i++) {
                    if (col == width) {
                        col = 0;
                        row--;
                    }
                    v = getEstimateFromPixel(new Color(pixels[i]));
                    g.setCell(row, col, v);
                    col++;
                }
                // Describe result
                //System.out.println(result.toString(0, HandleOutOfMemoryError));
                // long nrows = b1KMGrid.getNRows();
                //  long ncols = b1KMGrid.getNCols();
                for (row = 0; row < nrows; row++) {
                    y = result.getCellYDouble(row);
                    //y = result.getCellYDouble(row) + halfcellsize; // adding half a cellsize is in an attempt to prevent striping where images join.
                    for (col = 0; col < ncols; col++) {
                        vb = result.getCell(row, col);
                        if (vb != noDataValue) {
                            x = result.getCellXDouble(col);
                            //x = result.getCellXDouble(col) + halfcellsize; // adding half a cellsize is in an attempt to prevent striping where images join.
                            v = g.getCell(x, y);
                            if (v != noDataValue) {
                                //System.out.println("Value at (x, y) (" + x + ", " + y + ")= " + v);

//                                double test;
//                                test = result.getCell(row, col); //row 21
//                                if (test != noDataValue && test != 0.0d) {
//                                    //if (row != 21 && row != 22) {
//                                    int debug = 1;
//                                    //}
//                                }
                                //result.addToCell(row, col, v);
                                result.setCell(row, col, v);
                            }
                        } else {
                            //System.out.println("Out of study area.");
                        }
                    }
                }
                ge.removeGrid(g);
            } catch (NullPointerException e) {
                System.out.println("File " + file.toString() + " exists in "
                        + this.getClass().getName() + "." + methodName
                        + ", but is empty or there is some other problem with it "
                        + "being loaded as an image, returning null.");
            }
        }
//        result.getStats().update();
//        System.out.println("Result " + result.getStats().toString());
        return result;
    }

    /**
     *
     * @param f
     * @param g0
     * @param nrows
     * @param ncols
     * @param ndv
     * @param gridf
     * @param indir
     * @param gridDir
     * @param t
     * @param dateComponentDelimeter
     * @param dateTimeDivider
     * @param timeComponentDivider
     * @param ending
     * @param cellsize
     * @param lp
     * @return
     */
    public Grids_GridDouble getGridForecast(Grids_GridDoubleFactory f,
            Grids_GridDouble g0, long nrows, long ncols,
            double ndv,
            Grids_Files gridf,
            File indir, File gridDir, Generic_Time t, String timen,
            String dateComponentDelimeter,
            String dateTimeDivider, String timeComponentDivider, String ending,
            BigDecimal cellsize, SARIC_MetOfficeLayerParameters lp) {
        String methodName;
        methodName = "getGridForecast(File,Generic_Time,BigDecimal,Vector_Envelope2D)";
        Grids_GridDouble result;
        System.out.println("<Duplicate a1KMGrid>");
        File gdir;
        gdir = gridf.createNewFile(gridf.getGeneratedGridDoubleDir());
        result = (Grids_GridDouble) f.create(gdir, g0);

        String s;
        String YYYYMMDD;
        YYYYMMDD = t.getYYYYMMDD();
        File dir;
        dir = new File(indir, t.getYYYYMM());
        dir = new File(dir, YYYYMMDD);
        s = t.getYYYYMMDDHHMMSS(dateComponentDelimeter, dateTimeDivider,
                timeComponentDivider, ending);
        dir = new File(dir, s);
        dir = new File(dir, timen);

        File[] files;
        files = dir.listFiles();
        Vector_Envelope2D tileBounds;
        String rowCol;
        int[] rowColint;

        Grids_GridDouble g;
        double y;
        double x;
        double vb;
        double v;

        if (files == null) {
            int debug = 1;
        }

        for (File file : files) {
            rowCol = file.getName().split(s)[1];
            rowColint = getRowCol(rowCol);
            tileBounds = lp.getTileBounds(rowColint[0], rowColint[1]);
            //boolean hoome = true;
            Image image = null;
            int width;
            int height;
            try {
                image = ImageIO.read(file);
            } catch (IOException io) {
                io.printStackTrace(System.err);
            }
            try {
                // Grab the pixels.
                width = image.getWidth(null);
                height = image.getHeight(null);
                Grids_Dimensions dimensions;
                dimensions = new Grids_Dimensions(tileBounds.XMin, tileBounds.XMax,
                        tileBounds.YMin, tileBounds.YMax, cellsize);
                g = (Grids_GridDouble) f.create(gridDir, height, width, dimensions);
                //g = (Grids_GridDouble) gf.create(gridDir, height, width, dimensions);
                int[] pixels = new int[width * height];
                PixelGrabber pg;
                pg = new PixelGrabber(image, 0, 0, width, height, pixels, 0, width);
                try {
                    pg.grabPixels();
                } catch (InterruptedException ex) {
                    ex.printStackTrace(System.err);
                }
                long row = height - 1;
                long col = 0;

//                boolean test = true;
//                long r0 = 0;
//                long c0 = 0;
                for (int i = 0; i < pixels.length; i++) {
                    if (col == width) {
                        col = 0;
                        row--;
                    }
                    v = getEstimateFromPixel(new Color(pixels[i]));

//                    if (v > 0 && test) {
//                        int debug = 1;
//                        r0 = row;
//                        c0 = col;
//                        test = false;
//                    }
                    g.setCell(row, col, v);
                    col++;
                }

                g.getStats().update();
                System.out.println(g.getStats().toString());
                System.out.println(g.toString());

                for (row = 0; row < nrows; row++) {
                    y = result.getCellYDouble(row);
                    //y = result.getCellYDouble(row) + halfcellsize; // adding half a cellsize is in an attempt to prevent striping where images join.
                    for (col = 0; col < ncols; col++) {
                        vb = result.getCell(row, col);
                        if (vb != ndv) {
                            x = result.getCellXDouble(col);
                            //x = result.getCellXDouble(col) + halfcellsize; // adding half a cellsize is in an attempt to prevent striping where images join.
                            v = g.getCell(x, y);
                            if (v != ndv) {
                                //System.out.println("Value at (x, y) (" + x + ", " + y + ")= " + v);

//                                double test;
//                                test = result.getCell(row, col); //row 21
//                                if (test != noDataValue && test != 0.0d) {
//                                    //if (row != 21 && row != 22) {
//                                    int debug = 1;
//                                    //}
//                                }
                                //result.addToCell(row, col, v);
                                result.setCell(row, col, v);
                            }
                        } else {
                            //System.out.println("Out of study area.");
                        }
                    }
                }
                ge.removeGrid(g);
            } catch (NullPointerException e) {
                System.out.println("File " + file.toString() + " exists in "
                        + this.getClass().getName() + "." + methodName
                        + ", but is empty or there is some other problem with it "
                        + "being loaded as an image, returning null.");
            }
        }
//        result.getStats().update();
//        System.out.println("Result " + result.getStats().toString());
        return result;
    }

    public double getEstimateFromPixel(Color pixel) {
        // Process the pixels.
//      Colour: ColourHex: Official Range: Mid range value used in mm/hr
//      Blue: #0000FE: 0.01 - 0.5: 0.25 mm/hr
//      Light Blue: #3265FE: 0.5 - 1: 0.75
//      Muddy Green: #7F7F00: 1 - 2: 1.5
//      Yellow: #FECB00: 2 - 4: 3
//      Orange: #FE9800: 4 - 8: 6
//      Red: #FE0000: 8 - 16: 12
//      Pink: #FE00FE: 16 - 32: 24
//      Pale Blue: #E5FEFE: 32+: 48
        switch (estimateType) {
            case -1:
                if (pixel.equals(Blue)) {
                    return 0.01d;
                } else if (pixel.equals(LightBlue)) {
                    return 0.5d;
                } else if (pixel.equals(MuddyGreen)) {
                    return 1d;
                } else if (pixel.equals(Yellow)) {
                    return 2d;
                } else if (pixel.equals(Orange)) {
                    return 4d;
                } else if (pixel.equals(Red)) {
                    return 8d;
                } else if (pixel.equals(Pink)) {
                    return 16d;
                } else if (pixel.equals(PaleBlue)) {
                    return 32d;
                } else if (pixel.equals(Color.BLACK)) {
                    return 0.0d;
                } else {
                    return 0.0d;
                }
            case 0:
                if (pixel.equals(Blue)) {
                    return 0.25d;
                } else if (pixel.equals(LightBlue)) {
                    return 0.75d;
                } else if (pixel.equals(MuddyGreen)) {
                    return 1.5d;
                } else if (pixel.equals(Yellow)) {
                    return 3d;
                } else if (pixel.equals(Orange)) {
                    return 6d;
                } else if (pixel.equals(Red)) {
                    return 12d;
                } else if (pixel.equals(Pink)) {
                    return 24d;
                } else if (pixel.equals(PaleBlue)) {
                    return 48d;
                } else if (pixel.equals(Color.BLACK)) {
                    return 0.0d;
                } else {
                    return 0.0d;
                }
            default:
                if (pixel.equals(Blue)) {
                    return 0.5d;
                } else if (pixel.equals(LightBlue)) {
                    return 1d;
                } else if (pixel.equals(MuddyGreen)) {
                    return 2d;
                } else if (pixel.equals(Yellow)) {
                    return 4d;
                } else if (pixel.equals(Orange)) {
                    return 8d;
                } else if (pixel.equals(Red)) {
                    return 16d;
                } else if (pixel.equals(Pink)) {
                    return 32d;
                } else if (pixel.equals(PaleBlue)) {
                    return 64d;
                } else if (pixel.equals(Color.BLACK)) {
                    return 0.0d;
                } else {
                    return 0.0d;
                }
        }
    }

    public double getEstimateFromPoint(SARIC_SiteForecastRecord r) {
        switch (estimateType) {
            case 0:
                return getEstimateMid(r);
            case -1:
                return getEstimateLow(r);
            default:
                return getEstimateHigh(r);
        }
    }

    /**
     * Significant weather as a code: NA Not available 0 Clear night 1 Sunny day
     * 2 Partly cloudy (night) 3 Partly cloudy (day) 4 Not used 5 Mist 6 Fog 7
     * Cloudy 8 Overcast 9 Light rain shower (night) 10 Light rain shower (day)
     * 11 Drizzle 12 Light rain 13 Heavy rain shower (night) 14 Heavy rain
     * shower (day) 15 Heavy rain 16 Sleet shower (night) 17 Sleet shower (day)
     * 18 Sleet 19 Hail shower (night) 20 Hail shower (day) 21 Hail 22 Light
     * snow shower (night) 23 Light snow shower (day) 24 Light snow 25 Heavy
     * snow shower (night) 26 Heavy snow shower (day) 27 Heavy snow 28 Thunder
     * shower (night) 29 Thunder shower (day) 30 Thunder
     *
     * "Drizzle" (or "Very Light") equates to rates of 0.01 to 0.1mm/hr "Light"
     * equates to rates of 0.1 to 1mm/hr "Heavy" equates to rates of >1mm/hr
     * https://groups.google.com/forum/#!searchin/metoffice-datapoint/code$20rain%7Csort:relevance/metoffice-datapoint/UZkLK45ZXWE/xvUZ7JZPbBQJ
     *
     * @param r
     * @return
     */
    protected double getEstimateMid(SARIC_SiteForecastRecord r) {
        int weatherType;
        weatherType = r.getWeatherType();
        int precipitationProbability;
        precipitationProbability = r.getPrecipitationProbability();
        double v;
        if ((weatherType >= 9 && weatherType <= 12) || (weatherType >= 19 && weatherType <= 24 && weatherType != 21)) {
            v = 0.55d * precipitationProbability / 100.0d;
        } else if ((weatherType >= 16 && weatherType <= 18) || weatherType == 21) {
            v = 0.055d * precipitationProbability / 100.0d;
        } else if ((weatherType >= 13 && weatherType <= 15) || (weatherType >= 25 && weatherType <= 30)) {
            v = 5.5d * precipitationProbability / 100.0d;
        } else {
            v = 0.0d;
        }

        if (v < 0) {
            int debug = 1;
        }

        return v;
    }

    /**
     * Significant weather as a code: NA Not available 0 Clear night 1 Sunny day
     * 2 Partly cloudy (night) 3 Partly cloudy (day) 4 Not used 5 Mist 6 Fog 7
     * Cloudy 8 Overcast 9 Light rain shower (night) 10 Light rain shower (day)
     * 11 Drizzle 12 Light rain 13 Heavy rain shower (night) 14 Heavy rain
     * shower (day) 15 Heavy rain 16 Sleet shower (night) 17 Sleet shower (day)
     * 18 Sleet 19 Hail shower (night) 20 Hail shower (day) 21 Hail 22 Light
     * snow shower (night) 23 Light snow shower (day) 24 Light snow 25 Heavy
     * snow shower (night) 26 Heavy snow shower (day) 27 Heavy snow 28 Thunder
     * shower (night) 29 Thunder shower (day) 30 Thunder
     *
     * "Drizzle" (or "Very Light") equates to rates of 0.01 to 0.1mm/hr "Light"
     * equates to rates of 0.1 to 1mm/hr "Heavy" equates to rates of >1mm/hr
     * https://groups.google.com/forum/#!searchin/metoffice-datapoint/code$20rain%7Csort:relevance/metoffice-datapoint/UZkLK45ZXWE/xvUZ7JZPbBQJ
     *
     * @param r
     * @return
     */
    protected double getEstimateLow(SARIC_SiteForecastRecord r) {
        int weatherType;
        weatherType = r.getWeatherType();
        int precipitationProbability;
        precipitationProbability = r.getPrecipitationProbability();
        double v;
        if ((weatherType >= 9 && weatherType <= 12) || (weatherType >= 19 && weatherType <= 24 && weatherType != 21)) {
            v = 0.1d * precipitationProbability / 100.0d;
        } else if ((weatherType >= 16 && weatherType <= 18) || weatherType == 21) {
            v = 0.01d * precipitationProbability / 100.0d;
        } else if ((weatherType >= 13 && weatherType <= 15) || (weatherType >= 25 && weatherType <= 30)) {
            v = 1.0d * precipitationProbability / 100.0d;
        } else {
            v = 0.0d;
        }

        if (v < 0) {
            int debug = 1;
        }

        return v;
    }

    /**
     * Significant weather as a code: NA Not available 0 Clear night 1 Sunny day
     * 2 Partly cloudy (night) 3 Partly cloudy (day) 4 Not used 5 Mist 6 Fog 7
     * Cloudy 8 Overcast 9 Light rain shower (night) 10 Light rain shower (day)
     * 11 Drizzle 12 Light rain 13 Heavy rain shower (night) 14 Heavy rain
     * shower (day) 15 Heavy rain 16 Sleet shower (night) 17 Sleet shower (day)
     * 18 Sleet 19 Hail shower (night) 20 Hail shower (day) 21 Hail 22 Light
     * snow shower (night) 23 Light snow shower (day) 24 Light snow 25 Heavy
     * snow shower (night) 26 Heavy snow shower (day) 27 Heavy snow 28 Thunder
     * shower (night) 29 Thunder shower (day) 30 Thunder
     *
     * "Drizzle" (or "Very Light") equates to rates of 0.01 to 0.1mm/hr "Light"
     * equates to rates of 0.1 to 1mm/hr "Heavy" equates to rates of >1mm/hr
     * https://groups.google.com/forum/#!searchin/metoffice-datapoint/code$20rain%7Csort:relevance/metoffice-datapoint/UZkLK45ZXWE/xvUZ7JZPbBQJ
     *
     * @param r
     * @return
     */
    protected double getEstimateHigh(SARIC_SiteForecastRecord r) {
        int weatherType;
        weatherType = r.getWeatherType();
        int precipitationProbability;
        precipitationProbability = r.getPrecipitationProbability();
        double v;
        if ((weatherType >= 9 && weatherType <= 12) || (weatherType >= 19 && weatherType <= 24 && weatherType != 21)) {
            v = 1.0d * precipitationProbability / 100.0d;
        } else if ((weatherType >= 16 && weatherType <= 18) || weatherType == 21) {
            v = 0.1d * precipitationProbability / 100.0d;
        } else if ((weatherType >= 13 && weatherType <= 15) || (weatherType >= 25 && weatherType <= 30)) {
            v = 10.0d * precipitationProbability / 100.0d;
        } else {
            v = 0.0d;
        }

        if (v < 0) {
            int debug = 1;
        }

        return v;
    }

}
