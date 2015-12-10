package com.jbp.main;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JButton;

import com.jbp.beans.*;
import com.jbp.db.*;
import com.jbp.db.dao.CompanyDAO;
import com.jbp.db.dao.CouponDAO;
import com.jbp.db.dao.CustomerDAO;
import com.jbp.db.dao.DAOFactory;
import com.jbp.facade.*;
import com.jbp.gui.UILogin;
import com.jbp.utils.*;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.Map;
import java.util.Timer;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JList;

import java.awt.Font;

import javax.swing.border.LineBorder;

import java.awt.Color;

//
// This is the main system. it loads the properties from file and
// running a timer task (Thread) for cleanning old Coupons.
//
public class CouponSystemJFrame extends JFrame implements ActionListener {


	private static CouponSystemJFrame couponSystemInstance = new CouponSystemJFrame();
	// _vars to represent properties from file. parsing the file occurs in Utils class
	private String _systemName;
	private String _adminHash;
	private String _lookAndFeelTemplate;
	private Boolean _threadEnabled;
	private Integer _threadIntervalMinutes;
	private Boolean _loggingEnabled;
	Map<String, String> sysParams;

	private ConnPool connPool;
	private CompanyDAO companyDao;
	private CouponDAO couponDao;
	private CustomerDAO customerDao;

	private JPanel contentPane;
	private JList<Entry<String, String>> listSysParams;
	// scheduler for the daily cleaner thread task
	private Timer timer;
	private JButton btnShutdown;
	private JButton btnThread;
	private JButton btnLogin;

	// (singleton) - if no instance , create it only once.
	public synchronized static CouponSystemJFrame getInstance() {
		if (couponSystemInstance == null) {
			// create a CouponSystem only once
			couponSystemInstance = new CouponSystemJFrame();
		}
		return couponSystemInstance;
	}

	// gracefull shutdown
	public void shutdown(){
		Utils.logMessage(this, Severity.WARN, "system shutdown requested.");
		try {
			connPool.closeAllConnections();
		} catch (SQLException e) {
			Utils.logMessage(this, Severity.ERROR, "sql error returned from pool : " + e.getMessage());}
		Utils.logMessage(this, Severity.INFO, "system gracefully shutdown.");
		System.exit(0);
	}

	// private c'tor 'hides' the public default one. CouponSystem is a singleton
	private CouponSystemJFrame() {
		// load sys params from properties file
		Utils.logMessage(this, Severity.INFO, "Coupon system created.");

		// NOT IN USE in a web-coupon system. ( see method doc )
		// Utils.sysparam already set by a LoaderServlet
		//
		// Utils.loadSystemParameters();
        sysParams = Utils.getSystemParameters();
		_systemName = sysParams.get("SYSTEM_NAME");
		_adminHash = sysParams.get("ADMIN_HASH");
		_lookAndFeelTemplate = sysParams.get("LOOK_AND_FEEL_TEMPLATE");
		_threadEnabled = Boolean.parseBoolean(sysParams.get("THREAD_ENABLED"));
		_threadIntervalMinutes = Integer.parseInt(sysParams.get("THREAD_INTERVAL_MINUTES"));
		_loggingEnabled = Boolean.parseBoolean(sysParams.get("LOGGING_ENABLED"));
		Utils.setLoggingEnabled(_loggingEnabled);
		Utils.logMessage(this, Severity.DEBUG, "hashmap assigned to local vars, configuration applied.");

		setTitle("Coupon System : " + _systemName);
		setResizable(false);
		try {
			UIManager.setLookAndFeel(_lookAndFeelTemplate);
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			Utils.logMessage(this, Severity.PANIC, "cannot load LookAndFeel template. exiting...");
			System.exit(0);
		}

		// load this JFrame only after initialization from properties file.
		this.setVisible(true);

		// Main app CANNOT be closed with 'x' , must press on the 'shutdown'
		// buttong for gracefull shutdown.
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 785, 380);
		contentPane = new JPanel();
		contentPane.setLayout(null);
		setContentPane(contentPane);

		btnLogin = new JButton("Login");
		btnLogin.addActionListener(this);
		btnLogin.setBounds(659, 253, 98, 79);
		contentPane.add(btnLogin);

		listSysParams = new JList<Entry<String, String>>();
		listSysParams.setBorder(new LineBorder(new Color(0, 0, 0)));
		listSysParams.setFont(new Font("Consolas", Font.PLAIN, 14));
		listSysParams.setEnabled(false);
		listSysParams.setBounds(20, 11, 737, 219);
		contentPane.add(listSysParams);

		// a model is needed for JTable
		DefaultListModel<Entry<String, String>> model = new DefaultListModel<Entry<String, String>>();
		listSysParams.setModel(model);

		JPanel panelSystem = new JPanel();
		panelSystem.setBorder(new LineBorder(new Color(0, 0, 0)));
		panelSystem.setLayout(null);
		panelSystem.setBounds(20, 253, 428, 79);
		contentPane.add(panelSystem);

		btnShutdown = new JButton("Shutdown system");
		btnShutdown.setForeground(Color.RED);
		btnShutdown.setBounds(20, 36, 181, 23);
		panelSystem.add(btnShutdown);

		btnThread = new JButton("Stop cleaner thread");
		btnThread.addActionListener(this);
		btnThread.setForeground(Color.RED);
		btnThread.setBounds(224, 36, 181, 23);
		panelSystem.add(btnThread);

