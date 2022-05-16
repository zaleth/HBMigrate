/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.zaleth.hbmigrate;


import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.xml.sax.SAXException;

public class HBMigrate implements ActionListener {

  public static void main(String[] args) {
    new HBMigrate();
  }

  private JFrame root;
  private JFileChooser loadDialog, saveDialog;
  private JLabel fileName, dirName, className;
  private JScrollPane scrollArea;
  private JPanel columns;
  private JTextField pack;
  
  private File srcDir, destDir;
  private Parser parser;
  
  public HBMigrate() {
    root = new JFrame("HBMigrate");
    root.setLocation(100, 100);
    root.setSize(800, 600);
    root.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    root.setLayout(new BorderLayout());
    
    srcDir = new File("");
    destDir = new File("");

    parser = new Parser();
    
    loadDialog = new JFileChooser();
    loadDialog.setFileFilter(new FileNameExtensionFilter("XML files", "xml"));
    loadDialog.setDialogType(JFileChooser.OPEN_DIALOG);
    loadDialog.setDialogTitle("Choose an HBM file to parse");
    
    saveDialog = new JFileChooser();
    saveDialog.setDialogType(JFileChooser.SAVE_DIALOG);
    
    JPanel p = new JPanel(new GridLayout(0, 1));
    
    JPanel ip = new JPanel(new BorderLayout());
    JButton b = new JButton("Load HBM file");
    b.addActionListener(this);
    ip.add(b, BorderLayout.WEST);
    ip.add(fileName = new JLabel("No file selected"), BorderLayout.CENTER);
    b = new JButton("Generate wrapper");
    b.setActionCommand("Wrapper");
    b.addActionListener(this);
    ip.add(b, BorderLayout.EAST);
    p.add(ip);
    
    ip = new JPanel(new BorderLayout());
    b = new JButton("Set target dir");
    b.addActionListener(this);
    ip.add(b, BorderLayout.WEST);
    ip.add(dirName = new JLabel("No directory selected"), BorderLayout.CENTER);
    p.add(ip);
    
    ip = new JPanel(new BorderLayout());
    ip.add(pack = new JTextField("Enter package name"), BorderLayout.CENTER);
    p.add(ip);
    
    ip = new JPanel(new BorderLayout());
    ip.add(new JLabel("Class name: "), BorderLayout.WEST);
    ip.add(className = new JLabel(""), BorderLayout.CENTER);
    b = new JButton("Modify source");
    b.setActionCommand("Modify");
    b.addActionListener(this);
    ip.add(b, BorderLayout.EAST);
    p.add(ip);
    
    root.add(p, BorderLayout.NORTH);
    
    columns = new JPanel(new GridLayout(0, 1));
    scrollArea = new JScrollPane(columns);
    root.add(scrollArea, BorderLayout.CENTER);
    
    root.setVisible(true);
  }

  public void actionPerformed(ActionEvent e) {
      String cmd = e.getActionCommand();
      
      if(cmd.equals("Load HBM file")) {
          loadDialog.setCurrentDirectory(srcDir);
          if(loadDialog.showOpenDialog(root) == JFileChooser.APPROVE_OPTION) {
              try {
                  // save directory
                  srcDir = loadDialog.getCurrentDirectory();
                  fileName.setText(loadDialog.getSelectedFile().getName());
                  parser.loadFile(loadDialog.getSelectedFile());
                  TableMapping map = parser.parse();
                  className.setText(map.getClassName());
                  columns.removeAll();
                  columns.add(map.getId());
                  for(Column c : map.getColumns()) {
                      columns.add(c);
                  }
                  scrollArea.repaint();
              } catch(IOException ex) {
                  // most likely transient error
                  ex.printStackTrace();
              } catch(SAXException ex) {
                  // malformed document
                  ex.printStackTrace();
              }
              
          }
      } else if(cmd.equals("Set target dir")) {
          saveDialog.setCurrentDirectory(destDir);
          saveDialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
          saveDialog.setDialogTitle("Choose directory for generated file");
          if(saveDialog.showOpenDialog(root) == JFileChooser.APPROVE_OPTION) {
              destDir = saveDialog.getCurrentDirectory();
              dirName.setText(saveDialog.getSelectedFile().getAbsolutePath());
          }
      } else if(cmd.equals("Wrapper")) {
          if(destDir != null)
              parser.generateWrapper(new File(dirName.getText()), pack.getText());              
          
      } else if(cmd.equals("Modify")) {
          
      }

  }
  
}