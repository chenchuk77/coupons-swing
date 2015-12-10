package com.jbp.facade;

import java.sql.Date;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JOptionPane;

import com.jbp.beans.Company;
import com.jbp.beans.Coupon;
import com.jbp.beans.CouponType;
import com.jbp.db.dao.CompanyDAO;
import com.jbp.db.dao.CouponDAO;
import com.jbp.exceptions.CouponCreationException;
import com.jbp.exceptions.CouponRemovalException;
import com.jbp.exceptions.CouponUpdateException;
import com.jbp.facade.CouponClientFacade;
import com.jbp.main.ClientType;
import com.jbp.utils.*;

//
// Company Facade provides the business logic for companies
// DAO's exists for Companies/Coupons operations
//
public class CompanyFacade implements CouponClientFacade {
	private CompanyDAO companyDao;
	private CouponDAO couponDao;
	private final Company company;

	// returns the company
	public Company getCompany() {
		return this.company;
	}

	// set the Company DAO
	public void setCompanyDao(CompanyDAO companyDao) {
		this.companyDao = companyDao;
	}

	// set the Coupon DAO
	public void setCouponDao(CouponDAO couponDao) {
		this.couponDao = couponDao;
	}

	// c'tor refers to a company
	public CompanyFacade(Company company) {
		this.company = company;
	}

	@Override
	// not used
	public CouponClientFacade login(String name, String password,
			ClientType clientType) {
		return null;
	}

	// C.R.U.D operations may throws exceptions. all exception must be handled
	// in the client code ( GUI Windows )
	//
	public void createCoupon(Coupon coupon) throws CouponCreationException {
		couponDao.createCoupon(coupon);
	}

	public void removeCoupon(Coupon coupon) throws CouponRemovalException {
		couponDao.removeCoupon(coupon);
	}

	public void updateCoupon(Coupon coupon) throws CouponUpdateException {
		couponDao.updateCoupon(coupon);
	}

	// get all coupons of that company
	public Collection<Coupon> getAllCoupons() {
		return companyDao.getCoupons(this.getCompany().getId());
	}

	// get all and remove if coupon type is not the requested type
	public Coupon getCoupon(long id) {
		return couponDao.getCoupon(id);
	}
	
	public Collection<Coupon> getCouponById(long id) {
		Collection<Coupon> myCoupon = this.getAllCoupons();
		for (Iterator<Coupon> iterator = myCoupon.iterator(); iterator
				.hasNext();) {
			Coupon coupon = iterator.next();
			if (coupon.getId() != id) {
				iterator.remove();
			}
		}
		return myCoupon;
	}

	// get all and remove if coupon type is not the requested type
	public Collection<Coupon> getAllCouponsByType(CouponType couponType) {
		Collection<Coupon> allCoupons = this.getAllCoupons();
		for (Iterator<Coupon> iterator = allCoupons.iterator(); iterator
				.hasNext();) {
			Coupon coupon = iterator.next();
			if (coupon.getType() != couponType) {
				iterator.remove();
			}
		}
		return allCoupons;
	}

	// get all and remove if price is higher than requested
	public Collection<Coupon> getAllCouponsByMaxPrice(double price) {
		Collection<Coupon> allCoupons = this.getAllCoupons();
		for (Iterator<Coupon> iterator = allCoupons.iterator(); iterator
				.hasNext();) {
			Coupon coupon = iterator.next();
			if (coupon.getPrice() > price) {
				iterator.remove();
			}
		}
		return allCoupons;
	}

	// get all and remove if end date is greater than requested
	public Collection<Coupon> getAllCouponsByMaxDate(String date) {
		Collection<Coupon> allCoupons = this.getAllCoupons();
		Date maxDate = Utils.stringToSQLDate(date);
		if (maxDate != null) {
			for (Iterator<Coupon> iterator = allCoupons.iterator(); iterator
					.hasNext();) {
				Coupon coupon = iterator.next();
				if (coupon.getEndDate().after(maxDate)) {
					iterator.remove();
				}
			}
			return allCoupons;
		} else {
			JOptionPane.showMessageDialog(null, "Invalid Date Format !",
					"Error!", JOptionPane.ERROR_MESSAGE);
			return null;
		}
	}
}
