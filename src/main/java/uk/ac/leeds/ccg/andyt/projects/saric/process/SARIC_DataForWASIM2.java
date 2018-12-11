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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotools.data.DataUtilities;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import uk.ac.leeds.ccg.andyt.generic.io.Generic_IO;
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
import uk.ac.leeds.ccg.andyt.projects.saric.data.wasim.SARIC_WASIMRecord;
import uk.ac.leeds.ccg.andyt.projects.saric.io.SARIC_Files;
import uk.ac.leeds.ccg.andyt.generic.time.Generic_Date;
import uk.ac.leeds.ccg.andyt.generic.time.Generic_Time;
import uk.ac.leeds.ccg.andyt.vector.core.Vector_Environment;
import uk.ac.leeds.ccg.andyt.vector.projection.Vector_OSGBtoLatLon;

/**
 *
 * @author geoagdt
 */
public class SARIC_DataForWASIM2 extends SARIC_Object implements Runnable {

    // For convenience
    SARIC_Files sf;
    SARIC_Strings ss;
    Grids_Environment ge;
    Grids_Processor gp;
    Grids_GridDoubleFactory gf;
    Vector_Environment ve;

    protected SARIC_DataForWASIM2() {
    }

    public SARIC_DataForWASIM2(SARIC_Environment se) {
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
    }

