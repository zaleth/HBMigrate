/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.zaleth.hbmigrate.mappings;

import org.w3c.dom.*;

/**
 *
 * @author krister
 */
public class Param extends Mapping {
    
    public Param(Element e) {
        super(e, false);
        mapType = Mapping.PARAM_MAPPING;
        /*NodeList list = e.getChildNodes();
        for(int i = 0; i < list.getLength(); i++) {
            Node n = list.item(i);
            System.out.println(n.getNodeName() + "(" + n.getNodeType() + "): " + n.getNodeValue());
        }*/
    }

    @Override
    public String getAnnotations() {
        return "";
    }

    @Override
    public String getJavaType() {
        return "";
    }
    
}
