package com.jbp.gui;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import javax.swing.JRadioButton;
import com.jbp.beans.Coupon;
import com.jbp.beans.CouponType;
import com.jbp.exceptions.CouponCreationException;
import com.jbp.exceptions.CouponRemovalException;
import com.jbp.exceptions.CouponUpdateException;
import com.jbp.facade.CompanyFacade;
import com.jbp.utils.*;

//
// This is the GUI for companies.
// it provides Company functionality using the Company Facade logic
//
public class CompanyWindow extends JFrame implements ActionListener {

	// this window is for a specific Company
	private CompanyFacade cf;

	private JTextField tfNewCoupTitle;
	private JTextField tfNewCoupStartDate;
	private JTextField tfNewCoupEndDate;
	private JTextField tfNewCoupAmount;
	private JTextField tfNewCoupMessage;
	private JTextField tfNewCoupPrice;
	private JTextField tfNewCoupImg;
	private JTextField textField;
	private JTextField tfCouponById;
	private JTextField tfMaxPrice;
	private JTextField tfMaxExpDate;
	private JLabel lblCompWelcome;
	private JComboBox<CouponType> cbNewCoupType;
	private JButton btnCreateCoupon;
	private JButton btnUpdatePerform;
	private JRadioButton rbId;
	private JRadioButton rbType;
	private JRadioButton rbExpDate;
	private JRadioButton rbMaxPrice;
	private JRadioButton rbGetAll;
	private JComboBox<CouponType> cbCouponType;
	private JButton btnGetCoupons;
	private JButton btnRemoveCoupon;
	private JButton btnLoadCoupon;
	private JList<Coupon> jlMyCoupons;
	private DefaultListModel<Coupon> model;
	private JScrollPane myCouponsScrollPan;
	private Coupon couponToUpdate;
	private JLabel lblGetCoupResult;
	private JLabel lblCompanyID;

	Collection<Coupon> coupons = null;
	private Coupon coupon;

