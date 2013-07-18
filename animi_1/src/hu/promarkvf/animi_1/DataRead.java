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

public class DataRead extends AsyncTask<String, Integer, String> {
	private Context context = null;
	private ProgressDialog progressDialog = null;
	private long responseLength;

	public DataRead(Context context) {
		super();
		this.context = context;
	}

	@Override
	protected String doInBackground(String... params) {
		AndroidHttpClient httpClient = null;
		String resp = null;
		List<NameValuePair> nameValuePairs = null;
		if ( params.length > 0 ) {
			try {
				httpClient = AndroidHttpClient.newInstance("Android");
				HttpPost httppost = new HttpPost(MainTabActivity.JSONUlr + params[0]);
				for ( String param : params ) {
					String[] posts = param.split("=");
					if ( posts.length > 1 ) {
						nameValuePairs = new ArrayList<NameValuePair>(2);
						nameValuePairs.add(new BasicNameValuePair(posts[0], posts[1]));
					}
				}
				if ( nameValuePairs != null ) {
					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				}
				resp = httpClient.execute(httppost, new BasicResponseHandler());

			}
			catch ( IOException e ) {
				e.printStackTrace();
			}
			finally {
				if ( httpClient != null ) {
					httpClient.close();
				}
			}
		}
		return resp;
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		double percent = 0;
		if ( responseLength != 0 ) {
			percent = 100 * values[0] / responseLength;
		}
		Log.d("AsyncTask progress", "" + percent + " %");
	}

}
