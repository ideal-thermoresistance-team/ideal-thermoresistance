package ideal_thermoresistance.gui;

import ideal_thermoresistance.functions.ElectronsConcentration;
import ideal_thermoresistance.functions.Function;
import ideal_thermoresistance.functions.Resistance;
import ideal_thermoresistance.parameters.BooleanParameterName;
import ideal_thermoresistance.parameters.DoubleParameterName;
import ideal_thermoresistance.parameters.Parameters;

import java.awt.GridLayout;
import java.text.DecimalFormat;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
 
public class GraphPane extends JFrame implements Observer {
	private static final long serialVersionUID = 1L;

	public static final int defaultHeight = 300, defaultWidth = 400;
	
	private JTabbedPane tp;
	private JPanel pElectronsConcentration, pResistance;//, pFermi;
	Parameters params;
	
	public GraphPane(Parameters params) {
		this.params = params;
		setTitle("Graph");
		tp = new JTabbedPane();
		getContentPane().add(tp);
		pElectronsConcentration = initPanel();
		pResistance  = initPanel();
		//pFermi = initPanel();
		tp.addTab("Electrons concentration", pElectronsConcentration);
		tp.addTab("Resistivity", pResistance);
		//tp.addTab("F", pFermi);
		setVisible(false);
		
		setSize(500, 500);
		setLocation(400, 0);
	}
	
	private JPanel initPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout());
		return panel;
	}

	private void recompute() {
		pResistance.removeAll();
		pElectronsConcentration.removeAll();
		//pFermi.removeAll();
		pElectronsConcentration.add(makePanel(new ElectronsConcentration(), "N"));
		pResistance.add(makePanel(new Resistance(), "ρ"));
	    //pFermi.add(makePanel(new FermiLevel(), "F"));
	}
	
	private ChartPanel makePanel(Function func, String str) {
		XYSeries series;
		String chartStr = str + " = " + str + "(T)", strT = "T", strFunc = str;
		
	    if (params.getBoolean(BooleanParameterName.reverseT)) {
	    	strT = "1/T";
    		chartStr = str + "(1/T)";
    		series = new XYSeries(chartStr); 
    		for(double i = params.getDouble(DoubleParameterName.T2); i >= params.getDouble(DoubleParameterName.T1); i-=10){
    			series.add(1/i, func.compute(params, i));
    		}
	    } else {
	    	chartStr = str + "(T)";
	    	series = new XYSeries(chartStr);
			for(double i = params.getDouble(DoubleParameterName.T1); i < params.getDouble(DoubleParameterName.T2); i+=10){
	    		series.add(i, func.compute(params, i));
	    	}
	    }
	    chartStr += ", " + func.getUnits();
	    
	    XYDataset xyDataset = new XYSeriesCollection(series);
	    JFreeChart chart = ChartFactory
	        .createXYLineChart(chartStr, strT, strFunc, xyDataset, PlotOrientation.VERTICAL, true, true, true);
	    XYPlot plot = (XYPlot) chart.getPlot();
	    
	    if (params.getBoolean(BooleanParameterName.logScale) && plot.getRangeAxis().getRange().getLowerBound() >= 0) {
	    	LogarithmicAxis ax = new LogarithmicAxis(str);
	    	ax.setNumberFormatOverride(new DecimalFormat("0.###E0"));
	    	plot.setRangeAxis(ax);
	    }
	    else {
	    	NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
		    // FIXME: Due to the unexpected behavior of JFreeChart (not showing the Y axis values for too big numbers),
		    // the number of ticks on the vertical axis is set to 15.
		    yAxis.setTickUnit(new NumberTickUnit(yAxis.getRange().getLength() / 15, new DecimalFormat("0.###E0")));
	    }
	    
	    ChartPanel panel = new ChartPanel(chart);
	    
	    return panel;
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		recompute();
		setVisible(true);
	}
}
