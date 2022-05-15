
import java.io.File;
import javax.swing.*;

public class DirectoryTree extends JPanel {
  
  private File rootDir;
  private JTree treeView;

  public DirectoryTree() {
    this("");
  }

  public DirectoryTree(String root) {
    this(new File(root));
  }
  
  public DirectoryTree(File root) {

  }
}
