package ideal_thermoresistance.math;

import java.math.BigDecimal;

public class ZeroFinder {
	private static BigDecimalMath bdm = new BigDecimalMath();
	
	public interface DoubleFunction {
		public abstract double compute(double x, double[] params);
	}
	public interface BDFunction {
		public abstract BigDecimal compute(BigDecimal x, BigDecimal[] params, BigDecimalMath bdm);
	}
	
	public static double newtonsMethod(DoubleFunction func, DoubleFunction deriv, double root_start, double[] params) {
		double x = root_start;
		
		double func_val, deriv_val;
		func_val = deriv_val = 0;
		int iter_count = 150;
		
		/* Searching for root of the electroneutrality equation using Newton's algorithm */
		
		do {
			func_val = func.compute(x, params);
			deriv_val = deriv.compute(x, params);
			
			x = x - func_val/deriv_val;
		} while ((Math.abs(func_val) > 1e-50 || Math.abs(func_val/deriv_val) > 1e-50) && --iter_count >= 0);
		
		debugPrint("Last error: " + func_val);
		
		return x;
	}
	
	public static BigDecimal newtonsMethod(BDFunction func, BDFunction deriv, BigDecimal root_start, BigDecimal[] params) {
		BigDecimal x = root_start;
		
		BigDecimal func_val, deriv_val;
		func_val = deriv_val = bdm.toBD(0);
		int iter_count = 300;
		
		BigDecimal shift = null;
		
		/* Searching for root of the electroneutrality equation using Newton's algorithm */
		
		do {
			func_val = func.compute(x, params, bdm);
			deriv_val = deriv.compute(x, params, bdm);
			shift = bdm.div(func_val, deriv_val);
			
			if (deriv_val.signum() == 0) break;
			
			x = bdm.sub( x, shift );
		} while ((func_val.abs().doubleValue() > 1e-90 || shift.abs().doubleValue() > 1e-90) && --iter_count >= 0);
		
		debugPrint("=== Finished newton's method ===");
		debugPrint("Last error: " + func_val.doubleValue());
		debugPrint("Iter count: " + (300 - iter_count));
		
		return x;
	}
	
	public static double iterationsMethod(DoubleFunction phi, double root_start, double[] params) {
		double x = root_start;
		double x_prev = x;
		
		int iter_count = 10;
		
		do {
			x_prev = x;
			x = phi.compute(x, params);
		} while ((Math.abs(x - x_prev) > 1e-4) && --iter_count >= 0);
		
		return x;
	}
	
	public static BigDecimal iterationsMethod(BDFunction phi, BigDecimal root_start, BigDecimal params[]) {
		BigDecimal x = root_start;
		BigDecimal x_prev = x;
		
		int iter_count = 900;
		
		do {
			if (x.signum() == 0)
				break;
			x_prev = x;
			x = phi.compute(x, params, bdm);
			if (x == null) {
				x = x_prev;
				break;
			}
		} while ((bdm.sub(x, x_prev).abs().doubleValue() > 1e-120) && --iter_count >= 0);
		
		debugPrint("=== Finished iterations method ===");
		debugPrint("Last error: " + bdm.sub(x.pow(2), params[0]).doubleValue());
		debugPrint("Required " + (900 - iter_count) + " iterations.");
		
		return x;
	}
	
	private static void debugPrint(String s) {
//		System.out.println(s);
	}
}
