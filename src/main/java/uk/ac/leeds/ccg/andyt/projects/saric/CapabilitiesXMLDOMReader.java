/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.projects.saric;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import uk.ac.leeds.ccg.andyt.generic.io.Generic_XMLDOMReader;

/**
 *
 * @author geoagdt
 */
public class CapabilitiesXMLDOMReader extends Generic_XMLDOMReader {

    public TreeSet<String> outcodePostcodes;

    public CapabilitiesXMLDOMReader() {
    }

    public CapabilitiesXMLDOMReader(
            File file) {
        init(file, "*");
    }

    protected void initNodeList() {
        nodeList = aDocument.getElementsByTagName("*");
    }

    public static void main(String args[]) {
        CapabilitiesXMLDOMReader r;
        r = new CapabilitiesXMLDOMReader();
        File file = new File("C:/Users/geoagdt/src/projects/saric/data/MetOffice/layer/wxobs/all/xml/capabilities.xml");
        String nodeName = "*";
        r.init(file, nodeName);
        ArrayList<String> times;
        times = r.getTimes("RADAR_UK_Composite_Highres");
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

    protected ArrayList<String> getTimes(String layerName) {
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
                if (nNodeName.equalsIgnoreCase("Time")) {
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
