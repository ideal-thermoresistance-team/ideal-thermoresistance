package ideal_thermoresistance.functions;

import ideal_thermoresistance.parameters.DoubleParameterName;
import ideal_thermoresistance.parameters.Parameters;
import ideal_thermoresistance.parameters.Unit;

public class Resistance implements Function {

	private FermiLevel fermi_level = new FermiLevel();

	public double compute(Parameters params, double T) {
		double Eg = params.getDouble(DoubleParameterName.Eg);
		double Ed1 = params.getDouble(DoubleParameterName.Ed1);
		double Ed2 = params.getDouble(DoubleParameterName.Ed2);
		double Nd1 = params.getDouble(DoubleParameterName.Nd1);
		double Nd2 = params.getDouble(DoubleParameterName.Nd2);
		
		double me = params.getDouble(DoubleParameterName.me);
		double mh = params.getDouble(DoubleParameterName.mh);
		
		// TODO: Move constants to the happier land.
		double k  = 1.38e-16;
		double m0 = Unit.me.value();
		double e  = 1.6e-19; 
		
		double NC = 2.51e19 * Math.pow(me/m0 * T/300, 1.5);
		double NV = 2.51e19 * Math.pow(mh/m0 * T/300, 1.5);
		
		double q0 = Math.exp(Eg / (k*T));
		double q1 = Math.exp((Eg-Ed1) / (k*T));
		double q2 = Math.exp((Eg-Ed2) / (k*T));
		
		double root = fermi_level.getExp(params, T);
		
		double n = NC * root / q0;
		double p = NV / root;
		// Share of charged particles:
		double Nd1_c = Nd1 / (1 + q1 / root);
		double Nd2_c = Nd2 / (1 + q2 / root);
		
		double Cn = params.getDouble(DoubleParameterName.Cn);
		double Cp = params.getDouble(DoubleParameterName.Cn);
		double T0n = params.getDouble(DoubleParameterName.T0n);
		double T0p = params.getDouble(DoubleParameterName.T0p);
		
		double mu_n = Cn / (Math.pow(T/T0n, 1.5) + 
				(Nd1_c + Nd2_c + p)*Math.pow(T0n/T, 1.5));
		double mu_p = Cp / (Math.pow(T/T0p, 1.5) + 
				(Nd1_c + Nd2_c + n)*Math.pow(T0p/T, 1.5));
		
		double sigma = e * (n*mu_n + p*mu_p) * 1e6;
		double rho = 1 / sigma;
		// Conversion to (Om * cm):
		rho *= 0.01;
		
		return rho;
	}

	public String getUnits() {
		return "Om*cm";
	}
	public String getLongName() {
		return "Resistivity";
	}
	public String getName() {
		return "rho";
	}
}
