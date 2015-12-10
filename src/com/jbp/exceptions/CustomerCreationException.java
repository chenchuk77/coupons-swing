package com.jbp.exceptions;

import com.jbp.beans.Customer;

// exception may occur when trying to create a new customer

public class CustomerCreationException extends Exception 
{
	private Customer customer;
	
	public CustomerCreationException(Customer customer) {
		this.customer = customer;
	}
	
	@Override
	public String getMessage() {
		return "Creating customer "+this.customer.getCustName()+" failed !";
	}
}
