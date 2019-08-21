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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.leeds.ccg.andyt.generic.core.Generic_Environment;
//import uk.ac.leeds.ccg.andyt.generic.utilities.Generic_Time;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Environment;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Object;
import uk.ac.leeds.ccg.andyt.projects.saric.data.metoffice.datapoint.SARIC_MetOfficeScraper;
import uk.ac.leeds.ccg.andyt.projects.saric.data.metoffice.nimrod.SARIC_NIMRODDataHandler;
import uk.ac.leeds.ccg.andyt.generic.time.Generic_Time;

/**
 * This is the main processor/controller for SARIC processing. Everything is
 * supposed to be actionable from here.
 *
 * @author geoagdt
 */
public class SARIC_Processor extends SARIC_Object implements Runnable {

    String string_xml;

    public String getString_xml() {
        if (string_xml == null) {
            string_xml = "xml";
        }
        return string_xml;
    }

    public SARIC_Processor(SARIC_Environment se) {
        super(se);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        File dataDir;
        if (args.length != 1) {
            dataDir = new File(System.getProperty("user.dir"), "data");
        } else {
            dataDir = new File(args[0]);
        }
        Generic_Environment ge = new Generic_Environment(dataDir);
        SARIC_Environment se;
        se = new SARIC_Environment(dataDir);
        Generic_Time st;
        st = new Generic_Time(se);
        se.setTime(st);
        SARIC_Processor sp;
        sp = new SARIC_Processor(se);
        sp.run();
    }

