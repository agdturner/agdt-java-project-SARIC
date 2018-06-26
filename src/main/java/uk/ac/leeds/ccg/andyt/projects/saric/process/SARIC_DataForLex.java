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

import com.vividsolutions.jts.geom.Geometry;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotools.data.DataUtilities;
import org.geotools.feature.SchemaException;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import uk.ac.leeds.ccg.andyt.generic.io.Generic_StaticIO;
import uk.ac.leeds.ccg.andyt.geotools.Geotools_Shapefile;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridDouble;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridDoubleFactory;
import uk.ac.leeds.ccg.andyt.grids.core.grid.stats.Grids_GridDoubleStatsNotUpdated;
import uk.ac.leeds.ccg.andyt.grids.io.Grids_Files;
import uk.ac.leeds.ccg.andyt.grids.process.Grids_Processor;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Environment;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Object;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Strings;
import uk.ac.leeds.ccg.andyt.projects.saric.data.catchment.SARIC_Catchment;
import uk.ac.leeds.ccg.andyt.projects.saric.data.catchment.SARIC_Teifi;
import uk.ac.leeds.ccg.andyt.projects.saric.data.catchment.SARIC_Wissey;
import uk.ac.leeds.ccg.andyt.projects.saric.io.SARIC_Files;
import uk.ac.leeds.ccg.andyt.generic.utilities.time.Generic_Date;
import uk.ac.leeds.ccg.andyt.generic.utilities.time.Generic_Time;
import uk.ac.leeds.ccg.andyt.generic.utilities.time.Generic_YearMonth;
import uk.ac.leeds.ccg.andyt.projects.saric.data.lex.SARIC_LexRecord;
import static uk.ac.leeds.ccg.andyt.projects.saric.process.SARIC_ImageProcessor.getEstimateName;
import uk.ac.leeds.ccg.andyt.vector.core.Vector_Environment;
import uk.ac.leeds.ccg.andyt.vector.projection.Vector_OSGBtoLatLon;

/**
 *
 * @author geoagdt
 */
public class SARIC_DataForLex extends SARIC_Object implements Runnable {

    // For convenience
    SARIC_Files sf;
    SARIC_Strings ss;
    Grids_Environment ge;
    Grids_Processor gp;
    Grids_GridDoubleFactory gf;
    Vector_Environment ve;

    /**
     * @param estimateType if estimateType == 1 this is a high estimate,
     * estimateType == -1 this is a low estimate, estimateType == 0 this is a
     * average estimate,
     */
    int estimateType;
    String estimateName;
    boolean skip10;
    boolean dolast5days;

    protected SARIC_DataForLex() {
    }

    public SARIC_DataForLex(SARIC_Environment se, int estimateType, boolean skip10, boolean dolast5days) {
        super(se);
        sf = se.getFiles();
        ss = se.getStrings();
        ge = se.getGrids_Env();
        gp = ge.getProcessor();
        gf = gp.GridDoubleFactory;
        gf.setChunkNCols(62);
        gf.setChunkNRows(40);
        gf.Stats = new Grids_GridDoubleStatsNotUpdated(ge);
        ve = se.getVector_Env();
        this.estimateType = estimateType;
        this.skip10 = skip10;
        this.dolast5days = dolast5days;
    }

