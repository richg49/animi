package hu.promarkvf.animi_1;

public class Vendeg {
	int id;
	String nev;
	String rnev;
	Kezeles[] kezesek;
	
	public Vendeg(int id, String nev, String rnev, Kezeles[] kezesek) {
		super();
		this.id = id;
		this.nev = nev;
		this.rnev = rnev;
		this.kezesek = kezesek;
	}

	public Vendeg() {
		super();
		this.id = (Integer) null;
		this.nev = null;
		this.rnev = null;
		this.kezesek = null;
	}
}
