package com.jbp.db.xmldao;

import java.io.IOException;
import java.util.Collection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.jbp.beans.Coupon;
import com.jbp.beans.Customer;
import com.jbp.db.ConnPool;
import com.jbp.db.dao.CustomerDAO;
import com.jbp.exceptions.CustomerCreationException;
import com.jbp.exceptions.CustomerRemovalException;
import com.jbp.exceptions.CustomerUpdateException;

//
// EXTENSION CLASS : IS NOT PART OF THE PROJECT.
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// it implements the CustomerDAO interface ( same functionality ), but the 
// underlying data storage as XML files instead of DB (in ~/xml/ folder ).
// its not fully implemented, it just shows how the DAOFactory can create any
// object that implements the DAO without changing the client code.
//
public class CustomerXMLDAO implements CustomerDAO {

	public static final String XML_CUSTOMER_FILE = "xml/customers.xml";
	
	public CustomerXMLDAO(ConnPool connPool) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createCustomer(Customer customer) throws CustomerCreationException {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder;
			docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(XML_CUSTOMER_FILE);
			Node root = doc.getFirstChild();
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
	}

	@Override
	public void removeCustomer(Customer customer)
			throws CustomerRemovalException {
		// TODO Auto-generated method stub
	}

	@Override
	public void updateCustomer(Customer customer)
			throws CustomerUpdateException {
		// TODO Auto-generated method stub
	}

	@Override
	public Customer getCustomer(long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Customer> getAllCustomers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Coupon> getCoupons(Customer customer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long login(String compName, String password) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getCustomerId(String custName) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void addCouponToCustomer(Coupon coupon, Customer customer) {
		// TODO Auto-generated method stub
	}
}
