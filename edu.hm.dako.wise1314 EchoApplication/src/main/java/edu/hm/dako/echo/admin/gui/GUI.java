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

import edu.hm.dako.echo.admin.service.AdminService;
import edu.hm.dako.echo.admin.service.impl.AdminServiceMock;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;

public class GUI {

	private JFrame Admin_Client;
	private JTextField txtEingabeDerClient;
	private JTextField textField;
	private JTextField txtAnzahlDerNachrichten;
	private JTextField textField_1;
	private AdminService adminService;
	private JLabel lblLöschstatus;
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
		
		adminService = new AdminServiceMock();
		
		/*
		 * Fensterrahmen
		 */
		Admin_Client = new JFrame("Admin Service");
		Admin_Client.getContentPane().setForeground(new Color(0, 0, 128));
		
		Admin_Client.getContentPane().setBackground(new Color(230, 230, 250));
		Admin_Client.getContentPane().setLayout(null);
		
		
		/*
		 * Anlegen des Icons für den Button btnEingabe
		 */
		ImageIcon iconBtnEingabe = new ImageIcon("images\\checkmark.png");
		
		/*
		 * Erstellen eines Buttons btnEingabe, der die Eingegebene ID aus dem textField ausliest 
		 */
		final JButton btnEingabe = new JButton("", iconBtnEingabe);
		btnEingabe.setBounds(345, 40, 49, 28);
		btnEingabe.setEnabled(false);
		Admin_Client.getContentPane().add(btnEingabe);
		btnEingabe.addMouseListener(new MouseAdapter() {
			// TODO
			@Override
			public void mouseClicked(MouseEvent arg0) {
				int number = adminService.getNumberOfMessages(textField.getText());
				textField_1.setText(String.valueOf(number));
			}
			
		});
		
		
	
		/*
		 * Button welcher alle Daten aus der Datenbank löschen soll
		 */
		JButton btnDeleteAll = new JButton("Daten l\u00F6schen");
		
		btnDeleteAll.setForeground(new Color(0, 0, 128));
		btnDeleteAll.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnDeleteAll.setBounds(120, 192, 200, 44);
		
		btnDeleteAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				boolean deleted;
				deleted = adminService.deleteAllData();
				if (deleted == true){
					lblLöschstatus.setForeground(Color.green);
					lblLöschstatus.setText("Alle Daten wurden erfolgreich gelöscht!");
					lblLöschstatus.setFont(new Font("Corbel", Font.BOLD, 20));
					
				}
				else{
					lblLöschstatus.setForeground(Color.RED);
					lblLöschstatus.setText("Daten konnten nicht gelöscht werden!");
					lblLöschstatus.setFont(new Font("Corbel", Font.BOLD, 20));
				}
			}
		});
		
		Admin_Client.getContentPane().add(btnDeleteAll);
		
		
		
		/*
		 * Textfeld welches zur Eingabe einer Client ID auffordert
		 */
		txtEingabeDerClient = new JTextField();
		
		txtEingabeDerClient.setForeground(new Color(0, 0, 128));
		txtEingabeDerClient.setEditable(false);
		txtEingabeDerClient.setFont(new Font("Corbel", Font.BOLD, 20));
		txtEingabeDerClient.setBackground(new Color(230, 230, 250));
		txtEingabeDerClient.setText("Eingabe der Client ID:");
		txtEingabeDerClient.setBounds(24, 40, 224, 31);
		txtEingabeDerClient.setBorder(null);
		Admin_Client.getContentPane().add(txtEingabeDerClient);
		txtEingabeDerClient.setColumns(10);
		
		/*
		 * Keyadapter der bei Eingabe einer Zahl von 1-1000000 in textField den Button btnEingabe aktiviert
		 */
		KeyAdapter keyadapter = new KeyAdapter(){
			public void keyTyped(KeyEvent event){
				if(event.getSource() instanceof JTextField){
					JTextField txtfld = (JTextField) event.getSource();
					int result = 0;
					try {
						String txtContent = txtfld.getText();
						result = (txtContent.length());
						if (result >= 0){
							btnEingabe.setEnabled(true);
						
						}
					}catch (Exception ex){
					}
				}
			}
		};
		
		
		
		
		/*
		 * Textfeld in welches eine Client ID eingegeben wird
		 */
		textField = new JTextField();
		textField.setBorder(null);
		textField.setForeground(SystemColor.windowBorder);
		textField.setFont(new Font("Arial", Font.BOLD, 20));
		textField.setBounds(258, 40, 77, 28);
		textField.setColumns(10);
		textField.addKeyListener(keyadapter);
		Admin_Client.getContentPane().add(textField);
		
		
		
		
		
		txtAnzahlDerNachrichten = new JTextField();
		txtAnzahlDerNachrichten.setEditable(false);
		txtAnzahlDerNachrichten.setBackground(new Color(230, 230, 250));
		txtAnzahlDerNachrichten.setForeground(new Color(0, 0, 128));
		txtAnzahlDerNachrichten.setFont(new Font("Corbel", Font.BOLD, 20));
		txtAnzahlDerNachrichten.setText("Anzahl der Nachrichten:");
		txtAnzahlDerNachrichten.setBounds(24, 101, 224, 31);
		txtAnzahlDerNachrichten.setColumns(10);
		txtAnzahlDerNachrichten.setBorder(null);
		Admin_Client.getContentPane().add(txtAnzahlDerNachrichten);
		
		
		textField_1 = new JTextField();
		textField_1.setForeground(SystemColor.windowBorder);
		textField_1.setFont(new Font("Arial", Font.BOLD, 20));
		textField_1.setEditable(false);
		textField_1.setBackground(new Color(230, 230, 250));
		textField_1.setBounds(258, 102, 77, 28);
		textField_1.setColumns(10);
		textField_1.setBorder(null);
		Admin_Client.getContentPane().add(textField_1);
		textField.setBackground(SystemColor.menu);
		
		lblLöschstatus = new JLabel("");
		lblLöschstatus.setBounds(34, 143, 400, 28);
		Admin_Client.getContentPane().add(lblLöschstatus);
		
		Admin_Client.setBounds(100, 100, 450, 300);
		Admin_Client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
