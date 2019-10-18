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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.gce.arcgrid.ArcGridReader;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.GridCoverageLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.map.MapViewport;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.swing.JMapFrame;
import uk.ac.leeds.ccg.andyt.geotools.core.Geotools_Environment;
import uk.ac.leeds.ccg.andyt.geotools.Geotools_Maps;
import uk.ac.leeds.ccg.andyt.geotools.Geotools_Shapefile;
import uk.ac.leeds.ccg.andyt.geotools.Geotools_Style;
import uk.ac.leeds.ccg.andyt.geotools.Geotools_StyleParameters;
import uk.ac.leeds.ccg.andyt.geotools.demo.Geotools_DisplayShapefile;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridDouble;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridDoubleFactory;
import uk.ac.leeds.ccg.andyt.grids.io.Grids_Files;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Environment;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Strings;
import uk.ac.leeds.ccg.andyt.projects.saric.data.catchment.SARIC_Teifi;
import uk.ac.leeds.ccg.andyt.projects.saric.data.catchment.SARIC_Wissey;
import uk.ac.leeds.ccg.andyt.projects.saric.io.SARIC_Files;

/**
 *
 * @author geoagdt
 */
public class SARIC_DataViewer extends Geotools_DisplayShapefile implements Runnable {

    SARIC_Environment se;
    SARIC_Files sf;
    Geotools_Environment Geotools_Env;

    boolean doWissey;
    boolean doTeifi;
    boolean addGBHRUs;
    ArrayList<GridCoverageLayer> gcls;

    private SARIC_DataViewer() {
    }

    public SARIC_DataViewer(SARIC_Environment se, boolean doWissey,
            boolean doTeifi, boolean addGBHRUs) {
        this.se = se;
        this.sf = se.files;
        this.doWissey = doWissey;
        this.doTeifi = doTeifi;
        this.addGBHRUs = addGBHRUs;
        Geotools_Env = se.geotoolsEnv;
    }

