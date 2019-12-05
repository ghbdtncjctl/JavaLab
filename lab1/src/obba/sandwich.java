package obba;
interface Nutritious{
	public int calculatecalories();
}
public class sandwich extends Food{
	
	public String filling1=null;
	public String filling2=null;
	public sandwich(String Fil1,String Fil2){
		super("sandwich");
		filling1=Fil1;
		filling2=Fil2;
	};
	public String getFilling1() {
		return filling1;
	}
	public void setFilling1(String filling1) {
		this.filling1 = filling1;
	}
	public String getFilling2() {
		return filling2;
	}
	public void setFilling2(String filling2) {
		this.filling2 = filling2;
	}
	public boolean equals(Object arg0) {
		if (!(arg0 instanceof Food)) return false;
		if (filling1==null || filling2==null || ((sandwich)arg0).filling1==null || ((sandwich)arg0).filling2==null) return false;
		return filling1.equals(((sandwich)arg0).filling1) && filling2.equals(((sandwich)arg0).filling2);
	}
	public void consume(){
		System.out.println(super.name+" c nachinkami "+filling1+" and "+filling2);
	}
	public int calculatecalories() {
		return super.name.length()+filling1.length()+filling2.length();
	}

}
