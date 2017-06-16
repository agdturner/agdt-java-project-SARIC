/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.projects.saric;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.leeds.ccg.andyt.agdtgeotools.AGDT_Geotools;
import uk.ac.leeds.ccg.andyt.agdtgeotools.demo.AGDT_DisplayShapefile;

/**
 *
 * @author geoagdt
 */
public class CEHDataViewer extends AGDT_DisplayShapefile {
    
    public CEHDataViewer() {
    }

    public static void main(String[] args) {
        new CEHDataViewer().run();
    }

    @Override
    public void run() {
        ArrayList<File> files;
        files = new ArrayList<File>();
        String name;
        File dir;
        File f;
        
        name = "ihu_catchments.shp";
        dir = new File(
                "C:/Users/geoagdt/src/projects/saric/data/CEH/WGS84");
        f = AGDT_Geotools.getShapefile(dir, name, false);
        files.add(f);
        
        try {
            displayShapefiles(files);
        } catch (Exception ex) {
            Logger.getLogger(AGDT_DisplayShapefile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
