/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.zaleth.hbmigrate.mappings;

import org.w3c.dom.Node;

/**
 *
 * @author krister
 */
public class CompositeElement extends Mapping {

    public CompositeElement(Node n) {
        super(n);
        mapType = Mapping.COMPOSITE_MAPPING;
    }
    
    @Override
    public String getAnnotations() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getJavaType() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
