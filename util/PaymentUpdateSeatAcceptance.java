package util;

import java.util.*;
import java.io.*;
import java.sql.*;

class PaymentUpdateSeatAcceptance{

	static Connection getConnection(){

		Connection connection = null;
		try{
			Class.forName("org.postgresql.Driver");
			connection = DriverManager.getConnection("jdbc:postgresql://localhost:54032/jam16db","jam_app", "jam_app198");
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		return connection;
	}


	public static void main(String[] args){

		int count = 0;

		while( true ){	

			Connection conn = null;
			try{
				conn = getConnection();
				ResultSet rs = null;
				String merchantCode = "IIT_CHENNAI";

				String query = "select payment_id, gateway_transaction_reference_no, fee_amount, application_id from seat_acceptance_payment_details where is_payment_received = false and application_id in ( select application_id from seat_allocation where status_id = 1  )";
					

				Statement smt = conn.createStatement();
				rs =  smt.executeQuery( query );

				while( rs.next() ){

					try{
						String applicationId = rs.getString("application_id");
						String gatewayId = rs.getString("gateway_transaction_reference_no");
						String paymentId = rs.getString("payment_id");
						String feeAmount = rs.getString("fee_amount");

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
						String SMSText =  "Dear Applicant,\n\nJAM 2020 (JOAPS) has received your seat acceptance fee payment with Payment-Id "+response.getSbi_ref_no()+"\n\n---\nJAM 2020 Team";

						if( response != null ){	

							if(response.getStatus() != null && response.getStatus().toUpperCase().equals("SUCCESS") ){


								if( (response.getAmount()+"").trim().equals( feeAmount.trim() ) && response.getSbi_ref_no() != null  ){

									String updateApplicant = "UPDATE seat_allocation set status_id = 3 where application_id = '"+applicationId+"' and (status_id = 1 )";
									String paymentDetails = "UPDATE seat_acceptance_payment_details set gateway_transaction_reference_no = '"+response.getSbi_ref_no()+"', is_payment_received = true  where payment_id = "+response.getPayment_Id()+" and application_id = '"+applicationId+"'";							    	   
									Connection con = getConnection();
									Statement tsmt = con.createStatement();
									try{
										String tupdateApplicant = "UPDATE seat_allocation set action_id = 3, status_id = 3 where application_id = '"+applicationId+"' AND action_id = 1 AND status_id = 1";

										con.setAutoCommit(false);

										int n = tsmt.executeUpdate( tupdateApplicant );	
										if( n == 0){
										    n = tsmt.executeUpdate( updateApplicant );	
										}	
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
