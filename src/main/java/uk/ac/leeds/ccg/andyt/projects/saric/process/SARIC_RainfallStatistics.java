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
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;
import uk.ac.leeds.ccg.andyt.generic.io.Generic_StaticIO;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Dimensions;
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
public class SARIC_RainfallStatistics extends SARIC_Object implements Runnable {

    /**
     * For convenience
     */
    SARIC_Files sf;
    SARIC_Strings ss;
    Grids_Environment ge;
    Grids_Processor gp;
    Grids_GridDoubleFactory gf;
    double noDataValue = -9999.0;
    Grids_ESRIAsciiGridExporter ae;
    Grids_ImageExporter ImageExporter;
    File dirIn;
    File dirOut;
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

    public SARIC_RainfallStatistics(
            SARIC_Environment se,
            File dirIn,
            File dirOut,
            boolean doWissey,
            boolean doTeifi,
            boolean overwrite
    ) {
        this.se = se;
        this.dirIn = dirIn;
        this.dirOut = dirOut;
        this.doWissey = doWissey;
        this.doTeifi = doTeifi;
        this.overwrite = overwrite;
        sf = se.getFiles();
        ss = se.getStrings();
        ge = se.getGrids_Environment();
        ae = new Grids_ESRIAsciiGridExporter(ge);
        ImageExporter = new Grids_ImageExporter(ge);
        gp = ge.getProcessor();
        init_gf();
    }

    private void init_gf() {
        gf = new Grids_GridDoubleFactory(ge, gp.GridChunkDoubleFactory,
                gp.DefaultGridChunkDoubleFactory, noDataValue, 256, 256,
                new Grids_Dimensions(256, 256),
                new Grids_GridDoubleStatsNotUpdated(ge));
        gf.setNoDataValue(noDataValue);
        gp.GridDoubleFactory = gf;
    }

