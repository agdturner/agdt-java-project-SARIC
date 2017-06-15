/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.projects.saric;
import java.io.File;
import java.util.Iterator;
import java.util.TreeSet;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import uk.ac.leeds.ccg.andyt.generic.io.Generic_XMLDOMReader;

/**
 *
 * @author geoagdt
 */
public class CapabilitiesXMLDOMReader  extends Generic_XMLDOMReader {

    public TreeSet<String> outcodePostcodes;

    public CapabilitiesXMLDOMReader() {
    }

    public CapabilitiesXMLDOMReader(
            File file) {
        init(file, "url");
        parseNodeList();
    }
    
    public static void main(String args[]) {
        CapabilitiesXMLDOMReader r;
        r = new CapabilitiesXMLDOMReader();
        File file = new File("/scratch02/zoopla/outcodes.xml");
        String nodeName = "url";
        r.init(file, nodeName);
        r.parseNodeList();
    }

    protected void print() {
        Iterator<String> ite = outcodePostcodes.iterator();
        while (ite.hasNext()) {
            System.out.println(ite.next());
        }
    }

    @Override
    protected final void parseNodeList() {
        //readNodeListElements();
        outcodePostcodes = new TreeSet<String>();
        /*
         * <url>
         *  <lastmod>2013-03-05</lastmod>
         *  <priority>0.4</priority>
         *  <loc>http://www.zoopla.co.uk/home-values/browse/tn35/</loc>
         *  <changefreq>weekly</changefreq>
         * </url>
         */
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node nNode = nodeList.item(i);
            //System.out.println(nNode.getNodeName());
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element aElement = (Element) nNode;
                //System.out.println("lastmod : " + aElement.getAttribute("lastmod"));
                //System.out.println("priority : " + aElement.getElementsByTagName("priority").item(0).getTextContent());
                String loc = aElement.getElementsByTagName("loc").item(0).getTextContent();
                //System.out.println("loc : " + loc);
                if (loc.contains("http://www.zoopla.co.uk/home-values/")) {
                    String[] split = loc.split("http://www.zoopla.co.uk/home-values/");
                    //System.out.println("split[1] : " + split[1]);
                    String[] split2 = split[1].split("/");
                    if (split2.length == 1) {
                        //System.out.println("split2[0] : " + split2[0]);
                        outcodePostcodes.add(split2[0]);
                    }
                }
                //System.out.println("changefreq : " + aElement.getElementsByTagName("changefreq").item(0).getTextContent());
            }
        }
        //print();
    }
}
