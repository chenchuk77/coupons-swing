package com.jbp.beans;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

import javax.xml.bind.annotation.XmlRootElement;

//
// this class used for customer type objects
//
@XmlRootElement
public class Customer implements Serializable
{
	
	private static final long serialVersionUID = 1L;
	private long id;
	private String custName;
	private String password;
	private Collection<Coupon> coupons;

	// used for full constructor
	public Customer(long id, String custName, String password){
		this.id=id;
		this.custName=custName;
		this.password=password;
	}

	// default constructor
	public Customer() {}

	// getting customer's ID
	public long getId() {
		return id;
	}

	// setting customer's ID
	public void setId(long id) {
		this.id = id;
	}

	// getting customer's name
	public String getCustName() {
		return custName;
	}

	// setting customer's name
	public void setCustName(String castName) {
		this.custName = castName;
	}

	// getting customer's password
	public String getPassword() {
		return password;
	}

	// setting customer's password
	public void setPassword(String password) {
		this.password = password;
	}

	public Collection<Coupon> getCoupons() {
		// ??? return Collections.unmodifiableCollection(coupons);
		return coupons;
	}

	// tostring to get customer's details
	public String toString(){
		return "customer[id=" + getId() +
										", name=" + getCustName() +
										", password=" + getPassword() +
										", coupons="+ getCoupons() +
										"].";
	}

	public void setCoupons(Collection<Coupon> coupons) {
		this.coupons = coupons;
	}


}
