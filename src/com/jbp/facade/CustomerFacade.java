package com.jbp.facade;

import java.util.*;

import com.jbp.beans.Coupon;
import com.jbp.beans.CouponType;
import com.jbp.beans.Customer;
import com.jbp.db.dao.CouponDAO;
import com.jbp.db.dao.CustomerDAO;
import com.jbp.exceptions.CouponNotAvailableException;
import com.jbp.exceptions.CouponUpdateException;
import com.jbp.facade.CouponClientFacade;
import com.jbp.main.ClientType;
import com.jbp.utils.*;

//
// Customer Facade provides the business logic for customers
// DAO's exists for Customers/Coupons operations
//
public class CustomerFacade implements CouponClientFacade {
	private CustomerDAO customerDao;
	private CouponDAO couponDao;
	private Customer customer;

	// default public c'tor
	public CustomerFacade() {
	}

	// return this customer
	public Customer getCustomer() {
		return this.customer;
	}

	// set the Facade for this customer
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	// set the Customer DAO
	public void setCustomerDao(CustomerDAO customerDao) {
		this.customerDao = customerDao;
	}

	// set the Coupon DAO
	public void setCouponDao(CouponDAO couponDao) {
		this.couponDao = couponDao;
	}

	@Override
	// not in use, but needs to be implemented
	public CouponClientFacade login(String name, String password,	ClientType clientType) {
			return null;
	}


	// TO REMOVE
	//
	// added to check JAX-RS returning only 1 coupon
	public Coupon getCouponById(long id){
		return couponDao.getCoupon(id);
	}





	// get all my Coupons
	public Collection<Coupon> getAllPurchasedCoupons() {
		return customerDao.getCoupons(this.customer);
	}

	// get all and remove by comparing the CouponType
	// Iterator.remove is the only safe way to modify a collection during iteration
	public Collection<Coupon> getAllPurchasedCouponsByType(CouponType couponType) {
		Collection<Coupon> allMyCoupons = customerDao.getCoupons(this.customer);
		for (Iterator<Coupon> iterator = allMyCoupons.iterator(); iterator.hasNext();) {
		    Coupon coupon = iterator.next();
		    if (coupon.getType() != couponType){
		        iterator.remove();
		    }
		}
		return allMyCoupons;
	}

	// get all and remove if price is higher than requested
	public Collection<Coupon> getAllPurchasedCouponsByPrice(double price) {
		Collection<Coupon> allMyCoupons = customerDao.getCoupons(this.customer);
		for (Iterator<Coupon> iterator = allMyCoupons.iterator(); iterator.hasNext();) {
		    Coupon coupon = iterator.next();
		    if (coupon.getPrice() > price){
		        iterator.remove();
		    }
		}
		return allMyCoupons;
	}

	// coupon i can buy (those who are not mine already ) and ( amount > 0 )
	public Collection<Coupon> getAllAvailableCoupons(){
		// getting all coupons in system
		Collection<Coupon> allCoupons = couponDao.getAllCoupons();
		// get  my coupons
		Collection<Coupon> myCoupons = customerDao.getCoupons(this.customer);
		// Subtract and store in a new var notMyCoupons
		allCoupons.removeAll(myCoupons);
		Collection<Coupon> notMyCoupons = allCoupons;
		for(Iterator<Coupon> iterator = notMyCoupons.iterator() ; iterator.hasNext() ;){
			Coupon coupon = iterator.next();
			// remove from list if out of stock
			if (coupon.getAmount() < 1){
				iterator.remove();
			}
		}
		return notMyCoupons;
	}

	// filter list of all available coupon to purchase
	public Collection<Coupon> getAllAvailableCouponsByType(CouponType couponType){
		Collection<Coupon> allAvailableCoupons = getAllAvailableCoupons();
		// list to populate with the matching coupons
		for(Iterator<Coupon> iterator = allAvailableCoupons.iterator() ; iterator.hasNext() ;){
			Coupon coupon = iterator.next();
			if (coupon.getType() != couponType){
				iterator.remove();
			}
		}
		return allAvailableCoupons;
	}

	// filter list of all available coupon to purchase
	public Collection<Coupon> getAllAvailableCouponsByPrice(double price) {
		Collection<Coupon> allAvailableCoupons = getAllAvailableCoupons();
		// list to populate with the matching coupons
		for(Iterator<Coupon> iterator = allAvailableCoupons.iterator() ; iterator.hasNext() ;){
			Coupon coupon = iterator.next();
			if (coupon.getPrice() > price){
				iterator.remove();
			}
		}
		return allAvailableCoupons;
	}



	// purchase has 2 steps :
	// 1 - check if more than 1 available
	// 2 - check that this customer doesnt already owned this coupon
	// 3 - update amount in DB ( -- )
	public void purchaseCoupon(Coupon coupon) throws CouponNotAvailableException, CouponUpdateException {
		// getting realtime coupon amount from DB
		Coupon couponFromDB = couponDao.getCoupon(coupon.getId());

		if (couponFromDB == null){
			throw new CouponNotAvailableException(coupon);
		}
		if (couponFromDB.getAmount() <= 0){
			Utils.logMessage(this, Severity.ERROR, "coupon amount is 0.");
			throw new CouponNotAvailableException(coupon);
		}
		// and not purchased already
		if (getAllPurchasedCoupons().contains(couponFromDB)) {
			Utils.logMessage(this, Severity.ERROR, "coupon already owned by customer " + getCustomer().getCustName());
			throw new CouponNotAvailableException(coupon);
		}
		// purchase
		customerDao.addCouponToCustomer(couponFromDB, this.customer);
		Utils.logMessage(this, Severity.INFO, "coupon purchased by customer " + getCustomer().getCustName());
		// decrease amount
		couponFromDB.setAmount(couponFromDB.getAmount() - 1);
		couponDao.updateCoupon(couponFromDB);
		Utils.logMessage(this, Severity.INFO, "coupon new amount updated in db " + getCustomer().getCustName());
	}
}
