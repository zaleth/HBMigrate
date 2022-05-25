/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.zaleth.hbmigrate.mappings;

import org.w3c.dom.Node;

/**
 *
 * @author krister
 */
public class InnerColumn extends InnerMapping {

    public InnerColumn(Node n) {
        super(n);
        // maintain type as INVALID_MAPPING
    }
    
    @Override
    public void consume(Mapping outer) {
        if(outer instanceof Column) {
            outer.setAttribute("column", getAttribute("name"));
            outer.setAttribute("type", getAttribute("sql-type"));
        } else
            throw new UnsupportedOperationException("Outer Mapping is not a Column.");
    }

    @Override
    public String getAnnotations() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getJavaType() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
