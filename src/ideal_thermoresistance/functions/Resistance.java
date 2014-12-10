package ideal_thermoresistance.functions;

import ideal_thermoresistance.parameters.DoubleParameterName;
import ideal_thermoresistance.parameters.Parameters;

public class Resistance implements Function {

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
		double m0 = 9.1e-28;
		double e  = 4.8e-10; // Is it CGS? 
		
		double NC = 2.51e19 * Math.pow(me/m0 * T/300, 1.5);
		double NV = 2.51e19 * Math.pow(mh/m0 * T/300, 1.5);
		
		double q0 = Math.exp(Eg / (k*T));
		double q1 = q0 * Math.exp(-Ed1 / (k*T));
		double q2 = q0 * Math.exp(-Ed2 / (k*T));
		
		double a = (Nd1*q2 + Nd2*q1) - (NC*(q1 + q2) - NV*q0);
		double b = NC*q1*q2 - NV*q0*(q1 + q2);
		double c = NV*q0*q1*q2;
		
		double denom = (Nd1 + Nd2)*q0 - NC;
		
		a /= denom;
		b /= denom;
		c /= denom;
		
		CubicPolynomial poly = new CubicPolynomial(a, b, c);
		
		double[] roots = poly.getRoots();
		double root = 0;
		
		for (int i = 0; i < roots.length; ++i) {
			if (roots[i] > 0 && !Double.isNaN(roots[i])) {
				root = roots[i];
				break;
			}
		}
		
		double n = NC * root / q0;
		double p = NV / root;
		// Share of charged particles:
		double Nd1_c = Nd1 / (1 + q1 / root);
		double Nd2_c = Nd2 / (1 + q2 / root);
		
		// TODO: These parameters must be set by user.
		double Cn = 1.0;
		double Cp = 1.0;
		double T0n = 1.0;
		double T0p = 1.0;
		
		
		double mu_n = Cn / (Math.pow(T/T0n, 1.5) + 
				(Nd1_c + Nd2_c + p)*Math.pow(T0n/T, 1.5));
		double mu_p = Cp / (Math.pow(T/T0p, 1.5) + 
				(Nd1_c + Nd2_c + n)*Math.pow(T0p/T, 1.5));
		
		double sigma = e * (n*mu_n + p*mu_p);
		
		return 1/sigma;
	}

}
