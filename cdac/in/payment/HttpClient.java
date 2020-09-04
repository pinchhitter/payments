package cdac.in.payment; 

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.xml.sax.InputSource;

public class HttpClient
{
	private static final String USER_AGENT = "Mozilla/5.0";
	private static final String ERROR = "error";

	public static void main(String[] args) throws Exception{

		HttpClient http = new HttpClient();

	}

	public String parseXMLResponseString(String xmlStr){

		SAXBuilder jdomBuilder = new SAXBuilder();
		String urlParams = null;

		try
		{
			InputSource is = new InputSource(new StringReader(xmlStr));
			Document xmlDoc = null;

			try
			{
				xmlDoc = jdomBuilder.build(is);

				Element root = xmlDoc.getRootElement();
				Element response = root.getChild("MERCHANT").getChild("RESPONSE");

				urlParams = response.getChildText("url") + "?";
				List<Element> params = response.getChildren("param");

				for(int i = 0; i < params.size(); i++)
				{
					Element param = params.get(i);
					urlParams += param.getAttributeValue("name") + "=" + param.getText() + "&";
				}

				urlParams = urlParams.substring(0, (urlParams.length() - 1));
				System.out.println("URL= " + urlParams);

				return urlParams;
			}
			catch (JDOMException saxe)
			{
				System.out.println("JDOMException occurred while parsing using JDOM Parser!" + saxe);
			}
			catch (IOException ioe)
			{
				System.out.println("IOException occurred while using JDOM Parser!" + ioe);
			}
		}
		catch (Exception e)
		{
			System.out.println("Exception occurred while using JDOM Parser!" + e);
		}

		return "ERROR";
	}

	public String sendHttpGet(String url) throws Exception{

		URL urlObj = null;

		StringBuffer response = new StringBuffer();

		try{
			urlObj = new URL(url);

			try{
				HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();
				con.setRequestMethod("GET");
				con.setRequestProperty("User-Agent", USER_AGENT);
				int responseCode = con.getResponseCode();

				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

				String inputLine;

				while ((inputLine = in.readLine()) != null)
				{
					response.append(inputLine);
				}

				in.close();

				return response.toString();
			}
			catch (IOException ioe)
			{
				System.out.println("IOException occurred!" + ioe);
			}
			catch (Exception e)
			{
				System.out.println("Exception occurred!" + e);
			}
		}
		catch (MalformedURLException mfue)
		{
			System.out.println("MalformedURLException occurred while parsing URL!" + mfue);
		}
		catch (Exception e)
		{
			System.out.println("Exception occurred!" + e);
		}

		return "ERROR";
	}

	public String sendHttpsGet(String url) throws Exception{

		URL urlObj = null;
		StringBuffer response = new StringBuffer();

		try{
			urlObj = new URL(url);

			try{
				HttpsURLConnection con = (HttpsURLConnection) urlObj.openConnection();
				con.setRequestMethod("GET");
				con.setRequestProperty("User-Agent", USER_AGENT);

				int responseCode = con.getResponseCode();
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;

				while ((inputLine = in.readLine()) != null)
				{
					response.append(inputLine);
				}

				in.close();

				return response.toString();
			}
			catch (IOException ioe)
			{
				System.out.println("IOException occurred!" + ioe);
			}
			catch (Exception e)
			{
				System.out.println("Exception occurred!" + e);
			}
		}
		catch (MalformedURLException mfue)
		{
			System.out.println("MalformedURLException occurred while parsing URL!" + mfue);
		}
		catch (Exception e)
		{
			System.out.println("Exception occurred!" + e);
		}

		return "ERROR";
	}

	public String sendHttpPost(String url, String urlParams) throws Exception{
		URL urlObj = null;
		StringBuffer response = new StringBuffer();

		try{
			urlObj = new URL(url);

			try{
				HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();

				con.setRequestMethod("POST");
				con.setRequestProperty("User-Agent", USER_AGENT);
				con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
				con.setDoOutput(true);

				DataOutputStream wr = new DataOutputStream(con.getOutputStream());
				wr.writeBytes(urlParams);
				wr.flush();
				wr.close();

				int responseCode = con.getResponseCode();
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

				String inputLine;
				while ((inputLine = in.readLine()) != null)
				{
					response.append(inputLine);
				}

				in.close();

				return response.toString();
			}
			catch (IOException ioe)
			{
				System.out.println("IOException occurred!" + ioe);
			}
			catch (Exception e)
			{
				System.out.println("Exception occurred!" + e);
			}
		}
		catch (MalformedURLException mfue)
		{
			System.out.println("MalformedURLException occurred while parsing URL!" + mfue);
		}
		catch (Exception e)
		{
			System.out.println("Exception occurred!" + e);
		}

		return "ERROR";
	}

	public String sendHttpsPost(String url, String urlParams) throws Exception{
		URL urlObj = null;
		StringBuffer response = new StringBuffer();

		try{
			urlObj = new URL(url);
			HttpsURLConnection con = (HttpsURLConnection) urlObj.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("User-Agent", USER_AGENT);
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
			con.setDoOutput(true);

			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(urlParams);
			wr.flush();
			wr.close();

			int responseCode = con.getResponseCode();
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

			String inputLine;
			while ( ( inputLine = in.readLine()) != null ){
				response.append(inputLine);
			}

			in.close();

			return response.toString();
		}
		catch (MalformedURLException mfue){
			System.out.println("MalformedURLException occurred!" + mfue);
		}
		catch (IOException ioe) {
			System.out.println("IOException occurred while parsing URL!" + ioe);
		}
		catch (Exception e) {
			System.out.println("Exception occurred!" + e);
		}

		return "ERROR";
	}
}
