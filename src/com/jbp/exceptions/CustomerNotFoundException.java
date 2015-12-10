package com.jbp.exceptions;

import com.jbp.beans.Customer;

// exception may occur when trying to get a customer which does not exists

public class CustomerNotFoundException extends Exception {
	private Customer customer;
	
	public CustomerNotFoundException(Customer customer) {
		this.customer = customer;
	}
	
	@Override
	public String getMessage() {
		return "Customer "+this.customer.getCustName()+" not found !";
	}
}
