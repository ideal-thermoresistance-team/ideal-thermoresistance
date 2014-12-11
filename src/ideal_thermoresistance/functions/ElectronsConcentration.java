package ideal_thermoresistance.functions;

import ideal_thermoresistance.parameters.DoubleParameterName;
import ideal_thermoresistance.parameters.Parameters;

public class ElectronsConcentration implements Function {
	private FermiLevel fermi_level = new FermiLevel();

	public double compute(Parameters params, double T) {
		double Eg = params.getDouble(DoubleParameterName.Eg);
		
		double me = params.getDouble(DoubleParameterName.me);
		
		// TODO: Move constants to the happier land.
		double k  = 1.38e-16;
		double m0 = 9.1e-28;
		
		double NC = 2.51e19 * Math.pow(me/m0 * T/300, 1.5);
		
		double root = fermi_level.compute(params, T);
		
		return NC * root * Math.exp(Eg / (k*T));
	}

}
