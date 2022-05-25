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
public class Generator extends Mapping {
    
    public Generator(Element e) {
        super(e);
        mapType = Mapping.GENERATOR_MAPPING;
    }

    @Override
    public String getDescription() {
        return "Generator";
    }
    
    @Override
    public String getAnnotations() {
        StringBuilder sb = new StringBuilder("@GeneratedValue\n");
        for(Mapping m : getChildren()) {
            String s = m.getAnnotations();
            if(! s.isBlank())
                sb.append(s);
        }
        return sb.toString();
    }

    @Override
    public String getJavaType() {
        return "";
    }
    
}
