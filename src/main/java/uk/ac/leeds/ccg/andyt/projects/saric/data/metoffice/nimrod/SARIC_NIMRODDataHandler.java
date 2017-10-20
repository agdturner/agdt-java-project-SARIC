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
package uk.ac.leeds.ccg.andyt.projects.saric.data.metoffice.nimrod;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.leeds.ccg.andyt.generic.io.Generic_StaticIO;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Dimensions;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_Grid2DSquareCellDouble;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_Grid2DSquareCellDoubleChunkArrayFactory;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_Grid2DSquareCellDoubleFactory;
import uk.ac.leeds.ccg.andyt.grids.core.statistics.Grids_GridStatistics1;
import uk.ac.leeds.ccg.andyt.grids.exchange.Grids_ImageExporter;
import uk.ac.leeds.ccg.andyt.grids.process.Grids_Processor;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Environment;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Object;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Strings;
import uk.ac.leeds.ccg.andyt.projects.saric.data.catchment.SARIC_Teifi;
import uk.ac.leeds.ccg.andyt.projects.saric.data.catchment.SARIC_Wissey;
import uk.ac.leeds.ccg.andyt.projects.saric.io.SARIC_Files;
import uk.ac.leeds.ccg.andyt.projects.saric.util.SARIC_Date;
import uk.ac.leeds.ccg.andyt.projects.saric.util.SARIC_Time;
import uk.ac.leeds.ccg.andyt.projects.saric.visualisation.SARIC_Colour;

/**
 *
 * @author geoagdt
 */
public class SARIC_NIMRODDataHandler extends SARIC_Object {

    SARIC_Files sf;
    SARIC_Strings ss;
    Grids_Environment ge;
    Grids_Processor gp;
    Grids_Grid2DSquareCellDoubleFactory gf;

    boolean doWissey;
    boolean doTeifi;

    protected SARIC_NIMRODDataHandler() {
    }

    public SARIC_NIMRODDataHandler(
            SARIC_Environment se,
            boolean doWissey,
            boolean doTeifi) {
        super(se);
        sf = se.getFiles();
        ss = se.getStrings();
        ge = se.getGrids_Environment();
        gp = ge.get_Grid2DSquareCellProcessor();
        gf = gp.Grid2DSquareCellDoubleFactory;
        this.doWissey = doWissey;
        this.doTeifi = doTeifi;
    }

