
import java.io.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class HBMigrate implements ActionListener {

  public static void main(String[] args) {
    new HBMigrate();
  }

  private JFrame root;
  private JFileChooser loadDialog;
  private File srcDir, destDir;

  public HBMigrate() {
    root = new JFrame("HBMigrate");
    root.setLocation(100, 100);
    root.setSize(800, 600);
    root.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    srcDir = new File("");
    destDir = new File("");

    loadDialog = new JFileChooser();
    loadDialog.setFileFilter(new FileNameExtensionFilter("XML files", "xml"));
    loadDialog.setDialogType(JFileChooser.OPEN_DIALOG);
    loadDialog.setDialogTitle("Choose an HBM file to parse");
    
    root.setVisible(true);
  }

  public void actionPerformed(ActionEvent e) {

  }
  
}