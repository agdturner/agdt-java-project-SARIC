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
import uk.ac.leeds.ccg.andyt.grids.core.Grids_AbstractGrid2DSquareCellDoubleChunkFactory;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_AbstractGridStatistics;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Grid2DSquareCellDouble;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Grid2DSquareCellDoubleFactory;
import uk.ac.leeds.ccg.andyt.grids.exchange.Grids_ESRIAsciiGridExporter;
import uk.ac.leeds.ccg.andyt.grids.exchange.Grids_ImageExporter;
import uk.ac.leeds.ccg.andyt.grids.process.Grid2DSquareCellProcessor;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Environment;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Object;
import uk.ac.leeds.ccg.andyt.projects.saric.data.catchment.SARIC_Teifi;
import uk.ac.leeds.ccg.andyt.projects.saric.data.catchment.SARIC_Wissey;
import uk.ac.leeds.ccg.andyt.projects.saric.data.metoffice.datapoint.SARIC_MetOfficeCapabilitiesXMLDOMReader;
import uk.ac.leeds.ccg.andyt.projects.saric.data.metoffice.datapoint.SARIC_MetOfficeLayerParameters;
import uk.ac.leeds.ccg.andyt.projects.saric.data.metoffice.datapoint.SARIC_MetOfficeParameters;
import uk.ac.leeds.ccg.andyt.projects.saric.io.SARIC_Files;
import uk.ac.leeds.ccg.andyt.projects.saric.util.SARIC_Time;
import uk.ac.leeds.ccg.andyt.vector.geometry.Vector_Envelope2D;

/**
 *
 * @author geoagdt
 */
public class SARIC_ImageProcessor extends SARIC_Object implements Runnable {

