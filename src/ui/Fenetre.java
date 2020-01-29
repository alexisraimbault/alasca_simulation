package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.NumberFormatter;

import classes.Operations;
import classes.PlanifiedTask;
import components.Controller;
 
public class Fenetre extends JFrame implements ActionListener{
  private JPanel container = new JPanel();
  private JPanel container1 = new JPanel();
  private JPanel container2 = new JPanel();
  JComboBox c1;
  JFormattedTextField textField;
  JFormattedTextField textField2;
 
  //Compteur de clics
  private int compteur = 0;
  
  private double cpt = 20;
  
  private JLabel label = new JLabel("température : " + cpt);
  
  private Controller controller;
  
  public Fenetre(Controller controller){
	this.controller = controller;
    this.setTitle("Control Panel");
    this.setSize(600, 200);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setLocationRelativeTo(null);
 
    container.setBackground(Color.white);
    container.setLayout(new FlowLayout());
    container1.setLayout(new FlowLayout());
    container2.setLayout(new FlowLayout());
    JButton button1 = new JButton("+");
    JButton button2 = new JButton("-");
    JButton button3 = new JButton("Plan cooking task");
    
    String s1[] = { "COOK HIGH", "COOK LOW"}; 
    
    c1 = new JComboBox(s1); 
    
    NumberFormat format = NumberFormat.getInstance();
    NumberFormatter formatter = new NumberFormatter(format);
    formatter.setValueClass(Integer.class);
    formatter.setMinimum(0);
    formatter.setMaximum(Integer.MAX_VALUE);
    formatter.setAllowsInvalid(false);
    // If you want the value to be committed on each keystroke instead of focus lost
    formatter.setCommitsOnValidEdit(true);
    textField = new JFormattedTextField(formatter);
    textField2 = new JFormattedTextField(formatter);
    
    textField.setColumns(10);
    textField2.setColumns(10);
        
    textField.setValue(0);
	textField2.setValue(0);
          
    Font police = new Font("Tahoma", Font.BOLD, 16);  
    label.setFont(police);  
    label.setForeground(Color.blue);  
    label.setHorizontalAlignment(JLabel.CENTER);
    
    container1.add(button2);
    container1.add(label);
    container1.add(button1);
    container2.add(c1);
    container2.add(textField);
    container2.add(textField2);
    container2.add(button3);
    container.add(container1);
    container.add(container2);
    
    button1.addActionListener(new ActionListener()
    {
    	  public void actionPerformed(ActionEvent e)
    	  {
    		  cpt++;
    		  try {
					controller.setHeaterAimedTemp(cpt);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
    		  label.setText("température : " + cpt);
    	  }
    	});
    
    button2.addActionListener(new ActionListener()
    {
    	  public void actionPerformed(ActionEvent e)
    	  {
    		  cpt--;
    		  try {
	  				controller.setHeaterAimedTemp(cpt);
	  			} catch (Exception e1) {
	  				e1.printStackTrace();
	  			}
    		  label.setText("température : " + cpt);
    	  }
    	});
    
    button3.addActionListener(new ActionListener()
    {
    	  public void actionPerformed(ActionEvent e)
    	  {
    		  if(!textField.getText().equals("") && !textField2.getText().equals("")){
    			  try {
        			  switch(String.valueOf(c1.getSelectedItem()))
        			  {
        			  	case "COOK HIGH":
        			  		controller.addPlanifiedTask(new PlanifiedTask((int)textField.getValue(), (int)textField2.getValue(),Operations.COOK_HIGH ,1));
        			  		break;
        			  	case "COOK LOW":
        			  		controller.addPlanifiedTask(new PlanifiedTask((int)textField.getValue(), (int)textField2.getValue(),Operations.COOK_LOW ,1));
        			  		break;
        			  }
    	  			} catch (Exception e1) {
    	  				e1.printStackTrace();
    	  			}
    		  }
    		  textField.setValue(0);
    		  textField2.setValue(0);
    		  
    	  }
    	});
    
    this.setContentPane(container);
    this.setVisible(true);
  }

@Override
public void actionPerformed(ActionEvent e) {
		
		
	}

}