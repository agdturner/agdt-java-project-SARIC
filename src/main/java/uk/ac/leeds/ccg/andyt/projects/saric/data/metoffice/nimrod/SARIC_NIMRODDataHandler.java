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
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Environment;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Object;
import uk.ac.leeds.ccg.andyt.projects.saric.io.SARIC_Files;

/**
 *
 * @author geoagdt
 */
public class SARIC_NIMRODDataHandler extends SARIC_Object {

    SARIC_Files sf;

    protected SARIC_NIMRODDataHandler() {
    }

    public SARIC_NIMRODDataHandler(SARIC_Environment se) {
        sf = se.getFiles();
    }

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
        f = new File(
                dir,
                "test");
        FileInputStream fis;
            DataInputStream dis;
        try {
            fis = new FileInputStream(f);
           dis = new DataInputStream(fis);
           SARIC_NIMRODHeader snh;
           snh = new SARIC_NIMRODHeader(dis);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SARIC_NIMRODDataHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
