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
package uk.ac.leeds.ccg.andyt.projects.saric.data.metoffice.datapoint;

import uk.ac.leeds.ccg.andyt.projects.saric.io.SARIC_XMLDOMReader;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Environment;
import uk.ac.leeds.ccg.andyt.projects.saric.util.SARIC_Time;
import uk.ac.leeds.ccg.andyt.vector.geometry.Vector_Envelope2D;
import uk.ac.leeds.ccg.andyt.vector.geometry.Vector_Point2D;

/**
 *
 * @author geoagdt
 */
public class SARIC_MetOfficeCapabilitiesXMLDOMReader extends SARIC_XMLDOMReader {

    SARIC_MetOfficeParameters metOfficeParameters;

    protected SARIC_MetOfficeCapabilitiesXMLDOMReader() {
    }

    public SARIC_MetOfficeCapabilitiesXMLDOMReader(
            SARIC_Environment se,
            File file) {
        super(se);
        this.metOfficeParameters = se.getMetOfficeParameters();
        init(file, "*");
    }

    protected ArrayList<String> getTimesInspireWMTS(String layerName) {
        ArrayList<String> result;
        result = new ArrayList<String>();
        boolean foundLayer;
        foundLayer = false;
        int i;
        i = 0;
        Node n;
        n = null;
        String nTextContent;
        i = find("Contents", nodeList, i);
        //System.out.println(nodeList.item(i).getNodeName());
        while (!foundLayer) {
            i = find("Layer", nodeList, i + 1);
            //System.out.println(nodeList.item(i).getNodeName());
            i = find("ows:Identifier", nodeList, i + 1);
            n = nodeList.item(i);
            //System.out.println(n.getNodeName());
            nTextContent = n.getTextContent();
            if (nTextContent.equalsIgnoreCase(layerName)) {
                //System.out.println(nTextContent);
                foundLayer = true;
            }
        }
        i = find("Value", nodeList, i + 1);
        n = nodeList.item(i);
        //System.out.println(nodeList.item(i).getNodeName());
        nTextContent = n.getTextContent();
        result.add(nTextContent);
        //System.out.println(nTextContent);
        i = find("Value", nodeList, i + 1);
        n = nodeList.item(i);
        while (nodeList.item(i + 1).getNodeName().equalsIgnoreCase("Value")) {
            nTextContent = n.getTextContent();
            result.add(nTextContent);
            //System.out.println(nTextContent);
            i = find("Value", nodeList, i + 1);
            n = nodeList.item(i);
        }
        nTextContent = n.getTextContent();
        result.add(nTextContent);
        return result;
    }

    protected int getNcols(String tileMatrix) {
        int result;
        String tileMatrixParameter;
        tileMatrixParameter = getTileMatrixParameter(
                tileMatrix,
                "MatrixWidth");
        result = new Integer(tileMatrixParameter);
        return result;
    }

    protected int getNrows(String tileMatrix) {
        int result;
        String tileMatrixParameter;
        tileMatrixParameter = getTileMatrixParameter(
                tileMatrix,
                "MatrixHeight");
        result = new Integer(tileMatrixParameter);
        return result;
    }

    protected BigDecimal getCellsize(
            String tileMatrix) {
        BigDecimal result;
        String tileMatrixParameter;
        tileMatrixParameter = getTileMatrixParameter(
                tileMatrix,
                "ScaleDenominator");
        result = new BigDecimal(tileMatrixParameter).multiply(metOfficeParameters.DefaultOGCWMTSResolution);
        return result;
    }

    protected Vector_Envelope2D getDimensions(
            BigDecimal cellsize,
            int nrows,
            int ncols,
            String tileMatrix,
            BigDecimal TwoFiveSix) {
        Vector_Envelope2D result;
        String tileMatrixParameter;
        tileMatrixParameter = getTileMatrixParameter(
                tileMatrix,
                "TopLeftCorner");
        String[] split;
        split = tileMatrixParameter.split(" ");
        BigDecimal TwoFiveSixCellsize;
        TwoFiveSixCellsize = TwoFiveSix.multiply(cellsize);
        BigDecimal xmin;
        xmin = new BigDecimal(split[0]);
        BigDecimal ymax;
        ymax = new BigDecimal(split[1]);
        BigDecimal ymin;
        ymin = ymax.subtract(TwoFiveSixCellsize.multiply(new BigDecimal(nrows)));
        BigDecimal xmax;
        xmax = xmin.add((TwoFiveSixCellsize.multiply(new BigDecimal(ncols))));
        Vector_Point2D p;
        p = new Vector_Point2D(se.getVector_Environment(), xmin, ymin);
        result = p.getEnvelope2D();
        p = new Vector_Point2D(se.getVector_Environment(), xmax, ymax);
        result = result.envelope(p.getEnvelope2D());
        return result;
    }

