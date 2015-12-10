package com.jbp.gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JButton;

import com.jbp.exceptions.CouponNotAvailableException;
import com.jbp.exceptions.CouponUpdateException;
import com.jbp.facade.CustomerFacade;
import com.jbp.beans.Coupon;
import com.jbp.beans.CouponType;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Collection;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JList;
import javax.swing.ListSelectionModel;

//
// This is the GUI for customers.
// it provides Customer functionality using the Customer Facade logic
//
public class CustomerWindow extends JFrame implements ActionListener {

	// this window is for a specific Customer
	private CustomerFacade cf;
	
	private JPanel contentPane;
	private JTextField tfMyPrice;
	private JRadioButton rbMyAll;
	private JRadioButton rbMyByType;
	private JRadioButton rbMyByPrice;
	private JComboBox<CouponType> cbCouponTypes;
	private JComboBox<CouponType> cbAvailableCouponTypes;
	private JComboBox<CouponType> cbMyCouponTypes;
	private JList<Coupon> listOfCoupons;
	private DefaultListModel<Coupon> model;
	private JTextField tfAvailablePrice;
	private JLabel lblCouponList;
	private JRadioButton rbAvailableAll;
	private JRadioButton rbAvailableByType;
	private JRadioButton rbAvailableByPrice;
	private JButton btnMy;
	private JButton btnAvailable;
	private JButton btnPurchase;

