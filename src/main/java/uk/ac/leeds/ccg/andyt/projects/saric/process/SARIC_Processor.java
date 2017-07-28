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

import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Environment;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Object;
import uk.ac.leeds.ccg.andyt.projects.saric.data.metoffice.datapoint.SARIC_MetOfficeScraper;

/**
 *
 * @author geoagdt
 */
public class SARIC_Processor extends SARIC_Object {

    protected SARIC_Processor() {
    }

    public SARIC_Processor(SARIC_Environment env) {
        super(env);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            if (args.length != 1) {
                System.err.println(
                        "Expected an argument which is the location "
                        + "of the directory containing the (input) data. "
                        + "Aborting.");
                System.exit(0);
            } else {
                SARIC_Environment env = new SARIC_Environment(args[0]);
                SARIC_Processor SARIC_Processor;
                SARIC_Processor = new SARIC_Processor(env);
                SARIC_Processor.run();
            }
        } catch (Exception e) {
            System.err.println(e.getLocalizedMessage());
            e.printStackTrace(System.err);
//            StackTraceElement[] stes = e.getStackTrace();
//            for (StackTraceElement ste : stes) {
//                System.err.println(ste.toString());
//            }
        } catch (Error e) {
            System.err.println(e.getLocalizedMessage());
            e.printStackTrace(System.err);
//            StackTraceElement[] stes = e.getStackTrace();
//            for (StackTraceElement ste : stes) {
//                System.err.println(ste.toString());
//            }
        }
    }

    /**
     * This is the main run method for the Digital welfare project.
     *
     * @throws Exception
     */
    public void run() throws Exception {

        //RunSARIC_MetOfficeScraper = true;
        RunSARIC_ImageProcessor = true;
        /**
         * Run SARIC_MetOfficeScraper
         */
        if (RunSARIC_MetOfficeScraper) {
            SARIC_MetOfficeScraper SARIC_MetOfficeScraper;
            SARIC_MetOfficeScraper = new SARIC_MetOfficeScraper(env);
//        Observation = true;
            Forecast = true;
//        TileFromWMTSService = true;
//        ObservationSiteList = true;
//        ForecastSiteList = true;
            SARIC_MetOfficeScraper.run(
                    Observation,
                    Forecast,
                    TileFromWMTSService,
                    ObservationSiteList,
                    ForecastSiteList);
        }

        /**
         * Run SARIC_ImageProcessor
         */
        if (RunSARIC_ImageProcessor) {
            SARIC_ImageProcessor SARIC_ImageProcessor;
            SARIC_ImageProcessor = new SARIC_ImageProcessor(env);
            SARIC_ImageProcessor.run();
        }
    }

    boolean Observation = false;
    boolean Forecast = false;
    boolean TileFromWMTSService = false;
    boolean ObservationSiteList = false;
    boolean ForecastSiteList = false;
    boolean RunSARIC_MetOfficeScraper = false;
    boolean RunSARIC_ImageProcessor = false;

}
