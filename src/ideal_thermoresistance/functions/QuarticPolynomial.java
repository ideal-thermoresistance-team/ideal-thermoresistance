package ideal_thermoresistance.functions;

/**
 * Polynomial of form x^4 + a*x^3 + b*x^2 + cx + d;
 */
public class QuarticPolynomial {
	private double a;
	private double b;
	private double c;
	private double d;
	
	private double sqrt(double d) {
		return Math.sqrt(d);
		//return Math.sqrt(Math.abs(d));
	}
	
	public QuarticPolynomial(double a, double b, double c, double d) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
	}
	/**
	 * The algorithm is honestly stolen from http://en.wikipedia.org/wiki/Quartic_function#General_formula_for_roots
	 * and http://ateist.spb.ru/mw/alg4.htm
	 * Complex numbers are currently ignored. 
	 * @return Roots of the polynomial.
	 */
	public double[] getRoots() {
		/* The equation is converted to the canonical form */
		double p = (8*b - 3*a*a) / 8;
		double q = (8*c + a*a*a - 4*a*b) / 8;
		double r = (16*a*a*b - 64*a*c - 3*Math.pow(a, 4) + 256*d) / 256;
		/* y^4 + p*y^2 +q*y + r = 0 */
		double roots[] = new double[4];
		
		if (q == 0) {
			roots[0] = Math.sqrt( (-p - Math.sqrt(p*p - 4*r))/2 );
			roots[1] = -roots[0];
			roots[2] = Math.sqrt( (-p + Math.sqrt(p*p - 4*r))/2 );
			roots[3] = -roots[2];
		}
		else {
			CubicPolynomial aux_poly = new CubicPolynomial(p, (p*p - 4*r)/4, -q*q/8);
			double[] z_roots = aux_poly.getRoots();
			double z = 0;
			for (int i = 0; i < z_roots.length; ++i) {
				if (!Double.isNaN(z_roots[i]) && z_roots[i] > 0)
					z = z_roots[i];
			}
			if (z == 0) {
				//System.out.println("Z is 0");
				return roots;
			}
			
			//System.out.println(q/2/Math.sqrt(2*z));
			roots[0] = Math.sqrt(2*z) - sqrt(2*z - 4*(p/2 + z + q/2/Math.sqrt(2*z)));
			roots[0] /= 2;
			roots[1] = Math.sqrt(2*z) + sqrt(2*z - 4*(p/2 + z + q/2/Math.sqrt(2*z)));
			roots[1] /= 2;
			roots[2] = -Math.sqrt(2*z) - sqrt(2*z - 4*(p/2 + z - q/2/Math.sqrt(2*z)));
			roots[2] /= 2;
			roots[3] = -Math.sqrt(2*z) + sqrt(2*z - 4*(p/2 + z - q/2/Math.sqrt(2*z)));
			roots[3] /= 2;
		}
		
		System.out.println("==== QUARTIC EQUATION ERRORS ====");
		
		//System.out.println("0: " + (Math.pow(roots[0], 4) + p*Math.pow(roots[0], 2) + q*roots[0] + r));
		for (int i = 0; i < roots.length; ++i) {
			roots[i] -= a/4;
			double temp = (Math.pow(roots[i], 4) + a*Math.pow(roots[i], 3) + b*Math.pow(roots[i], 2) + c*roots[i] + d);
			System.out.println("Error for root " + i + "(" + roots[i] + "): " + temp);
		}
		
		return roots;
	}
}
