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

import java.io.File;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Environment;
import uk.ac.leeds.ccg.andyt.projects.saric.io.SARIC_Files;

/**
 *
 * @author geoagdt
 */
public class SARIC_NIMRODDataHandler {

    SARIC_Environment SARIC_Environment;
    SARIC_Files SARIC_Files;

    public SARIC_NIMRODDataHandler() {
    }

    public static void main(String[] args) {
        SARIC_NIMRODDataHandler dh;
        dh = new SARIC_NIMRODDataHandler();
        dh.run();
    }

    public void run() {
        File dir;
        File inputFile;
        dir = new File(
                SARIC_Files.getInputDataMetOfficeNimrodDir(),
                "data/composite/uk-1km/2017/metoffice-c-band-rain-radar_uk_20170627_1km-composite.dat.gz/test/");
        inputFile = new File(
                dir,
                "test");

    }
}
