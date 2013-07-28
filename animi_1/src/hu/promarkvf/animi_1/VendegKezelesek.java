package hu.promarkvf.animi_1;

import org.json.JSONException;
import org.json.JSONObject;

public class VendegKezelesek {
	VendegKezeles[] vendegKezelesek;

	public VendegKezelesek() {
		super();
		this.vendegKezelesek = null;
	}

	public void VendegKezelesek_fill(final String resp) {
		this.vendegKezelesek = null;

		try {
			JSONObject root = new JSONObject(resp);
			// -- üress-e a root
			if ( !root.isNull("posts") ) {
				JSONObject jsonkezelesek = root.getJSONObject("posts");
				int db = jsonkezelesek.length();
				vendegKezelesek = new VendegKezeles[db];
				// --- JSON feldolgozás
				for ( int i = 1; i <= db; i++ ) {
					JSONObject jsonkezelesekr = jsonkezelesek.getJSONObject(Integer.toString(i));
					vendegKezelesek[i-1] = new VendegKezeles(jsonkezelesekr.getInt("id"), jsonkezelesekr.getString("azonosito"), jsonkezelesekr.getString("leiras"), jsonkezelesekr.getInt("mw_kezeles_id"), jsonkezelesekr.getString("datum"));
				}
			}
		}
		catch ( JSONException e ) {
			e.printStackTrace();
		}
	}

	public int Lenght() {
		if ( vendegKezelesek != null ) {
			return vendegKezelesek.length;
		} else {
			return 0;
		}
	}
	
	public VendegKezeles GetVendegkezeles(int pos) {
		if ( vendegKezelesek != null && pos >= 0 && pos < vendegKezelesek.length) {
			return vendegKezelesek[pos];
		} else {
			return null;
		}
		
	}
	public boolean IsEmpty() {
		if ( vendegKezelesek == null ) {
			return true;
		} else {
			return false;
		}
	}
}
