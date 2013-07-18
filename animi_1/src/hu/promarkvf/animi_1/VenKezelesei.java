package hu.promarkvf.animi_1;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class VenKezelesei extends AsyncTask<String, Integer, String> {

	private Context context = null;
	private ProgressDialog progressDialog = null;
	private long responseLength;
	
	public VenKezelesei(Context context) {
	    this.context = context; 
	}
	
	@Override
	protected void onCancelled() {
		super.onCancelled();
	}

	@Override
	protected String doInBackground(String... params) {
		MainActivity.venkezeles = new VenKezeles(params[0]);
//		MainActivity.venkezeles = new VenKezeles(319);
		return null;
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
