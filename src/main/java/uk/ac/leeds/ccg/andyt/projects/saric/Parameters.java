/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.projects.saric;

import java.util.ArrayList;

/**
 *
 * @author geoagdt
 */
public class Parameters {

    int nrows;
    int ncols;
    ArrayList<String> times;
    String layerName;

    public Parameters() {
    }

    public Parameters(
            int nrows,
            int ncols,
            ArrayList<String> times,
            String layerName) {
        this.nrows = nrows;
        this.ncols = ncols;
        this.times = times;
        this.layerName = layerName;
    }

    public int getNrows() {
        return nrows;
    }

    public void setNrows(int nrows) {
        this.nrows = nrows;
    }

    public int getNcols() {
        return ncols;
    }

    public void setNcols(int ncols) {
        this.ncols = ncols;
    }

    public ArrayList<String> getTimes() {
        return times;
    }

    public void setTimes(ArrayList<String> times) {
        this.times = times;
    }

    public String getLayerName() {
        return layerName;
    }

    public void setLayerName(String layerName) {
        this.layerName = layerName;
    }

}
