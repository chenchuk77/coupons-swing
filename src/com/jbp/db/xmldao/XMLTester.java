package com.jbp.db.xmldao;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class XMLTester {
	public static final String XML_CUSTOMER_FILE = "xml/customers.xml";
	
	public static void main(String[] args) {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder;
			docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(XML_CUSTOMER_FILE);
	
			Node root = doc.getFirstChild();
			System.out.println(root.getNodeName());
			Element cust = doc.createElement("Customer");
			
			Element custId = doc.createElement("ID");
			custId.appendChild(doc.createTextNode("2222"));
			cust.appendChild(custId);
			
			Element custName = doc.createElement("CUST_NAME");
			custName.appendChild(doc.createTextNode("moshon"));
			cust.appendChild(custName);

			Element custPassword = doc.createElement("CUST_PASSWORD");
			custPassword.appendChild(doc.createTextNode("1234qwer"));
			cust.appendChild(custPassword);
			
			// cust.appendChild(custId).
			// cust.appendChild(doc.createTextNode("moshon"));
			root.appendChild(cust);
			// Node node = root.appendChild(newChild)
			
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(XML_CUSTOMER_FILE));
			transformer.transform(source, result);

			System.out.println("Done");

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
