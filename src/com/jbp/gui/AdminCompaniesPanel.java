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
import com.jbp.beans.Company;
import com.jbp.exceptions.CompanyCreationException;
import com.jbp.exceptions.CompanyNotFoundException;
import com.jbp.exceptions.CompanyRemovalException;
import com.jbp.exceptions.CompanyUpdateException;
import com.jbp.facade.AdminFacade;

//
// JPanel for AdminWindow JFrame.
// it provides administration operations on Companies
//
public class AdminCompaniesPanel extends JPanel implements ActionListener {

	private Collection<Company> colCompanies;
	private AdminFacade af;
	private JTextField tfCompName;
	private JPasswordField tfCompPass;
	private JTextField tfCompEmail;
	private JButton btnCreateCompany;
	private JButton btnRemoveCompany;
	private JList<String> listOfCompanies;
	private DefaultListModel<String> model;
	private JScrollPane scrollPane;
	private JButton btnLoadCompany;
	private JButton btnUpdateCompany;
	private JLabel lblId;

	// refreshing the list after operations
	public void refreshCompaniesJList(){
		model.removeAllElements();
		Collection<Company> allCompanies = af.getAllCompanies();
		for (Company company: allCompanies) {
			model.addElement(company.getCompName());
		}
	}
	
	// c'tor refers to the Admin Facade
	public AdminCompaniesPanel(AdminFacade af) {
		this.af = af;

		setLayout(null);
		setBounds(10, 45, 229, 384);
		JLabel lblAddCompany = new JLabel("Create New Company");
		lblAddCompany.setBounds(10, 11, 139, 14);
		add(lblAddCompany);
		
		tfCompName = new JTextField();
		tfCompName.setBounds(10, 52, 210, 20);
		tfCompName.setColumns(10);
		add(tfCompName);
		
		btnCreateCompany = new JButton("Create");
		btnCreateCompany.addActionListener(this);
		btnCreateCompany.setBounds(10, 162, 91, 23);
		add(btnCreateCompany);
		
		btnRemoveCompany = new JButton("Remove");
		btnRemoveCompany.addActionListener(this);
		btnRemoveCompany.setBounds(10, 350, 91, 23);
		add(btnRemoveCompany);
		
		JLabel lblRemoveOrLoadCompany = new JLabel("Remove / Load Company :");
		lblRemoveOrLoadCompany.setBounds(10, 218, 210, 14);
		add(lblRemoveOrLoadCompany);
		
		JLabel lblName = new JLabel("Name :");
		lblName.setBounds(10, 36, 139, 14);
		add(lblName);
		
		tfCompPass = new JPasswordField();
		tfCompPass.setColumns(10);
		tfCompPass.setBounds(10, 91, 210, 20);
		add(tfCompPass);
		
		JLabel lblPassword = new JLabel("Password :");
		lblPassword.setBounds(10, 75, 139, 14);
		add(lblPassword);
		
		JLabel lblEmail = new JLabel("Email :");
		lblEmail.setBounds(10, 115, 139, 14);
		add(lblEmail);
		
		tfCompEmail = new JTextField();
		tfCompEmail.setColumns(10);
		tfCompEmail.setBounds(10, 131, 210, 20);
		add(tfCompEmail);
		model = new DefaultListModel<String>();
		
		scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(10, 234, 210, 104);
		add(scrollPane);
		
		 listOfCompanies = new JList<String>();
		 scrollPane.setViewportView(listOfCompanies);
		listOfCompanies.setModel(model);
		
		btnLoadCompany = new JButton("Load");
		btnLoadCompany.addActionListener(this);
		btnLoadCompany.setBounds(129, 350, 91, 23);
		add(btnLoadCompany);
		
		btnUpdateCompany = new JButton("Update");
		btnUpdateCompany.setBounds(129, 162, 91, 23);
		btnUpdateCompany.addActionListener(this);
		add(btnUpdateCompany);
		
		lblId = new JLabel("x");
		lblId.setBounds(141, 36, 79, 14);
		add(lblId);
		
		refreshCompaniesJList();
	}

