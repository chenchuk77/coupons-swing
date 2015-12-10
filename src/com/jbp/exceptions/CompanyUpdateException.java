package com.jbp.exceptions;

import com.jbp.beans.Company;

// exception to indicate a company could not be update

public class CompanyUpdateException extends Exception {
	private Company company;
	
	public CompanyUpdateException(Company company) {
		this.company = company;
	}
	
	@Override
	public String getMessage() {

		return "Update for company "+this.company.getCompName()+" failed !";
	}
}
