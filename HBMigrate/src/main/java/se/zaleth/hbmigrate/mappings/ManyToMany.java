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
public class ManyToMany extends Mapping {
    
    public ManyToMany(Element e) {
        super(e);
        mapType = Mapping.MANY_TO_MANY_MAPPING;
    }
    
    public String getClassName() {
        return getAttribute("class");
    }

    /*public void setClassName(String className) {
        setAttribute("class", className);
    }*/

    @Override
    public String getAnnotations() {
        StringBuilder sb = new StringBuilder("@ManyToMany\n");
        if(getAttribute("lazy") != null)
            sb.append("@LazyToOne(LazyToOneOption.").append(getAttribute("lazy").toUpperCase()).
                    append(")\n");
        if(getAttribute("column") != null)
            sb.append("@JoinColumn(name=").append(getAttribute("column")).append(")\n");
                        
        return sb.toString();
    }

    @Override
    public String getJavaType() {
        return getClassName();
    }
    
}
