package ideal_thermoresistance.functions;

import ideal_thermoresistance.math.BigDecimalMath;
import ideal_thermoresistance.parameters.DoubleParameterName;
import ideal_thermoresistance.parameters.Parameters;
import ideal_thermoresistance.parameters.Unit;

public class FermiLevel implements Function {
	private static double k  = 1.380648813131313e-16;
	
    private double Eg;
    private double Ed1;
    private double Ed2;
    private double Nd1;
    private double Nd2;
    private double me;
    private double mh;
    
	private double NC;
	private double NV;
    
    /**
     * Load parameters from Parameters class.
     */
    private void loadParameters(Parameters params, double T) {
    	Eg = params.getDouble(DoubleParameterName.Eg);
        Ed1 = params.getDouble(DoubleParameterName.Ed1);
        Ed2 = params.getDouble(DoubleParameterName.Ed2);
        Nd1 = params.getDouble(DoubleParameterName.Nd1);
        Nd2 = params.getDouble(DoubleParameterName.Nd2);
        
        me = params.getDouble(DoubleParameterName.me);
        mh = params.getDouble(DoubleParameterName.mh);
        
        double m0 = Unit.me.value();
		NC = 2.51e19 * Math.pow(me/m0 * T/300, 1.5);
		NV = 2.51e19 * Math.pow(mh/m0 * T/300, 1.5);
    }
	
	private double getPreciseSolution(Parameters params, double T) {
		/* Calculating auxiliary variables. */
		
		double q0 = Math.exp(Eg / (k*T));
		double q1 = Math.exp((-Ed1) / (k*T));
		double q2 = Math.exp((-Ed2) / (k*T));
		
		if (Double.isInfinite(q0)) {
			
		}
		
		/* Calculating quartic polynomial coefficients */
		
//		double a = NC*(q1 + q2) - (Nd1 + Nd2);
//		double b = (NC*q1*q2 - NV/q0) - (Nd1*q2 + Nd2*q1);
//		double c = -NV*(q1+q2)/q0;
//		double d = -NV*q1*q2/q0;
		double a = NC*(q1 + q2);
		double b = (NC*q1*q2 - NV/q0) - (Nd1*q1 + Nd2*q2);
		double c = -NV*(q1+q2)/q0 - (Nd1 + Nd2)*q1*q2;
		double d = -NV*q1*q2/q0;
		
		a /= NC;
		b /= NC;
		c /= NC;
		d /= NC;
		
		/* Calculating roots of the polynomial. Biggest positive root is taken. */
		
		QuarticPolynomial poly = new QuarticPolynomial(a, b, c, d);

		double[] roots = poly.getBestRoots();
		double root = 0;
		
		for (int i = 0; i < roots.length; ++i) {
			if (roots[i] > root && !Double.isNaN(roots[i])) {
				root = roots[i];
			}
		}
		
		/* If no root was found, via precise solution, approximation is done */
		
		if (root == 0) {
			QuarticPolynomial2 poly2 = new QuarticPolynomial2(a, b, c, d);

			double[] roots2;
			if (T > 120)
				roots2 = poly2.getBestRoots();
			else {
				BigDecimalMath bdm = new BigDecimalMath();
				roots2 = bdm.toDouble(poly2.getRoots());
			}
			
			for (int i = 0; i < roots.length; ++i) {
				if (roots2[i] > root && !Double.isNaN(roots2[i])) {
					root = roots2[i];
				}
			}
			root *= q0;
			if (root <= 0) root = 1e5;
		}
		else root *= q0;
		
		/* Input of some debug values */
		
		double Nd1_c = Nd1*root / (root + q0*q1);
		double Nd2_c = Nd2*root / (root + q0*q2);
		double n = NC * root / q0;
		double p = NV / root;
		double temp = 0;
		
		debugPrint("root = " + root);
		debugPrint("1: " + (Nd1_c + Nd2_c - n + p));
		temp  = Math.pow(root, 4);
		temp += a*q0 * Math.pow(root, 3);
		temp += b*q0*q0 * Math.pow(root, 2);
		temp += c*q0*q0*q0 * Math.pow(root, 1);
		temp += d*q0*q0*q0*q0;
		debugPrint("2: " + temp);
		
		/********************************/
		
		return root;
	}
	
	// Returns exp(mu / (k*T))
	public double getExp(Parameters params, double T) {
		debugPrint("=== T = " + T + " ===");
		loadParameters(params, T);
		return getPreciseSolution(params, T);
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
	
	private void debugPrint(String s) {
		//System.out.println(s);
	}
}
