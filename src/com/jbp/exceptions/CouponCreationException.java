package com.jbp.exceptions;

import com.jbp.beans.Coupon;

// exception handler for an exception which may occur during company object creation
// and it's DB registration

public class CouponCreationException extends Exception 
{
	private Coupon coupon;
	
	public CouponCreationException(Coupon coupon)
	{
		this.coupon = coupon;
	}
	
	public String getMessage()
	{
		return "Creating coupon "+coupon.getTitle()+" failed !";
	}
}
