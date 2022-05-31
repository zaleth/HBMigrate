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
public class IdColumn extends Column {
    
    public IdColumn(Element e) {
        super(e);
        mapType = Mapping.ID_MAPPING;
    }
    
    @Override
    public String getJavaType() {
        return "long";
    }
    
    @Override
    public String getAnnotations() {
        StringBuilder sb = new StringBuilder("@Id\n");
        for(Mapping m : getChildren()) {
            String s = m.getAnnotations();
            if(! s.isBlank())
                sb.append(s);
        }
        return sb.append(super.getAnnotations()).toString();
    }
    
}
