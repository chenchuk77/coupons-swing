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

public class CouponSystem {
	//public class CouponSystem extends JFrame implements ActionListener {


	private static CouponSystem couponSystemInstance = new CouponSystem();
	// _vars to represent properties from file. parsing the file occurs in Utils class
	private String _systemName;
	private String _adminHash;
	private Boolean _threadEnabled;
	private Integer _threadIntervalMinutes;
	private Boolean _loggingEnabled;
	Map<String, String> sysParams;

	private ConnPool connPool;

	private CompanyDAO companyDao;
	private CouponDAO couponDao;
	private CustomerDAO customerDao;

	// scheduler for the daily cleaner thread task
	private Timer timer;

	// (singleton) - if no instance , create it only once.
	public synchronized static CouponSystem getInstance() {
		if (couponSystemInstance == null) {
			// create a CouponSystem only once
			couponSystemInstance = new CouponSystem();
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
	private CouponSystem() {
		// load sys params from properties file
		Utils.logMessage(this, Severity.INFO, "Coupon system created.");

		// NOT IN USE in a web-coupon system. ( see method doc )
		// Utils.sysparam already set by a LoaderServlet
		//
		// Utils.loadSystemParameters();
        sysParams = Utils.getSystemParameters();
		_systemName = sysParams.get("SYSTEM_NAME");
		_adminHash = sysParams.get("ADMIN_HASH");
		_threadEnabled = Boolean.parseBoolean(sysParams.get("THREAD_ENABLED"));
		_threadIntervalMinutes = Integer.parseInt(sysParams.get("THREAD_INTERVAL_MINUTES"));
		_loggingEnabled = Boolean.parseBoolean(sysParams.get("LOGGING_ENABLED"));
		Utils.setLoggingEnabled(_loggingEnabled);
		Utils.logMessage(this, Severity.DEBUG, "hashmap assigned to local vars, configuration applied.");

		// get/create the connection pool
		connPool=ConnPool.getInstance();

		// pass it to DAO's
		createDAOs(connPool);

		// cleaner thread can be disabled in cs.properties file
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

	public ConnPool getConnPool() {
		return connPool;
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
}