    /**
     * This is the main run method for the Digital welfare project.
     *
     */
    @Override
    public void run() {
        try {
            // Main switches
//            RunProjectShapefiles = true;
//            RunCatchmentViewer = true;
//            RunSARIC_MetOfficeScraper = true;
//            RunSARIC_ImageProcessor = true;
//            RunSARIC_CreatePointShapefile = true;
//            RunSARIC_DisplayShapefile = true;
//            RunSARIC_DataForWASIM = true;
//            RunSARIC_DataForWASIM2 = true;
//            RunSARIC_DataForLex = true;
//            RunSARIC_ProcessNIMROD = true;
//            RunSARIC_RainfallStatistics = true;
            RunCatchup = true;
            RunSARIC_FullWorkflow = true;

            /**
             * Run catchup
             */
            if (RunCatchup) {
                doRunSARIC_MetOfficeScraper(null, 0, true);
                // Wait for 5 minutes
                TimeUnit.MINUTES.sleep(5);
                doRunSARIC_ImageProcessor();
                RunSARIC_DataForLex = true;
            }
            /**
             * RunProjectShapefiles
             */
            if (RunProjectShapefiles) {
//                doWissey = true;
                doTeifi = true;
//                addGBHRUs = true;
                SARIC_ProjectShapefiles p;
                p = new SARIC_ProjectShapefiles(se, doWissey, doTeifi);
                //sdv.run();
                Thread t;
                t = new Thread(p);
                t.start();
            }

            /**
             * RunCatchmentViewer
             */
            if (RunCatchmentViewer) {
//                SARIC_CatchmentViewer scv;
//                scv = new SARIC_CatchmentViewer(se);
//                //scv.run();
//                Thread scvThread;
//                    scvThread = new Thread(scv);
//                    scvThread.start();
//                doWissey = true;
                doTeifi = true;
//                addGBHRUs = true;
                SARIC_DataViewer sdv;
                sdv = new SARIC_DataViewer(se, doWissey, doTeifi, addGBHRUs);
                //sdv.run();
                Thread sdvThread;
                sdvThread = new Thread(sdv);
                sdvThread.start();
            }

            /**
             * Run SARIC_MetOfficeScraper
             */
            if (RunSARIC_MetOfficeScraper) {
                doRunSARIC_MetOfficeScraper(null, 0, true);
            }

            /**
             * Run SARIC_ImageProcessor
             */
            if (RunSARIC_ImageProcessor) {
                doRunSARIC_ImageProcessor();
            }
        } catch (Exception | Error e) {
            System.err.println(e.getLocalizedMessage());
            e.printStackTrace(System.err);
        }

        if (RunSARIC_CreatePointShapefile) {
            boolean doForecasts;
            boolean doObservations;
            doForecasts = true;
            doObservations = true;
            overwrite = false;
            SARIC_CreatePointShapefiles p;
            p = new SARIC_CreatePointShapefiles(se, doForecasts, doObservations,
                    overwrite);
            p.run();
        }

        if (RunSARIC_DisplayShapefile) {
            SARIC_DisplayShapefile p;
            p = new SARIC_DisplayShapefile(se);
            p.run();
        }

        if (RunSARIC_DataForWASIM) {
            SARIC_DataForWASIM p;
            p = new SARIC_DataForWASIM(se);
            p.run();
        }

        if (RunSARIC_DataForWASIM2) {
            SARIC_DataForWASIM2 p;
            p = new SARIC_DataForWASIM2(se);
            p.run();
        }

        if (RunSARIC_DataForLex) {
            skip10 = true;
//            dolast5days = true;
            SARIC_DataForLex p;
            for (int estimateType = -1; estimateType < 2; estimateType++) {
                p = new SARIC_DataForLex(se, estimateType, skip10, dolast5days);
                p.run();
            }
        }

        if (RunSARIC_FullWorkflow) {
            long midnight;
            midnight = LocalDateTime.now().until(LocalDate.now().plusDays(1).atStartOfDay(), ChronoUnit.MINUTES);
            long fouram;
            long four30am;
            long fiveam;
            fouram = midnight + TimeUnit.HOURS.toMinutes(4);
            four30am = midnight + TimeUnit.HOURS.toMinutes(4) + 30;
            fiveam = midnight + TimeUnit.HOURS.toMinutes(5);
            ScheduledExecutorService scheduler;
            scheduler = Executors.newScheduledThreadPool(1);
            // Start the scraper now and keep it running if it is not already running.
            if (!RunCatchup) {
                doRunSARIC_MetOfficeScraper(null, 0, true);
            }
            // Kick the scraper at 4am to get all the latest results before processing them.
            doRunSARIC_MetOfficeScraper(scheduler, fouram, false);
            System.out.println("Scheduling processing...");
            // For ImageProcessing of Observations
            SARIC_ImageProcessor[] pIPO;
            pIPO = new SARIC_ImageProcessor[3];
            SARIC_ImageProcessor[] pIPF;
            pIPF = new SARIC_ImageProcessor[3];
            SARIC_DataForLex[] p;
            p = new SARIC_DataForLex[3];

            // Main Switches
            doImageProcessObservations = true;
//        doImageProcessObservations = false;
            doProcessForecasts = true;
//                doProcessForecasts = false;
            doWissey = true;
//                doWissey = false;
            doTeifi = true;
//                doTeifi = false;

            File dirIn;
            File dirOut;
            boolean outputGreyScale;
            int colorDupication;
//                outputGreyScale = false;
            outputGreyScale = true;
            colorDupication = 0;
            for (int estimateType = -1; estimateType < 2; estimateType++) {

                if (doImageProcessObservations) {
                    // Switches
                    doNonTiledObs = false;
                    doNonTiledFcs = false;
                    doTileFromWMTSService = true;
                    doObservationsTileFromWMTSService = true;
                    doForecastsTileFromWMTSService = false;
                    overwrite = false;
                    dirIn = Files.getInputDataMetOfficeDataPointDir();
                    dirOut = Files.getOutputDataMetOfficeDataPointDir();
                    pIPO[estimateType + 1] = new SARIC_ImageProcessor(se, dirIn,
                            dirOut, doNonTiledFcs, doNonTiledObs,
                            doTileFromWMTSService,
                            doObservationsTileFromWMTSService,
                            doForecastsTileFromWMTSService, doWissey, doTeifi,
                            overwrite, estimateType, outputGreyScale,
                            colorDupication);
                    scheduler.scheduleAtFixedRate(pIPO[estimateType + 1], four30am, TimeUnit.DAYS.toMinutes(1), TimeUnit.MINUTES);
                }

                if (doProcessForecasts) {
                    // Switches
//                        doNonTiledFcs = false;
                    doNonTiledFcs = true;
//                        doTileFromWMTSService = false;
                    doTileFromWMTSService = true;
                    doObservationsTileFromWMTSService = false;
                    doForecastsTileFromWMTSService = true;
//                        doForecastsTileFromWMTSService = false;
                    overwrite = false;
                    dirIn = Files.getInputDataMetOfficeDataPointDir();
                    dirOut = Files.getOutputDataMetOfficeDataPointDir();
                    pIPF[estimateType + 1] = new SARIC_ImageProcessor(se, dirIn,
                            dirOut, doNonTiledFcs, doNonTiledObs,
                            doTileFromWMTSService,
                            doObservationsTileFromWMTSService,
                            doForecastsTileFromWMTSService, doWissey, doTeifi,
                            overwrite, estimateType, outputGreyScale,
                            colorDupication);
                    scheduler.scheduleAtFixedRate(pIPF[estimateType + 1], four30am, TimeUnit.DAYS.toMinutes(1), TimeUnit.MINUTES);
                }
            }

            skip10 = false;
            dolast5days = true;
            for (int estimateType = -1; estimateType < 2; estimateType++) {
                p[estimateType + 1] = new SARIC_DataForLex(se, estimateType, skip10, dolast5days);
                scheduler.scheduleAtFixedRate(p[estimateType + 1], fiveam, TimeUnit.DAYS.toMinutes(1), TimeUnit.MINUTES);
            }
            try {
                // waits for termination for 1000 days
                scheduler.awaitTermination(1000, TimeUnit.DAYS);
            } catch (InterruptedException ex) {
                Logger.getLogger(SARIC_Processor.class.getName()).log(Level.SEVERE, null, ex);
            }

//            long timeDelay = 1000 * 60 * 60 * 24;
//            while (true) {
//                doRunSARIC_ImageProcessor();
//                
//                for (int estimateType = -1; estimateType < 2; estimateType++) {
//                    p = new SARIC_DataForLex(se, estimateType);
//                    p.run();
//                }
//                synchronized (this) {
//                    try {
//                        this.wait(timeDelay);
//                    } catch (InterruptedException ex) {
//                        Logger.getLogger(SARIC_MetOfficeScraper.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                    System.out.println("Waited " + uk.ac.leeds.ccg.andyt.generic.utilities.Generic_Time.getTime(timeDelay) + ".");
//                }
//            }
        }

        if (RunSARIC_ProcessNIMROD) {
            doWissey = true;
//            doWissey = false;
            doTeifi = true;
//            doTeifi = false;
            SARIC_NIMRODDataHandler p;
            p = new SARIC_NIMRODDataHandler(se, doWissey, doTeifi);
            p.run();
        }

        if (RunSARIC_RainfallStatistics) {
            doWissey = true;
            doWissey = false;
            doTeifi = true;
//            doTeifi = false;
            overwrite = true;
            File dirIn;
            dirIn = Files.getInputDataMetOfficeDataPointDir();
            File dirOut;
            dirOut = Files.getOutputDataMetOfficeDataPointDir();
            SARIC_RainfallStatistics p;
            p = new SARIC_RainfallStatistics(se, dirIn, dirOut, doWissey,
                    doTeifi, overwrite);
            p.run();
        }
    }

