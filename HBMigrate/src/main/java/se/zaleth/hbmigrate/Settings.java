/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package se.zaleth.hbmigrate;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

/**
 *
 * @author hp
 */
public class Settings implements ActionListener {

    private static final String FILE_NAME = "settings.txt";
    private static final String[] dbOptions = {
      "connection.driver_class",
        "connection.url",
        "connection.username",
        "connection.password"
    };
    
    private Properties props;
    private boolean dirty;
    private JDialog dialog;
    private JTextField[] fields;
    
    public Settings() {
        props = new Properties();
        try {
            props.load(new FileInputStream(new File(FILE_NAME)));
        } catch(IOException e) {
            HBMigrate.log("Error loading " + FILE_NAME);
        }
        dirty = false;
        
        dialog = new JDialog();
        dialog.setModal(true);
        dialog.setTitle("Database connection settings");
        dialog.setLayout(new GridLayout(0, 2));
        fields = new JTextField[dbOptions.length];
        for(int i = 0; i < dbOptions.length; i++) {
            dialog.add(new JLabel(dbOptions[i]));
            dialog.add(fields[i] = new JTextField(props.getProperty(dbOptions[i], "")));
        }
        JButton b = new JButton("Cancel");
        b.addActionListener(this);
        dialog.add(b);
        b = new JButton("OK");
        b.addActionListener(this);
        dialog.add(b);
    }
    
    public void openDBOptionsDialog(JFrame parent) {
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }
    
    public String get(String key) {
        return props.getProperty(key);
    }

    public String get(String key, String def) {
        return props.getProperty(key, def);
    }
    
    public String put(String key, String value) {
        dirty = true;
        return (String) props.setProperty(key, value);
    }

    public String remove(String key) {
        dirty = true;
        return (String) props.remove(key);
    }

    public void putAll(Map<? extends String, ? extends String> m) {
        props.putAll(m);
    }

    public void save() {
        if(dirty) {
            try {
                props.store(new FileOutputStream(new File(FILE_NAME)), FILE_NAME);
                dirty = false;
            } catch(IOException e) {
                HBMigrate.log("Error saving " + FILE_NAME + ": " + e.getMessage());
            }
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        dialog.setVisible(false);
        if(e.getActionCommand().equals("OK")) {
            dirty = true;
            for(int i = 0; i < dbOptions.length; i++) {
                props.setProperty(dbOptions[i], fields[i].getText());
            }
        }
    }
}
