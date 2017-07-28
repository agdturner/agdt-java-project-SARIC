/*
 * Copyright (C) 2014 geoagdt.
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
package uk.ac.leeds.ccg.andyt.projects.saric.visualisation;

import uk.ac.leeds.ccg.andyt.agdtgeotools.AGDT_StyleParameters;
import uk.ac.leeds.ccg.andyt.agdtgeotools.AGDT_Point;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import org.geotools.data.collection.TreeSetFeatureCollection;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import uk.ac.leeds.ccg.andyt.agdtgeotools.AGDT_Maps;
import uk.ac.leeds.ccg.andyt.agdtgeotools.AGDT_Shapefile;
import uk.ac.leeds.ccg.andyt.generic.io.Generic_StaticIO;
import uk.ac.leeds.ccg.andyt.generic.utilities.Generic_Collections;
import uk.ac.leeds.ccg.andyt.projects.saric.SARIC_Environment;
import uk.ac.leeds.ccg.andyt.projects.saric.io.SARIC_Files;

/**
 *
 * @author geoagdt
 */
public class SARIC_Maps extends AGDT_Maps {

    protected transient SARIC_Environment env;
    protected transient SARIC_Files SARIC_Files;
    
    /**
     * For storing level(s) (OA, LSOA, MSOA, PostcodeSector, PostcodeUnit, ...)
     */
    protected AGDT_StyleParameters styleParameters;

    public boolean doDebug;

    public SARIC_Maps(SARIC_Environment env) {
        this.env = env;
        SARIC_Files = env.getSARIC_Files();
    }

    /*
     * Select and create a new shapefile.
     *
     * @param sdsf
     * @param fc
     * @param sft
     * @param codesToSelect
     * @param targetPropertyName
     * @param outputFile
     */
    public static void selectAndCreateNewShapefile(
            ShapefileDataStoreFactory sdsf,
            FeatureCollection fc,
            SimpleFeatureType sft,
            TreeSet<String> codesToSelect,
            //String attributeName, 
            String targetPropertyName,
            File outputFile) {
        // Initialise the collection of new Features
        TreeSetFeatureCollection tsfc;
        tsfc = new TreeSetFeatureCollection();
        // Create SimpleFeatureBuilder
        //FeatureFactory ff = FactoryFinder.getGeometryFactories();
        SimpleFeatureBuilder sfb;
        sfb = new SimpleFeatureBuilder(sft);
        FeatureIterator featureIterator;
        featureIterator = fc.features();
        int id_int = 0;
        while (featureIterator.hasNext()) {
            Feature inputFeature = featureIterator.next();
            Collection<Property> properties;
            properties = inputFeature.getProperties();
            Iterator<Property> itep = properties.iterator();
            while (itep.hasNext()) {
                Property p = itep.next();
                //System.out.println("Property " + p.toString());
                String propertyName = p.getName().toString();
                //System.out.println("PropertyName " + propertyName);
                if (propertyName.equalsIgnoreCase(targetPropertyName)) {
                    //PropertyType propertyType = p.getType();
                    //System.out.println("PropertyType " + propertyType);
                    Object value = p.getValue();
                    //System.out.println("PropertyValue " + value);
                    String valueString = value.toString();
                    if (codesToSelect.contains(valueString)) {
                        if (valueString.trim().equalsIgnoreCase("E02002337")) {
                            int debug = 1;
                        }
                        String id = "" + id_int;
                        addFeatureToFeatureCollection(
                                (SimpleFeature) inputFeature,
                                sfb,
                                tsfc,
                                id);
                        id_int++;
                    } else {
//                        System.out.println(valueString);
                    }
                }
            }
        }
        featureIterator.close();
        AGDT_Shapefile.transact(outputFile, sft, tsfc, sdsf);
    }

    public SARIC_Shapefile getTeifiBoundary_SARIC_Shapefile() {
        SARIC_Shapefile result;
        String name;
        //name = "62001.shp";
        name = "WW_area.shp";
        File dir = new File(
                env.getSARIC_Files().getCatchmentBoundariesDataDir(),
                "Teifi");
        dir = new File(dir, name);
        File f;
        f = new File(
                dir,
                name);
        result = new SARIC_Shapefile(f);
        return result;
    }

    public AGDT_StyleParameters getStyleParameters() {
        return styleParameters;
    }

    public void setStyleParameters(AGDT_StyleParameters sp) {
        this.styleParameters = sp;
    }
    
    
}
