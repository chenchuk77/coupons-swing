package com.jbp.exceptions;

import com.jbp.beans.Customer;

// exception may occur when trying to remove a customer

public class CustomerRemovalException extends Exception {
	private Customer customer;
	
	public CustomerRemovalException(Customer customer) {
		this.customer = customer;
	}
	
	@Override
	public String getMessage() {
		return "Removing customer "+this.customer.getCustName()+" failed !";
	}
}
