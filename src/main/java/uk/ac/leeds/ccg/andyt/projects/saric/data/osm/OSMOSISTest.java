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
package uk.ac.leeds.ccg.andyt.projects.saric.data.osm;

import java.io.File;
import java.util.List;
import org.openstreetmap.osmosis.core.cli.CommandLineParser;
import org.openstreetmap.osmosis.core.pipeline.common.TaskConfiguration;

/**
 *
 * @author geoagdt
 */
public class OSMOSISTest {
    
    public OSMOSISTest(){}
    
    public static void main(String[] args) {
        new OSMOSISTest().run();
    }
    
    public void run() {
        // Get all 
        //osmosis  --read-xml enableDateParsing=no file=-  --bounding-box top=49.5138 left=10.9351 bottom=49.3866 right=11.201 --write-xml file=-
        File osmDir;
        osmDir = new File("N:/Earth&Environment/Geography/Research-3/Projects/SARIC Runoff risk/AndyTurner/data/OSM");
        File inputDir;
        inputDir = new File(osmDir,"input");
        File input;
        input = new File(inputDir,"great-britain-latest.osm");
        File processedDir;
        processedDir = new File(osmDir,"processed");
        File output;
        output = new File(processedDir, "wissey.osm");
        
        String[] args;
//        args = new String[10];
//        args[0] = "--read-xml";
//        args[1] = "enableDateParsing=no";
//        args[2] = "file=" + input.getPath();
//        args[3] = "--bounding-box";
//        args[4] = "top=53.0";
//        args[5] = "left=0.6";
//        args[6] = "bottom=52.0";
//        args[7] = "right=0.7";
//        args[8] = "--write-xml";
//        args[9] = "file=" + output.getPath();

        args = new String[3];
        args[0] = "--read-xml enableDateParsing=no file=" + input.getPath();
        args[1] = "--bounding-box top=53.0 left=0.6 bottom=52.0 right=0.7";
        args[2] = "--write-xml file=" + output.getPath();
        
        // Select all highways using the tag-filter parameter:
        //osmosis --read-xml city.osm --tf accept-ways highway=* --used-node --write-xml highways.osm

//        CommandLineParser clp;
//        clp = new CommandLineParser();
//        clp.parse(args);
//        List<TaskConfiguration> l;
//                l = clp.getTaskInfoList();
//                l.get(0).

    }
}
