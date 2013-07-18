package hu.promarkvf.animi_1;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import yuku.ambilwarna.AmbilWarnaDialog;
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class MainActivity extends Activity {

	public static String JSONUlr; 
	String JSONVendegAll;
	String JSONKezelesAll;
	public static String JSONKezelesId;
	String animiserverIP;
	int animiserverPort;
	int animiserverTimeout;
	String animiserverulr;
	String login_user;
	String login_password;
	public static hu.promarkvf.animi_1.Kezeles kezeles;
	private int aktido = 0;
	private int reszIdo = 0;
	private int kezelesindex = 1;
	TimerTask idoTask_fo;
	TimerTask kezelesTask;
	Socket_io gep_1 = null;
	private boolean pause = false;
	private int pauseTime = 0;
	Resources resources;
	DisplayMetrics metrics;
	int[] szinek;
	String[] szineks;
	ArrayAdapter<String> adapterszinek;
	int[] vendegekid;
	Integer vendegid = 0;
	int[] kezelesekid;
	int kezelesid = 0;
	int islogin = 0;
	int userid = 0;
	String nev = "";
	int cegid = 0;
	String ceg = "";
	JSONObject mysqlobject;
	static VenKezeles venkezeles;
	float fenyero = 1/5f;
	MainActivity maincontext = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		maincontext = MainActivity.this;
		setContentView(R.layout.activity_grid);
		// pref load
		resources = MainActivity.this.getResources();
		metrics = resources.getDisplayMetrics();
		loadPref();
		userell();
		fillSpinner();
		// --- Vendegek lenyiló
		final Spinner spinnerVendeg = (Spinner) findViewById(R.id.spinner_vendegek);
		// --- Kezelesek lenyilo
		final Spinner spinnerKezelesek = (Spinner) findViewById(R.id.spinner_kezelesek);
		// --- Kezeles inditas
		final Button btnStart = (Button) findViewById(R.id.buttonStart);
		// --- Kezeles stop
		final Button btnStop = (Button) findViewById(R.id.buttonStop);
		// --- Kezeles pause
		final Button btnPause = (Button) findViewById(R.id.buttonPause);
		// --- Kezeles inditas
		final Button btnRestart = (Button) findViewById(R.id.buttonRestart);
		btnStart.setEnabled(true);
		btnStop.setEnabled(false);
		btnPause.setEnabled(false);
		btnRestart.setEnabled(false);
		// szinek feltöltése
		szinek = new int[8];
		szinek[0] = Color.WHITE;
		szinek[1] = Color.WHITE;
		szinek[2] = Color.BLACK;
		szinek[3] = Color.BLUE;
		szinek[4] = Color.YELLOW;
		szinek[5] = Color.MAGENTA;
		szinek[6] = Color.GREEN;
		szinek[7] = Color.RED;

		szineks = new String[8];
		szineks[0] = "Válassz";
		szineks[1] = "WHITE";
		szineks[2] = "BLACK";
		szineks[3] = "BLUE";
		szineks[4] = "YELLOW";
		szineks[5] = "MAGENTA";
		szineks[6] = "GREEN";
		szineks[7] = "RED";

		adapterszinek = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, szineks);

		// --- Kezeles inditas
		btnStart.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if ( aktido == 0 ) {
					kezelesindex = 1;
					reszIdo = 0;
					pause = false;
					pauseTime = 0;
//					idoTask_fo = idofut();
					ThreadPolicy tp = ThreadPolicy.LAX;
					StrictMode.setThreadPolicy(tp);
//					gep_1 = new Socket_io("188.6.164.115", 8001, 5000);
					gep_1 = new Socket_io(animiserverIP, animiserverPort, animiserverTimeout);
					if ( gep_1.isConnect() ) {
						idoTask_fo = idofut_socket();
						btnStart.setEnabled(false);
						btnStop.setEnabled(true);
						btnPause.setEnabled(true);
						spinnerKezelesek.setEnabled(false);
						spinnerVendeg.setEnabled(false);
						writedb();
					}
					else {
						Toast.makeText(MainActivity.this, getString(R.string.NotSer2net), Toast.LENGTH_LONG).show();
					}
				}
			}
		});
		// --- Kezeles stop
		btnStop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if ( aktido != 0 ) {
					idoTask_fo.cancel();
					// kezelesTask.cancel();
					aktido = 0;
					TextView tv = (TextView) findViewById(R.id.TextViewAktIdo);
					tv.setText("0");
					TextView tvl = (TextView) findViewById(R.id.textViewLepesAktualis);
					tvl.setText("0");
					pause = false;
					pauseTime = 0;
					// Kikapcsolás
					new Thread() {
						@Override
						public void run() {
							AndroidHttpClient httpClient = null;
							httpClient = AndroidHttpClient.newInstance("Android");
							HttpPost httppost = new HttpPost(animiserverulr);
							try {
								List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
								nameValuePairs.add(new BasicNameValuePair("parancs", "Clear"));
								httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
								httpClient.execute(httppost);
								// Toast.makeText(MainActivity.this,
								// response.toString(),Toast.LENGTH_LONG).show();
							}
							catch ( ClientProtocolException e ) {
								e.printStackTrace();
							}
							catch ( IOException e ) {
								e.printStackTrace();
							}
							catch ( ArrayIndexOutOfBoundsException e ) {
								e.printStackTrace();
							}
							finally {
								if ( httpClient != null )
									httpClient.close();
							}
						}
					}.start();

					SurfaceView sv = (SurfaceView) findViewById(R.id.surfaceView2);
					sv.setBackgroundColor(Color.rgb(0, 0, 0));
					btnPause.setText(getString(R.string.BtnPause));
					btnStart.setEnabled(true);
					btnStop.setEnabled(false);
					btnPause.setEnabled(false);
					btnRestart.setEnabled(false);
					spinnerKezelesek.setEnabled(true);
					spinnerVendeg.setEnabled(true);
				}
			}
		});
		// Pause
		btnPause.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if ( aktido != 0 ) {
					btnStop.setEnabled(true);
					btnPause.setEnabled(false);
					btnRestart.setEnabled(true);
					pauseTime = 0;
					pause = true;
				}
			}
		});
		// restart
		btnRestart.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if ( aktido != 0 ) {
					btnStop.setEnabled(true);
					btnPause.setEnabled(true);
					btnRestart.setEnabled(false);
					btnPause.setText(getString(R.string.BtnPause));
					pauseTime = 0;
					pause = false;
				}
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		Spinner s = (Spinner) findViewById(R.id.spinner_vendegek);
		if ( s.getCount() == 0 ) {
			fillSpinner();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent();
//		intent.setClass(MainActivity.this, SetPreferenceActivity.class);
		intent.setClass(maincontext, MainTabActivity.class);
		startActivityForResult(intent, 0);

		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		loadPref();
	}

	/*
	 * Lenyilló feltöltése webről
	 */
	private int[] fillJSONToSpinner(String ulr, int spinnerId) {
		AndroidHttpClient httpClient = null;
		int[] elemekid = null;
		try {
			httpClient = AndroidHttpClient.newInstance("Android");
			HttpGet httpGet = new HttpGet(ulr);
			final String resp = httpClient.execute(httpGet, new BasicResponseHandler());

			final String array_spinner[];
			// --- JSON feldolgozás

			JSONObject root = new JSONObject(resp);
			JSONArray jArray = root.getJSONArray("posts");
			int jsoncount = jArray.length();
			array_spinner = new String[jsoncount];
			elemekid = new int[jsoncount];

			for ( int i = 0; i < jsoncount; i++ ) {
				JSONObject e = jArray.getJSONObject(i);
				String s = e.getString("post");
				JSONObject jObject = new JSONObject(s);
				array_spinner[i] = URLDecoder.decode(jObject.getString("nev"), "UTF-8");
				elemekid[i] = jObject.getInt("id");
			}

			final Spinner s = (Spinner) findViewById(spinnerId);
			s.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(final AdapterView<?> arg0, final View arg1, final int arg2, long arg3) {
					if ( arg0.getId() == R.id.spinner_kezelesek ) {
						if ( kezelesekid != null ) {
							kezelesid = kezelesekid[arg2];
						}
						new KezelesFeltoltes(MainActivity.this) {
							private ProgressDialog progressDialog = null;

							@Override
							protected void onPostExecute(String result) {
								progressDialog.dismiss();
								Kezelestabla(result);
							}

							@Override
							protected void onPreExecute() {
								progressDialog = new ProgressDialog(MainActivity.this);
								progressDialog.setMessage("Kérem várjon...");
								progressDialog.show();
							}

						}.execute(arg0.getItemAtPosition(arg2).toString());
					}
					if ( arg0.getId() == R.id.spinner_vendegek ) {
						if ( vendegekid != null ) {
							vendegid = vendegekid[arg2];
						}
						// vendeg elozo kezeleseinek betoltese
						new VenKezelesei(MainActivity.this) {
							private ProgressDialog progressDialog = null;

							@Override
							protected void onPostExecute(String result) {
								progressDialog.dismiss();

								TableLayout table = (TableLayout) findViewById(R.id.maintablevendeg);
								int nIndex = table.getChildCount();
								// törölni kell ha már van benne sor
								if ( nIndex > 1 ) {
									for ( int i = nIndex; i > 0; i-- ) {
										table.removeViewAt(i - 1);
									}
								}

								int db = ( venkezeles.elozokezeles != null ) ? venkezeles.elozokezeles.length : 0;
								android.widget.TableRow.LayoutParams lp = new android.widget.TableRow.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
								for ( int i = 1; i < db; i++ ) {
									TableRow tr = new TableRow(MainActivity.this);
									lp.weight = 1;
									tr.setLayoutParams(lp);
									Button b = new Button(MainActivity.this);
									b.setText(venkezeles.elozokezeles[i].datum.subSequence(0, 10));
									b.setLayoutParams(lp);
									b.setTextSize(12);
									b.setWidth(MainTabActivity.convertDpToPixel(95, MainActivity.this));
									b.setBackgroundColor(Color.argb(255, 204, 204, 204));
									b.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View arg0) {
											VendegTablaClear();
											arg0.setBackgroundColor(Color.GREEN);
											TableLayout table = (TableLayout) findViewById(R.id.maintablevendeg);
											if ( arg0.getParent() instanceof TableRow ) {
												TableRow rr = (TableRow) arg0.getParent();
												int rowindex = table.indexOfChild(rr) + 1;
												int iid = venkezeles.elozokezeles[rowindex].id;
												System.out.println(iid);

												new KezelesFeltoltesElozo(MainActivity.this) {
													private ProgressDialog progressDialog = null;

													@Override
													protected void onPostExecute(String result) {
														progressDialog.dismiss();
														Kezelestabla(result);
													}

													@Override
													protected void onPreExecute() {
														progressDialog = new ProgressDialog(MainActivity.this);
														progressDialog.setMessage("Kérem várjon...");
														progressDialog.show();
													}
												}.execute(Integer.toString(iid));
											}
										}
									});

									tr.addView(b);
									TextView tvleiras = new TextView(MainActivity.this);
									tvleiras.setLayoutParams(lp);
									tvleiras.setBackgroundColor(Color.WHITE);
									tvleiras.setText(venkezeles.elozokezeles[i].leiras);
									tvleiras.setWidth(MainTabActivity.convertDpToPixel(200, MainActivity.this));
									tr.addView(tvleiras);

									/* Add row to TableLayout. */
									table.addView(tr, new android.widget.TableRow.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
								}
							}

							@Override
							protected void onPreExecute() {
								progressDialog = new ProgressDialog(MainActivity.this);
								progressDialog.setMessage("Kérem várjon...");
								progressDialog.show();
							}
						}.execute(vendegid.toString());
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
				}
			});

			final ArrayAdapter<?> adapter = new ArrayAdapter<Object>(this, android.R.layout.simple_spinner_item, array_spinner);

			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					s.setAdapter(adapter);
				}
			});
		}
		catch ( JSONException e ) {
			e.printStackTrace();
		}
		catch ( IOException e ) {
			e.printStackTrace();
		}
		finally {
			if ( httpClient != null ) {
				httpClient.close();
			}
		}
		return elemekid;
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

	/*
	 * Lenyíló listák feltöltése
	 */
	private void fillSpinner() {
		// vendégek list feltöltése
		if ( isOnline() ) {
			new Thread() {
				@Override
				public void run() {
					vendegekid = fillJSONToSpinner(JSONUlr + JSONVendegAll, R.id.spinner_vendegek);
				}
			}.start();
			// kezelések list feltöltése
			new Thread() {
				@Override
				public void run() {
					kezelesekid = fillJSONToSpinner(JSONUlr + JSONKezelesAll, R.id.spinner_kezelesek);
				}
			}.start();
		}
		else {
			Toast.makeText(MainActivity.this, getString(R.string.NotInternet), Toast.LENGTH_LONG).show();
		}
	}

	// kezelés inditasa
	private TimerTask idofut() {
		Timer t = new Timer();

		// Set the schedule function and rate
		TimerTask idoTask2 = ( new TimerTask() {

			@Override
			public void run() {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						TextView tv = (TextView) findViewById(R.id.TextViewAktIdo);
						tv.setText(String.valueOf(aktido));

						if ( reszIdo == 0 ) {
							SurfaceView sv = (SurfaceView) findViewById(R.id.surfaceView2);
							TextView tvlepes = (TextView) findViewById(R.id.textViewLepesAktualis);
							if ( kezeles.osszlepes.intValue() >= kezelesindex ) {
								tvlepes.setText(String.valueOf(Integer.toString(kezelesindex)));
								/*
								 * String szinek[] =
								 * kezeles.lepes[kezelesindex].
								 * pszin.split("[(,)]"); if( szinek.length >=3 )
								 * { r = Integer.parseInt(szinek[1].trim()); g =
								 * Integer.parseInt(szinek[2].trim()); b =
								 * Integer.parseInt(szinek[3].trim());
								 * sv.setBackgroundColor(Color.rgb(r,g,b));
								 * 
								 * } else {
								 * sv.setBackgroundColor(Color.rgb(0,0,0)); }
								 */
								sv.setBackgroundColor(Color.rgb(kezeles.lepes[kezelesindex].szin_r, kezeles.lepes[kezelesindex].szin_g, kezeles.lepes[kezelesindex].szin_b));
								reszIdo = kezeles.lepes[kezelesindex].ido;
								// Vezérlés
								new Thread() {
									@Override
									public void run() {
										AndroidHttpClient httpClient = null;
										httpClient = AndroidHttpClient.newInstance("Android");
										HttpPost httppost = new HttpPost(animiserverulr);
										try {
											List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
											nameValuePairs.add(new BasicNameValuePair("parancs", "Write"));
											nameValuePairs.add(new BasicNameValuePair("fenyero", "1/5"));
											nameValuePairs.add(new BasicNameValuePair("pszin", kezeles.lepes[kezelesindex].pszin));
											nameValuePairs.add(new BasicNameValuePair("pixeltomb", kezeles.lepes[kezelesindex].pxtomb));
											httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

											// Execute HTTP Post Request
											httpClient.execute(httppost);
											// Toast.makeText(MainActivity.this,
											// response.toString(),Toast.LENGTH_LONG).show();
											kezelesindex += 1;
										}
										catch ( ClientProtocolException e ) {
											e.printStackTrace();
										}
										catch ( IOException e ) {
											e.printStackTrace();
										}
										catch ( ArrayIndexOutOfBoundsException e ) {
											e.printStackTrace();
										}
										finally {
											if ( httpClient != null )
												httpClient.close();
										}
									}
								}.start();

							}
						}
						if ( !pause ) {
							aktido += 1;
							reszIdo -= 1;
						}
						else {
							pauseTime += 1;
							Button btnPause = (Button) findViewById(R.id.buttonPause);
							btnPause.setText(getString(R.string.BtnPause) + "  (" + String.valueOf(pauseTime) + ")");
						}

						if ( kezeles.osszido.intValue() < aktido ) {
							// Kikapcsolás
							new Thread() {
								@Override
								public void run() {
									AndroidHttpClient httpClient = null;
									httpClient = AndroidHttpClient.newInstance("Android");
									HttpPost httppost = new HttpPost(animiserverulr);
									try {
										List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
										nameValuePairs.add(new BasicNameValuePair("parancs", "Clear"));
										httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
										httpClient.execute(httppost);
										// Stop
										nameValuePairs.clear();
										nameValuePairs.add(new BasicNameValuePair("parancs", "Stop"));
										httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
										httpClient.execute(httppost);
									}
									catch ( ClientProtocolException e ) {
										e.printStackTrace();
									}
									catch ( IOException e ) {
										e.printStackTrace();
									}
									catch ( ArrayIndexOutOfBoundsException e ) {
										e.printStackTrace();
									}
									finally {
										if ( httpClient != null )
											httpClient.close();
									}
								}
							}.start();

							idoTask_fo.cancel();
							aktido = 0;
							TextView tvi = (TextView) findViewById(R.id.TextViewAktIdo);
							tvi.setText("0");
							TextView tvl = (TextView) findViewById(R.id.textViewLepesAktualis);
							tvl.setText("0");
							SurfaceView sv = (SurfaceView) findViewById(R.id.surfaceView2);
							sv.setBackgroundColor(Color.rgb(0, 0, 0));

							Button btnStart = (Button) findViewById(R.id.buttonStart);
							Button btnStop = (Button) findViewById(R.id.buttonStop);
							Button btnPause = (Button) findViewById(R.id.buttonPause);
							Button btnRestart = (Button) findViewById(R.id.buttonRestart);
							btnStart.setEnabled(true);
							btnStop.setEnabled(false);
							btnPause.setEnabled(false);
							btnRestart.setEnabled(false);
							// --- Vendegek lenyiló
							final Spinner spinnerVendeg = (Spinner) findViewById(R.id.spinner_vendegek);
							// --- Kezelesek lenyilo
							final Spinner spinnerKezelesek = (Spinner) findViewById(R.id.spinner_kezelesek);
							spinnerKezelesek.setEnabled(true);
							spinnerVendeg.setEnabled(true);
						}
					};
				});
			};
		} );
		t.scheduleAtFixedRate(idoTask2, 0, 1000);
		return idoTask2;
	}

	// kezelés inditasa socket
	private TimerTask idofut_socket() {
		Timer t = new Timer();

		// Set the schedule function and rate
		TimerTask idoTask2 = ( new TimerTask() {

			@Override
			public void run() {
				runOnUiThread(new Runnable() {

					@Override
					public void run() { 

						TextView tv = (TextView) findViewById(R.id.TextViewAktIdo);
						tv.setText(String.valueOf(aktido));

						if ( reszIdo == 0 ) {
							SurfaceView sv = (SurfaceView) findViewById(R.id.surfaceView2);
							TextView tvlepes = (TextView) findViewById(R.id.textViewLepesAktualis);
							if ( kezeles.osszlepes.intValue() >= kezelesindex ) {
								tvlepes.setText(String.valueOf(Integer.toString(kezelesindex)));
								sv.setBackgroundColor(Color.rgb(kezeles.lepes[kezelesindex].szin_r, kezeles.lepes[kezelesindex].szin_g, kezeles.lepes[kezelesindex].szin_b));
								reszIdo = kezeles.lepes[kezelesindex].ido;
								// Vezérlés
								gep_1.socket_write(fenyero, kezeles.lepes[kezelesindex].szin_r, kezeles.lepes[kezelesindex].szin_g, kezeles.lepes[kezelesindex].szin_b, kezeles.lepes[kezelesindex].pxtomb);
								kezelesindex += 1;
							}
						}
						if ( !pause ) {
							aktido += 1;
							reszIdo -= 1;
						}
						else {
							pauseTime += 1;
							Button btnPause = (Button) findViewById(R.id.buttonPause);
							btnPause.setText(getString(R.string.BtnPause) + "  (" + String.valueOf(pauseTime) + ")");
						}

						if ( kezeles.osszido.intValue() < aktido ) {
							// Kikapcsolás
							gep_1.socket_clear();
							gep_1.socket_stop();
							gep_1.socket_close();
							idoTask_fo.cancel();
							aktido = 0;
							TextView tvi = (TextView) findViewById(R.id.TextViewAktIdo);
							tvi.setText("0");
							TextView tvl = (TextView) findViewById(R.id.textViewLepesAktualis);
							tvl.setText("0");
							SurfaceView sv = (SurfaceView) findViewById(R.id.surfaceView2);
							sv.setBackgroundColor(Color.rgb(0, 0, 0));

							Button btnStart = (Button) findViewById(R.id.buttonStart);
							Button btnStop = (Button) findViewById(R.id.buttonStop);
							Button btnPause = (Button) findViewById(R.id.buttonPause);
							Button btnRestart = (Button) findViewById(R.id.buttonRestart);
							btnStart.setEnabled(true);
							btnStop.setEnabled(false);
							btnPause.setEnabled(false);
							btnRestart.setEnabled(false);
							// --- Vendegek lenyiló
							final Spinner spinnerVendeg = (Spinner) findViewById(R.id.spinner_vendegek);
							// --- Kezelesek lenyilo
							final Spinner spinnerKezelesek = (Spinner) findViewById(R.id.spinner_kezelesek);
							spinnerKezelesek.setEnabled(true);
							spinnerVendeg.setEnabled(true);
						}
					};//--
				});//--
			};
		} );
		t.scheduleAtFixedRate(idoTask2, 0, 1000);
		return idoTask2;
	}
	
	// kezeles kiirasa adatbazisba
	private void writedb() {
		TextView tvOleir = (TextView) findViewById(R.id.leiras);
		TextView tvOdiag = (TextView) findViewById(R.id.diagnozis);
		TextView tvOanam = (TextView) findViewById(R.id.anamnezis);
		try {
			mysqlobject = new JSONObject();
			mysqlobject.put("osszlepes", kezeles.osszlepes.intValue());
			mysqlobject.put("kezeles_id", kezeles.kezeles_id);
			mysqlobject.put("leiras", URLEncoder.encode(tvOleir.getText().toString(), "UTF-8"));
			mysqlobject.put("diagnozis", URLEncoder.encode(tvOdiag.getText().toString(), "UTF-8"));
			mysqlobject.put("anamnezis", URLEncoder.encode(tvOanam.getText().toString(), "UTF-8"));
			mysqlobject.put("vendeg_id", vendegid);
			mysqlobject.put("partner_id", cegid);
			mysqlobject.put("kezelo_id", userid);
			for ( int i = 1; i <= kezeles.osszlepes.intValue(); i++ ) {
				JSONObject lepesobject = new JSONObject();
				lepesobject.put("sorszam", kezeles.lepes[i].sorszam);
				lepesobject.put("kezeles_reszletekid", kezeles.lepes[i].kezeles_reszletekid);
				lepesobject.put("lepes", URLEncoder.encode(kezeles.lepes[i].lepes, "UTF-8"));
				lepesobject.put("ido", kezeles.lepes[i].ido);
				lepesobject.put("megjegyzes", URLEncoder.encode(kezeles.lepes[i].megjegyzes, "UTF-8"));
				lepesobject.put("pszin", kezeles.lepes[i].pszin);
				lepesobject.put("pxtomb", kezeles.lepes[i].pxtomb);
				mysqlobject.put(Integer.toString(i), lepesobject);

			}
			System.out.println(mysqlobject);
			new Thread() {
				@Override
				public void run() {
					AndroidHttpClient httpClient = null;
					httpClient = AndroidHttpClient.newInstance("Android");
					String ulr = MainActivity.JSONUlr + "webservice_kezeles_vendegir.php";
					HttpPost httppost = new HttpPost(ulr);
					try {
						List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
						nameValuePairs.add(new BasicNameValuePair("json", mysqlobject.toString()));
						httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
						String resp = httpClient.execute(httppost, new BasicResponseHandler());
						System.out.println(resp);

					}
					catch ( ClientProtocolException e ) {
						e.printStackTrace();
					}
					catch ( IOException e ) {
						e.printStackTrace();
					}
					catch ( ArrayIndexOutOfBoundsException e ) {
						e.printStackTrace();
					}
					finally {
						if ( httpClient != null )
							httpClient.close();
					}
				}

			}.start();

		}
		catch ( JSONException e ) {
			e.printStackTrace();
		}
		catch ( UnsupportedEncodingException e ) {
			e.printStackTrace();
		}
	}

	// user - password ellenorzése
	private void userell() {
		new Thread() {
			@Override
			public void run() {
				AndroidHttpClient httpClient = null;
				httpClient = AndroidHttpClient.newInstance("Android");
				String ulr = MainActivity.JSONUlr + "webservice_userell.php";
				HttpPost httppost = new HttpPost(ulr);
				try {
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
					nameValuePairs.add(new BasicNameValuePair("user", login_user));
					nameValuePairs.add(new BasicNameValuePair("password", login_password));
					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					String resp = httpClient.execute(httppost, new BasicResponseHandler());
					JSONObject root = new JSONObject(resp);
					// -- üress-e a root
					if ( !root.isNull("posts") ) {
						JSONObject jsonkezeles = root.getJSONObject("posts");
						islogin = jsonkezeles.getInt("login");
						nev = jsonkezeles.getString("nev");
						userid = jsonkezeles.getInt("id");
						ceg = jsonkezeles.getString("ceg");
						cegid = jsonkezeles.getInt("partner_id");
					}

				}
				catch ( ClientProtocolException e ) {
					e.printStackTrace();
				}
				catch ( IOException e ) {
					e.printStackTrace();
				}
				catch ( ArrayIndexOutOfBoundsException e ) {
					e.printStackTrace();
				}
				catch ( JSONException e ) {
					e.printStackTrace();
				}
				finally {
					if ( httpClient != null )
						httpClient.close();
				}
			}
		}.start();

	}

	// kezelés tábla feltöltése
	private void Kezelestabla(String result) {

		TextView tvOl = (TextView) findViewById(R.id.textViewOsszlepes);
		TextView tvOi = (TextView) findViewById(R.id.TextViewOsszIdo);
		TextView tvOleir = (TextView) findViewById(R.id.leiras);
		TextView tvOdiag = (TextView) findViewById(R.id.diagnozis);
		TextView tvOanam = (TextView) findViewById(R.id.anamnezis);
		// sOsszlepes = kezeles.osszlepes.toString();
		if ( tvOl != null ) {
			if ( result == "1" ) {
				tvOl.setText(kezeles.osszlepes.toString());
				tvOi.setText(kezeles.osszido.toString());
				tvOleir.setText(kezeles.leiras.toString());
				tvOdiag.setText(kezeles.diagnozis.toString());
				tvOanam.setText(kezeles.anamnezis.toString());
				// szinválasztás feltöltése
				TableLayout table = (TableLayout) findViewById(R.id.maintablelepes);
				int nIndex = table.getChildCount();
				// törölni kell ha már van benne sor
				if ( nIndex > 1 ) {
					for ( int i = nIndex; i > 0; i-- ) {
						table.removeViewAt(i - 1);
					}
				}
				android.widget.TableRow.LayoutParams lp = new android.widget.TableRow.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				for ( int i = 1; i <= kezeles.osszlepes.intValue(); i++ ) {
					TableRow tr = new TableRow(MainActivity.this);
					lp.weight = 1;
					tr.setLayoutParams(lp);
					// sorszám
					TextView tvSorsz = new TextView(MainActivity.this);
					lp.weight = 1;
					lp.setMargins(5, 1, 1, 1);
					tvSorsz.setLayoutParams(lp);
					tvSorsz.setWidth(54 * ( metrics.densityDpi / 160 ));
					tvSorsz.setBackgroundColor(Color.argb(255, 204, 204, 204));
					// tvSorsz.setBackgroundColor(Color.WHITE);
					tvSorsz.setGravity(17);
					tvSorsz.setText(String.valueOf(i));
					// Név
					TextView tvNev = new TextView(MainActivity.this);
					lp.setMargins(2, 0, 2, 0);
					tvNev.setLayoutParams(lp);
					tvNev.setWidth(220 * ( metrics.densityDpi / 160 ));
					tvNev.setBackgroundColor(Color.rgb(kezeles.lepes[i].szin_r, kezeles.lepes[i].szin_g, kezeles.lepes[i].szin_b));
					tvNev.setText(kezeles.lepes[i].megjegyzes.toString());
					// --------------
					tvNev.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(final View arg0) {
							int currtentColor = Color.BLUE;
							AmbilWarnaDialog dialog = new AmbilWarnaDialog(MainActivity.this, currtentColor, new OnAmbilWarnaListener() {
								@Override
								public void onCancel(AmbilWarnaDialog dialog) {
								}

								@Override
								public void onOk(AmbilWarnaDialog dialog, int color) {
									TableLayout table = (TableLayout) findViewById(R.id.maintablelepes);
									if ( arg0.getParent() instanceof TableRow ) {
										TableRow rr = (TableRow) arg0.getParent();
										int rowindex = table.indexOfChild(rr) + 1;
										kezeles.lepes[rowindex].pszin = String.format("rgb(%d, %d, %d)", ( 0x00FF0000 & color ) / ( 256 * 256 ), ( 0x0000FF00 & color ) / 256, 0x000000FF & color);
										kezeles.lepes[rowindex].szin_r = ( 0x00FF0000 & color ) / ( 256 * 256 );
										kezeles.lepes[rowindex].szin_g = ( 0x0000FF00 & color ) / 256;
										kezeles.lepes[rowindex].szin_b = 0x000000FF & color;
										arg0.setBackgroundColor(color);
									}
								}
							});
							dialog.show();
						}
					});
					// --------------
					Spinner sSzin = new Spinner(MainActivity.this);
					sSzin.setBackgroundColor(Color.rgb(kezeles.lepes[i].szin_r, kezeles.lepes[i].szin_g, kezeles.lepes[i].szin_b));
					sSzin.setOnItemSelectedListener(new OnItemSelectedListener() {
						@Override
						public void onItemSelected(final AdapterView<?> arg0, final View arg1, final int arg2, long arg3) {
							if ( arg2 != 0 ) {
								TableLayout table = (TableLayout) findViewById(R.id.maintablelepes);
								// table.getChildAt(0).setBackgroundColor(szinek[arg2]);
								if ( arg1.getParent().getParent() instanceof TableRow ) {
									TableRow rr = (TableRow) arg1.getParent().getParent();
									// rr.setBackgroundColor(szinek[arg2]);
									int rowindex = table.indexOfChild(rr) + 1;
									// String strColor1 = String.format("%X",
									// szinek[arg2]);
									// String strColor =
									// String.format("rgb(%d, %d, %d)",
									// (0x00FF0000 & szinek[arg2])/(256*256),
									// (0x0000FF00 & szinek[arg2])/256,
									// 0x000000FF & szinek[arg2]);
									// System.out.println(strColor);
									kezeles.lepes[rowindex].pszin = String.format("rgb(%d, %d, %d)", ( 0x00FF0000 & szinek[arg2] ) / ( 256 * 256 ), ( 0x0000FF00 & szinek[arg2] ) / 256, 0x000000FF & szinek[arg2]);
									kezeles.lepes[rowindex].szin_r = ( 0x00FF0000 & szinek[arg2] ) / ( 256 * 256 );
									kezeles.lepes[rowindex].szin_g = ( 0x0000FF00 & szinek[arg2] ) / 256;
									kezeles.lepes[rowindex].szin_b = 0x000000FF & szinek[arg2];
								}
								arg1.setBackgroundColor(szinek[arg2]);
							}
						}

						@Override
						public void onNothingSelected(AdapterView<?> arg0) {

						}
					});
					sSzin.setAdapter(adapterszinek);
					/* Add row to TableLayout. */
					// sSzin.setWidth(53*(metrics.densityDpi / 160));
					tr.addView(tvSorsz);
					tr.addView(tvNev);
					tr.addView(sSzin);

					table.addView(tr, new android.widget.TableRow.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
				}
			}
			else {
				tvOl.setText(getString(R.string.hiba_1));
				tvOi.setText("0");
			}
		}
	}

	// Vendég kezelései táblában a dátum alapszinezése
	private void VendegTablaClear() {
		TableLayout table = (TableLayout) findViewById(R.id.maintablevendeg);
		int nIndex = table.getChildCount();
		// törölni kell ha már van benne sor
		if ( nIndex > 1 ) {
			for ( int i = nIndex; i >= 0; i-- ) {
				TableRow tr = new TableRow(MainActivity.this);
				if ( table.getChildAt(i) instanceof TableRow ) {
					tr = (TableRow) table.getChildAt(i);
					if ( tr.getChildAt(0) instanceof Button ) {
						Button b = (Button) tr.getChildAt(0);
						b.setBackgroundColor(Color.argb(255, 204, 204, 204));
					}
				}
			}
		}
	}

}
