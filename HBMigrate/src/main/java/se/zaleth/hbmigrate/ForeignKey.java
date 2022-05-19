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
public class ForeignKey extends Mapping {

    private TableMapping parent;
    
    public ForeignKey(Element e) {
        super(e);
        mapType = Mapping.FOREIGN_KEY_MAPPING;
        parent = TableMapping.getByTableName(((Element) e.getParentNode()).getAttribute("table"));
    }
    
    @Override
    public String getAnnotations() {
        return "@ForeignKey";
    }

    @Override
    public String getJavaType() {
        // we will have the same type as the column we reference
        return parent.getColumnByTName(getAttribute("column")).getJavaType();
    }
    
}
