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
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import uk.ac.leeds.ccg.andyt.geotools.Geotools_Shapefile;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridDouble;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridDoubleFactory;
import uk.ac.leeds.ccg.andyt.grids.core.grid.stats.Grids_GridDoubleStatsNotUpdated;
import uk.ac.leeds.ccg.andyt.grids.io.Grids_Files;
import uk.ac.leeds.ccg.andyt.grids.process.Grids_Processor;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Environment;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Object;
import uk.ac.leeds.ccg.andyt.projects.saric.data.catchment.SARIC_Catchment;
import uk.ac.leeds.ccg.andyt.projects.saric.data.catchment.SARIC_Teifi;
import uk.ac.leeds.ccg.andyt.projects.saric.data.catchment.SARIC_Wissey;
import uk.ac.leeds.ccg.andyt.projects.saric.data.wasim.SARIC_WASIMRecord;
import uk.ac.leeds.ccg.andyt.generic.time.Generic_Date;
import uk.ac.leeds.ccg.andyt.generic.time.Generic_Time;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Strings;
import uk.ac.leeds.ccg.andyt.vector.core.Vector_Environment;

/**
 *
 * @author geoagdt
 */
public class SARIC_DataForWASIM extends SARIC_Object implements Runnable {

    // For convenience
    Grids_Environment ge;
    Grids_Processor gp;
    Grids_GridDoubleFactory gf;
    Vector_Environment ve;

    public SARIC_DataForWASIM(SARIC_Environment se) {
        super(se);
        ge = se.gridsEnv;
        gp = ge.getProcessor();
        gf = gp.GridDoubleFactory;
        gf.setChunkNCols(62);
        gf.setChunkNRows(40);
        gf.Stats = new Grids_GridDoubleStatsNotUpdated(ge);
        ve = se.vectorEnv;
    }

