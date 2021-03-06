package ideal_thermoresistance.gui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import ideal_thermoresistance.functions.ElectronsConcentration;
import ideal_thermoresistance.parameters.BooleanParameterName;
import ideal_thermoresistance.parameters.DoubleParameterEntry;
import ideal_thermoresistance.parameters.DoubleParameterName;
import ideal_thermoresistance.parameters.Parameters;
import ideal_thermoresistance.parameters.Unit;

public class InputBar extends JPanel implements ActionListener, Observer {
	private static final long serialVersionUID = 1L;
	private HashMap<DoubleParameterName, JFormattedTextField> doubleFields;
	private HashMap<DoubleParameterName, JComboBox<Unit>> doubleUnits;
	private HashMap<BooleanParameterName, JCheckBox> booleanFields;
	
	private Parameters params;
	public static final int defaultHeight = 300, defaultWidth = 400;
	private static DecimalFormat format;
	
	static{
		format = (DecimalFormat) NumberFormat.getInstance(Locale.ENGLISH); 
		format.applyPattern("0.#########E0");
	}
	
	private void createDoubleField(DoubleParameterName name, String label)
	{
		double value = params.getDouble(name);
		add(new JLabel(label));
		
		JFormattedTextField field = new JFormattedTextField(format);
		field.setValue(value);
		add(field);
		
		JComboBox<Unit> unit = new JComboBox<Unit>(name.units);
		unit.setSelectedIndex(params.getDoubleEntry(name).getUnit());
		add(unit);
		
		doubleFields.put(name, field);
		doubleUnits.put(name, unit);
	}
	
	private void createBooleanField(BooleanParameterName name, String label)
	{
		boolean value = params.getBoolean(name);
		JCheckBox result = new JCheckBox(label, value);
		add(result);
		booleanFields.put(name, result);
	}
	
	private double getDoubleWithoutConversion(DoubleParameterName name)
	{
		return ((Number)doubleFields.get(name).getValue()).doubleValue();
	}
	
	private Unit getUnit(DoubleParameterName name)
	{
		return ((Unit)doubleUnits.get(name).getSelectedItem());
	}
	
	private double getDouble(DoubleParameterName name)
	{
		return getDoubleWithoutConversion(name) * 
				getUnit(name).value();
	}
	
	private void updateEntry(DoubleParameterName name)
	{
		params.setDouble(name, getDoubleWithoutConversion(name), getUnit(name).getPos());
	}
	
	private boolean getBoolean(BooleanParameterName name)
	{
		return booleanFields.get(name).getSelectedObjects() != null;
	}
	
	private JButton createButton(String label, String action)
	{
		JButton result = new JButton(label);
		result.setActionCommand(action);
		add(result);
		result.addActionListener(this);
		return result;
	}
	
	private void error(String msg)
	{
		JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	public InputBar(Parameters params)
	{
		doubleFields = new HashMap<>();
		doubleUnits = new HashMap<>();
		booleanFields = new HashMap<>();
		
		this.params = params;
		params.addObserver(this);
		setLayout(new GridLayout(0, 3));
		setPreferredSize(new Dimension(defaultWidth, defaultHeight));
		createDoubleField(DoubleParameterName.Eg, "Energy gap");
		createDoubleField(DoubleParameterName.me, "Effective electron mass");
		createDoubleField(DoubleParameterName.mh, "Effective hole mass");
		createDoubleField(DoubleParameterName.Ed1, "Donor 1 energy");
		createDoubleField(DoubleParameterName.Nd1, "Donor 1 concentration");
		createDoubleField(DoubleParameterName.Ed2, "Donor 2 energy");
		createDoubleField(DoubleParameterName.Nd2, "Donor 2 concentration");
		createDoubleField(DoubleParameterName.T1, "Starting temperature");
		createDoubleField(DoubleParameterName.T2, "Ending temperature");
		createDoubleField(DoubleParameterName.Cn, "Cn");
		createDoubleField(DoubleParameterName.Cp, "Cp");
		createDoubleField(DoubleParameterName.T0n, "T0n");
		createDoubleField(DoubleParameterName.T0p, "T0p");
		createBooleanField(BooleanParameterName.reverseT, "1/T");
		createBooleanField(BooleanParameterName.logScale, "Logarithmic scale");
		createButton("Apply", "apply");
	}
	
	public Parameters getParameters() {
		return params;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("apply"))
		{
			double Eg = getDouble(DoubleParameterName.Eg);
			double me = getDouble(DoubleParameterName.me);
			double mh = getDouble(DoubleParameterName.mh);
			double Ed1 = getDouble(DoubleParameterName.Ed1);
			double Ed2 = getDouble(DoubleParameterName.Ed2);
			double Nd1 = getDouble(DoubleParameterName.Nd1);
			double Nd2 = getDouble(DoubleParameterName.Nd2);
			double T1 = getDouble(DoubleParameterName.T1);
			double T2 = getDouble(DoubleParameterName.T2);
			double Cn = getDouble(DoubleParameterName.Cn);
			double Cp = getDouble(DoubleParameterName.Cp);
			double T0n = getDouble(DoubleParameterName.T0n);
			double T0p = getDouble(DoubleParameterName.T0p);
			
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
			if (T1 < 0 || T2 < 0 || T0n < 0 || T0p < 0)
			{
				error("Temperatures should be bigger than 0K");
				return;
			}
			if (T1 >= T2)
			{
				error("Starting temperature should be less than ending temperature");
				return;
			}
			
			for (DoubleParameterName name : doubleFields.keySet())
			{
				updateEntry(name);
			}
			
			params.setBoolean(BooleanParameterName.logScale, getBoolean(BooleanParameterName.logScale));
			params.setBoolean(BooleanParameterName.reverseT, getBoolean(BooleanParameterName.reverseT));
			params.update();
		}
		
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		for (DoubleParameterName name : doubleFields.keySet())
		{
			DoubleParameterEntry d = params.getDoubleEntry(name);
			doubleFields.get(name).setValue(d.getValue());
			doubleUnits.get(name).setSelectedIndex(d.getUnit());
		}
	}
}
