package ideal_thermoresistance.functions;

import ideal_thermoresistance.parameters.DoubleParameterName;
import ideal_thermoresistance.parameters.Parameters;

public class ElectronMobility implements Function {

	private FermiLevel fermi_level = new FermiLevel();

	public double compute(Parameters params, double T) {
		double Eg = params.getDouble(DoubleParameterName.Eg);
		double Ed1 = params.getDouble(DoubleParameterName.Ed1);
		double Ed2 = params.getDouble(DoubleParameterName.Ed2);
		double Nd1 = params.getDouble(DoubleParameterName.Nd1);
		double Nd2 = params.getDouble(DoubleParameterName.Nd2);
		
		double mh = params.getDouble(DoubleParameterName.mh);
		
		// TODO: Move constants to the happier land.
		double k  = 1.38e-16;
		double m0 = 9.1e-28;
		
		double NV = 2.51e19 * Math.pow(mh/m0 * T/300, 1.5);
		
		double q1 = Math.exp((Eg-Ed1) / (k*T));
		double q2 = Math.exp((Eg-Ed2) / (k*T));
		
		double root = fermi_level.getExp(params, T);
		
		double p = NV / root;
		// Share of charged particles:
		double Nd1_c = Nd1 / (1 + q1 / root);
		double Nd2_c = Nd2 / (1 + q2 / root);
		
		double Cn = params.getDouble(DoubleParameterName.Cn);
		double T0n = params.getDouble(DoubleParameterName.T0n);
		
		// TODO: These formulae are operated in International System of Units
		double mu_n = Cn / (Math.pow(T/T0n, 1.5) + 
				(Nd1_c + Nd2_c + p)*Math.pow(T0n/T, 1.5));
		
		return mu_n;
	}

	public String getUnits() {
		return "Om*cm";
	}
}
