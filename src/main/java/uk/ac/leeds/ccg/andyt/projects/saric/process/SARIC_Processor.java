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
import uk.ac.leeds.ccg.andyt.generic.utilities.Generic_Time;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Environment;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Object;
import uk.ac.leeds.ccg.andyt.projects.saric.data.metoffice.datapoint.SARIC_MetOfficeScraper;

/**
 * This is the main processor/controller for SARIC processing. Everything is
 * supposed to be actionable from here.
 *
 * @author geoagdt
 */
public class SARIC_Processor extends SARIC_Object {

    protected SARIC_Processor() {
    }

    public SARIC_Processor(SARIC_Environment se) {
        super(se);
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
                SARIC_Environment se = new SARIC_Environment(args[0]);
                SARIC_Processor sp;
                sp = new SARIC_Processor(se);
                sp.run();
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

        // Main switches
        //RunSARIC_MetOfficeScraper = true;
        RunSARIC_ImageProcessor = true;

        /**
         * Run SARIC_MetOfficeScraper
         */
        if (RunSARIC_MetOfficeScraper) {
            long timeDelay;
            String name;

            // Main Switches
//            doNonTiledObs = true;
            doNonTiledFcs = true;
//            doTileFromWMTSService = true;
//            doObservationsTileFromWMTSService = true;
//            doForecastsTileFromWMTSService = true;

            /**
             * Observation thread gets data every 2 and 3/4 hours. New data is
             * supposed to be released every 15 minutes, so it might be better
             * to just keep getting this new data, but for simplicity this
             * currently gets data for every 15 minutes in the last 3 hours set.
             */
            if (doNonTiledObs) {
                Observations = false;
                Forecasts = false;
                TileFromWMTSService = false;
                ObservationSiteList = false;
                ObservationsTileFromWMTSService = false;
                ForecastsTileFromWMTSService = false;
                ForecastSiteList = false;
                ForecastsForSite = true;
                ObservationsForSite = true;
                timeDelay = (long) (Generic_Time.MilliSecondsInHour * 2.75);
                name = "Observations";
                overwrite = false;
                SARIC_MetOfficeScraper ObservationsMetOfficeScraper;
                ObservationsMetOfficeScraper = new SARIC_MetOfficeScraper(
                        se,
                        Observations,
                        Forecasts,
                        TileFromWMTSService,
                        ObservationsTileFromWMTSService,
                        ForecastsTileFromWMTSService,
                        ObservationSiteList,
                        ForecastSiteList,
                        ForecastsForSite,
                        ObservationsForSite,
                        timeDelay,
                        name,
                        overwrite);
                Thread observationsThread;
                observationsThread = new Thread(ObservationsMetOfficeScraper);
                observationsThread.start();
            }

            /**
             * Forecasts thread gets data every 5.5 hours. New data is supposed
             * to be released for every 6 hours. There is one release marked for
             * each of these times: 3am, 9am, 3pm, 9pm. At each of these times
             * there are forecasts for 3 hourly intervals for up to 36 hours (12
             * forecasts). So, there are 6 forecasts for any time. The most
             * recent forecast will be the most useful, but sometimes we have to
             * look a long way ahead. It may be that there are longer term
             * forecasts made for the sites where there are ground observations.
             */
            if (doNonTiledFcs) {
                Observations = false;
                Forecasts = false;
                TileFromWMTSService = false;
                ObservationsTileFromWMTSService = false;
                ForecastsTileFromWMTSService = false;
                ObservationSiteList = true;
                ForecastSiteList = true;
                ForecastsForSite = true;
//                ForecastSiteList = false;
//                ForecastsForSite = false;
                ObservationsForSite = false;
                timeDelay = (long) (Generic_Time.MilliSecondsInHour * 5.5);
                name = "Forecasts";
                overwrite = false;
                SARIC_MetOfficeScraper ForecastsMetOfficeScraper;
                ForecastsMetOfficeScraper = new SARIC_MetOfficeScraper(
                        se,
                        Observations,
                        Forecasts,
                        TileFromWMTSService,
                        ObservationsTileFromWMTSService,
                        ForecastsTileFromWMTSService,
                        ObservationSiteList,
                        ForecastSiteList,
                        ForecastsForSite,
                        ObservationsForSite,
                        timeDelay,
                        name,
                        overwrite);
                Thread forecastsThread;
                forecastsThread = new Thread(ForecastsMetOfficeScraper);
                forecastsThread.start();
            }

            /**
             * Forecasts thread gets data every 5.5 hours. New data is supposed
             * to be released for every 6 hours. There is one release marked for
             * each of these times: 3am, 9am, 3pm, 9pm. At each of these times
             * there are forecasts for 3 hourly intervals for up to 36 hours (12
             * forecasts). So, there are 6 forecasts for any time. The most
             * recent forecast will be the most useful, but sometimes we have to
             * look a long way ahead. It may be that there are longer term
             * forecasts made for the sites where there are ground observations.
             */
            if (doTileFromWMTSService) {
                Observations = false;
                Forecasts = false;
                TileFromWMTSService = true;
                ObservationSiteList = false;
                ForecastSiteList = false;
                ForecastsForSite = false;
                ObservationsForSite = false;

                if (doObservationsTileFromWMTSService) {
                    ObservationsTileFromWMTSService = true;
                    ForecastsTileFromWMTSService = false;
                    timeDelay = (long) (Generic_Time.MilliSecondsInHour * 19);
                    name = "Higher Resolution Tiled Forecasts and Observations";
                    overwrite = false;
                    SARIC_MetOfficeScraper ForecastsMetOfficeScraper;
                    ForecastsMetOfficeScraper = new SARIC_MetOfficeScraper(
                            se,
                            Observations,
                            Forecasts,
                            TileFromWMTSService,
                            ObservationsTileFromWMTSService,
                            ForecastsTileFromWMTSService,
                            ObservationSiteList,
                            ForecastSiteList,
                            ForecastsForSite,
                            ObservationsForSite,
                            timeDelay,
                            name,
                            overwrite);
                    Thread forecastsThread;
                    forecastsThread = new Thread(ForecastsMetOfficeScraper);
                    forecastsThread.start();
                }
                if (doForecastsTileFromWMTSService) {
                    ObservationsTileFromWMTSService = false;
                    ForecastsTileFromWMTSService = true;
                    timeDelay = (long) (Generic_Time.MilliSecondsInHour * 5.5);
                    name = "Higher Resolution Tiled Forecasts and Observations";
                    overwrite = false;
                    SARIC_MetOfficeScraper ForecastsMetOfficeScraper;
                    ForecastsMetOfficeScraper = new SARIC_MetOfficeScraper(
                            se,
                            Observations,
                            Forecasts,
                            TileFromWMTSService,
                            ObservationsTileFromWMTSService,
                            ForecastsTileFromWMTSService,
                            ObservationSiteList,
                            ForecastSiteList,
                            ForecastsForSite,
                            ObservationsForSite,
                            timeDelay,
                            name,
                            overwrite);
                    Thread forecastsThread;
                    forecastsThread = new Thread(ForecastsMetOfficeScraper);
                    forecastsThread.start();
                }
            }
        }

        /**
         * Run SARIC_ImageProcessor
         */
        if (RunSARIC_ImageProcessor) {

            // Main Switches
            boolean doWissey;
            boolean doTeifi;
            doWissey = true;
            doTeifi = true;
//            doNonTiledObs = true;
            doNonTiledObs = false;
//            doNonTiledFcs = true;
            doNonTiledFcs = false;
            doTileFromWMTSService = true;
            doObservationsTileFromWMTSService = true;
            doForecastsTileFromWMTSService = true;

            File dirIn;
            dirIn = se.getFiles().getInputDataMetOfficeDataPointDir();
            File dirOut;
            dirOut = se.getFiles().getOutputDataMetOfficeDataPointDir();
            SARIC_ImageProcessor SARIC_ImageProcessor;
            SARIC_ImageProcessor = new SARIC_ImageProcessor(
                    se,
                    dirIn,
                    dirOut,
                    doNonTiledFcs,
                    doNonTiledObs,
                    doTileFromWMTSService,
                    doForecastsTileFromWMTSService,
                    doObservationsTileFromWMTSService,
                    doWissey,
                    doTeifi);
            SARIC_ImageProcessor.run();
        }
    }

    boolean Observations = false;
    boolean Forecasts = false;
    boolean TileFromWMTSService = false;
    boolean ObservationsTileFromWMTSService = false;
    boolean ForecastsTileFromWMTSService = false;
    boolean ObservationSiteList = false;
    boolean ForecastSiteList = false;
    boolean doNonTiledObs = false;
    boolean doNonTiledFcs = false;
    boolean doTileFromWMTSService = false;
    boolean doObservationsTileFromWMTSService = false;
    boolean doForecastsTileFromWMTSService = false;
    boolean ForecastsForSite = false;
    boolean ObservationsForSite = false;
    boolean RunSARIC_MetOfficeScraper = false;
    boolean RunSARIC_ImageProcessor = false;
    boolean overwrite = false;

}
