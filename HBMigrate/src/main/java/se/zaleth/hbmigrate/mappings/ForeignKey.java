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
public class ForeignKey extends Mapping {

    private Mapping parent;
    private String refClass;
    
    public ForeignKey(Element e, Mapping parent) {
        super(e);
        mapType = Mapping.FOREIGN_KEY_MAPPING;
        this.parent = parent;
    }
    
    @Override
    public String getAnnotations() {
        return "@ForeignKey@JoinColumn(name=\"" + getAttribute("column") + "\")\n";
    }

    @Override
    public String getJavaName() {
        return "fk" + getJavaType();
    }
    
    @Override
    public String getJavaType() {
        if(parent == null)
            return "";
        return parent.getJavaType() == null ? "" : parent.getJavaType();
    }
    
}
