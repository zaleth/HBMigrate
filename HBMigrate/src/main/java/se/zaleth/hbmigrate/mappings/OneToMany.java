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
        super(e, false);
        mapType = Mapping.ONE_TO_MANY_MAPPING;
        Mapping.parseType(this, getClassName());
    }
    
    public String getClassName() {
        return getAttribute("class");
    }

    @Override
    public String getAnnotations() {
        StringBuilder sb = new StringBuilder("@OneToMany");
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
