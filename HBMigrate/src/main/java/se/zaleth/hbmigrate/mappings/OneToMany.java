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
public class OneToMany extends Mapping {

    public OneToMany(Element e) {
        super(e);
        mapType = Mapping.ONE_TO_MANY_MAPPING;
    }
    
    public String getClassName() {
        return getAttribute("class");
    }

    /*public void setClassName(String className) {
        setAttribute("class", className);
    }*/

    @Override
    public String getAnnotations() {
        return "@OneToMany";
    }

    @Override
    public String getJavaType() {
        return getClassName();
    }
    
    
}
