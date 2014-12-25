package ideal_thermoresistance.parameters;

public enum Unit {
	// Energy
	erg("erg", 1., 0),
	eV("eV", 1.6021765653535353535e-12, 1),
	J("J", 1e+7, 2),
	// Mass
	g("g", 1, 0),
	kg("kg", 1000, 1),
	me("me", 9.1093829140404040404e-28, 2),
	// Temperature
	K("K", 1., 0),
	// Concentration
	reverse_cm3("cm^-3", 1., 0),
	reverse_m3("m^-3", 1e+6, 1),
	// movement
	constants("*", 1, 0);
	
	public static final Unit[] mass;
	public static final Unit[] energy;
	public static final Unit[] temperature;
	public static final Unit[] concentration;
	public static final Unit[] movement;
	
	static {
		mass = new Unit[]{g, kg, me};
		energy = new Unit[]{erg, eV, J};
		temperature = new Unit[]{K};
		concentration = new Unit[]{reverse_cm3, reverse_m3};
		movement = new Unit[]{constants};
	}
	
	private final double value;
	private final String name;
	private final int pos;
	
	Unit(String name, double value, int pos)
	{
		this.value = value;
		this.name = name;
		this.pos = pos;
	}
	
	public String toString()
	{
		return name;
	}
	
	public double value()
	{
		return value;
	}
	
	public int getPos()
	{
		return pos;
	}
}
