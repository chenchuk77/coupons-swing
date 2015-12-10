package com.jbp.main;

import java.awt.EventQueue;
import javax.swing.JFrame;
import com.jbp.utils.Severity;
import com.jbp.utils.Utils;
//
// Test class is used to instantiate the CouponSystem object
// it will open a swing JFrame, all try/catch blocks are handled in the GUI.
//

// using a JFrame template for the EventQueue
public class Test extends JFrame {

	// static variable for coupon system
	private static CouponSystemJFrame cs;

	// entry point
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Utils.loadSystemParameters();
					cs = CouponSystemJFrame.getInstance();
				} catch (Exception e) {
					Utils.logMessage(this, Severity.INFO, e.getMessage());
					System.out.println(e.getMessage());
					System.exit(0);
				}
			}
		});
	}
}
