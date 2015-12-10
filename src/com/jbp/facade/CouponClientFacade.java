package com.jbp.facade;

import com.jbp.main.ClientType;
//
// common interface that provides login check functionality
// the concrete classes ( all Facade's ) must implement this method
//
public interface CouponClientFacade {
	// determine which login type (admin/user/company)
	// to return the correct Facade
	public CouponClientFacade login(String name, String password, ClientType clientType);
}
