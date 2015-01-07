package ideal_thermoresistance.functions;

import java.math.BigDecimal;

import ideal_thermoresistance.math.BigDecimalMath;
import ideal_thermoresistance.math.ZeroFinder;
import ideal_thermoresistance.math.ZeroFinder.BDFunction;
import ideal_thermoresistance.math.ZeroFinder.DoubleFunction;

/**
 * Polynomial of form x^4 + a*x^3 + b*x^2 + cx + d;
 */
public class QuarticPolynomial2 {
	private BigDecimalMath bdm = new BigDecimalMath();
	
	private BigDecimal a;
	private BigDecimal b;
	private BigDecimal c;
	private BigDecimal d;
	
	private double sqrt(double d) {
		return Math.sqrt(d);
	}
	
	public QuarticPolynomial2(double a, double b, double c, double d) {
		this.a = bdm.toBD(a);
		this.b = bdm.toBD(b);
		this.c = bdm.toBD(c);
		this.d = bdm.toBD(d);
	}
	public QuarticPolynomial2(BigDecimal a, BigDecimal b, BigDecimal c, BigDecimal d) {
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
	public BigDecimal[] getRoots() {
		/* The equation is converted to the canonical form */
		BigDecimal p = bdm.div( bdm.sub(bdm.mult(8, b), bdm.mult(3, a.pow(2))), 8 );
		BigDecimal q = bdm.div( bdm.sum(new BigDecimal[]{
				bdm.mult(8, c),
				a.pow(3),
				bdm.mult(4, bdm.mult(a, b)).negate()
		}), 8 );
		BigDecimal r = bdm.div( bdm.sum(new BigDecimal[]{
				bdm.mult(16, bdm.mult(a.pow(2), b)),
				bdm.mult(64, bdm.mult(a, c)).negate(),
				bdm.mult(3, a.pow(4)).negate(),
				bdm.mult(256, d)
		}), 256 );
		/* y^4 + p*y^2 +q*y + r = 0 */
		BigDecimal roots[] = new BigDecimal[4];
		
		if (q.signum() == 0) {
			BigDecimal temp = bdm.sqrt(bdm.sub(p.pow(2), bdm.mult(4, r)));
			
			roots[0] = bdm.sqrt( bdm.div(bdm.sub(p.negate(), temp), 2) );
			roots[1] = roots[0].negate();
			roots[2] = bdm.sqrt( bdm.div(bdm.add(p.negate(), temp), 2) );
			roots[3] = roots[2].negate();
		}
		else {
			BigDecimal temp = bdm.sub(p.pow(2), bdm.mult(4, r));
			CubicPolynomial2 aux_poly = new CubicPolynomial2(p, 
					bdm.div( bdm.sub(p.pow(2), bdm.mult(4, r)), 4 ), 
					bdm.div( q.pow(2).negate(), 8 ));
			BigDecimal[] z_roots = aux_poly.getApproximateRoots();
			BigDecimal z = bdm.toBD(0);
			for (int i = 0; i < z_roots.length; ++i) {
				if (z_roots[i] != null && z_roots[i].signum() > 0) {
					z = z_roots[i];
				}
			}
			
			if (z.signum() == 0) {
				for (int i = 0; i < z_roots.length; ++i) {
					if (z_roots[i] != null && z_roots[i].signum() != 0) {
						z = z_roots[i].abs();
					}
				}
				//debugPrint("Z is 0");
				if (z.signum() == 0) {
					return roots;
				}
			}
			
			temp = bdm.mult(2, z);
			BigDecimal sqrt_0 = bdm.sqrt(temp);
			BigDecimal temp1 = bdm.sub(temp, bdm.mult(4, 
					bdm.sum(new BigDecimal[]{
							bdm.div(p, 2),
							z,
							bdm.div(q, bdm.mult(2, sqrt_0))
					})));
			BigDecimal temp2 = bdm.sub(temp, bdm.mult(4, 
					bdm.sum(new BigDecimal[]{
							bdm.div(p, 2),
							z,
							bdm.div(q, bdm.mult(2, sqrt_0)).negate()
					})));
			BigDecimal sqrt_1 = bdm.sqrt(temp1);
			BigDecimal sqrt_2 = bdm.sqrt(temp2);
			
			//debugPrint(q/2/Math.sqrt(2*z));
			if (sqrt_1 != null) {
				roots[0] = bdm.sub( sqrt_0, sqrt_1 );
				roots[1] = bdm.add( sqrt_0, sqrt_1 );
			}
			else
				roots[0] = roots[1] = null;
			
			if (sqrt_2 != null) {
				roots[2] = bdm.sub( sqrt_0.negate(), sqrt_2 );
				roots[3] = bdm.add( sqrt_0.negate(), sqrt_2 );
			}
			else
				roots[2] = roots[3] = null;

			for (int i = 0; i < roots.length; i++)
				if (roots[i] != null) roots[i] = bdm.div(roots[i], 2);
		}
		
//		debugPrint("Coefs: " + a.doubleValue() + " " + b.doubleValue() + 
//				" " + c.doubleValue() + " " + d.doubleValue());
		debugPrint("==== QUARTIC EQUATION ERRORS ====");
		
		BigDecimal temp;
		
		for (int i = 0; i < roots.length; ++i) {
			if (roots[i] == null) continue;
			
			roots[i] = bdm.sub( roots[i], bdm.div(a, 4) );
			temp = bdm.sum(new BigDecimal[]{
				roots[i].pow(4),
				bdm.mult(a, roots[i].pow(3)),
				bdm.mult(b, roots[i].pow(2)),
				bdm.mult(c, roots[i]),
				d
			});
			debugPrint("Error for root " + i + "(" + roots[i].doubleValue() + "): " + temp.doubleValue());
		}
		
		return roots;
	}
	
	public BigDecimal[] getApproximateRoots() {
		BigDecimal[] bd_roots = getRoots();
		BigDecimal[] app_roots = new BigDecimal[bd_roots.length];
		
		for (int i = 0; i < app_roots.length; ++i) {
			if (bd_roots[i] == null) {
				app_roots[i] = null;
				continue;
			}
			app_roots[i] = approximateRoot(bd_roots[i]);
//			app_roots[i] = approximateRoot(bd_roots[i].abs());
		}
		
		return app_roots;
	}
	public double[] getBestRoots() {
		BigDecimal[] app_roots = getApproximateRoots();
		double[] ret = new double[app_roots.length];
		
		for (int i = 0; i < ret.length; ++i) {
			if (app_roots[i] != null)
				ret[i] = app_roots[i].doubleValue();
			else
				ret[i] = Double.NaN;
		}
		
		return ret;
	}
	
	public BigDecimal approximateRoot(BigDecimal root_start) {
		BigDecimal ret = ZeroFinder.newtonsMethod(new BDFunction() {
			public BigDecimal compute(BigDecimal x, BigDecimal[] params,
					BigDecimalMath bdm) {
				BigDecimal a = params[0];
				BigDecimal b = params[1];
				BigDecimal c = params[2];
				BigDecimal d = params[3];
				return bdm.sum(new BigDecimal[]{
						x.pow(4),
						bdm.mult(a, x.pow(3)),
						bdm.mult(b, x.pow(2)),
						bdm.mult(c, x),
						d
					});
			}
		}, new BDFunction() {
			public BigDecimal compute(BigDecimal x, BigDecimal[] params,
					BigDecimalMath bdm) {
				BigDecimal a = params[0];
				BigDecimal b = params[1];
				BigDecimal c = params[2];
				return bdm.sum(new BigDecimal[]{
						bdm.mult(4, x.pow(3)),
						bdm.mult(3, bdm.mult(a, x.pow(2))),
						bdm.mult(2, bdm.mult(b, x)),
						c
					});
			}
		}, root_start, new BigDecimal[]{a, b, c, d});
		return ret;
	}
	
	private void debugPrint(String s) {
		//System.out.println(s);
	}
}