    @Override
    public void run() {
        estimateName = getEstimateName(estimateType);
        // Initial day set
//        Generic_Date day0;
        // Number of days after initial day results are output for
//        int numberOfDaysRun;
        // What areas to run for
        ArrayList<String> areas = new ArrayList<>();
        areas.add(ss.getS_Teifi());
        areas.add(ss.getS_Wissey());
        // Fill in gaps or overwrite?
        boolean overwrite;
        overwrite = false;
        //overwrite = true;
//        // Run 1
//        day0 = new Generic_Date(se, "2017-09-06"); 
//        numberOfDaysRun = 7;
//        // Run 2
//        day0 = new Generic_Date(se, "2017-09-21"); 
//        numberOfDaysRun = 9;
//        // Run 3
//        day0 = new Generic_Date(se, "2017-10-01"); 
//        numberOfDaysRun = 8;
//        // Run 4
//        day0 = new Generic_Date(se, "2017-10-11");
//        numberOfDaysRun = 14;
//        // Run 5
//        day0 = new Generic_Date(se, "2017-10-25");
//        numberOfDaysRun = 28;
        // Run 6
//        day0 = new Generic_Date(se, "2017-09-06");
//        numberOfDaysRun = 200;
//        // Run 7
//        day0 = new Generic_Date(se, "2018-03-10");
//        numberOfDaysRun = 10;
        // Declaration
        String area;
        Generic_Date day;
        //TreeMap<Generic_Time, Grids_GridDouble> observationsGrids;

        Grids_GridDouble g;

        int numberOfDaysSinceLastRainfallEventGT2mm;

        TreeMap<Generic_Date, Grids_GridDouble> observationsGrids;

        Grids_GridDouble o10;
        Grids_GridDouble o9;
        Grids_GridDouble o8;
        Grids_GridDouble o7;
        Grids_GridDouble o6;
        Grids_GridDouble o5;
        Grids_GridDouble o4;
        Grids_GridDouble o3;
        Grids_GridDouble o2;
        Grids_GridDouble o1;
        double observedRainfall10DaysAgo;
        double observedRainfall9DaysAgo;
        double observedRainfall8DaysAgo;
        double observedRainfall7DaysAgo;
        double observedRainfall6DaysAgo;
        double observedRainfall5DaysAgo;
        double observedRainfall4DaysAgo;
        double observedRainfall3DaysAgo;
        double observedRainfall2DaysAgo;
        double observedRainfallYesterday;

        Grids_GridDouble f0;
        Grids_GridDouble f1;
        Grids_GridDouble f2;
        Grids_GridDouble f3;
        Grids_GridDouble f4;
        //Grids_GridDouble f5;
        double forecastRainfallInTheNext24Hours;
        double forecastRainfallIn24to48Hours;
        double forecastRainfallIn48to72Hours;
        double forecastRainfallIn72to96HoursHours;
        double forecastRainfallIn96to120Hours;
        //double forecastRainfallIn120to144Hours;
        double noDataValue;

        Grids_GridDouble mask;

        SARIC_LexRecord r;
        File f;
        PrintWriter pw = null;

        //double[] Easting_Northing;
        Iterator<String> iteArea;
        iteArea = areas.iterator();
        while (iteArea.hasNext()) {
            area = iteArea.next();
            observationsGrids = getObservationsGrids(area);
            // GetWaterCompanyShapefile Geometry
            if (area.equalsIgnoreCase(ss.getS_Wissey())) {
                // Wissey (Wissington), latitude: 52.551, longitude: 0.447
                //Easting_Northing = Vector_OSGBtoLatLon.latlon2osgb(52.551, 0.447);
                //Easting_Northing = Vector_OSGBtoLatLon.latlon2osgb(52.6, 0.5);
                SARIC_Wissey sw;
                sw = new SARIC_Wissey(se);
                mask = (Grids_GridDouble) sw.get1KMGridMaskedToCatchment()[0];
            } else {
                // Teifi (Lampeter), latitude: 52.114, longitude: -4.078
                //Easting_Northing = Vector_OSGBtoLatLon.latlon2osgb(52.114, -4.078);
                SARIC_Teifi st;
                st = new SARIC_Teifi(se);
                mask = (Grids_GridDouble) st.get1KMGridMaskedToCatchment()[0];
            }
            long nrows;
            long ncols;
            nrows = mask.getNRows();
            ncols = mask.getNCols();
            noDataValue = mask.getNoDataValue();

            // get days not processed yet
            File dir;
            TreeSet<Generic_Date> daysProcessed;
            TreeSet<Generic_Date> daysToProcess;
            dir = new File("Y:/projects/saric", "Lex");
            dir = new File(dir, area);
            dir = new File(dir, estimateName);
            daysProcessed = getDays(dir);
            dir = new File(sf.getOutputDataMetOfficeDataPointDir(), "/inspire/view/wmts/Wissey/RADAR_UK_Composite_Highres/EPSG_27700_4/l");
            daysToProcess = getDays(dir);
            File dir0;
            //dir0 = sf.getOutputDataDir(ss);
            dir0 = new File("Y:/projects/saric", "Lex");
            if (!overwrite) {
                daysToProcess.removeAll(daysProcessed);
            }
            Iterator<Generic_Date> ite;
            ite = daysToProcess.iterator();
            if (skip10) {
                // Skip the first 10 dates as the first 10 days are needed.
                if (overwrite || (daysProcessed.size() < 10)) {
                    for (int i = 0; i < 10; i++) {
                        day = ite.next();
                        System.out.println("Skip day " + day.getYYYYMMDD());
                    }
                }
            }
            if (dolast5days) {
                for (int i = 0; i < daysToProcess.size() - 5; i++) {
                        day = ite.next();
                        System.out.println("Skip day " + day.getYYYYMMDD());
                    }
            }
//            for (int days = 0; days < numberOfDaysRun; days++) {
//                day = new Generic_Date(day0);
//                day.addDays(days);
            while (ite.hasNext()) {
                day = ite.next();
                //System.out.println("day " + day.getYYYYMMDD());
                f = getFile(dir0, area, day);
                if (f.exists() && overwrite || !f.exists()) {

                    boolean found;
                    int i;
                    double v;

                    o10 = getObservationGrid(observationsGrids, day, -10);
                    o9 = getObservationGrid(observationsGrids, day, -9);
                    o8 = getObservationGrid(observationsGrids, day, -8);
                    o7 = getObservationGrid(observationsGrids, day, -7);
                    o6 = getObservationGrid(observationsGrids, day, -6);
                    o5 = getObservationGrid(observationsGrids, day, -5);
                    o4 = getObservationGrid(observationsGrids, day, -4);
                    o3 = getObservationGrid(observationsGrids, day, -3);
                    o2 = getObservationGrid(observationsGrids, day, -2);
                    o1 = getObservationGrid(observationsGrids, day, -1);
                    f0 = getForecastsGrid(area, day, 0);
                    f1 = getForecastsGrid(area, day, 1);
                    f2 = getForecastsGrid(area, day, 2);
                    f3 = getForecastsGrid(area, day, 3);
                    f4 = getForecastsGrid(area, day, 4);
                    //f5 = getForecastsGrid(area, t, 5);
                    // Assume all grids have the same noDataValue.
                    pw = initialisePrintWriter(f);
                    //int i;
                    //double v;
                    for (long row = 0; row < nrows; row++) {
                        for (long col = 0; col < ncols; col++) {
                            if (mask.getCell(row, col) != noDataValue) {
                                i = 0;
                                numberOfDaysSinceLastRainfallEventGT2mm = 1;
                                found = false;
                                g = observationsGrids.get(day);
                                while (!found) {
                                    //System.out.println(day1);
                                    if (g != null) {
                                        v = g.getCell(row, col);
                                    } else {
                                        v = 0.0d;
                                    }
                                    if (v != noDataValue) {
                                        //accumulation += v;
                                        if (!found) {
                                            if (v > 2) {
                                                found = true;
                                            } else {
                                                numberOfDaysSinceLastRainfallEventGT2mm++;
                                                i--;
                                                g = getObservationGrid(observationsGrids, day, i);
                                                if (g == null) {
                                                    if (i < -10) {
                                                        found = true; // Found that the last rainfall event of greater than 2mm was longer than 10 days ago and we've hit a null obsevation record.
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                //System.out.println("numberOfDaysSinceLastRainfallEventGT2mm " + numberOfDaysSinceLastRainfallEventGT2mm);
                                if (o10 == null) {
                                    observedRainfall10DaysAgo = 0.0d;
                                } else {
                                    observedRainfall10DaysAgo = o10.getCell(row, col);
                                    if (observedRainfall10DaysAgo == noDataValue) {
                                        observedRainfall10DaysAgo = 0.0d;
                                    }
                                }
                                if (o9 == null) {
                                    observedRainfall9DaysAgo = 0.0d;
                                } else {
                                    observedRainfall9DaysAgo = o9.getCell(row, col);
                                    if (observedRainfall9DaysAgo == noDataValue) {
                                        observedRainfall9DaysAgo = 0.0d;
                                    }
                                }
                                if (o8 == null) {
                                    observedRainfall8DaysAgo = 0.0d;
                                } else {
                                    observedRainfall8DaysAgo = o8.getCell(row, col);
                                    if (observedRainfall8DaysAgo == noDataValue) {
                                        observedRainfall8DaysAgo = 0.0d;
                                    }
                                }
                                if (o7 == null) {
                                    observedRainfall7DaysAgo = 0.0d;
                                } else {
                                    observedRainfall7DaysAgo = o7.getCell(row, col);
                                    if (observedRainfall7DaysAgo == noDataValue) {
                                        observedRainfall7DaysAgo = 0.0d;
                                    }
                                }
                                if (o6 == null) {
                                    observedRainfall6DaysAgo = 0.0d;
                                } else {
                                    observedRainfall6DaysAgo = o6.getCell(row, col);
                                    if (observedRainfall6DaysAgo == noDataValue) {
                                        observedRainfall6DaysAgo = 0.0d;
                                    }
                                }
                                if (o5 == null) {
                                    observedRainfall5DaysAgo = 0.0d;
                                } else {
                                    observedRainfall5DaysAgo = o5.getCell(row, col);
                                    if (observedRainfall5DaysAgo == noDataValue) {
                                        observedRainfall5DaysAgo = 0.0d;
                                    }
                                }
                                if (o4 == null) {
                                    observedRainfall4DaysAgo = 0.0d;
                                } else {
                                    observedRainfall4DaysAgo = o4.getCell(row, col);
                                    if (observedRainfall4DaysAgo == noDataValue) {
                                        observedRainfall4DaysAgo = 0.0d;
                                    }
                                }
                                if (o3 == null) {
                                    observedRainfall3DaysAgo = 0.0d;
                                } else {
                                    observedRainfall3DaysAgo = o3.getCell(row, col);
                                    if (observedRainfall3DaysAgo == noDataValue) {
                                        observedRainfall3DaysAgo = 0.0d;
                                    }
                                }
                                if (o2 == null) {
                                    observedRainfall2DaysAgo = 0.0d;
                                } else {
                                    observedRainfall2DaysAgo = o2.getCell(row, col);
                                    if (observedRainfall2DaysAgo == noDataValue) {
                                        observedRainfall2DaysAgo = 0.0d;
                                    }
                                }
                                if (o1 == null) {
                                    observedRainfallYesterday = 0.0d;
                                } else {
                                    observedRainfallYesterday = o1.getCell(row, col);
                                    if (observedRainfallYesterday == noDataValue) {
                                        observedRainfallYesterday = 0.0d;
                                    }
                                }
                                if (f0 == null) {
                                    forecastRainfallInTheNext24Hours = 0.0d;
                                } else {
                                    forecastRainfallInTheNext24Hours = f0.getCell(row, col);
                                    if (forecastRainfallInTheNext24Hours == noDataValue) {
                                        forecastRainfallInTheNext24Hours = 0.0d;
                                    }
                                }
                                if (f1 == null) {
                                    forecastRainfallIn24to48Hours = 0.0d;
                                } else {
                                    forecastRainfallIn24to48Hours = f1.getCell(row, col);
                                    if (forecastRainfallIn24to48Hours == noDataValue) {
                                        forecastRainfallIn24to48Hours = 0.0d;
                                    }
                                }
                                if (f2 == null) {
                                    forecastRainfallIn48to72Hours = 0.0d;
                                } else {
                                    forecastRainfallIn48to72Hours = f2.getCell(row, col);
                                    if (forecastRainfallIn48to72Hours == noDataValue) {
                                        forecastRainfallIn48to72Hours = 0.0d;
                                    }
                                }
                                if (f3 == null) {
                                    forecastRainfallIn72to96HoursHours = 0.0d;
                                } else {
                                    forecastRainfallIn72to96HoursHours = f3.getCell(row, col);
                                    if (forecastRainfallIn72to96HoursHours == noDataValue) {
                                        forecastRainfallIn72to96HoursHours = 0.0d;
                                    }
                                }
                                if (f4 == null) {
                                    forecastRainfallIn96to120Hours = 0.0d;
                                } else {
                                    forecastRainfallIn96to120Hours = f4.getCell(row, col);
                                    if (forecastRainfallIn96to120Hours == noDataValue) {
                                        forecastRainfallIn96to120Hours = 0.0d;
                                    }
                                }
//                            if (f5 == null) {
//                                forecastRainfallIn120to144Hours = 0.0d;
//                            } else {
//                                forecastRainfallIn120to144Hours = f5.getCell(row, col);
//                                if (forecastRainfallIn120to144Hours == noDataValue) {
//                                    forecastRainfallIn120to144Hours = 0.0d;
//                                }
//                            }

                                r = new SARIC_LexRecord(
                                        Long.toString(row * ncols + col),//mask.getCellID(row, col).toString(),//day.getYYYYMMDD(),
                                        row,//Easting_Northing[1],
                                        col,//Easting_Northing[0],
                                        mask.getCellYDouble(row),
                                        mask.getCellXDouble(col),
                                        numberOfDaysSinceLastRainfallEventGT2mm,
                                        observedRainfall10DaysAgo,
                                        observedRainfall9DaysAgo,
                                        observedRainfall8DaysAgo,
                                        observedRainfall7DaysAgo,
                                        observedRainfall6DaysAgo,
                                        observedRainfall5DaysAgo,
                                        observedRainfall4DaysAgo,
                                        observedRainfall3DaysAgo,
                                        observedRainfall2DaysAgo,
                                        observedRainfallYesterday,
                                        forecastRainfallInTheNext24Hours,
                                        forecastRainfallIn24to48Hours,
                                        forecastRainfallIn48to72Hours,
                                        forecastRainfallIn72to96HoursHours,
                                        forecastRainfallIn96to120Hours);
                                System.out.println(r.toString());
                                if (pw != null) {
                                    pw.println(r.toString());
                                }
                            }
                        }
                        if (pw != null) {
                            pw.flush();
                        }
                    }
                } else {
                    System.out.println("File " + f.toString() + " already exists and is not being overwriiten.");
                }
                //ID++;
//                    }
//                }
            }
            if (pw != null) {
                pw.close();
            }
        }
    }

    TreeSet<Generic_Date> getDays(File dir) {
        TreeSet<Generic_Date> result;
        result = new TreeSet<>();
        Generic_Date date;
        File[] files0;
        File[] files1;
        files0 = dir.listFiles();
        if (files0 != null) {
            for (int i = 0; i < files0.length; i++) {
                files1 = files0[i].listFiles();
                for (int j = 0; j < files1.length; j++) {
                    date = new Generic_Date(se, files1[j].getName());
                    result.add(date);
                }
            }
        }
        return result;
    }

    File getDir(File dir0, String area) {
        File dir;
        //dir = new File(dir0, "Lex");
        dir = new File(dir0, area);
        return dir;
    }

    File getFile(File dir0, String area, Generic_Date day) {
        File result;
        File dir;
        dir = getDir(dir0, area);
        dir = new File(dir, estimateName);
        dir = new File(dir, day.getYYYYMM());
        dir = new File(dir, day.getYYYYMMDD());
        dir.mkdirs();
        String filename;
        filename = day + ".csv";
        result = new File(dir, filename);
        return result;
    }

    /**
     * Initialise the PrintWriter for the output
     */
    PrintWriter initialisePrintWriter(File f) {
        PrintWriter result;
        result = Generic_StaticIO.getPrintWriter(f, false);
        result.println(//"ID,EASTING,NORTHING,"
                "ID,Col,Row,Northing,Easting,"
                + "NumberOfDaysSinceLastRainfallEventGT2mm,"
                + "ObservedRainfall10DaysAgo,"
                + "ObservedRainfall9DaysAgo,"
                + "ObservedRainfall8DaysAgo,"
                + "ObservedRainfall7DaysAgo,"
                + "ObservedRainfall6DaysAgo,"
                + "ObservedRainfall5DaysAgo,"
                + "ObservedRainfall4DaysAgo,"
                + "ObservedRainfall3DaysAgo,"
                + "ObservedRainfall2DaysAgo,"
                + "ObservedRainfallYesterday,"
                + "ForecastRainfallInTheNext24Hours,"
                + "ForecastRainfallIn24to48Hours,"
                + "ForecastRainfallIn48to72Hours,"
                + "ForecastRainfallIn72to96HoursHours,"
                + "ForecastRainfallIn96to120Hours");
        return result;
    }

    Geometry getWaterCompanyShapefileGeometry(String area) {
        Geometry result;
        SARIC_Catchment sc = null;
        if (area.equalsIgnoreCase(ss.getS_Teifi())) {
            sc = new SARIC_Teifi(se);
        } else if (area.equalsIgnoreCase(ss.getS_Wissey())) {
            sc = new SARIC_Wissey(se);
        }
        Geotools_Shapefile shpf;
        shpf = sc.getWaterCompanyAGDT_Shapefile();
        SimpleFeature feature2 = null;
        try {
            feature2 = (SimpleFeature) shpf.getFeatureSource().getFeatures().features().next();

        } catch (IOException ex) {
            Logger.getLogger(SARIC_DataForLex.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        result = (Geometry) feature2.getDefaultGeometry();
        // Describe result
        String geometryType;
        geometryType = result.getGeometryType();
        System.out.println("geometryType " + geometryType);
        System.out.println("result " + result.toString());
        System.out.println("result " + result.toText());
        return result;
    }

    protected Grids_GridDouble getObservationGrid(String area, Generic_Date d,
            int offset) {
        Grids_Files gridf;
        gridf = ge.getFiles();
        Generic_Date d1;
        d1 = new Generic_Time(d);
        d1.addDays(offset);
        Grids_GridDouble result;
        File dir;
        dir = new File(sf.getOutputDataMetOfficeDataPointDir(),
                ss.getS_inspire());
        dir = new File(dir, ss.getS_view());
        //dir = new File(dir, ss.getS_wmts() + "0");
        dir = new File(dir, ss.getS_wmts());
        dir = new File(dir, area);
        dir = new File(dir, ss.getS_RADAR_UK_Composite_Highres());
        dir = new File(dir, "EPSG_27700_4");
        dir = new File(dir, estimateName);
        File f = new File(sf.getNestedTimeDirectory(dir, d1),
                ss.getS_RADAR_UK_Composite_Highres() + ".asc");
        //System.out.println(f);
        if (f.exists()) {
            File gdir;
            gdir = gridf.createNewFile(gridf.getGeneratedGridDoubleDir());
            result = (Grids_GridDouble) gf.create(gdir, f);
            //System.out.println(result);
            return result;
        }
        return null;
    }
    
    protected Grids_GridDouble getObservationGrid(
            TreeMap<Generic_Date, Grids_GridDouble> observationsGrids, 
            Generic_Date d,
            int offset) {
        Grids_GridDouble result;
        Generic_Date d1;
        d1 = new Generic_Time(d);
        d1.addDays(offset);
        result = observationsGrids.get(d1);
        return result;
    }
        

    protected Grids_GridDouble getForecastsGrid(String area, Generic_Date d,
            int offset) {
        Grids_Files gridf;
        gridf = ge.getFiles();
        Generic_Date d1;
        d1 = new Generic_Time(d);
        d1.addDays(offset);
        Grids_GridDouble result;
        File dir;
        dir = new File(sf.getOutputDataMetOfficeDataPointDir(),
                ss.getS_inspire());
        dir = new File(dir, ss.getS_view());
        //dir = new File(dir, ss.getS_wmts() + "0");
        dir = new File(dir, ss.getS_wmts());
        dir = new File(dir, area);
        dir = new File(dir, ss.getS_Precipitation_Rate());
        dir = new File(dir, "EPSG_27700_4");
        dir = new File(dir, estimateName);
        if (offset < 2) {
            File f = new File(sf.getNestedTimeDirectory(dir, d),
                    d.getYYYYMMDD() + "_ForecastFor_" + d1.getYYYYMMDD() + ".asc");
            //System.out.println(f);
            if (f.exists()) {
                File gdir;
                gdir = gridf.createNewFile(gridf.getGeneratedGridDoubleDir());
                result = (Grids_GridDouble) gf.create(gdir, f);
                //System.out.println(result);
                return result;
            }
        } else {
            // System.out.println("Load in some other data from the longer range forecasts.");
            dir = new File(sf.getOutputDataMetOfficeDataPointDir(),
                    ss.getS_val());
            dir = new File(dir, ss.getS_wxfcs());
            dir = new File(dir, ss.getS_all());
            dir = new File(dir, ss.getS_xml());
            dir = new File(dir, area);
            dir = new File(dir, estimateName);
            dir = new File(dir, d.getYYYYMM());
            dir = new File(dir, d.getYYYYMMDD());
            dir = new File(dir, d.getYYYYMMDD() + "-00");
            File f = new File(dir,
                    d.getYYYYMMDD() + "-00_ForecastFor_" + d1.getYYYYMMDD() + ".asc");
            //System.out.println(f);
            if (f.exists()) {
                File gdir;
                gdir = gridf.createNewFile(gridf.getGeneratedGridDoubleDir());
                result = (Grids_GridDouble) gf.create(gdir, f);
                //System.out.println(result);
                return result;
            }
        }
        return null;
    }

    protected TreeMap<Generic_Date, Grids_GridDouble> getObservationsGrids(
            String area) {
        Grids_Files gridf;
        gridf = ge.getFiles();
        TreeMap<Generic_Date, Grids_GridDouble> result;
        result = new TreeMap<>();
        File dir;
        dir = new File(sf.getOutputDataMetOfficeDataPointDir(),
                ss.getS_inspire());
        dir = new File(dir, ss.getS_view());
        //dir = new File(dir, ss.getS_wmts() + "0");
        dir = new File(dir, ss.getS_wmts());
        dir = new File(dir, area);
        dir = new File(dir, ss.getS_RADAR_UK_Composite_Highres());
        dir = new File(dir, "EPSG_27700_4");
        dir = new File(dir, estimateName);
        System.out.println(dir);
        File[] dirs;
        dirs = dir.listFiles();
        String[] dates;
        File f;
        File dir3;
        Generic_Date d;
        Grids_GridDouble g;
        // Load each grid
        for (File dir2 : dirs) {
            //System.out.println(dir2);
            dates = dir2.list();
            for (String date : dates) {
                //System.out.println(date);
                d = new Generic_Date(se, date);
                dir3 = new File(dir2, date);
                f = new File(dir3,
                        ss.getS_RADAR_UK_Composite_Highres() + ".asc");
                if (f.exists()) {
                    File gdir;
                    gdir = gridf.createNewFile(
                            gridf.getGeneratedGridDoubleDir());
                    g = (Grids_GridDouble) gf.create(gdir, f);
                    //System.out.println(g);
                    result.put(d, g);
                }
            }
        }
        return result;
    }

    private static SimpleFeatureType initSimpleFeatureType(String type,
            String srid) {
        SimpleFeatureType result = null;
        try {
            result = DataUtilities.createType(
                    "Location",
                    "the_geom:" + type + ":srid=" + srid + ","
                    + // <- the geometry attribute
                    "name:String," // <- a String attribute
            );

        } catch (SchemaException ex) {
            Logger.getLogger(SARIC_DataForLex.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public static SimpleFeatureType getPointSimpleFeatureType(String srid) {
        if (!getPointSimpleFeatureTypes().containsKey(srid)) {
            SimpleFeatureType sft;
            sft = initSimpleFeatureType(
                    "Point", srid);
            pointSimpleFeatureTypes.put(
                    srid, sft);
            return sft;
        }
        return pointSimpleFeatureTypes.get(srid);
    }

    public static HashMap<String, SimpleFeatureType> getPointSimpleFeatureTypes() {
        if (pointSimpleFeatureTypes == null) {
            pointSimpleFeatureTypes = new HashMap<>();
            //pointSimpleFeatureTypes = initPointSimpleFeatureTypes();
        }
        return pointSimpleFeatureTypes;
    }

    private static HashMap<String, SimpleFeatureType> pointSimpleFeatureTypes;
    public static final String defaultSRID = "27700";
}
