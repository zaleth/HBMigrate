/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.zaleth.hbmigrate;

import java.util.Vector;

/**
 *
 * @author krister
 */
public class TableMapping {
    
    private String className;
    private String tableName;
    private String extendsName;

    private IdColumn id;
    private Vector<Column> columns;
    private Vector<ManyToOne> manyToOnes;
    private Vector<TableMapping> subClasses;

    public TableMapping() {
        columns = new Vector<Column>();
        manyToOnes = new Vector<ManyToOne>();
        subClasses = new Vector<TableMapping>();
    }

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
    
    public Vector<Column> getColumns() {
        return columns;
    }

    public void addManyToOne(ManyToOne m) {
        manyToOnes.add(m);
    }
    
    public Vector<ManyToOne> getManyToOnes() {
        return manyToOnes;
    }

    public void addSubClass(TableMapping tm) {
        subClasses.add(tm);
    }
    
    public Vector<TableMapping> getSubClasses() {
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
    
}
