package ideal_thermoresistance.parameters;

public enum Unit {
	// Energy
	erg("erg", 1.),
	eV("eV", 1.6021765653535353535e-12),
	J("J", 1e+7),
	// Mass
	g("g", 1),
	kg("kg", 1000),
	me("me", 9.1093829140404040404e-31),
	// Temperature
	K("K", 1.),
	// Concentration
	reverse_cm3("cm^-3", 1.),
	reverse_m3("m^-3", 1e+6);
	
	public static final Unit[] mass;
	public static final Unit[] energy;
	public static final Unit[] temperature;
	public static final Unit[] concentration;
	
	static {
		mass = new Unit[]{g, kg, me};
		energy = new Unit[]{erg, eV, J};
		temperature = new Unit[]{K};
		concentration = new Unit[]{reverse_cm3, reverse_m3};
	}
	
	private final double value;
	private final String name;
	
	Unit(String name, double value)
	{
		this.value = value;
		this.name = name;
	}
	
	public String toString()
	{
		return name;
	}
	
	public double value()
	{
		return value;
	}
}
