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
    SARIC_Files SARIC_Files;
    Grids_Environment ge;
    Grid2DSquareCellProcessor gridProcessor;
    Grids_Grid2DSquareCellDoubleFactory f;
    Grids_ESRIAsciiGridExporter gridAscii;
    Grids_ImageExporter gridImage;

    public SARIC_ImageProcessor(SARIC_Environment env) {
        this.se = env;
        SARIC_Files = env.getFiles();
        ge = env.getGrids_Environment();
        gridAscii = new Grids_ESRIAsciiGridExporter(ge);
        gridImage = new Grids_ImageExporter(ge);
        gridProcessor = ge.get_Grid2DSquareCellProcessor();        
        f = gridProcessor._Grid2DSquareCellDoubleFactory;
    }

    public void run() {
        Boolean HandleOutOfMemoryError = true;
        File in;
        in = new File(
                SARIC_Files.getInputDataMetOfficeDataPointDir(),
                "layer/wxfcs/Precipitation_Rate/png/Precipitation_Rate2017-07-27T03_00_003.png");

        Image image = null;
        // Read an image file.
        try {
            image = ImageIO.read(in);
        } catch (IOException io) {
            io.printStackTrace();
        }

        // Grab the pixels.
        int width = image.getWidth(null);
        int height = image.getHeight(null);
        System.out.println("width, height " + width + ", " + height);

        
        Grids_Grid2DSquareCellDouble g;
        g = (Grids_Grid2DSquareCellDouble) f.create(height, width);

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
                g.setCell(row, col, 0.25d, HandleOutOfMemoryError);
            } else if (pixel.equals(LightBlue)) {
                g.setCell(row, col, 0.5d, HandleOutOfMemoryError);
            } else if (pixel.equals(MuddyGreen)) {
                g.setCell(row, col, 1.5d, HandleOutOfMemoryError);
            } else if (pixel.equals(Yellow)) {
                g.setCell(row, col, 3d, HandleOutOfMemoryError);
            } else if (pixel.equals(Orange)) {
                g.setCell(row, col, 6d, HandleOutOfMemoryError);
            } else if (pixel.equals(Red)) {
                g.setCell(row, col, 12d, HandleOutOfMemoryError);
            } else if (pixel.equals(Pink)) {
                g.setCell(row, col, 24d, HandleOutOfMemoryError);
            } else if (pixel.equals(PaleBlue)) {
                g.setCell(row, col, 48, HandleOutOfMemoryError);
            } else {
                g.setCell(row, col, 0.0d, HandleOutOfMemoryError);
            }
            col++;
        }

        // Describe g
        g.toString(0, HandleOutOfMemoryError);

        // Write it out.
        File out;
        out = new File(
                SARIC_Files.getInputDataMetOfficeDataPointDir(),
                "layer/wxfcs/Precipitation_Rate/png/Precipitation_Rate2017-07-27T03_00_003.asc");

        gridAscii.toAsciiFile(g, out, HandleOutOfMemoryError);

        out = new File(
                SARIC_Files.getInputDataMetOfficeDataPointDir(),
                "layer/wxfcs/Precipitation_Rate/png/Precipitation_Rate2017-07-27T03_00_003.png.png");

        gridImage.toGreyScaleImage(g, gridProcessor, out, "png", HandleOutOfMemoryError);
//        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
//        bufferedImage.getGraphics().drawImage(image, 0, 0, null);

//        String path = out.getPath();
//        try {
//            ImageIO.write(bufferedImage, path.substring(path.lastIndexOf(".") + 1), out);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
