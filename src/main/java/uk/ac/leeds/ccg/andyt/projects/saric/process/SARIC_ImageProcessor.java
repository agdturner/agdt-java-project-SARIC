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
import uk.ac.leeds.ccg.andyt.grids.core.grid.stats.Grids_AbstractGridNumberStats;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridDouble;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridDoubleFactory;
import uk.ac.leeds.ccg.andyt.grids.core.grid.stats.Grids_GridDoubleStatsNotUpdated;
import uk.ac.leeds.ccg.andyt.grids.io.Grids_ESRIAsciiGridExporter;
import uk.ac.leeds.ccg.andyt.grids.io.Grids_Files;
import uk.ac.leeds.ccg.andyt.grids.io.Grids_ImageExporter;
import uk.ac.leeds.ccg.andyt.grids.process.Grids_Processor;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Environment;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Object;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Strings;
import uk.ac.leeds.ccg.andyt.projects.saric.data.catchment.SARIC_Teifi;
import uk.ac.leeds.ccg.andyt.projects.saric.data.catchment.SARIC_Wissey;
import uk.ac.leeds.ccg.andyt.projects.saric.data.metoffice.datapoint.SARIC_MetOfficeCapabilitiesXMLDOMReader;
import uk.ac.leeds.ccg.andyt.projects.saric.data.metoffice.datapoint.SARIC_MetOfficeLayerParameters;
import uk.ac.leeds.ccg.andyt.projects.saric.data.metoffice.datapoint.SARIC_MetOfficeParameters;
import uk.ac.leeds.ccg.andyt.projects.saric.data.metoffice.datapoint.site.SARIC_MetOfficeSiteXMLSAXHandler;
import uk.ac.leeds.ccg.andyt.projects.saric.data.metoffice.datapoint.site.SARIC_Site;
import uk.ac.leeds.ccg.andyt.projects.saric.data.metoffice.datapoint.site.SARIC_SiteForecastRecord;
import uk.ac.leeds.ccg.andyt.projects.saric.io.SARIC_Files;
import uk.ac.leeds.ccg.andyt.generic.utilities.time.Generic_Date;
import uk.ac.leeds.ccg.andyt.generic.utilities.time.Generic_Time;
import uk.ac.leeds.ccg.andyt.generic.utilities.time.Generic_YearMonth;
import uk.ac.leeds.ccg.andyt.projects.saric.visualisation.SARIC_Colour;
import uk.ac.leeds.ccg.andyt.vector.geometry.Vector_Envelope2D;

/**
 *
 * @author geoagdt
 */
public class SARIC_ImageProcessor extends SARIC_Object implements Runnable {

    /**
     * For convenience
     */
    SARIC_Files Files;
    SARIC_Strings Strings;
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
        this.se = se;
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
        Files = se.getFiles();
        Strings = se.getStrings();
        ge = se.getGrids_Env();
        ae = new Grids_ESRIAsciiGridExporter(ge);
        ie = new Grids_ImageExporter(ge);
        gp = ge.getProcessor();
        //init_gf();
    }

