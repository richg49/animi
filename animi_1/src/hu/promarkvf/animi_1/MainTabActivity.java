package hu.promarkvf.animi_1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainTabActivity extends FragmentActivity {
	final static int MOD_MEGJ_ACTIVITY_ID = 1;
	final static int TEXTSIZE = 18;
	static FragmentTabHost mTabHost;
	static Context maincontext;
	static String JSONUlr;
	static String JSONVendegAll;
	static String JSONKezelesAll;
	static String JSONKezelesId;
	static String animiserverIP;
	static int animiserverPort;
	static int animiserverTimeout;
	static String animiserverulr;
	static String login_user;
	static String login_password;
	Bundle bundle;
	protected static int sel_vendeg_pos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		bundle = new Bundle();
		sel_vendeg_pos = 0;

		maincontext = MainTabActivity.this;
		setContentView(R.layout.activity_main_tab);
		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
		mTabHost.addTab(mTabHost.newTabSpec(getString(R.string.Vendeg)).setIndicator(getString(R.string.Vendeg)), VendegActivity.class, bundle);

		mTabHost.addTab(mTabHost.newTabSpec(getString(R.string.FejlecKezelesek)).setIndicator(getString(R.string.FejlecKezelesek), getResources().getDrawable(R.drawable.ic_launcher)), UjKezelesActivity.class, null);
		mTabHost.addTab(mTabHost.newTabSpec(getString(R.string.BtnStart)).setIndicator(getString(R.string.BtnStart), getResources().getDrawable(R.drawable.ic_launcher)), StartKezelesActivity.class, null);
		// mTabHost.addTab(mTabHost.newTabSpec("Status").setIndicator("Status",
		// getResources().getDrawable(R.drawable.ic_device_tab)),
		// DeviceFragment.class, null);

		loadPref();

	}

/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent();
		// intent.setClass(maincontext, SetPreferenceActivity.class);
		intent.setClass(maincontext, MainActivity.class);
		startActivityForResult(intent, 0);

		return true;
	}
*/
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch ( requestCode ) {
		case MainTabActivity.MOD_MEGJ_ACTIVITY_ID:
			if ( resultCode == Activity.RESULT_OK ) {
				CharSequence megjegyzes = (CharSequence) data.getParcelableExtra("Megjegyzes");
				TextView editmegj = (TextView) findViewById(R.id.TextViewMegjegyzes);
				if ( editmegj != null && megjegyzes != null ) {
					editmegj.setText(megjegyzes);
				}
			}
			break;
		}

		loadPref();
	}

	/*
	 * Beállítások betöltése
	 */
	private void loadPref() {
		SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		// JSONUlr =
		// mySharedPreferences.getString("@string/prefJSONUlr","http://10.1.6.11/animiwebservice/");
		login_user = mySharedPreferences.getString("Azonosito", "");
		login_password = mySharedPreferences.getString("Jelszo", "");
		JSONUlr = mySharedPreferences.getString("JSONUlr", "http://188.6.164.115:63080/animiwebservice/");
		JSONVendegAll = mySharedPreferences.getString("@string/prefJSONVendegAll", "webservice_vendegall.php");
		JSONKezelesAll = mySharedPreferences.getString("@string/prefJSONKezelesAll", "webservice_kezelesall.php");
		JSONKezelesId = mySharedPreferences.getString("@string/prefJSONKezelesid", "webservice_kezelesid.php");
		animiserverulr = mySharedPreferences.getString("@string/prefanimiserverulr", "http://188.6.164.115:63080/serial-1/serial_com.php");
		animiserverIP = mySharedPreferences.getString("@string/animiserverIP", "188.6.164.115");
		animiserverPort = Integer.parseInt(mySharedPreferences.getString("@string/animiserverPort", "8001"));
		animiserverTimeout = Integer.parseInt(mySharedPreferences.getString("@string/animiserverTimeout", "1000"));
	}

	/*
	 * Internet ellenőrzése
	 */
	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if ( netInfo != null && netInfo.isConnectedOrConnecting() ) {
			return true;
		}
		return false;
	}

	/**
	 * This method converts dp unit to equivalent pixels, depending on device
	 * density.
	 * 
	 * @param dp
	 *            A value in dp (density independent pixels) unit. Which we need
	 *            to convert into pixels
	 * @param context
	 *            Context to get resources and device specific display metrics
	 * @return A float value to represent px equivalent to dp depending on
	 *         device density
	 */
	public static int convertDpToPixel(float dp, Context context) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		int px = Math.round(dp * ( metrics.densityDpi / 160f ));
		return px;
	}

	public static Bitmap SetPxtomb(String pxtomb, int dim1, int dim2, int colorw, int nagyitas) {
		int color = Color.BLACK;
		int pos = 0;

		Bitmap.Config conf = Bitmap.Config.ARGB_8888;
		Bitmap bmp = Bitmap.createBitmap(dim1 * nagyitas, dim2 * nagyitas, conf);
		if ( dim1 * dim2 == pxtomb.length() ) {
			for ( int i = 0; i < dim1; i++ ) {
				for ( int j = 0; j < dim2; j++ ) {
					pos = i * dim1 + j;
					int s = Integer.valueOf(pxtomb.charAt(pos));
					if ( s == 48 ) { // 0 érték
						color = Color.BLACK;
					}
					else {
						color = colorw;
					}
					for ( int k = 0; k < nagyitas; k++ ) {
						for ( int l = 0; l < nagyitas; l++ ) {
							bmp.setPixel(nagyitas * i + k, nagyitas * j + l, color);
						}
					}
				}
			}
		}
		return bmp;
	}

}