    @Override
    public void run() {
        try {
            // Ititalise gcl
            gcls = getGridCoverageLayers();

            MapContent mc;
            ReferencedEnvelope re;

            mc = new MapContent();

            addGridCoverageLayersToMapContent(mc);
            re = mc.getMaxBounds();

            HashMap<Integer, File> files;
            files = new HashMap<>();
            String name;

            Geotools_Shapefile as;
            FeatureLayer fl;

            if (addGBHRUs) {
                File dir;
                File f;
                dir = new File(this.sf.getInputDataCEHDir(), "WGS84");
                name = "ihu_catchments.shp";
                f = Geotools_Env.getShapefile(dir, name, false);
                files.put(0, f);
            }

            if (doWissey) {
                // Wissey
                SARIC_Wissey sw;
                sw = new SARIC_Wissey(se);
                as = sw.getNRFAAGDT_Shapefile();
                files.put(1, as.getFile());
                fl = as.getFeatureLayer();
                mc.addLayer(fl);
                as = sw.getWaterCompanyAGDT_Shapefile();
                files.put(2, as.getFile());
                fl = as.getFeatureLayer();
                mc.addLayer(fl);
                re = fl.getBounds();
                printBounds(re);
            }

            if (doTeifi) {
                // Teifi
                SARIC_Teifi st;
                st = new SARIC_Teifi(se);
                // Add NRFA Catchment boundary
                as = st.getNRFAAGDT_Shapefile();
                files.put(3, as.getFile());
                fl = as.getFeatureLayer();
                mc.addLayer(fl);
                // Add Water Company Catchment boundary
                as = st.getWaterCompanyAGDT_Shapefile();
                files.put(4, as.getFile());
                fl = as.getFeatureLayer();
                mc.addLayer(fl);
                re = fl.getBounds();
                printBounds(re);
                re = mc.getMaxBounds();
                printBounds(re);

                // Add OSM data
                String osmLayerName;
                // Roads
                osmLayerName = "gis.osm_roads_free_1.shp";
                as = st.getOSMAGDT_Shapefile(osmLayerName);
                files.put(5, as.getFile());
                fl = as.getFeatureLayer();
                mc.addLayer(fl);
                // Railways
                osmLayerName = "gis.osm_railways_free_1.shp";
                as = st.getOSMAGDT_Shapefile(osmLayerName);
                files.put(6, as.getFile());
                fl = as.getFeatureLayer();
                mc.addLayer(fl);
                // Water
                osmLayerName = "gis.osm_water_a_free_1.shp";
                as = st.getOSMAGDT_Shapefile(osmLayerName);
                files.put(7, as.getFile());
                fl = as.getFeatureLayer();
                mc.addLayer(fl);
                // Waterways
                osmLayerName = "gis.osm_waterways_free_1.shp";
                as = st.getOSMAGDT_Shapefile(osmLayerName);
                files.put(8, as.getFile());
                fl = as.getFeatureLayer();
                mc.addLayer(fl);
            }

            try {
                displayShapefiles(files, 800, 600, re);
            } catch (Exception ex) {
                Logger.getLogger(Geotools_DisplayShapefile.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
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
    protected void displayShapefiles(HashMap<Integer, File> files,
            int displayWidth, int displayHeight,
            ReferencedEnvelope re) throws Exception {
        MapContent mc;
        mc = new MapContent();

        File f;
        Style style;

        if (addGBHRUs) {
            f = files.get(0);
            style = getStyleIHU();
            addToMap(mc, f, style);
        }
        // First add 
        addGridCoverageLayersToMapContent(mc);

        int id;
        id = 0;
        Iterator<Integer> ite;
        ite = files.keySet().iterator();
        while (ite.hasNext()) {
            id = ite.next();
            if (id != 0) {
//        CoordinateReferenceSystem crs;
//        crs = store.getSchema().getCoordinateReferenceSystem();
//        System.out.println(crs.toWKT());
//        System.out.println(crs.toString());
                switch (id) {
                    case 1:
                        style = getStyleNRFA();
                        break;
                    case 2:
                        style = getStyleWaterCompany();
                        break;
                    case 3:
                        style = getStyleNRFA();
                        break;
                    case 4:
                        style = getStyleWaterCompany();
                        break;
                    case 5:
                        style = getStyleOSMRoad(); // This is for showing road.
                        break;
                    case 6:
                        style = getStyleOSMRailway(); // This is for showing road.
                        break;
                    case 7:
                        style = getStyleOSMWater(); // This is for showing road.
                        break;
                    case 8:
                        style = getStyleOSMWaterWay(); // This is for showing road.
                        break;
                    default:
                        style = getStyleIHU();
                        break;
                }
                addToMap(mc, files.get(id), style);
            }
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

    public void addToMap(MapContent mc, File f, Style s) {
        FileDataStore fds;
        SimpleFeatureSource fs;
        Layer layer;
        try {
            fds = FileDataStoreFinder.getDataStore(f);
            fs = fds.getFeatureSource();
            layer = new FeatureLayer(fs, s);
            mc.layers().add(layer);
        } catch (IOException ex) {
            Logger.getLogger(SARIC_DataViewer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Style getStyleNRFA() {
        Style result;
        Color outlineColor;
        Color fillColor;
        float opacity;
        outlineColor = Color.BLACK;
        fillColor = Color.LIGHT_GRAY;
        opacity = 0;
        result = SLD.createPolygonStyle(outlineColor, fillColor, opacity);
        return result;
    }

    public Style getStyleWaterCompany() {
        Style result;
        Color outlineColor;
        Color fillColor;
        float opacity;
        outlineColor = Color.PINK;
        fillColor = Color.DARK_GRAY;
        opacity = 0;
        result = SLD.createPolygonStyle(outlineColor, fillColor, opacity);
        return result;
    }

    public Style getStyleOSMRoad() {
        Style result;
        Color outlineColor;
        Color fillColor;
        float opacity;
        outlineColor = Color.LIGHT_GRAY;
        fillColor = Color.DARK_GRAY;
        opacity = 0;
        result = SLD.createPolygonStyle(outlineColor, fillColor, opacity);
        return result;
    }

    public Style getStyleOSMRailway() {
        Style result;
        Color outlineColor;
        Color fillColor;
        float opacity;
        outlineColor = Color.RED;
        fillColor = Color.DARK_GRAY;
        opacity = 0;
        result = SLD.createPolygonStyle(outlineColor, fillColor, opacity);
        return result;
    }

    public Style getStyleOSMWater() {
        Style result;
        Color outlineColor;
        Color fillColor;
        float opacity;
        outlineColor = Color.BLUE;
        fillColor = Color.DARK_GRAY;
        opacity = 0;
        result = SLD.createPolygonStyle(outlineColor, fillColor, opacity);
        return result;
    }

    public Style getStyleOSMWaterWay() {
        Style result;
        Color outlineColor;
        Color fillColor;
        float opacity;
        outlineColor = Color.BLUE;
        fillColor = Color.DARK_GRAY;
        opacity = 0;
        result = SLD.createPolygonStyle(outlineColor, fillColor, opacity);
        return result;
    }

    public Style getStyleIHU() {
        Style result;
        Color outlineColor;
        Color fillColor;
        float opacity;
        outlineColor = Color.CYAN;
        fillColor = Color.WHITE;
        opacity = 0;
        result = SLD.createPolygonStyle(outlineColor, fillColor, opacity);
        return result;
    }

    public ArrayList<GridCoverageLayer> getGridCoverageLayers() throws IOException {
        ArrayList<GridCoverageLayer> result;
        result = new ArrayList<>();

        File dir;
        File f;
        String name = "2017-08-09"
                + SARIC_Strings.s_RADAR_UK_Composite_Highres + ".asc";

        if (doWissey) {
            dir = new File(sf.getOutputDataMetOfficeDataPointDir(),
                    "inspire/view/wmts/Wissey/RADAR_UK_Composite_Highres/EPSG_27700_4/2017-08/2017-08-09");
            GridCoverageLayer gcl;
            gcl = getGridCoverageLayer(dir, name);
            result.add(gcl);
        }

        if (doTeifi) {
            dir = new File(sf.getOutputDataMetOfficeDataPointDir(),
                    "inspire/view/wmts0/Teifi/RADAR_UK_Composite_Highres/EPSG_27700_4/2017-08/2017-08-09");
            GridCoverageLayer gcl;
            gcl = getGridCoverageLayer(dir, name);
            result.add(gcl);
        }

        return result;
    }

    public GridCoverageLayer getGridCoverageLayer(File dir, String name) throws IOException {
        Grids_Files gridf = env.grids_env.files;
        GridCoverageLayer result;
        File f;
        f = new File(dir, name);

        Geotools_StyleParameters styleParameters;
        styleParameters = new Geotools_StyleParameters();
        styleParameters.setnClasses(9);
        styleParameters.setPaletteName("Reds");
        styleParameters.setAddWhiteForZero(true);
        styleParameters.setClassificationFunctionName("EqualInterval");

        Geotools_Maps Maps;
        Maps = Geotools_Env.getMaps();

        ArcGridReader agr;
        agr = Maps.getArcGridReader(f);

        GridCoverage2D gc;
        gc = Maps.getGridCoverage2D(agr);

        Grids_GridDoubleFactory gf;
        gf = se.gridsEnv.getProcessor().GridDoubleFactory;
        File gdir;
        gdir = se.env.io.createNewFile(gridf.getGeneratedGridDoubleDir());
        Grids_GridDouble g;
        g = (Grids_GridDouble) gf.create(gdir, f);

        double normalisation = 1.0d;

        Geotools_Style Style;
        Style = Geotools_Env.getStyle();

        Object[] styleAndLegendItems;
        styleAndLegendItems = Style.getStyleAndLegendItems(
                normalisation,
                g,
                gc,
                styleParameters.getClassificationFunctionName(),
                styleParameters.getnClasses(),
                styleParameters.getPaletteName(),
                styleParameters.isAddWhiteForZero());
        Style style;
        style = (Style) styleAndLegendItems[0];
//        ArrayList<AGDT_LegendItem> legendItems;
//        legendItems = (ArrayList<AGDT_LegendItem>) styleAndLegendItems[1];
        result = new GridCoverageLayer(gc, style);
        return result;
    }

    protected void addGridCoverageLayersToMapContent(MapContent mc) {
        Iterator<GridCoverageLayer> ite;
        ite = gcls.iterator();
        GridCoverageLayer gcl;
        while (ite.hasNext()) {
            gcl = ite.next();
            mc.addLayer(gcl);
        }
    }
}
