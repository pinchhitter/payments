package cdac.in.payment;

import java.util.*;
import java.io.*;
import java.sql.*;

import java.util.Properties;

public class Payment{

	Connection connection = null;
	String merchantCode = null;
	String doubleVerificationUrl = null;
	String dbURL = null;
	String dbUname = null;
	String dbPassword = null;
	String exam = null;
	String year = null;
	
	
	Payment(){

		if( connection == null ){

			try{
				Properties prop = new Properties();
				prop.load(getClass().getResourceAsStream("./config.properties"));
				merchantCode = prop.getProperty("merchantCode").trim();
				doubleVerificationURL = prop.getProperty("doubleVerificationUrl").trim();
				dbURL = prop.getProperty("db.URL").trim();
				dbUname = prop.getProperty("db.uname").trim();
				dbPassword = prop.getProperty("db.password").trim();
				exam = prop.getProperty("exam").trim();
				year = prop.getProperty("year").trim();

				Class.forName("org.postgresql.Driver");
				connection = DriverManager.getConnection(dbURL, dbUname, dbPassword);

				System.err.println( merchantCode+", "+doubleVerificationURL+", "+dbURL+", "+dbUname+", "+dbPassword+", "+exam+", "+year);
			}catch(Exception e){
				e.printStackTrace();
				return null;
			}
		}

		return connection;
	}

	Connection getConnection(){

		if( connection == null ){
			try{
				Class.forName("org.postgresql.Driver");
				connection = DriverManager.getConnection(dbURL, dbUname, dbPassword);
			}catch(Exception e){
				e.printStackTrace();
				return null;
			}

		}
	return connection;
	}

	public static void main(String[] args){
		Payment payment = new Payment();
		System.err.println( payment.getConnection() );		
	}
}