    public void run() {

        // For visualisation/output
        SARIC_Colour sc;
        sc = new SARIC_Colour(se);
        TreeMap<Double, Color> cm;
        cm = sc.getColorMap();
        Grids_ImageExporter ie;
        ie = new Grids_ImageExporter(ge);
        File outfile;

        String path0;
        File inputDir;
        File generatedDir0;
        File generatedDir1;
        File generatedDir2 = null;
        File outputDir0;
        File outputDir1;
        path0 = "data/composite/uk-1km/";
        generatedDir0 = new File(
                sf.getGeneratedDataMetOfficeNimrodDir(),
                path0);
        generatedDir0 = new File(
                generatedDir0,
                "Grids");
        outputDir0 = new File(
                sf.getOutputDataMetOfficeNimrodDir(),
                path0);
        outputDir0.mkdirs();
        //metoffice-c-band-rain-radar_uk_201706270000_1km-composite.dat
        File gpDir;

        String YYYY;
        String MM;
        String DD;
        String path;
        SARIC_Date date;
        File f = null;

        YYYY = "2017";
        MM = "06";
        DD = "27";
        path = "/" + YYYY + "/" + YYYY + "-" + MM + "/";
        date = new SARIC_Date(se, YYYY + "-" + MM + "-" + DD);
        inputDir = new File(
                sf.getInputDataMetOfficeNimrodDir(),
                path0 + path);
        generatedDir1 = new File(
                generatedDir0,
                path);
        outputDir1 = new File(
                outputDir0,
                path);

        //metoffice-c-band-rain-radar_uk_201706270000_1km-composite.dat
        int numberOf5MinutePeriodsIn24Hours;
        numberOf5MinutePeriodsIn24Hours = 480;

        //numberOf5MinutePeriodsIn24Hours = 2;
        //numberOf5MinutePeriodsIn24Hours = 0;
        SARIC_Time st;
        st = new SARIC_Time(date);
        String name;
        System.out.println("Time " + st.getYYYYMMDDHHMM());
        FileInputStream fis;
        DataInputStream dis;
        Grids_Grid2DSquareCellDouble g;
        Grids_Grid2DSquareCellDouble ag = null;

        // Teifi
        SARIC_Teifi teifi;
        Object[] tg;
        Grids_Grid2DSquareCellDouble tg0;
        Grids_Grid2DSquareCellDouble tg1;
        Grids_Grid2DSquareCellDouble tg2;
        Grids_Grid2DSquareCellDoubleFactory tgf;
        teifi = new SARIC_Teifi(se);
        tg = teifi.get1KMGridMaskedToCatchment();
        tg0 = (Grids_Grid2DSquareCellDouble) tg[0];
        tgf = (Grids_Grid2DSquareCellDoubleFactory) tg[1];
        File dirt1;
        File dirt2;
        dirt1 = new File(
                tgf.getDirectory(true),
                "TG1");
        tgf.setDirectory(dirt1);
        tg1 = (Grids_Grid2DSquareCellDouble) tgf.create(dirt1, tg0, 0, 0, tg0.getNRows(true) - 1, tg0.getNCols(true) - 1);
        dirt2 = new File(
                tgf.getDirectory(true),
                "TG2");
        tgf.setDirectory(dirt2);

        // Wissey
        SARIC_Wissey wissey;
        Object[] wg;
        Grids_Grid2DSquareCellDouble wg0;
        Grids_Grid2DSquareCellDouble wg1;
        Grids_Grid2DSquareCellDouble wg2;
        Grids_Grid2DSquareCellDoubleFactory wgf;
        wissey = new SARIC_Wissey(se);
        wg = wissey.get1KMGridMaskedToCatchment();
        wg0 = (Grids_Grid2DSquareCellDouble) wg[0];
        wgf = (Grids_Grid2DSquareCellDoubleFactory) wg[1];
        File dirw1;
        File dirw2;
        dirw1 = new File(
                wgf.getDirectory(true),
                "WG1");
        wgf.setDirectory(dirw1);
        wg1 = (Grids_Grid2DSquareCellDouble) wgf.create(dirw1, wg0, 0, 0, wg0.getNRows(true) - 1, wg0.getNCols(true) - 1);
        dirw2 = new File(
                wgf.getDirectory(true),
                "WG2");
        wgf.setDirectory(dirw2);
        // Set archive parameters
        long GridID;
        //long maxID;
        int range;
        GridID = 0L;
        //maxID = 10000L;
        range = 100;

        for (int i = 0; i < numberOf5MinutePeriodsIn24Hours; i++) {
            tg2 = (Grids_Grid2DSquareCellDouble) tgf.create(dirt2, tg0, 0, 0, tg0.getNRows(true) - 1, tg0.getNCols(true) - 1);
            wg2 = (Grids_Grid2DSquareCellDouble) wgf.create(dirw2, wg0, 0, 0, wg0.getNRows(true) - 1, wg0.getNCols(true) - 1);
            f = new File(
                    inputDir,
                    "metoffice-c-band-rain-radar_uk_" + st.getYYYYMMDDHHMM() + "_1km-composite.dat");
            if (f.exists()) {
                try {
                    // Set archive for storing grids
                    if (generatedDir2 == null) {
//                        try {
//                            Generic_StaticIO.initialiseArchive(generatedDir1, range, maxID);
//                        } catch (IOException ex) {
//                            Logger.getLogger(SARIC_NIMRODDataHandler.class.getName()).log(Level.SEVERE, null, ex);
//                        }
//                        generatedDir2 = Generic_StaticIO.getObjectDirectory(
//                                generatedDir1,
//                                GridID,
//                                maxID,
//                                range);
                        Generic_StaticIO.initialiseArchive(generatedDir1, range);
                        generatedDir2 = Generic_StaticIO.getObjectDirectory(
                                generatedDir1,
                                GridID,
                                GridID,
                                range);
                    } else {
                        GridID++;
                        generatedDir2 = Generic_StaticIO.addToArchive(generatedDir1, range, GridID);
                    }
                    generatedDir2.mkdirs();
                    gf.setDirectory(generatedDir2);
//                    gpDir = new File(generatedDir2, "Processor");
//                    gpDir.mkdirs();
//                    gp.setDirectory(gpDir, false, true);
                    // Initialise streams for reading
                    fis = new FileInputStream(f);
                    dis = new DataInputStream(fis);
                    SARIC_NIMRODHeader snh;
                    snh = new SARIC_NIMRODHeader(fis, dis);
                    Grids_Dimensions dimensions;
                    dimensions = new Grids_Dimensions(
                            new BigDecimal(snh.EastingOrLongitudeOfBottomLeftCornerOfTheImage),
                            new BigDecimal(snh.NorthingOrLatitudeOfBottomLeftCornerOfTheImage),
                            new BigDecimal(snh.EastingOrLongitudeOfTopRightCornerOfTheImage),
                            new BigDecimal(snh.NorthingOrLatitudeOfTopRightCornerOfTheImage),
                            new BigDecimal(snh.IntervalBetweenRows));
                    gf.set_NoDataValue(-1d);
                    gf.setDimensions(dimensions);
                    gf.setChunkNCols(345);
                    gf.setChunkNRows(435);
                    gf.setGridStatistics(new Grids_GridStatistics1(ge));
                    gf.setChunkFactory(new Grids_Grid2DSquareCellDoubleChunkArrayFactory());
                    gp.Grid2DSquareCellDoubleFactory = gf;
                    g = (Grids_Grid2DSquareCellDouble) gf.create(generatedDir2, snh.nrows, snh.ncols, dimensions, true);
                    try {
                        for (long row = 0; row < snh.nrows; row++) {
                            for (long col = 0; col < snh.ncols; col++) {
                                g.setCell(row, col, dis.readShort(), true);
                            }
                            //System.out.println("done row " + row);
                        }
                        System.out.println(g.toString(true));
                        System.out.println(g.toString(0, true));
                        fis.close();
                        dis.close();
                    } catch (IOException ex) {
                        Logger.getLogger(SARIC_NIMRODDataHandler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    /**
                     * Clip grid to the Wissey and Teifi catchments here to
                     * reduce computing for the
                     */
                    name = st.getYYYYMMDDHHMM();
                    if (doTeifi) {
                        gp.addToGrid(tg1, g, true);
                        gp.addToGrid(tg2, g, true);
                        outputImages(
                                outputDir1,
                                ss.getString_Teifi(),
                                name,
                                tgf,
                                tg2,
                                tg0,
                                ie,
                                cm);
                    }
                    if (doWissey) {
                        gp.addToGrid(wg1, g, true);
                        gp.addToGrid(wg2, g, true);
                        outputImages(
                                outputDir1,
                                ss.getString_Wissey(),
                                name,
                                wgf,
                                wg2,
                                wg0,
                                ie,
                                cm);
                    }
//                    /**
//                     * Initialise aggregateGrid if it has not been already and
//                     * aggergate if it has.
//                     */
//                    if (ag == null) {
//                        ag = g;
//                    } else {
//                        gp.addToGrid(g, ag, true);
//                    }
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(SARIC_NIMRODDataHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("Aggregated " + st.getYYYYMMDDHHMM());
            }
            st.addMinutes(5);
        }
        if (doTeifi) {
            outputImages(
                    outputDir1,
                    ss.getString_Teifi(),
                    "",
                    tgf,
                    tg1,
                    tg0,
                    ie,
                    cm);
        }
        if (doWissey) {
            outputImages(
                    outputDir1,
                    ss.getString_Wissey(),
                    "",
                    wgf,
                    wg1,
                    wg0,
                    ie,
                    cm);
        }
    }

    public void outputImages(
            File outdir,
            String area,
            String name,
            //SARIC_Time st,
            Grids_Grid2DSquareCellDoubleFactory gf,
            Grids_Grid2DSquareCellDouble g,
            Grids_Grid2DSquareCellDouble mask,
            Grids_ImageExporter ie,
            TreeMap<Double, Color> cm) {
        File outputDir1;
        outputDir1 = new File(
                outdir,
                area);
        outputDir1.mkdirs();
        File outfile;
        gp.Grid2DSquareCellDoubleFactory.set_NoDataValue(gf.get_NoDataValue());
        gp.mask(g, mask, true);
//        outfile = new File(
//                outputDir1,
//                area + st.getYYYYMMDDHHMM() + ".png");
//        ie.toGreyScaleImage(g, gp, outfile, "png", true);
        outfile = new File(
                outputDir1,
                area + "Colour" + name + ".png");
        ie.toColourImage(0, cm, Color.BLACK, g, outfile, "PNG", true);
    }
}
