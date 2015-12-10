package com.jbp.exceptions;

import com.jbp.beans.Company;

// exception to indicate a company could not be removed

public class CompanyRemovalException extends Exception {
	private Company company;

	public CompanyRemovalException(Company company) {
		this.company = company;
	}
	
	@Override
	public String getMessage() {
		
		return "Remove company "+this.company.getCompName()+" failed !";
	}
}
