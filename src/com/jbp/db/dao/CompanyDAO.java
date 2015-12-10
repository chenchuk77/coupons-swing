package com.jbp.db.dao;

import java.util.Collection;

import com.jbp.beans.*;
import com.jbp.exceptions.CompanyCreationException;
import com.jbp.exceptions.CompanyRemovalException;
import com.jbp.exceptions.CompanyUpdateException;
// interface to define the operation needed for a Company object,
// decoupling functionality from implementation.
// implementation will have to deal with DB driver and SQL commands
//
public interface CompanyDAO {
	public void createCompany(Company company) throws CompanyCreationException;
	public void removeCompany(Company company) throws CompanyRemovalException;
	public void updateCompany(Company company) throws CompanyUpdateException;
	public Company  getCompany(long id);
	public Collection<Company> getAllCompanies();
	public Collection<Coupon> getCoupons(long id);
	public Long login(String compName, String password);
	public long getCompanyId(String compName);


}
