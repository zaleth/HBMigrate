/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.zaleth.hbmigrate.mappings;

import java.io.*;
import java.util.ArrayList;
import org.w3c.dom.*;
import se.zaleth.hbmigrate.HBMigrate;

/**
 * This class represents the "class" or "joined-subclass" HBM tags
 * @author krister
 */
public class TableMapping {
    
    private static ArrayList<TableMapping> tables = new ArrayList<>();
    private static TableMapping current;
    
    public static TableMapping getByClassName(String pack, String name) {
        for(TableMapping tm : tables)
            if(tm.packName.equals(pack) && tm.className.equals(name))
                return tm;
        return null;
    }
    
    public static TableMapping getByTableName(String name) {
        for(TableMapping tm : tables)
            if(tm.tableName.equals(name))
                return tm;
        return null;
    }
    
    public static String getCurrentTableName() {
        if(current == null)
            return "";
        return current.tableName;
    }
    
    private TableMapping parent;
    
    private String packName;
    private String className;
    private String tableName;
    private String extendsName;
    private boolean isAbstract;
    private String lazy;

    private IdColumn id;
    private ArrayList<Mapping> mappings;
    private ArrayList<Column> columns;
    private ArrayList<ManyToOne> manyToOnes;
    private ArrayList<TableMapping> subClasses;

    public String getPackName() {
        return packName;
    }
    
    public TableMapping() {
        mappings = new ArrayList<>();
        columns = new ArrayList<>();
        manyToOnes = new ArrayList<>();
        subClasses = new ArrayList<>();
        parent = null;
        tables.add(this);
        current = this;
    }

    public TableMapping(Element classElement) {
        this();
        if(classElement.getTagName().contains("class")) {
            parseDocument(classElement);
        }
    }
    
    public TableMapping(Element classElement, TableMapping parent) {
        this();
        this.parent = parent;
        if(classElement.getTagName().contains("class")) {
            parseDocument(classElement);
        }
    }
    
    private void parseDocument(Element docRoot) {
        TableMapping next = null;
        NamedNodeMap attrs = docRoot.getAttributes();
        Node sibling = docRoot.getNextSibling();
        //Mapping m = Mapping.parseElement((Element) child);
        
        if(attrs != null) {
            for(int i = 0; i < attrs.getLength(); i++) {
                Node n = attrs.item(i);
                if(n.getNodeType() == Node.ATTRIBUTE_NODE) {
                    Attr a = (Attr) n;
                    String name = a.getName();
                    if(name.equals("name")) {
                        String str = a.getValue();
                        if(str.lastIndexOf(".") > -1) {
                            packName = str.substring(0, str.lastIndexOf("."));
                            className = str.substring(str.lastIndexOf(".") + 1);
                        } else {
                            HBMigrate.log("WARNING: class name does not include package");
                            packName = "";
                            className = str;
                        }
                    } else if(name.equals("table")) {
                        tableName = a.getValue();
                    } else if(name.equals("abstract")) {
                        isAbstract = Boolean.parseBoolean(a.getValue());
                    } else if(name.equals("lazy")) {
                        lazy = a.getValue();
                    } else
                        if(a.getSpecified())
                            HBMigrate.log("WARNING: unhandled attribute '" + name + "' (value is " + a.getValue() + ") of node " + docRoot.getTagName());
                } else {
                    HBMigrate.log("WARNING: node '" + n.getNodeName() + "' has node type " + n.getNodeType() + " in " + docRoot.getTagName());
                }
            }
            HBMigrate.log(" -- " + packName + "." + className + " -> " + tableName);
        }
        // first, process our children
        NodeList children = docRoot.getChildNodes();
        Mapping last = null;
        for(int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if(! node.getParentNode().isSameNode(docRoot))
                HBMigrate.log(" --- HAHAHA ---");
            switch(node.getNodeType()) {
                case Node.ELEMENT_NODE:
                    if(((Element) node).getTagName().equals("joined-subclass")) {
                        subClasses.add(new TableMapping((Element) node, this));
                    } else {
                        if(last != null)
                            mappings.add(last);
                        last = Mapping.parseElement((Element) node, last);
                        if(last instanceof IdColumn)
                            id = (IdColumn) last;
                    }
                    break;
                    
                case Node.ATTRIBUTE_NODE:
                    if(last != null)
                        last.parseAttribute(node);
                    else
                        HBMigrate.log("WARNING: orphan attribute detected");
                    break;
                    
                case Node.COMMENT_NODE:
                    //HBMigrate.log(((Comment) node).getData());
                    break;

                case Node.TEXT_NODE:
                    //HBMigrate.log(((Text) node).getData());
                    break;
                    
                default:
                    HBMigrate.log("WARNING: node '" + node.getNodeName() + "' has node type " + node.getNodeType());
                    break;
            }
        }
        if(last != null)
            mappings.add(last);
        
        // then, process our siblings
        if(sibling != null && sibling.getNodeType() == Node.ELEMENT_NODE)
            next = new TableMapping((Element) sibling);
        
    }
    
