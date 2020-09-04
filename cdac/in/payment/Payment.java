package cdac.in.payment;

import java.util.*;
import java.io.*;
import java.sql.*;

public class Payment{

	String merchantCode = null;
	String doubleVerificationURL = null;
	String dbURL = null;
	String dbUname = null;
	String dbPassword = null;
	String exam = null;
	String year = null;


	Payment(){

		try{

			Properties prop = new Properties();
			prop.load(getClass().getResourceAsStream("./config.properties"));

			merchantCode = prop.getProperty("merchantCode").trim();
			doubleVerificationURL = prop.getProperty("doubleVerificationURL").trim();
			exam = prop.getProperty("exam").trim();
			year = prop.getProperty("year").trim();

			dbURL = prop.getProperty("db.url").trim();
			dbUname = prop.getProperty("db.uname").trim();
			dbPassword = prop.getProperty("db.password").trim();

			//System.err.println( merchantCode+", "+doubleVerificationURL+", "+dbURL+", "+dbUname+", "+dbPassword+", "+exam+", "+year);

		}catch(Exception e){
			e.printStackTrace();
		}
	}

	Connection getConnection(){

		Connection connection = null;

		try{
			Class.forName("org.postgresql.Driver");
			connection = DriverManager.getConnection(dbURL, dbUname, dbPassword);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		return connection;
	}

	void closeConnection( Connection connection ){

		try{
			if( connection != null ){
				connection.close();
			}

		}catch(Exception e){
			e.printStackTrace();
		}finally{
			connection = null;
		}
	}

	String getMerchantCode(){
		return merchantCode;
	}

	String getDoubleVerificationURL(){
		return doubleVerificationURL;
	}

	String getExam(){
		return exam;
	}

	String getYear(){
		return year;
	}

	public static void main(String[] args){

		Payment payment = new Payment();
		System.err.println( payment.getConnection() );		
	}
}
