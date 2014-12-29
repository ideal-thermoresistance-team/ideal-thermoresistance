package ideal_thermoresistance.functions;

import ideal_thermoresistance.math.BigDecimalMath;
import ideal_thermoresistance.math.ZeroFinder;
import ideal_thermoresistance.math.ZeroFinder.BDFunction;

import java.math.BigDecimal;

/**
 * Polynomial of form x^3 + a*x^2 + b*x + c;
 */
public class CubicPolynomial2 {
	BigDecimalMath bdm = new BigDecimalMath();
	
	private BigDecimal a;
	private BigDecimal b;
	private BigDecimal c;
	
	public CubicPolynomial2(double a, double b, double c) {
		this.a = bdm.toBD(a);
		this.b = bdm.toBD(b);
		this.c = bdm.toBD(c);
	}
	public CubicPolynomial2(BigDecimal a, BigDecimal b, BigDecimal c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}
	/**
	 * The algorithm is honestly stolen from http://algolist.manual.ru/maths/findroot/cubic.php
	 * Complex numbers are currently ignored. 
	 * @return Roots of the polynomial.
	 */
	public BigDecimal[] getRoots() {
		BigDecimal temp;
		
		BigDecimal Q = bdm.div(bdm.sub(a.pow(2), bdm.mult(bdm.toBD(3), b)), 9);
		temp = bdm.sum(new BigDecimal[]{
				bdm.mult(2, a.pow(3)), 
				bdm.mult(9, bdm.mult(a, b)).negate(),
				bdm.mult(27, c)
		});
		BigDecimal R = bdm.div(temp, 54);
		BigDecimal roots[] = new BigDecimal[3];
		
		BigDecimal Q3 = Q.pow(3);
		
		if (R.pow(2).compareTo(Q3) < 0) {
			BigDecimal Q_sqrt = bdm.sqrt(Q);
			// Vieta's formulas.
			temp = bdm.div(R, Q_sqrt.pow(3));
			double t = Math.acos(temp.doubleValue()) / 3;
			
			roots[0] = bdm.sub( bdm.mult(-2*Math.cos(t), Q_sqrt), bdm.div(a,3) );
			t += 2 * Math.PI/3;
			roots[1] = bdm.sub( bdm.mult(-2*Math.cos(t), Q_sqrt), bdm.div(a,3) );
			t -= 4 * Math.PI/3;
			roots[2] = bdm.sub( bdm.mult(-2*Math.cos(t), Q_sqrt), bdm.div(a,3) );
		}
		else {
			// Cardano's formulas.
			BigDecimal A = bdm.mult(-R.signum(), 
					bdm.cubicRoot(bdm.add(
					R.abs(), 
					bdm.sqrt(bdm.sub(R.pow(2), Q.pow(3))))));
			BigDecimal B;
			
			if (A.signum() != 0) B = bdm.div(Q, A);
			else B = bdm.toBD(0);
			
			roots[0] = bdm.sub( bdm.add(A, B), bdm.div(a, 3) );
			if (A != B) {
				roots[1] = roots[2] = null;
			}
			else {
				roots[1] = bdm.add( A, bdm.div(a, 3) ).negate();
			}
		}
		
		debugPrint("==== CUBIC EQUATION ERRORS ====");
		
		for (int i = 0; i < roots.length; ++i) {
			if (roots[i] == null) continue;
			BigDecimal temp3 = bdm.sum(new BigDecimal[]{
				roots[i].pow(3),
				bdm.mult(a, roots[i].pow(2)),
				bdm.mult(b, roots[i]),
				c
			});
			debugPrint("Error for root " + i + "(" + roots[i].doubleValue() + "): " + temp3.doubleValue());
		}
		
		return roots;
//		double[] roots2 = new double[3];
//		for (int i = 0; i < 3; ++i)
//			roots2[i] = roots[i].doubleValue();
//		
//		return roots2;
	}
	
	public BigDecimal[] getApproximateRoots() {
		BigDecimal[] bd_roots = getRoots();
		BigDecimal[] app_roots = new BigDecimal[bd_roots.length];
		
		for (int i = 0; i < app_roots.length; ++i) {
			// TODO: This is not the best way to exclude negative coefficients.
			if (bd_roots[i] == null) {
				app_roots[i] = null;
				continue;
			}
			app_roots[i] = approximateRoot(bd_roots[i]);
		}
		
		return app_roots;
	}
	public double[] getBestRoots() {
		BigDecimal[] bd_roots = getApproximateRoots();
		double[] roots = new double[bd_roots.length];
		
		for (int i = 0; i < roots.length; ++i) {
			if (bd_roots[i] == null) {
				roots[i] = Double.NaN;
				continue;
			}
			roots[i] = approximateRoot(bd_roots[i]).doubleValue();
		}
		
		return roots;
	}
	
	public BigDecimal approximateRoot(BigDecimal root_start) {
		BigDecimal ret = ZeroFinder.newtonsMethod(new BDFunction() {
			public BigDecimal compute(BigDecimal x, BigDecimal[] params,
					BigDecimalMath bdm) {
				BigDecimal a = params[0];
				BigDecimal b = params[1];
				BigDecimal c = params[2];
				return bdm.sum(new BigDecimal[]{
						x.pow(3),
						bdm.mult(a, x.pow(2)),
						bdm.mult(b, x),
						c
					});
			}
		}, new BDFunction() {
			public BigDecimal compute(BigDecimal x, BigDecimal[] params,
					BigDecimalMath bdm) {
				BigDecimal a = params[0];
				BigDecimal b = params[1];
				return bdm.sum(new BigDecimal[]{
						bdm.mult(3, x.pow(2)),
						bdm.mult(2, bdm.mult(a, x)),
						b
					});
			}
		}, root_start, new BigDecimal[]{a, b, c});
		return ret;
	}
	
	private void debugPrint(String s) {
		//System.out.println(s);
	}
}
