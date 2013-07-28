package hu.promarkvf.animi_1;

import java.io.IOException;
import java.net.URLEncoder;

import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;

public class KezelesFeltoltes extends AsyncTask<String, Integer, String> {
	private Context context = null;
	private ProgressDialog progressDialog = null;
	private long responseLength;

	public KezelesFeltoltes(Context context) {
		this.context = context;
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
	}

	@Override
	protected String doInBackground(String... selectedItem) {

		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, "UTF-8");
		params.setBooleanParameter("http.protocol.expect-continue", false);
		AndroidHttpClient httpClient = null;
		try {
			httpClient = AndroidHttpClient.newInstance("Android");
			String ulr = MainActivity.JSONUlr + MainActivity.JSONKezelesId + "?val_kezeles_azonosito=" + URLEncoder.encode(selectedItem[0], "UTF-8");
			HttpGet httpGet = new HttpGet(ulr);
			String resp = httpClient.execute(httpGet, new BasicResponseHandler());
			kezelesFill(resp);
		}
		catch ( IOException e ) {
			e.printStackTrace();
		}
		finally {
			if ( httpClient != null )
				httpClient.close();
		}
		// ------------------
		if ( MainActivity.kezeles.osszlepes != null ) {
			return "1";
		}
		else {
			return "0";
		}
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		double percent = 0;
		if ( responseLength != 0 ) {
			percent = 100 * values[0] / responseLength;
		}
		Log.d("AsyncTask progress", "" + percent + " %");
	}

	public static void kezelesFill(String resp) {
		Integer i = 0;
		Integer j = 0;
		Integer k = 0;

		try {
			// --- JSON feldolgozás
			JSONObject root = new JSONObject(resp);

			// -- üress-e a root
			if ( !root.isNull("posts") ) {
				JSONObject jsonkezeles = root.getJSONObject("posts");
				MainActivity.kezeles = new Kezeles();

				MainActivity.kezeles.kezeles_id = jsonkezeles.getInt("kezeles_id");
				MainActivity.kezeles.azonosito = jsonkezeles.getString("azonosito");
				MainActivity.kezeles.berendezes_dim1 = jsonkezeles.getInt("berendezes_dim1");
				MainActivity.kezeles.berendezes_dim2 = jsonkezeles.getInt("berendezes_dim2");
				MainActivity.kezeles.leiras = jsonkezeles.getString("leiras");
				MainActivity.kezeles.diagnozis = jsonkezeles.getString("diagnozis");
				MainActivity.kezeles.anamnezis = jsonkezeles.getString("anamnezis");
				MainActivity.kezeles.osszlepes = jsonkezeles.getInt("osszlepes");
				MainActivity.kezeles.osszido = jsonkezeles.getInt("osszido");

				MainActivity.kezeles.lepes = new KezelesLepes[MainActivity.kezeles.osszlepes + 1];

				for ( i = 1; i <= MainActivity.kezeles.osszlepes; i++ ) {
					JSONObject jsonkezelesLepes = jsonkezeles.getJSONObject(i.toString());
					MainActivity.kezeles.lepes[i] = new KezelesLepes(MainActivity.kezeles.berendezes_dim1, MainActivity.kezeles.berendezes_dim2);
					MainActivity.kezeles.lepes[i].sorszam = jsonkezelesLepes.getInt("sorszam");
					MainActivity.kezeles.lepes[i].kezeles_reszletekid = jsonkezelesLepes.getInt("kezeles_reszletekid");
					MainActivity.kezeles.lepes[i].lepes = jsonkezelesLepes.getString("lepes");
					MainActivity.kezeles.lepes[i].ido = jsonkezelesLepes.getInt("ido");
					MainActivity.kezeles.lepes[i].szin_r = jsonkezelesLepes.getInt("szin_r");
					MainActivity.kezeles.lepes[i].szin_g = jsonkezelesLepes.getInt("szin_g");
					MainActivity.kezeles.lepes[i].szin_b = jsonkezelesLepes.getInt("szin_b");
					MainActivity.kezeles.lepes[i].pxtomb = jsonkezelesLepes.getString("pxtomb");
					MainActivity.kezeles.lepes[i].megjegyzes = jsonkezelesLepes.getString("megjegyzes");
				}
			}
		}
		catch ( JSONException e ) {
			e.printStackTrace();
		}
	}

}
