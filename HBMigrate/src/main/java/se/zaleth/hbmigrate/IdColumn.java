/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.zaleth.hbmigrate;

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
    public String getAnnotations() {
        return "@Id @GeneratedValue\n" + super.getAnnotations();
    }
    
}
