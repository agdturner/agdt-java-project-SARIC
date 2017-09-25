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

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
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
import uk.ac.leeds.ccg.andyt.projects.saric.io.SARIC_Files;

/**
 *
 * @author geoagdt
 */
public class SARIC_NIMRODDataHandler extends SARIC_Object {

    SARIC_Files sf;
    Grids_Environment ge;
    Grid2DSquareCellProcessor gp;
    Grids_Grid2DSquareCellDoubleFactory gf;

    protected SARIC_NIMRODDataHandler() {
    }

    public SARIC_NIMRODDataHandler(SARIC_Environment se) {
        super(se);
        sf = se.getFiles();
        ge = se.getGrids_Environment();
        gp = ge.get_Grid2DSquareCellProcessor();
        gf = gp._Grid2DSquareCellDoubleFactory;
    }

    @Deprecated
    public static void main(String[] args) {
        SARIC_Environment se;
        se = new SARIC_Environment("C:/Users/geoagdt/src/projects/saric/data");
        SARIC_NIMRODDataHandler dh;
        dh = new SARIC_NIMRODDataHandler(se);
        dh.run();
    }

    public void run() {
        File dir;
        File f;
        dir = new File(
                sf.getInputDataMetOfficeNimrodDir(),
                "data/composite/uk-1km/2017/metoffice-c-band-rain-radar_uk_20170627_1km-composite.dat.gz/test/");
        File gpDir;
        gpDir = new File(dir,"gp");
        gpDir.mkdirs();
        gp.set_Directory(gpDir, false, true);
        f = new File(
                dir,
                "test");
        FileInputStream fis;
        DataInputStream dis;

        Grids_Grid2DSquareCellDouble g;

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
            gf.set_Directory(dir);
            gf.set_ChunkNCols(345);
            gf.set_ChunkNRows(435);
            gf.set_GridStatistics(new Grids_GridStatistics1());
            gf.setGrid2DSquareCellDoubleChunkFactory(new Grids_Grid2DSquareCellDoubleChunkArrayFactory());
            gp._Grid2DSquareCellDoubleFactory = gf;
            g = (Grids_Grid2DSquareCellDouble) gf.create(dir, snh.nrows, snh.ncols, dimensions, ge, true);
            Grids_ImageExporter ie;

            File testImage;
            testImage = new File(
                    f.getParentFile(),
                    "test.png");
            ie = new Grids_ImageExporter(ge);
            try {
                for (long row = 0; row < snh.nrows; row++) {
                    for (long col = 0; col < snh.ncols; col++) {
                        g.setCell(row, col, dis.readShort(), true);
                    }
                    //System.out.println("done row " + row);
                }
                System.out.println(g.toString(true));
                System.out.println(g.toString(0, true));
                ie.toGreyScaleImage(g, gp, testImage, "png", true);
                fis.close();
                dis.close();
            } catch (IOException ex) {
                Logger.getLogger(SARIC_NIMRODDataHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SARIC_NIMRODDataHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
