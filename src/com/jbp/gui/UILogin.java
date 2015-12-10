package com.jbp.gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import com.jbp.utils.*;
import com.jbp.facade.*;
import com.jbp.main.ClientType;
import com.jbp.main.CouponSystemJFrame;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
//
// This class provides a GUI for login requests. it passes it to the CouponSystem 
// for authentication, and receive the relevant Facade upon successful login.
//
public class UILogin extends JFrame implements ActionListener{

	// reference for the CouponSystem, to send the login request
	private CouponSystemJFrame cs;

	private JPanel contentPane;
	private JTextField tfUsername;
	private JPasswordField tfPassword;
	private JRadioButton rbAdmin, rbCompany, rbCustomer;
	private JButton btnLogin;

	public UILogin(CouponSystemJFrame cs) {
		this.cs = cs;
		Utils.logMessage(this, Severity.DEBUG, "UI Login window created.");
		setTitle("Login : " + this.cs.getSystemName());
		setResizable(false);
		// closing the login should NOT close the application
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 426, 211);
		contentPane = new JPanel();
		contentPane.setLayout(null);
		setContentPane(contentPane);
		
		tfUsername = new JTextField();
		tfUsername.setBounds(26, 132, 86, 20);
		contentPane.add(tfUsername);
		tfUsername.setColumns(10);
		
		tfPassword = new JPasswordField();
		tfPassword.setBounds(122, 132, 86, 20);
		contentPane.add(tfPassword);
		
		// setting the TextField to "Admin" when RadioButton is choosen
		rbAdmin = new JRadioButton("Admin");
		rbAdmin.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (rbAdmin.isSelected()){
					tfUsername.setText("Admin");
					tfUsername.setEditable(false);
				} else {
					tfUsername.setText("");
					tfUsername.setEditable(true);
				}
			}
		});
		rbAdmin.setBounds(26, 30, 109, 23);
		contentPane.add(rbAdmin);
		
		rbCompany = new JRadioButton("Company");
		rbCompany.setBounds(26, 56, 109, 23);
		contentPane.add(rbCompany);
		
		rbCustomer = new JRadioButton("Customer");
		rbCustomer.setSelected(true);
		rbCustomer.setBounds(26, 82, 109, 23);
		contentPane.add(rbCustomer);
		
		ButtonGroup rbGrpUserTypes = new ButtonGroup();
		rbGrpUserTypes.add(rbAdmin);
		rbGrpUserTypes.add(rbCompany);
		rbGrpUserTypes.add(rbCustomer);

		JLabel lblUsername = new JLabel("Username :");
		lblUsername.setBounds(26, 118, 86, 14);
		contentPane.add(lblUsername);
		
		JLabel lblPassword = new JLabel("Password :");
		lblPassword.setBounds(122, 118, 86, 14);
		contentPane.add(lblPassword);
		
		btnLogin = new JButton("Login");
		btnLogin.addActionListener(this);
		btnLogin.setBounds(238, 131, 150, 23);
		contentPane.add(btnLogin);
	}

	@Override
	// triggered upon pressing on "login"
	public void actionPerformed(ActionEvent e) {
		// Admin login requested
		if (rbAdmin.isSelected()){
			// safe to downcast, only AdminFacade may return 
			AdminFacade adminFacade = (AdminFacade)cs.login("Admin", tfPassword.getText(), ClientType.ADMIN);
			// if auth success - AdminFacade returned
			if (adminFacade != null){
				// open admin window refering to this facade of admin
				AdminWindow adminFrame = new AdminWindow(adminFacade);
				adminFrame.setVisible(true);
				// and hide this window
				this.setVisible(false);
			}else{
				JOptionPane.showMessageDialog(new JFrame(), "authentication failure for admin.", "Dialog", JOptionPane.ERROR_MESSAGE);
				Utils.logMessage(this, Severity.INFO, "authentication failure for admin.");
			}
		}
		// Company login requested
		if (rbCompany.isSelected()){
			CompanyFacade companyFacade = (CompanyFacade) cs.login(tfUsername.getText(), tfPassword.getText(), ClientType.COMPANY);
			// if auth success - CustomerFacade returned
			if (companyFacade != null){
				// and passed to the Customer window
				CompanyWindow companyWindow= new CompanyWindow(companyFacade);
				companyWindow.setVisible(true);
				this.setVisible(false);
			}else{
				JOptionPane.showMessageDialog(new JFrame(), "authentication failure for company : " + tfUsername.getText(), "Dialog", JOptionPane.ERROR_MESSAGE);
				Utils.logMessage(this, Severity.INFO, "authentication failure for company : " + tfUsername.getText());
			}
		}
		// Customer login requested
		if (rbCustomer.isSelected()){
			// safe to downcast, only CustomerFacade may return 
			CustomerFacade customerFacade = (CustomerFacade) cs.login(tfUsername.getText(), tfPassword.getText(), ClientType.CUSTOMER);
			// if auth success - CustomerFacade returned
			if (customerFacade != null){
				// and passed to the Customer window
				CustomerWindow customerWindow= new CustomerWindow(customerFacade);
				customerWindow.setVisible(true);
				this.setVisible(false);
			}else{
				JOptionPane.showMessageDialog(new JFrame(), "authentication failure for customer : " + tfUsername.getText(), "Dialog", JOptionPane.ERROR_MESSAGE);
				Utils.logMessage(this, Severity.INFO, "authentication failure for customer : " + tfUsername.getText());
			}
		}
	}
}
