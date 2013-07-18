package hu.promarkvf.animi_1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.http.AndroidHttpClient;

public class VenKezeles {
	public ElozoKezeles[] elozokezeles;
	
	public VenKezeles (String vendegid) {
		AndroidHttpClient httpClient = null;
		httpClient = AndroidHttpClient.newInstance("Android");
		String ulr = MainActivity.JSONUlr+"webservice_vendegid.php";
		HttpPost httppost = new HttpPost(ulr);
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("val_vendeg_id", vendegid));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			String resp = httpClient.execute(httppost, new BasicResponseHandler());

			JSONObject root = new JSONObject(resp);
			// -- üress-e a root
			if(!root.isNull("posts")) {
				JSONObject jsonkezelesek = root.getJSONObject("posts");
				int db = jsonkezelesek.length();
				elozokezeles = new ElozoKezeles[db + 1];
				// --- JSON feldolgozás
				for(int i=1; i <=db; i++ ) {
					JSONObject jsonkezelesekr = jsonkezelesek.getJSONObject(Integer.toString(i));
					elozokezeles[i] = new ElozoKezeles();
					elozokezeles[i].id = jsonkezelesekr.getInt("id");
					elozokezeles[i].leiras = jsonkezelesekr.getString("leiras");
					elozokezeles[i].mw_kezeles_id = jsonkezelesekr.getInt("mw_kezeles_id");
					elozokezeles[i].datum = jsonkezelesekr.getString("datum");
				}
			}
					
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			if (httpClient != null)
				httpClient.close();
		}
	}
	
	public class ElozoKezeles {
		public Integer id; 
		public String leiras; 
		public Integer mw_kezeles_id; 
		public String datum; 
		
		public ElozoKezeles () {
			id = 0;
			leiras = "";
			mw_kezeles_id = 0;
			datum = "";
		}
	}
}