package com.jbp.gui;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

import com.jbp.facade.AdminFacade;

//
// This is the GUI for admin.
// it provides administration functionality using the Admin Facade logic
// its contains 3 panels on a tabbed pane 
//
public class AdminWindow extends JFrame {

	private AdminFacade af;
	private JPanel contentPane;
	
    // Returns an ImageIcon, or null if the path was invalid
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = JTabbedPane.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    // c'tor refers to the Admin Facade
	public AdminWindow(AdminFacade af) {
	    JTabbedPane tabbedPane = new JTabbedPane();
	    ImageIcon iconCompany = createImageIcon("/images/company.png");
	    ImageIcon iconCustomer = createImageIcon("/images/customer2.png");
	    ImageIcon iconSystem = createImageIcon("/images/system.png");
    
	    JComponent panelSystem = new AdminSystemPanel(af);
	    tabbedPane.addTab("System", iconSystem, panelSystem, "System properties");
	    tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

	    JComponent panelCompanies = new AdminCompaniesPanel(af);
	    tabbedPane.addTab("Companies", iconCompany, panelCompanies, "Companies manager");
	    tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
	    
	    JComponent panelCustomers = new AdminCustomersPanel(af);
	    tabbedPane.addTab("Customers", iconCustomer, panelCustomers, "Customers manager");
	    tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);
		
	    // if this JFrame closed - all other childs should be close also
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(200, 200, 620, 490);
		setResizable(false);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		contentPane.add(tabbedPane);
		super.setTitle("Admin Window");	}
}
