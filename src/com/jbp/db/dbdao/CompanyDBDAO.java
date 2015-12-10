package com.jbp.db.dbdao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.jbp.beans.*;
import com.jbp.db.ConnPool;
import com.jbp.db.dao.CompanyDAO;
import com.jbp.exceptions.CompanyCreationException;
import com.jbp.exceptions.CompanyRemovalException;
import com.jbp.exceptions.CompanyUpdateException;
import com.jbp.utils.*;

//
// CompanyDBDAO class implements CompanyDAO interface.
// Provide's methods to insert and retrieve data from and to the DB.
//

public class CompanyDBDAO implements CompanyDAO {

	// reference to the pool for this DAO
	private ConnPool connPool;
	
	// constructor, must get a ConnPool object.
	// initialize the pool for this DAO
	public CompanyDBDAO(ConnPool connPool) 
	{
		this.connPool=connPool;
	}

	// all methods access the db in the same way :
	// 1. get connection from pool
	// 2. run a SQL query/update using prepareStatement for safety
	// SELECT queries construct an object from the returned ResultSet
	// INSERT/DELETE/UPDATE update the DB
	// 3. print the log message according to the action performed
	// 4. return the used connection into the connection pool
	//
	
	//	uses to create new company obj and insert it into the DB
	@Override
	public void createCompany(Company company) throws CompanyCreationException {
		Connection conn = connPool.getConnection();
		try {
			String query = "INSERT INTO Companies (COMP_NAME, PASSWORD,EMAIL) VALUES (?, ?, ?)";
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setString(1, company.getCompName());
			pstmt.setString(2, company.getPassword());
			pstmt.setString(3, company.getEmail());
			pstmt.executeUpdate();
			pstmt.close();
			Utils.logMessage(this, Severity.INFO, "company created : " + company.getCompName() + ".");
		} catch (SQLException e) {
			Utils.logMessage(this, Severity.ERROR, "company " + company.getCompName() + " already exists." );
			throw new CompanyCreationException(company);
		}
		finally {connPool.returnConnection(conn);}
	}

	// this method impact 4 possible tables :
	// customer_coupon (if customer purchase any)
	// company_coupon (if it has coupons)
	// coupon (if any)
	// company
	
	public void removeCompany(Company company) throws CompanyRemovalException{
		Connection conn = connPool.getConnection();
		try {			
			// first we got the list of couponIds of this company
			String query = "SELECT COUPON_ID FROM Company_Coupon WHERE COMP_ID=?";
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setLong(1, company.getId());
			// save the result in a list
			ResultSet rs = pstmt.executeQuery();
			List<Long> couponIds = new ArrayList<Long>();
			while (rs.next()) {
				Long couponId = rs.getLong("COUPON_ID");
				couponIds.add(rs.getLong("COUPON_ID"));
				Utils.logMessage(this, Severity.DEBUG, "couponId to remove = " + couponId);
			}

			// if list not empty ( at lease 1 coupon. continue )
			if (couponIds.size() > 0) {
				Utils.logMessage(this, Severity.DEBUG, "# couponId = " + couponIds);
				// using StringBuilder to build  - SELECT FROM ... WHERE ID IN (7, 8 ,24);
				// and run SQL as a single statement
				StringBuilder sb = new StringBuilder("(");
				for (int i = 0; i < couponIds.size(); i++) {
					// using elvis to terminate with ')' and ignore the last comma. ie. "(3,4)" and not "(3,4,"
					sb.append(couponIds.get(i)+ (i == couponIds.size() - 1 ? ")" : ", "));
				}
				Utils.logMessage(this, Severity.DEBUG, "sb = " + sb);
				
				// deleting all coupons of that company, that were already purchased by customers
				query = "DELETE FROM Customer_Coupon WHERE COUPON_ID IN " + sb.toString() + ";";
				pstmt = conn.prepareStatement(query);
				int rowsDeletedFromCustomer_Coupon = pstmt.executeUpdate();
				Utils.logMessage(this, Severity.DEBUG,rowsDeletedFromCustomer_Coupon + " rows deleted from table : Customer_Coupon.");
				
				// delete coupons of this company from company_coupon
				query = "DELETE FROM Company_Coupon WHERE COMP_ID=?";
				pstmt = conn.prepareStatement(query);
				pstmt.setLong(1, company.getId());
				int rowsDeletedFromCompany_Coupon = pstmt.executeUpdate();
				Utils.logMessage(this, Severity.DEBUG,rowsDeletedFromCompany_Coupon + " rows deleted from table : Company_Coupon.");

				// delete the coupon itself from the Coupons table
				query = "DELETE FROM Coupons WHERE COMPANY_ID=?";
				pstmt = conn.prepareStatement(query);
				pstmt.setLong(1, company.getId());
				int rowsDeletedFromCoupon = pstmt.executeUpdate();
				Utils.logMessage(this, Severity.DEBUG,rowsDeletedFromCoupon + " rows deleted from table : Coupons.");
			}
			// finally delete from companies table
			query = "DELETE FROM Companies WHERE ID =?";
			pstmt = conn.prepareStatement(query);
			pstmt.setLong(1, company.getId());
			int rowsrecordsDeletedFromCompany = pstmt.executeUpdate();
			Utils.logMessage(this, Severity.DEBUG,rowsrecordsDeletedFromCompany + " rows deleted from table : Companies.");
			
			pstmt.close();
			Utils.logMessage(this, Severity.INFO, "company removed : " + company.getCompName() + ", and ALL it coupons and relations");
		} catch (SQLException e) {
			Utils.logMessage(this, Severity.ERROR, "cannot remove company : " + company.getCompName() + " : "+ e.getMessage());
			throw new CompanyRemovalException(company);
		}
		finally {connPool.returnConnection(conn);}
	}

