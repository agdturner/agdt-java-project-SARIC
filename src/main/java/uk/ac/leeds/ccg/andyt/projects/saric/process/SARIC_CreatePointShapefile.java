/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.projects.saric.process;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotools.data.DataUtilities;
import org.geotools.data.collection.TreeSetFeatureCollection;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import uk.ac.leeds.ccg.andyt.agdtgeotools.AGDT_Geotools;
import uk.ac.leeds.ccg.andyt.agdtgeotools.AGDT_Point;
import uk.ac.leeds.ccg.andyt.agdtgeotools.AGDT_Shapefile;
import uk.ac.leeds.ccg.andyt.generic.io.Generic_StaticIO;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Environment;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Object;
import uk.ac.leeds.ccg.andyt.projects.saric.data.catchment.SARIC_Teifi;
import uk.ac.leeds.ccg.andyt.projects.saric.data.catchment.SARIC_Wissey;
import uk.ac.leeds.ccg.andyt.projects.saric.data.metoffice.datapoint.site.SARIC_Site;
import uk.ac.leeds.ccg.andyt.projects.saric.data.metoffice.datapoint.site.SARIC_SiteHandler;
import uk.ac.leeds.ccg.andyt.projects.saric.io.SARIC_Files;
import uk.ac.leeds.ccg.andyt.vector.core.Vector_Environment;
import uk.ac.leeds.ccg.andyt.vector.geometry.Vector_Envelope2D;
import uk.ac.leeds.ccg.andyt.vector.geometry.Vector_Point2D;
import uk.ac.leeds.ccg.andyt.vector.projection.Vector_OSGBtoLatLon;

/**
 *
 * @author geoagdt
 */
public class SARIC_CreatePointShapefile extends SARIC_Object implements Runnable {

    SARIC_Files sf;
    Vector_Environment ve;

    protected SARIC_CreatePointShapefile() {
    }

    public SARIC_CreatePointShapefile(SARIC_Environment se) {
        super(se);
        sf = se.getFiles();
        ve = se.getVector_Environment();
    }

    public static void main(String[] args) {
        new SARIC_CreatePointShapefile().run();
    }

    public void run() {
        SARIC_SiteHandler sh;
        sh = new SARIC_SiteHandler(se);
        sh.initForecastsSites();
        HashSet<SARIC_Site> sites;
        sites = sh.getSites();
        SARIC_Wissey wissey;
        wissey = new SARIC_Wissey(se);
        Vector_Envelope2D wisseyBounds;
        wisseyBounds = wissey.getBounds();
        SARIC_Teifi teifi;
        teifi = new SARIC_Teifi(se);
        Vector_Envelope2D teifiBounds;
        teifiBounds = teifi.getBounds();
        HashSet<SARIC_Site> sitesInWissey;
        sitesInWissey = new HashSet<SARIC_Site>();
        HashSet<SARIC_Site> sitesInTeifi;
        sitesInTeifi = new HashSet<SARIC_Site>();

        SimpleFeatureType aPointSFT = null;
        try {
            aPointSFT = DataUtilities.createType(
                    "POINT",
                    "the_geom:Point:srid=27700," 
                            + "name:String," 
                            + "ID:Integer," 
                            + "Latitude:Double," 
                            + "Longitude:Double,"
                            + "Elevation:Double");
            //srid=27700 is the Great_Britain_National_Grid
        } catch (SchemaException ex) {
            Logger.getLogger(SARIC_CreatePointShapefile.class.getName()).log(Level.SEVERE, null, ex);
        }

        TreeSetFeatureCollection tsfc;
        TreeSetFeatureCollection tsfcWissey;
        TreeSetFeatureCollection tsfcTeifi;
        tsfc = new TreeSetFeatureCollection();
        tsfcWissey = new TreeSetFeatureCollection();
        tsfcTeifi = new TreeSetFeatureCollection();
        SimpleFeatureBuilder sfb;
        sfb = new SimpleFeatureBuilder(aPointSFT);

        // Create SimpleFeatureBuilder
        //FeatureFactory ff = FactoryFinder.getGeometryFactories();
        GeometryFactory gF;
        gF = new GeometryFactory();
        String name;
        Coordinate c;
        Point point;
        SimpleFeature feature;

        Iterator<SARIC_Site> ite;
        ite = sites.iterator();
        SARIC_Site site;
        double[] OSGBEastingAndNorthing;
        Vector_Point2D p;
        while (ite.hasNext()) {
            site = ite.next();
            name = site.getName();
            OSGBEastingAndNorthing = Vector_OSGBtoLatLon.latlon2osgb(
                    site.getLatitude(), site.getLongitude());
            p = new Vector_Point2D(ve, OSGBEastingAndNorthing[0], OSGBEastingAndNorthing[1]);
            c = new Coordinate(p._x.doubleValue(), p._y.doubleValue());
            point = gF.createPoint(c);
            sfb.add(point);
            sfb.add(name);
            sfb.add(site.getId());
            sfb.add(site.getLatitude());
            sfb.add(site.getLongitude());
            sfb.add(site.getElevation());
            feature = sfb.buildFeature(name);
            tsfc.add(feature);
            if (wisseyBounds.getIntersects(p)) {
                tsfcWissey.add(feature);
            }
            if (teifiBounds.getIntersects(p)) {
                tsfcTeifi.add(feature);
            }
        }

        // Write out sites in the Wissey/Teifi
        File outfile;
        outfile = AGDT_Geotools.getOutputShapefile(
                sf.getGeneratedDataMetOfficeDataPointDir(),
                "Sites");
        outfile.getParentFile().mkdirs();
        AGDT_Shapefile.transact(
                outfile,
                aPointSFT,
                tsfc,
                new ShapefileDataStoreFactory());
        outfile = AGDT_Geotools.getOutputShapefile(
                sf.getGeneratedDataMetOfficeDataPointDir(),
                "WisseySites");
        outfile.getParentFile().mkdirs();
        AGDT_Shapefile.transact(
                outfile,
                aPointSFT,
                tsfcWissey,
                new ShapefileDataStoreFactory());
        outfile = AGDT_Geotools.getOutputShapefile(
                sf.getGeneratedDataMetOfficeDataPointDir(),
                "TeifiSites");
        outfile.getParentFile().mkdirs();
        AGDT_Shapefile.transact(
                outfile,
                aPointSFT,
                tsfcTeifi,
                new ShapefileDataStoreFactory());
    }

}
