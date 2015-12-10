-- Database script file for table creation and data insertion
-- also contain useful queries
--
-- Database access :
--------------------
-- SERVER		:	appsrv.kukinet.net : 3306
-- DATABASE		:	DBCoupons
-- USERNAME		:	admin
-- PASSWORD		:	1234
------------------------------------------------------------------

CREATE TABLE Companies(
	ID BIGINT,
	COMP_NAME VARCHAR(30),
	PASSWARD VARCHAR(30),
	EMAIL VARCHAR(30),
	PRIMARY KEY (ID)
);

CREATE TABLE Customers(
	ID BIGINT,
	CUST_NAME VARCHAR(30),
	PASSWARD VARCHAR(30),
	PRIMARY KEY (ID)
);

CREATE TABLE Coupons(
	ID BIGINT,
	TITLE VARCHAR(30),
	START_DATE DATE,
	END_DATE DATE,
	AMOUNT INT,
	TYPE VARCHAR(30),
	MESSAGE VARCHAR(30),
	PRICE DOUBLE,
	IMAGE VARCHAR(255),
	PRIMARY KEY (ID)
);

CREATE TABLE Customer_Coupon (
	CUST_ID BIGINT,
	COUPON_ID BIGINT,
	PRIMARY KEY (CUST_ID, COUPON_ID)
);

CREATE TABLE Company_Coupon (
	COMP_ID BIGINT,
	COUPON_ID BIGINT,
	PRIMARY KEY (COMP_ID, COUPON_ID)
);

-- add new field in Coupon table : COMPANY_ID
-- added FK_COMPANY_ID in Coupons table

-- sample data for database
INSERT INTO Customers (CUST_NAME, PASSWORD) VALUES ('Ben', 1234);
INSERT INTO Customers (CUST_NAME, PASSWORD) VALUES ('Avi', 1234);
INSERT INTO Customers (CUST_NAME, PASSWORD) VALUES ('Elad', 1234);
INSERT INTO Customers (CUST_NAME, PASSWORD) VALUES ('Yosi', 1234);
INSERT INTO Companies (COMP_NAME, PASSWORD, EMAIL) VALUES ('MAG', 1234, 'mag@gmail.com');
INSERT INTO Companies (COMP_NAME, PASSWORD, EMAIL) VALUES ('Armani', 1234, 'armani@gmail.com');
INSERT INTO Companies (COMP_NAME, PASSWORD, EMAIL) VALUES ('HTC', 1234, 'htc@gmail.com');
INSERT INTO Companies (COMP_NAME, PASSWORD, EMAIL) VALUES ('ViewSonic', 1234, 'vs@gmail.com');
INSERT INTO Companies (COMP_NAME, PASSWORD, EMAIL) VALUES ('Ibanez', 1234, 'ibanez@gmail.com');
INSERT INTO Companies (COMP_NAME, PASSWORD, EMAIL) VALUES ('Osem', 1234, 'marshall@gmail.com');
INSERT INTO Coupons (TITLE,START_DATE,END_DATE,AMOUNT,TYPE,MESSAGE, PRICE,IMAGE, COMPANY_ID) VALUES ('zzaver2222y old monito2r','2016-07-01','2016-07-10',200,'ELECTRICITY','nice new monitor',700,'', 41);
INSERT INTO Coupons (TITLE,START_DATE,END_DATE,AMOUNT,TYPE,MESSAGE, PRICE,IMAGE, COMPANY_ID) VALUES ('zzabig monitor','2016-07-01','2016-12-01',200,'ELECTRICITY','nice new monitor',1400,'', 41);
INSERT INTO Coupons (TITLE,START_DATE,END_DATE,AMOUNT,TYPE,MESSAGE, PRICE,IMAGE, COMPANY_ID) VALUES ('zzahuge monitor','2016-07-01','2016-08-01',50,'ELECTRICITY','nice new monitor',3800,'', 41);
INSERT INTO Coupons (TITLE,START_DATE,END_DATE,AMOUNT,TYPE,MESSAGE, PRICE,IMAGE, COMPANY_ID) VALUES ('hot sunglasses','2016-07-01','2018-07-01',50,'FASHION','hot looking black sunglasses',120,'', 42);
INSERT INTO Coupons (TITLE,START_DATE,END_DATE,AMOUNT,TYPE,MESSAGE, PRICE,IMAGE, COMPANY_ID) VALUES ('mens watch','2016-07-01','2018-07-01',300,'FASHION','swiss watch ',420,'', 42);
INSERT INTO Coupons (TITLE,START_DATE,END_DATE,AMOUNT,TYPE,MESSAGE, PRICE,IMAGE, COMPANY_ID) VALUES ('10-Tshirts pack','2015-07-01','2015-09-01',2000,'FASHION','10 shirts pack from china',200,'', 42);

-- adding AUTO_INCREMENT (identity column)
ALTER TABLE Customers MODIFY column ID BIGING NOT NULL AUTO_INCREMENT ;
ALTER TABLE Companies MODIFY column ID BIGINT NOT NULL AUTO_INCREMENT ;
ALTER TABLE Coupons MODIFY column ID BIGINT NOT NULL AUTO_INCREMENT ;

-- create user and enable access specific/any IP address
GRANT ALL PRIVILEGES ON DBCoupons.* TO 'admin'@'79.177.165.2' IDENTIFIED BY '1234';
GRANT ALL PRIVILEGES ON DBCoupons.* TO 'admin'@'%' IDENTIFIED BY '1234';

/* 
------------------------------------------------------------------------------
-- SELECT * from Customers
-- SELECT * FROM Companies;
-- SELECT * FROM Coupons;
-- SELECT * FROM Customer_Coupon;
-- SELECT * FROM Company_Coupon;
-- SELECT COUNT(*) FROM Companies;
-- SELECT date(today);
-- SELECT * from Coupons WHERE END_DATE > '2016-01-01'

authentication example
-- SELECT COUNT(*) FROM Companies WHERE COMP_NAME='Sony' AND PASSWORD="1234"; -- return 1
-- SELECT COUNT(*) FROM Companies WHERE COMP_NAME='Sony' AND PASSWORD="1235"; -- returns 0

join queries to see coupons-companies relation
-- SELECT * FROM Companies CMP JOIN Coupons CUP on CMP.ID = CUP.COMPANY_ID 

can return the company ID also 
-- SELECT ID FROM Companies WHERE COMP_NAME='Sony' AND PASSWORD="1234"; -- return 6
-- SELECT ID FROM Companies WHERE COMP_NAME='Sony' AND PASSWORD="1235"; -- returns null

-- SELECT * FROM Coupons WHERE COMPANY_ID=1
-- INSERT INTO Customer_Coupon (CUST_ID, COUPON_ID) VALUES (1, 5);
-- INSERT INTO Company_Coupon (COMP_ID, COUPON_ID) VALUES (41, 32);

for customer id =1 . show all coupons 
-- SELECT c.* FROM Coupons c JOIN Customer_Coupon j ON c.ID = j.COUPON_ID WHERE j.CUST_ID = 1

checking [SQL Date <-> java Date] conversion
-- SELECT year(date('2015-07-11'));
-- SELECT year('2015-07-11');
*/

