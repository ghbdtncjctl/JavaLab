package obba;
interface consumable
{
	public void consume();
}

public abstract class Food implements consumable,Nutritious {
	public String name = null;
	public Food(String name) {
	this.name = name;
	}
	public boolean equals(Object arg0) {
	if (!(arg0 instanceof Food)) return false; // ��� 1
	if (name==null || ((Food)arg0).name==null) return false; // ��� 2
	return name.equals(((Food)arg0).name); // ��� 3
	}
	public String getName() {
	return name;
	}
	public void setName(String name) {
	this.name = name;
	}
}
