package hu.promarkvf.animi_1;

public class VendegKezeles {
	public Integer id; 
	public String leiras; 
	public Integer mw_kezeles_id; 
	public String datum;
	
	public VendegKezeles() {
		super();
		this.id = 0;
		this.leiras = "";
		this.mw_kezeles_id = 0;
		this.datum = "";
	}

	public VendegKezeles(Integer id, String leiras, Integer mw_kezeles_id, String datum) {
		super();
		this.id = id;
		this.leiras = leiras;
		this.mw_kezeles_id = mw_kezeles_id;
		this.datum = datum;
	} 

}
