package com.jbp.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import com.jbp.beans.Customer;
import com.jbp.exceptions.CustomerCreationException;
import com.jbp.exceptions.CustomerNotFoundException;
import com.jbp.exceptions.CustomerRemovalException;
import com.jbp.exceptions.CustomerUpdateException;
import com.jbp.facade.AdminFacade;

//
// JPanel for AdminWindow JFrame.
// it provides administration operations on Customers
//
public class AdminCustomersPanel extends JPanel implements ActionListener {

	private AdminFacade af;
	private JTextField tfCustName;
	private JPasswordField tfCustPassword;
	private JButton btnCreateCustomer;
	private JButton btnRemoveCustomer;
	private JList<String> listOfCustomers;
	private DefaultListModel<String> customersListModel;
	private JScrollPane scrollPane;
	private JButton btnLoadCustomer;
	private JButton btnUpdateCustomer;
	private JLabel lbCustId;

	// refreshing the list after operations
	public void refreshCustomersJList(){
		customersListModel.removeAllElements();
		Collection<Customer> allCustomers = af.getAllCustomers();
		for (Customer customer: allCustomers) {
			customersListModel.addElement(customer.getCustName());
		}
	}
	
	// c'tor refers to the Admin Facade
	public AdminCustomersPanel(AdminFacade af) {
		this.af = af;

		setLayout(null);
		setBounds(10, 45, 229, 384);
		
		JLabel lblAddCustomer = new JLabel("Create New Customer");
		lblAddCustomer.setBounds(10, 11, 139, 14);
		add(lblAddCustomer);
		
		tfCustName = new JTextField();
		tfCustName.setBounds(10, 52, 210, 20);
		tfCustName.setColumns(10);
		add(tfCustName);
		
		btnCreateCustomer = new JButton("Create");
		btnCreateCustomer.addActionListener(this);
		btnCreateCustomer.setBounds(10, 122, 91, 23);
		add(btnCreateCustomer);
		
		btnRemoveCustomer = new JButton("Remove");
		btnRemoveCustomer.addActionListener(this);
		btnRemoveCustomer.setBounds(10, 350, 91, 23);
		add(btnRemoveCustomer);
		
		JLabel lblRemoveOrLoadCustomer = new JLabel("Remove / Load Customer :");
		lblRemoveOrLoadCustomer.setBounds(10, 156, 210, 14);
		add(lblRemoveOrLoadCustomer);
		
		JLabel lblName = new JLabel("Name :");
		lblName.setBounds(10, 36, 139, 14);
		add(lblName);
		
		tfCustPassword = new JPasswordField();
		tfCustPassword.setColumns(10);
		tfCustPassword.setBounds(10, 91, 210, 20);
		// admin can see password as ToolTip
		tfCustPassword.setToolTipText(tfCustPassword.getText());
		add(tfCustPassword);
		
		JLabel lblPassword = new JLabel("Password :");
		lblPassword.setBounds(10, 75, 139, 14);
		add(lblPassword);
		customersListModel = new DefaultListModel<String>();
		
		scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(10, 172, 210, 167);
		add(scrollPane);
		
		 listOfCustomers = new JList<String>();
		 scrollPane.setViewportView(listOfCustomers);
		listOfCustomers.setModel(customersListModel);
		
		btnLoadCustomer = new JButton("Load");
		btnLoadCustomer.addActionListener(this);
		btnLoadCustomer.setBounds(129, 350, 91, 23);
		add(btnLoadCustomer);
		
		btnUpdateCustomer = new JButton("Update");
		btnUpdateCustomer.setBounds(129, 122, 91, 23);
		btnUpdateCustomer.addActionListener(this);
		add(btnUpdateCustomer);
		
		lbCustId = new JLabel("x");
		lbCustId.setBounds(141, 36, 79, 14);
		add(lbCustId);
		
		refreshCustomersJList();

	}

