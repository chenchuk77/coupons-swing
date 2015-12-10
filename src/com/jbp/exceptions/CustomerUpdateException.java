package com.jbp.exceptions;

import com.jbp.beans.Customer;

// exception may occur when trying to update customer's details

public class CustomerUpdateException extends Exception {
private Customer customer;
	
	public CustomerUpdateException(Customer customer) {
		this.customer = customer;
	}
	
	@Override
	public String getMessage() {
		return "Updating customer "+this.customer.getCustName()+" failed !";
	}
}