	@Override
	public void actionPerformed(ActionEvent e)  {
		JButton btnClicked = (JButton)e.getSource();
		if (btnClicked == btnCreateCompany){
			// constructing a company object to pass to AdminFacade
			Company company = new Company();
			try {
				if (!tfCompName.getText().equals("")) {
					company.setCompName(tfCompName.getText());
					if (!tfCompPass.getText().equals("")) {
						company.setPassword(tfCompPass.getText());
						if (!tfCompEmail.getText().equals("")) {
							company.setEmail(tfCompEmail.getText());
							af.createCompany(company);
							refreshCompaniesJList();
							JOptionPane.showMessageDialog(null, "company "+company.getCompName()+" was created.", "OK", 1);
						}
						else JOptionPane.showMessageDialog(null, "Must enter company Email !", "Notice!", JOptionPane.ERROR_MESSAGE);
					}
					else JOptionPane.showMessageDialog(null, "Must choose a password !", "Notice!", JOptionPane.ERROR_MESSAGE);
				}
				else JOptionPane.showMessageDialog(null, "Must enter company name !", "Notice!", JOptionPane.ERROR_MESSAGE);
			}
				
			catch (CompanyCreationException e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage()+"\nCompany already exists!", "Dialog", JOptionPane.ERROR_MESSAGE);
			}
		}
		if (btnClicked == btnRemoveCompany){
			// get company name from list
			String companyNameToRemove = listOfCompanies.getSelectedValue();
			int answer = JOptionPane.showConfirmDialog(new JFrame(), "Remove company " + companyNameToRemove +" will also remove all its coupons (including coupons that were already purchased by customers. are you sure ?)", "Confirm remove company", JOptionPane.ERROR_MESSAGE);
				if(answer == JOptionPane.YES_OPTION){
					try {
						// preparing an object with ID to send to DAO - DAO needs only ID for removal
						// ID can be retrieved using getCompanyId
						Company companyToRemove= af.getCompany(companyNameToRemove);
						// long idToRemove = af.getCompanyId(companyNameToRemove);
						// Company companyToRemove = new Company();
						// companyToRemove.setId(companyToRemove.getId());
						af.removeCompany(companyToRemove);
						refreshCompaniesJList();
						} catch (CompanyRemovalException | CompanyNotFoundException e1) {
							JOptionPane.showMessageDialog(new JFrame(), e1.getMessage(), "Dialog", JOptionPane.ERROR_MESSAGE);
					}
				// do nothing if not confirmed
			}else if (answer == JOptionPane.NO_OPTION) {}
		}
		
		if (btnClicked == btnUpdateCompany){
			// get company name from list
			//String companyNameToLoad = listOfCompanies.getSelectedValue();
			
			int answer = JOptionPane.showConfirmDialog(new JFrame(), "This will update  the company  " + tfCompName.getText() + " with : "
					+ "\nEmail : " + tfCompEmail.getText() 
					+"\nPassword : " + tfCompPass.getText() + " "
					+ "\nAre you sure ?", "Confirm updating company", JOptionPane.ERROR_MESSAGE);
			if(answer == JOptionPane.YES_OPTION){
				try {
					Company companyToUpdate = new Company();
					companyToUpdate.setCompName(tfCompName.getText());
					companyToUpdate.setPassword(tfCompPass.getText());
					companyToUpdate.setEmail(tfCompEmail.getText());

					af.updateCompany(companyToUpdate);
					refreshCompaniesJList();
				} catch (CompanyUpdateException e1) {
					JOptionPane.showMessageDialog(new JFrame(), e1.getMessage(), "Dialog", JOptionPane.ERROR_MESSAGE);
				}
				// do nothing if not confirmed
			}else if (answer == JOptionPane.NO_OPTION) {}
		}
			if (btnClicked == btnLoadCompany){
				// get company name from list
				String companyNameToLoad = listOfCompanies.getSelectedValue();
				try {
					// preparing an object with ID to send to DAO - DAO needs only ID for removal
					// ID can be retrieved using getCompanyId
					Company companyToLoad= af.getCompany(companyNameToLoad);
					tfCompName.setText(companyToLoad.getCompName());
					tfCompPass.setText(companyToLoad.getPassword());
					tfCompEmail.setText(companyToLoad.getEmail());
					lblId.setText("Id : " + companyToLoad.getId());
					
					// show password as hint for admin user only
					tfCompPass.setToolTipText(tfCompPass.getText());
					} catch (CompanyNotFoundException e1) {
						JOptionPane.showMessageDialog(new JFrame(), e1.getMessage(), "Dialog", JOptionPane.ERROR_MESSAGE);
				}
		}
	}
}
