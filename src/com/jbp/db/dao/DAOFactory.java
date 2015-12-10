package com.jbp.db.dao;

import com.jbp.db.ConnPool;
import com.jbp.db.dbdao.CompanyDBDAO;
import com.jbp.db.dbdao.CouponDBDAO;
import com.jbp.db.dbdao.CustomerDBDAO;
import com.jbp.db.xmldao.CustomerXMLDAO;
import com.jbp.utils.Severity;
import com.jbp.utils.Utils;

public class DAOFactory {
	private static ConnPool connPool = null;
	private static CustomerDAO customerDAO = null;
	private static CompanyDAO companyDAO = null;
	private static CouponDAO couponDAO = null;
	
	public static void setConnPool(ConnPool connPool){
		// set the pool for the DAO's
		DAOFactory.connPool = connPool;
		Utils.logMessage("DAOFactory", Severity.INFO, "factory initialize connection pool");
	}
	
	public static CustomerDAO getCustomerDaoInstance(){
		if (customerDAO != null ){
			return customerDAO;
		} else if (connPool != null) {
			// no DAO , but pool available => can create a new instance
			customerDAO = new CustomerDBDAO(connPool);
			// the next line will not work ( XML is not fully implemented )
			// it just shows how the Factory can replace the object with another
			// without changing client code.
			//
			// customerDAO = new CustomerXMLDAO(connPool);
			//
			Utils.logMessage("DAOFactory", Severity.INFO, "CustomerDBDAO instantiated.");
			return customerDAO;
		}
		// if cant create DAO because no pool configured => null
		return null;
	}
	
	public static CompanyDAO getCompanyDaoInstance(){
		if (companyDAO != null ){
			return companyDAO;
		} else if (connPool != null) {
			// no DAO , but pool available => can create a new instance
			companyDAO = new CompanyDBDAO(connPool);
			Utils.logMessage("DAOFactory", Severity.INFO, "CompanyDBDAO instantiated.");
			return companyDAO;
		}
		// if cant create DAO because no pool configured => null
		return null;
	}

	public static CouponDAO getCouponDaoInstance(){
		if (couponDAO != null ){
			return couponDAO;
		} else if (connPool != null) {
			// no DAO , but pool available => can create a new instance
			couponDAO = new CouponDBDAO(connPool);
			Utils.logMessage("DAOFactory", Severity.INFO, "CouponDBDAO instantiated.");
			return couponDAO;
		}
		// if cant create DAO because no pool configured => null
		return null;
	}

	
}
