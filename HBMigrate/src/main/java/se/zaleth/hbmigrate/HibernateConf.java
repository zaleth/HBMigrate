/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.zaleth.hbmigrate;

import java.sql.*;
import java.util.*;
import org.hibernate.*;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

/**
 *
 * @author krister
 */
public class HibernateConf {

    private static HibernateConf single;
    
    public static String getJavaType(String table, String column) {
        if(single == null)
            return "";
        return single.getType(table, column);
    }
    
    private SessionFactory factory;
    private Properties props;
    private HashMap<String,HashMap<String,String>> cache;
    
    public HibernateConf(Settings settings) {
        props = new Properties();
        props.setProperty("connection.driver_class", settings.get("connection.driver_class"));
        props.setProperty("connection.url", settings.get("connection.url"));
        props.setProperty("connection.username", settings.get("connection.username"));
        props.setProperty("connection.password", settings.get("connection.password"));
        factory = null;
        single = this;
        cache = new HashMap<>();
    }

    public void printDBInfo() {
            try {
                Connection conn = DriverManager.getConnection(props.getProperty("connection.url"),
                    props.getProperty("connection.username"), props.getProperty("connection.password"));
                System.out.println("Connected to DB " + conn.getCatalog() + " " + conn.getSchema());
                DatabaseMetaData md = conn.getMetaData();
                ResultSet rs;
                /*rs = md.getSchemas();
                while(rs.next())
                    System.out.println(rs.getString("TABLE_SCHEM"));
                System.out.println("Schemas done");
                conn.setSchema("RAWMATSTAT_DEV");
                rs = md.getTables(conn.getCatalog(), conn.getSchema(), "%", null);
                while(rs.next())
                    System.out.println(rs.getString("TABLE_NAME"));
                System.out.println("Tables done");*/
                rs = md.getColumns(conn.getCatalog(), conn.getSchema(), "R050_CHARGE", "%");
                while(rs.next()) {
                    System.out.println(rs.getString("COLUMN_NAME") + ": " + 
                            rs.getString("TYPE_NAME") + " (" + rs.getString("DATA_TYPE") + ")");
                }
                System.out.println("Done with tables");
            } catch(SQLException e) {
                e.printStackTrace();
            }        
    }
    
    public String getType(String table, String column) {
        HashMap<String,String> tMap = cache.get(table);
        if(tMap == null) {
            try {
                Connection conn = DriverManager.getConnection(props.getProperty("connection.url"),
                    props.getProperty("connection.username"), props.getProperty("connection.password"));
                DatabaseMetaData md = conn.getMetaData();
                ResultSet rs = md.getColumns(conn.getCatalog(), conn.getSchema(), table, "%");
                tMap = new HashMap<>();
                while(rs.next()) {
                    tMap.put(rs.getString("COLUMN_NAME"), rs.getString("TYPE_NAME"));
                }
                cache.put(table, tMap);
            } catch(SQLException e) {
                e.printStackTrace();
                return "";
            }
        }
        return tMap.get(column);
    }
}
