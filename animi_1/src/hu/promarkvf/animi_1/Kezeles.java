package hu.promarkvf.animi_1;

import org.json.JSONException;
import org.json.JSONObject;

public class Kezeles {
	public Integer kezeles_id;
	public String azonosito;
	public Integer berendezes_dim1;
	public Integer berendezes_dim2;
	public String leiras;
	public String diagnozis;
	public String anamnezis;
	public Integer osszlepes;
	public Integer osszido;
	public KezelesLepes[] lepes;

	public Kezeles() {
		super();
		this.kezeles_id = 0;
		azonosito = "";
		berendezes_dim1 = 0;
		berendezes_dim2 = 0;
		leiras = "";
		diagnozis = "";
		anamnezis = "";
		osszlepes = 0;
		osszido = 0;
		lepes = null;
	}

	public void Kezeles_fill(String resp) {
		Integer i = 0;
		try {
			// --- JSON feldolgozás
			JSONObject root = new JSONObject(resp);

			// -- üress-e a root
			if ( !root.isNull("posts") ) {
				JSONObject jsonkezeles = root.getJSONObject("posts");
				kezeles_id = jsonkezeles.getInt("kezeles_id");
				azonosito = jsonkezeles.getString("azonosito");
				berendezes_dim1 = jsonkezeles.getInt("berendezes_dim1");
				berendezes_dim2 = jsonkezeles.getInt("berendezes_dim2");
				leiras = jsonkezeles.getString("leiras");
				diagnozis = jsonkezeles.getString("diagnozis");
				anamnezis = jsonkezeles.getString("anamnezis");
				osszlepes = jsonkezeles.getInt("osszlepes");
				osszido = jsonkezeles.getInt("osszido");

				lepes = new KezelesLepes[osszlepes];

				for ( i = 0; i < osszlepes; i++ ) {
					JSONObject jsonkezelesLepes = jsonkezeles.getJSONObject(String.valueOf((i+1)));
					lepes[i] = new KezelesLepes();
					lepes[i].sorszam = jsonkezelesLepes.getInt("sorszam");
					lepes[i].kezeles_reszletekid = jsonkezelesLepes.getInt("kezeles_reszletekid");
					lepes[i].lepes = jsonkezelesLepes.getString("lepes");
					lepes[i].ido = jsonkezelesLepes.getInt("ido");
					lepes[i].szin_r = jsonkezelesLepes.getInt("szin_r");
					lepes[i].szin_g = jsonkezelesLepes.getInt("szin_g");
					lepes[i].szin_b = jsonkezelesLepes.getInt("szin_b");
					lepes[i].pszin = String.format("rgb(%d, %d, %d)", lepes[i].szin_r, lepes[i].szin_g, lepes[i].szin_b);
					lepes[i].megjegyzes = jsonkezelesLepes.getString("megjegyzes");
					lepes[i].pxtomb = jsonkezelesLepes.getString("pxtomb");
				}
			}
		}
		catch ( JSONException e ) {
			e.printStackTrace();
		}
	}
}
