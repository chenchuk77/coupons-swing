package com.jbp.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

//import _connpool.Conn;

import com.jbp.utils.Severity;
import com.jbp.utils.Utils;

// Singleton class to manage connections.
// it uses a Map of <conn><bool> (true=free conn , false=conn in use)
// private c'tor builds a Map of NUMBER_OF_CONNS entries of 'true' ( new free conns )
// 2 sync methods (get/return) using wait/notify to signal the DAO (threads)
//
public class ConnPool {

	public static int connId = 0;
	public static final String DBURL = Utils.getProperty("DB_URL");
	public static final String USER = Utils.getProperty("DB_USERNAME");
	public static final String PASSWORD = Utils.getProperty("DB_PASSWORD");
	public static final int MAX_DB_CONNECTIONS = Integer.parseInt(Utils.getProperty("MAX_DB_CONNECTIONS"));

	private Set<Connection> listConns;

	public static ConnPool instance = new ConnPool();
	// map to save connection and its current status as a table ( conn name , is
	// available)
	private Map<Connection, Boolean> allConns = new HashMap<Connection, Boolean>();

	// hiding the default c'tor (singleton)
	private ConnPool() {
		Utils.logMessage(this, Severity.DEBUG, "private c'tor invoked for this singleton.");
		listConns = new HashSet<Connection>();
		for (int i = 1; i <= MAX_DB_CONNECTIONS; i++) {
			Connection conn = createConnection();
			// add to Map
			allConns.put(conn, true);
			// add also to List (just for output the status of pool)
			listConns.add(conn);
		}
		Utils.logMessage(this, Severity.INFO,
				"singleton c'tor finished. Total of " + allConns.size() + " DB Connections.");
	}

	private Connection createConnection() {
		Connection conn = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(DBURL, USER, PASSWORD);
			Utils.logMessage(this, Severity.DEBUG, "Connection object created. connId=" + ++connId);
		} catch (SQLException e) {
			Utils.logMessage(this, Severity.PANIC, "cannot get connection from driver." + e.getMessage());
			System.exit(0);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return conn;
	}

	// (singleton) - if no instance , create it only once.
	public synchronized static ConnPool getInstance() {
		// instance should never be null ? , creating by class loader upon
		// loading the program
		if (instance == null) {
			synchronized (DBURL) {
				if (instance == null) {
					instance = new ConnPool();
				}
			}
		}
		return instance;
	}

	public synchronized Connection getConnection() {
		// this method blocks until a free connection exist. if not - thread
		// will 'wait'. when notified : will do the while-loop again
		while (true) {
			for (Connection conn : allConns.keySet()) {
				// if free conn exists
				if (allConns.get(conn) == true) {
					try {
						if (conn.isClosed()) {
							allConns.remove(conn);
							listConns.remove(conn);
							Utils.logMessage(this, Severity.INFO, "closed connection removed from map. map contains "+allConns.size() +" conns");
							conn = createConnection();

							// adding new conn to map, tagged as false because
							// it will be in use
							listConns.add(conn);
							allConns.put(conn, false);
							Utils.logMessage(this, Severity.INFO, "new connection provided by pool. free connections "
									+ numOfFreeConns() + "/" + MAX_DB_CONNECTIONS);
						} else {

							Utils.logMessage(this, Severity.INFO, "valid connection provided by pool. free connections "
									+ numOfFreeConns() + "/" + MAX_DB_CONNECTIONS);
							allConns.put(conn, false);
						}

					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					return conn;
				}
				try {
					// wait if no free connections. after being notified :
					// will iterate the while again and MAY find free conn ( or
					// wait again )
					wait();
				} catch (InterruptedException e) {
					e.getMessage();
				}
			}
		}
	}

	// returning a connection to pull and notify all 'waiters'
	public synchronized void returnConnection(Connection conn) {
		// put back to pool
		allConns.put(conn, true);
		Utils.logMessage(this, Severity.INFO,
				"connection returned to pool. free connections " + numOfFreeConns() + "/" + MAX_DB_CONNECTIONS);
		// Utils.logMessage(this, Severity.DEBUG, this.toString());
		notifyAll();
	}

	// close the pool
	public void closeAllConnections() throws SQLException {
		Utils.logMessage(this, Severity.INFO, "requested to close all " + allConns.size() + " connections");
		for (Connection conn : allConns.keySet()) {
			conn.close();
		}
		Utils.logMessage(this, Severity.INFO, "all connetions closed.");
	}

	// check for the amount of free conn at the current time
	private int numOfFreeConns() {
		int i = 0;
		for (Connection conn : allConns.keySet()) {
			if (allConns.get(conn) == true) {
				i++;
			}
		}
		return i;
	}

	private boolean isFree(Connection conn) {
		return allConns.get(conn);
	}

	// toString can be used to show connections distribution in pool
	// ie. "[-] [-] [+]" will be returned if conn#3 is requested/returned
	@Override
	public String toString() {
		// - : free connection
		// + : in use
		StringBuilder sb = new StringBuilder();
		Iterator iter = listConns.iterator();
		while (iter.hasNext()) {
			sb.append("[" + ((allConns.get(iter.next())) ? "-" : "+") + "] ");
		}
		//sdsdfsdf
		return sb.toString();
	}

	// method to reset the pool because of a timeout
	public void resetPool() throws SQLException{
		Utils.logMessage(this, Severity.INFO,"reset pool requested.");
		closeAllConnections();
		//fillPool();
		listConns.clear();
		allConns.clear();
		Utils.logMessage(this, Severity.INFO, "set/hashmap cleared. map contains "+allConns.size() +" conns");
		for (int i = 1; i <= MAX_DB_CONNECTIONS; i++) {
			Connection conn = createConnection();
			// add to Map
			allConns.put(conn, true);
			// add also to List (just for output the status of pool)
			listConns.add(conn);
		}
		Utils.logMessage(this, Severity.INFO, allConns.size() + " new connections added to set/hashmap as free=true");
	}
}
