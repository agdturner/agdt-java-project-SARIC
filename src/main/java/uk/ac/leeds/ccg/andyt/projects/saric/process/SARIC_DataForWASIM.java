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
import uk.ac.leeds.ccg.andyt.projects.saric.data.catchment.SARIC_Teifi;
import uk.ac.leeds.ccg.andyt.projects.saric.data.wasim.SARIC_WASIMRecord;
import uk.ac.leeds.ccg.andyt.projects.saric.io.SARIC_Files;
import uk.ac.leeds.ccg.andyt.generic.utilities.time.Generic_Date;
import uk.ac.leeds.ccg.andyt.generic.utilities.time.Generic_Time;
import uk.ac.leeds.ccg.andyt.vector.core.Vector_Environment;

/**
 *
 * @author geoagdt
 */
public class SARIC_DataForWASIM extends SARIC_Object implements Runnable {

    // For convenience
    SARIC_Files sf;
    SARIC_Strings ss;
    Grids_Environment ge;
    Grids_Processor gp;
    Grids_GridDoubleFactory gf;
    Vector_Environment ve;

    protected SARIC_DataForWASIM() {
    }

    public SARIC_DataForWASIM(SARIC_Environment se) {
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

        // Initialise the PrintWriter for the output
        Generic_Date day;
        day = new Generic_Date(se, "2017-08-26");

        File dir;
        dir = new File(sf.getOutputDataDir(ss), "WASIM");
        dir.mkdirs();
        String filename;
        filename = day + ".csv";
        File f;
        f = new File(dir, filename);
        PrintWriter pw;
        pw = Generic_StaticIO.getPrintWriter(f, false);
        pw.println("ID,EASTING,NORTHING,NumberOfDaysSinceLast2mmRainfall,"
                + "TotalAccumulatedRainfallOverTheLast10Days,"
                + "ForecastRainfallInTheNext24Hours,"
                + "ForecastRainfallIn24to48Hours,"
                + "ForecastRainfallIn48to72Hours,"
                + "ForecastRainfallIn72to96HoursHours,"
                + "ForecastRainfallIn96to120Hours");
        // Load all the observations grids
        String area;
        TreeMap<Generic_Time, Grids_GridDouble> observationsGrids;
        area = ss.getS_Teifi();
        observationsGrids = getObservationsGrids(area);

        // GeoTools
        SimpleFeatureType sft;
        sft = getPointSimpleFeatureType(defaultSRID);
        GeometryFactory geometryFactory;
        geometryFactory = JTSFactoryFinder.getGeometryFactory();
        SimpleFeatureBuilder sfb;
        Point point;

        // Teifi
        SARIC_Teifi st;
        st = new SARIC_Teifi(se);
        Geotools_Shapefile shpf;
        shpf = st.getWaterCompanyAGDT_Shapefile();
        SimpleFeature feature2 = null;
        try {
            feature2 = (SimpleFeature) shpf.getFeatureSource().getFeatures().features().next();
        } catch (IOException ex) {
            Logger.getLogger(SARIC_DataForWASIM.class.getName()).log(Level.SEVERE, null, ex);
        }
        Geometry geometry2;
        geometry2 = (Geometry) feature2.getDefaultGeometry();
//        // Describe geometry2
//        String geometryType;
//        geometryType = geometry2.getGeometryType();
//        System.out.println("geometryType " + geometryType);
//        System.out.println("geometry2 " + geometry2.toString());
//        System.out.println("geometry2 " + geometry2.toText());
//        BigDecimal y;
//        Vector_Point2D p;
        double Easting;
        double Northing;
        Grids_GridDouble g;
        g = observationsGrids.firstEntry().getValue();
        long nrows;
        long ncols;
        nrows = g.getNRows();
        ncols = g.getNCols();
        int numberOfDaysSinceLastRainfallEventGT2mm;
        double accumulation;
        long ID;
        ID = 0;
        Generic_Time t0;
        t0 = new Generic_Time(day);
        Generic_Time t;
        t = new Generic_Time(day);

        Grids_GridDouble f1;
        Grids_GridDouble f2;
        Grids_GridDouble f3;
        Grids_GridDouble f4;
        Grids_GridDouble f5;
        f1 = getForecastsGrid(area, t, 1);
        f2 = getForecastsGrid(area, t, 2);
        f3 = getForecastsGrid(area, t, 3);
        f4 = getForecastsGrid(area, t, 4);
        f5 = getForecastsGrid(area, t, 5);

        double forecastRainfallInTheNext24Hours;
        double forecastRainfallIn24to48Hours;
        double forecastRainfallIn48to72Hours;
        double forecastRainfallIn72to96HoursHours;
        double forecastRainfallIn96to120Hours;

        // Assuming all grids have the same noDataValue.
        double noDataValue;
        noDataValue = g.getNoDataValue();

        Iterator<Generic_Time> ite;
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

                    forecastRainfallInTheNext24Hours = f1.getCell(row, col);
                    if (forecastRainfallInTheNext24Hours == noDataValue) {
                        forecastRainfallInTheNext24Hours = 0.0d;
                    }
                    forecastRainfallIn24to48Hours = f2.getCell(row, col);
                    if (forecastRainfallIn24to48Hours == noDataValue) {
                        forecastRainfallIn24to48Hours = 0.0d;
                    }
                    forecastRainfallIn48to72Hours = f3.getCell(row, col);
                    if (forecastRainfallIn48to72Hours == noDataValue) {
                        forecastRainfallIn48to72Hours = 0.0d;
                    }
                    forecastRainfallIn72to96HoursHours = f4.getCell(row, col);
                    if (forecastRainfallIn72to96HoursHours == noDataValue) {
                        forecastRainfallIn72to96HoursHours = 0.0d;
                    }
                    forecastRainfallIn96to120Hours = f5.getCell(row, col);
                    if (forecastRainfallIn96to120Hours == noDataValue) {
                        forecastRainfallIn96to120Hours = 0.0d;
                    }
                    SARIC_WASIMRecord r;
                    r = new SARIC_WASIMRecord(
                            ID,
                            Easting,
                            Northing,
                            numberOfDaysSinceLastRainfallEventGT2mm,
                            accumulation,
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

        }
        return null;
    }

    protected TreeMap<Generic_Time, Grids_GridDouble> getObservationsGrids(String area) {
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

    private static SimpleFeatureType initSimpleFeatureType(String type, String srid) {
        SimpleFeatureType result = null;
        try {
            result = DataUtilities.createType(
                    "Location",
                    "the_geom:" + type + ":srid=" + srid + ","
                    + // <- the geometry attribute
                    "name:String," // <- a String attribute
            );
        } catch (SchemaException ex) {
            Logger.getLogger(SARIC_DataForWASIM.class.getName()).log(Level.SEVERE, null, ex);
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
