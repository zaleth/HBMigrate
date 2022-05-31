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

    private Mapping parent;
    
    Set(Element e, Mapping parent) {
        super(e, false);
        this.mapType = Mapping.SET_MAPPING;
        this.parent = parent;
        for(Mapping m : children) {
            switch(m.mapType) {
                case MANY_TO_ONE_MAPPING:
                case MANY_TO_MANY_MAPPING:
                case ONE_TO_MANY_MAPPING:
                case ONE_TO_ONE_MAPPING:
                    // we found our reference
                    m.setAttribute("name", getAttribute("name"));
                    String s = m.getJavaType();
                    Mapping.parseType(this, s);
                    //setJavaType(s.substring(s.lastIndexOf(".") + 1));
                    break;
                    
                default:
                    // do nothing
                    break;
            }
        }
    }
    
    @Override
    public String getAnnotations() {
        StringBuilder sb = new StringBuilder("@Set");
        for(Mapping m : children) {
            sb.append(m.getAnnotations());
        }
        return sb.toString();
    }

}
