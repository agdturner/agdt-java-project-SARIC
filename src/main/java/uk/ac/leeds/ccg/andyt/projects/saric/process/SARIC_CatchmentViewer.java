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

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.map.MapViewport;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.swing.JMapFrame;
import org.ojalgo.type.colour.Colour;
import uk.ac.leeds.ccg.andyt.agdtgeotools.AGDT_Geotools;
import uk.ac.leeds.ccg.andyt.agdtgeotools.AGDT_Shapefile;
import uk.ac.leeds.ccg.andyt.agdtgeotools.demo.AGDT_DisplayShapefile;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Environment;
import uk.ac.leeds.ccg.andyt.projects.saric.data.catchment.SARIC_Teifi;
import uk.ac.leeds.ccg.andyt.projects.saric.data.catchment.SARIC_Wissey;
import uk.ac.leeds.ccg.andyt.projects.saric.io.SARIC_Files;

/**
 *
 * @author geoagdt
 */
public class SARIC_CatchmentViewer extends AGDT_DisplayShapefile {

    SARIC_Environment se;
    SARIC_Files sf;
    
    private SARIC_CatchmentViewer() {}

    public SARIC_CatchmentViewer(SARIC_Environment se) {
        this.se = se;
        this.sf = se.getFiles();
    }

    @Override
    public void run() {
        ArrayList<File> files;
        files = new ArrayList<File>();
        String name;
        File dir;
        File f;

        AGDT_Shapefile as;
        FeatureLayer fl;
        
        MapContent mc;
        ReferencedEnvelope re;
        
        mc = new MapContent();

        // Wissey
        SARIC_Wissey sw;
        sw = new SARIC_Wissey(se);
        as = sw.getNRFAAGDT_Shapefile();
        files.add(as.getFile());
        mc.addLayer(as.getFeatureLayer());
        as = sw.getAnglianAGDT_Shapefile();
        files.add(as.getFile());
        fl = as.getFeatureLayer();
        mc.addLayer(fl);
        re = fl.getBounds();
        printBounds(re);
        
        // Teifi
        SARIC_Teifi st;
        st = new SARIC_Teifi(se);
        as = st.getNRFAAGDT_Shapefile();
        files.add(as.getFile());
        mc.addLayer(as.getFeatureLayer());
        as = st.getWWAGDT_Shapefile();
        files.add(as.getFile());
        fl = as.getFeatureLayer();
        mc.addLayer(fl);
        re = fl.getBounds();
        printBounds(re);
        re = mc.getMaxBounds();
                printBounds(re);
        
        
         dir = new File(
                this.sf.getInputDataCEHDir(),
                "WGS84");
        name = "ihu_catchments.shp";
        f = AGDT_Geotools.getShapefile(dir, name, false);
        files.add(f);

        try {
            displayShapefiles(files, 800, 600, re);
        } catch (Exception ex) {
            Logger.getLogger(AGDT_DisplayShapefile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void printBounds(ReferencedEnvelope re) {
        double minx;
        double maxx;
        double miny;
        double maxy;
        minx = re.getMinX();
        maxx = re.getMaxX();
        miny = re.getMinY();
        maxy = re.getMaxY();
        System.out.println("minx " + minx);
        System.out.println("maxx " + maxx);
        System.out.println("miny " + miny);
        System.out.println("maxy " + maxy);
    }
    
    /**
     * @param files
     * @param displayWidth
     * @param displayHeight
     * @param re Used to set MapViewport
     * @throws Exception 
     */
    @Override
    protected void displayShapefiles(
            ArrayList<File> files,
            int displayWidth,
            int displayHeight,
            ReferencedEnvelope re) throws Exception {
        MapContent mc;
        mc = new MapContent();
        int i;
        i = 0;
        Iterator<File> ite;
        ite = files.iterator();
        while (ite.hasNext()) {
            File f;
            f = ite.next();
            FileDataStore fds;
            fds = FileDataStoreFinder.getDataStore(f);
            SimpleFeatureSource fs;
            fs = fds.getFeatureSource();

//        CoordinateReferenceSystem crs;
//        crs = store.getSchema().getCoordinateReferenceSystem();
//        System.out.println(crs.toWKT());
//        System.out.println(crs.toString());
            Color outlineColor;
            Color fillColor;
            float opacity;
            if (i == 0) {
               outlineColor = Color.BLACK;
               fillColor = Color.LIGHT_GRAY;
                       opacity = 0;
            } else if (i == 1) {
                outlineColor = Color.BLUE;
                fillColor = Color.WHITE;
                       opacity = 0;
            } else {
                outlineColor = Color.CYAN;
                fillColor = Color.WHITE;
                       opacity = 0;
            }
            Style style;
            //style = SLD.createSimpleStyle(fs.getSchema());
            style = SLD.createPolygonStyle(outlineColor, fillColor, opacity);
            Layer layer;
            layer = new FeatureLayer(fs, style);
            mc.layers().add(layer);
            i ++;
        }
        // Create a JMapFrame with custom toolbar buttons
        JMapFrame mapFrame = new JMapFrame(mc);
        mapFrame.enableToolBar(true);
        mapFrame.enableStatusBar(true);

//        JToolBar toolbar = mapFrame.getToolBar();
//        toolbar.addSeparator();
//        toolbar.add(new JButton(new ValidateGeometryAction()));
//        toolbar.add(new JButton(new ExportShapefileAction()));
        // Display the map frame. When it is closed the application will exit
        mapFrame.setSize(800, 600);

        if (re != null) {
            MapViewport mvp;
            mvp = mc.getViewport();
            mvp.setBounds(re);
            mc.setViewport(mvp);
        }
        
        mapFrame.setVisible(true);
    }
}