    /**
     * For convenience
     */
    SARIC_Files sf;
    Grids_Environment ge;
    Grid2DSquareCellProcessor gp;
    Grids_Grid2DSquareCellDoubleFactory f;
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
        sf = se.getFiles();
        ge = se.getGrids_Environment();
        ae = new Grids_ESRIAsciiGridExporter(ge);
        ie = new Grids_ImageExporter(ge);
        gp = ge.get_Grid2DSquareCellProcessor();
        f = new Grids_Grid2DSquareCellDoubleFactory(
                gp.get_Directory(doTileFromWMTSService),
                256,
                256,
                (Grids_AbstractGrid2DSquareCellDoubleChunkFactory) gp._Grid2DSquareCellDoubleChunkArrayFactory,
                ge,
                true
        );
        f = gp._Grid2DSquareCellDoubleFactory;
    }

    public void run() {

        if (doTileFromWMTSService) {
            File indir;
            File[] indirs;
            File infile;
            File outdir;
            String path;
            String name;
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
            SARIC_Wissey sw;
            SARIC_Teifi st;
            String s;
            SARIC_Time time;

            inspireWMTSCapabilities = sf.getInputDataMetOfficeDataPointInspireViewWmtsCapabilitiesFile();
            p = new SARIC_MetOfficeParameters();
            r = new SARIC_MetOfficeCapabilitiesXMLDOMReader(se, inspireWMTSCapabilities);
            tileMatrixSet = "EPSG:27700"; // British National Grid
            sw = se.getWissey();
            st = se.getTeifi();

            if (doForecastsTileFromWMTSService) {
                TreeMap<SARIC_Time, String> orderedOutDirNames;
                TreeMap<SARIC_Time, File> orderedIndirs;

                layerName = "Precipitation_Rate";
                for (int scale = 4; scale < 5; scale++) {
                    tileMatrix = tileMatrixSet + ":" + scale;
                    metOfficeLayerParameters = p.getMetOfficeLayerParameters();
                    SARIC_MetOfficeLayerParameters lp;
                    lp = metOfficeLayerParameters.get(tileMatrix);
                    cellsize = r.getCellsize(tileMatrix);
                    if (lp == null) {
                        lp = new SARIC_MetOfficeLayerParameters(se, cellsize, p);
                    }
                    nrows = r.getNrows(tileMatrix);
                    ncols = r.getNcols(tileMatrix);
                    bounds = r.getDimensions(cellsize, nrows, ncols, tileMatrix, p.TwoFiveSix);
                    System.out.println(bounds.toString());
                    p.setBounds(bounds);
                    if (doWissey) {
                        area = "Wissey";
                        System.out.println("Area " + area);
                        path = "inspire/view/wmts/" + area + "/" + layerName + "/EPSG_27700_";
                        System.out.println("scale " + scale);
                        indir = new File(
                                sf.getInputDataMetOfficeDataPointDir(),
                                path + scale);
                        indirs = indir.listFiles();
                        /**
                         * indirs should contain data for all dates. The data
                         * for some dates actually refers to forecasts for the
                         * following day or even for the day after that as the
                         * forecasts are for the following 36 hours.
                         *
                         * It is imperative to have an ordered list of
                         * directories for each date, so we create one when
                         * initialising outdirs as this already involves going
                         * through indirs.
                         */
                        orderedIndirs = new TreeMap<SARIC_Time, File>();
                        orderedOutDirNames = new TreeMap<SARIC_Time, String>();
                        for (int j = 0; j < indirs.length; j++) {
                            s = indirs[j].getName();
                            time = new SARIC_Time(s);
                            orderedOutDirNames.put(time, s);
                            orderedIndirs.put(time, indirs[j]);
                        }
                        outdir = new File(
                                sf.getOutputDataMetOfficeDataPointDir(),
                                path + scale);
                        name = layerName;
//                    p.setBounds(sw.getBounds());
                        Grids_Grid2DSquareCellDouble swg;
                        swg = sw.get1KMGrid();
                        processForecasts(orderedIndirs, orderedOutDirNames, outdir, layerName, name, cellsize, p, lp, r, swg);
                    }

                }
            }

            if (doObservationsTileFromWMTSService) {
                HashSet<String> outdirNames;
                layerName = "RADAR_UK_Composite_Highres";
                for (int scale = 4; scale < 5; scale++) {
                    tileMatrix = tileMatrixSet + ":" + scale;
                    metOfficeLayerParameters = p.getMetOfficeLayerParameters();
                    SARIC_MetOfficeLayerParameters lp;
                    lp = metOfficeLayerParameters.get(tileMatrix);
                    cellsize = r.getCellsize(tileMatrix);
                    if (lp == null) {
                        lp = new SARIC_MetOfficeLayerParameters(se, cellsize, p);
                    }
                    nrows = r.getNrows(tileMatrix);
                    ncols = r.getNcols(tileMatrix);
                    bounds = r.getDimensions(cellsize, nrows, ncols, tileMatrix, p.TwoFiveSix);
                    System.out.println(bounds.toString());
                    p.setBounds(bounds);
                    if (doWissey) {
                        area = "Wissey";
                        System.out.println("Area " + area);
                        path = "inspire/view/wmts0/" + area + "/" + layerName + "/EPSG_27700_";
                        System.out.println("scale " + scale);
                        indir = new File(
                                sf.getInputDataMetOfficeDataPointDir(),
                                path + scale);
                        indirs = indir.listFiles();
                        // initialise outdirs
                        outdirNames = new HashSet<String>();
                        for (int j = 0; j < indirs.length; j++) {
                            outdirNames.add(indirs[j].getName());
                        }
                        outdir = new File(
                                sf.getOutputDataMetOfficeDataPointDir(),
                                path + scale);
                        name = layerName;
//                    p.setBounds(sw.getBounds());
                        Grids_Grid2DSquareCellDouble swg;
                        swg = sw.get1KMGrid();
                        processObservations(indirs, outdirNames, outdir, layerName, name, cellsize, p, lp, r, swg);
                    }
                    if (doTeifi) {
                        area = "Teifi";
                        System.out.println("Area " + area);
                        path = "inspire/view/wmts0/" + area + "/" + layerName + "/EPSG_27700_";
                        System.out.println("scale " + scale);
                        indir = new File(
                                sf.getInputDataMetOfficeDataPointDir(),
                                path + scale);
                        indirs = indir.listFiles();
                        // initialise outdirs
                        outdirNames = new HashSet<String>();
                        for (int j = 0; j < indirs.length; j++) {
                            outdirNames.add(indirs[j].getName().split("T")[0]);
                        }
                        outdir = new File(
                                sf.getOutputDataMetOfficeDataPointDir(),
                                path + scale);
                        name = layerName;
//                    p.setBounds(st.getBounds());
                        Grids_Grid2DSquareCellDouble stg;
                        stg = st.get1KMGrid();
                        processObservations(indirs, outdirNames, outdir, layerName, name, cellsize, p, lp, r, stg);
                    }
                }
            }
        }
    }

    /**
     *
     * @param orderedForecastdirs
     * @param orderedOutdirNames
     * @param outdir
     * @param layerName
     * @param name
     * @param cellsize
     * @param p
     * @param lp
     * @param r
     * @param a1KMGrid
     */
    public void processForecasts(
            TreeMap<SARIC_Time, File> orderedForecastdirs,
            TreeMap<SARIC_Time, String> orderedOutdirNames,
            File outdir,
            String layerName,
            String name,
            BigDecimal cellsize,
            SARIC_MetOfficeParameters p,
            SARIC_MetOfficeLayerParameters lp,
            SARIC_MetOfficeCapabilitiesXMLDOMReader r,
            Grids_Grid2DSquareCellDouble a1KMGrid) {
        String methodName;
        methodName = "processForecasts("
                + "TreeMap<SARIC_Time, File>,"
                + "TreeMap<SARIC_Time, String>,"
                + "HashSet<String>,FileString,String,"
                + "BigDecimal,SARIC_MetOfficeParameters,"
                + "SARIC_MetOfficeLayerParameters,"
                + "SARIC_MetOfficeCapabilitiesXMLDOMReader,"
                + "Grids_Grid2DSquareCellDouble)";
        Vector_Envelope2D tileBounds;
        Boolean HandleOutOfMemoryError = true;
        double weight;
        weight = 1d;
        String outDirName;
        String outDirNameCheck;
        String indirname;
        String indirname2;
        File[] indirs2;
        File[] infiles;
        File[] unorderedForecastdirs2;
        File indir;
        String rowCol;
//        int row;
//        int col;
        double x;
        double y;
        double halfcellsize;
        halfcellsize = cellsize.doubleValue() / 2.0d;
        Grids_Grid2DSquareCellDouble g;
        File outascii;
        File outpng;
        SARIC_Time time;
        SARIC_Time time2;
        SARIC_Time time3;
        SARIC_Time time4;
        Iterator<SARIC_Time> ite;
        ite = orderedOutdirNames.keySet().iterator();
        /**
         * With a full set of forecasts there are essentially 7 forecasts of
         * rainfall for each 3 hourly time snapshot. However, there is a need to
         * start and end somewhere, so for the first dates we only have the
         * forecasts from that time going forwards and likewise for the most
         * recent dates, there may be more forecasts to come...
         */
        HashMap<SARIC_Time, Grids_Grid2DSquareCellDouble> output1kmGrids;
        output1kmGrids = new HashMap<SARIC_Time, Grids_Grid2DSquareCellDouble>();
        HashMap<SARIC_Time, HashMap<String, Grids_Grid2DSquareCellDouble>> gridsAll;
        gridsAll = new HashMap<SARIC_Time, HashMap<String, Grids_Grid2DSquareCellDouble>>();
        HashMap<String, Grids_Grid2DSquareCellDouble> grids;
        HashMap<SARIC_Time, Integer> counts;
        counts = new HashMap<SARIC_Time, Integer>();
        File outdir2;
        while (ite.hasNext()) {
            time = ite.next();
            /**
             * At the end of this iteration we should be able to output the
             * grids from this time as everything that can be added will be
             * added by this stage. Also the grids can be set to null to free
             * memory.
             */
            outDirName = orderedOutdirNames.get(time); // outDirName could also be derived from date perhaps via a toString() method!
            System.out.println(outDirName);
            outdir2 = new File(
                    outdir,
                    outDirName);
            outascii = new File(
                    outdir2,
                    name + ".asc");
            if (!overwrite && outascii.exists()) {
                System.out.println("Not overwriting and " + outascii.toString() + " exists.");
            } else {
                /**
                 * In the fullness of time (assuming all the forecasts are
                 * collected), there should be four indirs, one for each of the
                 * 4 forecasts made for the proceeding 36 hours. The scheduled
                 * forecasts are from 3am, 9am, 3pm and 9pm each day. So if
                 * there are fewer than 4 directories, the processing could be
                 * for the current date, rather than being processing for a day
                 * in the past for which the generalised data are still wanted
                 * for validation purposes.
                 */
                outdir2.mkdirs();
                indir = orderedForecastdirs.get(time);
                unorderedForecastdirs2 = indir.listFiles();

                TreeMap<SARIC_Time, File> orderedForecastDirs2;
                orderedForecastDirs2 = new TreeMap<SARIC_Time, File>();
                for (int i = 0; i < unorderedForecastdirs2.length; i++) {
                    time3 = new SARIC_Time(unorderedForecastdirs2[i].getName());
                    orderedForecastDirs2.put(time3, unorderedForecastdirs2[i]);
                }

                Iterator<SARIC_Time> ite2;
                ite2 = orderedForecastDirs2.keySet().iterator();
                while (ite2.hasNext()) {
                    time3 = ite2.next();
                    File dir;
                    dir = orderedForecastDirs2.get(time3);
                    
                    if (dir == null) {
                        int debug = 1;
                    dir = orderedForecastDirs2.get(time3);
                    dir = orderedForecastDirs2.get(time3);
                    dir = orderedForecastDirs2.get(time3);
                        
                    }
                    
                    indirname2 = dir.getName();
                    for (int k = 0; k <= 36; k += 3) {
                        time2 = new SARIC_Time(time3);
                        time2.addHours(k);
                        /**
                         * Set time4 to be the right day. There is only a need
                         * to add up to 2 days as only looking forward 36 hours.
                         */
                        time4 = new SARIC_Time(time3);
                        if (time2.getDayOfMonth() != time4.getDayOfMonth()) {
                            time4.addDays(1);
                            if (time2.getDayOfMonth() != time4.getDayOfMonth()) {
                                time4.addDays(1);
                            }
                        }
                        // Initialise grids and counts
                        if (gridsAll.containsKey(time4)) {
                            grids = gridsAll.get(time4);
                        } else {
                            grids = new HashMap<String, Grids_Grid2DSquareCellDouble>();
                            gridsAll.put(time4, grids);
                        }
                        File indir3;
                        indir3 = new File(
                                dir,
                                "" + k);
                        infiles = indir3.listFiles();
                        if (infiles == null) {
                            int DEBUG = 1;
                        } else {
                            for (int i = 0; i < infiles.length; i++) {
                                rowCol = infiles[i].getName().split(indirname2)[1];
                                int[] rowColint;
                                rowColint = getRowCol(rowCol);
                                tileBounds = lp.getTileBounds(rowColint[0], rowColint[1]);
                                System.out.println("Infile " + infiles[i]);
                                g = getGrid(infiles[i], cellsize, tileBounds, layerName, rowColint);
                                if (g != null) {
                                    if (grids.containsKey(rowCol)) {
                                        Grids_Grid2DSquareCellDouble gridToAddTo;
                                        gridToAddTo = grids.get(rowCol);
                                        gp.addToGrid(gridToAddTo, g, weight, true);
                                    } else {
                                        grids.put(rowCol, g);
                                    }
                                }
                            }
                            if (counts.containsKey(time4)) {
                                counts.put(time4, counts.get(time4) + 1);
                            } else {
                                counts.put(time4, 1);
                            }
                        }
                    }
                }
                ite2 = orderedForecastDirs2.keySet().iterator();
                while (ite2.hasNext()) {
                    time3 = ite2.next();
                    grids = gridsAll.get(time3);
                    Iterator<String> gridsIte;
                    gridsIte = grids.keySet().iterator();
                    Grids_Grid2DSquareCellDouble b1KMGrid = null;
                    if (output1kmGrids.containsKey(time3)) {
                        b1KMGrid = output1kmGrids.get(time3);
                    } else {
                        b1KMGrid = (Grids_Grid2DSquareCellDouble) f.create(a1KMGrid);
                        output1kmGrids.put(time3, b1KMGrid);
                    }
                    long nrows = b1KMGrid.get_NRows(true);
                    long ncols = b1KMGrid.get_NCols(true);
                    while (gridsIte.hasNext()) {
                        rowCol = gridsIte.next();
                        int[] rowColint;
                        rowColint = getRowCol(rowCol);
                        g = grids.get(rowCol);
                        // Iterate over grid and get values
                        for (long row = 0; row < nrows; row++) {
                            y = b1KMGrid.getCellYDouble(row, true) + halfcellsize; // adding half a cellsize is in an attempt to prevent striping where images join.
                            for (long col = 0; col < ncols; col++) {
                                x = b1KMGrid.getCellXDouble(col, true) + halfcellsize; // adding half a cellsize is in an attempt to prevent striping where images join.
                                b1KMGrid.addToCell(row, col, g.getCell(x, y, true), true);
                            }
                        }
                    }
                }
            }
            g = output1kmGrids.get(time);
            Grids_AbstractGridStatistics gs;
            gs = g.getGridStatistics(true);
            double max = gs.getMaxDouble(true);
            double min = gs.getMinDouble(true);
            /**
             * The scaleFactor is the number of grids divided by the number of hours in the day
             */
            double scaleFactor = counts.get(time) / 24d; 
            g = gp.rescale(g, null, min * scaleFactor, max * scaleFactor, true);
            ae.toAsciiFile(g, outascii, HandleOutOfMemoryError);
            outpng = new File(
                    outdir2,
                    name + ".png");
            ie.toGreyScaleImage(g, gp, outpng, "png", HandleOutOfMemoryError);
            /**
             * Having output, we can now clear some space
             */
            output1kmGrids.remove(time);
            gridsAll.remove(time);
        }
    }

    public void processObservations(
            File[] indirs,
            HashSet<String> outdirNames,
            File outdir,
            String layerName,
            String name,
            BigDecimal cellsize,
            SARIC_MetOfficeParameters p,
            SARIC_MetOfficeLayerParameters lp,
            SARIC_MetOfficeCapabilitiesXMLDOMReader r,
            Grids_Grid2DSquareCellDouble a1KMGrid) {
        String methodName;
        methodName = "processObservations(File[],HashSet<String>,FileString,"
                + "String,BigDecimal,SARIC_MetOfficeParameters,"
                + "SARIC_MetOfficeLayerParameters,"
                + "SARIC_MetOfficeCapabilitiesXMLDOMReader,"
                + "Grids_Grid2DSquareCellDouble)";
        Vector_Envelope2D tileBounds;
        Boolean HandleOutOfMemoryError = true;
        double weight;
        weight = 0.25d; // This is because observations are in mm per hour and we are dealing with 15 minute periods
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
        Grids_Grid2DSquareCellDouble g;
        File outascii;
        File outpng;
        Iterator<String> outdirNamesIte;
        outdirNamesIte = outdirNames.iterator();
        while (outdirNamesIte.hasNext()) {
            HashMap<String, Grids_Grid2DSquareCellDouble> grids;
            grids = new HashMap<String, Grids_Grid2DSquareCellDouble>();
            outDirName = outdirNamesIte.next();
            System.out.println(outDirName);
            File outdir2;
            outdir2 = new File(
                    outdir,
                    outDirName);
            outascii = new File(
                    outdir2,
                    name + ".asc");
            if (!overwrite && outascii.exists()) {
                System.out.println("Not overwriting and " + outascii.toString() + " exists.");
            } else {
                outdir2.mkdirs();
                for (int k = 0; k < indirs.length; k++) {
                    indirname = indirs[k].getName();
                    outDirNameCheck = indirname;
                    if (outDirName.equalsIgnoreCase(outDirNameCheck)) {
                        indirs2 = indirs[k].listFiles();
                        if (indirs2 != null) {
                            for (int j = 0; j < indirs2.length; j++) {
                                if (!indirs2[j].isDirectory()) {
                                    System.out.println(this.getClass().getName() + "." + methodName + ": "
                                            + "Input directory given by " + indirs2[j] + " is not a directory.");
                                } else {
                                    indirname2 = indirs2[j].getName();
                                    infiles = indirs2[j].listFiles();

                                    if (infiles == null) {
                                        int DEBUG = 1;
                                    } else {
                                        for (int i = 0; i < infiles.length; i++) {
                                            rowCol = infiles[i].getName().split(indirname2)[1];
                                            int[] rowColint;
                                            rowColint = getRowCol(rowCol);
                                            tileBounds = lp.getTileBounds(rowColint[0], rowColint[1]);
                                            System.out.println("Infile " + infiles[i]);
                                            g = getGrid(infiles[i], cellsize, tileBounds, layerName, rowColint);
                                            if (g != null) {
                                                if (grids.containsKey(rowCol)) {
                                                    Grids_Grid2DSquareCellDouble gridToAddTo;
                                                    gridToAddTo = grids.get(rowCol);
                                                    gp.addToGrid(gridToAddTo, g, weight, true);
                                                } else {
                                                    grids.put(rowCol, g);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                Iterator<String> gridsIte;
                gridsIte = grids.keySet().iterator();
                Grids_Grid2DSquareCellDouble b1KMGrid = null;
                b1KMGrid = (Grids_Grid2DSquareCellDouble) f.create(a1KMGrid);
                while (gridsIte.hasNext()) {
                    rowCol = gridsIte.next();
                    int[] rowColint;
                    rowColint = getRowCol(rowCol);
                    g = grids.get(rowCol);
                    tileBounds = lp.getTileBounds(rowColint[0], rowColint[1]);
//                // If bounds intersect add
//                if (p.getBounds().getIntersects(tileBounds)) {
                    // Iterate over grid and get values
                    long nrows = b1KMGrid.get_NRows(true);
                    long ncols = b1KMGrid.get_NCols(true);
                    for (long row = 0; row < nrows; row++) {
                        y = b1KMGrid.getCellYDouble(row, true) + halfcellsize; // adding half a cellsize is in an attempt to prevent striping where images join.
                        for (long col = 0; col < ncols; col++) {
                            x = b1KMGrid.getCellXDouble(col, true) + halfcellsize; // adding half a cellsize is in an attempt to prevent striping where images join.
                            b1KMGrid.addToCell(row, col, g.getCell(x, y, true), true);
                        }
                    }
//                }
                    // Output as tiles
                    outascii = new File(
                            outdir2,
                            name + "_" + rowColint[0] + "_" + rowColint[1] + ".asc");
                    outpng = new File(
                            outdir2,
                            name + "_" + rowColint[0] + "_" + rowColint[1] + ".png.png");
                    ae.toAsciiFile(g, outascii, HandleOutOfMemoryError);
                    ie.toGreyScaleImage(g, gp, outpng, "png", HandleOutOfMemoryError);
                }
                // Output result grid
                outascii = new File(
                        outdir2,
                        name + ".asc");
                outpng = new File(
                        outdir2,
                        name + ".png");
                ae.toAsciiFile(b1KMGrid, outascii, HandleOutOfMemoryError);
                ie.toGreyScaleImage(b1KMGrid, gp, outpng, "png", HandleOutOfMemoryError);
            }
        }
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
     * @param in
     * @param cellsize
     * @param tileBounds
     * @param layerName
     * @param rowColint This has length 2 the first element is the row and the
     * second the column of the tile.
     * @return
     */
    public Grids_Grid2DSquareCellDouble getGrid(
            File in,
            BigDecimal cellsize,
            Vector_Envelope2D tileBounds,
            String layerName,
            int[] rowColint) {
        String methodName;
        methodName = "getGrid(File,BigDecimal,Vector_Envelope2D,String,int[])";
        Grids_Grid2DSquareCellDouble result = null;
        Boolean HandleOutOfMemoryError = true;
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

            BigDecimal[] dimensions;
            dimensions = new BigDecimal[5];
            dimensions[0] = cellsize;
            dimensions[1] = tileBounds._xmin;
            dimensions[2] = tileBounds._ymin;
            dimensions[3] = tileBounds._xmax;
            dimensions[4] = tileBounds._ymax;
//        dimensions[1] = tileBounds._xmin.subtract(cellsize.multiply(new BigDecimal(rowColint[1]).multiply(new BigDecimal(height)))); //XMIN
//        dimensions[4] = tileBounds._ymax.subtract(cellsize.multiply(new BigDecimal(rowColint[0]).multiply(new BigDecimal(width)))); //YMAX
//        dimensions[2] = dimensions[4].subtract(cellsize.multiply(new BigDecimal(height))); //YMIN
//        dimensions[3] = dimensions[1].subtract(cellsize.multiply(new BigDecimal(width)));  //XMAX

            result = (Grids_Grid2DSquareCellDouble) f.create(height, width, dimensions);

            int[] pixels = new int[width * height];
            PixelGrabber pg = new PixelGrabber(image, 0, 0, width, height, pixels, 0, width);
            try {
                pg.grabPixels();
            } catch (InterruptedException ie) {
                ie.printStackTrace();
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
                    result.setCell(row, col, 0.25d, HandleOutOfMemoryError);
                } else if (pixel.equals(LightBlue)) {
                    result.setCell(row, col, 0.5d, HandleOutOfMemoryError);
                } else if (pixel.equals(MuddyGreen)) {
                    result.setCell(row, col, 1.5d, HandleOutOfMemoryError);
                } else if (pixel.equals(Yellow)) {
                    result.setCell(row, col, 3d, HandleOutOfMemoryError);
                } else if (pixel.equals(Orange)) {
                    result.setCell(row, col, 6d, HandleOutOfMemoryError);
                } else if (pixel.equals(Red)) {
                    result.setCell(row, col, 12d, HandleOutOfMemoryError);
                } else if (pixel.equals(Pink)) {
                    result.setCell(row, col, 24d, HandleOutOfMemoryError);
                } else if (pixel.equals(PaleBlue)) {
                    result.setCell(row, col, 48d, HandleOutOfMemoryError);
                } else if (pixel.equals(Color.BLACK)) {
                    result.setCell(row, col, 0.0d, HandleOutOfMemoryError);
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
//                        Grids_Grid2DSquareCellDouble lowerResGrid;
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
//                        System.out.println("Max " + result.getGridStatistics(true).getMaxDouble(true));
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
                    result.setCell(row, col, 0.0d, HandleOutOfMemoryError);
                }
                col++;
            }

            // Describe result
            System.out.println(result.toString(0, HandleOutOfMemoryError));
        } catch (NullPointerException e) {
            System.out.println("File " + in.toString() + " exists in "
                    + this.getClass().getName() + "." + methodName
                    + ", but is empty or there is some other problem with it "
                    + "being loaded as an image, returning null.");
        }
        return result;
    }
}
