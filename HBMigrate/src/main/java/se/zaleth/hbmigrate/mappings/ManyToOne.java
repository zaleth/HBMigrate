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
public class ManyToOne extends Mapping {
    
    public ManyToOne(Element e) {
        super(e);
        mapType = Mapping.MANY_TO_ONE_MAPPING;
    }
    
    public String getClassName() {
        return getAttribute("class");
    }

    /*public void setClassName(String className) {
        setAttribute("class", className);
    }*/

    @Override
    public String getAnnotations() {
        return "@ManyToOne";
    }

    @Override
    public String getJavaType() {
        return getClassName();
    }
    
}
