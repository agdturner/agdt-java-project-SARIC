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
import uk.ac.leeds.ccg.andyt.generic.io.Generic_IO;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Dimensions;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridDouble;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkDoubleArrayFactory;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridDoubleFactory;
import uk.ac.leeds.ccg.andyt.grids.io.Grids_Files;
import uk.ac.leeds.ccg.andyt.grids.io.Grids_ImageExporter;
import uk.ac.leeds.ccg.andyt.grids.process.Grids_Processor;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Environment;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Object;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Strings;
import uk.ac.leeds.ccg.andyt.projects.saric.data.catchment.SARIC_Teifi;
import uk.ac.leeds.ccg.andyt.projects.saric.data.catchment.SARIC_Wissey;
import uk.ac.leeds.ccg.andyt.projects.saric.io.SARIC_Files;
import uk.ac.leeds.ccg.andyt.generic.time.Generic_Date;
import uk.ac.leeds.ccg.andyt.generic.time.Generic_Time;
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
    Grids_GridDoubleFactory gf;

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
        ge = se.getGrids_Env();
        gp = ge.getProcessor();
        gf = gp.GridDoubleFactory;
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

        Grids_Files gridf;
        gridf = ge.getFiles();
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
        Generic_Date date;
        File f;

        YYYY = "2017";
        MM = "06";
        DD = "27";
        path = "/" + YYYY + "/" + YYYY + "-" + MM + "/";
        date = new Generic_Date(se, YYYY + "-" + MM + "-" + DD);
        inputDir = new File(sf.getInputDataMetOfficeNimrodDir(), path0 + path);
        generatedDir1 = new File(generatedDir0, path);
        outputDir1 = new File(outputDir0, path);

        //metoffice-c-band-rain-radar_uk_201706270000_1km-composite.dat
        int numberOf5MinutePeriodsIn24Hours;
        numberOf5MinutePeriodsIn24Hours = 480;

        //numberOf5MinutePeriodsIn24Hours = 2;
        //numberOf5MinutePeriodsIn24Hours = 0;
        Generic_Time st;
        st = new Generic_Time(date);
        String name;
        System.out.println("Time " + st.getYYYYMMDDHHMM());
        FileInputStream fis;
        DataInputStream dis;
        Grids_GridDouble g;
        Grids_GridDouble ag = null;

        // Teifi
        SARIC_Teifi teifi;
        Object[] tg;
        Grids_GridDouble tg0;
        Grids_GridDouble tg1;
        Grids_GridDouble tg2;
        Grids_GridDoubleFactory tgf;
        teifi = new SARIC_Teifi(se);
        tg = teifi.get1KMGridMaskedToCatchment();
        tg0 = (Grids_GridDouble) tg[0];
        tgf = (Grids_GridDoubleFactory) tg[1];
        File dirt1;
        File dirt2;
        dirt1 = new File(gridf.getGeneratedGridDoubleDir(), "TG1");
        tg1 = (Grids_GridDouble) tgf.create(dirt1, tg0, 0, 0,
                tg0.getNRows() - 1, tg0.getNCols() - 1);
        dirt2 = new File(gridf.getGeneratedGridDoubleDir(), "TG2");

        // Wissey
        SARIC_Wissey wissey;
        Object[] wg;
        Grids_GridDouble wg0;
        Grids_GridDouble wg1;
        Grids_GridDouble wg2;
        Grids_GridDoubleFactory wgf;
        wissey = new SARIC_Wissey(se);
        wg = wissey.get1KMGridMaskedToCatchment();
        wg0 = (Grids_GridDouble) wg[0];
        wgf = (Grids_GridDoubleFactory) wg[1];
        File dirw1;
        File dirw2;
        dirw1 = new File(gridf.getGeneratedGridDoubleDir(), "WG1");
        wg1 = (Grids_GridDouble) wgf.create(dirw1, wg0, 0, 0,
                wg0.getNRows() - 1, wg0.getNCols() - 1);
        dirw2 = new File(gridf.getGeneratedGridDoubleDir(), "WG2");
        // Set archive parameters
        long GridID;
        //long maxID;
        int range;
        GridID = 0L;
        //maxID = 10000L;
        range = 100;

        for (int i = 0; i < numberOf5MinutePeriodsIn24Hours; i++) {
            tg2 = (Grids_GridDouble) tgf.create(dirt2, tg0, 0, 0,
                    tg0.getNRows() - 1, tg0.getNCols() - 1);
            wg2 = (Grids_GridDouble) wgf.create(dirw2, wg0, 0, 0,
                    wg0.getNRows() - 1, wg0.getNCols() - 1);
            f = new File(inputDir,
                    "metoffice-c-band-rain-radar_uk_" + st.getYYYYMMDDHHMM() + "_1km-composite.dat");
            if (f.exists()) {
                try {
                    // Set archive for storing grids
                    if (generatedDir2 == null) {
//                        try {
//                            Generic_IO.initialiseArchive(generatedDir1, range, maxID);
//                        } catch (IOException ex) {
//                            Logger.getLogger(SARIC_NIMRODDataHandler.class.getName()).Log(Level.SEVERE, null, ex);
//                        }
//                        generatedDir2 = Generic_IO.getObjectDirectory(
//                                generatedDir1,
//                                GridID,
//                                maxID,
//                                range);
                        Generic_IO.initialiseArchive(generatedDir1, range);
                        generatedDir2 = Generic_IO.getObjectDir(
                                generatedDir1, GridID, GridID, range);
                    } else {
                        GridID++;
                        generatedDir2 = Generic_IO.addToArchive(
                                generatedDir1, range, GridID);
                    }
                    generatedDir2.mkdirs();
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
                            new BigDecimal(snh.EastingOrLongitudeOfTopRightCornerOfTheImage),
                            new BigDecimal(snh.NorthingOrLatitudeOfBottomLeftCornerOfTheImage),
                            new BigDecimal(snh.NorthingOrLatitudeOfTopRightCornerOfTheImage),
                            new BigDecimal(snh.IntervalBetweenRows));
                    gf.setNoDataValue(-1d);
                    gf.setDimensions(dimensions);
                    gf.setChunkNCols(345);
                    gf.setChunkNRows(435);
                    gf.setDefaultChunkFactory(new Grids_GridChunkDoubleArrayFactory());
                    gp.GridDoubleFactory = gf;
                    g = (Grids_GridDouble) gf.create(generatedDir2, snh.nrows, snh.ncols, dimensions);
                    try {
                        for (long row = 0; row < snh.nrows; row++) {
                            for (long col = 0; col < snh.ncols; col++) {
                                g.setCell(row, col, dis.readShort());
                            }
                            //System.out.println("done row " + row);
                        }
                        System.out.println(g.toString());
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
                        gp.addToGrid(tg1, g, 1.0d);
                        gp.addToGrid(tg2, g, 1.0d);
                        outputImages(
                                outputDir1,
                                ss.getS_Teifi(),
                                name,
                                tgf,
                                tg2,
                                tg0,
                                ie,
                                cm);
                    }
                    if (doWissey) {
                        gp.addToGrid(wg1, g, 1.0d);
                        gp.addToGrid(wg2, g, 1.0d);
                        outputImages(
                                outputDir1,
                                ss.getS_Wissey(),
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
                    ss.getS_Teifi(),
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
                    ss.getS_Wissey(),
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
            //Generic_Time st,
            Grids_GridDoubleFactory gf,
            Grids_GridDouble g,
            Grids_GridDouble mask,
            Grids_ImageExporter ie,
            TreeMap<Double, Color> cm) {
        File outputDir1;
        outputDir1 = new File(
                outdir,
                area);
        outputDir1.mkdirs();
        File outfile;
        gp.GridDoubleFactory.setNoDataValue(gf.getNoDataValue());
        gp.mask(g, mask);
//        outfile = new File(
//                outputDir1,
//                area + st.getYYYYMMDDHHMM() + ".png");
//        ie.toGreyScaleImage(g, gp, outfile, "png", true);
        outfile = new File(
                outputDir1,
                area + "Colour" + name + ".png");
        ie.toColourImage(0, cm, Color.BLACK, g, outfile, "PNG");
    }
}