    @Override
    public void run() {

        // Initial day set
        Generic_Date day0;
        // Number of days after initial day results are output for
        int numberOfDaysRun;
        // What areas to run for
        ArrayList<String> areas = new ArrayList<>();
        areas.add(ss.getS_Teifi());
        areas.add(ss.getS_Wissey());
        // Fill in gaps or overwrite?
        boolean overwrite;
//        overwrite = false;
        overwrite = true;
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
        day0 = new Generic_Date(se, "2017-09-06");
        numberOfDaysRun = 100;
//        // Run 7
//        day0 = new Generic_Date(se, "2018-03-10");
//        numberOfDaysRun = 10;
        // Declaration
        String area;
        Generic_Date day;
        TreeMap<Generic_Time, Grids_GridDouble> observationsGrids;

        Grids_GridDouble g;
        int numberOfDaysSinceLastRainfallEventGT2mm;
        double accumulation;
        Generic_Time t0;
        Generic_Time t;

        Grids_GridDouble o1;
        double observedRainfallInTheLast24Hours;

        Grids_GridDouble f1;
        Grids_GridDouble f2;
        Grids_GridDouble f3;
        Grids_GridDouble f4;
        Grids_GridDouble f5;
        double forecastRainfallInTheNext24Hours;
        double forecastRainfallIn24to48Hours;
        double forecastRainfallIn48to72Hours;
        double forecastRainfallIn72to96HoursHours;
        double forecastRainfallIn96to120Hours;
        double noDataValue;

        SARIC_WASIMRecord r;
        File f;
        PrintWriter pw = null;

        double[] Easting_Northing;
        Iterator<String> iteArea;
        iteArea = areas.iterator();
        while (iteArea.hasNext()) {
            area = iteArea.next();
            Iterator<Generic_Time> ite;

            // Load all the observations grids
            observationsGrids = getObservationsGrids(area);

            // GetWaterCompanyShapefile Geometry
            if (area.equalsIgnoreCase(ss.getS_Wissey())) {
                // Wissey (Wissington), latitude: 52.551, longitude: 0.447
                //Easting_Northing = Vector_OSGBtoLatLon.latlon2osgb(52.551, 0.447);
                Easting_Northing = Vector_OSGBtoLatLon.latlon2osgb(52.6, 0.5);
            } else {
                // Teifi (Lampeter), latitude: 52.114, longitude: -4.078
                Easting_Northing = Vector_OSGBtoLatLon.latlon2osgb(52.114, -4.078);
            }
            long row;
            long col;
            g = observationsGrids.firstEntry().getValue();
            row = g.getRow(Easting_Northing[1]);
            col = g.getCol(Easting_Northing[0]);
            // Assume all grids have the same noDataValue.
            noDataValue = g.getNoDataValue();
            f = getFile(area, (int) Easting_Northing[0] + "_" + (int) Easting_Northing[1]);
            if (f.exists() && overwrite || !f.exists()) {
                pw = initialisePrintWriter(f);
            }
            for (int days = 0; days < numberOfDaysRun; days++) {
                day = new Generic_Date(day0);
                day.addDays(days);

                System.out.println("day " + day.getYYYYMMDD());

                o1 = observationsGrids.get(new Generic_Time(day));

                t0 = new Generic_Time(day);
                t = new Generic_Time(day);
                f1 = getForecastsGrid(area, t, 1);
                f2 = getForecastsGrid(area, t, 2);
                f3 = getForecastsGrid(area, t, 3);
                f4 = getForecastsGrid(area, t, 4);
                f5 = getForecastsGrid(area, t, 5);

                // Let us start on 2017-08-29 and with cell in row 20, col 32.
                numberOfDaysSinceLastRainfallEventGT2mm = 0;
                accumulation = 0.0d;
                t = null;

                ite = observationsGrids.descendingKeySet().iterator();
                // Get to the right time
                while (ite.hasNext()) {
                    t = ite.next();
                    if (t.equals(t0)) {
                        System.out.println("Got to time " + t);
                        break;
                    }
                }
                boolean found;
                found = false;

                int i;
                double v;

                // Calculate accumulation
                Generic_Time gt;
                for (i = 0; i < 10; i++) {
                    gt = new Generic_Time(t);
                    gt.addDays(-i);
                    g = observationsGrids.get(gt);
                    if (g != null) {
                        v = g.getCell(row, col);
                        accumulation += v;
                    }
                }

                g = observationsGrids.get(t);
                int daysOfAccumulation;
                daysOfAccumulation = 10;
                i = 0;
                while (!found || i < daysOfAccumulation) {
                    System.out.println(t);
                    i++;
                    v = g.getCell(row, col);
                    if (v != noDataValue) {
                        //accumulation += v;
                        if (!found) {
                            if (v > 2) {
                                found = true;
                            } else {
                                numberOfDaysSinceLastRainfallEventGT2mm++;
                                if (ite.hasNext()) {
                                    t = ite.next();
                                    g = observationsGrids.get(t);
                                    if (g == null) {
                                        found = true; // Found that the last rainfall event of greater than 2mm was longer ago than the data records checked.
                                    }
                                } else {
                                    found = true; // Found that the last rainfall event of greater than 2mm was longer ago than the data records checked.
                                }
                            }
                        }
                    }
                }
                System.out.println("numberOfDaysSinceLastRainfallEventGT2mm " + numberOfDaysSinceLastRainfallEventGT2mm);
                System.out.println("total accumulation over the last " + daysOfAccumulation + " days " + accumulation);

                if (o1 == null) {
                    observedRainfallInTheLast24Hours = 0.0d;
                } else {
                    observedRainfallInTheLast24Hours = o1.getCell(row, col);
                    if (observedRainfallInTheLast24Hours == noDataValue) {
                        observedRainfallInTheLast24Hours = 0.0d;
                    }
                }
                /**
                 * TODO: By default, if there is no forecast this assumes no
                 * rain. It would probably be better to forecast a monthly
                 * average amount of rainfall instead.
                 */
                if (f1 == null) {
                    forecastRainfallInTheNext24Hours = 0.0d;
                } else {
                    forecastRainfallInTheNext24Hours = f1.getCell(row, col);
                    if (forecastRainfallInTheNext24Hours == noDataValue) {
                        forecastRainfallInTheNext24Hours = 0.0d;
                    }
                }
                if (f2 == null) {
                    forecastRainfallIn24to48Hours = 0.0d;
                } else {
                    forecastRainfallIn24to48Hours = f2.getCell(row, col);
                    if (forecastRainfallIn24to48Hours == noDataValue) {
                        forecastRainfallIn24to48Hours = 0.0d;
                    }
                }
                if (f3 == null) {
                    forecastRainfallIn48to72Hours = 0.0d;
                } else {
                    forecastRainfallIn48to72Hours = f3.getCell(row, col);
                    if (forecastRainfallIn48to72Hours == noDataValue) {
                        forecastRainfallIn48to72Hours = 0.0d;
                    }
                }
                if (f4 == null) {
                    forecastRainfallIn72to96HoursHours = 0.0d;
                } else {
                    forecastRainfallIn72to96HoursHours = f4.getCell(row, col);
                    if (forecastRainfallIn72to96HoursHours == noDataValue) {
                        forecastRainfallIn72to96HoursHours = 0.0d;
                    }
                }
                if (f5 == null) {
                    forecastRainfallIn96to120Hours = 0.0d;
                } else {
                    forecastRainfallIn96to120Hours = f5.getCell(row, col);
                    if (forecastRainfallIn96to120Hours == noDataValue) {
                        forecastRainfallIn96to120Hours = 0.0d;
                    }
                }
                r = new SARIC_WASIMRecord(day.getYYYYMMDD(), Easting_Northing[0], Easting_Northing[1],
                        numberOfDaysSinceLastRainfallEventGT2mm,
                        accumulation,
                        observedRainfallInTheLast24Hours,
                        forecastRainfallInTheNext24Hours,
                        forecastRainfallIn24to48Hours,
                        forecastRainfallIn48to72Hours,
                        forecastRainfallIn72to96HoursHours,
                        forecastRainfallIn96to120Hours);
                System.out.println(r.toString());
                pw.println(r.toString());

                pw.flush();

                //ID++;
//                    }
//                }
            }
            pw.close();
        }
    }

