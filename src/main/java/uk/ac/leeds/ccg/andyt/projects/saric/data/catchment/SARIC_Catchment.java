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
package uk.ac.leeds.ccg.andyt.projects.saric.data.catchment;

//import com.vividsolutions.jts.geom.Coordinate;
//import com.vividsolutions.jts.geom.Geometry;
//import com.vividsolutions.jts.geom.GeometryFactory;
//import com.vividsolutions.jts.geom.LinearRing;
//import com.vividsolutions.jts.geom.Point;
//import com.vividsolutions.jts.geom.Polygon;
//import com.vividsolutions.jts.geom.PrecisionModel;
//import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import uk.ac.leeds.ccg.andyt.geotools.core.Geotools_Environment;
import uk.ac.leeds.ccg.andyt.geotools.Geotools_Shapefile;
import uk.ac.leeds.ccg.andyt.math.Math_BigDecimal;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Dimensions;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridDouble;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridDoubleFactory;
import uk.ac.leeds.ccg.andyt.grids.core.grid.stats.Grids_GridDoubleStats;
import uk.ac.leeds.ccg.andyt.grids.process.Grids_Processor;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Environment;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Object;
import uk.ac.leeds.ccg.andyt.projects.saric.data.metoffice.datapoint.site.SARIC_Site;
import uk.ac.leeds.ccg.andyt.projects.saric.data.metoffice.datapoint.site.SARIC_SiteHandler;
import uk.ac.leeds.ccg.andyt.projects.saric.process.SARIC_DataForWASIM;
import uk.ac.leeds.ccg.andyt.vector.core.Vector_Environment;
import uk.ac.leeds.ccg.andyt.vector.geometry.Vector_Envelope2D;
import uk.ac.leeds.ccg.andyt.vector.geometry.Vector_Point2D;
import uk.ac.leeds.ccg.andyt.vector.projection.Vector_OSGBtoLatLon;

/**
 *
 * @author geoagdt
 */
public abstract class SARIC_Catchment extends SARIC_Object {

    // For convenience
    Grids_Environment Grids_Env;
    Vector_Environment Vector_Env;
    Geotools_Environment Geotools_Env;

    String CatchmentName;

    public SARIC_Catchment(SARIC_Environment se, String catchmentName) {
        super(se);
        Grids_Env = se.gridsEnv;
        Vector_Env = se.vectorEnv;
        Geotools_Env = se.geotoolsEnv;
        this.CatchmentName = catchmentName;
    }

    public Geotools_Shapefile getAGDT_Shapefile(String name, File dir) {
        Geotools_Shapefile result;
        File f = Geotools_Env.getShapefile(dir, name, false);
        result = new Geotools_Shapefile(Geotools_Env, f);
        return result;
    }

    public Geotools_Shapefile getNRFAAGDT_Shapefile(String name) {
        Geotools_Shapefile result;
        result = getAGDT_Shapefile(name);
        return result;
    }

    public abstract Geotools_Shapefile getWaterCompanyAGDT_Shapefile();

    public Geotools_Shapefile getAGDT_Shapefile(String name) {
        Geotools_Shapefile r;
        File dir = new File(files.getInputDataCatchmentBoundariesDir(),
                CatchmentName);
        r = getAGDT_Shapefile(name, dir);
        return r;
    }

    public abstract Vector_Envelope2D getBounds();

    public Vector_Envelope2D getBounds(BigDecimal xmin, BigDecimal xmax,
            BigDecimal ymin, BigDecimal ymax) {
        Vector_Envelope2D r;
        Vector_Point2D aPoint;
        aPoint = new Vector_Point2D(se.vectorEnv, xmin, ymin);
        Vector_Point2D bPoint;
        bPoint = new Vector_Point2D(se.vectorEnv, xmax, ymax);
        r = new Vector_Envelope2D(aPoint, bPoint);
        return r;
    }

    public abstract Vector_Envelope2D get1KMGridBounds();

    /**
     * https://www.ordnancesurvey.co.uk/resources/maps-and-geographic-resources/the-national-grid.html
     *
     * @param name "1KMGrid", "1KMGridMaskedToCatchment"
     * @return
     */
    public Object[] get1KMGrid(String name) throws IOException {
        Object[] result;
        result = new Object[3];
        Grids_GridDouble grid;
        Vector_Envelope2D bounds;
        bounds = get1KMGridBounds();
        //Grids_Grid2DSquareCellDoubleFactory inf;
        File dir;
        dir = new File(files.getGeneratedDataCatchmentBoundariesDir(),
                CatchmentName);
        dir = new File(dir, name);
        BigDecimal cellsize = new BigDecimal("1000");
        Grids_Dimensions dimensions;
        dimensions = new Grids_Dimensions(bounds.XMin, bounds.XMax, bounds.YMin, bounds.YMax, cellsize);
        long nrows;
        long ncols;
        ncols = Math_BigDecimal.divideNoRounding(bounds.XMax.subtract(bounds.XMin), cellsize).longValueExact();
        nrows = Math_BigDecimal.divideNoRounding(bounds.YMax.subtract(bounds.YMin), cellsize).longValueExact();
        //inf = se.getGrids_Env().get_Grid2DSquareCellProcessor()._Grid2DSquareCellDoubleFactory;
        Grids_Processor gp;
        gp = Grids_Env.getProcessor();
        Grids_GridDoubleFactory f;
        f = new Grids_GridDoubleFactory(Grids_Env,
                gp.GridChunkDoubleFactory, gp.DefaultGridChunkDoubleFactory,
                -Double.MAX_VALUE, (int) nrows, (int) ncols, dimensions,
                new Grids_GridDoubleStats(Grids_Env));
        if (dir.exists()) {
            grid = (Grids_GridDouble) f.create(dir, dir);
            result[2] = true;
        } else {
            grid = f.create(dir, nrows, ncols, dimensions);
            grid.writeToFile();
            result[2] = false;
        }
        result[0] = grid;
//        System.out.println("grid " + grid);
        result[1] = f;
        return result;
    }

