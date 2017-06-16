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
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node n;
            n = nodeList.item(i);
            String nNodeName;
            nNodeName = n.getNodeName();
            String nTextContent;
            if (nNodeName.equalsIgnoreCase("Contents")) {
                i = find("Layer", nodeList);
                
                System.out.println(nodeName);
                for (int j = i + 1; j < nodeList.getLength(); j++) {
                    n = nodeList.item(j);
                    nNodeName = n.getNodeName();
                    if (nNodeName.equalsIgnoreCase("Layer")) {
                        System.out.println(nodeName);
                        for (int k = j + 1; k < nodeList.getLength(); k++) {
                            n = nodeList.item(k);
                            nNodeName = n.getNodeName();
                            if (nNodeName.equalsIgnoreCase("ows:Identifier")) {
                                System.out.println(nodeName);
                                nTextContent = n.getTextContent();
                                if (nTextContent.equalsIgnoreCase(layerName)) {
                                    foundLayer = true;
                                    System.out.println(nTextContent);

                                } else {
                                    break;
                                }
                            } else if (nNodeName.equalsIgnoreCase("Layer")) {
                                
                            }
                        }
                    }
                }
            }
            if (!foundLayer) {
                if (nNodeName.equalsIgnoreCase("Layer")) {
                    for (int j
                            = <ows {
                        
                    }:Identifier > RADAR_UK_Composite_Highres <  / ows
                    :Identifier
                            > nTextContent = n.getTextContent();
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

    /**
     * get the index in the nodeList of the next node with tag s
     * @param s
     * @param nodeList
     * @param i
     * @return 
     */
    protected int find(String s, NodeList nodeList, int i) {
        int result;
        for (int j = i; j < nodeList.getLength(); j++) {
            
        }
        return result;
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
