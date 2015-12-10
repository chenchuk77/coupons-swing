package com.jbp.beans;

import java.io.Serializable;
import java.util.*;

import javax.swing.JOptionPane;
import javax.xml.bind.annotation.XmlRootElement;

//
// this class used for company type objects
//
@XmlRootElement
public class Company implements Serializable
{
	
	private static final long serialVersionUID = 1L;
	private long id;
	private String compName;
	private String password;
	private String email;
	private Collection<Coupon> coupons;

	// added for simple call when no object needed
	public Company(){
	}

	// use for a full constructor
	public Company(long id, String compName, String password, String email){
		this.id=id;
		this.compName=compName;
		setPassword(password);
		setEmail(email);
	}

	// used to get company's id
	public long getId() {
		return id;
	}

	// added to set id when creating a new object
	public void setId(long id) {
		this.id= id;
	}

	// use to get company's name
	public String getCompName() {
		return compName;
	}

	// use to set comany's name
	public void setCompName(String compName) {
		this.compName = compName;
	}

	// use to get company's password
	public String getPassword() {
		return password;
	}

	// use to set the company's password
	public void setPassword(String password) {
		this.password = password;
	}

	// use to get company's email
	public String getEmail() {
		return email;
	}

	// use to set company's email
	public void setEmail(String email) {
		// throws exception when get company by id
//		if (email.contains("@"))
			this.email = email;
//		else JOptionPane.showMessageDialog(null, "illegal E-Mail address", "notice!", 1);
	}

	// use to get all company's coupons
	public Collection<Coupon> getCoupons() {
		// ???? return Collections.unmodifiableCollection(coupons);
		return coupons;
	}

	// use to add a simple coupon to the company
	public void addCoupon(Coupon c) {
		this.coupons.add(c);
	}


	// tostring use to display company's details
	public String toString() {
		return "Id:"+getId()+", Name:"+getCompName()+", Password:"+getPassword()+", email:"+getEmail()+".";
	}

	public void setCoupons(Collection<Coupon> coupons) {
		this.coupons = coupons;
	}


}
