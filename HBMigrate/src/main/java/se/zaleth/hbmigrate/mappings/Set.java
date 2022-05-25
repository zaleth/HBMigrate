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
public class Set extends Mapping {

    private TableMapping parent;
    
    Set(Element e, TableMapping parent) {
        super(e);
        this.mapType = Mapping.SET_MAPPING;
        this.parent = parent;
    }
    
    @Override
    public String getAnnotations() {
        return "@Set";
    }

    @Override
    public String getJavaType() {
        return "<inferred type>";
    }
    
}
