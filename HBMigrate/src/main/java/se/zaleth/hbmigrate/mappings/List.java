/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.zaleth.hbmigrate.mappings;

import org.w3c.dom.Element;

/**
 *
 * @author krister
 */
public class List extends Mapping {

    private Mapping parent;
    
    public List(Element e, Mapping parent) {
        super(e);
        this.mapType = Mapping.LIST_MAPPING;
        this.parent = parent;
    }
    
    @Override
    public String getAnnotations() {
        return "@List";
    }

    @Override
    public String getJavaType() {
        return "List";
    }
    
}
