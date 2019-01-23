/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.projects.saric.process;

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
import uk.ac.leeds.ccg.andyt.geotools.core.Geotools_Environment;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Environment;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Object;

/**
 *
 * @author geoagdt
 */
public class SARIC_DisplayShapefile extends SARIC_Object {

    // For convenience
    Geotools_Environment Geotools_Env;
    
    protected SARIC_DisplayShapefile(SARIC_Environment se) {
        super(se);
        Geotools_Env = se.Geotools_Env;
    }
    
    public void run() {
        ArrayList<File> files;
        files = new ArrayList<>();
        String name;
        File dir;
        File f;

//        name = "Sites.shp";
//        name = Strings.getS_Wissey() + "Sites.shp";
//        name = Strings.getS_Teifi() + "Sites.shp";
//        name = Strings.getS_Wissey() + "SitesBuffered.shp";
        name = Strings.getS_Teifi()  + "SitesBuffered.shp";
//        dir = new File(
//                Files.getGeneratedDataMetOfficeDataPointDir(),
//                Strings.getS_Forecasts());
        dir = new File(
                Files.getGeneratedDataMetOfficeDataPointDir(),
                Strings.getS_Observations());
        f = Geotools_Env.getShapefile(dir, name, false);
        files.add(f);

        try {
            displayShapefiles(files);
        } catch (Exception ex) {
            Logger.getLogger(SARIC_DisplayShapefile.class.getName()).log(Level.SEVERE, null, ex);
        }
}

    protected void displayShapefiles(ArrayList<File> files) throws Exception {
        displayShapefiles(files, 800, 600, null);
    }

    /**
     * @param files
     * @param displayWidth
     * @param displayHeight
     * @param re Used to set MapViewport
     * @throws Exception 
     */
    protected void displayShapefiles(
            ArrayList<File> files,
            int displayWidth,
            int displayHeight,
            ReferencedEnvelope re) throws Exception {
        MapContent mc;
        mc = new MapContent();
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
            Style style;
            style = SLD.createSimpleStyle(fs.getSchema());
            Layer layer;
            layer = new FeatureLayer(fs, style);
            mc.layers().add(layer);
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
