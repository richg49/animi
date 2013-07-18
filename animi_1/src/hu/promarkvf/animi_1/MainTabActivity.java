package hu.promarkvf.animi_1;

import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;

public class MainTabActivity extends FragmentActivity {
	private FragmentTabHost mTabHost;
	static Context maincontext;
	static String JSONUlr; 
	static String JSONVendegAll;
	String JSONKezelesAll;
	static String JSONKezelesId;
	String animiserverIP;
	int animiserverPort;
	int animiserverTimeout;
	String animiserverulr;
	String login_user;
	String login_password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		maincontext = MainTabActivity.this;
		setContentView(R.layout.activity_main_tab);
		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
		mTabHost.addTab(mTabHost.newTabSpec(getString(R.string.Vendeg)).setIndicator(getString(R.string.Vendeg)), VendegActivity.class, null);
		
		mTabHost.addTab(mTabHost.newTabSpec("Régi felület").setIndicator("Régi felület", getResources().getDrawable(R.drawable.ic_launcher)), MainActivity.class, null);
		// mTabHost.addTab(mTabHost.newTabSpec("Status").setIndicator("Status",
		// getResources().getDrawable(R.drawable.ic_device_tab)),
		// DeviceFragment.class, null);
		loadPref();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent();
//		intent.setClass(maincontext, SetPreferenceActivity.class);
		intent.setClass(maincontext , MainActivity.class);
		startActivityForResult(intent, 0);

		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
		animiserverTimeout = Integer.parseInt(mySharedPreferences.getString("@string/animiserverTimeout", "5000"));
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
	 * This method converts dp unit to equivalent pixels, depending on device density. 
	 * 
	 * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
	 * @param context Context to get resources and device specific display metrics
	 * @return A float value to represent px equivalent to dp depending on device density
	 */
	public static int convertDpToPixel(float dp, Context context){
	    Resources resources = context.getResources();
	    DisplayMetrics metrics = resources.getDisplayMetrics();
	    int px = Math.round( dp * (metrics.densityDpi / 160f));
	    return px;
	}


}
