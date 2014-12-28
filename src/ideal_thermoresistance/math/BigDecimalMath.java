package ideal_thermoresistance.math;

import ideal_thermoresistance.math.ZeroFinder.BDFunction;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class BigDecimalMath {
	
//	private static final MathContext DEFAULT_MATH_CONTEXT = MathContext.UNLIMITED;
	private static final MathContext DEFAULT_MATH_CONTEXT = MathContext.DECIMAL128;

	public BigDecimalMath () {
	}
	
	public BigDecimal toBD(double d) {
		return new BigDecimal(d, DEFAULT_MATH_CONTEXT);
	}
	
	public BigDecimal pow(BigDecimal d, double power) {
		return new BigDecimal(Math.pow(d.doubleValue(), power), MathContext.UNLIMITED);
	}
	
	public BigDecimal div(BigDecimal d1, BigDecimal d2) {
		if (d2.signum() == 0) return null;
		return d1.divide(d2, 128, RoundingMode.HALF_UP);
	}
	public BigDecimal div(BigDecimal d1, double d2) {
		return div(d1, toBD(d2));
	}
	public BigDecimal add(BigDecimal d1, BigDecimal d2) {
		return d1.add(d2);
	}
	public BigDecimal sum(BigDecimal[] d) {
		BigDecimal ret = d[0];
		for (int i = 1; i < d.length; ++i) {
			ret = ret.add(d[i]);
		}
		return ret;
	}
	public BigDecimal add(double d1, BigDecimal d2) {
		return toBD(d1).add(d2);
	}
	public BigDecimal sub(BigDecimal d1, BigDecimal d2) {
		return d1.subtract(d2);
	}
	public BigDecimal mult(BigDecimal d1, BigDecimal d2) {
		return d1.multiply(d2);
	}
	public BigDecimal mult(double d1, BigDecimal d2) {
		return mult(toBD(d1), d2);
	}
	
	public BigDecimal sqrt(BigDecimal d) {
		if (d.signum() < 0) return null;
		if (d.signum() == 0) return toBD(0);
		
		BigDecimal ret = ZeroFinder.iterationsMethod(new BDFunction() {
			public BigDecimal compute(BigDecimal x, BigDecimal[] params,
					BigDecimalMath bdm) {
				BigDecimal temp = bdm.div(params[0], x);
				if (temp != null)
					return bdm.div( bdm.add(x, temp), 2 );
				else return null;
			}
		}, div(d, 2), new BigDecimal[]{d});
		
		return ret;
	}
	public BigDecimal cubicRoot(BigDecimal d) {
		BigDecimal ret = ZeroFinder.iterationsMethod(new BDFunction() {
			public BigDecimal compute(BigDecimal x, BigDecimal[] params,
					BigDecimalMath bdm) {
				BigDecimal temp = bdm.div(params[0], x.pow(2));
				return bdm.div( bdm.add(bdm.mult(2, x), temp), 3 );
			}
		}, div(d, 3), new BigDecimal[]{d});
		
		return ret;
	}

	public BigDecimal pow(double a, int power) {
		return toBD(a).pow(power);
	}
}
