package ideal_thermoresistance.functions;

import ideal_thermoresistance.parameters.Parameters;

public interface Function {
	public double compute(Parameters params, double T);
}
