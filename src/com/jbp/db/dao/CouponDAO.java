package com.jbp.db.dao;

import com.jbp.beans.*;
import com.jbp.exceptions.CouponCreationException;
import com.jbp.exceptions.CouponRemovalException;
import com.jbp.exceptions.CouponUpdateException;

import java.util.Collection;
// interface to define the operation needed for a Coupon object,
// decoupling functionality from implementation.
// implementation will have to deal with DB driver and SQL commands
//
public interface CouponDAO {
	public void createCoupon(Coupon coupon) throws CouponCreationException;
	public void removeCoupon(Coupon coupon) throws CouponRemovalException;
	public void updateCoupon(Coupon coupon) throws CouponUpdateException;
	public Coupon  getCoupon(long id);
	public Collection<Coupon> getAllCoupons();
	// passing an enum of CouponType
	public Collection<Coupon> getCouponsByType(CouponType couponType);
	
	// added : will be called from DailyTask thread
//	public int removeOldCoupons();
	public Collection<Coupon> getOldCoupons();

}
