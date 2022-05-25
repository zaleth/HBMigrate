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
public abstract class InnerMapping extends Mapping {

    protected String innerName;
    
    public InnerMapping(Node n) {
        super(n);
        innerName = n.getNodeName();
        // maintain type as INVALID_MAPPING
    }
    
    public boolean isEmpty() {
        return getChildren().isEmpty();
    }
    
    public abstract void consume(Mapping outer);
    
}
