package hu.promarkvf.animi_1;

import org.xmlpull.v1.XmlPullParser;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.util.Xml;
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
import android.widget.EditText;
import android.widget.LinearLayout;
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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		V = inflater.inflate(R.layout.vendeg_view, container, false);

		final Spinner spinnervendeg = (Spinner) V.findViewById(R.id.spinner_vendegek);

		spinnervendeg.setOnItemSelectedListener(vendegselect);

		new DataRead(MainTabActivity.maincontext) {
			private ProgressDialog progressDialog = null;

			@Override
			protected void onPostExecute(String result) {
				progressDialog.dismiss();
				if ( result != null ) {
					vendegek.Vendegek_fill(result);
					ArrayAdapter<String> aa = new ArrayAdapter<String>(MainTabActivity.maincontext, android.R.layout.simple_spinner_item, vendegek.nevek);

					spinnervendeg.setAdapter(aa);

					// vendég kezelések feltöltése
					KezelesekOlv(vendegek.GetVendegId(0));
				}
			}

			@Override
			protected void onPreExecute() {
				progressDialog = new ProgressDialog(MainTabActivity.maincontext);
				progressDialog.setMessage("Kérem várjon...");
				progressDialog.show();
			}

		}.execute(MainTabActivity.JSONVendegAll);

		return V;
	}

	OnItemSelectedListener vendegselect = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			// vendég kezelések feltöltése
			System.out.println("In:" + arg2 + "  Id:" + vendegek.GetVendegId(arg2));
			KezelesekOlv(vendegek.GetVendegId(arg2));
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}
	};

	private void VendegKezelesekToTable(final VendegKezelesek vk) {
		TableLayout table = (TableLayout) V.findViewById(R.id.maintablevendegkezelesek);
		int nIndex = table.getChildCount() - 1;
		// törölni kell ha már van benne sor
		if ( nIndex > 1 ) {
			for ( int i = nIndex; i >= 0; i-- ) {
				table.removeViewAt(i);
			}
		}

		// XmlPullParser parser = getXml(R.style.BodyRow);
		// AttributeSet attributes = Xml.asAttributeSet(parser);

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
			tr.setFocusable(true);
			tr.setFocusableInTouchMode(true);
//			tr.setBackgroundResource(R.drawable.border);
			tr.setBackgroundResource(R.drawable.row);
			tr.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if ( arg0 instanceof TableRow ) {
						TableLayout table = (TableLayout) V.findViewById(R.id.maintablevendegkezelesek);
						TableRow rr = (TableRow) arg0;
						int rowindex = table.indexOfChild(rr);
						VendegKezeles vk_akt = vk.GetVendegkezeles(rowindex);
						KezelesReszletekOlv(vk_akt.id);
						Toast.makeText(MainTabActivity.maincontext, vk_akt.id.toString(), Toast.LENGTH_LONG).show();
					}
				}
			});
			// dátum
			TextView tvDatum = new TextView(MainTabActivity.maincontext, null, R.style.BodyText);
			tvDatum.setText(v.datum.subSequence(0, 10));
			tvDatum.setLayoutParams(lp_tv);
			tvDatum.setBackgroundResource(R.drawable.border);
			tvDatum.setWidth(MainTabActivity.convertDpToPixel(95, MainTabActivity.maincontext));
			tvDatum.setGravity(Gravity.CENTER);
			tvDatum.setPadding(MainTabActivity.convertDpToPixel(5, MainTabActivity.maincontext), 0, 0, 0);
			tr.addView(tvDatum);
			// Kezelés leírás
			TextView tvleiras = new TextView(MainTabActivity.maincontext, null, R.style.BodyText);
			tvleiras.setText(v.leiras);
			tvleiras.setLayoutParams(lp_tv);
			tvleiras.setPadding(MainTabActivity.convertDpToPixel(5, MainTabActivity.maincontext), 0, 0, 0);
			tvleiras.setBackgroundResource(R.drawable.border);
			tvleiras.setWidth(MainTabActivity.convertDpToPixel(205, MainTabActivity.maincontext));
			tr.addView(tvleiras);
			// if ( ( i % 2 ) == 0 ) {
			// tr.setBackgroundColor(Color.DKGRAY);
			// }
			// else {
			// tr.setBackgroundColor(Color.LTGRAY);
			// }

			/* Add row to TableLayout. */
			table.addView(tr, new android.widget.TableRow.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		}

	}

	private void KezelesekOlv(int vendegId) {
		new DataRead(MainTabActivity.maincontext) {
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
		new DataRead(MainTabActivity.maincontext) {
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
		if ( vk != null ) {
			tv_kez_azonosito.setText(vk.azonosito);
			tv_kez_anamnezis.setText(vk.anamnezis);
			tv_kez_diagnozis.setText(vk.diagnozis);
			KezelesekLepeseiToTable(vk);
		}
		else {
			tv_kez_azonosito.setText("");
			tv_kez_anamnezis.setText("");
			tv_kez_diagnozis.setText("");
			TablePurge (R.id.maintablekezeleslepesek);
		}
	}

	private void KezelesekLepeseiToTable(final Kezeles kezeles) {
		TableLayout table = (TableLayout) V.findViewById(R.id.maintablekezeleslepesek);
		TablePurge (R.id.maintablekezeleslepesek);
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
			tr.setBackgroundResource(R.drawable.border);
			tr.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if ( arg0 instanceof TableRow ) {
						TableLayout table = (TableLayout) V.findViewById(R.id.maintablevendegkezelesek);
						TableRow rr = (TableRow) arg0;
						int rowindex = table.indexOfChild(rr);
						// VendegKezeles vk_akt = vk.GetVendegkezeles(rowindex);
						Toast.makeText(MainTabActivity.maincontext, String.valueOf(lepes.sorszam), Toast.LENGTH_LONG).show();
					}
				}
			});
			// Sorszám
			TextView tvSorsz = new TextView(MainTabActivity.maincontext, null, R.style.BodyText);
			tvSorsz.setText(String.valueOf(lepes.sorszam));
			tvSorsz.setLayoutParams(lp_tv);
			tvSorsz.setWidth(MainTabActivity.convertDpToPixel(70, MainTabActivity.maincontext));
			tvSorsz.setBackgroundResource(R.drawable.border);
			tvSorsz.setGravity(Gravity.CENTER);
			tr.addView(tvSorsz);
			// Lépes azonositó
			TextView tvLepes = new TextView(MainTabActivity.maincontext, null, R.style.BodyText);
			tvLepes.setText(lepes.lepes);
			tvLepes.setLayoutParams(lp_tv);
			tvLepes.setPadding(MainTabActivity.convertDpToPixel(5, MainTabActivity.maincontext), 0, 0, 0);
			tvLepes.setWidth(MainTabActivity.convertDpToPixel(145, MainTabActivity.maincontext));
			tvLepes.setBackgroundResource(R.drawable.border);
			tr.addView(tvLepes);
			// Idő
			TextView tvIdo = new TextView(MainTabActivity.maincontext, null, R.style.BodyText);
			tvIdo.setText(String.valueOf(lepes.ido));
			tvIdo.setLayoutParams(lp_tv);
			tvIdo.setWidth(MainTabActivity.convertDpToPixel(70, MainTabActivity.maincontext));
			tvIdo.setBackgroundResource(R.drawable.border);
			tvIdo.setGravity(Gravity.CENTER);
			tr.addView(tvIdo);
			// Megjegyzés
			TextView tvMegjegyzes = new TextView(MainTabActivity.maincontext, null, R.style.BodyText);
			tvMegjegyzes.setText(lepes.megjegyzes);
			tvMegjegyzes.setLayoutParams(lp_tv);
			tvMegjegyzes.setPadding(MainTabActivity.convertDpToPixel(5, MainTabActivity.maincontext), 0, 0, 0);
			tvMegjegyzes.setWidth(MainTabActivity.convertDpToPixel(195, MainTabActivity.maincontext));
			tvMegjegyzes.setBackgroundResource(R.drawable.border);
			tr.addView(tvMegjegyzes);
			// if ( ( i % 2 ) == 0 ) {
			// tr.setBackgroundColor(Color.DKGRAY);
			// }
			// else {
			// tr.setBackgroundColor(Color.LTGRAY);
			// }

			/* Add row to TableLayout. */
			table.addView(tr, new android.widget.TableRow.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		}

	}

	private void TablePurge(int tableId) {
		Object o = (Object) V.findViewById(tableId);
		if ( o instanceof TableLayout ) {
			TableLayout table = (TableLayout) o;
			int nIndex = table.getChildCount() - 1;
			// törölni kell ha már van benne sor
			if ( nIndex > 1 ) {
				for ( int i = nIndex; i >= 0; i-- ) {
					table.removeViewAt(i);
				}
			}
		}
	}
}