    File getFile(String area, Generic_Date day) {
        File result;
        File dir;
        dir = new File(sf.getOutputDataDir(ss), "WASIM");
        dir = new File(dir, area);
        dir = new File(dir, day.getYYYYMM());
        dir = new File(dir, day.getYYYYMMDD());
        dir.mkdirs();
        String filename;
        filename = day + ".csv";
        result = new File(dir, filename);
        return result;
    }

    File getFile(String area, String name) {
        File result;
        File dir;
        dir = new File(sf.getOutputDataDir(ss), "WASIM");
        dir = new File(dir, area);
        dir.mkdirs();
        String filename;
        filename = name + ".csv";
        result = new File(dir, filename);
        return result;
    }

    /**
     * Initialise the PrintWriter for the output
     */
    PrintWriter initialisePrintWriter(File f) {
        PrintWriter result;
        result = Generic_IO.getPrintWriter(f, false);
        result.println("ID,EASTING,NORTHING,NumberOfDaysSinceLast2mmRainfall,"
                + "TotalAccumulatedRainfallOverTheLast10Days,"
                + "ObservedRainfallInTheLast24Hours,"
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
            Logger.getLogger(SARIC_DataForWASIM2.class
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
        dir = new File(dir, ss.getS_wmts() + "0");
        dir = new File(dir, area);
        dir = new File(dir, ss.getS_Precipitation_Rate());
        dir = new File(dir, "EPSG_27700_4");
        if (offset < 2) {
            File f = new File(sf.getNestedTimeDirectory(dir, d),
                    d.getYYYYMMDD() + "_ForecastFor_" + d1.getYYYYMMDD() + ".asc");
            System.out.println(f);
            if (f.exists()) {
                File gdir;
                gdir = gridf.createNewFile(gridf.getGeneratedGridDoubleDir());
                result = (Grids_GridDouble) gf.create(gdir, f);
                System.out.println(result);
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
            dir = new File(dir, d.getYYYYMM());
            dir = new File(dir, d.getYYYYMMDD());
            dir = new File(dir, d.getYYYYMMDD() + "-00");
            File f = new File(dir,
                    d.getYYYYMMDD() + "-00_ForecastFor_" + d1.getYYYYMMDD() + ".asc");
            System.out.println(f);
            if (f.exists()) {
                File gdir;
                gdir = gridf.createNewFile(gridf.getGeneratedGridDoubleDir());
                result = (Grids_GridDouble) gf.create(gdir, f);
                System.out.println(result);
                return result;
            }
        }
        return null;
    }

    protected TreeMap<Generic_Time, Grids_GridDouble> getObservationsGrids(
            String area) {
        Grids_Files gridf;
        gridf = ge.getFiles();
        TreeMap<Generic_Time, Grids_GridDouble> result;
        result = new TreeMap<>();
        File dir;
        dir = new File(sf.getOutputDataMetOfficeDataPointDir(),
                ss.getS_inspire());
        dir = new File(dir, ss.getS_view());
        dir = new File(dir, ss.getS_wmts() + "0");
        dir = new File(dir, area);
        dir = new File(dir, ss.getS_RADAR_UK_Composite_Highres());
        dir = new File(dir, "EPSG_27700_4");
        System.out.println(dir);
        File[] dirs;
        dirs = dir.listFiles();
        String[] dates;
        File f;
        File dir3;
        Generic_Time t;
        Grids_GridDouble g;
        // Load each grid
        for (File dir2 : dirs) {
            System.out.println(dir2);
            dates = dir2.list();
            for (String date : dates) {
                t = new Generic_Time(se, date);
                System.out.println(t);
                dir3 = new File(dir2, date);
                f = new File(dir3,
                        date + ss.getS_RADAR_UK_Composite_Highres() + ".asc");
                if (f.exists()) {
                    File gdir;
                    gdir = gridf.createNewFile(gridf.getGeneratedGridDoubleDir());
                    g = (Grids_GridDouble) gf.create(gdir, f);
                    System.out.println(g);
                    result.put(t, g);
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
            Logger.getLogger(SARIC_DataForWASIM2.class
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
