package util;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.naming.NamingException;

import java.io.File;
import java.io.FileInputStream;
import javax.naming.NamingException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class SBIOnlinePaymentResponse{

	private static final long serialVersionUID = 1L;

	private String encdata;

	static byte[] returnbyte(String path)
	{
		byte[] abyte = null;
		try {
			FileInputStream fileinputstream = new FileInputStream(path);
			abyte = new byte[fileinputstream.available()];
			fileinputstream.read(abyte);
			fileinputstream.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return abyte;
	}

	private boolean verifyCheckSums( String decryptedResponseData )
	{
		String respParamsWithoutCheckSum = decryptedResponseData.substring(0, decryptedResponseData.indexOf("checkSum") - 1);
		String respCheckSum = decryptedResponseData.substring(decryptedResponseData.indexOf("checkSum"));
		respCheckSum = respCheckSum.substring(respCheckSum.indexOf("=") + 1);
		String localComputedCheckSum = CheckSumUtil.checkSumSHA256(respParamsWithoutCheckSum);
		return localComputedCheckSum.equals( respCheckSum );
	}

	public static  String encryptRequest(String data){

		String path = "./util/IIT_BOMBAY_GATE.key";
		byte[] key = null;
		try {
			key = returnbyte(path);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		String encData = null;
		try {
			Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

			int blockSize = cipher.getBlockSize();
			byte[] iv = new byte[cipher.getBlockSize()];
			byte[] dataBytes = data.getBytes();
			int plaintextLength = dataBytes.length;
			int remainder = plaintextLength % blockSize;
			if (remainder != 0) {
				plaintextLength += blockSize - remainder;
			}
			byte[] plaintext = new byte[plaintextLength];

			System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);
			SecretKeySpec keySpec = new SecretKeySpec(key, "AES");

			SecureRandom randomSecureRandom = SecureRandom.getInstance("SHA1PRNG");
			randomSecureRandom.nextBytes(iv);
			GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);
			cipher.init(1, keySpec, parameterSpec);
			byte[] results = cipher.doFinal(plaintext);
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			outputStream.write(iv);
			outputStream.write(results);
			byte[] encrypteddata = outputStream.toByteArray();
			encData = Base64.encodeBase64String(encrypteddata);
			encData = encData.replace("\n", "").replace("\r", "");

		} catch (Exception ex) {
			System.out.println("Exception occured :" + ex);
		}

		return encData;
	}

	public static String decryptResponse(String encData)
	{
		String decdata = null;

		String path = "./util/IIT_BOMBAY_GATE.key";

		byte[] key = null;

		try {
			key = returnbyte(path);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		try {

			Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
			SecretKeySpec keySpec = new SecretKeySpec( key, "AES");

			byte[] results = Base64.decodeBase64(encData);
			byte[] iv = Arrays.copyOfRange(results, 0, cipher.getBlockSize());
			cipher.init(2, keySpec, new GCMParameterSpec(128, iv));
			byte[] results1 = Arrays.copyOfRange(results, cipher.getBlockSize(), results.length);
			byte[] ciphertext = cipher.doFinal(results1);
			decdata = new String(ciphertext).trim();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	return decdata;
	}

	private String prepareDoubleVerificationReqData(SBIPaymentResponseBean sbiPaymentResponseBean){

		String reqString = "payment_Id=" + urlEncodeParam("" + sbiPaymentResponseBean.getPayment_Id())
			+ "|amount=" + urlEncodeParam("" + sbiPaymentResponseBean.getAmount().intValueExact());

		return reqString + "|checkSum=" + CheckSumUtil.checkSumMD5(reqString);
	}

	public String urlEncodeParam(String param) {

		if(param == null) {
			return null;
		}
		try {
			return URLEncoder.encode(param, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			System.out.println("GTDG:XXX: UnsupportedEncodingException occurred in urlEncodeParam!");
			return null;
		}
	}

	private void populateResponseBean(String pipeDelimitedText, SBIPaymentResponseBean sbiResBean){

		String[] tokens = pipeDelimitedText.split("\\|");
		int i = 0;
		try{
			while (i < tokens.length)
			{
				String token = tokens[i].trim();
				try{
					String[] tk = token.split("=");

					tk[0] = tk[0].trim();
					tk[1] = tk[1].trim();

					if (tk[0].equals("amount"))
					{
						sbiResBean.setAmount(new BigDecimal(tk[1]));
					}
					else if (tk[0].equals("bank_name"))
					{
						sbiResBean.setBank_name(tk[1]);

					}
					else if (tk[0].equals("payment_Id"))
					{
						sbiResBean.setPayment_Id(Integer.parseInt(tk[1]));

					}
					else if (tk[0].equals("status"))
					{
						sbiResBean.setStatus(tk[1]);

					}
					else if (tk[0].equals("status_desc"))
					{
						sbiResBean.setStatus_desc(tk[1]);

					}
					else if (tk[0].equals("ttype"))
					{
						sbiResBean.setTtype(tk[1]);

					}
					else if (tk[0].equals("sbi_ref_no")){
						sbiResBean.setSbi_ref_no(tk[1]);
					}
					else if (tk[0].equals("timestamp")) {
						sbiResBean.setTimestamp(tk[1]);
					}
				}catch(Exception e){
					//e.printStackTrace();
				}
				i++;
			}
		} catch(Exception e) {
			//System.out.println("Exception : " + e);
		}
	}

	public SBIPaymentResponseBean paymentDoubleVerification(String feeAmount, String paymentId, String merchantCode, String dblVerificationURL){
		try{

			HttpClient httpClient = new HttpClient();

			String reqString = "payment_Id=" + urlEncodeParam(paymentId) + "|amount=" + urlEncodeParam(feeAmount); 

			reqString = reqString + "|checkSum=" + CheckSumUtil.checkSumMD5(reqString);
			String encryptedDblVerificationReqData = encryptRequest(reqString);
			encryptedDblVerificationReqData = urlEncodeParam(encryptedDblVerificationReqData);

			if(encryptedDblVerificationReqData == null){
				System.out.println("null value returned from urlEncodeParam! Please verify encodeDoubleVerificationReqData!");
			}

			encryptedDblVerificationReqData = "encdata=" + encryptedDblVerificationReqData + "&merchant_code=" + merchantCode;

			String dblVerificationEncryptedResp = httpClient.sendHttpsPost(dblVerificationURL, encryptedDblVerificationReqData);
			String dblVerificationDecryptedResp = decryptResponse(dblVerificationEncryptedResp);

			SBIPaymentResponseBean sbiPaymentDblVerificationResponseBean = new SBIPaymentResponseBean();
			populateResponseBean(dblVerificationDecryptedResp, sbiPaymentDblVerificationResponseBean);

			if(verifyCheckSums( dblVerificationDecryptedResp ) ) {
				return sbiPaymentDblVerificationResponseBean;
			}
			else {
				return null;
			}
		}
		catch (Exception e) {
			//System.out.println("Exception occurred!" + e);
		}
		return null;
	}


	public static void main(String[] args){

		int i =0;

		String feeAmount = null, merchantCode = null, paymentId = null;
		merchantCode = "IIT_BOMBAY_GATE";

		while(i < args.length)
		{
			if(args[i].equals("-f"))
			{
				feeAmount = args[i+1].trim();
				i++;
			}
			else if(args[i].equals("-m"))
			{
				merchantCode = args[i+1].trim();
				i++;
			}
			else if(args[i].equals("-pid") || args[i].equals("-p"))
			{
				paymentId = args[i+1].trim();
				i++;
			}
			i++;
		}

		if(feeAmount == null || merchantCode == null || paymentId == null)
		{
			System.out.println("Usage: java -cp ./lib:. -f 1500 -pid 10000241");
			System.exit(0);
		}
		SBIOnlinePaymentResponse sbiPaymentResponse = new SBIOnlinePaymentResponse();
		SBIPaymentResponseBean response = sbiPaymentResponse.paymentDoubleVerification(feeAmount, paymentId, merchantCode, "https://merchant.onlinesbi.com/thirdparties/doubleverification.htm");
		System.out.println( response.toString() );	
	}
}
