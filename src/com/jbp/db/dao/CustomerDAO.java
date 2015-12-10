package com.jbp.db.dao;

import java.util.Collection;

import com.jbp.beans.*;
import com.jbp.exceptions.CustomerCreationException;
import com.jbp.exceptions.CustomerRemovalException;
import com.jbp.exceptions.CustomerUpdateException;
import com.jbp.utils.*;
// interface to define the operation needed for a Customer object,
// decoupling functionality from implementation.
// implementation will have to deal with DB driver and SQL commands
//
public interface CustomerDAO {
	public void createCustomer(Customer customer) throws CustomerCreationException; 
	public void removeCustomer(Customer customer) throws CustomerRemovalException;
	public void updateCustomer(Customer customer) throws CustomerUpdateException;
	public Customer getCustomer(long id);
	public Collection<Customer> getAllCustomers();
	public Collection<Coupon> getCoupons(Customer customer);
	public Long login(String compName, String password);
	
	// get the Id from DB. this is used for CustomerFacade
	public long getCustomerId(String custName);
	public void addCouponToCustomer(Coupon coupon, Customer customer);
}