    public void createNewFile(File targetDir) {
        
    }
    
    public void modifyExistingFile(File targetDir) {
        try {
            String className = getClassName();
            className = className.substring(className.lastIndexOf(".") + 1);
            
            // we create the temp file in target dir in the hope the rename at the end will be faster
            File target = File.createTempFile("HBM", ".tmp", targetDir);
            
            // source file, will also be the final target
            File source = new File(targetDir, className + ".java");
            
            if(! source.exists()) {
                //wrapFromTableMapping(map, targetDir, packageName);
                createNewFile(targetDir);
            } else {
                PrintWriter out = new PrintWriter(new FileOutputStream(target));
                BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(source)));

                String line;
                while(!(line = in.readLine()).contains("class")) {
                    out.println(line);
                }

                // first injection, import line and class annotations
                out.println("import javax.persistence.*;");
                out.println();
                out.println("@Entity");
                out.println("@Table(name = \"" + getTableName() + "\")");
                out.println(line);

                ArrayList<Column> cols = (ArrayList<Column>) getColumns().clone();
                ArrayList<ManyToOne> mtos = (ArrayList<ManyToOne>) getManyToOnes().clone();
                if(getId() != null)
                    cols.add(getId());
                else {
                    // mock up an id field
                    HBMigrate.log("WARNING: no ID found for '" + className + "'");
                    out.println("\t@Id @GeneratedValue long id;");
                }

                // now to scan the body of the class ...
                while((line = in.readLine()) != null) {
                    // hoo boy, how to tell which lines are variable declarations?
                    // a declaration line ends in ';'
                    if(line.endsWith(";")) {
                        String name;
                        // it might have an assignment; if so, we want the part before '='
                        if(line.indexOf("=") > -1)
                            // this is a very nice bit of syntax sugar: index the returned array right away
                            name = line.split("=")[0];
                        else
                            // make sure name has a value
                            name = line.substring(0, line.length() - 1);
                        // now, the last word of our line should be the name of the variable
                        String[] ss = name.split(" ");
                        name = ss[ss.length - 1];
                        Column c = getColumnByJName(cols, name);
                        if(c != null) {
                            if(c instanceof IdColumn) {
                                //HBMigrate.log(name + " is an id column variable");
                                out.println("\t@Id @GeneratedValue");
                                out.println("\t@Column(name = \"" + c.getTableName() + "\")");
                            } else {
                                //HBMigrate.log(name + " is a column variable");
                                out.println("\t@Column(name = \"" + c.getTableName() + "\")");
                            }
                            cols.remove(c);
                        } else {
                            ManyToOne m = getMTOByJName(mtos, name);
                            if(m != null) {
                                //HBMigrate.log(name + " is a many-to-one variable");
                                out.println("\t@ManyToOne");
                                mtos.remove(m);
                            } else {
                                //HBMigrate.log("No variable found in '" + line + "' ('" + name + "')");
                            }
                        }
                    }
                    // no matter what, now output the original line
                    out.println(line);
                }

                // clean up
                in.close();
                out.flush();
                out.close();
                if(!source.delete()) {
                    HBMigrate.log("Error deleting " + source.getName());
                }
                if(!target.renameTo(source)) {
                    HBMigrate.log("Error renaming file to " + source.getName());
                }
                HBMigrate.log("Done with " + source.getName());
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        
        //recurse over subclasses
        ArrayList<TableMapping> maps = getSubClasses();
        for(TableMapping tm : maps)
            tm.modifyExistingFile(targetDir);
        
    }
    /*private void parseElement(Node node) {
        if(node == null)
            return;
        
        if(node.getNodeType() != Node.ELEMENT_NODE) {
            HBMigrate.log("WARNING: node '" + node.getNodeName() + "' has node type " + node.getNodeType());
        } else {
            Element e = (Element) node;
            NamedNodeMap attrs = e.getAttributes();
            if(attrs != null) {
                for(int i = 0; i < attrs.getLength(); i++) {
                    Node n = attrs.item(i);
                    if(n.getNodeType() == Node.ATTRIBUTE_NODE) {
                        Attr a = (Attr) n;
                        String name = a.getName();
                        if(name.equals("name")) {
                            String str = a.getValue();
                            if(str.lastIndexOf(".") > -1) {
                                packName = str.substring(0, str.lastIndexOf("."));
                                className = str.substring(str.lastIndexOf(".") + 1);
                            } else {
                                HBMigrate.log("WARNING: class name does not include package");
                                packName = "";
                                className = str;
                            }
                        } else if(name.equals("table")) {
                            tableName = a.getValue();
                        } else
                            if(a.getSpecified())
                                HBMigrate.log("WARNING: unhandled attribute '" + name + "' (value is " + a.getValue() + ") of node " + e.getTagName());
                    } else {
                        HBMigrate.log("WARNING: node '" + n.getNodeName() + "' has node type " + n.getNodeType() + " in " + e.getTagName());
                    }
                }
            }
            HBMigrate.log("Element " + e.getTagName() + " scanned");
        }
        parseElement(node.getNextSibling());
    }*/
    
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getExtendsName() {
        return extendsName;
    }

