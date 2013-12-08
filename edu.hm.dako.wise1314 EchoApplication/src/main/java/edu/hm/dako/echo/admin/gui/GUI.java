package edu.hm.dako.echo.admin.gui;

import java.awt.EventQueue;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JSplitPane;
import java.awt.Color;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.SystemColor;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.swing.UIManager;

public class GUI {

	private JFrame Admin_Client;
	private JTextField txtEingabeDerClient;
	private JTextField textField;
	private JTextField txtAnzahlDerNachrichten;
	private JTextField textField_1;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI window = new GUI();
					window.Admin_Client.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		Admin_Client = new JFrame();
		Admin_Client.getContentPane().setBackground(new Color(230, 230, 250));
		Admin_Client.getContentPane().setLayout(null);
		
		ImageIcon iconBtnEingabe = new ImageIcon("images\\checkmark.png");
		final JButton btnEingabe = new JButton("", iconBtnEingabe);
		
		btnEingabe.setBounds(345, 40, 79, 28);
		btnEingabe.setEnabled(false);
		Admin_Client.getContentPane().add(btnEingabe);
		
		
		
		KeyAdapter keyadapter = new KeyAdapter(){
			public void keyTyped(KeyEvent event){
				if(event.getSource() instanceof JTextField){
					JTextField txtfld = (JTextField) event.getSource();
					int result = 0;
					try {
						result = Integer.parseInt(txtfld.getText());
						if (result>=0 && result <= 1000000){
							btnEingabe.setEnabled(true);
						
						}
					}catch (Exception ex){
					}
				}
			}
		};
	
		
		JButton btnDeleteAll = new JButton("Daten l\u00F6schen");
		btnDeleteAll.setBounds(120, 192, 200, 44);
		
		Admin_Client.getContentPane().add(btnDeleteAll);
		
		
		txtEingabeDerClient = new JTextField();
		txtEingabeDerClient.setForeground(new Color(0, 0, 128));
		txtEingabeDerClient.setEditable(false);
		txtEingabeDerClient.setFont(new Font("Corbel", Font.BOLD, 20));
		txtEingabeDerClient.setBackground(new Color(230, 230, 250));
		txtEingabeDerClient.setText("Eingabe der Client ID:");
		txtEingabeDerClient.setBounds(10, 40, 224, 31);
		Admin_Client.getContentPane().add(txtEingabeDerClient);
		txtEingabeDerClient.setColumns(10);
		
		textField = new JTextField();
		textField.setBounds(258, 40, 77, 31);
		textField.setColumns(10);
		textField.addKeyListener(keyadapter);
		Admin_Client.getContentPane().add(textField);
		
		
		txtAnzahlDerNachrichten = new JTextField();
		txtAnzahlDerNachrichten.setEditable(false);
		txtAnzahlDerNachrichten.setBackground(new Color(230, 230, 250));
		txtAnzahlDerNachrichten.setForeground(new Color(0, 0, 128));
		txtAnzahlDerNachrichten.setFont(new Font("Corbel", Font.BOLD, 20));
		txtAnzahlDerNachrichten.setText("Anzahl der Nachrichten:");
		txtAnzahlDerNachrichten.setBounds(10, 102, 224, 31);
		txtAnzahlDerNachrichten.setColumns(10);
		Admin_Client.getContentPane().add(txtAnzahlDerNachrichten);
		
		
		textField_1 = new JTextField();
		textField_1.setEditable(false);
		textField_1.setBackground(UIManager.getColor("Button.background"));
		textField_1.setBounds(258, 102, 77, 31);
		textField_1.setColumns(10);
		Admin_Client.getContentPane().add(textField_1);
		textField.setBackground(UIManager.getColor(textField_1));
		
		Admin_Client.setBounds(100, 100, 450, 300);
		Admin_Client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