	@Override
	public void actionPerformed(ActionEvent e)  {
		JButton btnClicked = (JButton)e.getSource();
		if (btnClicked == btnCreateCustomer){
			// constructing a customer object to pass to AdminFacade
			Customer customer = new Customer();
			try {
				// name and password must be entered.
				// empty fields will invoke a notice to to client and will stop the creation process
				if (!tfCustName.getText().equals("")) {
					customer.setCustName(tfCustName.getText());
					if (!tfCustPassword.getText().equals("")) {
						customer.setPassword(tfCustPassword.getText());
						af.createCustomer(customer);
						JOptionPane.showMessageDialog(new JFrame(), "Customer "+ customer.getCustName()+" created successfully", "Dialog", JOptionPane.INFORMATION_MESSAGE);
						refreshCustomersJList();
						}
					else JOptionPane.showMessageDialog(new JFrame(), "must choose password !", "Notice!", JOptionPane.ERROR_MESSAGE);
					}
				else JOptionPane.showMessageDialog(new JFrame(), "must choose customer name !", "Notice!", JOptionPane.ERROR_MESSAGE);
				}
				
			catch (CustomerCreationException e1) {
				JOptionPane.showMessageDialog(new JFrame(), e1.getMessage()+"\nCustomer already exists !", "Dialog", JOptionPane.ERROR_MESSAGE);
			}
		}
		if (btnClicked == btnRemoveCustomer){
			// get customer name from list
			String customerNameToRemove = listOfCustomers.getSelectedValue();
			int answer = JOptionPane.showConfirmDialog(new JFrame(), "Remove customer " + customerNameToRemove +",  are you sure ?)", 
																																					"Confirm remove customer", JOptionPane.ERROR_MESSAGE);
				if(answer == JOptionPane.YES_OPTION){
					try {
						// preparing an object with ID to send to DAO - DAO needs only ID for removal
						Customer customerToRemove= af.getCustomer(customerNameToRemove);
						af.removeCustomer(customerToRemove);
						refreshCustomersJList();
						} catch (CustomerRemovalException | CustomerNotFoundException e1) {
							JOptionPane.showMessageDialog(new JFrame(), e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					}
				// do nothing if not confirmed
			}else if (answer == JOptionPane.NO_OPTION) {}
		}
		
		if (btnClicked == btnUpdateCustomer){
			// ask for confirmation
			int answer = JOptionPane.showConfirmDialog(new JFrame(), "This will update  the customer  " + tfCustName.getText() + " with : "
					+"\nPassword : " + tfCustPassword.getText() + " "
					+ "\nAre you sure ?", "Confirm updating customer", JOptionPane.ERROR_MESSAGE);
			if(answer == JOptionPane.YES_OPTION){
				try {
					Customer customerToUpdate = new Customer();
					customerToUpdate.setCustName(tfCustName.getText());
					customerToUpdate.setPassword(tfCustPassword.getText());

					af.updateCustomer(customerToUpdate);
					refreshCustomersJList();
				} catch (CustomerUpdateException e1) {
					JOptionPane.showMessageDialog(new JFrame(), e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
				// do nothing if not confirmed
			}else if (answer == JOptionPane.NO_OPTION) {}
		}
			if (btnClicked == btnLoadCustomer){
				// get customer name from list
				String customerNameToLoad = listOfCustomers.getSelectedValue();
				try {
					// preparing an object with ID to send to DAO - DAO needs only ID for removal
					// ID can be retrieved using getCompanyId
					Customer customerToLoad= af.getCustomer(customerNameToLoad);
					tfCustName.setText(customerToLoad.getCustName());
					tfCustPassword.setText(customerToLoad.getPassword());
					lbCustId.setText("Id : " + customerToLoad.getId());
					
					// show password as hint for admin user only
					refreshCustomersJList();
					} catch (CustomerNotFoundException e1) {
						JOptionPane.showMessageDialog(new JFrame(), e1.getMessage() + customerNameToLoad + ".", "Error", JOptionPane.ERROR_MESSAGE);
				}
		}
	}


}
