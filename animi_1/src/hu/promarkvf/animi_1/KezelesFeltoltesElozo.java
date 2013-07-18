package hu.promarkvf.animi_1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.message.BasicNameValuePair;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;

public class KezelesFeltoltesElozo extends AsyncTask<String, Integer, String> {
	private Context context = null;
	private ProgressDialog progressDialog = null;
	private long responseLength;
	
	public KezelesFeltoltesElozo(Context context) {
	    this.context = context; 
	}
	
	@Override
	protected void onCancelled() {
		super.onCancelled();
	}

	@Override
	protected String doInBackground(String... selectedItem) {
		AndroidHttpClient httpClient = null;
		try {
			httpClient = AndroidHttpClient.newInstance("Android");
			String ulr = MainActivity.JSONUlr + MainActivity.JSONKezelesId;
			HttpPost httppost = new HttpPost(ulr);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("val_elozokezeles_id", selectedItem[0]));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			String resp = httpClient.execute(httppost, new BasicResponseHandler());
			KezelesFeltoltes.kezelesFill(resp);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (httpClient != null)
				httpClient.close();
		}
		if (MainActivity.kezeles.osszlepes != null) {
			return "1";
		} else {
			return "0";
		}
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		double percent = 0;
		if (responseLength != 0) {
			percent = 100*values[0]/responseLength;
		}
		Log.d("AsyncTask progress", ""+percent+" %");
	}
	
}
