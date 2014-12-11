package ideal_thermoresistance;

import javax.swing.JFrame;

import ideal_thermoresistance.functions.ElectronsConcentration;
import ideal_thermoresistance.gui.InputBar;
import ideal_thermoresistance.parameters.BooleanParameterName;
import ideal_thermoresistance.parameters.DoubleParameterName;
import ideal_thermoresistance.parameters.Parameters;

public class Main extends JFrame{

	public Main()
	{
		double m0 = 9.1e-28;
		
		Parameters params = new Parameters();
		// NOTE: The real value for Eg is 1.21 * 1.6e-12
		params.setDouble(DoubleParameterName.Eg, 1.21 * 1.6e-12);
		params.setDouble(DoubleParameterName.Ed1, 1e-13);
		params.setDouble(DoubleParameterName.Ed2, 1e-13);
		params.setDouble(DoubleParameterName.Nd1, 1.5e10);
		params.setDouble(DoubleParameterName.Nd2, 2.4e10);
		params.setDouble(DoubleParameterName.me, 1.08 * m0);
		params.setDouble(DoubleParameterName.mh, 0.56 * m0);
		
		params.setDouble(DoubleParameterName.T1, 200);
		params.setDouble(DoubleParameterName.T2, 400);
		
		params.setDouble(DoubleParameterName.Cn, 1);
		params.setDouble(DoubleParameterName.Cp, 1);
		params.setDouble(DoubleParameterName.T0n, 1);
		params.setDouble(DoubleParameterName.T0p, 1);
		
		params.setBoolean(BooleanParameterName.logScale, false);
		params.setBoolean(BooleanParameterName.reverseT, false);
		
		add(new InputBar(params));
		pack();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	public static void main(String[] args) {
		Main frame = new Main();
		frame.setVisible(true);
	}

}
