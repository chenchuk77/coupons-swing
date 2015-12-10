package com.jbp.exceptions;

import com.jbp.beans.Coupon;

//exception may occur when trying to update a specific coupon from DB

public class CouponUpdateException extends Exception {
	private Coupon coupon;
	
	public CouponUpdateException(Coupon coupon)
	{
		this.coupon = coupon;
	}
	
	public String getMessage()
	{
		return "Updating Coupon "+coupon.getTitle()+" failed !";
	}
}
