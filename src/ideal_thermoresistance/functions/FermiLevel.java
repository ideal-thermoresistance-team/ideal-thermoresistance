package ideal_thermoresistance.functions;

import ideal_thermoresistance.parameters.DoubleParameterName;
import ideal_thermoresistance.parameters.Parameters;
import ideal_thermoresistance.parameters.Unit;

/**
 * Attention! The function returns not the actual fermi level, but exp(mu / (k*T)).
 * This is done to avoid unnecessary calculations.
 */
public class FermiLevel implements Function {
	
	private double approximatePoly(double root_start, double a, double b, double c, double d) {
		double root = root_start;
		double root_prev = 0;
		double func, deriv;
		// TODO: Give more precise end condition.
		for (int i = 0; i < 100; ++i) {
			func  = Math.pow(root, 4);
			func += a * Math.pow(root, 3);
			func += b * Math.pow(root, 2);
			func += c * Math.pow(root, 1);
			func += d;
			if (i ==0 ) System.out.println("func0: " + func);
			deriv  = 4*Math.pow(root, 3);
			deriv += a * 3*Math.pow(root, 2);
			deriv += b * 2*Math.pow(root, 1);
			deriv += c;
			root_prev = root;
			root = root - func/deriv;
		}
		func  = Math.pow(root, 4);
		func += a * Math.pow(root, 3);
		func += b * Math.pow(root, 2);
		func += c * Math.pow(root, 1);
		func += d;
		System.out.println("Last error: " + func);
		System.out.println("Last eps: " + (root-root_prev));
		
		return root;
	}
	
	private double approximate(Parameters params, double T) {
		double Eg = params.getDouble(DoubleParameterName.Eg);
		double Ed1 = params.getDouble(DoubleParameterName.Ed1);
		double Ed2 = params.getDouble(DoubleParameterName.Ed2);
		double Nd1 = params.getDouble(DoubleParameterName.Nd1);
		double Nd2 = params.getDouble(DoubleParameterName.Nd2);
		
		double me = params.getDouble(DoubleParameterName.me);
		double mh = params.getDouble(DoubleParameterName.mh);
		
		double k  = 1.380648813131313e-16;
		double m0 = Unit.me.value();
		
		double NC = 2.51e19 * Math.pow(me/m0 * T/300, 1.5);
		double NV = 2.51e19 * Math.pow(mh/m0 * T/300, 1.5);
		
		double q0 = Math.exp(Eg / (k*T));
		double q1 = Math.exp((-Ed1) / (k*T));
		double q2 = Math.exp((-Ed2) / (k*T));
		
		double root_start = Math.sqrt(NV/q0/NC);
		double x = root_start;
		
		double func, deriv;
		func = deriv = 0;
		int iter_count = 10000;
		
		do {
			double Nd1_c = Nd1*x / (x + q0*q1);
			double Nd2_c = Nd2*x / (x + q0*q2);
			double n = NC * x / q0;
			double p = NV / x;
			
			double Nd1_c_deriv = Nd1*q0*q1/(x+q0*q1)/(x+q0*q1);
			double Nd2_c_deriv = q0*q2*Nd2/(x+q0*q2)/(x+q0*q2);
			double n_deriv = NC / q0;
			double p_deriv = -NV / x / x;
			
			func = Nd1_c + Nd2_c + p - n;
			deriv = Nd1_c_deriv + Nd2_c_deriv + p_deriv - n_deriv;
			
			x = x - func/deriv;
			
		} while ((Math.abs(func) > 1e-4 || Math.abs(func/deriv) > 1e-4) && --iter_count >= 0);
		System.out.println("Last error: " + func);
		System.out.println("Last eps: " + (func/deriv));
		System.out.println("Last ic: " + iter_count);
		
		if (Math.abs(func) > 1 || x < 0) {
			double x2 = getPreciseSolution(params, T);
			double Nd1_c = Nd1*x2 / (x2 + q0*q1);
			double Nd2_c = Nd2*x2 / (x2 + q0*q2);
			double n = NC * x2 / q0;
			double p = NV / x2;
			
			double error = Nd1_c + Nd2_c + p - n;
			if (Math.abs(error) < Math.abs(func) || x < 0)
				return x2;
		}
		
		return x;
	}
	