	// c'tor will refer to the Facade
	public CompanyWindow(CompanyFacade cf) {
		this.cf = cf;
		this.setSize(900, 510);
		this.setLocation(200, 100);
		setTitle("Company Window");
		// closing this JFrame should NOT close the system
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(null);

		lblCompWelcome = new JLabel("Welcome To "
				+ cf.getCompany().getCompName() + " Coupon Menagment System");
		lblCompWelcome.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 18));
		lblCompWelcome.setBounds(10, 5, 500, 31);
		getContentPane().add(lblCompWelcome);

		lblCompanyID = new JLabel("Company ID: " + cf.getCompany().getId());
		lblCompanyID.setBounds(510, 5, 120, 31);
		getContentPane().add(lblCompanyID);

		JPanel pnCopounDetails = new JPanel();
		pnCopounDetails.setBounds(613, 40, 250, 355);
		pnCopounDetails.setBackground(Color.lightGray);
		getContentPane().add(pnCopounDetails);
		pnCopounDetails.setLayout(null);

		JLabel lblNewCoupon = new JLabel("Update / Create new coupon");
		lblNewCoupon.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 14));
		lblNewCoupon.setBounds(10, 11, 300, 25);
		pnCopounDetails.add(lblNewCoupon);

		tfNewCoupTitle = new JTextField();
		tfNewCoupTitle.setBounds(76, 78, 146, 20);
		pnCopounDetails.add(tfNewCoupTitle);
		tfNewCoupTitle.setColumns(10);

		JLabel lblNewCoupTitle = new JLabel("Title:");
		lblNewCoupTitle.setBounds(20, 81, 46, 14);
		pnCopounDetails.add(lblNewCoupTitle);

		JLabel lblNewCoupType = new JLabel("Choose type:");
		lblNewCoupType.setBounds(20, 52, 74, 14);
		pnCopounDetails.add(lblNewCoupType);

		cbNewCoupType = new JComboBox<CouponType>(CouponType.values());
		cbNewCoupType.setBounds(102, 47, 120, 20);
		pnCopounDetails.add(cbNewCoupType);

		tfNewCoupStartDate = new JTextField();
		tfNewCoupStartDate.setColumns(10);
		tfNewCoupStartDate.setBounds(86, 109, 135, 20);
		Date startDate = new Date(Calendar.getInstance().getTime().getTime());
		String modifiedStartDate = new SimpleDateFormat("yyyy-MM-dd")
				.format(startDate);
		tfNewCoupStartDate.setText(modifiedStartDate);
		pnCopounDetails.add(tfNewCoupStartDate);

		JLabel lblNewCoupStartDate = new JLabel("StartDate:");
		lblNewCoupStartDate.setBounds(20, 112, 60, 14);
		pnCopounDetails.add(lblNewCoupStartDate);

		tfNewCoupEndDate = new JTextField();
		tfNewCoupEndDate.setColumns(10);
		tfNewCoupEndDate.setBounds(87, 140, 135, 20);
		Date endDate = new Date(Calendar.getInstance().getTime().getTime());
		String modifiedEndDate = new SimpleDateFormat("yyyy-MM-dd")
				.format(endDate);
		tfNewCoupEndDate.setText(modifiedEndDate);
		pnCopounDetails.add(tfNewCoupEndDate);

		JLabel lblNewCoupEndDate = new JLabel("End Date:");
		lblNewCoupEndDate.setBounds(20, 143, 60, 14);
		pnCopounDetails.add(lblNewCoupEndDate);

		tfNewCoupAmount = new JTextField();
		tfNewCoupAmount.setColumns(10);
		tfNewCoupAmount.setBounds(87, 169, 135, 20);
		pnCopounDetails.add(tfNewCoupAmount);

		JLabel lblNewCoupAmount = new JLabel("Amount:");
		lblNewCoupAmount.setBounds(20, 172, 57, 14);
		pnCopounDetails.add(lblNewCoupAmount);

		tfNewCoupMessage = new JTextField();
		tfNewCoupMessage.setColumns(10);
		tfNewCoupMessage.setBounds(87, 200, 135, 20);
		pnCopounDetails.add(tfNewCoupMessage);

		JLabel lblNewCoupMessage = new JLabel("Message:");
		lblNewCoupMessage.setBounds(20, 203, 57, 14);
		pnCopounDetails.add(lblNewCoupMessage);

		tfNewCoupPrice = new JTextField();
		tfNewCoupPrice.setColumns(10);
		tfNewCoupPrice.setBounds(76, 231, 146, 20);
		pnCopounDetails.add(tfNewCoupPrice);

		JLabel lblNewCoupPrice = new JLabel("Price:");
		lblNewCoupPrice.setBounds(20, 234, 46, 14);
		pnCopounDetails.add(lblNewCoupPrice);

		tfNewCoupImg = new JTextField();
		tfNewCoupImg.setColumns(10);
		tfNewCoupImg.setBounds(76, 263, 146, 20);
		pnCopounDetails.add(tfNewCoupImg);

		JLabel lblNewCoupImg = new JLabel("Image:");
		lblNewCoupImg.setBounds(20, 266, 46, 14);
		pnCopounDetails.add(lblNewCoupImg);

		btnCreateCoupon = new JButton("Create Coupon");
		btnCreateCoupon.setFont(new Font("Tahoma", Font.PLAIN, 10));
		btnCreateCoupon.setBounds(137, 319, 105, 25);
		btnCreateCoupon.addActionListener(this);
		pnCopounDetails.add(btnCreateCoupon);

		btnUpdatePerform = new JButton("Execute Update");
		btnUpdatePerform.setFont(new Font("Tahoma", Font.PLAIN, 10));
		btnUpdatePerform.setBounds(15, 319, 105, 25);
		btnUpdatePerform.addActionListener(this);
		pnCopounDetails.add(btnUpdatePerform);
		btnUpdatePerform.setEnabled(false);

		JPanel pnGetCoupons = new JPanel();
		pnGetCoupons.setBounds(20, 40, 553, 230);
		getContentPane().add(pnGetCoupons);
		pnGetCoupons.setLayout(null);

		JLabel lblGetCouponsPanel = new JLabel("Get Coupons By:");
		lblGetCouponsPanel.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblGetCouponsPanel.setBounds(10, 11, 116, 22);
		pnGetCoupons.add(lblGetCouponsPanel);

		ButtonGroup rbGrpAvailable = new ButtonGroup();

		rbId = new JRadioButton("ID");
		rbId.setBounds(17, 40, 150, 23);
		pnGetCoupons.add(rbId);
		rbGrpAvailable.add(rbId);

		rbType = new JRadioButton("Type");
		rbType.setBounds(17, 70, 150, 23);
		pnGetCoupons.add(rbType);
		rbGrpAvailable.add(rbType);

		rbExpDate = new JRadioButton("Max Expiration Date");
		rbExpDate.setBounds(17, 100, 150, 23);
		pnGetCoupons.add(rbExpDate);
		rbGrpAvailable.add(rbExpDate);

		rbMaxPrice = new JRadioButton("Max Price");
		rbMaxPrice.setBounds(17, 130, 150, 23);
		pnGetCoupons.add(rbMaxPrice);
		rbGrpAvailable.add(rbMaxPrice);

		rbGetAll = new JRadioButton("Get All");
		rbGetAll.setSelected(true);
		rbGetAll.setBounds(17, 160, 150, 23);
		pnGetCoupons.add(rbGetAll);
		rbGrpAvailable.add(rbGetAll);

		JLabel lblEnterCouponId = new JLabel("Enter coupon ID:");
		lblEnterCouponId.setBounds(190, 44, 120, 14);
		pnGetCoupons.add(lblEnterCouponId);

		tfCouponById = new JTextField();
		tfCouponById.setBounds(309, 41, 126, 20);
		pnGetCoupons.add(tfCouponById);
		tfCouponById.setColumns(10);

		JLabel lblChooseType = new JLabel("Choose Type:");
		lblChooseType.setBounds(190, 74, 120, 14);
		pnGetCoupons.add(lblChooseType);

		cbCouponType = new JComboBox<CouponType>(CouponType.values());
		cbCouponType.setBounds(309, 71, 126, 20);
		pnGetCoupons.add(cbCouponType);

		tfMaxExpDate = new JTextField();
		tfMaxExpDate.setBounds(190, 103, 126, 20);
		pnGetCoupons.add(tfMaxExpDate);

		tfMaxPrice = new JTextField();
		tfMaxPrice.setBounds(190, 135, 126, 20);
		pnGetCoupons.add(tfMaxPrice);

		btnGetCoupons = new JButton("Get Coupons");
		btnGetCoupons.setBounds(210, 195, 125, 30);
		btnGetCoupons.addActionListener(this);
		pnGetCoupons.add(btnGetCoupons);

		btnRemoveCoupon = new JButton("Remove Coupon");
		btnRemoveCoupon.setBounds(79, 426, 141, 31);
		btnRemoveCoupon.addActionListener(this);
		getContentPane().add(btnRemoveCoupon);
		btnRemoveCoupon.setEnabled(false);

		btnLoadCoupon = new JButton("Load Coupon");
		btnLoadCoupon.setBounds(248, 426, 141, 31);
		btnLoadCoupon.addActionListener(this);
		getContentPane().add(btnLoadCoupon);
		btnLoadCoupon.setEnabled(false);

		model = new DefaultListModel<Coupon>();
		jlMyCoupons = new JList<Coupon>(model);
		myCouponsScrollPan = new JScrollPane();
		myCouponsScrollPan.setViewportView(jlMyCoupons);
		myCouponsScrollPan.setBounds(20, 300, 553, 105);
		getContentPane().add(myCouponsScrollPan);

		lblGetCoupResult = new JLabel("");
		lblGetCoupResult.setBounds(20, 275, 450, 14);
		lblGetCoupResult.setFont(new Font("Tahoma", Font.BOLD, 13));
		getContentPane().add(lblGetCoupResult);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton btnClicked = (JButton) e.getSource();
		// collection to contain all the coupons on a "get coupons" action

		if (btnClicked == btnCreateCoupon) {
			coupon = new Coupon();
			createNewCoupon();
		}
		if (btnClicked == btnGetCoupons) {
			// getting the company coupons based on the radio buttons condition
			getCoupons();
		}
		// remove a coupon that was selected from the coupons list
		// only available after retrieving any coupons to the list
		if (btnClicked == btnRemoveCoupon) {
			removeCoupon();
		}
		// loading selected coupon from jlMyCoupons to the
		// "update / create new  couopn" panel
		if (btnClicked == btnLoadCoupon) {
			loadCoupon();
		}

		// perform changes from the UI into the DB
		if (btnClicked == btnUpdatePerform) {
			updatePerform();
		}
	}

	// create new Coupon
	private void createNewCoupon() {
		// errors will represent a counter which be greater then 0 if there are any
		// Illegal inputs during the coupon creation
		int errors = 0;
		// when creating a coupon, must make sure all necessary fields have  values
		// returns a proper message if there is an empty field that must have value
		// or if the value is illegal
		if (!tfNewCoupTitle.getText().equals("")) {
			coupon.setTitle(tfNewCoupTitle.getText());
			if (!tfNewCoupStartDate.getText().equals("")) {
				coupon.setStartDate(Utils.stringToSQLDate(tfNewCoupStartDate
						.getText()));
				if (!tfNewCoupEndDate.getText().equals("")) {
					coupon.setEndDate(Utils.stringToSQLDate(tfNewCoupEndDate
							.getText()));
					if (!tfNewCoupAmount.getText().equals("")) {
						// for both amount and price - making sure the input is
						// a number
						try {
							coupon.setAmount(Integer.parseInt(tfNewCoupAmount
									.getText()));
							coupon.setType((CouponType) (cbNewCoupType
									.getSelectedItem()));
							coupon.setMessage(tfNewCoupMessage.getText());
							if (!tfNewCoupPrice.getText().equals("")) {
								try {
									coupon.setPrice(Double
											.parseDouble(tfNewCoupPrice
													.getText()));
									coupon.setImage(tfNewCoupImg.getText());
									coupon.setCompanyId((cf.getCompany()
											.getId()));
								} catch (NumberFormatException ex) {
									JOptionPane.showMessageDialog(null,
											"price must be a number!");
									errors++;
								}
							} else {
								JOptionPane.showMessageDialog(null,
										"Must choose a price for the coupon!",
										"price empty", 1);
								errors++;
							}
						} catch (NumberFormatException ex) {
							JOptionPane.showMessageDialog(null,
									"amount must be a whole number!");
							errors++;
						}
					} else {
						JOptionPane.showMessageDialog(null,
								"Must choose a amount for the coupon!",
								"amount empty", 1);
						errors++;
					}
				} else {
					JOptionPane.showMessageDialog(null,
							"Must choose a end date for the coupon!",
							"end date empty", 1);
					errors++;
				}
			} else {
				JOptionPane.showMessageDialog(null,
						"Must choose a start date for the coupon!",
						"start date empty", 1);
				errors++;
			}
		} else {
			JOptionPane.showMessageDialog(null,
					"Must choose a title for the coupon!", "title empty", 1);
			errors++;
		}

		if (errors == 0) {
			String dateCompareRs = dateValidation(tfNewCoupStartDate.getText(),
					tfNewCoupEndDate.getText());
			if (dateCompareRs.equals("valid dates"))
				try {
					cf.createCoupon(coupon);
				} catch (CouponCreationException e) {
					JOptionPane.showMessageDialog(
							null,
							e.getMessage() + "\nCoupon already exists.",
							"Notice!", 0);
				}
			else if (dateCompareRs.equals("invalid date range"))
				JOptionPane.showMessageDialog(null,
						"End date must be greater then start date!",
						"Time line error", 0);
			else
				JOptionPane.showMessageDialog(null,
						"Date must be in the next format - YYYY-MM-DD",
						"Invalid date format", 0);
		}
		coupon = null;
	}

	// remove coupon 
	private void removeCoupon() {
		try {
			coupon = jlMyCoupons.getSelectedValue();
			if (coupon != null) {
				cf.removeCoupon(coupon);
				model.remove(model.indexOf(coupon));
				coupon = null;
				if (model.isEmpty()) {
					btnLoadCoupon.setEnabled(false);
					btnRemoveCoupon.setEnabled(false);
				}
			} else
				JOptionPane.showMessageDialog(null, "no coupon was chosen!",
						"Notice!", 1);
		} catch (CouponRemovalException ex) {
			JOptionPane.showMessageDialog(null, ex.getMessage(),
					"Error!", 1);
		}
	}

	private void loadCoupon() {
		// fill the fields with the loaded coupon's details
		couponToUpdate = jlMyCoupons.getSelectedValue();
		if (couponToUpdate != null) {
			cbNewCoupType.setSelectedItem(couponToUpdate.getType());
			cbNewCoupType.setEnabled(false); // type cant be changed
			tfNewCoupTitle.setText(couponToUpdate.getTitle());
			tfNewCoupTitle.setEditable(false); // title cant be edited
			tfNewCoupStartDate
					.setText(couponToUpdate.getStartDate().toString());
			tfNewCoupStartDate.setEditable(false);
			tfNewCoupEndDate.setText(couponToUpdate.getEndDate().toString());
			Integer amount = couponToUpdate.getAmount();
			tfNewCoupAmount.setText(amount.toString());
			tfNewCoupMessage.setText(couponToUpdate.getMessage());
			tfNewCoupImg.setText(couponToUpdate.getImage());
			Double price = couponToUpdate.getPrice();
			tfNewCoupPrice.setText(price.toString());
			btnUpdatePerform.setEnabled(true);
		} else
			JOptionPane.showMessageDialog(null, "No coupon was choosen",
					"Notice!", 1);
	}

	private void getCoupons() {
		try {
			model.removeAllElements();
			// get all the company coupons
			if (rbGetAll.isSelected()) {
				coupons = cf.getAllCoupons();
				for (Coupon Coupon : coupons)
					model.addElement(Coupon);
			}
			// get coupons by type
			if (rbType.isSelected()) {
				coupons = cf.getAllCouponsByType(CouponType
						.valueOf(cbCouponType.getSelectedItem().toString()));
				for (Coupon Coupon : coupons)
					model.addElement(Coupon);
			}
			// get coupons by expiration date
			// throw a notification in case of wrong date format
			if (rbExpDate.isSelected()) {
				coupons = cf.getAllCouponsByMaxDate(tfMaxExpDate.getText());
				for (Coupon Coupon : coupons)
					model.addElement(Coupon);
			}

			// get coupons by max price
			// throw a notification in case of the input is not a number
			if (rbMaxPrice.isSelected()) {
				try {
					coupons = cf.getAllCouponsByMaxPrice(Double
							.parseDouble(tfMaxPrice.getText()));
					for (Coupon Coupon : coupons)
						model.addElement(Coupon);
				} catch (NumberFormatException e) {
					JOptionPane.showMessageDialog(null,
							"Price must be a valide number !", "Error !",
							JOptionPane.ERROR_MESSAGE);
				}
			}

			// get coupons by coupon id
			// throw a notification in case of the input is not an integer
			if (rbId.isSelected()) {
				try {
					coupons = cf.getCouponById(Long.parseLong(tfCouponById
							.getText()));
					for (Coupon Coupon : coupons)
						model.addElement(Coupon);
				} catch (NumberFormatException e) {
					JOptionPane.showMessageDialog(null,
							"ID must be a valide number !", "Error !",
							JOptionPane.ERROR_MESSAGE);
				}
			}

			// if any coupons are shown, set remove and load button visible
			// else make sure both buttons are disabled
			if (!model.isEmpty()) {
				btnRemoveCoupon.setEnabled(true);
				btnLoadCoupon.setEnabled(true);
				if (rbGetAll.isSelected())
					lblGetCoupResult.setText("This are all "
							+ cf.getCompany().getCompName() + "`s coupons:");
				if (rbId.isSelected())
					lblGetCoupResult.setText("This are coupon "
							+ tfCouponById.getText() + " details:");
				if (rbType.isSelected())
					lblGetCoupResult.setText("This are all "
							+ cf.getCompany().getCompName() + "`s coupons of "
							+ model.firstElement().getType().toString()
							+ " Type:");
				if (rbExpDate.isSelected())
					lblGetCoupResult.setText("This are all "
							+ cf.getCompany().getCompName()
							+ "`s coupons which expiers before "
							+ tfMaxExpDate.getText());
				if (rbMaxPrice.isSelected())
					lblGetCoupResult.setText("This are all "
							+ cf.getCompany().getCompName()
							+ "`s coupons which cost up to "
							+ tfMaxPrice.getText() + " NIS");
			} else {
				btnRemoveCoupon.setEnabled(false);
				btnLoadCoupon.setEnabled(false);
				lblGetCoupResult.setText("");
			}
		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(null, "Id must be a number!",
					"Incorrect id", 1);
			btnRemoveCoupon.setEnabled(false);
			btnLoadCoupon.setEnabled(false);
			lblGetCoupResult.setText("");
		}
	}

	private void updatePerform() {
		// errors will represent a counter which be greater then 0 if there are any
		// Illegal inputs during the coupon update
		int errors = 0;
		// when creating a coupon, must make sure all necessary fields have values
		// returns a proper message if there is an empty field that must have  value
		// or if the value is illegal
		if (couponToUpdate != null) {
			if (dateValidation(tfNewCoupStartDate.getText(), tfNewCoupEndDate.getText())=="valid dates")
			{
				if (!tfNewCoupEndDate.getText().equals("")) {
					couponToUpdate.setEndDate(Utils
							.stringToSQLDate(tfNewCoupEndDate.getText()));
					if (!tfNewCoupAmount.getText().equals("")) {
						// for both amount and price - making sure the input is a
						// number
						try {
							couponToUpdate.setAmount(Integer
									.parseInt(tfNewCoupAmount.getText()));
							couponToUpdate.setMessage(tfNewCoupMessage.getText());
							if (!tfNewCoupPrice.getText().equals("")) {
								try {
									couponToUpdate.setPrice(Double
											.parseDouble(tfNewCoupPrice.getText()));
									couponToUpdate.setImage(tfNewCoupImg.getText());
								} catch (NumberFormatException ex) {
									JOptionPane.showMessageDialog(null,
											"price must be a number!");
									errors++;
								}
							} else {
								JOptionPane.showMessageDialog(null,
										"Must choose a price for the coupon!",
										"price empty", 1);
								errors++;
							}
						} catch (NumberFormatException ex) {
							JOptionPane.showMessageDialog(null,
									"amount must be a whole number!");
							errors++;
						}
					} else {
						JOptionPane.showMessageDialog(null,
								"Must choose a amount for the coupon!",
								"amount empty", 1);
						errors++;
					}
				} else {
					JOptionPane.showMessageDialog(null,
							"Must choose a end date for the coupon!",
							"end date empty", 1);
					errors++;
				}
			}
			else { 
				 JOptionPane.showMessageDialog(null, 
						 dateValidation(tfNewCoupStartDate.getText(), tfNewCoupEndDate.getText()), "error",
						 JOptionPane.ERROR_MESSAGE);
			}
		}
		if (errors == 0) {
			try {
				cf.updateCoupon(couponToUpdate);
				cbNewCoupType.setEnabled(true);
				tfNewCoupAmount.setText("");
				tfNewCoupTitle.setEditable(true);
				tfNewCoupTitle.setText("");
				tfNewCoupStartDate.setEditable(true);
				// inserting initial date for new coupons. default will be today's date
				Date endDate = new Date(Calendar.getInstance().getTime().getTime());
				String modifiedDate = new SimpleDateFormat("yyyy-MM-dd")
						.format(endDate);
				tfNewCoupStartDate.setText(modifiedDate);
				tfNewCoupEndDate.setText(modifiedDate);
				tfNewCoupImg.setText("");
				tfNewCoupMessage.setText("");
				tfNewCoupPrice.setText("");
				btnUpdatePerform.setEnabled(false);
			} catch (CouponUpdateException e1) {
				JOptionPane.showMessageDialog(null, e1.getMessage(),
						"Error!", 1);
			}
		}
	}

	// method to validate that start and end dates are
	// 1) in legal format
	// 2) end date is greater then start date
	public String dateValidation(String startDate, String endDate) {
		java.sql.Date sDate = Utils.stringToSQLDate(startDate);
		java.sql.Date eDate = Utils.stringToSQLDate(endDate);
		String response = null;
		if (sDate != null && eDate != null) {
			if (eDate.compareTo(sDate) > 0) {
				response = "valid dates";
				return response;
			} // if dates are valid
			else {
				response = "invalid date range";
				return response; // if end date isnt greater then start date
			}
		} else {
			response = "invalid date format";
			return response; // if one of the dates or both of them arent in
								// valid format
		}
	}
}
