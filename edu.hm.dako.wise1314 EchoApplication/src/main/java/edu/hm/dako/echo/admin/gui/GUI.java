package edu.hm.dako.echo.admin.gui;

import java.awt.EventQueue;

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
import java.awt.SystemColor;
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
		
		JButton btnDeleteAll = new JButton("Daten l\u00F6schen");
		btnDeleteAll.setBounds(176, 192, 100, 44);
		
		Admin_Client.getContentPane().add(btnDeleteAll);
		
		txtEingabeDerClient = new JTextField();
		txtEingabeDerClient.setForeground(new Color(0, 0, 128));
		txtEingabeDerClient.setEditable(false);
		txtEingabeDerClient.setFont(new Font("Corbel", Font.BOLD, 17));
		txtEingabeDerClient.setBackground(new Color(230, 230, 250));
		txtEingabeDerClient.setText("Eingabe der Client ID:");
		txtEingabeDerClient.setBounds(30, 30, 167, 20);
		Admin_Client.getContentPane().add(txtEingabeDerClient);
		txtEingabeDerClient.setColumns(10);
		
		textField = new JTextField();
		textField.setEditable(false);
		textField.setBounds(244, 30, 46, 20);
		Admin_Client.getContentPane().add(textField);
		textField.setColumns(10);
		
		txtAnzahlDerNachrichten = new JTextField();
		txtAnzahlDerNachrichten.setBackground(new Color(230, 230, 250));
		txtAnzahlDerNachrichten.setForeground(new Color(0, 0, 128));
		txtAnzahlDerNachrichten.setFont(new Font("Corbel", Font.BOLD, 17));
		txtAnzahlDerNachrichten.setText("Anzahl der Nachrichten:");
		txtAnzahlDerNachrichten.setBounds(30, 87, 187, 20);
		Admin_Client.getContentPane().add(txtAnzahlDerNachrichten);
		txtAnzahlDerNachrichten.setColumns(10);
		
		JButton btnNewButton = new JButton("New button");
		btnNewButton.setBounds(300, 29, 37, 23);
		Admin_Client.getContentPane().add(btnNewButton);
		
		textField_1 = new JTextField();
		textField_1.setBackground(UIManager.getColor("Button.background"));
		textField_1.setBounds(244, 87, 46, 20);
		Admin_Client.getContentPane().add(textField_1);
		textField_1.setColumns(10);
		Admin_Client.setBounds(100, 100, 450, 300);
		
		Admin_Client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
