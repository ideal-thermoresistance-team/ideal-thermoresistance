package ideal_thermoresistance.parameters;

import java.util.HashMap;
import java.util.Observable;

import ideal_thermoresistance.functions.Function;

public class Parameters extends Observable {
	private HashMap<DoubleParameterName, Double> doubleVals;
	private HashMap<BooleanParameterName, Boolean> boolVals;
	private Function func;
	
	public Parameters()
	{
		doubleVals = new HashMap<>();
		boolVals = new HashMap<>();
	}
	
	public Function getFunction() {
		return func;
	}
	public double getDouble(DoubleParameterName name) {
		return doubleVals.get(name);
	}
	public boolean getBoolean(BooleanParameterName name) {
		return boolVals.get(name);
	}
	
	public void setFunction(Function function)
	{
		func = function;
	}
	
	public void setDouble(DoubleParameterName name, double value)
	{
		doubleVals.put(name, value);
	}
	
	public void setBoolean(BooleanParameterName name, boolean value)
	{
		boolVals.put(name, value);
	}
	
	public void update()
	{
		setChanged();
		notifyObservers();
	}
}
