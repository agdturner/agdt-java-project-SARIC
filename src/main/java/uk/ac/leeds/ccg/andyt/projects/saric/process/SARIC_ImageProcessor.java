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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import uk.ac.leeds.ccg.andyt.projects.saric.util.SARIC_Date;
import uk.ac.leeds.ccg.andyt.projects.saric.util.SARIC_Time;
import uk.ac.leeds.ccg.andyt.projects.saric.util.SARIC_YearMonth;
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
    Grids_GridDoubleFactory gf;
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
            boolean overwrite
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
        Files = se.getFiles();
        Strings = se.getStrings();
        ge = se.getGrids_Environment();
        ae = new Grids_ESRIAsciiGridExporter(ge);
        ie = new Grids_ImageExporter(ge);
        gp = ge.getProcessor();
        init_gf();
    }

    private void init_gf() {
        gf = new Grids_GridDoubleFactory(ge, gp.GridChunkDoubleFactory,
                gp.DefaultGridChunkDoubleFactory, -Double.MAX_VALUE,
                256, 256, new Grids_Dimensions(256, 256),
                new Grids_GridDoubleStatsNotUpdated(ge));
        gp.GridDoubleFactory = gf;
    }

    @Override
    public void run() {

        SARIC_Colour sc;
        sc = new SARIC_Colour(se);

        TreeMap<Double, Color> colorMap;
        colorMap = sc.getColorMap();
        Color noDataValueColor;
        noDataValueColor = Color.BLACK;
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
            if (doTeifi) {
                areaName = Strings.getString_Teifi();
                SARIC_Teifi st;
                st = new SARIC_Teifi(se);
                sites = st.getForecastsSitesInStudyArea(Strings.getString_3hourly());
                nearestForecastsSitesGridAndFactory = st.getNearestForecastsSitesGrid(sites);
                nonTiledForecastsForArea(areaName, sites,
                        nearestForecastsSitesGridAndFactory,
                        colorMap, noDataValueColor);
            }
            if (doWissey) {
                areaName = Strings.getString_Wissey();
                SARIC_Wissey sw;
                sw = new SARIC_Wissey(se);
                sites = sw.getForecastsSitesInStudyArea(Strings.getString_3hourly());
                nearestForecastsSitesGridAndFactory = sw.getNearestForecastsSitesGrid(sites);
                nonTiledForecastsForArea(areaName, sites,
                        nearestForecastsSitesGridAndFactory,
                        colorMap, noDataValueColor);
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
            Object[] sw1KMGrid = null;
            Object[] sw1KMGridMaskedToCatchment = null;
            if (doWissey) {
                SARIC_Wissey sw;
                sw = se.getWissey();
                sw1KMGrid = sw.get1KMGrid("1KMGrid");
                sw1KMGridMaskedToCatchment = sw.get1KMGridMaskedToCatchment();
            }

            // Initialisation for Teifi
            Object[] st1KMGrid = null;
            Object[] st1KMGridMaskedToCatchment = null;
            if (doTeifi) {
                SARIC_Teifi st;
                st = se.getTeifi();
                st1KMGrid = st.get1KMGrid("1KMGrid");
                st1KMGridMaskedToCatchment = st.get1KMGridMaskedToCatchment();
            }

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
                        area = Strings.getString_Wissey();
                        processForecasts(colorMap, noDataValueColor, area,
                                scale, layerName, cellsize, p, lp, r, sw1KMGrid,
                                sw1KMGridMaskedToCatchment);
                    }
                    if (doTeifi) {
                        area = Strings.getString_Teifi();
                        processForecasts(colorMap, noDataValueColor, area,
                                scale, layerName, cellsize, p, lp, r, st1KMGrid,
                                st1KMGridMaskedToCatchment);
                    }
                }
            }
            // Observations
            if (doObservationsTileFromWMTSService) {
                layerName = Strings.getString_RADAR_UK_Composite_Highres();
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
                        area = Strings.getString_Wissey();
                        processObservations(
                                colorMap, noDataValueColor, area, scale,
                                layerName, cellsize, p, lp, r, sw1KMGrid,
                                sw1KMGridMaskedToCatchment);
                    }
                    if (doTeifi) {
                        area = Strings.getString_Teifi();
                        processObservations(
                                colorMap, noDataValueColor, area, scale,
                                layerName, cellsize, p, lp, r, st1KMGrid,
                                st1KMGridMaskedToCatchment);
                    }
                }
            }
        }
    }

    private void nonTiledForecastsForArea(
            String areaName,
            HashSet<SARIC_Site> sites,
            Object[] nearestForecastsSitesGridAndFactory,
            TreeMap<Double, Color> colorMap,
            Color noDataValueColor) {
        Grids_Files gridf;
        gridf = ge.getFiles();
        File gdir;
        String dataType;
        String path;
        File indir0;
        File indir1;
        File indir2;
        File indir3;
        File outdir0;
        File outdir1;
        File outdir2;
        File outdir3;
        String name;
        // Initialisation part 1
        dataType = Strings.getString_xml();
        path = Files.getValDataTypePath(dataType, Strings.getString_wxfcs());
        indir0 = new File(
                Files.getInputDataMetOfficeDataPointDir(),
                path);
        System.out.println(indir0);
        indir0 = new File(
                indir0,
                Strings.getString_site() + "0");
        /**
         * There is no need to run for daily, it is just for the same time as
         * the 3hourly, but gives lower temporal resolution and we want high
         * temporal resolution.
         */
        indir0 = new File(
                indir0,
                Strings.getString_3hourly());
        outdir0 = new File(
                Files.getOutputDataMetOfficeDataPointDir(),
                path);
        outdir0 = new File(outdir0, areaName);

        SARIC_Date date0;
        SARIC_Date date;
        // Declaration part 2
        SARIC_Date date1;
        SARIC_Date date2;
        TreeSet<SARIC_Date> dates;
        TreeSet<SARIC_Date> dates2;
        Grids_GridDouble nearestForecastsSitesGrid;
        double noDataValue1;
        HashMap<SARIC_Site, HashMap<SARIC_Time, SARIC_SiteForecastRecord>> forecasts;
        long nrows;
        long ncols;
        nearestForecastsSitesGrid = (Grids_GridDouble) nearestForecastsSitesGridAndFactory[0];
        noDataValue1 = nearestForecastsSitesGrid.getNoDataValue();
        gf.setNoDataValue(noDataValue1);
        nrows = nearestForecastsSitesGrid.getNRows();
        ncols = nearestForecastsSitesGrid.getNCols();
        Grids_GridDouble forecastsForTime2;
        File[] indirs;
        indirs = indir0.listFiles();
        File[] dirs3;
        for (File dirs2 : indirs) {
            dirs3 = dirs2.listFiles();
            for (File dir3 : dirs3) {
                date = new SARIC_Date(se, dir3.getName());
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
                    date1 = new SARIC_Date(date);
                    date1.addDays(i);
                    dates.add(date1);
                }
                Iterator<SARIC_Date> iterat;
                iterat = dates.iterator();
                while (iterat.hasNext()) {
                    date1 = iterat.next();
                    File outascii;
                    File outpng;
                    File outpng2;
                    File outpng3;
                    if (indir1.exists()) {
                        forecasts = new HashMap<>();
                        gdir = gridf.createNewFile(gridf.getGeneratedGridDoubleDir());
                        forecastsForTime2 = (Grids_GridDouble) gf.create(gdir, nearestForecastsSitesGrid);
                        name = date + "-00" + "_ForecastFor_" + date1.getYYYYMMDD();
                        outascii = new File(
                                outdir1,
                                name + ".asc");
                        outpng = new File(
                                outdir1,
                                name + ".png");
                        outpng2 = new File(
                                outdir1,
                                name + "Color.png");
                        outpng3 = new File(
                                outdir1,
                                name + "Color8.png");
                        if (outpng3.exists()) {
                            System.out.println("Output " + outpng + " already exists!!!");
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
                                indir2 = new File(
                                        indir1,
                                        "" + siteID);
                                //System.out.println("dir2 " + dir2);
                                String dirname;
                                dirname = indir2.list()[0];
                                //System.out.println("dirname " + dirname);
                                indir2 = new File(
                                        indir2,
                                        dirname);
                                String filename;
                                filename = siteID + Strings.getString_3hourly() + Strings.symbol_dot + dataType;
                                File f;
                                f = new File(
                                        indir2,
                                        filename);
                                SARIC_MetOfficeSiteXMLSAXHandler h;
                                h = new SARIC_MetOfficeSiteXMLSAXHandler(se, f);
                                HashMap<SARIC_Time, SARIC_SiteForecastRecord> forecastsForTime;
                                forecastsForTime = h.parse();
                                forecasts.put(site, forecastsForTime);
                                //System.out.println("SARIC_MetOfficeSiteXMLSAXHandler " + h);

                                // Get estimate of total rainfall.
                                estimate = 0.0d;
                                Iterator<SARIC_Time> ite2;
                                double numberOfEstimates;
                                numberOfEstimates = 0;
                                SARIC_Time t;
                                ite2 = forecastsForTime.keySet().iterator();
                                double normalisedEstimate;
                                while (ite2.hasNext()) {
                                    t = ite2.next();
                                    if (t.isSameDay(date1)) {
                                        estimate += getEstimate(forecastsForTime.get(t));
                                        numberOfEstimates++;
//                                                System.out.println("estimate " + estimate);
//                                                System.out.println("numberOfEstimates " + numberOfEstimates);
//                                                normalisedEstimate = (estimate / numberOfEstimates) * 24;
//                                                System.out.println("normalisedEstimate " + normalisedEstimate);
                                    }
                                }
                                /**
                                 * normalisedEstimate gives an estimate of the
                                 * total amount of rainfall in a day. This
                                 * averages all the intensities and then
                                 * multiplies by 24 as there are 24 hours in the
                                 * day.
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
                                        //if (v != noDataValue) {
                                        if (v == siteID) {
                                            forecastsForTime2.setCell(row, col, normalisedEstimate);
                                        }
                                        //}
                                    }
                                }
                            }
                            ae.toAsciiFile(forecastsForTime2, outascii);
                            ie.toGreyScaleImage(forecastsForTime2, gp, outpng, "png");
                            ie.toColourImage(0, colorMap, Color.BLACK, forecastsForTime2, outpng2, "png");
                            ie.toColourImage(8, colorMap, Color.BLACK, forecastsForTime2, outpng3, "png");
                        }
                    }
                }
            }
        }
    }

    /**
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
        methodName = "processForecasts("
                + "TreeMap<SARIC_Time, File>,"
                + "TreeMap<SARIC_Time, String>,"
                + "HashSet<String>,FileString,String,"
                + "BigDecimal,SARIC_MetOfficeParameters,"
                + "SARIC_MetOfficeLayerParameters,"
                + "SARIC_MetOfficeCapabilitiesXMLDOMReader,"
                + "Grids_Grid2DSquareCellDouble)";
        System.out.println("<" + methodName + ">");
        // Initial declaration
        Grids_Files gridf;
        gridf = ge.getFiles();
        File gdir;
        /**
         * ymDates, ym, dates and ite0 are for organising the order of
         * processing.
         */
        TreeMap<SARIC_YearMonth, TreeSet<SARIC_Date>> ymDates;
        SARIC_YearMonth ym;
        TreeSet<SARIC_Date> dates;
        Iterator<SARIC_YearMonth> ite0;
        /**
         *
         */
        String path;
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
        path = "inspire/view/wmts0/" + area + "/" + layerName + "/EPSG_27700_";
        System.out.println("scale " + scale);
        indir0 = new File(Files.getInputDataMetOfficeDataPointDir(), path + scale);
        outdir0 = new File(Files.getOutputDataMetOfficeDataPointDir(), path + scale);
        gridsdir0 = new File(gp.getDirectory(), path + scale);
        ymDates = new TreeMap<>();
        indirs0 = indir0.listFiles();
        for (int i = 0; i < indirs0.length; i++) {
            s = indirs0[i].getName();
            ym = new SARIC_YearMonth(se, s);
            dates = new TreeSet<>();
            ymDates.put(ym, dates);
            indir1 = new File(indir0, s);
            indirs1 = indir1.listFiles();
            // initialise outdirs
            for (int j = 0; j < indirs1.length; j++) {
                dates.add(new SARIC_Date(se, indirs1[j].getName().split("T")[0]));
            }
        }

        boolean hoome = true;
        Vector_Envelope2D tileBounds;
        double weight;
        weight = 3d; // These are 3 hourly forecasts.
        String outDirName;
        String outDirNameCheck;
        String indirname;
        String indirname2;
        File[] indirs2;
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
        File outtxt;
        Grids_GridDouble a1KMGridMaskedToCatchmentGrid;
        a1KMGridMaskedToCatchmentGrid = (Grids_GridDouble) a1KMGridMaskedToCatchment[0];
        SARIC_Date date0;
        SARIC_Date date1;
        SARIC_Date tomorrow;
        SARIC_Date dayAfterTomorrow;

        SARIC_Time time0;
        SARIC_Time time1;

        String name0;
        String name1;

        TreeMap<SARIC_Date, HashMap<String, Grids_GridDouble>> grids0;
        HashMap<SARIC_Date, HashMap<String, Double>> counts0;
        HashMap<String, Grids_GridDouble> grids1;
        HashMap<String, Double> counts1;

        Grids_AbstractGridNumberStats gs;
        double max;
        double min;
        double scaleFactor;

        Iterator<SARIC_Date> ite1;

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
                tomorrow = new SARIC_Date(date0);
                tomorrow.addDays(1);
                dayAfterTomorrow = new SARIC_Date(tomorrow);
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
                                time0 = new SARIC_Time(se, indirname2,
                                        se.getStrings().symbol_minus,
                                        se.getStrings().string_T,
                                        se.getStrings().symbol_underscore);
                                for (int k = 0; k <= 36; k += 3) {
                                    time1 = new SARIC_Time(time0);
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
                                                g2 = getGrid(infiles[l], gridsdir4, cellsize, tileBounds, layerName, rowColint, hoome);
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
                    Iterator<SARIC_Date> ite;
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
                            gs = g.getStats(true);
                            max = gs.getMax(true).doubleValue();
                            min = gs.getMin(true).doubleValue();
                            System.out.println("max " + max);
                            System.out.println("min " + min);
                            scaleFactor = 24.0d / d;
                            System.out.println("scaleFactor " + scaleFactor);
                            g2 = gp.rescale(g, null, min * scaleFactor, max * scaleFactor);

                            grids1.put(rowCol, g2);

                            // Output as tiles
                            outascii = new File(outdir2,
                                    name1 + "_" + rowColint[0] + "_" + rowColint[1] + ".asc");
                            outpng = new File(outdir2,
                                    name1 + "_" + rowColint[0] + "_" + rowColint[1] + ".png");
                            outpng2 = new File(outdir2,
                                    name1 + "_" + rowColint[0] + "_" + rowColint[1] + "Color.png");
                            /**
                             * TODO: For some weird reason if the colour image
                             * is generated after the Grey scale one then the
                             * files get confused!
                             */
                            ie.toColourImage(0, colorMap, noDataValueColor, g2, outpng2, "png");
                            ie.toGreyScaleImage(g2, gp, outpng, "png");
                            ae.toAsciiFile(g2, outascii);
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
                        outascii = new File(outdir2, name1 + ".asc");
                        outpng = new File(outdir2, name1 + ".png");
                        outpng2 = new File(outdir2, name1 + "Color.png");
                        outpng3 = new File(outdir2, name1 + "Color8.png");
                        outtxt = new File(outdir2, name1 + "Counts.txt");
                        ae.toAsciiFile(g, outascii);
                        ie.toGreyScaleImage(g, gp, outpng, "png");
                        ie.toColourImage(0, colorMap, noDataValueColor, g, outpng2, "png");
                        ie.toColourImage(8, colorMap, noDataValueColor, g, outpng3, "png");
                        try {
                            PrintWriter pw;
                            pw = new PrintWriter(outtxt);
                            //pw.println("" + counts.get(date1));
                            pw.close();
                        } catch (FileNotFoundException ex) {
                            Logger.getLogger(SARIC_ImageProcessor.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        init_gf();
                    }
                }
            }
        }
        System.out.println("</" + methodName + ">");
    }
    //    /**
    //     *
    //     * @param area
    //     * @param scale
    //     * @param layerName
    //     * @param cellsize
    //     * @param p
    //     * @param lp
    //     * @param r
    //     * @param a1KMGrid
    //     */
    //    public void processForecasts(
    //            TreeMap<Double, Color> colorMap,
    //            Color noDataValueColor,
    //            String area,
    //            int scale,
    //            String layerName,
    //            BigDecimal cellsize,
    //            SARIC_MetOfficeParameters p,
    //            SARIC_MetOfficeLayerParameters lp,
    //            SARIC_MetOfficeCapabilitiesXMLDOMReader r,
    //            Object[] a1KMGrid,
    //            Object[] a1KMGridMaskedToCatchment) {
    //        String methodName;
    //        methodName = "processForecasts("
    //                + "TreeMap<SARIC_Time, File>,"
    //                + "TreeMap<SARIC_Time, String>,"
    //                + "HashSet<String>,FileString,String,"
    //                + "BigDecimal,SARIC_MetOfficeParameters,"
    //                + "SARIC_MetOfficeLayerParameters,"
    //                + "SARIC_MetOfficeCapabilitiesXMLDOMReader,"
    //                + "Grids_GridDouble)";
    //        System.out.println("<" + methodName + ">");
    //        // Initial declarations
    //        String path;
    //        File indir;
    //        File[] indirs;
    //        File outdir;
    //        TreeMap<SARIC_Time, File> orderedForecastdirs;
    //        TreeMap<SARIC_Time, String> orderedOutdirNames;
    //        String s;
    //        SARIC_Time time;
    //        // Initial assignments
    //        System.out.println("Area " + area);
    //        path = "inspire/view/wmts0/" + area + "/" + layerName + "/EPSG_27700_";
    //        indir = new File(
    //                Files.getInputDataMetOfficeDataPointDir(),
    //                path + scale);
    //        indirs = indir.listFiles();
    //        /**
    //         * indirs should contain data for all dates. The data for some dates
    //         * actually refers to forecasts for the following day or even for the
    //         * day after that as the forecasts are for the following 36 hours.
    //         *
    //         * It is imperative to have an ordered list of directories for each
    //         * date, so we create one when initialising outdirs as this already
    //         * involves going through indirs.
    //         */
    //        orderedForecastdirs = new TreeMap<SARIC_Time, File>();
    //        orderedOutdirNames = new TreeMap<SARIC_Time, String>();
    //        for (int j = 0; j < indirs.length; j++) {
    //            s = indirs[j].getName();
    //            time = new SARIC_Time(s);
    //            orderedOutdirNames.put(time, s);
    //            orderedForecastdirs.put(time, indirs[j]);
    //        }
    //        outdir = new File(
    //                Files.getOutputDataMetOfficeDataPointDir(),
    //                path + scale);
    //        // Further declarations
    //        Vector_Envelope2D tileBounds;
    //        Boolean HandleOutOfMemoryError = true;
    //        double weight;
    //        weight = 1d;
    //        String outDirName;
    //        String outDirNameCheck;
    //        String indirname;
    //        String indirname2;
    //        File[] indirs2;
    //        File[] infiles;
    //        File[] unorderedForecastdirs2;
    //        String rowCol;
    ////        int row;
    ////        int col;
    //        double x;
    //        double y;
    //        double halfcellsize;
    //        halfcellsize = cellsize.doubleValue() / 2.0d;
    //        Grids_GridDouble g;
    //        File outascii;
    //        File outpng;
    //        File outpng2;
    //        SARIC_Time time2;
    //        SARIC_Time time3;
    //        SARIC_Time time4;
    //        /**
    //         * Main processing. With a full set of forecasts there are essentially 7
    //         * forecasts of rainfall for each 3 hourly time snapshot. However, there
    //         * is a need to start and end somewhere, so for the first dates we only
    //         * have the forecasts from that time going forwards and likewise for the
    //         * most recent dates, there may be more forecasts to come...
    //         */
    //        HashMap<SARIC_Time, Grids_GridDouble> output1kmGrids;
    //        output1kmGrids = new HashMap<SARIC_Time, Grids_GridDouble>();
    //        HashMap<SARIC_Time, HashMap<String, Grids_GridDouble>> gridsAll;
    //        gridsAll = new HashMap<SARIC_Time, HashMap<String, Grids_GridDouble>>();
    //        HashMap<String, Grids_GridDouble> grids;
    //        HashMap<SARIC_Time, Integer> counts;
    //        counts = new HashMap<SARIC_Time, Integer>();
    //        File outdir2;
    //        Iterator<SARIC_Time> ite;
    //        ite = orderedOutdirNames.keySet().iterator();
    //        while (ite.hasNext()) {
    //            time = ite.next();
    //            System.out.println("time " + time);
    //            /**
    //             * At the end of this iteration we should be able to output the
    //             * grids from this time as everything that can be added will be
    //             * added by this stage. Also the grids can be set to null to free
    //             * memory.
    //             */
    //            outDirName = orderedOutdirNames.get(time); // outDirName could also be derived from date perhaps via a toString() method!
    //            System.out.println(outDirName);
    //            outdir2 = new File(
    //                    outdir,
    //                    outDirName);
    //            outascii = new File(
    //                    outdir2,
    //                    layerName + ".asc");
    //            outpng2 = new File(
    //                    outdir2,
    //                    layerName + "Colour.png");
    //            if (!overwrite && outpng2.exists()) {
    //                System.out.println("Not overwriting and " + outascii.toString() + " exists.");
    //            } else {
    //                /**
    //                 * In the fullness of time (assuming all the forecasts are
    //                 * collected), there should be four indirs, one for each of the
    //                 * 4 forecasts made for the proceeding 36 hours. The scheduled
    //                 * forecasts are from 3am, 9am, 3pm and 9pm each day. So if
    //                 * there are fewer than 4 directories, the processing could be
    //                 * for the current date, rather than being processing for a day
    //                 * in the past for which the generalised data are still wanted
    //                 * for validation purposes.
    //                 */
    //                outdir2.mkdirs();
    //                indir = orderedForecastdirs.get(time);
    //                unorderedForecastdirs2 = indir.listFiles();
    //
    //                TreeMap<SARIC_Time, File> orderedForecastDirs2;
    //                orderedForecastDirs2 = new TreeMap<SARIC_Time, File>();
    //                for (int i = 0; i < unorderedForecastdirs2.length; i++) {
    //                    time3 = new SARIC_Time(unorderedForecastdirs2[i].getName());
    //                    orderedForecastDirs2.put(time3, unorderedForecastdirs2[i]);
    //                }
    //
    //                Iterator<SARIC_Time> ite2;
    //                ite2 = orderedForecastDirs2.keySet().iterator();
    //                while (ite2.hasNext()) {
    //                    time3 = ite2.next();
    //                    File dir;
    //                    dir = orderedForecastDirs2.get(time3);
    //
    //                    if (dir == null) {
    //                        System.out.println("dir " + dir + " == null!");
    //                        int debug = 1;
    //                        dir = orderedForecastDirs2.get(time3);
    //                    }
    //
    //                    indirname2 = dir.getName();
    //                    for (int k = 0; k <= 36; k += 3) {
    //                        time2 = new SARIC_Time(time3);
    //                        time2.addHours(k);
    //                        /**
    //                         * Set time4 to be the right day. There is only a need
    //                         * to add up to 2 days as only looking forward 36 hours.
    //                         */
    //                        time4 = new SARIC_Time(time);
    //                        if (time2.getDayOfMonth() != time4.getDayOfMonth()) {
    //                            time4.addDays(1);
    //                            if (time2.getDayOfMonth() != time4.getDayOfMonth()) {
    //                                time4.addDays(1);
    //                            }
    //                        }
    //                        // Initialise grids and counts
    //                        if (gridsAll.containsKey(time4)) {
    //                            grids = gridsAll.get(time4);
    //                        } else {
    //                            grids = new HashMap<String, Grids_GridDouble>();
    //                            gridsAll.put(time4, grids);
    //                        }
    //                        File indir3;
    //                        indir3 = new File(
    //                                dir,
    //                                "" + k);
    //                        infiles = indir3.listFiles();
    //                        if (infiles == null) {
    //                            System.out.println("infiles == null : There are no files in " + indir3);
    //                            int DEBUG = 1;
    //                        } else {
    //                            for (int i = 0; i < infiles.length; i++) {
    //                                rowCol = infiles[i].getName().split(indirname2)[1];
    //                                int[] rowColint;
    //                                rowColint = getRowCol(rowCol);
    //                                tileBounds = lp.getTileBounds(rowColint[0], rowColint[1]);
    //                                System.out.println("Infile " + infiles[i]);
    //                                g = getGrid(infiles[i], cellsize, tileBounds, layerName, rowColint);
    //                                if (g != null) {
    //                                    if (grids.containsKey(rowCol)) {
    //                                        Grids_GridDouble gridToAddTo;
    //                                        gridToAddTo = grids.get(rowCol);
    //                                        gp.addToGrid(gridToAddTo, g, weight, true);
    //                                    } else {
    //                                        grids.put(rowCol, g);
    //                                    }
    //                                }
    //                            }
    //                            if (counts.containsKey(time4)) {
    //                                counts.put(time4, counts.get(time4) + 1);
    //                            } else {
    //                                counts.put(time4, 1);
    //                            }
    //                        }
    //                    }
    //                }
    //            }
    //            grids = gridsAll.get(time);
    //            if (grids == null) {
    //                System.out.println("No grid for time " + time);
    //            } else {
    //                Iterator<String> gridsIte;
    //                gridsIte = grids.keySet().iterator();
    //                Grids_GridDouble b1KMGrid = null;
    //                if (output1kmGrids.containsKey(time)) {
    //                    b1KMGrid = output1kmGrids.get(time);
    //                } else {
    //                    Grids_GridDoubleFactory f;
    //                    f = (Grids_GridDoubleFactory) a1KMGrid[1];
    //                    b1KMGrid = (Grids_GridDouble) f.create((Grids_GridDouble) a1KMGrid[0]);
    //                    output1kmGrids.put(time, b1KMGrid);
    //                }
    //                long nrows = b1KMGrid.getNRows(true);
    //                long ncols = b1KMGrid.getNCols(true);
    //                while (gridsIte.hasNext()) {
    //                    rowCol = gridsIte.next();
    ////                int[] rowColint;
    ////                rowColint = getRowCol(rowCol);
    //                    g = grids.get(rowCol);
    //                    // Iterate over grid and get values
    //                    for (long row = 0; row < nrows; row++) {
    //                        y = b1KMGrid.getCellYDouble(row, true) + halfcellsize; // adding half a cellsize resolves an issue with striping where images join.
    //                        for (long col = 0; col < ncols; col++) {
    //                            x = b1KMGrid.getCellXDouble(col, true) + halfcellsize; // adding half a cellsize resolves an issue with striping where images join.
    //                            b1KMGrid.addToCell(row, col, g.getCell(x, y, true), true);
    //                        }
    //                    }
    //                }
    //                g = output1kmGrids.get(time);
    //                Grids_AbstractGridNumberStats gs;
    //                gs = g.getStats(true);
    //                double max = gs.getMaxDouble(true);
    //                double min = gs.getMinDouble(true);
    //                /**
    //                 * The scaleFactor is the number of grids divided by the number
    //                 * of hours in the day
    //                 */
    //                double scaleFactor;
    //                if (counts.get(time) == null) {
    //                    System.out.println("counts.get(time) == null");
    //                } else {
    //                    scaleFactor = counts.get(time) / 24d;
    //                    g = gp.rescale(g, null, min * scaleFactor, max * scaleFactor, true);
    //                    ae.toAsciiFile(g, outascii, HandleOutOfMemoryError);
    //                    outpng = new File(
    //                            outdir2,
    //                            layerName + ".png");
    //                    ie.toGreyScaleImage(g, gp, outpng, "png", HandleOutOfMemoryError);
    //                    ie.toColourImage(colorMap, noDataValueColor, g, gp, outpng, "png", HandleOutOfMemoryError);
    //                }
    //                /**
    //                 * Clear some space as these results are now output.
    //                 */
    //                output1kmGrids.remove(time);
    //                gridsAll.remove(time);
    //            }
    //        }
    //        System.out.println("</" + methodName + ">");
    //    }

    public void processObservations(
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
        methodName = "processObservations(File[],HashSet<String>,FileString,"
                + "String,BigDecimal,SARIC_MetOfficeParameters,"
                + "SARIC_MetOfficeLayerParameters,"
                + "SARIC_MetOfficeCapabilitiesXMLDOMReader,"
                + "Grids_Grid2DSquareCellDouble)";
        System.out.println("<" + methodName + ">");
        // Initial declaration
        Grids_Files gridf;
        gridf = ge.getFiles();
        File gdir;
        TreeMap<SARIC_YearMonth, TreeSet<SARIC_Date>> ymDates;
        SARIC_YearMonth ym;
        TreeSet<SARIC_Date> dates;
        Iterator<SARIC_YearMonth> ite0;
        String path;
        String s;
        File outdir0;
        File outdir1;
        File outdir2;
        File indir0;
        File indir1;
        File indir2;
        File gridsdir0;
        File gridsdir1;
        File gridsdir2;
        File gridsdir3;
        File[] indirs0;
        File[] indirs1;
        // Initial assignment
        System.out.println("Area " + area);
        path = "inspire/view/wmts0/" + area + "/" + layerName + "/EPSG_27700_";
        System.out.println("scale " + scale);
        indir0 = new File(Files.getInputDataMetOfficeDataPointDir(), path + scale);
        outdir0 = new File(Files.getOutputDataMetOfficeDataPointDir(), path + scale);
        gridsdir0 = new File(gp.getDirectory(), path + scale);
        ymDates = new TreeMap<>();
        indirs0 = indir0.listFiles();
        for (int i = 0; i < indirs0.length; i++) {
            s = indirs0[i].getName();
            ym = new SARIC_YearMonth(se, s);
            dates = new TreeSet<>();
            ymDates.put(ym, dates);
            indir1 = new File(indir0, s);
            indirs1 = indir1.listFiles();
            // initialise outdirs
            for (int j = 0; j < indirs1.length; j++) {
                dates.add(new SARIC_Date(se, indirs1[j].getName().split("T")[0]));
            }
        }

        Vector_Envelope2D tileBounds;
        boolean hoome = true;
        double weight;
        weight = 0.25d; // This is because observations are in mm per hour and we are dealing with 15 minute periods.
        String outDirName;
        String outDirNameCheck;
        String indirname;
        String indirname2;
        File[] indirs2;
        File[] infiles;
        String rowCol;
//        int row;
//        int col;
        double x;
        double y;
        double halfcellsize;
        halfcellsize = cellsize.doubleValue() / 2.0d;

        Grids_GridDouble g;
        File outascii;
        File outpng;
        File outpng2;
        File outpng3;
        Grids_GridDouble a1KMGridMaskedToCatchmentGrid;
        a1KMGridMaskedToCatchmentGrid = (Grids_GridDouble) a1KMGridMaskedToCatchment[0];
        SARIC_Date date0;

        Iterator<SARIC_Date> ite1;

        ite0 = ymDates.keySet().iterator();
        while (ite0.hasNext()) {
            ym = ite0.next();
            s = ym.getYYYYMM();
            dates = ymDates.get(ym);
            System.out.println(s);
            outdir1 = new File(outdir0, s);
            gridsdir1 = new File(gridsdir0, s);
            indir1 = new File(indir0, s);
            ite1 = dates.iterator();
            while (ite1.hasNext()) {
                date0 = ite1.next();
                s = date0.getYYYYMMDD();
                outdir2 = new File(outdir1, s);
                indir2 = new File(indir1, s);
                gridsdir2 = new File(gridsdir1, s);
                HashMap<String, Grids_GridDouble> grids;
                grids = new HashMap<>();
                outascii = new File(outdir2, s + layerName + ".asc");
                outpng2 = new File(outdir2, s + layerName + "Color.png");
                if (!overwrite && outpng2.exists()) {
                    System.out.println("Not overwriting and " + outascii.toString() + " exists.");
                } else {
                    outdir2.mkdirs();
                    indirs0 = indir2.listFiles();
                    if (indirs0 != null) {
                        for (int j = 0; j < indirs0.length; j++) {
                            if (!indirs0[j].isDirectory()) {
                                System.out.println(this.getClass().getName() + "." + methodName + ": "
                                        + "Input directory given by " + indirs0[j] + " is not a directory.");
                            } else {
                                indirname2 = indirs0[j].getName();
                                gridsdir3 = new File(gridsdir2, indirname2);
                                infiles = indirs0[j].listFiles();
                                if (infiles == null) {
                                    int DEBUG = 1;
                                } else {
                                    for (int l = 0; l < infiles.length; l++) {
                                        rowCol = infiles[l].getName().split(indirname2)[1];
                                        int[] rowColint;
                                        rowColint = getRowCol(rowCol);
                                        tileBounds = lp.getTileBounds(rowColint[0], rowColint[1]);
                                        System.out.println("Infile " + infiles[l]);
                                        g = getGrid(infiles[l], gridsdir3, cellsize, tileBounds, layerName, rowColint, hoome);
                                        if (g != null) {
                                            if (grids.containsKey(rowCol)) {
                                                Grids_GridDouble gridToAddTo;
                                                gridToAddTo = grids.get(rowCol);
                                                gp.addToGrid(gridToAddTo, g, weight);
                                            } else {
                                                grids.put(rowCol, g);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Iterator<String> gridsIte;
                    gridsIte = grids.keySet().iterator();
                    Grids_GridDouble b1KMGrid;
                    System.out.println("<Duplicate a1KMGrid>");
                    Grids_GridDoubleFactory f;
                    f = (Grids_GridDoubleFactory) a1KMGrid[1];
                    gdir = gridf.createNewFile(gridf.getGeneratedGridDoubleDir());
                    b1KMGrid = (Grids_GridDouble) f.create(gdir, (Grids_GridDouble) a1KMGrid[0]);
                    //b1KMGrid = (Grids_GridDouble) a1KMGrid[0];
                    double noDataValue = b1KMGrid.getNoDataValue();
                    System.out.println("</Duplicate a1KMGrid>");
                    double vb;
                    double v;
                    while (gridsIte.hasNext()) {
                        rowCol = gridsIte.next();
                        int[] rowColint;
                        rowColint = getRowCol(rowCol);
                        g = grids.get(rowCol);
                        //tileBounds = lp.getTileBounds(rowColint[0], rowColint[1]);
//                // If bounds intersect add
//                if (p.getBounds().getIntersects(tileBounds)) {
                        // Iterate over grid and get values
                        long nrows = b1KMGrid.getNRows();
                        long ncols = b1KMGrid.getNCols();
                        for (long row = 0; row < nrows; row++) {
                            //y = b1KMGrid.getCellYDouble(row, true);
                            y = b1KMGrid.getCellYDouble(row) + halfcellsize; // adding half a cellsize is in an attempt to prevent striping where images join.
                            for (long col = 0; col < ncols; col++) {
                                vb = a1KMGridMaskedToCatchmentGrid.getCell(row, col);
                                if (vb != noDataValue) {
                                    //x = b1KMGrid.getCellXDouble(col, true);
                                    x = b1KMGrid.getCellXDouble(col) + halfcellsize; // adding half a cellsize is in an attempt to prevent striping where images join.
                                    v = g.getCell(x, y);
                                    if (v != noDataValue) {
                                        //System.out.println("Value at (x, y) (" + x + ", " + y + ")= " + v);
                                        //b1KMGrid.setCell(row, col, v, true);
                                        b1KMGrid.addToCell(row, col, v);
                                    }
                                } else {
                                    //System.out.println("Out of study area.");
                                }
                            }
                        }
                        // Output as tiles
                        outascii = new File(outdir2,
                                layerName + "_" + rowColint[0] + "_" + rowColint[1] + ".asc");
                        outpng = new File(outdir2,
                                layerName + "_" + rowColint[0] + "_" + rowColint[1] + ".png");
                        outpng2 = new File(outdir2,
                                layerName + "_" + rowColint[0] + "_" + rowColint[1] + "Color.png");
                        ae.toAsciiFile(g, outascii);
                        ie.toGreyScaleImage(g, gp, outpng, "png");
                        ie.toColourImage(0, colorMap, noDataValueColor, g, outpng2, "png");
                    }
                    // Output result grid
                    outascii = new File(
                            outdir2,
                            s + layerName + ".asc");
                    outpng = new File(
                            outdir2,
                            s + layerName + ".png");
                    outpng2 = new File(
                            outdir2,
                            s + layerName + "Color.png");
                    outpng3 = new File(
                            outdir2,
                            s + layerName + "Color8.png");
                    ae.toAsciiFile(b1KMGrid, outascii);
                    ie.toGreyScaleImage(b1KMGrid, gp, outpng, "png");
                    ie.toColourImage(0, colorMap, noDataValueColor, b1KMGrid, outpng2, "png");
                    ie.toColourImage(8, colorMap, noDataValueColor, b1KMGrid, outpng3, "png");
                    init_gf();
                }
            }
        }
        System.out.println("</" + methodName + ">");
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

    /**
     *
     * @param indir
     * @param gridDir
     * @param cellsize
     * @param tileBounds
     * @param layerName
     * @param rowColint This has length 2 the first element is the row and the
     * second the column of the tile.
     * @param hoome
     * @return
     */
    public Grids_GridDouble getGrid(
            File indir,
            File gridDir,
            BigDecimal cellsize,
            Vector_Envelope2D tileBounds,
            String layerName,
            int[] rowColint,
            boolean hoome) {
        try {
            return getGrid(indir, gridDir, cellsize, tileBounds, layerName, rowColint);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                ge.clearMemoryReserve();
                ge.swapChunks(hoome);
                return getGrid(indir, gridDir, cellsize, tileBounds, layerName, rowColint, hoome);
            } else {
                throw e;
            }
        }
    }

    /**
     *
     * @param in
     * @param gridDir
     * @param cellsize
     * @param tileBounds
     * @param layerName
     * @param rowColint This has length 2 the first element is the row and the
     * second the column of the tile.
     * @return
     */
    public Grids_GridDouble getGrid(
            File in,
            File gridDir,
            BigDecimal cellsize,
            Vector_Envelope2D tileBounds,
            String layerName,
            int[] rowColint) {
        String methodName;
        methodName = "getGrid(File,BigDecimal,Vector_Envelope2D,String,int[])";
        Grids_GridDouble result = null;
        //boolean hoome = true;
        Image image = null;
        int width;
        int height;
        try {
            image = ImageIO.read(in);
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
            result = (Grids_GridDouble) gf.create(gridDir, height, width, dimensions);
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
                //System.out.println("row, col = " + row + ", " + col);
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
                Color pixel = new Color(pixels[i]);
                if (pixel.equals(Blue)) {
                    result.setCell(row, col, 0.25d);
                } else if (pixel.equals(LightBlue)) {
                    result.setCell(row, col, 0.75d);
                } else if (pixel.equals(MuddyGreen)) {
                    result.setCell(row, col, 1.5d);
                } else if (pixel.equals(Yellow)) {
                    result.setCell(row, col, 3d);
                } else if (pixel.equals(Orange)) {
                    result.setCell(row, col, 6d);
                } else if (pixel.equals(Red)) {
                    result.setCell(row, col, 12d);
                } else if (pixel.equals(Pink)) {
                    result.setCell(row, col, 24d);
                } else if (pixel.equals(PaleBlue)) {
                    result.setCell(row, col, 48d);
                } else if (pixel.equals(Color.BLACK)) {
                    result.setCell(row, col, 0.0d);
//                if (scale == 0) {
//                    if (row == height - 1 && col == 0) {
//                        // There is no lower resolution image.
//                        System.out.println(
//                                "Warning: missing data in " + in + "!!!!!");
//                        gp.addToGrid(result, 0.0d, HandleOutOfMemoryError);
//                        return result;
//                    } else {
////                        System.out.println(
////                                "Warning: missing data in " + in + " in "
////                                + "row " + row + ", col " + col + "!!!!!");
//                        result.setCell(row, col, 0.0d, HandleOutOfMemoryError);
//                    }
//                } else {
//                    if (row == height - 1 && col == 0) {
//                        System.out.println(
//                                "Warning: missing data in " + in + " in "
//                                + "row " + row + ", col " + col + ". "
//                                + "Getting lower resolution image.");
//                        
//                        int lowerResTilerow;
//                        int lowerResTilecol;
//                        double halfTilerow;
//                        halfTilerow = tilerow / 2.0d;
//                        double halfTilecol;
//                        halfTilecol = tilecol / 2.0d;
//                        lowerResTilerow = (int) Math.floor(halfTilerow);
//                        lowerResTilecol = (int) Math.floor(halfTilecol);
//                        //lowerResTilerow = (int) Math.ceil(halfTilerow);
//                        //lowerResTilecol = (int) Math.ceil(halfTilecol);
//                        int type;
//                        if (halfTilerow == lowerResTilerow) {
//                            if (halfTilecol == lowerResTilecol) {
//                                type = 0;
//                            } else {
//                                type = 1;
//                            }
//                        } else {
//                            if (halfTilecol == lowerResTilecol) {
//                                type = 2;
//                            } else {
//                                type = 3;
//                            }
//                        }
//                        File in2;
//                        in2 = in.getParentFile();
//                        File in3;
//                        in3 = in2.getParentFile();
//                        File in4;
//                        in4 = in3.getParentFile();
//                        String name2;
//                        name2 = in3.getName();
//                        String name3;
//                        name3 = name2.substring(0, name2.length() - Integer.toString(scale).length());
//                        name3 += scale - 1;
//                        String time;
//                        time = in2.getName();
//                        File in5 = new File(
//                                in4,
//                                name3);
//                        in5 = new File(
//                                in5,
//                                time);
////                    String[] inname;
////                    inname = in.getName().split("Z");
//                        in5 = new File(
//                                in5,
//                                layerName + name3 + time + "_" + lowerResTilerow + "_" + lowerResTilecol + ".png");
//                        Grids_GridDouble lowerResGrid;
//                        lowerResGrid = getGrid(in5, scale - 1, layerName, name);
//                        //C:\Users\geoagdt\src\saric\data\input\MetOffice\DataPoint\inspire\view\wmts\Wissey\RADAR_UK_Composite_Highres\EPSG_27700_3\2017-08-01T00_00_00Z\RADAR_UK_Composite_HighresEPSG_27700_32017-08-01T00_00_00Z_11_7.png
//                        //C:\Users\geoagdt\src\saric\data\input\MetOffice\DataPoint\inspire\view\wmts\Wissey\RADAR_UK_Composite_Highres\EPSG_27700_3\2017-08-01T00_00_00Z\RADAR_UK_Composite_HighresEPSG_27700_32017-08-01T00_00_00Z_11_7.png
//                        double value;
//                        for (int row2 = 0; row2 < 256; row2++) {
//                            for (int col2 = 0; col2 < 256; col2++) {
//                                switch (type) {
//                                    case 0:
//                                        value = lowerResGrid.getCell(
//                                                row2 / 2,
//                                                col2 / 2,
//                                                HandleOutOfMemoryError);
//                                        break;
//                                    case 1:
//                                        value = lowerResGrid.getCell(
//                                                (row2 / 2) + 128,
//                                                col2 / 2,
//                                                HandleOutOfMemoryError);
//                                        break;
//                                    case 2:
//                                        value = lowerResGrid.getCell(
//                                                row2 / 2,
//                                                (col2 / 2) + 128,
//                                                HandleOutOfMemoryError);
//                                        break;
//                                    default:
//                                        // type == 3
//                                        value = lowerResGrid.getCell(
//                                                (row2 / 2) + 128,
//                                                (col2 / 2) + 128,
//                                                HandleOutOfMemoryError);
//                                        break;
//                                }
//
//                                if (value != 0.0d) {
//                                    boolean getHere = true;
//                                }
//
//                                result.setCell(
//                                        row2,
//                                        col2,
//                                        value,
//                                        HandleOutOfMemoryError);
//                            }
//                        }
//                        System.out.println(result.toString(0, true));
//                        System.out.println("Max " + result.getStats(true).getMaxDouble(true));
//                        return result;
//                    } else {
////                        System.out.println(
////                                "Warning: missing data in " + in + " in "
////                                + "row " + row + ", col " + col + ". "
////                                + "Getting lower resolution image.");
//                        result.setCell(row, col, 0.0d, HandleOutOfMemoryError);
//                    }
//                }
                } else {
                    result.setCell(row, col, 0.0d);
                }
                col++;
            }

            // Describe result
            //System.out.println(result.toString(0, HandleOutOfMemoryError));
        } catch (NullPointerException e) {
            System.out.println("File " + in.toString() + " exists in "
                    + this.getClass().getName() + "." + methodName
                    + ", but is empty or there is some other problem with it "
                    + "being loaded as an image, returning null.");
        }
        return result;
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
    protected double getEstimate(SARIC_SiteForecastRecord r) {
        int weatherType;
        weatherType = r.getWeatherType();
        int precipitationProbability;
        precipitationProbability = r.getPrecipitationProbability();
        if ((weatherType >= 9 && weatherType <= 12) || (weatherType >= 19 && weatherType <= 24 && weatherType != 21)) {
            return 0.55 * precipitationProbability / 100.0d;
        } else if ((weatherType >= 16 && weatherType <= 18) || weatherType == 21) {
            return 0.055 * precipitationProbability / 100.0d;
        } else if ((weatherType >= 13 && weatherType <= 15) || (weatherType >= 25 && weatherType <= 30)) {
            return 5.5 * precipitationProbability / 100.0d;
        } else {
            return 0.0d;
        }
    }

}
