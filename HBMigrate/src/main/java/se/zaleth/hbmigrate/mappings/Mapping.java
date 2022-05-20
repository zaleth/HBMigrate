/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.zaleth.hbmigrate.mappings;

import java.util.HashMap;
import org.w3c.dom.*;
import se.zaleth.hbmigrate.HBMigrate;

/**
 *
 * @author krister
 */
public abstract class Mapping {
    
    public static final int INVALID_MAPPING = 0;
    public static final int COLUMN_MAPPING = 1;
    public static final int ID_MAPPING = 2;
    public static final int MANY_TO_ONE_MAPPING = 3;
    public static final int FOREIGN_KEY_MAPPING = 4;
    
    
    private int type;
    protected int mapType;
    private HashMap<String,String> attributes;
    
    public Mapping(Element e) {
        type = e.getNodeType();
        mapType = INVALID_MAPPING;
        attributes = new HashMap<>();
        parseAttributes(e.getAttributes());
    }
    
    public abstract String getAnnotations();
    
    public abstract String getJavaType();
    
    public String getJavaName() {
        return attributes.get("name");
    }
    
    public String getTableName() {
        return attributes.get("column");
    }
    
    public int getType() {
        return type;
    }
    
    public String getAttribute(String key) {
        return attributes.get(key);
    }
    
    public void setAttribute(String key, String value) {
        attributes.put(key, value);
    }
    
    public void parseAttribute(Node node) {
        if(node.getNodeType() != Node.ATTRIBUTE_NODE) {
            HBMigrate.log("parseAttribute called on node '" + node.getNodeName() + "' of type " + node.getNodeType());
        } else {
            Attr a = (Attr) node;
            if(a.getSpecified())
                setAttribute(a.getName(), a.getValue());
        }
    }
    
    public final void parseAttributes(NamedNodeMap list) {
        if(list != null) {
            for(int i = 0; i < list.getLength(); i++) {
                parseAttribute(list.item(i));
            }
        }
    }
    
    public static Mapping parseElement(Element e, TableMapping parent) {
        if(e.getNodeType() == Node.ELEMENT_NODE) {
            String name = e.getTagName();
            if(name.equals("property"))
                return new Column(e);
            else if(name.equals("id"))
                return new IdColumn(e);
            else if(name.equals("many-to-one"))
                return new ManyToOne(e);
            else if(name.equals("key"))
                return new ForeignKey(e, parent);
            HBMigrate.log("WARNING: unhandled element class '" + name + "'");        
        }
        
        HBMigrate.log("ERROR: parseElement() returning null");
        return null;
    }
    
    public static Mapping parseElement(Element e, Mapping m) {
        switch(e.getNodeType()) {
            case Node.ELEMENT_NODE:
                return Mapping.parseElement(e, (TableMapping) null);
                
            case Node.ATTRIBUTE_NODE:
                m.setAttribute(e.getNodeName(), e.getNodeValue());
                return m;
                
            default:
                HBMigrate.log("WARNING: unhandled node type " + e.getNodeType());
                return null;
        }
    }
}
