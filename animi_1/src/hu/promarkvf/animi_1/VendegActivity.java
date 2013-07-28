package hu.promarkvf.animi_1;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class VendegActivity extends Fragment {
	View V;
	final Vendegek vendegek = new Vendegek();
	final VendegKezelesek vendegKezelesek = new VendegKezelesek();
	Kezeles vendegKezeles = new Kezeles();
	TextView editmegj = null;
	Button btnstart = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		V = inflater.inflate(R.layout.vendeg_view, container, false);

		final Spinner spinnervendeg = (Spinner) V.findViewById(R.id.spinner_vendegek);

		spinnervendeg.setOnItemSelectedListener(vendegselect);

		new DataBase(MainTabActivity.maincontext) {
			private ProgressDialog progressDialog = null;

			@Override
			protected void onPostExecute(String result) {
				progressDialog.dismiss();
				if ( result != null ) {
					vendegek.Vendegek_fill(result);
					ArrayAdapter<String> aa = new ArrayAdapter<String>(MainTabActivity.maincontext, android.R.layout.simple_spinner_item, vendegek.nevek);

					spinnervendeg.setAdapter(aa);

					// vendég kezelések feltöltése
					KezelesekOlv(vendegek.GetVendegId(MainTabActivity.sel_vendeg_pos));
					spinnervendeg.setSelection(MainTabActivity.sel_vendeg_pos);
				}
			}

			@Override
			protected void onPreExecute() {
				progressDialog = new ProgressDialog(MainTabActivity.maincontext);
				progressDialog.setMessage("Kérem várjon...");
				progressDialog.show();
			}

		}.execute(MainTabActivity.JSONVendegAll);

		editmegj = (TextView) V.findViewById(R.id.TextViewMegjegyzes);
		editmegj.setOnClickListener(megjOnClickListener);

		btnstart = (Button) V.findViewById(R.id.button_ujra);
		OnClickListener btnstart_click = new OnClickListener() {
			@Override
			public void onClick(View v) {
				StartKezelesActivity.vendeg = vendegek.GetVendeg(MainTabActivity.sel_vendeg_pos);
				StartKezelesActivity.kezeles = vendegKezeles;
				MainTabActivity.mTabHost.setCurrentTabByTag(getString(R.string.BtnStart));// CurrentTab(2);
			}
		};
		btnstart.setOnClickListener(btnstart_click);

		return V;
	}

	OnItemSelectedListener vendegselect = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			// vendég kezelések feltöltése
			System.out.println("In:" + arg2 + "  Id:" + vendegek.GetVendegId(arg2));
			KezelesekOlv(vendegek.GetVendegId(arg2));
			MainTabActivity.sel_vendeg_pos = arg2;
			StartKezelesActivity.vendeg = vendegek.GetVendeg(MainTabActivity.sel_vendeg_pos);
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}
	};

	private void VendegKezelesekToTable(final VendegKezelesek vk) {
		TableLayout table = (TableLayout) V.findViewById(R.id.maintablevendegkezelesek);
		TablePurge(R.id.maintablevendegkezelesek);

		int db = ( vk != null ) ? vk.Lenght() : 0;
		android.widget.TableRow.LayoutParams lp_tr = new android.widget.TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp_tr.weight = 1f;
		lp_tr.gravity = Gravity.CENTER;
		TableRow.LayoutParams lp_tv = new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT, Gravity.CENTER);
		lp_tv.weight = 1f;
		for ( int i = 0; i < db; i++ ) {
			VendegKezeles v = vk.GetVendegkezeles(i);
			TableRow tr = new TableRow(MainTabActivity.maincontext, null);
			tr.setLayoutParams(lp_tr);
			// tr.setFocusable(true);
			tr.setFocusableInTouchMode(true);
			// tr.setBackgroundResource(R.drawable.border);
			tr.setBackgroundResource(R.drawable.row);
			tr.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if ( arg0 instanceof TableRow ) {
						TableLayout table = (TableLayout) V.findViewById(R.id.maintablevendegkezelesek);
						TableRow rr = (TableRow) arg0;
						int rowindex = table.indexOfChild(rr);
						// if ( rowindex != 0 &&
						// table.getChildAt(0).isSaveEnabled() ) {
						// table.getChildAt(0).setSelected(false);
						// table.getChildAt(0).setPressed(false);
						// }
						VendegKezeles vk_akt = vk.GetVendegkezeles(rowindex);
						if ( vk_akt != null ) {
							KezelesReszletekOlv(vk_akt.id);
							Toast.makeText(MainTabActivity.maincontext, vk_akt.id.toString(), Toast.LENGTH_LONG).show();
						}
						else {
							Toast.makeText(MainTabActivity.maincontext, "Hibás ROWINDEX:" + String.valueOf(rowindex), Toast.LENGTH_LONG).show();
						}
					}
				}
			});
			// dátum
			TextView tvDatum = new TextView(MainTabActivity.maincontext, null, R.style.BodyText);
			tvDatum.setText(v.datum.subSequence(0, 10));
			tvDatum.setLayoutParams(lp_tv);
			tvDatum.setBackgroundResource(R.drawable.border);
			tvDatum.setPadding(MainTabActivity.convertDpToPixel(5, MainTabActivity.maincontext), 0, 0, 0);
			tvDatum.setWidth(MainTabActivity.convertDpToPixel(105, MainTabActivity.maincontext));
			tvDatum.setGravity(Gravity.CENTER);
			tvDatum.setPadding(MainTabActivity.convertDpToPixel(5, MainTabActivity.maincontext), 0, 0, 0);
			tvDatum.setTextSize(MainTabActivity.TEXTSIZE);
			tr.addView(tvDatum);
			// Kezelés azonositó
			TextView tvazonosito = new TextView(MainTabActivity.maincontext, null, R.style.BodyText);
			tvazonosito.setText(v.azonosito);
			tvazonosito.setLayoutParams(lp_tv);
			tvazonosito.setPadding(MainTabActivity.convertDpToPixel(5, MainTabActivity.maincontext), 0, 0, 0);
			tvazonosito.setPadding(MainTabActivity.convertDpToPixel(5, MainTabActivity.maincontext), 0, 0, 0);
			tvazonosito.setBackgroundResource(R.drawable.border);
			tvazonosito.setWidth(MainTabActivity.convertDpToPixel(205, MainTabActivity.maincontext));
			tvazonosito.setTextSize(MainTabActivity.TEXTSIZE);
			tr.addView(tvazonosito);
			// if ( ( i % 2 ) == 0 ) {
			// tr.setBackgroundColor(Color.DKGRAY);
			// }
			// else {
			// tr.setBackgroundColor(Color.LTGRAY);
			// }

			if ( i == 0 ) {
				tr.setSelected(true);
				tr.setPressed(true);
			}
			/* Add row to TableLayout. */
			table.addView(tr, new android.widget.TableRow.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		}
	}

	private void KezelesekOlv(int vendegId) {
		new DataBase(MainTabActivity.maincontext) {
			private ProgressDialog progressDialog = null;

			@Override
			protected void onPostExecute(String result) {
				progressDialog.dismiss();
				if ( result != null ) {
					// System.out.println(result);
					vendegKezelesek.VendegKezelesek_fill(result);
					VendegKezelesekToTable(vendegKezelesek);
					if ( !vendegKezelesek.IsEmpty() ) {
						KezelesReszletekOlv(vendegKezelesek.GetVendegkezeles(0).id);
					}
					else {
						KezelesReszletekToTable(null);
					}
				}
			}

			@Override
			protected void onPreExecute() {
				progressDialog = new ProgressDialog(MainTabActivity.maincontext);
				progressDialog.setMessage("Kérem várjon...");
				progressDialog.show();
			}

		}.execute("webservice_vendegid.php", "val_vendeg_id=" + String.valueOf(vendegId));

	}

	private void KezelesReszletekOlv(int kezelesId) {
		new DataBase(MainTabActivity.maincontext) {
			private ProgressDialog progressDialog = null;

			@Override
			protected void onPostExecute(String result) {
				progressDialog.dismiss();
				if ( result != null ) {
					// System.out.println(result);
					vendegKezeles.Kezeles_fill(result);
					KezelesReszletekToTable(vendegKezeles);
				}
			}

			@Override
			protected void onPreExecute() {
				progressDialog = new ProgressDialog(MainTabActivity.maincontext);
				progressDialog.setMessage("Kérem várjon...");
				progressDialog.show();
			}

		}.execute("webservice_kezelesid.php", "val_elozokezeles_id=" + String.valueOf(kezelesId));

	}

	private void KezelesReszletekToTable(Kezeles vk) {
		// System.out.println(vk.azonosito);
		TextView tv_kez_azonosito = (TextView) V.findViewById(R.id.tv_kez_azonosito);
		TextView tv_kez_anamnezis = (TextView) V.findViewById(R.id.tv_kez_anamnezis);
		TextView tv_kez_diagnozis = (TextView) V.findViewById(R.id.tv_kez_diagnozis);
		StartKezelesActivity.kezeles = vk;
		if ( vk != null ) {
			btnstart.setEnabled(true);
			tv_kez_azonosito.setText(vk.azonosito);
			tv_kez_anamnezis.setText(vk.anamnezis);
			tv_kez_diagnozis.setText(vk.diagnozis);
			KezelesekLepeseiToTable(vk);
			editmegj.setEnabled(true);
		}
		else {
			btnstart.setEnabled(false);
			tv_kez_azonosito.setText("");
			tv_kez_anamnezis.setText("");
			tv_kez_diagnozis.setText("");
			editmegj.setText("");
			editmegj.scrollTo(0, 0);
			editmegj.setEnabled(false);
			TablePurge(R.id.maintablekezeleslepesek);
		}
	}

	private void KezelesekLepeseiToTable(final Kezeles kezeles) {
		TableLayout table = (TableLayout) V.findViewById(R.id.maintablekezeleslepesek);
		TablePurge(R.id.maintablekezeleslepesek);
		// XmlPullParser parser = getXml(R.style.BodyRow);
		// AttributeSet attributes = Xml.asAttributeSet(parser);

		int db = ( kezeles != null ) ? kezeles.osszlepes : 0;
		android.widget.TableRow.LayoutParams lp_tr = new android.widget.TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp_tr.weight = 1f;
		lp_tr.gravity = Gravity.CENTER;
		TableRow.LayoutParams lp_tv = new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT, Gravity.CENTER);
		lp_tv.weight = 1f;
		for ( int i = 0; i < db; i++ ) {
			final KezelesLepes lepes = kezeles.lepes[i];
			TableRow tr = new TableRow(MainTabActivity.maincontext, null);
			tr.setLayoutParams(lp_tr);
			tr.setFocusableInTouchMode(true);
			// tr.setBackgroundResource(R.drawable.border);
			tr.setBackgroundResource(R.drawable.row);
			tr.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if ( arg0 instanceof TableRow ) {
						TableLayout table = (TableLayout) V.findViewById(R.id.maintablevendegkezelesek);
						TableRow rr = (TableRow) arg0;
						int rowindex = table.indexOfChild(rr);
						if ( rowindex != 0 && table.getChildAt(0).isSaveEnabled() ) {
							table.getChildAt(0).setSelected(false);
							table.getChildAt(0).setPressed(false);
						}
						// VendegKezeles vk_akt = vk.GetVendegkezeles(rowindex);
						Toast.makeText(MainTabActivity.maincontext, String.valueOf(lepes.sorszam), Toast.LENGTH_LONG).show();
						if ( editmegj != null ) {
							editmegj.setText(lepes.megjegyzes);
							editmegj.scrollTo(0, 0);
						}
					}
				}
			});
			// Sorszám
			TextView tvSorsz = new TextView(MainTabActivity.maincontext, null, R.style.BodyText);
			tvSorsz.setText(String.valueOf(lepes.sorszam));
			tvSorsz.setLayoutParams(lp_tv);
			tvSorsz.setWidth(MainTabActivity.convertDpToPixel(65, MainTabActivity.maincontext));
			tvSorsz.setBackgroundResource(R.drawable.border);
			tvSorsz.setGravity(Gravity.CENTER);
			tr.addView(tvSorsz);
			// Lépes azonositó
			TextView tvLepes = new TextView(MainTabActivity.maincontext, null, R.style.BodyText);
			tvLepes.setText(lepes.lepes);
			tvLepes.setLayoutParams(lp_tv);
			tvLepes.setPadding(MainTabActivity.convertDpToPixel(5, MainTabActivity.maincontext), 0, 0, 0);
			tvLepes.setWidth(MainTabActivity.convertDpToPixel(115, MainTabActivity.maincontext));
			tvLepes.setBackgroundResource(R.drawable.border);
			tr.addView(tvLepes);
			// Idő
			// TextView tvIdo = new TextView(MainTabActivity.maincontext, null,
			// R.style.BodyText);
			// tvIdo.setText(String.valueOf(lepes.ido));
			// tvIdo.setLayoutParams(lp_tv);
			// tvIdo.setWidth(MainTabActivity.convertDpToPixel(65,
			// MainTabActivity.maincontext));
			// tvIdo.setBackgroundResource(R.drawable.border);
			// tvIdo.setGravity(Gravity.CENTER);
			// tr.addView(tvIdo);
			// Megjegyzés
			TextView tvMegjegyzes = new TextView(MainTabActivity.maincontext, null, R.style.BodyText);
			tvMegjegyzes.setText(lepes.megjegyzes.subSequence(0, lepes.megjegyzes.length() > 30 ? 30 : lepes.megjegyzes.length()));
			tvMegjegyzes.setLayoutParams(lp_tv);
			tvMegjegyzes.setPadding(MainTabActivity.convertDpToPixel(5, MainTabActivity.maincontext), 0, 0, 0);
			tvMegjegyzes.setWidth(MainTabActivity.convertDpToPixel(185, MainTabActivity.maincontext));
			tvMegjegyzes.setBackgroundResource(R.drawable.border);
			tr.addView(tvMegjegyzes);
			// Szín
			ImageView ivSzin = new ImageView(MainTabActivity.maincontext, null, R.style.BodyText);
			ivSzin.setLayoutParams(lp_tv);
			ivSzin.setMinimumWidth(MainTabActivity.convertDpToPixel(65, MainTabActivity.maincontext));
			ivSzin.setBackgroundResource(R.drawable.border);
			// ivSzin.setOnClickListener(szinOnclickListener);
			// ivSzin.setMinimumHeight(MainTabActivity.convertDpToPixel(55,
			// MainTabActivity.maincontext));
			int padding = MainTabActivity.convertDpToPixel(5, MainTabActivity.maincontext);
			ivSzin.setPadding(padding, padding, padding, padding);
			Bitmap.Config conf = Bitmap.Config.ARGB_8888;
			Bitmap bmp = Bitmap.createBitmap(kezeles.berendezes_dim1 * 4, kezeles.berendezes_dim1 * 4, conf);
			bmp = MainTabActivity.SetPxtomb(lepes.pxtomb, kezeles.berendezes_dim1, kezeles.berendezes_dim2, Color.rgb(lepes.szin_r, lepes.szin_g, lepes.szin_b), 4);
			ivSzin.setImageBitmap(bmp);

			tr.addView(ivSzin);
			// if ( ( i % 2 ) == 0 ) {
			// tr.setBackgroundColor(Color.DKGRAY);
			// }
			// else {
			// tr.setBackgroundColor(Color.LTGRAY);
			// }

			if ( i == 0 ) {
				tr.setSelected(true);
				tr.setPressed(true);
				if ( editmegj != null ) {
					editmegj.setText(lepes.megjegyzes);
					editmegj.scrollTo(0, 0);
				}
			}
			/* Add row to TableLayout. */
			table.addView(tr, new android.widget.TableRow.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		}

	}

	// Megjegyzés módosítása listener
	OnClickListener megjOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if ( editmegj.isEnabled() ) {
				Intent myIntent = new Intent();
				myIntent.setClass(MainTabActivity.maincontext, ModMegjActivity.class);
				CharSequence megjegyzes = ( (TextView) v ).getText();
				myIntent.putExtra("Megjegyzes", megjegyzes);
				myIntent.putExtra("Fejlec", "Fejléc");
				getActivity().startActivityForResult(myIntent, MainTabActivity.MOD_MEGJ_ACTIVITY_ID);
			}
		}
	};

	private void TablePurge(int tableId) {
		Object o = V.findViewById(tableId);
		if ( o instanceof TableLayout ) {
			TableLayout table = (TableLayout) o;
			int nIndex = table.getChildCount() - 1;
			// törölni kell ha már van benne sor
			if ( nIndex >= 0 ) {
				for ( int i = nIndex; i >= 0; i-- ) {
					table.removeViewAt(i);
				}
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch ( requestCode ) {
		case MainTabActivity.MOD_MEGJ_ACTIVITY_ID:
			if ( resultCode == Activity.RESULT_OK ) {
				CharSequence megjegyzes = (CharSequence) data.getParcelableExtra("Megjegyzes");
				if ( editmegj != null && megjegyzes != null ) {
					editmegj.setText(megjegyzes);
				}
			}
			break;
		}

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		/*
		 * try{ fragmentListener = (FragmentListener) activity; }catch
		 * (ClassCastException e) { throw new ClassCastException(
		 * "Parent activity must implement interface FragmentListener."); }
		 */
	}
}
