/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package se.zaleth.hbmigrate;

import java.io.*;
import java.util.*;

/**
 *
 * @author hp
 */
public class Settings {

    private static final String FILE_NAME = "settings.txt";
    
    private Properties props;
    private boolean dirty;
    
    public Settings() {
        props = new Properties();
        try {
            props.load(new FileInputStream(new File(FILE_NAME)));
        } catch(IOException e) {
            HBMigrate.log("Error loading " + FILE_NAME);
        }
        dirty = false;
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
    
}
