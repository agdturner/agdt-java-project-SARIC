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

/**
 *
 * @author geoagdt
 */
public class SARIC_Strings {

    // Symbols
    String symbol_ampersand;
    String symbol_equals;
    String string_datatype;
    String symbol_backslash;
    // Normal strings listed alphabetically
    String string_3hourly;
    String string_all;
    String string_Catchment;
    String string_CatchmentBoundaries;
    String string_CEH;
    String string_capabilities;
     String string_config;
    String string_DataPoint;
    String string_Generated;
    String string_hourly;
    String string_input;
    String string_inspire;
     String string_key;
    String string_layer;
     String string_MetOffice;
     String string_Nimrod;
     String string_output;
    String string_png;
    String symbol_questionmark;
    String string_res;
    String string_sitelist;
    String string_val;
    String string_view;
    String string_wmts;
    String string_wxfcs;
    String string_wxobs;
    String string_xml;

    public String getString_Catchment() {
        if (string_Catchment == null) {
            string_Catchment = "Catchment";
        }
        return string_Catchment;
    }

    public String getString_CatchmentBoundaries() {
        if (string_CatchmentBoundaries == null) {
            string_CatchmentBoundaries = "CatchmentBoundaries";
        }
        return string_CatchmentBoundaries;
    }

    public String getString_CEH() {
        if (string_CEH == null) {
            string_CEH = "CEH";
        }
        return string_CEH;
    }

    public String getString_config() {
        if (string_config == null) {
            string_config = "config";
        }
        return string_config;
    }

    public String getString_DataPoint() {
        if (string_DataPoint == null) {
            string_DataPoint = "DataPoint";
        }
        return string_DataPoint;
    }

    public String getString_Generated() {
        if (string_Generated == null) {
            string_Generated = "generated";
        }
        return string_Generated;
    }

    public String getString_input() {
        if (string_input == null) {
            string_input = "input";
        }
        return string_input;
    }

    public String getString_inspire() {
        if (string_inspire == null) {
            string_inspire = "inspire";
        }
        return string_inspire;
    }

    public String getString_MetOffice() {
        if (string_MetOffice == null) {
            string_MetOffice = "MetOffice";
        }
        return string_MetOffice;
    }

    public String getString_Nimrod() {
        if (string_Nimrod == null) {
            string_Nimrod = "Nimrod";
        }
        return string_Nimrod;
    }

    public String getString_output() {
        if (string_output == null) {
            string_output = "output";
        }
        return string_output;
    }
    
     public String getString_datatype() {
        if (string_datatype == null) {
            string_datatype = "datatype";
        }
        return string_datatype;
    }

    public String getString_layer() {
        if (string_layer == null) {
            string_layer = "layer";
        }
        return string_layer;
    }

    public String getString_wxobs() {
        if (string_wxobs == null) {
            string_wxobs = "wxobs";
        }
        return string_wxobs;
    }

    public String getString_png() {
        if (string_png == null) {
            string_png = "png";
        }
        return string_png;
    }

    public String getString_res() {
        if (string_res == null) {
            string_res = "res";
        }
        return string_res;
    }

    public String getString_3hourly() {
        if (string_3hourly == null) {
            string_3hourly = "3" + getString_hourly();
        }
        return string_3hourly;
    }

    public String getString_hourly() {
        if (string_hourly == null) {
            string_hourly = "hourly";
        }
        return string_hourly;
    }

    public String getSymbol_equals() {
        if (symbol_equals == null) {
            symbol_equals = "=";
        }
        return symbol_equals;
    }

    // Special symbols
    public String getSymbol_ampersand() {
        if (symbol_ampersand == null) {
            symbol_ampersand = "&";
        }
        return symbol_ampersand;
    }

    public String getSymbol_backslash() {
        if (symbol_backslash == null) {
            symbol_backslash = "/";
        }
        return symbol_backslash;
    }

    public String getString_wmts() {
        if (string_wmts == null) {
            string_wmts = "wmts";
        }
        return string_wmts;
    }

    public String getString_wxfcs() {
        if (string_wxfcs == null) {
            string_wxfcs = "wxfcs";
        }
        return string_wxfcs;
    }

    public String getString_xml() {
        if (string_xml == null) {
            string_xml = "xml";
        }
        return string_xml;
    }

    public String getString_key() {
        if (string_key == null) {
            string_key = "key";
        }
        return string_key;
    }

    public String getString_all() {
        if (string_all == null) {
            string_all = "all";
        }
        return string_all;
    }

    public String getString_val() {
        if (string_val == null) {
            string_val = "val";
        }
        return string_val;
    }

    public String getString_view() {
        if (string_view == null) {
            string_view = "view";
        }
        return string_view;
    }

    public String getSymbol_questionmark() {
        if (symbol_questionmark == null) {
            symbol_questionmark = "?";
        }
        return symbol_questionmark;
    }

    public String getString_sitelist() {
        if (string_sitelist == null) {
            string_sitelist = "sitelist";
        }
        return string_sitelist;
    }

    public String getString_capabilities() {
        if (string_capabilities == null) {
            string_capabilities = "capabilities";
        }
        return string_capabilities;
    }
    
}
