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
import com.jbp.db.dao.CustomerDAO;
import com.jbp.exceptions.CustomerCreationException;
import com.jbp.exceptions.CustomerRemovalException;
import com.jbp.exceptions.CustomerUpdateException;
import com.jbp.utils.*;

//
// CustomerDBDAO class implements CustomerDAO interface.
// Provide's methods to insert and retrieve data from and to the DB.
//

public class CustomerDBDAO implements CustomerDAO {
	// reference to the pool for this DAO
	private ConnPool connPool;

	// constructor, must get a ConnPool object.
	// c'tor initialize the pool for this DAO
	public CustomerDBDAO(ConnPool connPool) {
		this.connPool = connPool;
	}

	// all methods access the db in the same way :
	// 1. get connection from pool
	// 2. run a SQL query/update using prepareStatement for safety
	// SELECT queries construct an object from the returned ResultSet
	// INSERT/DELETE/UPDATE update the DB
	// 3. print the log message according to the action performed
	// 4. return the used connection into the connection pool
	//
	
	//	uses to create new customer obj and insert it into the DB
	@Override
	public void createCustomer(Customer customer)
			throws CustomerCreationException {
		Connection conn = connPool.getConnection();
		try {
			String query = "INSERT INTO Customers (CUST_NAME, PASSWORD) VALUES (?, ?)";
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setString(1, customer.getCustName());
			pstmt.setString(2, customer.getPassword());
			pstmt.executeUpdate();
			pstmt.close();
			Utils.logMessage(this, Severity.INFO, "customer created : "
					+ customer.getCustName() + ".");
		} catch (SQLException e) {
			Utils.logMessage(this, Severity.ERROR, "cannot create customer : "	+ e.getMessage());
			throw new CustomerCreationException(customer);
		} finally {
			connPool.returnConnection(conn);
		}
	}

	@Override
	// this method remove's a customer from the DB
	// the method impact 2 possible tables -
	// 1. customer_coupon (if own's any coupons)
	// 2. customer
	// modified. 2 SQL queries at once (need an explicit option in JDBC string ,
	// see ConnPool)
	public void removeCustomer(Customer customer)
			throws CustomerRemovalException {
		Connection conn = connPool.getConnection();
		try {
			String query = "DELETE FROM Customers WHERE ID=?; DELETE FROM Customer_Coupon WHERE CUST_ID=?";
			PreparedStatement pstmt = conn.prepareStatement(query);
			// deleting from 2 tables (2 sql queries)
			pstmt.setLong(1, customer.getId());
			pstmt.setLong(2, customer.getId());
			pstmt.executeUpdate();
			pstmt.close();
			Utils.logMessage(this, Severity.INFO,
					"customer " + customer.getCustName()
							+ " removed from Customers table.");
			Utils.logMessage(this, Severity.INFO,
					"customer " + customer.getCustName()
							+ " removed from Customer_Coupon table.");
		} catch (SQLException e) {
			Utils.logMessage(this, Severity.ERROR, "cannot delete customer : "
					+ customer.getCustName() + ". " + e.getMessage());
		} finally {
			connPool.returnConnection(conn);
		}
	}

	@Override
	// method to update any of the customer's changeable attributes
	public void updateCustomer(Customer customer)
			throws CustomerUpdateException {
		Connection conn = connPool.getConnection();
		try {
			String query = "UPDATE Customers SET CUST_NAME=?, PASSWORD=? WHERE CUST_NAME=?";
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setString(1, customer.getCustName());
			pstmt.setString(2, customer.getPassword());
			pstmt.setString(3, customer.getCustName());
			pstmt.executeUpdate();
			pstmt.close();
			Utils.logMessage(this, Severity.INFO, "customer updated : "
					+ customer.getCustName() + ".");
		} catch (SQLException e) {
			Utils.logMessage(this, Severity.ERROR, "cannot update customer : "
					+ customer.getCustName() + ". " + e.getMessage());
		} finally {
			connPool.returnConnection(conn);
		}
	}

	// method to get a specific customer frpm DB
	@Override
	public Customer getCustomer(long id) {
		// construct a Customer to return data
		Customer customer = new Customer();
		Connection conn = connPool.getConnection();
		try {
			String query = "SELECT * FROM Customers WHERE ID=?";
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setLong(1, id);
			ResultSet rs = pstmt.executeQuery();
			// if line return : its a customer record from DB.
			while (rs.next()) {
				customer.setId(rs.getLong("ID"));
				customer.setCustName(rs.getString("CUST_NAME"));
				customer.setPassword(rs.getString("PASSWORD"));
			}
			pstmt.close();
			Utils.logMessage(this, Severity.INFO, "customer returned : "
					+ customer.getCustName() + ".");
		} catch (SQLException e) {
			Utils.logMessage(this, Severity.ERROR, "cannot get customer : "
					+ customer.getCustName() + ". " + e.getMessage());
		} finally {
			connPool.returnConnection(conn);
		}
		return customer;
	}

