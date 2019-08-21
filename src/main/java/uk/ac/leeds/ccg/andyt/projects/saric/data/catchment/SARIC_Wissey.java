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
import java.util.HashSet;
import uk.ac.leeds.ccg.andyt.geotools.Geotools_Shapefile;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Environment;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Strings;
import uk.ac.leeds.ccg.andyt.projects.saric.data.metoffice.datapoint.site.SARIC_Site;
import uk.ac.leeds.ccg.andyt.vector.geometry.Vector_Envelope2D;

/**
 *
 * @author geoagdt
 */
public class SARIC_Wissey extends SARIC_Catchment {

    public SARIC_Wissey(SARIC_Environment se) {
        super(se, SARIC_Strings.s_Wissey);
    }

    /**
     * From the National River Flow Archive.
     * http://nrfa.ceh.ac.uk/data/station/spatial/33006
     * @return 
     */
    public Geotools_Shapefile getNRFAAGDT_Shapefile(){
        return getNRFAAGDT_Shapefile("33006.shp");
    }
    
    /**
     * Provided by Anglian Water.
     * @return 
     */
    @Override
    public Geotools_Shapefile getWaterCompanyAGDT_Shapefile() {
        return getAGDT_Shapefile("WISSEY_RBMP2.shp");
    }
    
    @Override
    public HashSet<SARIC_Site> getForecastsSitesInStudyArea(String time) {
        HashSet<SARIC_Site> result;
        File f;
        f = Files.getGeneratedDataMetOfficeDataPointForecastsSitesInWisseyFile();
        if (f.exists()) {
            result = (HashSet<SARIC_Site>) se.io.readObject(f);
        } else {
            result = super.getForecastsSitesInStudyArea(time);
            se.io.writeObject(result, f);
        }
        return result;
    }
    
    /**
     * Wissey Bounding Box: MinX 562996.9681000011; MaxX 599975.0000299839; MinY
     * 288600.00000000186; MaxY 313620.0.
     *
     * @return
     */
    @Override
    public Vector_Envelope2D getBounds() {
        return getBounds(
                new BigDecimal("562996.9681000011"),
                new BigDecimal("599975.0000299839"),
                new BigDecimal("288600.00000000186"),
                new BigDecimal("313620.0"));
    }
    
    /**
     * Teifi Bounding Box: MinX 562000; MaxX 600000; MinY
     * 288000; MaxY 314000.
     * 
     * @return
     */
    @Override
    public Vector_Envelope2D get1KMGridBounds() {
        return getBounds(
                new BigDecimal("562000"),
                new BigDecimal("600000"),
                new BigDecimal("288000"),
                new BigDecimal("314000"));
    }
}
