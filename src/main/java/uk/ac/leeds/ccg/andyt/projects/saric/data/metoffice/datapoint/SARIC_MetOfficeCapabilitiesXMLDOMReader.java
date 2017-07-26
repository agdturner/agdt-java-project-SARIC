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

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import uk.ac.leeds.ccg.andyt.generic.io.Generic_XMLDOMReader;
import uk.ac.leeds.ccg.andyt.generic.utilities.Generic_Time;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Environment;
import uk.ac.leeds.ccg.andyt.projects.saric.io.SARIC_Files;
import uk.ac.leeds.ccg.andyt.projects.saric.util.SARIC_Time;

/**
 *
 * @author geoagdt
 */
public class SARIC_MetOfficeCapabilitiesXMLDOMReader extends Generic_XMLDOMReader {

    SARIC_Environment SARIC_Environment;
    SARIC_Files SARIC_Files;

    public TreeSet<String> outcodePostcodes;

    protected SARIC_MetOfficeCapabilitiesXMLDOMReader() {
    }

    public SARIC_MetOfficeCapabilitiesXMLDOMReader(
            SARIC_Environment SARIC_Environment,
            File file) {
        this.SARIC_Environment = SARIC_Environment;
        this.SARIC_Files = SARIC_Environment.getSARIC_Files();
        init(file, "*");
    }

    protected void initNodeList() {
        nodeList = aDocument.getElementsByTagName("*");
    }

    protected void print() {
        Iterator<String> ite = outcodePostcodes.iterator();
        while (ite.hasNext()) {
            System.out.println(ite.next());
        }
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
        return result;
    }

    protected int[] getNrowsAndNcols(String tileMatrix) {
        int[] result;
        result = new int[2];
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
        i = find("MatrixWidth", nodeList, i + 1);
        n = nodeList.item(i);
        //System.out.println(n.getNodeName());
        nTextContent = n.getTextContent();
        //System.out.println(nTextContent);
        result[1] = new Integer(nTextContent);
        i = find("MatrixHeight", nodeList, i + 1);
        n = nodeList.item(i);
        //System.out.println(n.getNodeName());
        nTextContent = n.getTextContent();
        //System.out.println(nTextContent);
        result[0] = new Integer(nTextContent);
        return result;
    }

    /**
     * Return the index in the nodeList of the next node after that with index i
     * with name equal to nodeName or return nodeList.getLength();
     *
     * @param nodeName
     * @param nodeList
     * @param i
     * @return
     */
    protected int find(String nodeName, NodeList nodeList, int i) {
        int j;
        Node n;
        String nNodeName;
        for (j = i; j < nodeList.getLength(); j++) {
            n = nodeList.item(j);
            nNodeName = n.getNodeName();
            String nTextContent;
            if (nNodeName.equalsIgnoreCase(nodeName)) {
                return j;
            }
        }
        return j;
    }

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
                if (nNodeName.equalsIgnoreCase("TimeSteps") && ! startTimeSet) {
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
                        time.addMinutes(Integer.valueOf(v));
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

    @Override
    protected void parseNodeList() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