    /**
     * https://www.ordnancesurvey.co.uk/resources/maps-and-geographic-resources/the-national-grid.html
     *
     * @return
     */
    public Object[] get1KMGridMaskedToCatchment() throws IOException {
        Object[] result;
        result = get1KMGrid("1KMGridMaskedToCatchment");
        if (!(Boolean) result[2]) {
            Grids_GridDouble resultGrid;
            resultGrid = (Grids_GridDouble) result[0];
            // Get Outline
            Geometry geometry2;
            geometry2 = getOutlineGeometry();
            //MultiPolygon p = (MultiPolygon) geometry2;
            //System.out.println(p.toString());
            GeometryFactory gf;
            gf = JTSFactoryFinder.getGeometryFactory();
            //      Polygon p = new Polygon(geometry2, null, gf);
            // Set up for intersection
            SimpleFeatureType sft;
            sft = SARIC_DataForWASIM.getPointSimpleFeatureType(SARIC_DataForWASIM.defaultSRID);
            SimpleFeatureBuilder sfb;
            sfb = new SimpleFeatureBuilder(sft);
            Coordinate c;
            Coordinate[] cs;
            cs = new Coordinate[5];
            Point point;
            Polygon poly;
            PrecisionModel precisionModel;
            precisionModel = new PrecisionModel(PrecisionModel.FLOATING);
            SimpleFeature feature;
            Geometry geometry;
            Geometry intersection;
            String name;
            long nrows;
            long ncols;
            nrows = resultGrid.getNRows();
            ncols = resultGrid.getNCols();
            //double noDataValue = result.getNoDataValue(true);
            //double value;
            double cellsize;
            cellsize = resultGrid.getCellsizeDouble();
            double halfCellsize;
            halfCellsize = cellsize / 2.0d;
            double x;
            double y;
            for (long row = 0; row < nrows; row++) {
                y = resultGrid.getCellYDouble(row);
                for (long col = 0; col < ncols; col++) {
                    x = resultGrid.getCellXDouble(col);
                    c = new Coordinate(x, y);
                    //System.out.println("x, y = " + x + ", " + y);
                    //System.out.println("row, col = " + row + ", " + col);
                    cs[0] = new Coordinate(x - halfCellsize, y - halfCellsize);
                    cs[1] = new Coordinate(x - halfCellsize, y + halfCellsize);
                    cs[2] = new Coordinate(x + halfCellsize, y + halfCellsize);
                    cs[3] = new Coordinate(x + halfCellsize, y - halfCellsize);
                    cs[4] = cs[0];
                    CoordinateArraySequence coords;
                    coords = new CoordinateArraySequence(cs);
                    LinearRing lr;
                    lr = new LinearRing(coords, gf);
                    poly = new Polygon(lr, null, gf);
                    //point = gf.createPoint(c);
//                sfb.add(point);
//                name = "" + x + "_" + y;
//                sfb.add(name);
//                feature = sfb.buildFeature(null);
//                geometry = (Geometry) feature.getDefaultGeometry();
//                intersection = geometry.intersection(geometry2);
//                intersection = poly.intersection(p);
                    intersection = poly.intersection(geometry2);
//                intersection = p.intersection(poly);
//                intersection = point.intersection(p);
//                intersection = p.intersection(point);
                    if (intersection.isEmpty()) {
//                intersection = poly.intersection(geometry2);
//                if (intersection.isEmpty()) {
                        //if (!lr.crosses(geometry2)) {
                        //System.out.println("Point " + point + " does not intersect.");
                        //System.out.println("Poly " + poly + " does not intersect.");
                    } else {
                        //System.out.println("Intersection");
                        //System.out.println("row, col " + row + ", " + col);
                        //System.out.println("x, y " + x + ", " + y);
                        resultGrid.setCell(row, col, 0.0d);
                    }
                }
            }
            System.out.println("Catchment mask " + resultGrid);
            resultGrid.writeToFile();
        }
        return result;
    }