	// c'tor will refer to the Facade
	public CustomerWindow(CustomerFacade cf) {

		// for every window we pass the ref to Facade
		this.cf = cf;
		setTitle("Customer Window");
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		contentPane = new JPanel();
		contentPane.setLayout(null);
		setContentPane(contentPane);

		JPanel panelMyCoupons = new JPanel();
		panelMyCoupons.setLayout(null);
		panelMyCoupons.setBorder(new LineBorder(new Color(0, 0, 0)));
		panelMyCoupons.setBounds(20, 65, 383, 156);
		contentPane.add(panelMyCoupons);

		// left side panel ( for My coupons )
		ButtonGroup rbGrpMy = new ButtonGroup();

		rbMyAll = new JRadioButton("Get All Coupons");
		rbMyAll.setBounds(6, 49, 139, 23);
		rbMyAll.setSelected(true);
		panelMyCoupons.add(rbMyAll);
		rbGrpMy.add(rbMyAll);

		rbMyByType = new JRadioButton("Get Coupons by Type");
		rbMyByType.setBounds(6, 89, 177, 23);
		panelMyCoupons.add(rbMyByType);
		rbGrpMy.add(rbMyByType);

		rbMyByPrice = new JRadioButton("Get Coupons By Max Price");
		rbMyByPrice.setBounds(6, 126, 177, 23);
		panelMyCoupons.add(rbMyByPrice);
		rbGrpMy.add(rbMyByPrice);

		tfMyPrice = new JTextField();
		tfMyPrice.setText("100.00");
		tfMyPrice.setBounds(189, 127, 177, 20);
		tfMyPrice.setColumns(10);
		panelMyCoupons.add(tfMyPrice);

		cbMyCouponTypes = new JComboBox(CouponType.values());
		cbMyCouponTypes.setBounds(189, 89, 177, 22);
		panelMyCoupons.add(cbMyCouponTypes);

		btnMy = new JButton("Show My Coupons");
		btnMy.setBounds(98, 11, 177, 23);
		panelMyCoupons.add(btnMy);
		btnMy.addActionListener(this);

		JLabel lbMyCoupons = new JLabel("My Purchased Coupons");
		lbMyCoupons.setBounds(10, 47, 174, 14);
		contentPane.add(lbMyCoupons);

		// right side panel ( for purchase new Coupons )
		JPanel panelAvailableCoupons = new JPanel();
		panelAvailableCoupons.setLayout(null);
		panelAvailableCoupons.setBorder(new LineBorder(new Color(0, 0, 0)));
		panelAvailableCoupons.setBounds(443, 65, 381, 156);
		contentPane.add(panelAvailableCoupons);

		// wrapping in a group ensure that only 1 is selected
		ButtonGroup rbGrpAvailable = new ButtonGroup();

		rbAvailableAll = new JRadioButton("All Coupons");
		rbAvailableAll.setSelected(true);
		rbAvailableAll.setBounds(6, 49, 139, 23);
		panelAvailableCoupons.add(rbAvailableAll);
		rbGrpAvailable.add(rbAvailableAll);

		rbAvailableByType = new JRadioButton("Only Of Type");
		rbAvailableByType.setBounds(6, 88, 139, 23);
		panelAvailableCoupons.add(rbAvailableByType);
		rbGrpAvailable.add(rbAvailableByType);

		rbAvailableByPrice = new JRadioButton("Upto Price");
		rbAvailableByPrice.setBounds(6, 126, 139, 23);
		panelAvailableCoupons.add(rbAvailableByPrice);
		rbGrpAvailable.add(rbAvailableByPrice);

		tfAvailablePrice = new JTextField();
		tfAvailablePrice.setText("100.00");
		tfAvailablePrice.setColumns(10);
		tfAvailablePrice.setBounds(189, 127, 182, 20);
		panelAvailableCoupons.add(tfAvailablePrice);

		cbAvailableCouponTypes = new JComboBox(CouponType.values());
		cbAvailableCouponTypes.setBounds(189, 88, 182, 22);
		panelAvailableCoupons.add(cbAvailableCouponTypes);

		// the next CORRECT line cannot be parsed by Designer editor !!!
		// cbCouponTypes = new JComboBox<CouponType>(CouponType.values());

		btnAvailable = new JButton("Show Available Coupons");
		btnAvailable.addActionListener(this);
		btnAvailable.setBounds(90, 11, 180, 23);
		panelAvailableCoupons.add(btnAvailable);

		btnPurchase = new JButton("Purchase");
		btnPurchase.setForeground(Color.BLUE);
		btnPurchase.addActionListener(this);
		btnPurchase.setEnabled(false);
		btnPurchase.setBounds(191, 49, 180, 23);
		panelAvailableCoupons.add(btnPurchase);

		JLabel lblAvailableCoupons = new JLabel("Available Coupons To Purchase");
		lblAvailableCoupons.setBounds(443, 47, 179, 14);
		contentPane.add(lblAvailableCoupons);

		JPanel panelCustomer = new JPanel();
		panelCustomer.setLayout(null);
		panelCustomer.setBorder(null);
		panelCustomer.setBounds(10, 11, 822, 32);
		contentPane.add(panelCustomer);

		JLabel lblHello = new JLabel("Hello " + cf.getCustomer().getCustName() + ", wellcome back !");
		lblHello.setForeground(new Color(0, 0, 139));
		lblHello.setFont(new Font("Candara", Font.PLAIN, 18));
		lblHello.setBounds(10, 11, 493, 14);
		panelCustomer.add(lblHello);
		// will be filled at runtime by user requestt
		lblCouponList = new JLabel("");
		lblCouponList.setBounds(20, 232, 393, 14);
		contentPane.add(lblCouponList);
		
		model = new DefaultListModel<Coupon>();
		listOfCoupons = new JList<Coupon>();
		listOfCoupons.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listOfCoupons.setModel(model);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		 scrollPane.setViewportView(listOfCoupons);
		scrollPane.setBounds(20, 250, 804, 293);
		contentPane.add(scrollPane);
		
		this.setSize(850, 595);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Collection<Coupon> coupons = null;
		JButton btnPressed = (JButton) e.getSource();
		if (btnPressed == btnMy) {
			// disable purchase when showing my coupons
			btnPurchase.setEnabled(false);
			// clear combo
			model.removeAllElements();
			
			// check which filter to show
			if (rbMyAll.isSelected()) {
				lblCouponList.setText("My all coupon");
				coupons = cf.getAllPurchasedCoupons();
				fillComboWithMyCoupons(coupons);
			}
			if (rbMyByType.isSelected()) {
				CouponType requstedType = (CouponType) cbMyCouponTypes.getSelectedItem();
				lblCouponList.setText("My coupon of type : " + requstedType);
				coupons = cf.getAllPurchasedCouponsByType(requstedType);
				fillComboWithMyCoupons(coupons);
			}
			if (rbMyByPrice.isSelected()) {
				lblCouponList.setText("My all coupons upto price of : " + tfMyPrice.getText());
				try {
					coupons = cf.getAllPurchasedCouponsByPrice(Double.parseDouble(tfMyPrice.getText()));
					fillComboWithMyCoupons(coupons);
				} catch (IllegalArgumentException e1) {
					System.out.println(e1.getMessage());
					JOptionPane.showMessageDialog(null,
							"must enter a legal price", "price error", 0);
				}
			}
		}
		if (btnPressed == btnAvailable) {
			// enable purchase when showing available coupons
			btnPurchase.setEnabled(true);
			// clear combo
			model.removeAllElements();

			// check which filter to show
			if (rbAvailableAll.isSelected()) {
				lblCouponList.setText("All available coupons");
				coupons = cf.getAllAvailableCoupons();
				fillComboWithAvailableCoupons(coupons);
			}
			if (rbAvailableByType.isSelected()) {
				CouponType requstedType = (CouponType) cbAvailableCouponTypes.getSelectedItem();
				// Utils.logMessage(this, Severity.DEBUG, "byType requested : "
				// + requstedType);
				lblCouponList.setText("Available coupons of type : "+ requstedType);
				coupons = cf.getAllAvailableCouponsByType(requstedType);
				fillComboWithAvailableCoupons(coupons);
			}
			if (rbAvailableByPrice.isSelected()) {
				double maxPrice = Double.parseDouble(tfAvailablePrice.getText());
				lblCouponList.setText("Available coupons upto price of : "+ maxPrice);
				coupons = cf.getAllAvailableCouponsByPrice(maxPrice);
				fillComboWithAvailableCoupons(coupons);
			}
		}
		if (btnPressed == btnPurchase) {
			Coupon coupon = listOfCoupons.getSelectedValue();
			// only if there are coupon left
			try {
				cf.purchaseCoupon(coupon);
				// update display with the new list after purchase
				btnPurchase.setEnabled(false);
				model.removeAllElements();

				lblCouponList.setText("My all coupons");
				JOptionPane.showMessageDialog(null,"coupon purchased successfully", "Success", 1);
			} catch (CouponNotAvailableException | CouponUpdateException e1) {
				JOptionPane.showMessageDialog(null, e1.getMessage(),
						"Failure", 0);
			}
		}
	}

	// 2 methods , because the 'amount' should be hidden from customers
	private void fillComboWithMyCoupons(Collection<Coupon> coupons) {
		// fill the combo
		for (Coupon coupon : coupons) {
			// setting the amount according to the current customer.
			// otherwise - the amount that will be shown to the customer will reflect the
			// total amount of coupons left in the inventory.
			coupon.setAmount(1);
			model.addElement(coupon);
		}
	}
	
	// shows available coupons in db
	private void fillComboWithAvailableCoupons(Collection<Coupon> coupons) {
		// fill the combo
		for (Coupon coupon : coupons) {
			// setting the amount according to the current customer.
			// otherwise - the amount that will be shown to the customer will reflect the
			// total amount of coupons left in the inventory.
			model.addElement(coupon);
		}
	}
}
