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
public class SARIC_RasterAggregation extends SARIC_Object {

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

    public SARIC_RasterAggregation(
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
                path = "inspire/view/wmts/" + area + "/" + layerName + "/EPSG_27700_";
                for (int i = 0; i < 5; i++) {
                    indir = new File(
                            files.getInputDataMetOfficeDataPointDir(),
                            path + i);
                    indirs = indir.listFiles();
                    // initialise outdirs
                    outdirNames = new HashSet<String>();
                    for (int j = 0; j < indirs.length; j++) {
                        outdirNames.add(indirs[j].getName().split("T")[0]);
                    }
                    outdir = new File(
                            files.getOutputDataMetOfficeDataPointDir(),
                            path + i);
                    name = layerName;
                    process(indirs, outdirNames, outdir, name);
                }
            }
        }
    }

    public void process(
            File[] indirs,
            HashSet<String> outdirNames,
            File outdir,
            String name) {
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
                        g = getGrid(infiles[i]);
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
                col = new Integer(rowColSplit[2].substring(0, 1));
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

    public Grids_Grid2DSquareCellDouble getGrid(
            File in) {
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
        int pixels[] = new int[width * height];
        PixelGrabber pg = new PixelGrabber(image, 0, 0, width, height, pixels, 0, width);
        try {
            pg.grabPixels();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
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
        Color Blue;
        Blue = Color.decode("#0000FE");
        Color LightBlue;
        LightBlue = Color.decode("#3265FE");
        Color MuddyGreen;
        MuddyGreen = Color.decode("#7F7F00");
        Color Yellow;
        Yellow = Color.decode("#FECB00");
        Color Orange;
        Orange = Color.decode("#FE9800");
        Color Red;
        Red = Color.decode("#FE0000");
        Color Pink;
        Pink = Color.decode("#FE00FE");
        Color PaleBlue;
        PaleBlue = Color.decode("#E5FEFE");
        long row = height - 1;
        long col = 0;
        for (int i = 0; i < pixels.length; i++) {
            if (col == width) {
                col = 0;
                row--;
            }
            //System.out.println("row, col = " + row + ", " + col);
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