    @Override
    public void run() {
        SARIC_Colour sc;
        sc = new SARIC_Colour(se);
        TreeMap<Double, Color> colorMap;
        colorMap = sc.getVarianceColorMap();
        Color noDataValueColor;
        noDataValueColor = Color.BLACK;
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
        inspireWMTSCapabilities = sf.getInputDataMetOfficeDataPointInspireViewWmtsCapabilitiesFile();
        p = new SARIC_MetOfficeParameters();
        r = new SARIC_MetOfficeCapabilitiesXMLDOMReader(se, inspireWMTSCapabilities);
        tileMatrixSet = "EPSG:27700"; // British National Grid
        // Initialisation for Wissey
        SARIC_Wissey sw = null;
        Object[] sw1KMGrid = null;
        Object[] sw1KMGridMaskedToCatchment = null;
        if (doWissey) {
            sw = se.getWissey();
            sw1KMGrid = sw.get1KMGrid("1KMGrid");
            sw1KMGridMaskedToCatchment = sw.get1KMGridMaskedToCatchment();
        }
        // Initialisation for Wissey
        SARIC_Teifi st = null;
        Object[] st1KMGrid = null;
        Object[] st1KMGridMaskedToCatchment = null;
        if (doTeifi) {
            st = se.getTeifi();
            st1KMGrid = st.get1KMGrid("1KMGrid");
            st1KMGridMaskedToCatchment = st.get1KMGridMaskedToCatchment();
        }
        layerName = ss.getString_RADAR_UK_Composite_Highres();
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
                area = ss.getString_Wissey();
                processObservations(
                        colorMap, noDataValueColor, area, scale, layerName, cellsize, p, lp, r, sw1KMGrid, sw1KMGridMaskedToCatchment);
            }
            if (doTeifi) {
                area = ss.getString_Teifi();
                processObservations(
                        colorMap, noDataValueColor, area, scale, layerName, cellsize, p, lp, r, st1KMGrid, st1KMGridMaskedToCatchment);
            }
        }
    }

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
        File[] indirs0;
        File[] indirs1;
        // Initial assignment
        System.out.println("Area " + area);
        path = "inspire/view/wmts0/" + area + "/" + layerName + "/EPSG_27700_";
        System.out.println("scale " + scale);
        indir0 = new File(
                sf.getInputDataMetOfficeDataPointDir(),
                path + scale);
        outdir0 = new File(
                sf.getOutputDataMetOfficeDataPointDir(),
                path + scale);
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
        Boolean HandleOutOfMemoryError = true;
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
        TreeMap<SARIC_Time, HashMap<String, Grids_GridDouble>> atg;
        HashMap<String, Grids_GridDouble> variances;
        HashMap<String, Grids_GridDouble> tg;
        SARIC_Time st;
        double n;
        n = 0;

        File outascii;
        File outpng;
        File outpng2;
        File outpng3;
        Grids_GridDouble a1KMGridMaskedToCatchmentGrid;
        a1KMGridMaskedToCatchmentGrid = (Grids_GridDouble) a1KMGridMaskedToCatchment[0];
        SARIC_Date date0;

        // Number of times it is raining in each 15 minute observations
        Iterator<SARIC_Date> ite1;

        ite0 = ymDates.keySet().iterator();
        while (ite0.hasNext()) {

            ym = ite0.next();
            s = ym.getYYYYMM();
            dates = ymDates.get(ym);
            System.out.println(s);
            outdir1 = new File(
                    outdir0,
                    s);
            indir1 = new File(
                    indir0,
                    s);
            ite1 = dates.iterator();
            while (ite1.hasNext()) {

                // New map for each day
                atg = new TreeMap<>();

                date0 = ite1.next();
                s = date0.getYYYYMMDD();
                outdir2 = new File(
                        outdir1,
                        s);
                indir2 = new File(
                        indir1,
                        s);

                HashMap<String, Grids_GridDouble> grids;
                grids = new HashMap<>();

                outascii = new File(
                        outdir2,
                        s + layerName + "Max.asc");
                outpng2 = new File(
                        outdir2,
                        s + layerName + "Color.png");
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
                                infiles = indirs0[j].listFiles();

                                st = new SARIC_Time(se, indirname2, ss.symbol_minus, ss.string_T, ss.symbol_underscore);
                                if (atg.containsKey(st)) {
                                    tg = atg.get(st);
                                } else {
                                    tg = new HashMap<>();
                                    atg.put(st, tg);
                                }

                                if (infiles == null) {
                                    int DEBUG = 1;
                                } else {
                                    for (int l = 0; l < infiles.length; l++) {
                                        rowCol = infiles[l].getName().split(indirname2)[1];
                                        int[] rowColint;
                                        rowColint = getRowCol(rowCol);
                                        tileBounds = lp.getTileBounds(rowColint[0], rowColint[1]);
                                        System.out.println("Infile " + infiles[l]);
                                        g = getGrid(infiles[l], cellsize, tileBounds, layerName, rowColint);
                                        if (g != null) {
                                            tg.put(rowCol, g);
                                            if (grids.containsKey(rowCol)) {
                                                Grids_GridDouble gridToAddTo;
                                                gridToAddTo = grids.get(rowCol);
                                                gp.addToGrid(gridToAddTo, g, weight);
                                                n++;
                                            } else {
                                                grids.put(rowCol, g);
                                                n++;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    long NRows = 0;
                    long NCols = 0;
                    n /= 4;
                    System.out.println("n " + n);
                    // Do Statistics
                    Grids_GridDouble sum;
                    variances = new HashMap<>();
                    Grids_GridDouble variance = null;
                    Iterator<SARIC_Time> iteT;
                    Iterator<String> iteT2;
                    iteT = atg.keySet().iterator();
                    while (iteT.hasNext()) {
                        st = iteT.next();
                        tg = atg.get(st);
                        iteT2 = tg.keySet().iterator();
                        while (iteT2.hasNext()) {
                            rowCol = iteT2.next();
                            sum = grids.get(rowCol);
                            NRows = sum.getNRows();
                            NCols = sum.getNCols();
                            if (variances.containsKey(rowCol)) {
                                variance = variances.get(rowCol);
                            } else {
                                gdir = gridf.createNewFile(gridf.getGeneratedGridDoubleDir());
                                variance = gf.create(gdir,
                                        NRows, NCols, sum.getDimensions());
                                variances.put(rowCol, variance);
                            }
                            g = tg.get(rowCol);
                            for (long row = 0; row < NRows; row++) {
                                for (long col = 0; col < NCols; col++) {
                                    double v;
                                    v = g.getCell(row, col);
                                    double m;
                                    m = sum.getCell(row, col);
                                    variance.addToCell(row, col, (v - m) * (v - m));
                                }
                            }
//                            int NChunkRows = g.getNChunkRows(true);
//                            int NChunkCols = g.getNChunkCols(true);
//                            Grids_AbstractGrid2DSquareCellDoubleChunk sChunk;
//                            Grids_AbstractGrid2DSquareCellDoubleChunk gChunk;
//                            Grids_2D_ID_int chunkID;
//                            int chunkNrows;
//                            int chunkNcols;
//                            for (int i = 0; i < NChunkRows; i++) {
//                                for (int j = 0; j < NChunkCols; j++) {
//                                    chunkID = new Grids_2D_ID_int(i, j);
//                                    gChunk = g.getChunk(chunkID, true);
//                                    sChunk = sum.getChunk(chunkID, true);
//                                    chunkNrows = g.getChunkNRows(chunkID, true);
//                                    chunkNcols = g.getChunkNCols(chunkID, true);
//                                    for (int k = 0; k < chunkNrows; k++) {
//                                        for (int l = 0; l < chunkNcols; l++) {
//                                            double v;
//                                            v = g.getCell(gChunk, i, j, k, l, true);
//                                            double m;
//                                            m = sum.getCell(sChunk, i, j, k, l, true);
//                                            variance.setCell(chunkNrows, chunkNcols, chunkNrows, chunkNcols, noDataValue, true)cellID, noDataValue, true) = (v - m) * (v - m);
//                                        }
//                                    }
//                                }
//                            }
                        }
                    }

                    Iterator<String> gridsIte;
                    gridsIte = variances.keySet().iterator();
                    Grids_GridDouble b1KMGrid = null;
                    System.out.println("<Duplicate a1KMGrid>");
                    Grids_GridDoubleFactory f;
                    f = (Grids_GridDoubleFactory) a1KMGrid[1];
                    gdir = gridf.createNewFile(gridf.getGeneratedGridDoubleDir());
                    b1KMGrid = (Grids_GridDouble) f.create(gdir, (Grids_GridDouble) a1KMGrid[0]);
                    //b1KMGrid = (Grids_GridDouble) a1KMGrid[0];
                    System.out.println("</Duplicate a1KMGrid>");
                    double vb;
                    double v;
                    double var;
                    while (gridsIte.hasNext()) {
                        rowCol = gridsIte.next();
                        int[] rowColint;
                        rowColint = getRowCol(rowCol);
                        g = variances.get(rowCol);
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
                                x = b1KMGrid.getCellXDouble(col) + halfcellsize; // adding half a cellsize is in an attempt to prevent striping where images join.
                                v = g.getCell(x, y);
                                var = (Math.sqrt(v) / (double) (n - 1));
                                if (vb != noDataValue) {
                                    //x = b1KMGrid.getCellXDouble(col, true);
                                    if (v != noDataValue) {
                                        //System.out.println("Value at (x, y) (" + x + ", " + y + ")= " + v);
                                        //b1KMGrid.setCell(row, col, v, true);
                                        b1KMGrid.addToCell(row, col, var);
                                    }
                                }
                                g.setCell(x, y, var);
                            }
                        }
                        // Output as tiles
                        outascii = new File(
                                outdir2,
                                layerName + "_" + rowColint[0] + "_" + rowColint[1] + "Variance.asc");
                        outpng = new File(
                                outdir2,
                                layerName + "_" + rowColint[0] + "_" + rowColint[1] + "Variance.png");
                        outpng2 = new File(
                                outdir2,
                                layerName + "_" + rowColint[0] + "_" + rowColint[1] + "VarianceColor.png");
                        ae.toAsciiFile(g, outascii);
                        ImageExporter.toGreyScaleImage(g, gp, outpng, "png");
                        ImageExporter.toColourImage(0, colorMap, noDataValueColor, g, outpng2, "png");
                    }
                    // Output result grid
                    outascii = new File(
                            outdir2,
                            s + layerName + "Variance.asc");
                    outpng = new File(
                            outdir2,
                            s + layerName + "Variance.png");
                    outpng2 = new File(
                            outdir2,
                            s + layerName + "VarianceColor.png");
                    outpng3 = new File(
                            outdir2,
                            s + layerName + "VarianceColor8.png");
                    ae.toAsciiFile(b1KMGrid, outascii);
                    ImageExporter.toGreyScaleImage(b1KMGrid, gp, outpng, "png");
                    ImageExporter.toColourImage(0, colorMap, noDataValueColor, b1KMGrid, outpng2, "png");
                    ImageExporter.toColourImage(8, colorMap, noDataValueColor, b1KMGrid, outpng3, "png");
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
     * @param in
     * @param cellsize
     * @param tileBounds
     * @param layerName
     * @param rowColint This has length 2 the first element is the row and the
     * second the column of the tile.
     * @return
     */
    public Grids_GridDouble getGrid(
            File in,
            BigDecimal cellsize,
            Vector_Envelope2D tileBounds,
            String layerName,
            int[] rowColint) {
        String methodName;
        methodName = "getGrid(File,BigDecimal,Vector_Envelope2D,String,int[])";
        Grids_GridDouble result = null;
        Grids_Files gridf;
        gridf = ge.getFiles();
        File gdir;
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
            dimensions = new Grids_Dimensions(
                    tileBounds.XMin,
                    tileBounds.XMax,
                    tileBounds.YMin,
                    tileBounds.YMax,
                    cellsize);
//        dimensions[1] = tileBounds.XMin.subtract(cellsize.multiply(new BigDecimal(rowColint[1]).multiply(new BigDecimal(height)))); //XMIN
//        dimensions[4] = tileBounds.YMax.subtract(cellsize.multiply(new BigDecimal(rowColint[0]).multiply(new BigDecimal(width)))); //YMAX
//        dimensions[2] = dimensions[4].subtract(cellsize.multiply(new BigDecimal(height))); //YMIN
//        dimensions[3] = dimensions[1].subtract(cellsize.multiply(new BigDecimal(width)));  //XMAX
            gdir = gridf.createNewFile(gridf.getGeneratedGridDoubleDir());
            result = (Grids_GridDouble) gf.create(gdir, height, width, dimensions);
            int[] pixels = new int[width * height];
            PixelGrabber pg = new PixelGrabber(image, 0, 0, width, height, pixels, 0, width);
            try {
                pg.grabPixels();
            } catch (InterruptedException ie) {
                ie.printStackTrace(System.err);
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
//                        gp.addToGrid(result, 0.0d, HOOME);
//                        return result;
//                    } else {
////                        System.out.println(
////                                "Warning: missing data in " + in + " in "
////                                + "row " + row + ", col " + col + "!!!!!");
//                        result.setCell(row, col, 0.0d, HOOME);
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
//                                                HOOME);
//                                        break;
//                                    case 1:
//                                        value = lowerResGrid.getCell(
//                                                (row2 / 2) + 128,
//                                                col2 / 2,
//                                                HOOME);
//                                        break;
//                                    case 2:
//                                        value = lowerResGrid.getCell(
//                                                row2 / 2,
//                                                (col2 / 2) + 128,
//                                                HOOME);
//                                        break;
//                                    default:
//                                        // type == 3
//                                        value = lowerResGrid.getCell(
//                                                (row2 / 2) + 128,
//                                                (col2 / 2) + 128,
//                                                HOOME);
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
//                                        HOOME);
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
//                        result.setCell(row, col, 0.0d, HOOME);
//                    }
//                }
                } else {
                    result.setCell(row, col, 0.0d);
                }
                col++;
            }

            // Describe result
            //System.out.println(result.toString(0, HOOME));
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
