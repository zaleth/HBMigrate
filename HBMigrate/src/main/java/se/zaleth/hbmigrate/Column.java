/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.zaleth.hbmigrate;

import java.awt.event.*;
import javax.swing.*;

/**
 *
 * @author krister
 */
public class Column extends JPanel implements ActionListener {
    
    private JLabel tableName;
    private JLabel javaName;
    private JTextField type;

    public Column() {
        super();
        tableName = new JLabel();
        this.add(tableName);
        javaName = new JLabel();
        this.add(javaName);
        type = new JTextField();
        this.add(type);
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

    public String getType() {
        return type.getText();
    }

    public void setType(String type) {
        this.type.setText(type);
    }
    
    public void actionPerformed(ActionEvent e) {
        
    }
    
}
