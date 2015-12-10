package com.jbp.facade;

import java.util.Collection;

import com.jbp.beans.Company;
import com.jbp.beans.Customer;
import com.jbp.db.dao.CompanyDAO;
import com.jbp.db.dao.CouponDAO;
import com.jbp.db.dao.CustomerDAO;
import com.jbp.exceptions.CompanyCreationException;
import com.jbp.exceptions.CompanyNotFoundException;
import com.jbp.exceptions.CompanyRemovalException;
import com.jbp.exceptions.CompanyUpdateException;
import com.jbp.exceptions.CustomerCreationException;
import com.jbp.exceptions.CustomerNotFoundException;
import com.jbp.exceptions.CustomerRemovalException;
import com.jbp.exceptions.CustomerUpdateException;
import com.jbp.main.ClientType;
import com.jbp.utils.Severity;
import com.jbp.utils.Utils;

//
// Admin Facade provides the business functionality for sys admin
// this Facade refers to all 3 DAO's for all kind of operations
//
public class AdminFacade implements CouponClientFacade {
	private CustomerDAO customerDao;
	private CompanyDAO companyDao;
	private CouponDAO couponDao;

	public AdminFacade() {
	}
	
	// c'tor of Admin Facade refers to all DAO's ( for all admin operations )
	public AdminFacade(CustomerDAO customerDao, CompanyDAO companyDao, CouponDAO couponDao) {
		this.customerDao = customerDao;
		this.companyDao = companyDao;
		this.couponDao = couponDao;
	}
	
	@Override
	public CouponClientFacade login(String name, String password, ClientType clientType) {
		return null;
	}
	
	// C.R.U.D operations may throws exceptions. all exception must be handled
	// in the client code ( GUI Windows )
	//
	public void createCompany(Company company) throws CompanyCreationException {
		companyDao.createCompany(company);
		Utils.logMessage(this, Severity.INFO, "company created : " + company.getCompName());
	}
	public void removeCompany(Company company) throws CompanyRemovalException{
		companyDao.removeCompany(company);
		Utils.logMessage(this, Severity.INFO, "company removed : " + company.getCompName());
	}
	public void updateCompany(Company company) throws CompanyUpdateException{
		companyDao.updateCompany(company);
		Utils.logMessage(this, Severity.INFO, "company updated : " + company.getCompName());
	}
	public Collection<Company> getAllCompanies(){
		if (companyDao == null) {
			Utils.logMessage(this, Severity.DEBUG, "null companies returned");
			return null;
		}
		return companyDao.getAllCompanies();
	}

	// helper methods to ask DAO for the ID of a given name, then use the ID to get from DB 
	public Company getCompany(String compName) throws CompanyNotFoundException{
		long companyId = companyDao.getCompanyId(compName);
		Company company = new Company();
		company = companyDao.getCompany(companyId);
		Utils.logMessage(this, Severity.INFO, "company  : " + compName + " : Id = " + companyId);
		return company;
	}
	
	// helper methods to ask DAO for the company by ID 
		public Company getCompanyByID(long companyId) throws CompanyNotFoundException{
			Utils.logMessage(this, Severity.INFO, "company Id = " + companyId);
			return companyDao.getCompany(companyId);
		}

	// updating a customer
	public void updateCustomer(Customer customer) throws CustomerUpdateException{
		customerDao.updateCustomer (customer);
		Utils.logMessage(this, Severity.INFO, "customer updated : " + customer.getCustName());
	}
	
	// get collection of all customers
	public Collection<Customer> getAllCustomers() {
		Collection<Customer> allCustomers = customerDao.getAllCustomers();
		Utils.logMessage(this, Severity.INFO, "getAllCustomers() returns.");
		return allCustomers;
	}
	
	// create a new customer
	public void createCustomer(Customer customer) throws CustomerCreationException{
		customerDao.createCustomer(customer);
		Utils.logMessage(this, Severity.INFO, "customer created : " + customer.getCustName());
	}

	// remove a customer
	public void removeCustomer(Customer customer)  throws CustomerRemovalException{
		customerDao.removeCustomer(customer);
		Utils.logMessage(this, Severity.INFO, "customer removed : " + customer.getCustName());
	}
	
	// helper methods to ask DAO for the ID of a given name, then use the ID to get from DB 
	public Customer getCustomer(String custName) throws CustomerNotFoundException{
		long customerId = customerDao.getCustomerId(custName);
		Customer customer = new Customer();
		customer = customerDao.getCustomer(customerId);
		Utils.logMessage(this, Severity.INFO, "customer  : " + custName + " : Id = " + customerId);
		return customer;
	}
	
	// helper methods to ask DAO for the customer by ID
		public Customer getCustomer(long customerId) throws CustomerNotFoundException{
			Utils.logMessage(this, Severity.INFO, "customer Id = " + customerId);
			return customerDao.getCustomer(customerId);
		}
}