	private double getPreciseSolution(Parameters params, double T) {
		double e = 1.6e-12;
		
		double Eg = params.getDouble(DoubleParameterName.Eg) / e;
		double Ed1 = params.getDouble(DoubleParameterName.Ed1) / e;
		double Ed2 = params.getDouble(DoubleParameterName.Ed2) / e;
		double Nd1 = params.getDouble(DoubleParameterName.Nd1);
		double Nd2 = params.getDouble(DoubleParameterName.Nd2);
		
		double me = params.getDouble(DoubleParameterName.me);
		double mh = params.getDouble(DoubleParameterName.mh);
		
		double k  = 8.617e-5;
		double m0 = 9.1e-28;
		
		
		double NC = 2.51e19 * Math.pow(me/m0 * T/300, 1.5);
		double NV = 2.51e19 * Math.pow(mh/m0 * T/300, 1.5);
		
		double q0 = Math.exp(Eg / (k*T));
		double q1 = Math.exp((-Ed1) / (k*T));
		double q2 = Math.exp((-Ed2) / (k*T));
		
		if (Nd1 == Nd2 && Nd1 == 0) {
			double root = Math.sqrt(NV*q0/NC);
			double Nd1_c = Nd1 / (1 + q0*q1/root);
			double Nd2_c = Nd2 / (1 + q0*q2/root);
			double n = NC * root / q0;
			double p = NV / root;
			System.out.println("1: " + (Nd1_c + Nd2_c - n + p));
			//double n = NC * root / q0;
			//double p = NV / root;
			//return Math.sqrt(NV*q0/NC);
		}
		
		double a = NC*(q1 + q2) - (Nd1 + Nd2);
		double b = (NC*q1*q2 - NV/q0) - (Nd1*q2 + Nd2*q1);
		double c = -NV*(q1+q2)/q0;
		double d = -NV*q1*q2/q0;
		
		a /= NC;
		b /= NC;
		c /= NC;
		d /= NC;
		//System.out.println("coefs: " + a + " " + b + " " + c + " " + d);
		System.out.println("(q0, q1, q2) = " + "(" + q0 + ", " + q1 + ", " + q2 + ")");
		
		QuarticPolynomial poly = new QuarticPolynomial(a, b, c, d);
		
		double[] roots = poly.getRoots();
		double root = 0;
		
		for (int i = 0; i < roots.length; ++i) {
			if (roots[i] > root && !Double.isNaN(roots[i])) {
				root = roots[i];
				//break;
			}
		}
		
		if (root == 0) {
			root = approximatePoly(Math.sqrt(NV/q0/NC), a, b, c, d);
			if (root < 0) root = 1e-19;
		}
		else root *= q0;
		
		double Nd1_c = Nd1 / (1 + q0*q1/root);
		double Nd2_c = Nd2 / (1 + q0*q2/root);
		double n = NC * root / q0;
		double p = NV / root;
		double temp = 0;
		
		System.out.println("root = " + root);
		System.out.println("1: " + (Nd1_c + Nd2_c - n + p));
		temp  = Math.pow(root, 4);
		temp += a*q0 * Math.pow(root, 3);
		temp += b*q0*q0 * Math.pow(root, 2);
		temp += c*q0*q0*q0 * Math.pow(root, 1);
		temp += d*q0*q0*q0*q0;
		System.out.println("2: " + temp);
		
		return root;
	}
	
	public double getExp(Parameters params, double T) {
		System.out.println("=== T = " + T + " ===");
		return approximate(params, T);
	}
	public double compute(Parameters params, double T) {
		double res = getExp(params, T);
		double k  = 8.617e-5;
		return Math.log(res) * k * T;
	}

	public String getUnits() {
		return "eV";
	}
	public String getLongName() {
		return "Fermi level";
	}
	public String getName() {
		return "mu";
	}
}
