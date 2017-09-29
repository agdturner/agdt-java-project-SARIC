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
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Grid2DSquareCellDouble;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Grid2DSquareCellDoubleChunkArrayFactory;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Grid2DSquareCellDoubleFactory;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_GridStatistics1;
import uk.ac.leeds.ccg.andyt.grids.exchange.Grids_ImageExporter;
import uk.ac.leeds.ccg.andyt.grids.process.Grid2DSquareCellProcessor;
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
    Grid2DSquareCellProcessor gp;
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
        gf = gp._Grid2DSquareCellDoubleFactory;
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
        File outputDir0;
        File outputDir1;
        path0 = "data/composite/uk-1km/";
        generatedDir0 = new File(
                sf.getGeneratedDataMetOfficeNimrodDir(),
                path0);
        outputDir0 = new File(
                sf.getOutputDataMetOfficeNimrodDir(),
                path0);
        gf.set_Directory(generatedDir0);
        outputDir0.mkdirs();
        //metoffice-c-band-rain-radar_uk_201706270000_1km-composite.dat
        File gpDir;
        gpDir = new File(generatedDir0, "gp");
        gpDir.mkdirs();
        gp.set_Directory(gpDir, false, true);

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
        generatedDir0 = new File(
                generatedDir0,
                path);

        generatedDir0 = new File(
                generatedDir0,
                "" + System.currentTimeMillis());

        generatedDir0.mkdirs();
        gf.set_Directory(generatedDir0);
        gpDir = new File(generatedDir0, "gp");
        gpDir.mkdirs();

        gp.set_Directory(gpDir, false, true);

        //metoffice-c-band-rain-radar_uk_201706270000_1km-composite.dat
        int numberOf5MinutePeriodsIn24Hours;
        numberOf5MinutePeriodsIn24Hours = 480;

        //numberOf5MinutePeriodsIn24Hours = 2;
        //numberOf5MinutePeriodsIn24Hours = 0;
        SARIC_Time st;
        st = new SARIC_Time(date);
        String name;
        System.out.println("Time " + st.toString());
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
        tg1 = (Grids_Grid2DSquareCellDouble) tgf.create(tg0);

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
        wg1 = (Grids_Grid2DSquareCellDouble) wgf.create(wg0);

        for (int i = 0; i < numberOf5MinutePeriodsIn24Hours; i++) {
            tg2 = (Grids_Grid2DSquareCellDouble) tgf.create(tg0);
            wg2 = (Grids_Grid2DSquareCellDouble) wgf.create(wg0);
            f = new File(
                    inputDir,
                    "metoffice-c-band-rain-radar_uk_" + st.getYYYYMMDDHHMM() + "_1km-composite.dat");
            if (f.exists()) {
                try {
                    fis = new FileInputStream(f);
                    dis = new DataInputStream(fis);
                    SARIC_NIMRODHeader snh;
                    snh = new SARIC_NIMRODHeader(fis, dis);
                    BigDecimal[] dimensions;
                    dimensions = new BigDecimal[5];
                    dimensions[0] = new BigDecimal(snh.IntervalBetweenRows);
                    dimensions[1] = new BigDecimal(snh.EastingOrLongitudeOfBottomLeftCornerOfTheImage); // Xmin
                    dimensions[2] = new BigDecimal(snh.NorthingOrLatitudeOfBottomLeftCornerOfTheImage); // Ymin
                    dimensions[3] = new BigDecimal(snh.EastingOrLongitudeOfTopRightCornerOfTheImage); // Xmax
                    dimensions[4] = new BigDecimal(snh.NorthingOrLatitudeOfTopRightCornerOfTheImage); // Ymax
                    gf.set_NoDataValue(-1d);
                    gf.set_Dimensions(dimensions);
                    gf.set_Directory(generatedDir0);
                    gf.set_ChunkNCols(345);
                    gf.set_ChunkNRows(435);
                    gf.set_GridStatistics(new Grids_GridStatistics1());
                    gf.setGrid2DSquareCellDoubleChunkFactory(new Grids_Grid2DSquareCellDoubleChunkArrayFactory());
                    gp._Grid2DSquareCellDoubleFactory = gf;
                    g = (Grids_Grid2DSquareCellDouble) gf.create(generatedDir0, snh.nrows, snh.ncols, dimensions, ge, true);
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
                                outputDir0.getParentFile(),
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
                                outputDir0.getParentFile(),
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
                System.out.println("Aggregated " + st.toString());
            }
            st.addMinutes(5);
        }
        if (doTeifi) {
            outputImages(
                    outputDir0.getParentFile(),
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
                    outputDir0.getParentFile(),
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
        gp._Grid2DSquareCellDoubleFactory.set_NoDataValue(gf.get_NoDataValue());
        gp.mask(g, mask, true);
//        outfile = new File(
//                outputDir1,
//                area + st.getYYYYMMDDHHMM() + ".png");
//        ie.toGreyScaleImage(g, gp, outfile, "png", true);
        outfile = new File(
                outputDir1,
                area + "Colour" + name + ".png");
        ie.toColourImage(0, cm, Color.BLACK, g, gp, outfile, "PNG", true);
    }
}
