package com.jbp.utils;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import com.jbp.beans.Coupon;
import com.jbp.db.dao.CouponDAO;
import com.jbp.exceptions.CouponRemovalException;
//
// TimerTask implements Runnable. this task will be scheduled from
// the CouponSystem singleton. the interval is defined in the properties
// file in minutes . ( 1440 minutes = 24 hours )
//

public class DailyCouponExpirationTask extends TimerTask {
	private CouponDAO couponDAO;

	// c'tor invoked with the couponDAO passed in
	public DailyCouponExpirationTask(CouponDAO couponDAO) {
		this.couponDAO = couponDAO;
	}

	// will be invoked by the time scheduler
	@Override
	public void run() {
		Utils.logMessage("ExpirationTask", Severity.INFO, "Daily thread started.");
		cleanOldCoupons();
		Utils.logMessage("ExpirationTask", Severity.INFO, "Daily thread finished.");
	}

	// method to search for old coupons and remove them from db
	private void cleanOldCoupons() {
		Collection<Coupon> oldCoupons = couponDAO.getOldCoupons();
		int numOfOldCoupons = oldCoupons.size();
		if (numOfOldCoupons <= 0) {
			Utils.logMessage("ExpirationTask", Severity.INFO, "no old coupons to remove. cleaner thread has nothing to do");
			return;
		} else {
			Utils.logMessage("ExpirationTask", Severity.INFO, numOfOldCoupons + " old coupons found.");
			for (Coupon coupon : oldCoupons) {
				try {
					couponDAO.removeCoupon(coupon);
					Utils.logMessage("ExpirationTask", Severity.DEBUG, "Coupon " + coupon.getId() + " was removed.");
				} catch (CouponRemovalException e) {
					Utils.logMessage("ExpirationTask", Severity.ERROR, "Coupon " + coupon.getId() + " could not removed.");
				}
			}
		}
	}
}
