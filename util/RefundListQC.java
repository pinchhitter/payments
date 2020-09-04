package util;

import java.util.*;
import java.io.*;
import java.sql.*;

class RefundListQC{

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

		try{

			Connection conn = getConnection();
			ResultSet rs = null;
			String merchantCode = "IIT_CHENNAI";
			try{
				String query = "select payment_id, gateway_transaction_reference_no, fee_amount, application_id from payment_details_contest_answer_key where is_payment_received = false";

				Statement smt = conn.createStatement();
				rs =  smt.executeQuery( query );

				while( rs.next() ){

					try{
						String applicationId = rs.getString("application_id");
						String gatewayId = rs.getString("gateway_transaction_reference_no");
						String paymentId = rs.getString("payment_id");
						String feeAmount = rs.getString("fee_amount");

						System.out.println(feeAmount+", "+paymentId+" -m IIT_CHENNAI");	

						/*

						SBIOnlinePaymentResponse sbiPaymentResponse = new SBIOnlinePaymentResponse();
						SBIPaymentResponseBean response = sbiPaymentResponse.paymentDoubleVerification(feeAmount, paymentId, merchantCode, "https://www.onlinesbi.com/thirdparties/doubleverification.htm");

						if( response != null ){	

							if(response.getStatus() != null && response.getStatus().toUpperCase().equals("SUCCESS") ){

								if( (response.getAmount()+"").trim().equals( feeAmount.trim() ) && response.getSbi_ref_no() != null  ){
									System.out.println("#Payment Refund: "+response.getStatus()+","+applicationId+","+response.getSbi_ref_no()+","+paymentId+","+feeAmount);							
									System.out.println("#Double-Verification-Output: "+response.toString());
								}	
							}
						}
						*/	
					}catch(Exception e){
						e.printStackTrace();
					}
						
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				try{
					conn.close();
				}catch(Exception e){
					e.printStackTrace();
				}
			}	
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
