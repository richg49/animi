package hu.promarkvf.animi_1;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Vendegek {
	static Vendeg[] vendegek;
	public String[] nevek;

	public Vendegek() {
		super();
		Vendegek.vendegek = null;

	}

	public void Vendegek_fill(final String resp) {
		Vendegek.vendegek = null;

		try {
			JSONObject root = new JSONObject(resp);
			JSONArray jArray = root.getJSONArray("posts");
			int jsoncount = jArray.length();
			Vendegek.vendegek = new Vendeg[jsoncount];
			this.nevek = new String[jsoncount];

			for ( int i = 0; i < jsoncount; i++ ) {
				JSONObject e = jArray.getJSONObject(i);
				String s = e.getString("post");
				JSONObject jObject = new JSONObject(s);
				int id = jObject.getInt("id");
				String nev = URLDecoder.decode(jObject.getString("nev"), "UTF-8");
				String rnev = URLDecoder.decode(jObject.getString("username"), "UTF-8");
				Vendegek.vendegek[i] = new Vendeg(id, nev, rnev, null);
				this.nevek[i] = nev + " \"" + rnev + "\"";
			}
		}
		catch ( JSONException e ) {
			e.printStackTrace();
		}
		catch ( UnsupportedEncodingException e1 ) {
			e1.printStackTrace();
		}
	}

	public int GetVendegId(int pos) {
		if ( Vendegek.vendegek != null && Vendegek.vendegek.length > pos && pos >= 0 ) {
			return Vendegek.vendegek[pos].id;
		}
		else {
			return 0;
		}

	}
	public Vendeg GetVendeg(int pos) {
		if ( Vendegek.vendegek != null && Vendegek.vendegek.length > pos && pos >= 0 ) {
			return Vendegek.vendegek[pos];
		}
		else {
			return null;
		}

	}
}