    protected void doRunSARIC_MetOfficeScraper(
            ScheduledExecutorService scheduler, long timeAfterMidnightInMinutes, boolean iterate) {
        long timeDelay;
        String name;
        // Main Switches
//                doCalculateSitesInStudyAreas = true; // This is usually a one off.
//                doNonTiledObs = true;
//                doNonTiledFcs = true;
//                doTileFromWMTSService = true;
//                doObservationsTileFromWMTSService = true;
//                doTileFromWMTSService = true;
//                doForecastsTileFromWMTSService = true;
//                // All
//                doNonTiledObs = true;
        doNonTiledFcs = true;
        doTileFromWMTSService = true;
        doForecastsTileFromWMTSService = true;
        doObservationsTileFromWMTSService = true;
        /**
         * This thread parses the site list and returns an ArrayList<Integer> of
         * siteIDs for the sites that are within our study areas of the Wissey
         * and Teifi catchments. This needs improving so that the study areas
         * are buffered.
         */
        if (doCalculateSitesInStudyAreas) {
            CalculateForecastsSitesInStudyAreas = true;
            CalculateForecastsSitesInStudyAreas = false;
            CalculateObservationsSitesInStudyAreas = true;
            Observations = false;
            Forecasts = false;
            TileFromWMTSService = false;
            ObservationsSiteList = false;
            ObservationsTileFromWMTSService = false;
            ForecastsTileFromWMTSService = false;
            ForecastsSiteList = false;
            ForecastsForSites = false;
            ForecastsForSites = false;
            ObservationsForSites = false;
            timeDelay = (long) (uk.ac.leeds.ccg.andyt.generic.util.Generic_Time.MilliSecondsInHour * 2.75);
            name = "CalculateSitesInStudyAreas";
            overwrite = false;
            SARIC_MetOfficeScraper p;
            p = new SARIC_MetOfficeScraper(se,
                    CalculateForecastsSitesInStudyAreas,
                    CalculateObservationsSitesInStudyAreas,
                    Observations, Forecasts, TileFromWMTSService,
                    ObservationsTileFromWMTSService,
                    ForecastsTileFromWMTSService,
                    ObservationsSiteList, ForecastsSiteList, ForecastsForSites,
                    ObservationsForSites, timeDelay, name, overwrite,
                    getString_xml(), iterate);
            if (scheduler == null) {
                Thread t;
                t = new Thread(p);
                t.start();
            } else {
                scheduler.scheduleAtFixedRate(p, timeAfterMidnightInMinutes, TimeUnit.DAYS.toMinutes(1), TimeUnit.MINUTES);
            }
        }

        /**
         * New data is supposed to be released hourly. The most recent forecast
         * will be the most useful looking forward, but to analyse and process
         * data it is probably useful to keep all forecasts.
         */
        if (doNonTiledFcs) {
            //CalculateForecastsSitesInStudyAreas = true; // Only need to do this once or if file is lost.
            CalculateForecastsSitesInStudyAreas = false;
            CalculateObservationsSitesInStudyAreas = false;
            Observations = false;
            Forecasts = false;
            TileFromWMTSService = false;
            ObservationsTileFromWMTSService = false;
            ForecastsTileFromWMTSService = false;
//                    ForecastsSiteList = true;
            ForecastsSiteList = false;
            ForecastsForSites = true;
            ObservationsSiteList = false;
            ObservationsForSites = false;
            timeDelay = (long) (uk.ac.leeds.ccg.andyt.generic.util.Generic_Time.MilliSecondsInHour * 1);
            name = "Forecasts";
            overwrite = false;
            SARIC_MetOfficeScraper p;
            p = new SARIC_MetOfficeScraper(se,
                    CalculateForecastsSitesInStudyAreas,
                    CalculateObservationsSitesInStudyAreas,
                    Observations, Forecasts, TileFromWMTSService,
                    ObservationsTileFromWMTSService,
                    ForecastsTileFromWMTSService,
                    ObservationsSiteList, ForecastsSiteList,
                    ForecastsForSites, ObservationsForSites,
                    timeDelay, name, overwrite, getString_xml(), iterate);
            if (scheduler == null) {
                Thread t;
                t = new Thread(p);
                t.start();
            } else {
                //scheduler.scheduleAtFixedRate(p, timeAfterMidnightInMinutes, TimeUnit.DAYS.toMinutes(1), TimeUnit.MINUTES);
                scheduler.scheduleAtFixedRate(p, timeAfterMidnightInMinutes, 1, TimeUnit.HOURS);
            }
        }

        if (doNonTiledObs) {
            CalculateForecastsSitesInStudyAreas = false;
            CalculateObservationsSitesInStudyAreas = false;
            Observations = false;
            Forecasts = false;
            TileFromWMTSService = false;
            ObservationsTileFromWMTSService = false;
            ForecastsTileFromWMTSService = false;
            ForecastsSiteList = false;
            ForecastsForSites = false;
            ObservationsForSites = true;
            ObservationsSiteList = true;
            timeDelay = (long) (uk.ac.leeds.ccg.andyt.generic.util.Generic_Time.MilliSecondsInHour * 5.5);
            name = "Observations";
            overwrite = false;
            SARIC_MetOfficeScraper p;
            p = new SARIC_MetOfficeScraper(se,
                    CalculateForecastsSitesInStudyAreas,
                    CalculateObservationsSitesInStudyAreas,
                    Observations, Forecasts, TileFromWMTSService,
                    ObservationsTileFromWMTSService,
                    ForecastsTileFromWMTSService,
                    ObservationsSiteList, ForecastsSiteList, ForecastsForSites,
                    ObservationsForSites, timeDelay, name, overwrite,
                    getString_xml(), iterate);
            if (scheduler == null) {
                Thread t;
                t = new Thread(p);
                t.start();
            } else {
                scheduler.scheduleAtFixedRate(p, timeAfterMidnightInMinutes, TimeUnit.DAYS.toMinutes(1), TimeUnit.MINUTES);
            }
        }

        /**
         * Forecasts thread gets data every 5.5 hours. New data is supposed to
         * be released for every 6 hours. There is one release marked for each
         * of these times: 3am, 9am, 3pm, 9pm. At each of these times there are
         * forecasts for 3 hourly intervals for up to 36 hours (12 forecasts).
         * So, there are 6 forecasts for any time. The most recent forecast will
         * be the most useful, but sometimes we have to look a long way ahead.
         * It may be that there are longer term forecasts made for the sites
         * where there are ground observations.
         */
        if (doTileFromWMTSService) {
            CalculateForecastsSitesInStudyAreas = false;
            CalculateObservationsSitesInStudyAreas = false;
            Observations = false;
            Forecasts = false;
            TileFromWMTSService = true;
            ObservationsSiteList = false;
            ForecastsSiteList = false;
            ForecastsForSites = false;
            ObservationsForSites = false;

            if (doObservationsTileFromWMTSService) {
                ObservationsTileFromWMTSService = true;
                ForecastsTileFromWMTSService = false;
                timeDelay = (long) (uk.ac.leeds.ccg.andyt.generic.util.Generic_Time.MilliSecondsInHour * 1);
                name = "Higher Resolution Tiled Forecasts and Observations";
                overwrite = false;
                SARIC_MetOfficeScraper p;
                p = new SARIC_MetOfficeScraper(
                        se, CalculateForecastsSitesInStudyAreas,
                        CalculateObservationsSitesInStudyAreas,
                        Observations, Forecasts, TileFromWMTSService,
                        ObservationsTileFromWMTSService,
                        ForecastsTileFromWMTSService,
                        ObservationsSiteList, ForecastsSiteList,
                        ForecastsForSites, ObservationsForSites,
                        timeDelay, name, overwrite, getString_xml(), iterate);
                if (scheduler == null) {
                    Thread t;
                    t = new Thread(p);
                    t.start();
                } else {
                    //scheduler.scheduleAtFixedRate(p, timeAfterMidnightInMinutes, TimeUnit.DAYS.toMinutes(1), TimeUnit.MINUTES);
                    scheduler.scheduleAtFixedRate(p, timeAfterMidnightInMinutes, 1, TimeUnit.HOURS);
                }
            }
            if (doForecastsTileFromWMTSService) {
                CalculateForecastsSitesInStudyAreas = false;
                CalculateObservationsSitesInStudyAreas = false;
                ObservationsTileFromWMTSService = false;
                ForecastsTileFromWMTSService = true;
                //timeDelay = (long) (uk.ac.leeds.ccg.andyt.generic.utilities.Generic_Time.MilliSecondsInHour * 6);
                timeDelay = (long) (uk.ac.leeds.ccg.andyt.generic.util.Generic_Time.MilliSecondsInHour * 1);
                name = "Higher Resolution Tiled Forecasts and Observations";
                overwrite = false;
                SARIC_MetOfficeScraper p;
                p = new SARIC_MetOfficeScraper(
                        se, CalculateForecastsSitesInStudyAreas,
                        CalculateObservationsSitesInStudyAreas,
                        Observations, Forecasts, TileFromWMTSService,
                        ObservationsTileFromWMTSService,
                        ForecastsTileFromWMTSService,
                        ObservationsSiteList, ForecastsSiteList,
                        ForecastsForSites, ObservationsForSites,
                        timeDelay, name, overwrite, getString_xml(), iterate);
                if (scheduler == null) {
                    Thread t;
                    t = new Thread(p);
                    t.start();
                } else {
                    //scheduler.scheduleAtFixedRate(p, timeAfterMidnightInMinutes, 6, TimeUnit.HOURS);
                    scheduler.scheduleAtFixedRate(p, timeAfterMidnightInMinutes, 1, TimeUnit.HOURS);
                }
            }
        }
    }

