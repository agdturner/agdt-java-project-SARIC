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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_AbstractGrid2DSquareCellDoubleChunkFactory;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Grid2DSquareCellDouble;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Grid2DSquareCellDoubleFactory;
import uk.ac.leeds.ccg.andyt.grids.exchange.Grids_ESRIAsciiGridExporter;
import uk.ac.leeds.ccg.andyt.grids.exchange.Grids_ImageExporter;
import uk.ac.leeds.ccg.andyt.grids.process.Grid2DSquareCellProcessor;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Environment;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Object;
import uk.ac.leeds.ccg.andyt.projects.saric.io.SARIC_Files;

/**
 *
 * @author geoagdt
 */
public class SARIC_ImageProcessor extends SARIC_Object {

    /**
     * For convenience
     */
    SARIC_Files files;
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
    boolean doForecastsTileFromWMTSService;
    boolean doObservationsTileFromWMTSService;
    boolean doWissey;
    boolean doTeifi;
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
            boolean doForecastsTileFromWMTSService,
            boolean doObservationsTileFromWMTSService,
            boolean doWissey,
            boolean doTeifi
    ) {
        this.se = se;
        this.dirIn = dirIn;
        this.dirOut = dirOut;
        this.doNonTiledFcs = doNonTiledFcs;
        this.doNonTiledObs = doNonTiledObs;
        this.doTileFromWMTSService = doTileFromWMTSService;
        this.doForecastsTileFromWMTSService = doForecastsTileFromWMTSService;
        this.doObservationsTileFromWMTSService = doObservationsTileFromWMTSService;
        this.doWissey = doWissey;
        this.doTeifi = doTeifi;
        files = se.getFiles();
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
        File indir;
        File infile;
        File outdir;
        String path;
        String name;
        if (doNonTiledFcs) {
            name = "Precipitation_Rate2017-07-27T03_00_003";
            path = "layer/wxfcs/Precipitation_Rate/png/";
            infile = new File(
                    files.getInputDataMetOfficeDataPointDir(),
                    path + name + ".png");
            outdir = new File(
                    files.getInputDataMetOfficeDataPointDir(),
                    path);
            //process(infile, outdir, name);
        }
        if (doTileFromWMTSService) {
            String area;
            String layerName;
            layerName = "RADAR_UK_Composite_Highres";
            File[] indirs;
            HashSet<String> outdirNames;
            if (doWissey) {
                area = "Wissey";
                System.out.println("Area " + area);
                path = "inspire/view/wmts/" + area + "/" + layerName + "/EPSG_27700_";
                for (int scale = 4; scale < 5; scale++) {
                    System.out.println("scale " + scale);
                    indir = new File(
                            files.getInputDataMetOfficeDataPointDir(),
                            path + scale);
                    indirs = indir.listFiles();
                    // initialise outdirs
                    outdirNames = new HashSet<String>();
                    for (int j = 0; j < indirs.length; j++) {
                        outdirNames.add(indirs[j].getName().split("T")[0]);
                    }
                    outdir = new File(
                            files.getOutputDataMetOfficeDataPointDir(),
                            path + scale);
                    name = layerName;
                    process(indirs, outdirNames, outdir, layerName, name, scale);
                }
            }
            if (doTeifi) {
                area = "Teifi";
                System.out.println("Area " + area);
                path = "inspire/view/wmts/" + area + "/" + layerName + "/EPSG_27700_";
                for (int scale = 0; scale < 5; scale++) {
                    System.out.println("scale " + scale);
                    indir = new File(
                            files.getInputDataMetOfficeDataPointDir(),
                            path + scale);
                    indirs = indir.listFiles();
                    // initialise outdirs
                    outdirNames = new HashSet<String>();
                    for (int j = 0; j < indirs.length; j++) {
                        outdirNames.add(indirs[j].getName().split("T")[0]);
                    }
                    outdir = new File(
                            files.getOutputDataMetOfficeDataPointDir(),
                            path + scale);
                    name = layerName;
                    process(indirs, outdirNames, outdir, layerName, name, scale);
                }
            }
        }
    }

    public void process(
            File[] indirs,
            HashSet<String> outdirNames,
            File outdir,
            String layerName,
            String name,
            int scale) {
        Boolean HandleOutOfMemoryError = true;
        String outDirName;
        String outDirNameCheck;
        String indirname;
        File[] infiles;
        String rowCol;
        int row;
        int col;
        Grids_Grid2DSquareCellDouble g;
        File outascii;
        File outpng;
        Iterator<String> outdirNamesIte;
        outdirNamesIte = outdirNames.iterator();
        while (outdirNamesIte.hasNext()) {
            HashMap<String, Grids_Grid2DSquareCellDouble> grids;
            grids = new HashMap<String, Grids_Grid2DSquareCellDouble>();
            outDirName = outdirNamesIte.next();
            for (int j = 0; j < indirs.length; j++) {
                indirname = indirs[j].getName();
                outDirNameCheck = indirname.split("T")[0];
                if (outDirName.equalsIgnoreCase(outDirNameCheck)) {
                    infiles = indirs[j].listFiles();
                    for (int i = 0; i < infiles.length; i++) {
                        rowCol = infiles[i].getName().split(indirname)[1];
                        g = getGrid(infiles[i], scale, layerName, indirname);
                        if (grids.containsKey(rowCol)) {
                            Grids_Grid2DSquareCellDouble gridToAdd;
                            gridToAdd = grids.get(rowCol);
                            gp.addToGrid(g, gridToAdd, true);
                        } else {
                            grids.put(rowCol, g);
                        }
                    }
                }
            }
            Iterator<String> gridsIte;
            gridsIte = grids.keySet().iterator();
            while (gridsIte.hasNext()) {
                rowCol = gridsIte.next();
                String[] rowColSplit;
                rowColSplit = rowCol.split("_");
                row = new Integer(rowColSplit[1]);
                col = new Integer(rowColSplit[2].substring(0, rowColSplit[2].length() - 4));
                g = grids.get(rowCol);
                File outdir2;
                outdir2 = new File(
                        outdir,
                        outDirName);
                outdir2.mkdirs();
                outascii = new File(
                        outdir2,
                        name + "_" + row + "_" + col + ".asc");
                outpng = new File(
                        outdir2,
                        name + "_" + row + "_" + col + ".png.png");
                ae.toAsciiFile(g, outascii, HandleOutOfMemoryError);
                ie.toGreyScaleImage(g, gp, outpng, "png", HandleOutOfMemoryError);
            }
        }
    }

    /**
     *
     * @param in
     * @param scale Used if images is black to attempt to get lower resolution
     * image if available. If no lower resolution image is available a warning
     * is reported.
     * @param layerName
     * @param name Used if images is black to attempt to get lower resolution
     * image if available. If no lower resolution image is available a warning
     * is reported.
     * @return
     */
    public Grids_Grid2DSquareCellDouble getGrid(
            File in,
            int scale,
            String layerName,
            String name) {
        Grids_Grid2DSquareCellDouble result;
        Boolean HandleOutOfMemoryError = true;
        Image image = null;
        int width;
        int height;
        try {
            image = ImageIO.read(in);
        } catch (IOException io) {
            io.printStackTrace(System.err);
        }
        // Grab the pixels.
        width = image.getWidth(null);
        height = image.getHeight(null);
        //System.out.println("width, height " + width + ", " + height);
        result = (Grids_Grid2DSquareCellDouble) f.create(height, width);
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
                if (scale == 0) {
                    if (row == height - 1 && col == 0) {
                        // There is no lower resolution image.
                        System.out.println(
                                "Warning: missing data in " + in + "!!!!!");
                        gp.addToGrid(result, 0.0d, HandleOutOfMemoryError);
                        return result;
                    } else {
//                        System.out.println(
//                                "Warning: missing data in " + in + " in "
//                                + "row " + row + ", col " + col + "!!!!!");
                        result.setCell(row, col, 0.0d, HandleOutOfMemoryError);
                    }
                } else {
                    if (row == height - 1 && col == 0) {
                        System.out.println(
                                "Warning: missing data in " + in + " in "
                                + "row " + row + ", col " + col + ". "
                                + "Getting lower resolution image.");
                        int tilerow;
                        int tilecol;
                        String rowCol;
                        rowCol = in.getName().split(name)[1];
                        String[] rowColSplit = rowCol.split("_");
                        tilerow = new Integer(rowColSplit[1]);
                        tilecol = new Integer(rowColSplit[2].substring(0, rowColSplit[2].length() - 4));
                        int lowerResTilerow;
                        int lowerResTilecol;
                        double halfTilerow;
                        halfTilerow = tilerow / 2.0d;
                        double halfTilecol;
                        halfTilecol = tilecol / 2.0d;
                        lowerResTilerow = (int) Math.floor(halfTilerow);
                        lowerResTilecol = (int) Math.floor(halfTilecol);
                        //lowerResTilerow = (int) Math.ceil(halfTilerow);
                        //lowerResTilecol = (int) Math.ceil(halfTilecol);
                        int type;
                        if (halfTilerow == lowerResTilerow) {
                            if (halfTilecol == lowerResTilecol) {
                                type = 0;
                            } else {
                                type = 1;
                            }
                        } else {
                            if (halfTilecol == lowerResTilecol) {
                                type = 2;
                            } else {
                                type = 3;
                            }
                        }
                        File in2;
                        in2 = in.getParentFile();
                        File in3;
                        in3 = in2.getParentFile();
                        File in4;
                        in4 = in3.getParentFile();
                        String name2;
                        name2 = in3.getName();
                        String name3;
                        name3 = name2.substring(0, name2.length() - Integer.toString(scale).length());
                        name3 += scale - 1;
                        String time;
                        time = in2.getName();
                        File in5 = new File(
                                in4,
                                name3);
                        in5 = new File(
                                in5,
                                time);
//                    String[] inname;
//                    inname = in.getName().split("Z");
                        in5 = new File(
                                in5,
                                layerName + name3 + time + "_" + lowerResTilerow + "_" + lowerResTilecol + ".png");
                        Grids_Grid2DSquareCellDouble lowerResGrid;
                        lowerResGrid = getGrid(in5, scale - 1, layerName, name);
                        //C:\Users\geoagdt\src\saric\data\input\MetOffice\DataPoint\inspire\view\wmts\Wissey\RADAR_UK_Composite_Highres\EPSG_27700_3\2017-08-01T00_00_00Z\RADAR_UK_Composite_HighresEPSG_27700_32017-08-01T00_00_00Z_11_7.png
                        //C:\Users\geoagdt\src\saric\data\input\MetOffice\DataPoint\inspire\view\wmts\Wissey\RADAR_UK_Composite_Highres\EPSG_27700_3\2017-08-01T00_00_00Z\RADAR_UK_Composite_HighresEPSG_27700_32017-08-01T00_00_00Z_11_7.png
                        double value;
                        for (int row2 = 0; row2 < 256; row2++) {
                            for (int col2 = 0; col2 < 256; col2++) {
                                switch (type) {
                                    case 0:
                                        value = lowerResGrid.getCell(
                                                row2 / 2,
                                                col2 / 2,
                                                HandleOutOfMemoryError);
                                        break;
                                    case 1:
                                        value = lowerResGrid.getCell(
                                                (row2 / 2) + 128,
                                                col2 / 2,
                                                HandleOutOfMemoryError);
                                        break;
                                    case 2:
                                        value = lowerResGrid.getCell(
                                                row2 / 2,
                                                (col2 / 2) + 128,
                                                HandleOutOfMemoryError);
                                        break;
                                    default:
                                        // type == 3
                                        value = lowerResGrid.getCell(
                                                (row2 / 2) + 128,
                                                (col2 / 2) + 128,
                                                HandleOutOfMemoryError);
                                        break;
                                }

                                if (value != 0.0d) {
                                    boolean getHere = true;
                                }

                                result.setCell(
                                        row2,
                                        col2,
                                        value,
                                        HandleOutOfMemoryError);
                            }
                        }
                        System.out.println(result.toString(0, true));
                        System.out.println("Max " + result.getGridStatistics(true).getMaxDouble(true));
                        return result;
                    } else {
//                        System.out.println(
//                                "Warning: missing data in " + in + " in "
//                                + "row " + row + ", col " + col + ". "
//                                + "Getting lower resolution image.");
                        result.setCell(row, col, 0.0d, HandleOutOfMemoryError);
                    }
                }
            } else {
                result.setCell(row, col, 0.0d, HandleOutOfMemoryError);
            }
            col++;
        }

        // Describe result
        System.out.println(result.toString(0, HandleOutOfMemoryError));
        return result;
    }
}
