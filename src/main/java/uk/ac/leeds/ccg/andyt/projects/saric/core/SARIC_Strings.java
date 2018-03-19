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
package uk.ac.leeds.ccg.andyt.projects.saric.core;

import uk.ac.leeds.ccg.andyt.generic.core.Generic_Strings;

/**
 *
 * @author geoagdt
 */
public class SARIC_Strings extends Generic_Strings {

    
    // Normal ss listed alphabetically
    String S_3hourly;
    String S_all;
    String s_Catchment;
    String s_CatchmentBoundaries;
    String s_CEH;
    String S_capabilities;
    String s_config;
    String s_daily;
    String s_DataPoint;
    String s_datatype;
    String s_Forecasts;
    String s_Grids;
    String s_GridDoubleFactory;
    String s_hourly;
    String s_inspire;
    String s_key;
    String s_layer;
    String s_MetOffice;
    String s_Nimrod;
    String s_OSM;
    String s_Observations;
    String s_Precipitation_Rate;
    String s_png;
    String s_RADAR_UK_Composite_Highres;
    String s_res;
    String s_site;
    String s_Sites;
    String s_sitelist;
    String s_Teifi;
    String s_val;
    String s_view;
    String s_Wissey;
    String s_wmts;
    String s_wxfcs;
    String s_wxobs;
    String s_xml;

    public String getS_3hourly() {
        if (S_3hourly == null) {
            S_3hourly = "3" + getS_hourly();
        }
        return S_3hourly;
    }

    public String getS_all() {
        if (S_all == null) {
            S_all = "all";
        }
        return S_all;
    }

    public String getS_capabilities() {
        if (S_capabilities == null) {
            S_capabilities = "capabilities";
        }
        return S_capabilities;
    }

    public String getS_Catchment() {
        if (s_Catchment == null) {
            s_Catchment = "Catchment";
        }
        return s_Catchment;
    }

    public String getS_CatchmentBoundaries() {
        if (s_CatchmentBoundaries == null) {
            s_CatchmentBoundaries = "CatchmentBoundaries";
        }
        return s_CatchmentBoundaries;
    }

    public String getS_CEH() {
        if (s_CEH == null) {
            s_CEH = "CEH";
        }
        return s_CEH;
    }

    public String getS_config() {
        if (s_config == null) {
            s_config = "config";
        }
        return s_config;
    }

    public String getS_daily() {
        if (s_daily == null) {
            s_daily = "daily";
        }
        return s_daily;
    }

    public String getS_datatype() {
        if (s_datatype == null) {
            s_datatype = "datatype";
        }
        return s_datatype;
    }

    public String getS_DataPoint() {
        if (s_DataPoint == null) {
            s_DataPoint = "DataPoint";
        }
        return s_DataPoint;
    }

    public String getS_Forecasts() {
        if (s_Forecasts == null) {
            s_Forecasts = "Forecasts";
        }
        return s_Forecasts;
    }

    public String getS_Grids() {
        if (s_Grids == null) {
            s_Grids = "Grids";
        }
        return s_Grids;
    }

    public String getS_GridDoubleFactory() {
        if (s_GridDoubleFactory == null) {
            s_GridDoubleFactory = "GridDoubleFactory";
        }
        return s_GridDoubleFactory;
    }
            
    public String getS_hourly() {
        if (s_hourly == null) {
            s_hourly = "hourly";
        }
        return s_hourly;
    }

    public String getS_inspire() {
        if (s_inspire == null) {
            s_inspire = "inspire";
        }
        return s_inspire;
    }

    public String getS_key() {
        if (s_key == null) {
            s_key = "key";
        }
        return s_key;
    }

    public String getS_layer() {
        if (s_layer == null) {
            s_layer = "layer";
        }
        return s_layer;
    }

    public String getS_MetOffice() {
        if (s_MetOffice == null) {
            s_MetOffice = "MetOffice";
        }
        return s_MetOffice;
    }

    public String getS_OSM() {
        if (s_OSM == null) {
            s_OSM = "OSM";
        }
        return s_OSM;
    }

    
    public String getS_Nimrod() {
        if (s_Nimrod == null) {
            s_Nimrod = "Nimrod";
        }
        return s_Nimrod;
    }

    public String getS_Observations() {
        if (s_Observations == null) {
            s_Observations = "Observations";
        }
        return s_Observations;
    }

    public String getS_png() {
        if (s_png == null) {
            s_png = "png";
        }
        return s_png;
    }

    public String getS_Precipitation_Rate() {
        if (s_Precipitation_Rate == null) {
            s_Precipitation_Rate = "Precipitation_Rate";
        }
        return s_Precipitation_Rate;
    }

    public String getS_RADAR_UK_Composite_Highres() {
        if (s_RADAR_UK_Composite_Highres == null) {
            s_RADAR_UK_Composite_Highres = "RADAR_UK_Composite_Highres";
        }
        return s_RADAR_UK_Composite_Highres;
    }

    public String getS_res() {
        if (s_res == null) {
            s_res = "res";
        }
        return s_res;
    }

    public String getS_site() {
        if (s_site == null) {
            s_site = "site";
        }
        return s_site;
    }
    
    public String getS_Sites() {
        if (s_Sites == null) {
            s_Sites = "Sites";
        }
        return s_Sites;
    }

    public String getS_sitelist() {
        if (s_sitelist == null) {
            s_sitelist = "sitelist";
        }
        return s_sitelist;
    }

    public String getS_Teifi() {
        if (s_Teifi == null) {
            s_Teifi = "Teifi";
        }
        return s_Teifi;
    }

    public String getS_val() {
        if (s_val == null) {
            s_val = "val";
        }
        return s_val;
    }

    public String getS_view() {
        if (s_view == null) {
            s_view = "view";
        }
        return s_view;
    }

    public String getS_Wissey() {
        if (s_Wissey == null) {
            s_Wissey = "Wissey";
        }
        return s_Wissey;
    }

    public String getS_wmts() {
        if (s_wmts == null) {
            s_wmts = "wmts";
        }
        return s_wmts;
    }

    public String getS_wxfcs() {
        if (s_wxfcs == null) {
            s_wxfcs = "wxfcs";
        }
        return s_wxfcs;
    }

    public String getS_wxobs() {
        if (s_wxobs == null) {
            s_wxobs = "wxobs";
        }
        return s_wxobs;
    }

    public String getS_xml() {
        if (s_xml == null) {
            s_xml = "xml";
        }
        return s_xml;
    }

}
