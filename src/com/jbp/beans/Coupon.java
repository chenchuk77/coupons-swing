package com.jbp.beans;

import java.io.Serializable;
import java.sql.Date;

import javax.xml.bind.annotation.XmlRootElement;

//
// this class used for coupon type objects
//
@XmlRootElement
public class Coupon implements Serializable
{


	private static final long serialVersionUID = 1L;
	private long id;
	private String title;
	private Date startDate;
	private Date endDate;
	private int amount;
	private CouponType type;
	private String message;
	private double price;
	private String image;
	private long companyId;


	// default constructor
	public Coupon(){}

	// second option to establish constructor, using only part of the variables
	public Coupon(String title, Date startDate, Date endDate, int amount,
			CouponType type, double price) {
		this.title = title;
		this.startDate = startDate;
		this.endDate = endDate;
		this.amount = amount;
		this.type = type;
		this.price = price;
	}

	// third option to establish constructor, using all variables
	public Coupon(long id, String title, Date startDate, Date endDate, int amount,
			CouponType type,String message, double price, String image, long companyId) {
		this.id = id;
		this.title = title;
		this.startDate = startDate;
		this.endDate = endDate;
		this.amount = amount;
		this.type = type;
		this.message = message;
		this.price = price;
		this.image = image;
		this.companyId = companyId;
	}


//	public long getCompId() {
//		return companyId;
//	}

//	public void setCompId(long compId) {
//		this.companyId = compId;
//	}

	// getting the coupon's start date
	public Date getStartDate() {
		return startDate;
	}

	// setting the coupon's start date
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	// getting the coupon's end date
	public Date getEndDate() {
		return endDate;
	}

	// setting the coupon's end date
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	// getting the amount of coupon's
	public int getAmount() {
		return amount;
	}

	// setting the amount of coupon's
	public void setAmount(int amount) {
		this.amount = amount;
	}

	// getting coupon's message
	public String getMessage() {
		return message;
	}

	// setting coupon's message
	public void setMessage(String message) {
		this.message = message;
	}

	// getting coupon's price
	public double getPrice() {
		return price;
	}

	// setting coupon's price
	public void setPrice(double price) {
		this.price = price;
	}

	// getting coupon's image
	public String getImage() {
		return image;
	}

	// setting coupon's image
	public void setImage(String image) {
		this.image = image;
	}

	// getting couopn's ID
	public long getId() {
		return id;
	}

	// setting couopn's ID
	public void setId(long id) {
		this.id = id;
	}

	// setting coupon's title
	public void setTitle(String title) {
		this.title = title;
	}

	// setting coupon's type
	public void setType(CouponType type) {
		this.type = type;
	}

	// getting coupon's title
	public String getTitle() {
		return title;
	}

	// getting coupon's type
	public CouponType getType() {
		return type;
	}

	// getting coupon's ID
	public long getCompanyId() {
		return companyId;
	}

	// setting coupon's company ID
	// used to print company ID in the coupon object
	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}

	// tostring to display coupon's details
	public String toString() {
		return "Id:"+getId()+
				", Title:"+getTitle()+
				", Type:"+getType()+
				", Start:"+getStartDate()+
				", End:"+getEndDate()+
				", Amount:"+getAmount()+
				", Price:"+getPrice();
	}


//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + ((title == null) ? 0 : title.hashCode());
//		return result;
//	}


	// used to compare 2 coupon type objects
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Coupon other = (Coupon) obj;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}


}
