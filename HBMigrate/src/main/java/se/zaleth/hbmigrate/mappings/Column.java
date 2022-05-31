/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.zaleth.hbmigrate.mappings;

import java.awt.event.*;
import javax.swing.*;
import org.w3c.dom.Element;

/**
 *
 * @author krister
 */
public class Column extends Mapping {
    
    private JLabel tableName;
    private JLabel javaName;
    private JPanel panel;

    public Column(Element e) {
        super(e);
        panel = new JPanel();
        tableName = new JLabel(super.getTableName());
        panel.add(tableName);
        javaName = new JLabel(super.getJavaName());
        panel.add(javaName);
        mapType = Mapping.COLUMN_MAPPING;
    }
    
    public String getAnnotations() {
        return "@Column(name=\"" + tableName.getText() + "\")";
    }
    
    public JPanel getPanel() {
        return panel;
    }
    
    public String getTableName() {
        return tableName.getText();
    }

    public void setTableName(String tableName) {
        this.tableName.setText(tableName);
    }

    public String getJavaName() {
        return javaName.getText();
    }

    public void setJavaName(String javaName) {
        this.javaName.setText(javaName);
    }

}
