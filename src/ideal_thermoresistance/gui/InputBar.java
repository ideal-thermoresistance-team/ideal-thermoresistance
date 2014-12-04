package ideal_thermoresistance.gui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import ideal_thermoresistance.parameters.BooleanParameterName;
import ideal_thermoresistance.parameters.DoubleParameterName;
import ideal_thermoresistance.parameters.Parameters;

public class InputBar extends JPanel implements ActionListener {
	private JFormattedTextField fEg, fme, fmh, fT1, fT2, fEd1, fEd2, fNd1, fNd2;
	private JCheckBox flogScale, freverseT;
	private Parameters params;
	public static final int defaultHeight = 400, defaultWidth = 400;
	
	private JFormattedTextField createDoubleField(String label, double value)
	{
		add(new JLabel(label));
		JFormattedTextField result = new JFormattedTextField(new Double(value));
		add(result);
		return result;
	}
	
	private JCheckBox createBooleanField(String label, boolean value)
	{
		JCheckBox result = new JCheckBox(label, value);
		add(result);
		return result;
	}
	
	private double getDouble(JFormattedTextField f)
	{
		return ((Number)f.getValue()).doubleValue();
	}
	
	private JButton createButton(String label, String action)
	{
		JButton result = new JButton(label);
		result.setActionCommand(action);
		add(result);
		return result;
	}
	
	private void error(String msg)
	{
		JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	public InputBar(Parameters params)
	{
		this.params = params;
		setLayout(new GridLayout(0, 2));
		setPreferredSize(new Dimension(defaultWidth, defaultHeight));
		fEg = createDoubleField("Energy gap", params.getDouble(DoubleParameterName.Eg));
		fme = createDoubleField("Effective electron mass", params.getDouble(DoubleParameterName.me));
		fmh = createDoubleField("Effective hole mass", params.getDouble(DoubleParameterName.mh));
		fEd1 = createDoubleField("Donor 1 energy", params.getDouble(DoubleParameterName.Ed1));
		fNd1 = createDoubleField("Donor 1 concentration", params.getDouble(DoubleParameterName.Nd1));
		fEd2 = createDoubleField("Donor 2 energy", params.getDouble(DoubleParameterName.Ed2));
		fNd2 = createDoubleField("Donor 2 concentration", params.getDouble(DoubleParameterName.Nd2));
		fT1 = createDoubleField("Starting temperature", params.getDouble(DoubleParameterName.T1));
		fT2 = createDoubleField("Ending temperature", params.getDouble(DoubleParameterName.T2));
		freverseT = createBooleanField("1/T", params.getBoolean(BooleanParameterName.reverseT));
		flogScale = createBooleanField("Logarithmic scale", params.getBoolean(BooleanParameterName.logScale));
		createButton("Apply", "apply");
	}
	
	public Parameters getParameters() {
		return params;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("apply"))
		{
			double Eg = getDouble(fEg);
			double me = getDouble(fEg);
			double mh = getDouble(fEg);
			double Ed1 = getDouble(fEg);
			double Ed2 = getDouble(fEg);
			double Nd1 = getDouble(fEg);
			double Nd2 = getDouble(fEg);
			double T1 = getDouble(fEg);
			double T2 = getDouble(fEg);
			
			if (Eg <= 0)
			{
				error("Energy gap should be bigger than 0");
				return;
			}
			if (me <= 0 || mh <= 0)
			{
				error("Masses should be bigger than 0");
				return;
			}
			if (Ed1 < 0 || Ed2 < 0 || Ed1 > Eg || Ed2 > Eg)
			{
				error("Donor energies should be between 0 and energy gap");
				return;
			}
			if (Nd1 < 0 || Nd2 < 0 )
			{
				error("Donor concentrations should be at least 0");
				return;
			}
			if (T1 < 0 || T2 < 0)
			{
				error("Temperatures should be bigger than 0K");
				return;
			}
			if (T1 >= T2)
			{
				error("Starting temperature should be less than ending temperature");
				return;
			}
			params.setDouble(DoubleParameterName.Eg, Eg);
			params.setDouble(DoubleParameterName.Ed1, Ed1);
			params.setDouble(DoubleParameterName.Ed2, Ed2);
			params.setDouble(DoubleParameterName.Nd1, Nd1);
			params.setDouble(DoubleParameterName.Nd2, Nd2);
			params.setDouble(DoubleParameterName.me, me);
			params.setDouble(DoubleParameterName.mh, mh);
			params.setDouble(DoubleParameterName.T1, T1);
			params.setDouble(DoubleParameterName.T2, T2);
			params.setBoolean(BooleanParameterName.logScale, flogScale.getSelectedObjects() != null);
			params.setBoolean(BooleanParameterName.reverseT, freverseT.getSelectedObjects() != null);
			params.update();
			
		}
		
	}
}
