package com.jbp.utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

//
// This class used for global functions
// logging, config file parser, uptime calculator, md5 calculator
//
public class Utils {


	public static final String CONFIG_FILE = "cs.properties";
	//public static final String CONFIG_FILE = "cs.properties";
	//public static final String CONFIG_FILE = "WEB-INF/config/cs.properties";
	private static boolean loggingEnabled = true;
	public static Map<String, String> sysParams;
	// will be used to calculate system uptime
	public static long startTime = System.currentTimeMillis();


	// servlet will load cs.properties and pass the config map
	// ee
	public static void setConfigMap(Map<String, String> paramsFromServletLoader){
		sysParams = paramsFromServletLoader;
	}



	// globally enable/disable the logging
	// this parameters should be configured in cs.properties file
	public static void setLoggingEnabled(Boolean isLoggingEnabled){
		loggingEnabled = isLoggingEnabled;
	}

	// log message with timestamp, severity, and the calling object
	public static void logMessage(Object obj, Severity severity, String message){
		if(loggingEnabled){
			String callingClass;
			if(obj instanceof String){
				// this is used for static calls (cannot refer to class name, so passing the name as string )
				callingClass = obj.toString();
			} else {
				callingClass = obj.getClass().getSimpleName();
			}
			// short name will get \t\t . just for 'nice' output
			if (callingClass.length() < 8){
				callingClass += "\t";
			}
			System.out.println(getTimestamp() + "\t" + severity + "\t" + callingClass + "\t"+message);
		}
	}

	// method to get timeStamp
	public static String getTimestamp() {
		Calendar calendar = Calendar.getInstance();
		Timestamp currentTimestamp = new java.sql.Timestamp(calendar.getTime().getTime());
		return currentTimestamp.toString();
	}

	//	get a java.sql.Date object from a given String representation
	public static java.sql.Date stringToSQLDate(String date) {
		try {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			java.util.Date utilDate = df.parse(date);
			java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
			return sqlDate;
		}
		catch (ParseException e) {
			System.out.println(e.getMessage());
			return null;
		}
	}

	// get the MD5 hash for specific String, when admin login with pass=PASS
	// we calc MD5(PASS) and compare the hash against ADMIN_HASH
	// from properties file.
	// NOTE : MD5 algorithm implementation found on the net.
	public static String MD5(String clearText) {
		   try {
		        java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
		        byte[] array = md.digest(clearText.getBytes());
		        StringBuffer sb = new StringBuffer();
		        for (int i = 0; i < array.length; ++i) {
		          sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
		       }
		        return sb.toString();
		    } catch (java.security.NoSuchAlgorithmException e) {
		    }
		    return null;
		}

	// builds a hashmap from properties file
	//
	// this will be used for a local CouponSystem. if the system run on
	// web container, the LoaderServlet will be used to setup the params map
	// instead of using this method.
	public static void loadSystemParameters() {

		// method only to locate the current working directory

//		File f = new File("."); // current directory

//	    File[] files = f.listFiles();
//	    for (File file : files) {
//	        if (file.isDirectory()) {
//	            System.out.print("directory:");
//	        } else {
//	            System.out.print("     file:");
//	        }
//	        try {
//				System.out.println(file.getCanonicalPath());
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//	    }








		Utils.logMessage(new Utils(), Severity.DEBUG, "loadSystemParameters() invoked, file="+CONFIG_FILE);
		// construct a List<Customer> to return the data
		sysParams = new HashMap<String, String>();
		Properties properties = new Properties();
		try {
			properties.load(new FileReader(CONFIG_FILE));
			for(String propName : properties.stringPropertyNames()){
				sysParams.put(propName, properties.getProperty(propName));
			}
			Utils.logMessage(new Utils(), Severity.DEBUG, "properties from file loaded to a hashmap");
		} catch (IOException e) {
			Utils.logMessage(new Utils(), Severity.ERROR, "cannot load properties file ! exiting.");
			e.printStackTrace();
			//System.exit(0);
		}
	}

	// 2 accessors to the hashmap ( all / single )
	public static Map<String, String> getSystemParameters(){
		return sysParams;
	}

	// reply with a property value for any request
	public static String getProperty(String propName){
		return sysParams.get(propName);
	}

	// used to convert Map<s,s> to array[][] , needed for JTable
	public static Object[][] mapTo2dArray(Map<String, String> map){
		int rowNumber = 0;
		// unknown number of params , but only 2 columns (KVP)
		Object[][] parametersArray= new Object[map.keySet().size()][2];
		for(String key : map.keySet()){
			parametersArray[rowNumber][0] = key;
			parametersArray[rowNumber][1] = map.get(key);
			rowNumber++;
		}
		return parametersArray;
	}

	// get uptime ( now-start )
	public static Long getSystemUptime(){
		// calc running time, get rid of the msec
		return (System.currentTimeMillis() - startTime) / 1000 ;
	}
}