    public Vector_Envelope2D getBoundsBuffered(BigDecimal buffer) {
        Vector_Envelope2D bounds;
        bounds = getBounds();
        // Buffer
        if (buffer != null) {
            // Add buffer to bounds
            bounds.XMin = bounds.XMin.subtract(buffer);
            bounds.XMax = bounds.XMax.add(buffer);
            bounds.YMin = bounds.YMin.subtract(buffer);
            bounds.YMax = bounds.YMax.add(buffer);
        }
        return bounds;
    }

    public Object[] getNearestForecastsSitesGrid(HashSet<SARIC_Site> sites) throws IOException {
        Object[] result;
        result = get1KMGridMaskedToCatchment();
        Grids_GridDouble resultGrid;
        resultGrid = (Grids_GridDouble) result[0];
        long nrows;
        long ncols;
        nrows = resultGrid.getNRows();
        ncols = resultGrid.getNCols();
        double x;
        double y;
        double v;
        double[] OSGBEastingAndNorthing;
        double noDataValue = resultGrid.getNoDataValue();
        double distance;
        double minDistance;
        Vector_OSGBtoLatLon OSGBtoLatLon = Vector_Env.getOSGBtoLatLon();
        Iterator<SARIC_Site> ite;
        SARIC_Site site;
        for (long row = 0; row < nrows; row++) {
            y = resultGrid.getCellYDouble(row);
            for (long col = 0; col < ncols; col++) {
                x = resultGrid.getCellXDouble(col);
                v = resultGrid.getCell(row, col);
                if (v != noDataValue) {
                    ite = sites.iterator();
                    minDistance = Double.MAX_VALUE;
                    while (ite.hasNext()) {
                        site = ite.next();
                        OSGBEastingAndNorthing = Vector_OSGBtoLatLon.latlon2osgb(
                                site.getLatitude(), site.getLongitude());
                        double xdiff = (double) (OSGBEastingAndNorthing[0] - x);
                        double ydiff = (double) (OSGBEastingAndNorthing[1] - y);
                        distance = Math.sqrt((xdiff * xdiff) + (ydiff * ydiff));
                        if (distance < minDistance) {
                            minDistance = distance;
                            resultGrid.setCell(row, col, site.getId());
                        }
                    }
                    //System.out.println("minDistance from x " + x + ", y " + y + " = " + minDistance + " siteID " + site.getId());
                }
            }
        }
        return result;
    }

    /**
     * Intersects sites points with WaterCompany shapefile to return all sites
     * within.
     *
     * @param time Expecting "3hourly" or "daily".
     * @return
     */
    public HashSet<SARIC_Site> getForecastsSitesInStudyArea(String time) {
        HashSet<SARIC_Site> sites;
        SARIC_SiteHandler ssh;
        ssh = new SARIC_SiteHandler(se);
        sites = ssh.getForecastsSites(time);
        HashSet<SARIC_Site> result;
        result = new HashSet<>();
        // Get Outline
        Geometry geometry2;
        geometry2 = getOutlineGeometry();
        // Go through points
        String name;
        SimpleFeatureType sft;
        sft = SARIC_DataForWASIM.getPointSimpleFeatureType(SARIC_DataForWASIM.defaultSRID);
        GeometryFactory gf;
        gf = JTSFactoryFinder.getGeometryFactory();
        SimpleFeatureBuilder sfb;
        sfb = new SimpleFeatureBuilder(sft);
        Coordinate c;
        Point point;
        SimpleFeature feature;
        Geometry geometry;
        Geometry intersection;
        Iterator<SARIC_Site> ite;
        ite = sites.iterator();
        SARIC_Site site;
        Vector_OSGBtoLatLon OSGBtoLatLon = Vector_Env.getOSGBtoLatLon();
        double[] OSGBEastingAndNorthing;
        Vector_Point2D p;
        while (ite.hasNext()) {
            site = ite.next();
            name = site.getName();
            OSGBEastingAndNorthing = OSGBtoLatLon.latlon2osgb(site.getLatitude(), site.getLongitude());
            c = new Coordinate(OSGBEastingAndNorthing[0], OSGBEastingAndNorthing[1]);
            point = gf.createPoint(c);
            sfb.add(point);
            sfb.add(name);
            feature = sfb.buildFeature(null);
            geometry = (Geometry) feature.getDefaultGeometry();
            intersection = geometry.intersection(geometry2);
            if (intersection.isEmpty()) {
                //System.out.println("Point " + point + " does not intersect.");
            } else {
                result.add(site);
            }
        }
        return result;
    }

    /**
     * Gets the outline Geometry from the WaterCompany shapefile.
     *
     * @return
     */
    public Geometry getOutlineGeometry() {
        Geometry result;
        Geotools_Shapefile shpf;
        shpf = getWaterCompanyAGDT_Shapefile();
        SimpleFeature feature2 = null;
        try {
            feature2 = (SimpleFeature) shpf.getFeatureSource().getFeatures().features().next();

        } catch (IOException ex) {
            Logger.getLogger(SARIC_Catchment.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        result = (Geometry) feature2.getDefaultGeometry();
        shpf.dispose();
        return result;
    }
}
