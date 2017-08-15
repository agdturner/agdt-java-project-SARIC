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
package uk.ac.leeds.ccg.andyt.projects.saric.io;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import uk.ac.leeds.ccg.andyt.generic.io.Generic_XMLDOMReader;
import uk.ac.leeds.ccg.andyt.projects.saric.core.SARIC_Environment;
import uk.ac.leeds.ccg.andyt.projects.saric.io.SARIC_Files;

/**
 *
 * @author geoagdt
 */
public abstract class SARIC_XMLDOMReader extends Generic_XMLDOMReader {
    
    public SARIC_Environment se;
    public SARIC_Files files;

    protected SARIC_XMLDOMReader() {
    }
    
    protected SARIC_XMLDOMReader(
        SARIC_Environment se) {
            this.se = se;
             this.files = se.getFiles();
    }

    protected void initNodeList() {
        nodeList = aDocument.getElementsByTagName("*");
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

    @Override
    protected void parseNodeList() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
