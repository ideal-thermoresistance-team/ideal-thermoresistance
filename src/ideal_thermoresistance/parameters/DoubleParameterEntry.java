package ideal_thermoresistance.parameters;

public class DoubleParameterEntry {
	private DoubleParameterName name;
	private int unit;
	private double value;
	
	public DoubleParameterEntry(DoubleParameterName name, int unit) {
		super();
		this.name = name;
		this.unit = unit;
		value = 0;
	}

	public int getUnit() {
		return unit;
	}

	public void setUnit(int unit) {
		this.unit = unit;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public DoubleParameterName getName() {
		return name;
	}
	
	public double compute()
	{
		return name.value(value, unit);
	}
	
	
}
