package hu.promarkvf.animi_1;

public class Vendeg {
	int id;
	String nev;
	Kezeles[] kezesek;
	
	public Vendeg(int id, String nev, Kezeles[] kezesek) {
		super();
		this.id = id;
		this.nev = nev;
		this.kezesek = kezesek;
	}

	public Vendeg() {
		super();
		this.id = (Integer) null;
		this.nev = null;
		this.kezesek = null;
	}
}
