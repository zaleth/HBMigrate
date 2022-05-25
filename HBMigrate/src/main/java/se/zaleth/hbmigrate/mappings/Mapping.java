/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.zaleth.hbmigrate.mappings;

import java.util.HashMap;
import org.w3c.dom.*;
import se.zaleth.hbmigrate.HBMigrate;
import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 *
 * @author krister
 */
public abstract class Mapping implements ActionListener {
    
    public static final int INVALID_MAPPING = 0;
    public static final int COLUMN_MAPPING = 1;
    public static final int ID_MAPPING = 2;
    public static final int MANY_TO_ONE_MAPPING = 3;
    public static final int FOREIGN_KEY_MAPPING = 4;
    public static final int SET_MAPPING = 5;
    public static final int LIST_MAPPING = 6;
    public static final int GENERATOR_MAPPING = 7;
    public static final int ONE_TO_MANY_MAPPING = 8;
    public static final int PARAM_MAPPING = 9;
    public static final int COMPOSITE_MAPPING = 10;
    public static final int MANY_TO_MANY_MAPPING = 11;
    
    private int type;
    protected int mapType;
    private HashMap<String,String> attributes;
    private ArrayList<Mapping> children;
    private JPanel displayElement;
    private JButton toggleButton;
    private boolean isExpanded;
    
    public Mapping(Node e) {
        type = e.getNodeType();
        mapType = INVALID_MAPPING;
        attributes = new HashMap<>();
        parseAttributes(e.getAttributes());
        children = new ArrayList<>();
        NodeList list = e.getChildNodes();
        for(int i = 0; i < list.getLength(); i++) {
            Mapping m = parseNode(list.item(i), this);
            if(m != this)
                children.add(m);
        }
        displayElement = new JPanel();
        displayElement.setLayout(new BoxLayout(displayElement, BoxLayout.PAGE_AXIS));
        toggleButton = null;
        isExpanded = false;
    }
    
    public abstract String getAnnotations();
    
    public abstract String getJavaType();
    
    protected ArrayList<Mapping> getChildren() {
        return children;
    }
    
    public JPanel getDisplayElement() {
        if(toggleButton == null) {
            toggleButton = new JButton(getDescription());
            toggleButton.setActionCommand(getJavaName());
            toggleButton.addActionListener(this);
            displayElement.add(toggleButton);
        }
        return displayElement;
    }
    
    public String getDescription() {
        return getJavaType() + " " + getJavaName();
    }
    
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
    
    @Override
    public void actionPerformed(ActionEvent e) {
        displayElement.invalidate();
        if(isExpanded) {
            displayElement.removeAll();
            displayElement.add(toggleButton);
            isExpanded = false;
        } else {
            for(String key : attributes.keySet()) {
                displayElement.add(new JLabel(key + ": " + attributes.get(key)));
            }
            for(Mapping m : children) {
                displayElement.add(m.getDisplayElement());
            }
            String s = getAnnotations();
            if(! s.isBlank())
                displayElement.add(new JLabel("Annotations: " + s));
            isExpanded = true;
        }
        displayElement.revalidate();
    }
    
    public static Mapping parseElement(Element e, TableMapping parent) {
        if(e.getNodeType() == Node.ELEMENT_NODE) {
            String name = e.getTagName();
            if(name.equals("property"))
                return new Column(e);
            else if(name.equals("id"))
                return new IdColumn(e);
            else if(name.equals("generator"))
                return new Generator(e);
            else if(name.equals("param"))
                return new Param(e);
            else if(name.equals("many-to-one"))
                return new ManyToOne(e);
            else if(name.equals("one-to-many"))
                return new OneToMany(e);
            else if(name.equals("many-to-many"))
                return new ManyToMany(e);
            else if(name.equals("composite-element"))
                return new CompositeElement(e);
            else if(name.equals("key"))
                return new ForeignKey(e, parent);
            else if(name.equals("set"))
                return new Set(e, parent);
            else if(name.equals("list"))
                return new List(e, parent);
            else if(name.equals("column"))
                return new InnerColumn(e);
            else if(name.equals("list-index"))
                return new InnerListIndex(e);
            
            HBMigrate.log("WARNING: unhandled element class '" + name + "'");        
        }
        
        HBMigrate.log("ERROR: parseElement() returning null");
        return null;
    }
    
    public static Mapping parseNode(Node n, Mapping m) {
        switch(n.getNodeType()) {
            case Node.ELEMENT_NODE:
                Mapping map = Mapping.parseElement((Element) n, null);
                if(map instanceof InnerMapping) {
                    ((InnerMapping)map).consume(m);
                    return m;
                }
                return map;
                
            case Node.ATTRIBUTE_NODE:
                m.setAttribute(n.getNodeName(), n.getNodeValue());
                return m;
                
            case Node.TEXT_NODE:
                String val = n.getNodeValue().trim();
                if(! val.isBlank())
                    m.setAttribute("textValue", val);
                return m;
                
            case Node.COMMENT_NODE:
                // we ignore these nodes
                return m;
                
            default:
                HBMigrate.log("WARNING: unhandled node type " + n.getNodeType());
                return null;
        }
    }
    
    public static void parseType(Column c, String type) {
        if(type.isEmpty())
            c.setJavaType("int");
        else if(type.startsWith("nvarchar"))
            c.setJavaType("String");
        else if(type.startsWith("date"))
            c.setJavaType("java.sql.Date");
        else if(type.startsWith("timestamp"))
            c.setJavaType("java.sql.Timestamp");
        else if(type.startsWith("datetime"))
            c.setJavaType("java.sql.Time");
        else if(type.startsWith("blob"))
            c.setJavaType("java.sql.Blob");
        else // default case, assume it is a class name
            c.setJavaType(type);
    }
    
}
