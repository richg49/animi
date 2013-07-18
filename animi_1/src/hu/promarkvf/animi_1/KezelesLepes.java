package hu.promarkvf.animi_1;

public class KezelesLepes {

	public int sorszam;
	public String lepes;
	public int ido;
	public String megjegyzes;
	public String[][] pixeltomb;
	public String pxtomb;
	public String pszin;
	public int szin_r;
	public int szin_g;
	public int szin_b;
	public int kezeles_reszletekid;

	public KezelesLepes(int dim_1, int dim_2) {
		sorszam = 0;
		lepes = "";
		ido = 0;
		megjegyzes = "";
		pixeltomb = new String[dim_1][dim_2];
		pxtomb = "";
		pszin = "";
		szin_r = 0;
		szin_g = 0;
		szin_b = 0;
		kezeles_reszletekid = 0;
	}

	public KezelesLepes() {
		sorszam = 0;
		lepes = "";
		ido = 0;
		megjegyzes = "";
		pixeltomb = null;
		pxtomb = "";
		pszin = "";
		szin_r = 0;
		szin_g = 0;
		szin_b = 0;
		kezeles_reszletekid = 0;
	}

}
