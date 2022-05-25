/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.zaleth.hbmigrate;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.*;
import java.awt.event.*;

/**
 *
 * @author krister
 */
public class CustomDialog extends JDialog implements ActionListener {
    
    private JLabel body;
    private JPanel bPanel;
    private int choice;
    
    public CustomDialog(JFrame root) {
        super(root, true); // default to modal (ie blocking) dialog
      JPanel panel = new JPanel(new BorderLayout());
      panel.add(body = new JLabel(), BorderLayout.CENTER);
      panel.add(bPanel = new JPanel(new GridLayout(1, 0)), BorderLayout.SOUTH);
      this.add(panel);
    }
    
    public int displayDialog(String title, String body, String buttons) {
      this.setTitle(title);
      this.body.setText(body);
      bPanel.removeAll();
      String[] text = buttons.split("\\|");
      int c = 0;
      for(String s : text) {
          JButton b = new JButton(s);
          b.setActionCommand("" + c++);
          b.addActionListener(this);
          bPanel.add(b);
      }
      this.pack();
      this.setLocationRelativeTo(this.getParent());
      this.setVisible(true);
      return choice;
    }
    
    public void actionPerformed(ActionEvent e) {
        try {
            choice = Integer.parseInt(e.getActionCommand());
            this.setVisible(false);
        } catch(NumberFormatException ex) {
            
        }
    }

}