    /**
     * @param tileMatrix
     * @param parameterName
     * @return
     */
    protected String getTileMatrixParameter(
            String tileMatrix,
            String parameterName) {
        String result;
        boolean foundMatrix;
        foundMatrix = false;
        String nTextContent;
        int i;
        i = 0;
        Node n;
        while (!foundMatrix) {
            i = find("TileMatrix", nodeList, i);
            //System.out.println(nodeList.item(i).getNodeName());
            i = find("ows:Identifier", nodeList, i + 1);
            n = nodeList.item(i);
            //System.out.println(n.getNodeName());
            nTextContent = n.getTextContent();
            if (nTextContent.equalsIgnoreCase(tileMatrix)) {
                foundMatrix = true;
            }
        }
        i = find(parameterName, nodeList, i + 1);
        n = nodeList.item(i);
        //System.out.println(n.getNodeName());
        result = n.getTextContent();
        return result;
    }


    /**
     * Parses the Forecast Service Capabilities XML and returns a list of times
     * at which forecasts are suppose to be available.
     *
     * @param layerName The name of the forecast layer.
     * @return A list of times at which forecasts are suppose to be available.
     */
    protected ArrayList<String> getForecastTimes(String layerName) {
        ArrayList<String> result;
        result = new ArrayList<String>();
        boolean foundLayer;
        foundLayer = false;
        boolean startTimeSet;
        startTimeSet = false;
        SARIC_Time startTime = null;
        SARIC_Time time;
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node n;
            n = nodeList.item(i);
            String nNodeName;
            nNodeName = n.getNodeName();
//            System.out.println(nodeName);
            String nTextContent;
            if (!foundLayer) {
                if (nNodeName.equalsIgnoreCase("LayerName")) {
                    nTextContent = n.getTextContent();
                    if (nTextContent.equalsIgnoreCase(layerName)) {
                        foundLayer = true;
//                        System.out.println(nTextContent);
                    }
                }
            } else {
                String date;
                int timeStartYear;
                int timeStartMonth;
                int timeStartDay;
                int timeStartHour;
                int timeStartMinute;
                int timeStartSecond;
                if (nNodeName.equalsIgnoreCase("TimeSteps") && !startTimeSet) {
                    NamedNodeMap nnm = n.getAttributes();
                    String nodeValue;
                    nodeValue = nnm.item(0).getNodeValue();
                    //result.add(nodeValue);
                    String[] timeSplit;
                    timeSplit = nodeValue.split("T");
                    date = timeSplit[0];
                    String[] timeSplit2;
                    timeSplit2 = timeSplit[0].split("-");
                    timeStartYear = new Integer(timeSplit2[0]);
                    timeStartMonth = new Integer(timeSplit2[1]);
                    timeStartDay = new Integer(timeSplit2[2]);
                    timeSplit2 = timeSplit[1].split(":");
                    if (timeSplit2[0].startsWith("0")) {
                        timeStartHour = new Integer(timeSplit2[0].substring(1));
                    } else {
                        timeStartHour = new Integer(timeSplit2[0]);
                    }
                    if (timeSplit2[1].startsWith("0")) {
                        timeStartMinute = new Integer(timeSplit2[1].substring(1));
                    } else {
                        timeStartMinute = new Integer(timeSplit2[1]);
                    }
                    if (timeSplit2[2].startsWith("0")) {
                        timeStartSecond = new Integer(timeSplit2[2].substring(1));
                    } else {
                        timeStartSecond = new Integer(timeSplit2[2]);
                    }
                    startTime = new SARIC_Time(timeStartYear, timeStartMonth,
                            timeStartDay, timeStartHour, timeStartMinute,
                            timeStartSecond);
                    startTimeSet = true;
                } else {
                    if (nNodeName.equalsIgnoreCase("TimeStep")) {
                        String v;
                        v = n.getTextContent();
                        time = new SARIC_Time(startTime);
                        time.addHours(Integer.valueOf(v));
                        String timeString;
                        timeString = time.toString();
                        System.out.println(timeString);
                        result.add(time.toString());
                    }
                }
            }
        }
        return result;
    }

    protected ArrayList<String> getObservationTimes(String layerName, String nodeName) {
        ArrayList<String> result;
        result = new ArrayList<String>();
        boolean foundLayer;
        foundLayer = false;
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node n;
            n = nodeList.item(i);
            String nNodeName;
            nNodeName = n.getNodeName();
//            System.out.println(nodeName);
            String nTextContent;
            if (!foundLayer) {
                if (nNodeName.equalsIgnoreCase("LayerName")) {
                    nTextContent = n.getTextContent();
                    if (nTextContent.equalsIgnoreCase(layerName)) {
                        foundLayer = true;
//                        System.out.println(nTextContent);
                    }
                }
            } else {
                if (nNodeName.equalsIgnoreCase(nodeName)) {
                    nTextContent = n.getTextContent();
                    result.add(nTextContent);
//                    System.out.println(nTextContent);
                }
            }
        }
        return result;
    }

}