		JLabel lblSys = new JLabel("Coupon system running on : " + System.getProperty("os.name")
							+ " / [version-" + System.getProperty("os.version") + "].");
		lblSys.setBounds(20, 11, 369, 14);
		panelSystem.add(lblSys);
		btnShutdown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// calling for gracefull shutdown
				shutdown();
			}
		});

		// get a Set of Entries from all KVP in Map
		Set<Entry<String, String>> entries = sysParams.entrySet();
		for(Entry<String, String> entry : entries){
			model.addElement(entry);
		}

		// get/create the connection pool
		connPool=ConnPool.getInstance();

		// pass it to DAO's
		createDAOs(connPool);

		if (_threadEnabled) {
			// start the daily cleaner thread
			startTimer();
		}
	}

	// will start cleaner thread ( at c'tor , or after user stoped it manually )
	public void startTimer() {
		// start daily thread to clean old coupons
		Utils.logMessage(this, Severity.INFO, "Thread scheduler started. interval = " + _threadIntervalMinutes + " minutes." );
        timer = new Timer();
        // 60000 msec = 1 min
        // minutes of timer specified in cs.properties file
        timer.scheduleAtFixedRate(new DailyCouponExpirationTask(couponDao), 30,
        		(_threadIntervalMinutes * 60000));
	}

	// stop the cleaner thread
	public void stopTimer(){
		timer.cancel();
		Utils.logMessage(this, Severity.INFO, "Thread stopped by user request." );
	}

	// create concrete DAO objects that implements the interfaces
	// using a Factory pattern enable changing the underlying JDBC with
	// a different implementation
	private void createDAOs(ConnPool connPool){
		DAOFactory.setConnPool(connPool);
		this.customerDao = DAOFactory.getCustomerDaoInstance();
		this.companyDao =  DAOFactory.getCompanyDaoInstance();
		this.couponDao = DAOFactory.getCouponDaoInstance();
		Utils.logMessage(this, Severity.INFO, "3 DAO objects created.");
	}

	// returns DAO
	public CustomerDAO getCustomerDao() {
		return this.customerDao;
	}

	// returns DAO
	public CompanyDAO getCompanyDao() {
		return this.companyDao;
	}

	// returns DAO
	public CouponDAO getCouponDao() {
		return this.couponDao;
	}

	// returns SystemName
	public String getSystemName() {
		return this._systemName;
	}

	// global authentication method. depends on the ClientType
	public CouponClientFacade login(String name, String password, ClientType clientType){
		Utils.logMessage(this, Severity.DEBUG, clientType + " login invoked with pass = " + password);

		// login of Admin. using Utils class for chacking MD5 authentication
		if (clientType == ClientType.ADMIN){
			String md5HashOfPassword = Utils.MD5(password);
			Utils.logMessage(this, Severity.DEBUG, "db hash = " + _adminHash);
			Utils.logMessage(this, Severity.DEBUG, "calculated hash = " + md5HashOfPassword);
			if(md5HashOfPassword.equals(_adminHash)){
				Utils.logMessage(this, Severity.DEBUG, "admin authenticated by md5.");
				// admin auth success, init AdminFacade and passing the DAO's
				AdminFacade adminFacade = new AdminFacade(customerDao, companyDao, couponDao);
				return adminFacade;
			}
			Utils.logMessage(this, Severity.ERROR, "admin authentication failed. md5 not match !");
		}
		// login of Company
		if (clientType == ClientType.COMPANY){
			// success auth will return the customer ID
			Long companyId = companyDao.login(name, password);
			if(companyId != null && companyId != 0){
				// construct a customer with those params
				Company company = new Company();
				company.setId(companyId);
				company.setCompName(name);
				company.setPassword(password);
				// create a CustomerFacade, referring to this company
				CompanyFacade companyFacade = new CompanyFacade(company);
				companyFacade.setCompanyDao(companyDao);
				companyFacade.setCouponDao(couponDao);
				return companyFacade;
			}
		}
		// login of Customer
		if (clientType == ClientType.CUSTOMER){
			// success auth will return the customer ID
			Long customerId = customerDao.login(name, password);
			if(customerId != null && customerId != 0){
				// construct a customer with those params
				Customer customer = new Customer();
				customer.setId(customerId);
				customer.setCustName(name);
				customer.setPassword(password);
				// create a CustomerFacade
				CustomerFacade customerFacade = new CustomerFacade();
				// customerFacade need to ref that specific customer  ( 1 per client )
				customerFacade.setCustomer(customer);
				// and ref the DAOs ( customer + coupons )
				customerFacade.setCustomerDao(customerDao);
				customerFacade.setCouponDao(couponDao);
				return customerFacade;
			}
		}
        return null;
	}

	// handles local buttons in this JFrame
	@Override
	public void actionPerformed(ActionEvent e) {
		JButton button =(JButton)e.getSource();
		if (button == btnLogin) {
			// show the login UI as another JFrame. multiple access is optional
			UILogin uiLogin = new UILogin(this);
			uiLogin.setVisible(true);
		}
		if (button == btnThread){
			// manually stop/start the cleaning thread
			if (btnThread.getText().equals("Stop cleaner thread")) {
				// request to stop when thread in alive state
				stopTimer();
				btnThread.setText("Start cleaner thread");
				btnThread.setForeground(Color.BLUE);
			}else if (btnThread.getText().equals("Start cleaner thread")) {
				// request to start when thread in dead state
				startTimer();
				btnThread.setText("Stop cleaner thread");
				btnThread.setForeground(Color.RED);
			}
		}
	}
}


