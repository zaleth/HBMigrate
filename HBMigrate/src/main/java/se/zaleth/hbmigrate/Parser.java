/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.zaleth.hbmigrate;

import se.zaleth.hbmigrate.mappings.Column;
import se.zaleth.hbmigrate.mappings.ManyToOne;
import se.zaleth.hbmigrate.mappings.TableMapping;
import se.zaleth.hbmigrate.mappings.IdColumn;
import java.io.*;
import java.util.ArrayList;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import se.zaleth.hbmigrate.mappings.Mapping;

/**
 *
 * @author krister
 */
class Parser {
    
    private DocumentBuilder builder;
    private Document doc;
    private TableMapping mainMap;
    
    public Parser() {
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch(ParserConfigurationException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        doc = null;
        mainMap = null;
    }
    
    public void loadFile(File f) throws IOException, SAXException {
        doc = builder.parse(f);
        //HBMigrate.log(Node.ELEMENT_NODE + " " + Node.ATTRIBUTE_NODE + " " + Node.TEXT_NODE);
    }
    
    public void traverse() {
        traverse(doc.getDocumentElement(), 0);
    }
    
    private void printSpace(int spaces) {
        for(int n = 0; n < spaces; n++)
            System.out.print( " ");
    }
    
    public void traverse(Node node, int depth) {
        printSpace(depth * 2);
        HBMigrate.log(node.getNodeName() + "(" + node.getNodeType() + ")");
        NamedNodeMap aMap = node.getAttributes();
        if(aMap != null) {
            for(int i = 0; i < aMap.getLength(); i++) {
                Attr attr = (Attr) aMap.item(i);
                printSpace(depth * 2);
                HBMigrate.log(attr.getNodeName() + "=" + attr.getNodeValue());
            }
        }
        NodeList children = node.getChildNodes();
        for(int i = 0; i < children.getLength(); i++) {
            traverse(children.item(i), depth + 1);
        }
    }
    
    public TableMapping parse() {
        Element docRoot = doc.getDocumentElement();
        // only process first <class> element
        Element classElement = (Element) docRoot.getElementsByTagName("class").item(0);
        //mainMap = parseClassElement(classElement);
        mainMap = new TableMapping(classElement);
        return mainMap;
    }
    
    /*private TableMapping parseClassElement(Element classElement) {
        TableMapping map = new TableMapping();
        HBMigrate.log(classElement.getAttribute("name"));
        map.setClassName(classElement.getAttribute("name"));
        HBMigrate.log(classElement.getAttribute("table"));
        map.setTableName(classElement.getAttribute("table"));
        //HBMigrate.log(node.getNodeName() + "(" + node.getNodeType() + ")=" + node.getNodeValue());
        // there better only be one <id> element ...
        Element idElement = (Element) classElement.getElementsByTagName("id").item(0);
        if(idElement != null) {
            IdColumn id = new IdColumn(idElement);
            map.setId(id);
        } else {
            HBMigrate.log("WARNING: no 'id' found for '" + map.getClassName());
        }
        
        // find direct fields
        ArrayList<Element> nodes = getElementsByTagName(classElement, "property");
        for(Element col : nodes) {
            Column c = new Column(col);
            c.setJavaName(col.getAttribute("name"));
            if(col.getAttribute("column").isEmpty()) {
                // look for a child node
                Element child = (Element) col.getElementsByTagName("column").item(0);
                if(child != null) {
                    c.setTableName(child.getAttribute("name"));
                    String type = child.getAttribute("sql-type");
                    Mapping.parseType(c, type);
                } else {
                    HBMigrate.log("WARNING: column '" + c.getJavaName() + "' has no matching DB column");
                    // silently drop this column
                    continue;
                }
            } else {
                c.setTableName(col.getAttribute("column"));
                String type = (col.getAttribute("type"));
                Mapping.parseType(c, type);
            }
            map.addColumn(c);
        }
        
        // find foreign relationships
        nodes = getElementsByTagName(classElement, "many-to-one");
        for(Element el : nodes) {
            ManyToOne mto = new ManyToOne(el);
            map.addManyToOne(mto);
        }
        
        //find subclasses
        nodes = getElementsByTagName(classElement, "joined-subclass");
        for(Element el : nodes) {
            TableMapping tm = parseClassElement(el);
            tm.setExtendsName(map.getClassName());
            map.addSubClass(tm);
        }
        return map;
    }*/
    
    /** 
     * Returns all immediate children matching the given tag.
     * @param parent Parent element
     * @param tag Element tag to look for
     * @return A list of all immediate children matching the given tag
     */
    private ArrayList<Element> getElementsByTagName(Element parent, String tag) {
        NodeList nodes = parent.getElementsByTagName(tag);
        ArrayList<Element> ret = new ArrayList<>();
        
        for(int i = 0; i < nodes.getLength(); i++) {
            Element e = (Element) nodes.item(i);
            if(e.getParentNode().isEqualNode(parent))
                ret.add(e);
        }
        return ret;
    }
    
    public void modifyFile(File targetDir, String packageName) {
        renameFromTableMapping(mainMap, targetDir, packageName);
        //modifyFromTableMapping(mainMap, targetDir, packageName);
    }
    
    /* Create a temp file, copy lines from src file and inject additions as needed */
    private void renameFromTableMapping(TableMapping map, File targetDir, String packageName) {
        if(map == null)
            return;
        map.modifyExistingFile(targetDir);
    }
    
    /* this method, in-place modification, seems not to work as I wish */
    private void modifyFromTableMapping(TableMapping map, File targetDir, String packageName) {
        if(map == null)
            return;
        
        String className = map.getClassName();
        className = className.substring(className.lastIndexOf(".") + 1);
        //HBMigrate.log(targetDir + "/" + className + ".java");
        StringBuilder tmp = new StringBuilder();
        
        try {
            File target = new File(targetDir, className + ".java");
            if(! target.exists()) {
                wrapFromTableMapping(map, targetDir, packageName);
            }
            HBMigrate.log("Modifying " + target.getAbsolutePath());
            RandomAccessFile out = new RandomAccessFile(target, "rw");
            
            if(! seekForString(out, "import javax.persistence", false)) {
                // need to add import, but where?
                seekForString(out, "import", true);
                seekBackForByte(out, (byte) '\n');
                insertString(out, "import javax.persistence.*;\n");
            }
            
            if(seekForString(out, "public class " + className, true)) {
                tmp.delete(0, tmp.length());
                tmp.append("@Entity\n@Table(name=\"");
                tmp.append(map.getTableName());
                tmp.append("\")\n");
                insertString(out, tmp.toString());
            } else {
                HBMigrate.log("WARNING: No class found for '" + className + "'");
                tmp.delete(0, tmp.length());
                tmp.append("@Entity\n@Table(name=\"");
                tmp.append(map.getTableName());
                tmp.append("\")\npublic class ");
                tmp.append(map.getClassName());
                tmp.append(" {\n\n}");
                insertString(out, tmp.toString());                
            }
            
            if(map.getId() == null || !seekForString(out, map.getId().getJavaName(), true)) {
                HBMigrate.log("WARNING: no ID found for '" + className + "'");
                insertString(out, "\t@Id @GeneratedValue int id;\n");
            } else {
                if(seekBackForByte(out, (byte) '\n'))
                    insertString(out, "\t@Id @GeneratedValue\n");
            }
            
            for(Column c : map.getColumns()) {
                seekForString(out, c.getJavaName(), true);
                if(seekBackForByte(out, (byte) '\n'))
                    insertString(out, "\t@Column(name = \"" + c.getTableName() + "\")\n");
                else {
                    HBMigrate.log("WARNING: did not find declaration for '" + c.getJavaName() + "'");
                    // we want to add thje declaration, but where to do it?
                }
            }
            for(ManyToOne m : map.getManyToOnes()) {
                seekForString(out, m.getJavaName(), true);
                if(seekBackForByte(out, (byte) '\n'))
                    insertString(out, "\t@ManyToOne\n");
                else {
                    HBMigrate.log("WARNING: did not find declaration for '" + m.getJavaName() + "'");
                    // we want to add thje declaration, but where to do it?
                }
            }
            
            out.close();
            
            for(TableMapping tm : map.getSubClasses())
                modifyFromTableMapping(tm, targetDir, packageName);
            
            HBMigrate.log("Done");
        } catch(IOException e) {
            e.printStackTrace();
        }
    
    }
    
    /* Injects new string into the file, shuffling the rest of the file to make room
    
    */
    private void insertString(RandomAccessFile file, String str) throws IOException {
        int size = (int) (file.length() - file.getFilePointer());
        byte[] buffer = new byte[size];
        file.readFully(buffer);
        file.writeBytes(str);
        file.write(buffer);
    }
    
    /* Will position the write head at the start of str and return true.
     * If str is not found, returns false and sets file position to 0.
     * Ignores text inside comments.
    */
    private boolean seekForString(RandomAccessFile file, String str, boolean beforeString) throws IOException {
        boolean inOneLineComment = false, inBlockComment = false, seenSlash = false, seenStar = false;
        file.seek(0);
        byte[] chrs = str.getBytes();
        int index = 0;
        
        while(index < chrs.length && file.getFilePointer() < file.length()) {
            byte val = file.readByte();
            switch(val) {
                case '/':
                    if(inBlockComment) {
                        if(seenStar)
                            seenStar = inBlockComment = false;
                    } else if(!inOneLineComment) {
                        if(seenSlash) {
                            seenSlash = false;
                            inOneLineComment = true;
                        } else
                            seenSlash = true;
                    }
                    break;
                case '*':
                    if(!inBlockComment) {
                        if(seenSlash)
                            inBlockComment = true;
                    } else {
                        seenStar = true;
                    }
                    break;
                case '\n':
                    if(inOneLineComment)
                        inOneLineComment = false;
                    break;
                default:
                    seenSlash = seenStar = false;
                    if(!(inBlockComment || inOneLineComment))
                        if(chrs[index] == val)
                            index++;
                        else
                            index = 0;
                    break;
            }
            
            
        }
        if(index == chrs.length) {
            if(beforeString)
                file.seek(file.getFilePointer()-index); // return to start of string
            return true;
        }
        file.seek(0);
        return false;
    }
    
    /* Seeks backwards for a single character. Does not skip comments.
     * Will rewind back to the start of the file if the character is not found.
    */
    private boolean seekBackForByte(RandomAccessFile file, byte seek) throws IOException {
        while(file.getFilePointer() > 0) {
            byte val = file.readByte();
            file.seek(file.getFilePointer() - 2); // skip back to char before the one we just read
            if(val == seek)
                return true;
        }
        return false;
    }
    
    public void generateWrapper(File targetDir, String packageName) {
        wrapFromTableMapping(mainMap, targetDir, packageName);
    }
    
    private void wrapFromTableMapping(TableMapping map, File targetDir, String packageName) {
        if(map == null)
            return;
        
        String className = map.getClassName();
        className = "Managed" + className.substring(className.lastIndexOf(".") + 1);
        //HBMigrate.log(targetDir + "/" + className + ".java");
        
        try {
            File target = new File(targetDir, className + ".java");
            HBMigrate.log("Generating " + target.getAbsolutePath());
            PrintWriter out = new PrintWriter(new FileOutputStream(target));
            
            out.println();
            out.println("/* Generated by HBMigrate */");
            out.println();
            out.println("package " + packageName + ";");
            out.println();
            out.println("import " + map.getClassName() + ";");
            out.println("import javax.persistence.*;");
            out.println();
            out.println("@Entity");
            out.println("@Table(name = \"" + map.getTableName() + "\")");
            out.println("public class " + className + " extends " + map.getClassName().substring(map.getClassName().lastIndexOf(".") + 1) + " {");
            out.println();
            if(map.getId() == null) {
                HBMigrate.log("WARNING: no ID found for '" + className + "'");
                out.println("\t@Id @GeneratedValue int id;");
            } else {
                out.println("\t@Id @GeneratedValue");
                generateColumn(out, map.getId());
            }
            for(Column c : map.getColumns()) 
                generateColumn(out, c);
            for(ManyToOne m : map.getManyToOnes())
                generateMTO(out, m);
            out.println("}");
            
            out.flush();
            out.close();
            
            for(TableMapping tm : map.getSubClasses())
                wrapFromTableMapping(tm, targetDir, packageName);
            
            HBMigrate.log("Done");

        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    private void generateColumn(PrintWriter out, Column c) throws IOException {
            out.println("\t@Column(name = \"" + c.getTableName() + "\")");
            out.println("\t" + c.getType() + " " + c.getJavaName() + ";");
            out.println();        
    }
    
    private void generateMTO(PrintWriter out, ManyToOne m) throws IOException {
        out.println("\t@ManyToOne");
        out.println("\t" + m.getClassName() + " " + m.getJavaName() + ";");
        out.println();
    }
    
}
