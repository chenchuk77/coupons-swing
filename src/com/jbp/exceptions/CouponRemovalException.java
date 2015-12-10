package com.jbp.exceptions;

import com.jbp.beans.Coupon;

//exception may occur when trying to remove a specific coupon from DB

public class CouponRemovalException extends Exception {
	private Coupon coupon;
	
	public CouponRemovalException(Coupon coupon)
	{
		this.coupon = coupon;
	}
	
	public String getMessage()
	{
		return "Removing Coupon "+coupon.getTitle()+" failed !";
	}
}
