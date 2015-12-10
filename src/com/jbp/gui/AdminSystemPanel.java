package com.jbp.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Map;
import javax.swing.Timer;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import com.jbp.beans.Company;
import com.jbp.facade.AdminFacade;
import com.jbp.utils.Utils;
import javax.swing.JTable;

//
// JPanel for AdminWindow JFrame.
// it shows system global parameters that were loaded from cs.properties file
//
public class AdminSystemPanel extends JPanel implements ActionListener {

	private Collection<Company> colCompanies;
	private AdminFacade af;
	private DefaultListModel<String> companiesListModel;
	private JScrollPane scrollPane;
	private JTable table;
	private JLabel lblUptime;
	
	// c'tor refers to the Admin Facade
	public AdminSystemPanel(AdminFacade af) {
		// updating the display every second
		Timer timer = new javax.swing.Timer(1000, this);
		timer.start();
		
		this.af = af;
		colCompanies = this.af.getAllCompanies();
		setLayout(null);
		setBounds(10, 45, 600, 384);
		
		JLabel lblSystem = new JLabel("System management console");
		lblSystem.setBounds(10, 11, 210, 14);
		add(lblSystem);

		companiesListModel = new DefaultListModel<String>();
		
		scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(10, 68, 580, 271);
		add(scrollPane);
		
		String[] columnNames = {"Parameter",  "Value",};
		Map<String, String> sysParams = Utils.getSystemParameters();
		Object[][] data = Utils.mapTo2dArray(sysParams);
		
		table = new JTable(data, columnNames);
		scrollPane.setViewportView(table);
		
		lblUptime = new JLabel("uptime");
		lblUptime.setBounds(375, 354, 215, 14);
		add(lblUptime);
	}

	@Override
	// returns formatted uptime. will be called every second by the timer
	public void actionPerformed(ActionEvent e)  {
		Long secondsUp = Utils.getSystemUptime();

		Long uptimeDays = secondsUp / 86400;
		Long uptimeHours = (secondsUp % 86400 ) / 3600 ;
		Long uptimeMinutes = ((secondsUp % 86400 ) % 3600 ) / 60 ;
		Long uptimeSeconds = ((secondsUp % 86400 ) % 3600 ) % 60  ;

		String uptime = uptimeDays + " Days, "+ 
								((uptimeHours >= 10) ? "" : "0") + uptimeHours + ":"+
								((uptimeMinutes >= 10) ? "" : "0") + uptimeMinutes + ":"+
								((uptimeSeconds>= 10) ? "" : "0") + uptimeSeconds ;
		
		lblUptime.setText("System uptime : " + uptime);
	}
}
