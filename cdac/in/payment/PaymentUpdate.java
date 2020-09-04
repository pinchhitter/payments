package cdac.in.payment;

import java.util.*;
import java.io.*;
import java.sql.*;

import java.util.Properties;

class PaymentUpdate{

	public static void main(String[] args){

		int count = 0;
		Payment payment = new Payment();
		Connection conn = null;

		String lastpaymentId = "52047553";

		while( true ){	

			try{
				ResultSet rs = null;
				String query = "select payment_id, gateway_transaction_reference_no, fee_amount, application_id from payment_details where is_payment_received = false and payment_id >= "+lastpaymentId+" and payment_id < 52047683 and application_id in ( select application_id from applicant where  status_id = 1 )";
				conn = payment.getConnection();
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
						String applicantName = "Shekhar";
						try{
							Statement tsmt = conn.createStatement();
							String mQuery = "select mobile_number, email, applicant_name from users where application_id = '"+applicationId+"'";
							ResultSet trs = tsmt.executeQuery( mQuery );
							if( trs.next() ){	
								mobileNo = trs.getString("mobile_number").trim();
								applicantName = trs.getString("applicant_name").trim();
							}
						}catch(Exception e){
							e.printStackTrace();
						}

						SBIOnlinePaymentResponse sbiPaymentResponse = new SBIOnlinePaymentResponse();
						System.out.println( paymentId ) ;	

						SBIPaymentResponseBean response = sbiPaymentResponse.paymentDoubleVerification( feeAmount, paymentId, payment.getMerchantCode(), payment.getDoubleVerificationURL() );
							

						if( response != null ){	

							String SMSText =  "Dear "+applicantName+",\n"+payment.getExam()+" "+payment.getYear()+" has received your payment with Payment-Id "+response.getSbi_ref_no()+"\n\n-----\n"+payment.getExam()+" "+payment.getYear()+" Team";

							if(response.getStatus() != null && response.getStatus().toUpperCase().equals("SUCCESS") ){

								if( (response.getAmount()+"").trim().equals( feeAmount.trim() ) && response.getSbi_ref_no() != null ){
									String token[] = response.getTimestamp().split("/"); 	
									String date = token[1]+"/"+token[0]+"/"+token[2];

									String updateApplicant = "UPDATE applicant set status_id = 12 where application_id = '"+applicationId+"' and ( status_id = 1 )";
									String paymentDetails = "UPDATE payment_details set gateway_transaction_reference_no = '"+response.getSbi_ref_no()+"', is_payment_received = true, gateway_payment_status_id = '0300', gateway_transaction_date = '"+date+"' where payment_id = "+response.getPayment_Id()+" and application_id = '"+applicationId+"'";							    	   

									System.out.println( paymentDetails );
									System.out.println( updateApplicant );

									Connection con = payment.getConnection();
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
					if( count == 2 )
						System.exit(0);

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
