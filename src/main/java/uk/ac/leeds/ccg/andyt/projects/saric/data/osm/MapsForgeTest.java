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
package uk.ac.leeds.ccg.andyt.projects.saric.data.osm;

import java.io.File;
import org.mapsforge.core.graphics.GraphicFactory;
import org.mapsforge.map.awt.graphics.AwtGraphicFactory;
import org.mapsforge.map.awt.util.AwtUtil;
import org.mapsforge.map.awt.view.MapView;
import org.mapsforge.map.datastore.MapDataStore;
import org.mapsforge.map.datastore.MultiMapDataStore;
import org.mapsforge.map.datastore.MultiMapDataStore.DataPolicy;
import org.mapsforge.map.layer.Layer;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.cache.TileStore;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.reader.MapFile;
import org.mapsforge.map.rendertheme.InternalRenderTheme;

/**
 *
 * @author geoagdt
 */
public class MapsForgeTest {

    MapView mv;

    public MapsForgeTest() {
    }

    public static void main(String[] args) {
        new MapsForgeTest().run();
    }

    public void run() {
        File osmDir;
        osmDir = new File("N:/Earth&Environment/Geography/Research-3/Projects/SARIC Runoff risk/AndyTurner/data/OSM");
        File inputDir;
        inputDir = new File(osmDir, "input");
        File input;
        input = new File(inputDir, "great-britain-latest.osm");
        File tileStoreDir;
        tileStoreDir = new File(osmDir, "TileStore");
        tileStoreDir.mkdir();

         mv = new MapView();
         
        // create a tile cache of suitable size
        TileCache tileCache = AwtUtil.createTileCache(1000, 1.0d, 1000, tileStoreDir);
        
        // tile renderer layer using internal render theme
        MapDataStore mapDataStore = new MapFile(input);
        TileRendererLayer trl = new TileRendererLayer(tileCache, mapDataStore,
                mv.getModel().mapViewPosition, AwtGraphicFactory.INSTANCE);
        trl.setXmlRenderTheme(InternalRenderTheme.DEFAULT);
        
////        MapDataStore mds;
////        mds = new MapDataStore("xml");
//        MultiMapDataStore mmds;
////        mmds = new MultiMapDataStore(DataPolicy.DEDUPLICATE);
////        mmds = new MultiMapDataStore(DataPolicy.RETURN_ALL);
//        mmds = new MultiMapDataStore(DataPolicy.RETURN_FIRST);
//        
//        AwtGraphicFactory agf;
//        agf = new AwtGraphicFactory();
//
//        TileStore ts;
//        ts = new TileStore(tileStoreDir, "tile", agf);
//        TileRendererLayer trl;
//        trl = new TileRendererLayer(ts, mmds, mv.getModel().mapViewPosition, agf);
//        trl.setXmlRenderTheme(InternalRenderTheme.DEFAULT);
        mv.addLayer(trl);
    }

}
