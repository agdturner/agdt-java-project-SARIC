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

import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import uk.ac.leeds.ccg.andyt.geotools.Geotools_Shapefile;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_2D_ID_long;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Environment;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Strings;
import uk.ac.leeds.ccg.andyt.projects.saric.data.metoffice.datapoint.site.SARIC_Site;
import uk.ac.leeds.ccg.andyt.vector.geometry.Vector_Envelope2D;

/**
 *
 * @author geoagdt
 */
public class SARIC_Teifi extends SARIC_Catchment {

    /**
     * For storing a lookup of a 1km grid CellID to the nearest forecast site.
     * The intention is to use this to assign a rainfall estimate from Met
     * Office Data Point site forecasts.
     */
    HashMap<Grids_2D_ID_long, SARIC_Site> siteMap;

    public SARIC_Teifi(SARIC_Environment se) {
        super(se, SARIC_Strings.s_Teifi);
    }

    /**
     * From the National River Flow Archive.
     * http://nrfa.ceh.ac.uk/data/station/spatial/62001
     *
     * @return
     */
    public Geotools_Shapefile getNRFAAGDT_Shapefile() {
        return getNRFAAGDT_Shapefile("62001.shp");
    }

    /**
     * Provided by Dwr Cymru.
     *
     * @return
     */
    @Override
    public Geotools_Shapefile getWaterCompanyAGDT_Shapefile() {
        return getAGDT_Shapefile("WW_area.shp");
    }

    public Geotools_Shapefile getOSMAGDT_Shapefile(String name) {
        Geotools_Shapefile result;
        File dir = new File(                files.getGeneratedDataOSMDir(),
                "wales-latest-free.shp");
////        result = getAGDT_Shapefile(name, dir);
//
//         File sourceFile;
//         SimpleFeatureSource featureSource;
//         MapContent map;
//        
//
//        FileDataStore store = FileDataStoreFinder.getDataStore(sourceFile);
//        featureSource = store.getFeatureSource();
//        
//     
//        SimpleFeatureType schema = featureSource.getSchema();
//        
//        FeatureCollection fc;
//        fc = result.getFeatureCollection();
//        FeatureIterator fi;
//        fi = fc.features();
//        SimpleFeature f;
//        CoordinateReferenceSystem sourceCRS;
//        CoordinateReferenceSystem targetCRS;
//        Geometry g;
//        Geometry targetGeometry;
//        try {
//            sourceCRS = CRS.decode("EPSG:4326");
//            targetCRS = CRS.decode("EPSG:27700");
//            while (fi.hasNext()) {
//                f = (SimpleFeature) fi.next();
//               // g = (Geometry) f.getAttribute("GEOMETRY");
//                g =  (Geometry) f.getAttribute( 0 );
//                MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);
//                try {
//                    if (g != null) {
//                        targetGeometry = JTS.transform(g, transform);
//                        //f.setAttribute("GEOMETRY", targetGeometry);
//                        f.setAttribute(0, targetGeometry);
//                    }
//                } catch (MismatchedDimensionException ex) {
//                    Logger.getLogger(SARIC_Teifi.class.getName()).log(Level.SEVERE, null, ex);
//                } catch (TransformException ex) {
//                    Logger.getLogger(SARIC_Teifi.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//        } catch (FactoryException ex) {
//            Logger.getLogger(SARIC_Teifi.class.getName()).log(Level.SEVERE, null, ex);
//        }

        result = getAGDT_Shapefile(name, dir);
        return result;
    }
    
    @Override
    public HashSet<SARIC_Site> getForecastsSitesInStudyArea(String time) {
        HashSet<SARIC_Site> r;
        File f  = files.getGeneratedDataMetOfficeDataPointForecastsSitesInTeifiFile();
        if (f.exists()) {
            r = (HashSet<SARIC_Site>) se.env.io.readObject(f);
        } else {
            f.getParentFile().mkdirs();
            r = super.getForecastsSitesInStudyArea(time);
            se.env.io.writeObject(r, f);
        }
        return r;
    }

    /**
     * Teifi Bounding Box: MinX 218749.5025726173; MaxX 279871.8842591159; MinY
     * 231291.52626209427; MaxY 270891.8510279902.
     *
     * @return
     */
    @Override
    public Vector_Envelope2D getBounds() {
        return getBounds(
                new BigDecimal("218749.5025726173"),
                new BigDecimal("279871.8842591159"),
                new BigDecimal("231291.52626209427"),
                new BigDecimal("270891.8510279902"));
    }

    /**
     * Teifi Bounding Box: MinX 218000; MaxX 280000; MinY 231000; MaxY 271000.
     *
     * @return
     */
    @Override
    public Vector_Envelope2D get1KMGridBounds() {
        return getBounds(
                new BigDecimal("218000"),
                new BigDecimal("280000"),
                new BigDecimal("231000"),
                new BigDecimal("271000"));
    }
}
