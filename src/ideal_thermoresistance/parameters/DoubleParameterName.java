package ideal_thermoresistance.parameters;

public enum DoubleParameterName {
	Eg(Unit.energy), 
	me(Unit.mass), 
	mh(Unit.mass),
	Ed1(Unit.energy),
	Nd1(Unit.concentration),
	Ed2(Unit.energy),
	Nd2(Unit.concentration),
	T1(Unit.temperature), 
	T2(Unit.temperature),
	Cn(Unit.movement),
	Cp(Unit.movement),
	T0n(Unit.temperature),
	T0p(Unit.temperature);
	
	public final Unit[] units;
	
	private DoubleParameterName(final Unit[] units)
	{
		this.units = units;
	}
	
	public double value(double value, int unit)
	{
		return value * units[unit].value();
	}
	
}
