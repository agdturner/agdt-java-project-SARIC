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

import java.io.File;
import uk.ac.leeds.ccg.andyt.geotools.demo.Geotools_ProjectShapefile;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Environment;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Strings;
import uk.ac.leeds.ccg.andyt.projects.saric.io.SARIC_Files;

/**
 *
 * @author geoagdt
 */
public class SARIC_ProjectShapefiles extends Geotools_ProjectShapefile implements Runnable {
    
    SARIC_Environment se;
    
    // For convenience
    SARIC_Files sf;
    SARIC_Strings ss;
    
    boolean doWissey;
    boolean doTeifi;
    
    protected SARIC_ProjectShapefiles(){}
    
    public SARIC_ProjectShapefiles(
            SARIC_Environment se,
            boolean doWissey,
            boolean doTeifi) {
        this.se = se;
        sf = se.Files;
        ss = se.Strings;
        this.doWissey = doWissey;
        this.doTeifi = doTeifi;
    }
    
    public void run() {
        File indir;
        File outdir;
        String filenamePrefix;
        if (doTeifi) {
            indir = new File(
                sf.getInputDataOSMDir(),
                "wales-latest-free.shp");
            outdir = new File(
                sf.getGeneratedDataOSMDir(),
                "wales-latest-free.shp");
            filenamePrefix = "gis.osm_roads_free_1"; // roads
            run(indir, outdir, filenamePrefix);
            filenamePrefix = "gis.osm_railways_free_1"; // railways
            run(indir, outdir, filenamePrefix);
            filenamePrefix = "gis.osm_water_a_free_1"; // water
            run(indir, outdir, filenamePrefix);
            filenamePrefix = "gis.osm_waterways_free_1"; // waterways
            run(indir, outdir, filenamePrefix);
        }
    }
    
    public void run(
            File indir,
            File outdir,
            String filenamePrefix) {
        String dotShp;
        dotShp = ".shp";
        String filename;
        filename = filenamePrefix + dotShp;
        File indir2;
        indir2 = new File(
                indir,
                filename);
        File outdir2;
        outdir2 = new File(
                outdir,
                filename);
        File infile;
        infile = new File(
                indir2,
                filename);
        File outfile;
        outfile = new File(
                outdir2,
                filename);
        outfile.getParentFile().mkdirs();
        run(infile, outfile);
    }
    
}