	// this method updates the company's changeable details
	// all attributes can be updates except company name and company id
	//
	public void updateCompany(Company company) throws CompanyUpdateException{
		Connection conn = connPool.getConnection();
		try {
			String query = "UPDATE Companies SET PASSWORD=?, EMAIL=? WHERE COMP_NAME=?";
			PreparedStatement  pstmt = conn.prepareStatement(query);
			pstmt.setString(1, company.getPassword());
			pstmt.setString(2, company.getEmail());
			pstmt.setString(3, company.getCompName());
			pstmt.executeUpdate();
			pstmt.close();
			Utils.logMessage(this, Severity.INFO, "company updated : " + company.getCompName() + ".");
		} catch (SQLException e) {
			Utils.logMessage(this, Severity.ERROR, "cannot update company : " + company.getCompName() + " : "+ e.getMessage());
			throw new CompanyUpdateException(company);
		}
		finally {connPool.returnConnection(conn);}
	}

	// method to get a specific company from the Companies table
	public Company getCompany(long id) {
		// construct a Company to return data
		Company company = new Company();
		Connection conn = connPool.getConnection();
		try {
			String query = "SELECT * FROM Companies WHERE ID=?";
			PreparedStatement  pstmt = conn.prepareStatement(query);
			pstmt.setLong(1, id);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				company.setId(rs.getLong("ID"));
				company.setCompName(rs.getString("COMP_NAME"));
				company.setPassword(rs.getString("PASSWORD"));
				company.setEmail(rs.getString("EMAIL"));
			}
			pstmt.close();
			Utils.logMessage(this, Severity.INFO, "company returned : " + company.getCompName() + ".");
		} catch (SQLException e) {Utils.logMessage(this, Severity.ERROR, "cannot get company : " + e.getMessage());}
		finally {connPool.returnConnection(conn);}
		return company;
	}
	
	// method uses to get a collection of all companies in Companies table
	@Override
	public Collection<Company> getAllCompanies() {
		Utils.logMessage(this, Severity.DEBUG, "getAllCompanies() invoked.");

		// construct a List<Company> to return data
		List<Company> allCompanies = new ArrayList<Company>();
		Connection conn = connPool.getConnection();
		try {
			String query = "SELECT * FROM Companies";
			PreparedStatement pstmt = conn.prepareStatement(query);
			ResultSet rs = pstmt.executeQuery();
			Company company = null;
			while (rs.next()) {
				company = new Company();
				company.setId(rs.getLong("ID"));
				company.setCompName(rs.getString("COMP_NAME"));
				company.setPassword(rs.getString("PASSWORD"));
				company.setEmail(rs.getString("EMAIL"));
				allCompanies.add(company);
			}
			pstmt.close();
			Utils.logMessage(this, Severity.INFO, "companies list returned successfully.");
		} catch (SQLException e) {Utils.logMessage(this, Severity.ERROR, "cannot get all companies : " + e.getMessage());}
		finally {connPool.returnConnection(conn);}
		return allCompanies;
	}

	// method to get a collection of all coupons for a specific company
	@Override
	public Collection<Coupon> getCoupons(long companyId) {
		// construct a List<Company> to return data
		List<Coupon> coupons = new ArrayList<Coupon>();
		Connection conn = connPool.getConnection();
		try {
			String query = "SELECT * FROM Coupons WHERE COMPANY_ID=?";
			PreparedStatement  pstmt = conn.prepareStatement(query);
			pstmt.setLong(1, companyId);
			ResultSet rs = pstmt.executeQuery();
			Coupon coupon = null;
			while (rs.next()) {
				coupon = new Coupon();
				coupon.setId(rs.getLong("ID"));
				coupon.setTitle(rs.getString("TITLE"));
				coupon.setStartDate(rs.getDate("START_DATE"));
				coupon.setEndDate(rs.getDate("END_DATE"));
				coupon.setAmount(rs.getInt("AMOUNT"));
				coupon.setType(CouponType.valueOf(rs.getString("TYPE")));
				coupon.setMessage(rs.getString("MESSAGE"));
				coupon.setPrice(rs.getDouble("PRICE"));
				coupon.setImage(rs.getString("IMAGE"));
				coupon.setCompanyId(rs.getLong("COMPANY_ID"));
				coupons.add(coupon);
			}
			pstmt.close();
			Utils.logMessage(this, Severity.INFO, "companies list returned for company " + companyId +".");
		} catch (SQLException e) {Utils.logMessage(this, Severity.ERROR, "cannot get Coupons : " + e.getMessage());}
		finally {connPool.returnConnection(conn);}
		return coupons;
	}

	// this method will look for auth for the company name and password combination
	// in the Companies table.
	// if name and password match, returns the company ID 
	@Override
	public Long login(String compName, String password) {
		Long companyId = null;
		Connection conn = connPool.getConnection();
		try {
			String query = "SELECT ID FROM Companies WHERE COMP_NAME=? AND PASSWORD=?";
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setString(1, compName);
			pstmt.setString(2, password);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				companyId = rs.getLong(1);
				// if auth success, the ID returned 
			}
			pstmt.close();
			Utils.logMessage(this, Severity.INFO, "login ok company : [" + companyId + "]  -  " + compName + ".");
		} catch (SQLException e){Utils.logMessage(this, Severity.ERROR, "cannot login : "+ e.getMessage());}
		finally {connPool.returnConnection(conn);}
		return companyId;
	}
	
	//  method used to get the company's ID by the company name from the Companies table
	@Override
	public long getCompanyId(String compName) {
		// construct a Company to return data
		Company company= new Company();
		Connection conn = connPool.getConnection();
		try {
			String query = "SELECT ID FROM Companies WHERE COMP_NAME=?";
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setString(1, compName);
			ResultSet rs = pstmt.executeQuery();
			// if line return : its a company record from DB.
			while (rs.next()) {
				company.setId(rs.getLong("ID"));
			}
			pstmt.close();
			connPool.returnConnection(conn);
			Utils.logMessage(this, Severity.INFO, "company  " + compName + " : ID " + company.getId() +" returned from db.");
		} catch (SQLException e){Utils.logMessage(this, Severity.ERROR, "cannot get copmpany  id of "  + compName + ". " + e.getMessage());}
		finally {connPool.returnConnection(conn);}
		return company.getId();
	}
}