    public void setExtendsName(String extendsName) {
        this.extendsName = extendsName;
    }
    
    public IdColumn getId() {
        return id;
    }

    public void setId(IdColumn id) {
        this.id = id;
    }
    
    public void addColumn(Column c) {
        columns.add(c);
    }
    
    public ArrayList<Mapping> getMappings() {
        return mappings;
    }
    
    public ArrayList<Column> getColumns() {
        return columns;
    }

    public void addManyToOne(ManyToOne m) {
        manyToOnes.add(m);
    }
    
    public ArrayList<ManyToOne> getManyToOnes() {
        return manyToOnes;
    }

    public void addSubClass(TableMapping tm) {
        subClasses.add(tm);
    }
    
    public ArrayList<TableMapping> getSubClasses() {
        return subClasses;
    }

    /**
     * Finds a column based on its java (variable) name.
     * @return Column or null
     */
    public Column getColumnByJName(String name) {
        // hardcode to add IdColumn to this list
        if(id.getJavaName().equals(name))
            return id;
        for(Column c : columns)
            if(c.getJavaName().equals(name))
                return c;
        return null;
    }
    
        /**
     * Finds a column based on its java (variable) name.
     * @return Column or null
     */
    private Column getColumnByJName(ArrayList<Column> cols, String name) {
        for(Column c : cols)
            if(c.getJavaName().equals(name))
                return c;
        return null;
    }
    
/**
     * Finds a column based on its table (database) name.
     * @return Column or null
     */
    public Column getColumnByTName(String name) {
        // hardcode to add IdColumn to this list
        if(id.getTableName().equals(name))
            return id;
        for(Column c : columns)
            if(c.getTableName().equals(name))
                return c;
        return null;        
    }
    
    /**
     * Finds a many-to-one based on its java (variable) name.
     * @return ManyToOne or null
     */
    public ManyToOne getMTOByJName(String name) {
        for(ManyToOne m : manyToOnes)
            if(m.getJavaName().equals(name))
                return m;
        return null;
    }
    
    /**
     * Finds a many-to-one based on its java (variable) name.
     * @return ManyToOne or null
     */
    private ManyToOne getMTOByJName(ArrayList<ManyToOne> mtos, String name) {
        for(ManyToOne m : mtos)
            if(m.getJavaName().equals(name))
                return m;
        return null;
    }
    
}
