package ideal_thermoresistance;

import javax.swing.JFrame;

import ideal_thermoresistance.gui.InputBar;
import ideal_thermoresistance.parameters.BooleanParameterName;
import ideal_thermoresistance.parameters.DoubleParameterName;
import ideal_thermoresistance.parameters.Parameters;

public class Main extends JFrame{

	public Main()
	{
		Parameters params = new Parameters();
		params.setDouble(DoubleParameterName.Eg, 1);
		params.setDouble(DoubleParameterName.Ed1, 1);
		params.setDouble(DoubleParameterName.Ed2, 1);
		params.setDouble(DoubleParameterName.Nd1, 1);
		params.setDouble(DoubleParameterName.Nd2, 1);
		params.setDouble(DoubleParameterName.me, 1);
		params.setDouble(DoubleParameterName.mh, 1);
		
		params.setDouble(DoubleParameterName.T1, 1);
		params.setDouble(DoubleParameterName.T2, 1);
		
		params.setBoolean(BooleanParameterName.logScale, false);
		params.setBoolean(BooleanParameterName.reverseT, false);
		
		add(new InputBar(params));
		pack();
	}
	
	public static void main(String[] args) {
		Main frame = new Main();
		frame.setVisible(true);
	}

}
