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
import com.jbp.db.dao.CouponDAO;
import com.jbp.exceptions.CouponCreationException;
import com.jbp.utils.*;

//
// CouponDBDAO class implements CouponDAO interface.
// Provide's methods to insert and retrieve data from and to the DB.
//

public class CouponDBDAO implements CouponDAO {
	// reference to the pool for this DAO
	private ConnPool connPool;

	// constructor, must get a ConnPool object
	// c'tor initialize the pool for this DAO
	public CouponDBDAO(ConnPool connPool) {
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
	
	//	uses to create new coupon obj and insert it into the DB
	@Override
	public void createCoupon(Coupon coupon) throws CouponCreationException {
		Connection conn = connPool.getConnection();
		try {
			// add a new Coupon into Coupon table
			String query = "INSERT INTO Coupons "
					+ "(TITLE, START_DATE, END_DATE, AMOUNT, "
					+ "TYPE, MESSAGE, PRICE, IMAGE, COMPANY_ID) "
					+ "VALUES (?, ? ,? ,? ,? ,? ,?, ?, ?)";
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setString(1, coupon.getTitle());
			pstmt.setDate(2, coupon.getStartDate());
			pstmt.setDate(3, coupon.getEndDate());
			pstmt.setInt(4, coupon.getAmount());
			pstmt.setString(5, coupon.getType().toString());
			pstmt.setString(6, coupon.getMessage());
			pstmt.setDouble(7, coupon.getPrice());
			pstmt.setString(8, coupon.getImage());
			pstmt.setLong(9, coupon.getCompanyId());
			pstmt.executeUpdate();
			// retrieve its coupon ID
			long id = 0;
			query = "SELECT ID FROM Coupons WHERE TITLE=?";
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, coupon.getTitle());
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				id = rs.getLong("ID");
			}
			// add an entry into Company_Coupon table
			query = "INSERT INTO Company_Coupon (COMP_ID,COUPON_ID) VALUES(?, ?)";
			pstmt = conn.prepareStatement(query);
			pstmt.setLong(1, coupon.getCompanyId());
			pstmt.setLong(2, id);
			pstmt.executeUpdate();
			pstmt.close();
			Utils.logMessage(this, Severity.INFO,
					"coupon created : " + coupon.getTitle() + ".");
		} catch (SQLException e) {
			Utils.logMessage(this, Severity.ERROR, "cannot create coupon : "+ e.getMessage());
			throw new CouponCreationException(coupon);
		} finally {
			connPool.returnConnection(conn);
		}
	}

	// this method remove a coupon from 3 possible tables :
	// customer_coupon (if customer purchase any)
	// company_coupon
	// coupon
	@Override
	public void removeCoupon(Coupon coupon) {
		Connection conn = connPool.getConnection();
		try {
			String query = "DELETE FROM Customer_Coupon WHERE COUPON_ID=?;"
					+ "DELETE FROM Company_Coupon WHERE COUPON_ID=?;"
					+ "DELETE FROM Coupons WHERE ID=?";
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setLong(1, coupon.getId());
			pstmt.setLong(2, coupon.getId());
			pstmt.setLong(3, coupon.getId());
			pstmt.executeUpdate();
			pstmt.close();
			Utils.logMessage(this, Severity.INFO,
					"coupon removed from 3 tables.");
		} catch (SQLException e) {
			Utils.logMessage(this, Severity.ERROR, "cannot remove coupon : "
					+ e.getMessage());
		} finally {
			connPool.returnConnection(conn);
		}
	}

	// this method allows to update all changeable attributes for a coupon 
	@Override
	public void updateCoupon(Coupon coupon) {
		Connection conn = connPool.getConnection();
		try {
			String query = "UPDATE Coupons SET END_DATE=?, PRICE=?, AMOUNT=?, MESSAGE=?, IMAGE=? WHERE ID=?";
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setDate(1, coupon.getEndDate());
			pstmt.setDouble(2, coupon.getPrice());
			pstmt.setInt(3, coupon.getAmount());
			pstmt.setString(4, coupon.getMessage());
			pstmt.setString(5, coupon.getImage());
			pstmt.setLong(6, coupon.getId());
			pstmt.executeUpdate();
			pstmt.close();
			Utils.logMessage(this, Severity.INFO, "coupon " + coupon.getTitle()+ " updated.");
		} catch (SQLException e) {
			Utils.logMessage(this, Severity.ERROR, "cannot update coupon : "+ e.getMessage());
		} finally {
			connPool.returnConnection(conn);
		}
	}

	// method to get a specific coupon from DB
	@Override
	public Coupon getCoupon(long id) {
		// constructing a Coupon to return
		Coupon coupon = new Coupon();
		Connection conn = connPool.getConnection();
		try {
			String query = "SELECT * FROM Coupons WHERE ID=?";
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setLong(1, id);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
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
			}
			pstmt.close();
			Utils.logMessage(this, Severity.INFO, "get coupon returned.");
		} catch (SQLException e) {
			Utils.logMessage(this, Severity.ERROR, "cannot get coupon : "+ coupon.getId() + ". " + e.getMessage());
		} finally {
			connPool.returnConnection(conn);
		}
		return coupon;
	}

	// method to get all the coupons from the DB
	@Override
	public Collection<Coupon> getAllCoupons() {
		// constructing a List<Coupon> to return
		List<Coupon> allCoupons = new ArrayList<Coupon>();
		Connection conn = connPool.getConnection();
		try {
			String query = "SELECT * FROM Coupons";
			PreparedStatement pstmt = conn.prepareStatement(query);
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
				// add to list
				allCoupons.add(coupon);
			}
			pstmt.close();
			Utils.logMessage(this, Severity.INFO, "get all coupon returned.");
		} catch (SQLException e) {
			Utils.logMessage(this, Severity.ERROR, "cannot get all coupons : "+ e.getMessage());
		} finally {
			connPool.returnConnection(conn);
		}
		return allCoupons;
	}

	// method to get all coupons of the same type from DB
	// return a collection of coupons of the requested type
	@Override
	public Collection<Coupon> getCouponsByType(CouponType couponType) {
		// constructing a List<Coupon> to return
		List<Coupon> allCouponsByType = new ArrayList<Coupon>();
		Connection conn = connPool.getConnection();
		try {
			String query = "SELECT * FROM Coupons WHERE TYPE=?";
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setString(1, couponType.toString());
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
				// add to list
				allCouponsByType.add(coupon);
			}
			pstmt.close();
			Utils.logMessage(this, Severity.INFO, "coupons by type "+ couponType + " returned.");
		} catch (SQLException e) {
			Utils.logMessage(this, Severity.ERROR,"cannot get all coupons by type : " + e.getMessage());
		} finally {
			connPool.returnConnection(conn);
		}
		return allCouponsByType;
	}

	// method that gets id's of coupons which have expired
	// and returns a list of those coupons
	public List<Coupon> getOldCoupons() {
		// constructing a List<Coupon> to return
		List<Coupon> oldCoupons = new ArrayList<Coupon>();
		Connection conn = connPool.getConnection();
		try {
			String query = "SELECT ID FROM Coupons WHERE DATE(NOW())>END_DATE";
			PreparedStatement pstmt = conn.prepareStatement(query);
			ResultSet rs = pstmt.executeQuery();
			Coupon coupon = null;
			while (rs.next()) {
				coupon = new Coupon();
				coupon.setId(rs.getLong("ID"));
				oldCoupons.add(coupon);
			}
			pstmt.close();
			Utils.logMessage(this, Severity.INFO, "old coupons returned.");
		} catch (SQLException e) {
			Utils.logMessage(this, Severity.ERROR, "cannot get old coupons : "+ e.getMessage());
		} finally {
			connPool.returnConnection(conn);
		}
		return oldCoupons;
	}
}