//    private void init_gf() {
//        gf = new Grids_GridDoubleFactory(ge, gp.GridChunkDoubleFactory,
//                gp.DefaultGridChunkDoubleFactory, -Double.MAX_VALUE,
//                256, 256, new Grids_Dimensions(256, 256),
//                new Grids_GridDoubleStatsNotUpdated(ge));
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

        if (doNonTiledFcs) {
            //C:\Users\geoagdt\src\projects\saric\data\input\MetOffice\DataPoint\val\wxfcs\all\xml\site\3hourly\2017-09-04-11
            // Declaration part 1
            /**
             * dates is for storing a set of dates that will be processed. This
             * is initialised in a manual way currently below, but it could also
             * be initialised by looking at what data are stored in a directory.
             */
            Object[] nearestForecastsSitesGridAndFactory;
            String areaName;
            if (doWissey) {
                areaName = Strings.getS_Wissey();
                SARIC_Wissey sw;
                sw = new SARIC_Wissey(se);
                sites = sw.getForecastsSitesInStudyArea(Strings.getS_3hourly());
                nearestForecastsSitesGridAndFactory = sw.getNearestForecastsSitesGrid(sites);
                nonTiledForecastsForArea(areaName, sites,
                        nearestForecastsSitesGridAndFactory,
                        colorMap);
            }
            if (doTeifi) {
                areaName = Strings.getS_Teifi();
                SARIC_Teifi st;
                st = new SARIC_Teifi(se);
                sites = st.getForecastsSitesInStudyArea(Strings.getS_3hourly());
                nearestForecastsSitesGridAndFactory = st.getNearestForecastsSitesGrid(sites);
                nonTiledForecastsForArea(areaName, sites,
                        nearestForecastsSitesGridAndFactory,
                        colorMap);
            }
        }
        if (doTileFromWMTSService) {
            // Initial declaration
            File inspireWMTSCapabilities;
            SARIC_MetOfficeParameters p;
            SARIC_MetOfficeCapabilitiesXMLDOMReader r;
            String area;
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
            p = new SARIC_MetOfficeParameters();
            r = new SARIC_MetOfficeCapabilitiesXMLDOMReader(se, inspireWMTSCapabilities);
            tileMatrixSet = "EPSG:27700"; // British National Grid

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

            // Forecasts
            Grids_GridDouble g;
            Grids_GridDoubleFactory f;
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
                        area = Strings.getS_Wissey();
                        f = (Grids_GridDoubleFactory) sw1KMGridMaskedToCatchment[1];
                        g = (Grids_GridDouble) sw1KMGridMaskedToCatchment[0];
                        System.out.println(g.toString());
                        processForecasts2(colorMap, ndvColor, area,
                                scale, layerName, cellsize, p, lp, r, f, g);
                    }
                    if (doTeifi) {
                        area = Strings.getS_Teifi();
                        f = (Grids_GridDoubleFactory) st1KMGridMaskedToCatchment[1];
                        g = (Grids_GridDouble) st1KMGridMaskedToCatchment[0];
                        processForecasts2(colorMap, ndvColor, area,
                                scale, layerName, cellsize, p, lp, r, f, g);
                    }
                }
            }
            // Observations
            if (doObservationsTileFromWMTSService) {
                layerName = Strings.getS_RADAR_UK_Composite_Highres();
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
                        area = Strings.getS_Wissey();
                        f = (Grids_GridDoubleFactory) sw1KMGridMaskedToCatchment[1];
                        g = (Grids_GridDouble) sw1KMGridMaskedToCatchment[0];
                        processObservations(colorMap, ndvColor, area, scale,
                                layerName, cellsize, p, lp, r, f, g);
                    }
                    if (doTeifi) {
                        area = Strings.getS_Teifi();
                        f = (Grids_GridDoubleFactory) st1KMGridMaskedToCatchment[1];
                        g = (Grids_GridDouble) st1KMGridMaskedToCatchment[0];
                        processObservations(colorMap, ndvColor, area, scale,
                                layerName, cellsize, p, lp, r, f, g);
                    }
                }
            }
        }
    }

    /**
     *
     * @param areaName
     * @param sites
     * @param nearestForecastsSitesGridAndFactory
     * @param colorMap
     */
    private void nonTiledForecastsForArea(String areaName,
            HashSet<SARIC_Site> sites,
            Object[] nearestForecastsSitesGridAndFactory,
            TreeMap<Double, Color> colorMap) {
        Grids_Files gridf;
        gridf = ge.getFiles();
        File gdir;
        String dataType;
        String path;
        File indir0;
        File indir1;
        File indir2;
        File outdir0;
        File outdir1;
        String name;
        // Initialisation part 1
        dataType = Strings.getS_xml();
        path = Files.getValDataTypePath(dataType, Strings.getS_wxfcs());
        indir0 = new File(Files.getInputDataMetOfficeDataPointDir(), path);
        System.out.println(indir0);
        indir0 = new File(indir0, Strings.getS_site() + "0");
        /**
         * There is no need to run for daily, it is just for the same time as
         * the 3hourly, but gives lower temporal resolution and we want high
         * temporal resolution.
         */
        indir0 = new File(indir0, Strings.getS_3hourly());
        outdir0 = new File(Files.getOutputDataMetOfficeDataPointDir(), path);
        outdir0 = new File(outdir0, areaName);
        Generic_Date date;
        // Declaration part 2
        Generic_Date date1;
        TreeSet<Generic_Date> dates;
        Grids_GridDouble nearestForecastsSitesGrid;
        //double noDataValue1;
        HashMap<SARIC_Site, HashMap<Generic_Time, SARIC_SiteForecastRecord>> forecasts;
        long nrows;
        long ncols;
        nearestForecastsSitesGrid = (Grids_GridDouble) nearestForecastsSitesGridAndFactory[0];
        Grids_GridDoubleFactory fac = (Grids_GridDoubleFactory) nearestForecastsSitesGridAndFactory[1];
        //noDataValue1 = nearestForecastsSitesGrid.getNoDataValue();
        //gf.setNoDataValue(noDataValue1);
        nrows = nearestForecastsSitesGrid.getNRows();
        ncols = nearestForecastsSitesGrid.getNCols();
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
                    outdir1 = new File(outdir1, date + "-00"); // We could iterate through all of these.
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
                        File f;
                        if (indir1.exists()) {
                            forecasts = new HashMap<>();
                            gdir = gridf.createNewFile(gridf.getGeneratedGridDoubleDir());
                            //forecastsForTime2 = (Grids_GridDouble) gf.create(gdir, nearestForecastsSitesGrid);
                            forecastsForTime2 = (Grids_GridDouble) fac.create(gdir, nearestForecastsSitesGrid);
                            name = date + "-00" + "_ForecastFor_" + date1.getYYYYMMDD();
                            f = new File(outdir1, name + ".asc");
                            if (f.exists()) {
                                System.out.println("Output " + f + " already exists!!!");
                            } else {
                                if (!outdir1.exists()) {
                                    outdir1.mkdirs();
                                }
                                double estimate;
                                double noDataValue = forecastsForTime2.getNoDataValue();
                                double v;

                                Iterator<SARIC_Site> ite;
                                ite = sites.iterator();
                                SARIC_Site site;
                                int siteID;
                                while (ite.hasNext()) {
                                    site = ite.next();
                                    siteID = site.getId();
                                    indir2 = new File(indir1, "" + siteID);
                                    System.out.println("indir2 " + indir2);
                                    String dirname;

                                    dirname = indir2.list()[0]; //Sometimes data are missing here!
                                    //System.out.println("dirname " + dirname);
                                    indir2 = new File(indir2, dirname);
                                    String filename;
                                    filename = siteID + Strings.getS_3hourly() + Strings.symbol_dot + dataType;
                                    f = new File(indir2, filename);
                                    SARIC_MetOfficeSiteXMLSAXHandler h;
                                    h = new SARIC_MetOfficeSiteXMLSAXHandler(se, f);
                                    HashMap<Generic_Time, SARIC_SiteForecastRecord> forecastsForTime;
                                    forecastsForTime = h.parse();
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
                                            switch (estimateType) {
                                                case 0:
                                                    estimate += getEstimateMid(forecastsForTime.get(t));
                                                    break;
                                                case -1:
                                                    estimate += getEstimateLow(forecastsForTime.get(t));
                                                    break;
                                                default:
                                                    estimate += getEstimateHigh(forecastsForTime.get(t));
                                                    break;
                                            }
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
                                    System.out.println("noDataValue " + noDataValue);
                                    for (long row = 0; row < nrows; row++) {
                                        for (long col = 0; col < ncols; col++) {
                                            v = nearestForecastsSitesGrid.getCell(row, col);
                                            if (v != noDataValue) {
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
    }

    /**
     * For processing forecasts which are generally at 3 hourly intervals.
     *
     * @param colorMap
     * @param noDataValueColor
     * @param area
     * @param scale
     * @param layerName
     * @param cellsize
     * @param p
     * @param lp
     * @param r
     * @param a1KMGrid
     * @param a1KMGridMaskedToCatchment
     */
    public void processForecasts(
            TreeMap<Double, Color> colorMap,
            Color noDataValueColor,
            String area,
            int scale,
            String layerName,
            BigDecimal cellsize,
            SARIC_MetOfficeParameters p,
            SARIC_MetOfficeLayerParameters lp,
            SARIC_MetOfficeCapabilitiesXMLDOMReader r,
            Object[] a1KMGrid,
            Object[] a1KMGridMaskedToCatchment) {
        String methodName;
        methodName = "processForecasts(...)";
        System.out.println("<" + methodName + ">");
        // Initial declaration
        Grids_Files gridf;
        gridf = ge.getFiles();
        File gdir;
        /**
         * ymDates, ym, dates and ite0 are for organising the order of
         * processing.
         */
        TreeMap<Generic_YearMonth, TreeSet<Generic_Date>> ymDates;
        Generic_YearMonth ym;
        TreeSet<Generic_Date> dates;
        Iterator<Generic_YearMonth> ite0;
        /**
         *
         */
        String pathin;
        String pathout;
        String s;
        File outdir0;
        File outdir1;
        File outdir2;
        File indir0;
        File indir1;
        File indir2;
        File indir3;
        File[] indirs0;
        File[] indirs1;
        File gridsdir0;
        File gridsdir1;
        File gridsdir2;
        File gridsdir3;
        File gridsdir4;
        // Initial assignment
        System.out.println("Area " + area);
//        pathin = "inspire/view/wmts0/" + area + "/" + layerName + "/EPSG_27700_";
        pathin = "inspire/view/wmtsall/" + area + "/" + layerName + "/EPSG_27700_";
        pathout = "inspire/view/wmts0/" + area + "/" + estimateName + "/" + layerName + "/EPSG_27700_";
        System.out.println("scale " + scale);
        indir0 = new File(Files.getInputDataMetOfficeDataPointDir(), pathin + scale);
        outdir0 = new File(Files.getOutputDataMetOfficeDataPointDir(), pathout + scale);
        gridsdir0 = new File(Files.getGeneratedDataGridsDir(), pathout + scale);
        ymDates = new TreeMap<>();
        indirs0 = indir0.listFiles();
        for (int i = 0; i < indirs0.length; i++) {
            s = indirs0[i].getName();
            ym = new Generic_YearMonth(se, s);
            dates = new TreeSet<>();
            ymDates.put(ym, dates);
            indir1 = new File(indir0, s);
            indirs1 = indir1.listFiles();
            // initialise outdirs
            for (int j = 0; j < indirs1.length; j++) {
                dates.add(new Generic_Date(se, indirs1[j].getName().split("T")[0]));
            }
        }

        boolean hoome = true;
        Vector_Envelope2D tileBounds;
        double weight;
        weight = 3d; // These are 3 hourly forecasts.
        String indirname2;
        File[] infiles;
        String rowCol;
//        int row;
//        int col;
        BigDecimal x;
        BigDecimal y;
        BigDecimal halfcellsize;
        halfcellsize = cellsize.divide(BigDecimal.valueOf(2L));

        File outascii;
        File outpng;
        File outpng2;
        File outpng3;
        //File outtxt;
        Grids_GridDouble a1KMGridMaskedToCatchmentGrid;
        a1KMGridMaskedToCatchmentGrid = (Grids_GridDouble) a1KMGridMaskedToCatchment[0];
        Generic_Date date0;
        Generic_Date date1;
        Generic_Date tomorrow;
        Generic_Date dayAfterTomorrow;

        Generic_Time time0;
        Generic_Time time1;

        String name;
        String name0;
        String name1;

        TreeMap<Generic_Date, HashMap<String, Grids_GridDouble>> grids0;
        HashMap<Generic_Date, HashMap<String, Double>> counts0;
        HashMap<String, Grids_GridDouble> grids1;
        HashMap<String, Double> counts1;

        Grids_AbstractGridNumberStats gs;
        double max;
        double min;
        double scaleFactor;

        Iterator<Generic_Date> ite1;

        ite0 = ymDates.keySet().iterator();
        while (ite0.hasNext()) {
            ym = ite0.next();
            s = ym.getYYYYMM();
            dates = ymDates.get(ym);
            System.out.println(s);
            outdir1 = new File(outdir0, s);
            indir1 = new File(indir0, s);
            gridsdir1 = new File(gridsdir0, s);
            ite1 = dates.iterator();
            while (ite1.hasNext()) {
                date0 = ite1.next();
                s = date0.getYYYYMMDD();
                /**
                 * This forecast is for the next 36 hours, so for 3AM and 9AM
                 * forecasts there will be output for today and tomorrow. For a
                 * 3PM and 9PM forecast there will be a forecast for today,
                 * tomorrow and the day after.
                 */
                grids0 = new TreeMap<>();
                counts0 = new HashMap<>();
                tomorrow = new Generic_Date(date0);
                tomorrow.addDays(1);
                dayAfterTomorrow = new Generic_Date(tomorrow);
                dayAfterTomorrow.addDays(1);
                outdir2 = new File(outdir1, s);
                indir2 = new File(indir1, s);
                gridsdir2 = new File(gridsdir1, s);
                name0 = s + "_ForecastFor_";
                outascii = new File(outdir2, name0 + date0.getYYYYMMDD() + ".asc");
                int[] rowColint;
                if (!overwrite && outascii.exists()) {
                    System.out.println("Not computing for " + date0.getYYYYMMDD() + " output already exists. To recompute delete or rename output.");
                } else {
                    outdir2.mkdirs();
                    indirs0 = indir2.listFiles();
                    // Aggregate forecasts for each tile.
                    if (indirs0 != null) {
                        for (int j = 0; j < indirs0.length; j++) {
                            if (!indirs0[j].isDirectory()) {
                                System.out.println(this.getClass().getName() + "." + methodName + ": "
                                        + "Input directory given by " + indirs0[j] + " is not a directory.");
                            } else {
                                indirname2 = indirs0[j].getName();
                                time0 = new Generic_Time(se, indirname2,
                                        se.getStrings().symbol_minus,
                                        se.getStrings().s_T,
                                        se.getStrings().symbol_underscore);
                                for (int k = 0; k <= 36; k += 3) {
                                    time1 = new Generic_Time(time0);
                                    time1.addHours(k);
                                    indir3 = new File(indirs0[j], "" + k);
                                    gridsdir3 = new File(gridsdir2, indirname2 + k);
                                    date1 = time1.getDate();
                                    System.out.println(date1);
                                    if (grids0.containsKey(date1)) {
                                        /**
                                         * Set the map for storing the result.
                                         */
                                        grids1 = grids0.get(date1);
                                        counts1 = counts0.get(date1);
                                    } else {
                                        /**
                                         * Initialise a map to store the result.
                                         */
                                        grids1 = new HashMap<>();
                                        grids0.put(date1, grids1);
                                        counts1 = new HashMap<>();
                                        counts0.put(date1, counts1);
                                    }
                                    if (indir3.exists()) {
                                        infiles = indir3.listFiles();
                                        if (infiles == null) {
                                            int DEBUG = 1;
                                        } else {
                                            for (int l = 0; l < infiles.length; l++) {
                                                System.out.println("Infile " + infiles[l]);
                                                rowCol = infiles[l].getName().split(indirname2)[1];
                                                System.out.println(rowCol);
                                                rowColint = getRowCol(rowCol);
                                                tileBounds = lp.getTileBounds(rowColint[0], rowColint[1]);
                                                Grids_GridDouble g;
                                                Grids_GridDouble g2;
                                                gridsdir4 = new File(gridsdir3, rowColint[0] + "_" + rowColint[1]);

                                                //g2 = getGrid(infiles[l], gridsdir4, cellsize, tileBounds, layerName, rowColint, hoome);
                                                g2 = null;

                                                if (grids1.containsKey(rowCol)) {
                                                    g = grids1.get(rowCol);
                                                    if (g == null) {
                                                        grids1.put(rowCol, g2);
                                                        counts1.put(rowCol, 1.0d);
                                                    } else {
                                                        gp.addToGrid(g, g2, 1.0d);
                                                        //grids1.put(rowCol, g); // Is this really necessary?
                                                        double d = counts1.get(rowCol);
                                                        d += 1.0d;
                                                        counts1.put(rowCol, d);
                                                        g2.ge.removeGrid(g2);
                                                    }
                                                } else {
                                                    grids1.put(rowCol, g2);
                                                    counts1.put(rowCol, 1.0d);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    // Push out aggregated results for each tile
                    Iterator<String> ite2;
                    Iterator<Generic_Date> ite;
                    ite = grids0.keySet().iterator();
                    while (ite.hasNext()) {
                        date1 = ite.next();
                        name1 = name0 + date1;
                        grids1 = grids0.get(date1);
                        counts1 = counts0.get(date1);
                        ite2 = grids1.keySet().iterator();
                        while (ite2.hasNext()) {
                            rowCol = ite2.next();
                            System.out.println(rowCol);
                            rowColint = getRowCol(rowCol);
                            Grids_GridDouble g;
                            Grids_GridDouble g2;
                            g = grids1.get(rowCol);
                            double d = counts1.get(rowCol);
                            /**
                             * The scaleFactor normalises the precipitation
                             * intensity.
                             */
                            gs = g.getStats(true); //Debug, why NPE null here? Think it is something to do with missing input data
                            max = gs.getMax(true).doubleValue();
                            min = gs.getMin(true).doubleValue();
                            System.out.println("max " + max);
                            System.out.println("min " + min);
                            scaleFactor = 24.0d / d;
                            System.out.println("scaleFactor " + scaleFactor);
                            g2 = gp.rescale(g, null, min * scaleFactor, max * scaleFactor);

                            grids1.put(rowCol, g2);

                            // Output as tiles
                            name = name1 + "_" + rowColint[0] + "_" + rowColint[1];
                            outputGrid(outdir2, name, g2, noDataValueColor, colorMap);
                        }
                    }
                    // Aggregate into catchment grid
                    long nrows1km = a1KMGridMaskedToCatchmentGrid.getNRows();
                    long ncols1km = a1KMGridMaskedToCatchmentGrid.getNCols();
                    Grids_GridDouble b1KMGridMaskedToCatchment;
                    Grids_GridDouble b1KMGridMaskedToCatchmentN;
                    Grids_GridDouble b1KMGridMaskedToCatchmentD;
                    double vb;
                    double v;
                    ite = grids0.keySet().iterator();
                    while (ite.hasNext()) {
                        date1 = ite.next();
                        name1 = name0 + date1;
                        System.out.println("<Duplicate 1KMGridMaskedToCatchment>");
                        Grids_GridDoubleFactory f;
                        f = (Grids_GridDoubleFactory) a1KMGrid[1];
                        gdir = gridf.createNewFile(gridf.getGeneratedGridDoubleDir());
                        b1KMGridMaskedToCatchment = (Grids_GridDouble) f.create(
                                gdir, a1KMGridMaskedToCatchmentGrid,
                                0L, 0L, nrows1km - 1, ncols1km - 1);
                        gdir = gridf.createNewFile(gridf.getGeneratedGridDoubleDir());
                        b1KMGridMaskedToCatchmentN = (Grids_GridDouble) f.create(
                                gdir, a1KMGridMaskedToCatchmentGrid,
                                0L, 0L, nrows1km - 1, ncols1km - 1);
                        gdir = gridf.createNewFile(gridf.getGeneratedGridDoubleDir());
                        b1KMGridMaskedToCatchmentD = (Grids_GridDouble) f.create(
                                gdir, a1KMGridMaskedToCatchmentGrid,
                                0L, 0L, nrows1km - 1, ncols1km - 1);
                        double noDataValue = b1KMGridMaskedToCatchment.getNoDataValue();
                        System.out.println("</Duplicate 1KMGridMaskedToCatchment>");
                        grids1 = grids0.get(date1);

                        ite2 = grids1.keySet().iterator();
                        while (ite2.hasNext()) {
                            rowCol = ite2.next();
                            //System.out.println("rowColint[0] (row) " + rowColint[0]);
                            //System.out.println("rowColint[1] (col) " + rowColint[1]);
                            Grids_GridDouble g;
                            g = grids1.get(rowCol);
                            //System.out.println("g " + g);
                            // Iterate over grid and get values
                            long nrows = g.getNRows();
                            long ncols = g.getNCols();
                            long bRow;
                            long bCol;
                            for (long row = 0; row < nrows; row++) {
                                y = g.getCellYBigDecimal(row);
                                //System.out.println("y " + y);
                                bRow = b1KMGridMaskedToCatchment.getRow(y);
                                //System.out.println("bRow " + bRow);
                                for (long col = 0; col < ncols; col++) {
                                    x = g.getCellXBigDecimal(col);
                                    //System.out.println("x " + x);
                                    bCol = b1KMGridMaskedToCatchment.getCol(x);
                                    //System.out.println("bCol " + bCol);
                                    if (b1KMGridMaskedToCatchment.isInGrid(bRow, bCol)) {
                                        vb = b1KMGridMaskedToCatchment.getCell(bRow, bCol);
                                        if (vb != noDataValue) {
                                            v = g.getCell(row, col);
                                            //System.out.println("Row, Col " + bRow + ", " + bCol);
                                            if (v != noDataValue) {
                                                b1KMGridMaskedToCatchmentN.addToCell(bRow, bCol, v);
                                                b1KMGridMaskedToCatchmentD.addToCell(bRow, bCol, 1.0d);
                                            }
                                        } else {
                                            //System.out.println("Out of study area.");
                                            //b1KMGrid.setCell(row, col, noDataValue, true);
                                        }
                                    }
                                }
                            }
                        }
                        b1KMGridMaskedToCatchment = gp.divide(
                                b1KMGridMaskedToCatchmentN,
                                b1KMGridMaskedToCatchmentD);
                        b1KMGridMaskedToCatchmentN.ge.removeGrid(b1KMGridMaskedToCatchmentN);
                        b1KMGridMaskedToCatchmentD.ge.removeGrid(b1KMGridMaskedToCatchmentD);
                        Grids_GridDouble g;
                        g = b1KMGridMaskedToCatchment;
//                        gs = b1KMGridMaskedToCatchment.getStats(true);
//                        max = gs.getMax(true, true).doubleValue();
//                        min = gs.getMin(true, true).doubleValue();
//                        System.out.println("max " + max);
//                        System.out.println("min " + min);
//                        scaleFactor = 24.0d / (double) counts.get(date1);
//                        g = gp.rescale(b1KMGridMaskedToCatchment, null, min * scaleFactor, max * scaleFactor, true);
                        // Output as tiles
                        outputGrid(outdir2, name1, g, noDataValueColor, colorMap);
                        g.ge.removeGrid(g);
//                        try {
//                            PrintWriter pw;
//                            pw = new PrintWriter(outtxt);
//                            pw.println("" + counts.get(date1));
//                            pw.close();
//                        } catch (FileNotFoundException ex) {
//                            Logger.getLogger(SARIC_ImageProcessor.class.getName()).log(Level.SEVERE, null, ex);
//                        }
                        //init_gf();
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
    public void processForecasts2(TreeMap<Double, Color> colorMap,
            Color ndvColor, String area, int scale, String layerName,
            BigDecimal cellsize, SARIC_MetOfficeParameters p,
            SARIC_MetOfficeLayerParameters lp,
            SARIC_MetOfficeCapabilitiesXMLDOMReader r,
            Grids_GridDoubleFactory f, Grids_GridDouble g) {
        String methodName;
        methodName = "processForecasts(...)";
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
        pathIn = "inspire/view/wmtsall/" + area + "/" + layerName + "/EPSG_27700_";
        pathOut = "inspire/view/wmts0/" + area + "/" + layerName + "/EPSG_27700_";
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
        timesForecast = getTimesForecasts(
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
                outdir1 = new File(outdir0, d.getYYYYMM());
                outdir1 = new File(outdir1, d.getYYYYMMDD());
                outdir1.mkdirs();
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
            outdir1 = new File(outdir0, d.getYYYYMM());
            outdir1 = new File(outdir1, d.getYYYYMMDD());
            timen = Integer.toString((int) t.differenceInHours(t0));
            g0 = getGridForecast(f, b1KMGrid, nrows, ncols, ndv,
                    gridf, indir0, gridsdir, t, timen, dateComponentDelimeter,
                    dateTimeDivider, timeComponentDivider, ending, cellsize, lp);
            while (itet.hasNext()) {

                t1 = itet.next();
                name = name0 + d0.getYYYYMMDD();
                file = new File(outdir1, name + ".asc");
                if (overwrite || !file.exists()) {
                    writtenOut = false;
                    d1 = t1.getDate();
                    timen = Integer.toString((int) t.differenceInHours(t1));
                    g1 = getGridForecast(f, b1KMGrid, nrows, ncols, ndv,
                            gridf, indir0, gridsdir, t, timen, dateComponentDelimeter,
                            dateTimeDivider, timeComponentDivider, ending, cellsize, lp);
                    differenceInMinutes = t0.differenceInMinutes(t1);
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
//                                    if (v > 100) {
//                                        int debug = 1;
//                                    }
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
                        outdir1 = new File(outdir0, d.getYYYYMM());
                        outdir1 = new File(outdir1, d.getYYYYMMDD());
                        outdir1.mkdirs();
                        outputGrid(outdir1, name, b1KMGrid, ndvColor, colorMap);
                        b1KMGrid = reinitGrid(b1KMGrid, g, f, gridf);
                        writtenOut = true;
                    }
                    ge.removeGrid(g0);
                    g0 = g1;
                    d0 = d1;
                    outdir1 = new File(outdir0, d.getYYYYMM());
                    outdir1 = new File(outdir1, d.getYYYYMMDD());
                }
                t0 = t1;
            }
        }
        System.out.println("</" + methodName + ">");
    }

    /**
     * Initialise times.
     */
    public TreeSet<Generic_Time> getTimesObservations(File indir0, String dateComponentDelimeter,
            String dateTimeDivider, String timeComponentDivider, String ending) {
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
    public TreeMap<Generic_Time, TreeSet<Generic_Time>> getTimesForecasts(
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
    public void processObservations(TreeMap<Double, Color> colorMap,
            Color ndvColor, String area, int scale, String layerName,
            BigDecimal cellsize,
            SARIC_MetOfficeParameters p,
            SARIC_MetOfficeLayerParameters lp,
            SARIC_MetOfficeCapabilitiesXMLDOMReader r,
            Grids_GridDoubleFactory f,
            Grids_GridDouble g) {
        String methodName;
        methodName = "processObservations(...)";
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
        pathIn = "inspire/view/wmtsall/" + area + "/" + layerName + "/EPSG_27700_";
        pathOut = "inspire/view/wmts0/" + area + "/" + layerName + "/EPSG_27700_";
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
        times = getTimesObservations(indir0, dateComponentDelimeter, dateTimeDivider,
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
        outdir1 = new File(outdir0, d0.getYYYYMM());
        outdir1 = new File(outdir1, d0.getYYYYMMDD());
        g0 = getGridObserved(f, b1KMGrid, nrows, ncols, ndv,
                gridf, indir0, gridsdir, t0, dateComponentDelimeter,
                dateTimeDivider, timeComponentDivider, ending, cellsize, lp);
        while (itet.hasNext()) {
            t1 = itet.next();
            if (overwrite || !outdir1.exists()) {
                d1 = t1.getDate();
                g1 = getGridObserved(f, b1KMGrid, nrows, ncols, ndv,
                        gridf, indir0, gridsdir, t1, dateComponentDelimeter,
                        dateTimeDivider, timeComponentDivider, ending, cellsize, lp);
                differenceInMinutes = t0.differenceInMinutes(t1);
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
//                                    if (v > 100) {
//                                        int debug = 1;
//                                    }
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
                    outdir1 = new File(outdir0, d0.getYYYYMM());
                    outdir1 = new File(outdir1, d0.getYYYYMMDD());
                    outdir1.mkdirs();
                    outputGrid(outdir1, name, b1KMGrid, ndvColor, colorMap);
                    b1KMGrid = reinitGrid(b1KMGrid, g, f, gridf);
                }
                ge.removeGrid(g0);
                g0 = g1;
                d0 = d1;
                outdir1 = new File(outdir0, d0.getYYYYMM());
                outdir1 = new File(outdir1, d0.getYYYYMMDD());
            }
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
        g.ge.removeGrid(g);
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
//                ge.clearMemoryReserve();
//                ge.swapChunks(hoome);
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
        if ((weatherType >= 9 && weatherType <= 12) || (weatherType >= 19 && weatherType <= 24 && weatherType != 21)) {
            return 0.55d * precipitationProbability / 100.0d;
        } else if ((weatherType >= 16 && weatherType <= 18) || weatherType == 21) {
            return 0.055d * precipitationProbability / 100.0d;
        } else if ((weatherType >= 13 && weatherType <= 15) || (weatherType >= 25 && weatherType <= 30)) {
            return 5.5d * precipitationProbability / 100.0d;
        } else {
            return 0.0d;
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
    protected double getEstimateLow(SARIC_SiteForecastRecord r) {
        int weatherType;
        weatherType = r.getWeatherType();
        int precipitationProbability;
        precipitationProbability = r.getPrecipitationProbability();
        if ((weatherType >= 9 && weatherType <= 12) || (weatherType >= 19 && weatherType <= 24 && weatherType != 21)) {
            return 0.1d * precipitationProbability / 100.0d;
        } else if ((weatherType >= 16 && weatherType <= 18) || weatherType == 21) {
            return 0.01d * precipitationProbability / 100.0d;
        } else if ((weatherType >= 13 && weatherType <= 15) || (weatherType >= 25 && weatherType <= 30)) {
            return 1.0d * precipitationProbability / 100.0d;
        } else {
            return 0.0d;
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
    protected double getEstimateHigh(SARIC_SiteForecastRecord r) {
        int weatherType;
        weatherType = r.getWeatherType();
        int precipitationProbability;
        precipitationProbability = r.getPrecipitationProbability();
        if ((weatherType >= 9 && weatherType <= 12) || (weatherType >= 19 && weatherType <= 24 && weatherType != 21)) {
            return 1.0d * precipitationProbability / 100.0d;
        } else if ((weatherType >= 16 && weatherType <= 18) || weatherType == 21) {
            return 0.1d * precipitationProbability / 100.0d;
        } else if ((weatherType >= 13 && weatherType <= 15) || (weatherType >= 25 && weatherType <= 30)) {
            return 10.0d * precipitationProbability / 100.0d;
        } else {
            return 0.0d;
        }
    }

}