    @Override
    public void run() {
try {
        // Initial day set
        Generic_Date day0;
        // Number of days after initial day results are output for
        int numberOfDaysRun;
        // What areas to run for
        ArrayList<String> areas = new ArrayList<>();
//        areas.add(strings.getS_Teifi());
        areas.add(SARIC_Strings.s_Wissey);
        // Fill in gaps or overwrite?
        boolean overwrite;
        //overwrite = false;
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
        day0 = new Generic_Date(se.env, "2017-09-06");
        numberOfDaysRun = 100;
        // Declaration
        String area;
        Generic_Date day;

        //day = new Generic_Date(se, "2017-08-26");

        TreeMap<Generic_Time, Grids_GridDouble> observationsGrids;

        // GeoTools
        SimpleFeatureType sft;
        sft = getPointSimpleFeatureType(defaultSRID);
        GeometryFactory geometryFactory;
        geometryFactory = JTSFactoryFinder.getGeometryFactory();
        SimpleFeatureBuilder sfb;
        Point point;

        Geometry geometry2;
        double Easting;
        double Northing;
        Grids_GridDouble g;
        long nrows;
        long ncols;
        int numberOfDaysSinceLastRainfallEventGT2mm;
        double accumulation;
        long ID;
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
        PrintWriter pw;

        Iterator<String> iteArea;
        iteArea = areas.iterator();
        while (iteArea.hasNext()) {
            area = iteArea.next();
            Iterator<Generic_Time> ite;

            // Load all the observations grids
            observationsGrids = getObservationsGrids(area);

            // GetWaterCompanyShapefile Geometry
            geometry2 = getWaterCompanyShapefileGeometry(area);

            for (int days = 0; days < numberOfDaysRun; days++) {
                day = new Generic_Date(day0);
                day.addDays(days);
                // Initialise the PrintWriter for the output
                f = getFile(area, day);
                if (f.exists() && overwrite || !f.exists()) {
                    pw = initialisePrintWriter(f);

                    // Initialisation
                    g = observationsGrids.firstEntry().getValue();
                    nrows = g.getNRows();
                    ncols = g.getNCols();
                    ID = 0;
                    t0 = new Generic_Time(day);
                    t = new Generic_Time(day);
                    
                    o1 = observationsGrids.get(t);
                            
                    f1 = getForecastsGrid(area, t, 1);
                    f2 = getForecastsGrid(area, t, 2);
                    f3 = getForecastsGrid(area, t, 3);
                    f4 = getForecastsGrid(area, t, 4);
                    f5 = getForecastsGrid(area, t, 5);
                    // Assuming all grids have the same noDataValue.
                    noDataValue = g.getNoDataValue();

                    for (long row = 0; row < nrows; row++) {
                        for (long col = 0; col < ncols; col++) {

                            // Mask
                            Easting = g.getCellXDouble(col);
                            Northing = g.getCellYDouble(row);

                            sfb = new SimpleFeatureBuilder(sft);
                            point = geometryFactory.createPoint(new Coordinate(Easting, Northing));
                            sfb.add(point);
                            sfb.add("" + ID);
                            SimpleFeature feature;
                            Geometry geometry;
                            feature = sfb.buildFeature(null);
                            geometry = (Geometry) feature.getDefaultGeometry();
                            Geometry intersection;
                            intersection = geometry.intersection(geometry2);
                            if (intersection.isEmpty()) {
                                //System.out.println("Point " + point + " does not intersect.");
                            } else {
                                // Let us start on 2017-08-29 and with cell in row 20, col 32.
                                numberOfDaysSinceLastRainfallEventGT2mm = 0;
                                accumulation = 0.0d;
                                t = null;

                                ite = observationsGrids.descendingKeySet().iterator();
                                // Get to the right spot
                                while (ite.hasNext()) {
                                    t = ite.next();
                                    if (t.equals(t0)) {
                                        System.out.println("Got to time " + t);
                                        break;
                                    }
                                }
                                boolean found;
                                found = false;

                                g = observationsGrids.get(t);
                                double v;
                                int daysOfAccumulation;
                                daysOfAccumulation = 10;
                                int i;
                                i = 0;
                                while (!found || i < daysOfAccumulation) {
                                    System.out.println(t);
                                    i++;
                                    v = g.getCell(row, col);
                                    if (v != noDataValue) {
                                        accumulation += v;
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
                                 * TODO: By default, if there is no forecast
                                 * this assumes no rain. It would probably be
                                 * better to forecast a monthly average amount
                                 * of rainfall instead.
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
                                r = new SARIC_WASIMRecord(Long.toString(ID), Easting, Northing,
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
                                ID++;
                            }
                        }
                    }
                    pw.close();
                } else {
                    System.out.println("File " + f + " exists and is not being overwritten");
                }
            }
        }
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }

    File getFile(String area, Generic_Date day) {
        File result;
        File dir;
        dir = new File(files.getOutputDir(), "WASIM");
        dir = new File(dir, area);
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
        result = se.env.io.getPrintWriter(f, false);
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
        if (area.equalsIgnoreCase(SARIC_Strings.s_Teifi)) {
            sc = new SARIC_Teifi(se);
        } else if (area.equalsIgnoreCase(SARIC_Strings.s_Wissey)) {
            sc = new SARIC_Wissey(se);
        }
        Geotools_Shapefile shpf;
        shpf = sc.getWaterCompanyAGDT_Shapefile();
        SimpleFeature feature2 = null;
        try {
            feature2 = (SimpleFeature) shpf.getFeatureSource().getFeatures().features().next();

        } catch (IOException ex) {
            Logger.getLogger(SARIC_DataForWASIM.class
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
            int offset) throws IOException {
        Grids_Files gridf  = ge.files;
        Generic_Date d1  = new Generic_Time(d);
        d1.addDays(offset);
        Grids_GridDouble r;
        File dir  = new File(files.getOutputDataMetOfficeDataPointDir(),
                SARIC_Strings.s_inspire);
        dir = new File(dir, SARIC_Strings.s_view);
        dir = new File(dir, SARIC_Strings.s_wmts + "0");
        dir = new File(dir, area);
        dir = new File(dir, SARIC_Strings.s_Precipitation_Rate);
        dir = new File(dir, "EPSG_27700_4");
        if (offset < 2) {
            File f = new File(files.getNestedTimeDirectory(dir, d),
                    d.getYYYYMMDD() + "_ForecastFor_" + d1.getYYYYMMDD() + ".asc");
            System.out.println(f);
            if (f.exists()) {
                File gdir;
                gdir = se.env.io.createNewFile(gridf.getGeneratedGridDoubleDir());
                r = (Grids_GridDouble) gf.create(gdir, f);
                System.out.println(r);
                return r;
            }
        } else {
            // System.out.println("Load in some other data from the longer range forecasts.");
            dir = new File(files.getOutputDataMetOfficeDataPointDir(),
                    SARIC_Strings.s_val);
            dir = new File(dir, SARIC_Strings.s_wxfcs);
            dir = new File(dir, SARIC_Strings.s_all);
            dir = new File(dir, SARIC_Strings.s_xml);
            dir = new File(dir, area);
            dir = new File(dir, d.getYYYYMM());
            dir = new File(dir, d.getYYYYMMDD());
            dir = new File(dir, d.getYYYYMMDD() + "-00");
            File f = new File(dir,
                    d.getYYYYMMDD() + "-00_ForecastFor_" + d1.getYYYYMMDD() + ".asc");
            System.out.println(f);
            if (f.exists()) {
                File gdir;
                gdir = se.env.io.createNewFile(gridf.getGeneratedGridDoubleDir());
                r = (Grids_GridDouble) gf.create(gdir, f);
                System.out.println(r);
                return r;
            }
        }
        return null;
    }

    protected TreeMap<Generic_Time, Grids_GridDouble> getObservationsGrids(
            String area) throws IOException {
        Grids_Files gridf  = ge.files;
        TreeMap<Generic_Time, Grids_GridDouble> r= new TreeMap<>();
        File dir  = new File(files.getOutputDataMetOfficeDataPointDir(),
                SARIC_Strings.s_inspire);
        dir = new File(dir, SARIC_Strings.s_view);
        dir = new File(dir, SARIC_Strings.s_wmts + "0");
        dir = new File(dir, area);
        dir = new File(dir, SARIC_Strings.s_RADAR_UK_Composite_Highres);
        dir = new File(dir, "EPSG_27700_4");
        System.out.println(dir);
        File[] dirs  = dir.listFiles();
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
                t = new Generic_Time(se.env, date);
                System.out.println(t);
                dir3 = new File(dir2, date);
                f = new File(dir3,
                        date + SARIC_Strings.s_RADAR_UK_Composite_Highres + ".asc");
                if (f.exists()) {
                    File gdir;
                    gdir = se.env.io.createNewFile(gridf.getGeneratedGridDoubleDir());
                    g = (Grids_GridDouble) gf.create(gdir, f);
                    System.out.println(g);
                    r.put(t, g);
                }
            }
        }
        return r;
    }

    private static SimpleFeatureType initSimpleFeatureType(String type,
            String srid) {
        SimpleFeatureType r = null;
        try {
            r = DataUtilities.createType(
                    "Location",
                    "the_geom:" + type + ":srid=" + srid + ","
                    + // <- the geometry attribute
                    "name:String," // <- a String attribute
            );

        } catch (SchemaException ex) {
            Logger.getLogger(SARIC_DataForWASIM.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return r;
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