    protected void doRunSARIC_ImageProcessor() {
        // Main Switches
        doImageProcessObservations = true;
//        doImageProcessObservations = false;
        doProcessForecasts = true;
//                doProcessForecasts = false;
        doWissey = true;
//                doWissey = false;
        doTeifi = true;
//                doTeifi = false;

        boolean outputGreyScale;
        int colorDupication;
//                outputGreyScale = false;
        outputGreyScale = true;
        colorDupication = 0;
        for (int estimateType = -1; estimateType < 2; estimateType++) {

            if (doImageProcessObservations) {
                // Switches
                doNonTiledObs = false;
                doNonTiledFcs = false;
                doTileFromWMTSService = true;
                doObservationsTileFromWMTSService = true;
                doForecastsTileFromWMTSService = false;
                overwrite = false;
                File dirIn;
                dirIn = Files.getInputDataMetOfficeDataPointDir();
                File dirOut;
                dirOut = Files.getOutputDataMetOfficeDataPointDir();
                SARIC_ImageProcessor p;
                p = new SARIC_ImageProcessor(se, dirIn,
                        dirOut, doNonTiledFcs, doNonTiledObs,
                        doTileFromWMTSService,
                        doObservationsTileFromWMTSService,
                        doForecastsTileFromWMTSService, doWissey, doTeifi,
                        overwrite, estimateType, outputGreyScale,
                        colorDupication);
//                Thread t;
//                t = new Thread(p);
//                t.start();
                p.run();
            }

            if (doProcessForecasts) {
                // Switches
//                        doNonTiledFcs = false;
                doNonTiledFcs = true;
//                        doTileFromWMTSService = false;
                doTileFromWMTSService = true;
                doObservationsTileFromWMTSService = false;
                doForecastsTileFromWMTSService = true;
//                        doForecastsTileFromWMTSService = false;
                overwrite = false;
                File dirIn;
                dirIn = Files.getInputDataMetOfficeDataPointDir();
                File dirOut;
                dirOut = Files.getOutputDataMetOfficeDataPointDir();
                SARIC_ImageProcessor p;
                p = new SARIC_ImageProcessor(se, dirIn,
                        dirOut, doNonTiledFcs, doNonTiledObs,
                        doTileFromWMTSService,
                        doObservationsTileFromWMTSService,
                        doForecastsTileFromWMTSService, doWissey, doTeifi,
                        overwrite, estimateType, outputGreyScale,
                        colorDupication);
//                Thread t;
//                t = new Thread(p);
//                t.start();
                p.run();
            }
        }
    }