	// method to get all customers from DB
	// return's an ArrayList of customer objects
	@Override
	public Collection<Customer> getAllCustomers() {
		// construct a List<Customer> to return the data
		List<Customer> allCustomers = new ArrayList<Customer>();
		Connection conn = connPool.getConnection();
		try {
			String query = "SELECT * FROM Customers";
			PreparedStatement pstmt = conn.prepareStatement(query);
			ResultSet rs = pstmt.executeQuery();
			Customer customer = null;
			while (rs.next()) {
				customer = new Customer();
				customer.setId(rs.getLong("ID"));
				customer.setCustName(rs.getString("CUST_NAME"));
				customer.setPassword(rs.getString("PASSWORD"));
				allCustomers.add(customer);
			}
			pstmt.close();
			Utils.logMessage(this, Severity.INFO,
					"customers list returned successfully.");
		} catch (SQLException e) {
			Utils.logMessage(this, Severity.ERROR,
					"cannot get all customers : " + e.getMessage());
		} finally {
			connPool.returnConnection(conn);
		}
		return allCustomers;
	}

	// method to get all coupons for a specific customer from DB
	// method return's an ArrayList of coupon objects
	@Override
	public Collection<Coupon> getCoupons(Customer customer) {
		Utils.logMessage(
				this,
				Severity.DEBUG,
				"going to get coupons from this customer : "
						+ customer.getCustName());
		// construct a List<Coupon> to return the data
		List<Coupon> myCoupons = new ArrayList<Coupon>();
		Connection conn = connPool.getConnection();
		try {
			String query = "SELECT CPN.* FROM Coupons CPN "
					+ "JOIN Customer_Coupon JCS "
					+ "ON CPN.ID = JCS.COUPON_ID " + "WHERE JCS.CUST_ID = ?";
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setLong(1, customer.getId());
			ResultSet rs = pstmt.executeQuery();
			Coupon coupon = null;
			while (rs.next()) {
				coupon = new Coupon();
				coupon.setId(rs.getLong("ID"));
				coupon.setTitle(rs.getString("TITLE"));
				coupon.setStartDate(Utils.stringToSQLDate(rs
						.getString("START_DATE")));
				coupon.setEndDate(Utils.stringToSQLDate(rs
						.getString("END_DATE")));
				coupon.setAmount(rs.getInt("AMOUNT"));
				coupon.setType(CouponType.valueOf(rs.getString("TYPE")));
				coupon.setMessage(rs.getString("MESSAGE"));
				coupon.setPrice(rs.getDouble("PRICE"));
				coupon.setImage(rs.getString("IMAGE"));
				coupon.setCompanyId(rs.getLong("COMPANY_ID"));
				// add to list
				myCoupons.add(coupon);
			}
			pstmt.close();
			Utils.logMessage(this, Severity.INFO, "coupons list returned : "
					+ myCoupons.size() + " for customer " + customer.getId()
					+ ".");
		} catch (SQLException e) {
			Utils.logMessage(this, Severity.ERROR, "cannot get all coupons. "
					+ e.getMessage());
		} finally {
			connPool.returnConnection(conn);
		}
		return myCoupons;
	}

	// this method will look for auth for the customer name and password combination
	// in the customers table.
	// if name and password match, returns the customer ID 
	@Override
	public Long login(String custName, String password) {
		// to return the data
		Long customerId = null;
		Connection conn = connPool.getConnection();
		try {
			String query = "SELECT ID FROM Customers WHERE CUST_NAME=? AND PASSWORD=?";
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setString(1, custName);
			pstmt.setString(2, password);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				// if auth success, the ID returned
				customerId = rs.getLong(1);
			}
			pstmt.close();
			Utils.logMessage(this, Severity.INFO, "login ok customer : ["
					+ customerId + "]  -  " + custName + ".");
		} catch (SQLException e) {
			Utils.logMessage(this, Severity.ERROR,
					"cannot login : " + e.getMessage());
		} finally {
			connPool.returnConnection(conn);
		}
		return customerId;
	}

	// method to get customer ID from DB by customer name
	@Override
	public long getCustomerId(String custName) {
		// construct a Customer to return data
		Customer customer = new Customer();
		Connection conn = connPool.getConnection();
		try {
			String query = "SELECT ID FROM Customers WHERE CUST_NAME=?";
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setString(1, custName);
			ResultSet rs = pstmt.executeQuery();
			// if a line returns : its a customer record from DB.
			while (rs.next()) {
				customer.setId(rs.getLong("ID"));
			}
			pstmt.close();
			Utils.logMessage(this, Severity.INFO, "customer  " + custName
					+ " : ID " + customer.getId() + " returned from db.");
		} catch (SQLException e) {
			Utils.logMessage(this, Severity.ERROR, "cannot get customer id of "
					+ custName + ". " + e.getMessage());
		} finally {
			connPool.returnConnection(conn);
		}
		return customer.getId();
	}

	// method to add a coupon to customer in DB
	// impact customer_coupon table
	@Override
	public void addCouponToCustomer(Coupon coupon, Customer customer) {
		Connection conn = connPool.getConnection();
		try {
			String query = "INSERT INTO Customer_Coupon (CUST_ID, COUPON_ID) VALUES (?, ?)";
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setLong(1, customer.getId());
			pstmt.setLong(2, coupon.getId());
			pstmt.executeUpdate();
			Utils.logMessage(this, Severity.DEBUG,
					"record created cust/coupon: " + customer.getId() + "/"
							+ coupon.getId() + ".");
			pstmt.close();
			Utils.logMessage(this, Severity.INFO, "coupon purchased by "
					+ customer.getCustName() + ".");
		} catch (SQLException e) {
			Utils.logMessage(this, Severity.ERROR, "cannot create customer : "
					+ e.getMessage());
		} finally {
			connPool.returnConnection(conn);
		}
	}
}
