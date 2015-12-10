package com.jbp.exceptions;

import com.jbp.beans.Company;

//
// exception handler for an exception which may occur during company object creation
// and it's DB registration
//
public class CompanyCreationException extends Exception {
	private Company company;
	
	public CompanyCreationException(Company company) {
		this.company = company;
	}
	
	@Override
	public String getMessage() {
		
		return "Creating company "+this.company.getCompName()+" failed !";
	}
}
