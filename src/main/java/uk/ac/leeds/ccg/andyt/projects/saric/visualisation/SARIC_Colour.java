/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.projects.saric.visualisation;

import java.awt.Color;
import java.util.TreeMap;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Environment;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Object;

/**
 *
 * @author geoagdt
 */
public class SARIC_Colour extends SARIC_Object {
    
    protected SARIC_Colour(){}
    
    public SARIC_Colour(SARIC_Environment se){
        super(se);
    }
    
    //      Colour: ColourHex: Official Range: Mid range value used in mm/hr
    //      Blue: #0000FE: 0.01 - 0.5: 0.25 mm/hr
    //      Light Blue: #3265FE: 0.5 - 1: 0.75
    //      Muddy Green: #7F7F00: 1 - 2: 1.5
    //      Yellow: #FECB00: 2 - 4: 3
    //      Orange: #FE9800: 4 - 8: 6
    //      Red: #FE0000: 8 - 16: 12
    //      Pink: #FE00FE: 16 - 32: 24
    //      Pale Blue: #E5FEFE: 32+: 48
    public TreeMap<Double, Color> getColorMap() {
        TreeMap<Double, Color> result;
        result = new TreeMap<Double, Color>();
        result.put(0d, Color.WHITE);
        Color Blue = Color.decode("#0000FE");
        result.put(1.0d, Blue);
        Color LightBlue = Color.decode("#3265FE");
        result.put(2.0d, LightBlue);
        Color MuddyGreen = Color.decode("#7F7F00");
        result.put(4.0d, MuddyGreen);
        Color Yellow = Color.decode("#FECB00");
        result.put(8.0d, Yellow);
        Color Orange = Color.decode("#FE9800");
        result.put(16d, Orange);
        Color Red = Color.decode("#FE0000");
        result.put(32.0d, Red);
        Color Pink = Color.decode("#FE00FE");
        result.put(64.0d, Pink);
        Color PaleBlue = Color.decode("#E5FEFE");
        result.put(1000.0d, PaleBlue);
        return result;
    }
    
    //      Colour: ColourHex: Official Range: Mid range value used in mm/hr
    //      Blue: #0000FE: 0.0 - 0.4
    //      Light Blue: #3265FE: 0.4 - 0.8
    //      Muddy Green: #7F7F00: 0.8 - 1.2
    //      Yellow: #FECB00: 1.2 - 1.6
    //      Orange: #FE9800: 1.6 - 2.0
    //      Red: #FE0000: 2.0 - 2.4
    //      Pink: #FE00FE: 2.4 - 2.8
    //      Pale Blue: #E5FEFE: 2.8 +
    public TreeMap<Double, Color> getVarianceColorMap() {
        TreeMap<Double, Color> result;
        result = new TreeMap<Double, Color>();
        result.put(0d, Color.WHITE);
        Color Blue = Color.decode("#0000FE");
        result.put(0.2d, Blue);
        Color LightBlue = Color.decode("#3265FE");
        result.put(0.6d, LightBlue);
        Color MuddyGreen = Color.decode("#7F7F00");
        result.put(1d, MuddyGreen);
        Color Yellow = Color.decode("#FECB00");
        result.put(1.4d, Yellow);
        Color Orange = Color.decode("#FE9800");
        result.put(1.8d, Orange);
        Color Red = Color.decode("#FE0000");
        result.put(2.2d, Red);
        Color Pink = Color.decode("#FE00FE");
        result.put(2.6d, Pink);
        Color PaleBlue = Color.decode("#E5FEFE");
        result.put(1000.0d, PaleBlue);
        return result;
    }
}
