package ideal_thermoresistance;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import ideal_thermoresistance.gui.GraphPane;
import ideal_thermoresistance.gui.InputBar;
import ideal_thermoresistance.parameters.BooleanParameterName;
import ideal_thermoresistance.parameters.DoubleParameterEntry;
import ideal_thermoresistance.parameters.DoubleParameterName;
import ideal_thermoresistance.parameters.Parameters;
import ideal_thermoresistance.parameters.Unit;


public class Main extends JFrame implements ActionListener{
	private static final long serialVersionUID = 1L;
	private Parameters params;
	private GraphPane graph;
	private final double m0 = 9.1e-28;
	
	public Main()
	{
		JMenuBar bar = new JMenuBar();
		
		JMenu menu = new JMenu("File");
		bar.add(menu);
		
		JMenuItem item = new JMenuItem("Open");
		item.setActionCommand("open");
		item.addActionListener(this);
		menu.add(item);
		
		item = new JMenuItem("Save");
		item.setActionCommand("save");
		item.addActionListener(this);
		menu.add(item);
		
		menu = new JMenu("Materials");
		bar.add(menu);
		
		item = new JMenuItem("Silicon");
		item.setActionCommand("silicon");
		item.addActionListener(this);
		menu.add(item);
		
		item = new JMenuItem("Germanium");
		item.setActionCommand("germanium");
		item.addActionListener(this);
		menu.add(item);
		
		setJMenuBar(bar);
		


		
		params = new Parameters();
		
		
		params.setDouble(DoubleParameterName.Ed1, 0.1, Unit.eV.getPos());
		params.setDouble(DoubleParameterName.Ed2, 0.1, Unit.eV.getPos());
		params.setDouble(DoubleParameterName.Nd1, 1.5e10, Unit.reverse_cm3.getPos());
		params.setDouble(DoubleParameterName.Nd2, 2.4e10, Unit.reverse_cm3.getPos());
		params.setDouble(DoubleParameterName.T1, 200, Unit.K.getPos());
		params.setDouble(DoubleParameterName.T2, 400, Unit.K.getPos());
		
		params.setBoolean(BooleanParameterName.logScale, true);
		params.setBoolean(BooleanParameterName.reverseT, false);
		
		loadSiliconParams();
		
		add(new InputBar(params));
		graph = new GraphPane(params);
		params.addObserver(graph);
		
		params.update();
		pack();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	public static void main(String[] args) {
		Main frame = new Main();
		frame.setVisible(true);
	}
	
	public void loadSiliconParams()
	{
		params.setDouble(DoubleParameterName.Eg, 1.12, Unit.eV.getPos());
		
		params.setDouble(DoubleParameterName.me, 0.36, Unit.me.getPos());
		params.setDouble(DoubleParameterName.mh, 0.81, Unit.me.getPos());
				
		params.setDouble(DoubleParameterName.Cn, 1, Unit.constants.getPos());
		params.setDouble(DoubleParameterName.Cp, 1, Unit.constants.getPos());
		params.setDouble(DoubleParameterName.T0n, 1, Unit.K.getPos());
		params.setDouble(DoubleParameterName.T0p, 1, Unit.K.getPos());
		params.update();
	}
	
	public void loadGermaniumParams()
	{
		params.setDouble(DoubleParameterName.Eg, 0.661, Unit.eV.getPos());
		
		params.setDouble(DoubleParameterName.me, 0.22, Unit.me.getPos());
		params.setDouble(DoubleParameterName.mh, 0.34, Unit.me.getPos());
				
		params.setDouble(DoubleParameterName.Cn, 1, Unit.constants.getPos());
		params.setDouble(DoubleParameterName.Cp, 1, Unit.constants.getPos());
		params.setDouble(DoubleParameterName.T0n, 1, Unit.K.getPos());
		params.setDouble(DoubleParameterName.T0p, 1, Unit.K.getPos());
		params.update();
	}
	
	public void readDouble(Scanner scanner, 
			DoubleParameterName name)
	{
		double d = scanner.nextDouble();
		int i = scanner.nextInt();
		params.setDouble(name, d, i);
	}
	
	public void writeDouble(FileWriter writer, 
			DoubleParameterName name) throws IOException
	{
		DoubleParameterEntry e = params.getDoubleEntry(name);
		writer.write(Double.toString(e.getValue()));
		writer.write(' ');
		writer.write(Integer.toString(e.getUnit()));
		writer.write('\n');
	}
	
	public void openFile(File f)
	{
		Scanner scanner;
		try {
			scanner = new Scanner(f);
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
			return;
		}
		scanner.useLocale(Locale.ENGLISH);
		readDouble(scanner, DoubleParameterName.Eg);
		readDouble(scanner, DoubleParameterName.Ed1);
		readDouble(scanner, DoubleParameterName.Ed2);
		readDouble(scanner, DoubleParameterName.Nd1);
		readDouble(scanner, DoubleParameterName.Nd2);
		readDouble(scanner, DoubleParameterName.me);
		readDouble(scanner, DoubleParameterName.mh);
		
		readDouble(scanner, DoubleParameterName.Cn);
		readDouble(scanner, DoubleParameterName.Cp);
		readDouble(scanner, DoubleParameterName.T0n);
		readDouble(scanner, DoubleParameterName.T0p);
		
		readDouble(scanner, DoubleParameterName.T1);
		readDouble(scanner, DoubleParameterName.T2);
		
		params.setBoolean(BooleanParameterName.logScale, false);
		params.setBoolean(BooleanParameterName.reverseT, false);
		params.update();
	}
	
	public void open()
	{
		JFileChooser fc = new JFileChooser();
		int status = fc.showOpenDialog(this);
		if (status == JFileChooser.APPROVE_OPTION)
		{
			File f = fc.getSelectedFile();
			openFile(f);
		}
	}
	
	public void save()
	{
		try {
			JFileChooser fc = new JFileChooser();
			int status = fc.showSaveDialog(this);
			if (status == JFileChooser.APPROVE_OPTION)
			{
				File f = fc.getSelectedFile();
				FileWriter writer = new FileWriter(f);
				
				writeDouble(writer, DoubleParameterName.Eg);
				writeDouble(writer, DoubleParameterName.Ed1);
				writeDouble(writer, DoubleParameterName.Ed2);
				writeDouble(writer, DoubleParameterName.Nd1);
				writeDouble(writer, DoubleParameterName.Nd2);
				writeDouble(writer, DoubleParameterName.me);
				writeDouble(writer, DoubleParameterName.mh);
				
				writeDouble(writer, DoubleParameterName.Cn);
				writeDouble(writer, DoubleParameterName.Cp);
				writeDouble(writer, DoubleParameterName.T0n);
				writeDouble(writer, DoubleParameterName.T0p);
				
				writeDouble(writer, DoubleParameterName.T1);
				writeDouble(writer, DoubleParameterName.T2);
				writer.close();
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
		}
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("open"))
		{
			open();
		}
		else if (e.getActionCommand().equals("save"))
		{
			save();
		}
		else if (e.getActionCommand().equals("silicon"))
		{
			loadSiliconParams();
		}
		else if (e.getActionCommand().equals("germanium"))
		{
			loadGermaniumParams();
		}
	}

}
