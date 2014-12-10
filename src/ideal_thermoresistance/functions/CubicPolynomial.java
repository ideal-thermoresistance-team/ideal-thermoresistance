package ideal_thermoresistance.functions;

/**
 * Polynomial of form x^3 + a*x^2 + b*x + c;
 */
public class CubicPolynomial {
	private double a;
	private double b;
	private double c;
	
	public CubicPolynomial(double a, double b, double c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}
	/**
	 * The algorithm is honestly stolen from http://algolist.manual.ru/maths/findroot/cubic.php
	 * Complex numbers are currently ignored. 
	 * @return Roots of the polynomial.
	 */
	public double[] getRoots() {
		double Q = (Math.pow(a, 2) - 3*b)/9;
		double R = (2*Math.pow(a, 3) - 9*a*b + 27*c) / 54;
		double roots[] = new double[3];
		
		if (Math.pow(R, 2) < Math.pow(Q, 3)) {
			// Vieta's formulas.
			double t = Math.acos(R / Math.pow(Q, 1.5)) / 3;
			
			roots[0] = -2 * Math.sqrt(Q)*Math.cos(t) - a/3;
			roots[1] = -2 * Math.sqrt(Q)*Math.cos(t + 2*Math.PI/3) - a/3;
			roots[2] = -2 * Math.sqrt(Q)*Math.cos(t - 2*Math.PI/3) - a/3;
		}
		else {
			// Cardano's formulas.
			double A = -Math.signum(R) * Math.pow(Math.abs(R) + 
					Math.sqrt(Math.pow(R, 2) - Math.pow(Q, 3)), 1.0/3);
			double B;
			
			if (A != 0) B = Q / A;
			else B = 0;
			
			roots[0] = (A + B) - a/3;
			if (A != B) {
				roots[1] = roots[2] = Double.NaN;
			}
			else {
				roots[1] = -A - a/3;
			}
		}
		
		return roots;
	}
}