    // Parameters
    boolean CalculateForecastsSitesInStudyAreas = false;
    boolean CalculateObservationsSitesInStudyAreas = false;
    boolean Observations = false;
    boolean Forecasts = false;
    boolean TileFromWMTSService = false;
    boolean ObservationsTileFromWMTSService = false;
    boolean ForecastsTileFromWMTSService = false;
    boolean ObservationsSiteList = false;
    boolean ForecastsSiteList = false;
    boolean ForecastsForSites = false;
    boolean ObservationsForSites = false;
    boolean overwrite = false;

    // Switches
    boolean doCalculateSitesInStudyAreas = false;
    boolean doNonTiledObs = false;
    boolean doNonTiledFcs = false;
    boolean doTileFromWMTSService = false;
    boolean doObservationsTileFromWMTSService = false;
    boolean doForecastsTileFromWMTSService = false;
    boolean doWissey = false;
    boolean doTeifi = false;
    boolean addGBHRUs = false;
    boolean doImageProcessObservations = false;
    boolean doProcessForecasts = false;
    boolean skip10;
    boolean dolast5days;

    // Main switches
    boolean RunProjectShapefiles = false;
    boolean RunCatchmentViewer = false;
    boolean RunSARIC_MetOfficeScraper = false;
    boolean RunSARIC_ImageProcessor = false;
    boolean RunSARIC_CreatePointShapefile = false;
    boolean RunSARIC_DisplayShapefile = false;
    boolean RunSARIC_DataForWASIM = false;
    boolean RunSARIC_DataForWASIM2 = false;
    boolean RunSARIC_DataForLex = false;
    boolean RunSARIC_FullWorkflow = false;
    boolean RunCatchup = false;
    boolean RunSARIC_ProcessNIMROD = false;
    boolean RunSARIC_RainfallStatistics = false;
}
