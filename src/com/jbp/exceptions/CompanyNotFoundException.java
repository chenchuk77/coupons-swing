package com.jbp.exceptions;

import com.jbp.beans.Company;

// not in use, there is no access to non existed companies
public class CompanyNotFoundException extends Exception {

private Company company;
	
	public CompanyNotFoundException(Company company) {
		this.company = company;
	}
	
	@Override
	public String getMessage() {
		
		return "Company "+this.company.getCompName()+" wasn't found !";
	}
}
