/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.zaleth.hbmigrate;


import se.zaleth.hbmigrate.mappings.TableMapping;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.io.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.xml.sax.SAXException;
import se.zaleth.hbmigrate.mappings.Mapping;

public class HBMigrate implements ActionListener {

    private class HBMFileFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return name.endsWith(".hbm.xml");
        }
    }
    
    private static HBMigrate running = null;
    private static PrintWriter logFile = null;
    
  public static void main(String[] args) {
      try {
          logFile = new PrintWriter(new FileOutputStream("runlog.txt"));
      } catch(IOException e) {
          log(e.getMessage());
      }
    running = new HBMigrate();
  }

  public static void log(String msg) {
      StackTraceElement e = Thread.currentThread().getStackTrace()[2];
      String caller = "From " + e.getClassName() + 
              "." + e.getMethodName() + " line " +
              e.getLineNumber() + "(): ";
      System.out.print(caller);
      System.out.println(msg);
      if(running != null) {
          //running.textOut.append(caller);
          running.textOut.append(msg);
          running.textOut.append("\n");
      }
      if(logFile != null) {
          logFile.println(msg);
      }
  }
  
  private HBMFileFilter hbmFilter;
  private JFrame root;
  private CustomDialog dialog;
  private JFileChooser loadDialog, saveDialog;
  private JLabel fileName, dirName, className;
  private JScrollPane scrollArea, logArea;
  private JPanel columns;
  private JTextField pack;
  private JTextArea textOut;
  
  private File srcDir, destDir;
  private Parser parser;
  private Settings settings;
  private HibernateConf conf;
  
  public HBMigrate() {
      settings = new Settings();
      hbmFilter = new HBMFileFilter();
      
    root = new JFrame("HBMigrate");
    root.setLocation(Integer.parseInt(settings.get("window.left", "100")),
            Integer.parseInt(settings.get("window.top", "100")));
    root.setSize(Integer.parseInt(settings.get("window.width", "800")),
            Integer.parseInt(settings.get("window.height", "600")));
    root.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    root.setLayout(new BorderLayout());
    root.addWindowListener(new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent e) {
            settings.save();
            logFile.flush();
        }
    });
    root.addComponentListener(new ComponentAdapter() {
        @Override
        public void componentResized(ComponentEvent componentEvent) {
            Rectangle r = root.getBounds();
            settings.put("window.left", "" + r.x);
            settings.put("window.top", "" + r.y);
            settings.put("window.width", "" + r.width);
            settings.put("window.height", "" + r.height);
        }
    });
    
    srcDir = new File(settings.get("sourceDir", ""));
    destDir = new File(settings.get("destinationDir", ""));
    parser = new Parser();
    
    dialog = new CustomDialog(root);
    
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
    ip.add(dirName = new JLabel(settings.get("destinationDir", "No target directory selected")), BorderLayout.CENTER);
    b = new JButton("Batch convert");
    b.setActionCommand("Batch");
    b.addActionListener(this);
    ip.add(b, BorderLayout.EAST);
    p.add(ip);
    
    ip = new JPanel(new BorderLayout());
    ip.add(new JLabel("Package: "), BorderLayout.WEST);
    ip.add(pack = new JTextField("Enter package name"), BorderLayout.CENTER);
    b = new JButton("Convert file");
    b.setActionCommand("Convert");
    b.addActionListener(this);
    ip.add(b, BorderLayout.EAST);
    p.add(ip);
    
    ip = new JPanel(new BorderLayout());
    ip.add(new JLabel(settings.get("sourceDir", "No source directory selected")), BorderLayout.WEST);
    ip.add(className = new JLabel("No class"), BorderLayout.CENTER);
    b = new JButton("Modify source");
    b.setActionCommand("Modify");
    b.addActionListener(this);
    ip.add(b, BorderLayout.EAST);
    p.add(ip);
    
    root.add(p, BorderLayout.NORTH);
    
    columns = new JPanel();
    columns.setLayout(new BoxLayout(columns, BoxLayout.PAGE_AXIS));
    scrollArea = new JScrollPane(columns);
    root.add(scrollArea, BorderLayout.CENTER);
    
    textOut = new JTextArea();
    textOut.setEditable(false);
    logArea = new JScrollPane(textOut);
    root.add(logArea, BorderLayout.SOUTH);
    
    root.setVisible(true);

    boolean confDone = false;
    while(!confDone)
        try {
            conf = new HibernateConf(settings);
            //conf.printDBInfo();
            confDone = true;
        } catch(NullPointerException e) {
            settings.openDBOptionsDialog(root);
        }
  }

  public void batchConvert() {
      File[] files = srcDir.listFiles(hbmFilter);
      for(File file : files) {
          convertFile(file);
      }
      log("All files converted!");
  }
  
  public void convertFile(File src) {
    try {
        log("Source file: " + src.getAbsolutePath());
        parser.loadFile(src);
        //parser.traverse();
        TableMapping map = parser.parse();
        File targetDir = new File(destDir, map.getPackName().replace(".", File.separator));
        File target = new File(targetDir, map.getClassName() + ".java");
        log("Target file: " + target.getAbsolutePath());
    } catch(IOException ex) {
        // most likely transient error
        ex.printStackTrace();
    } catch(SAXException ex) {
        // malformed document
        ex.printStackTrace();
    }

  }
  
  public void actionPerformed(ActionEvent e) {
      String cmd = e.getActionCommand();
      
      if(cmd.equals("Load HBM file")) {
          loadDialog.setCurrentDirectory(srcDir);
          if(loadDialog.showOpenDialog(root) == JFileChooser.APPROVE_OPTION) {
              try {
                  // save directory
                  srcDir = loadDialog.getCurrentDirectory();
                  settings.put("sourceDir", srcDir.getAbsolutePath());
                  fileName.setText(loadDialog.getSelectedFile().getName());
                  parser.loadFile(loadDialog.getSelectedFile());
                  TableMapping map = parser.parse();
                  pack.setText(map.getPackName());
                  className.setText(File.separator + map.getClassName() + ".java");
                  scrollArea.invalidate();
                  columns.removeAll();
                  for(Mapping m : map.getMappings()) {
                      columns.add(m.getDisplayElement());
                  }
                  scrollArea.revalidate();
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
              destDir = saveDialog.getSelectedFile();
              settings.put("destinationDir", destDir.getAbsolutePath());
              dirName.setText(saveDialog.getSelectedFile().getAbsolutePath());
          }
      } else if(cmd.equals("Wrapper")) {
          if(destDir != null)
              parser.generateWrapper(new File(dirName.getText()), pack.getText());
      } else if(cmd.equals("Modify")) {
          if(destDir != null)
              parser.modifyFile(new File(dirName.getText()), pack.getText());
      } else if(cmd.equals("Convert")) {
          if(loadDialog.getSelectedFile() != null)
              convertFile(loadDialog.getSelectedFile());
          else
              dialog.displayDialog("Error", "No source file has been selected", "OK");
      } else if(cmd.equals("Batch")) {
          if(srcDir.listFiles(hbmFilter).length > 0) {
            batchConvert();              
          } else {
              dialog.displayDialog("Error", "Directory '" + srcDir.getAbsolutePath()
                      + "' does not contain any HBM files", "OK");
          }
      }

  }
  
}
