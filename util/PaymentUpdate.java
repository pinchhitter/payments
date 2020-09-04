package util;

import java.util.*;
import java.io.*;
import java.sql.*;

import java.util.Properties;

class PaymentUpdate{

	static Connection connection = null;
	static String merchantCode = null;
	static Strng doubleVerificationUrl = null;
	
	PaymentUpdate(){

		if( connection == null ){

			try{
				Properties prop = new Properties();
				prop.load(getClass().getResourceAsStream("./config.properties"));
				String merchantCode = prop.getProperty("merchantCode");
				String doubleVerificationURL = prop.getProperty("doubleVerificationUrl");
				String dbURL = prop.getProperty("db.URL");
				String dbUname = prop.getProperty("db.uname");
				String dbPword = prop.getProperty("db.password");
				String exam = prop.getProperty("exam");
				String year = prop.getProperty("year");

				Class.forName("org.postgresql.Driver");
				connection = DriverManager.getConnection(dbURL, dbUname, dbPassword);
				//connection = DriverManager.getConnection("jdbc:postgresql://localhost:54032/jam20db","jam_app", "jam_app371");
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

		int count = 0;
		String lastpaymentId = "20000001";
		while( true ){	

			try{
				PaymentUpdate
					
				ResultSet rs = null;
				String query = "select payment_id, gateway_transaction_reference_no, fee_amount, application_id from payment_details where is_payment_received = false and payment_id > "+lastpaymentId+" and application_id in ( select application_id from applicant where  status_id = 1 )";
					
				Statement smt = conn.createStatement();
				rs =  smt.executeQuery( query );

				while( rs.next() ){

					try{
						String applicationId = rs.getString("application_id");
						String gatewayId = rs.getString("gateway_transaction_reference_no");
						String paymentId = rs.getString("payment_id");
						String feeAmount = rs.getString("fee_amount");
						lastpaymentId = paymentId;

						String mobileNo = "9819095335";
						try{
							Statement tsmt = conn.createStatement();
							String mQuery = "select mobile_number, email from users where application_id = '"+applicationId+"'";
							ResultSet trs = tsmt.executeQuery( mQuery );
							if( trs.next() ){	
								mobileNo = trs.getString("mobile_number");
							}
						}catch(Exception e){
							e.printStackTrace();
						}

						SBIOnlinePaymentResponse sbiPaymentResponse = new SBIOnlinePaymentResponse();

						SBIPaymentResponseBean response = sbiPaymentResponse.paymentDoubleVerification(feeAmount, paymentId, merchantCode, "https://merchant.onlinesbi.com/thirdparties/doubleverification.htm");
				
						if( response != null ){	
							String SMSText =  "Dear Applicant,\n\nJOAPS 2020 has received your payment with Payment-Id "+response.getSbi_ref_no()+"\n\n-----\nJAM 2020 Team";

							//System.out.println("#Payment Update: "+response.getStatus()+","+applicationId+","+gatewayId+","+paymentId+","+feeAmount+", "+response.toString());							

							if(response.getStatus() != null && response.getStatus().toUpperCase().equals("SUCCESS") ){


								if( (response.getAmount()+"").trim().equals( feeAmount.trim() ) && response.getSbi_ref_no() != null && response.getTimestamp().indexOf("2019") >= 0 ){

									String token[] = response.getTimestamp().split("/"); 	
									String date = token[1]+"/"+token[0]+"/"+token[2];

									String updateApplicant = "UPDATE applicant set status_id = 12 where application_id = '"+applicationId+"' and ( status_id = 1 )";
									String paymentDetails = "UPDATE payment_details set gateway_transaction_reference_no = '"+response.getSbi_ref_no()+"', is_payment_received = true, gateway_payment_status_id = '0300', gateway_transaction_date = '"+date+"' where payment_id = "+response.getPayment_Id()+" and application_id = '"+applicationId+"'";							    	   

									System.out.println( paymentDetails );
									System.out.println( updateApplicant );
									
									Connection con = getConnection();
									Statement tsmt = con.createStatement();
									try{
										con.setAutoCommit(false);
										int n = tsmt.executeUpdate( updateApplicant );	

										if( n > 0){

											tsmt.executeUpdate( paymentDetails );
											System.out.println("#Payment Update: "+response.getStatus()+","+applicationId+","+gatewayId+","+paymentId+","+feeAmount);							
											System.out.println("#Double Verification Output: "+response.toString());
											String smsInsertQuery =  "INSERT INTO sms_queue(mobile_number, message, time_stamp) values ('"+mobileNo+"','"+SMSText+"', now() )";
											tsmt.executeUpdate( smsInsertQuery );
											con.commit();
										}
									}catch(Exception e){
										con.rollback();
										e.printStackTrace();
									}finally{
										try{
											con.close();
										}catch(Exception e){
											e.printStackTrace();
										}		
									}
								}	

							}

						}

					}catch(Exception e){
						e.printStackTrace();
					}
				}

			}catch(Exception e){
				e.printStackTrace();
			}finally{
				try{
					count++;
					System.err.println("Iterration done! "+count);
					conn.close();
					Thread.sleep(99999);

				}catch(Exception e){
					e.printStackTrace();
				}
			}	

		}

	}
}
