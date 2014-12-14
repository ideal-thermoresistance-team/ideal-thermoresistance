package ideal_thermoresistance.gui;

import ideal_thermoresistance.functions.ElectronsConcentration;
import ideal_thermoresistance.functions.FermiLevel;
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
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
 
public class GraphPane extends JFrame implements Observer {
	private static final long serialVersionUID = 1L;

	public static final int defaultHeight = 300, defaultWidth = 400;
	
	private JTabbedPane tp;
	private JPanel pElectronsConcentration, pResistance, pFermi;
	Parameters params;
	
	public GraphPane(Parameters params) {
		this.params = params;
		setTitle("Graph");
		tp = new JTabbedPane();
		getContentPane().add(tp);
		pElectronsConcentration = initPanel();
		pResistance  = initPanel();
		pFermi = initPanel();
		tp.addTab("EC", pElectronsConcentration);
		tp.addTab("R", pResistance);
		tp.addTab("F", pFermi);
		setVisible(false);
		setSize(700, 500);
	}
	
	private JPanel initPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout());
		return panel;
	}

	private void recompute() {
		pResistance.removeAll();
		pElectronsConcentration.removeAll();
		pFermi.removeAll();
		pElectronsConcentration.add(makePanel(new ElectronsConcentration(), "N"));
		pResistance.add(makePanel(new Resistance(), "R"));
	    pFermi.add(makePanel(new FermiLevel(), "F"));
	}
	
	private ChartPanel makePanel(Function func, String str) {
		XYSeries series;
		String chartStr = str + " = " + str + "(T)", strT = "T", strFunc = str;
	    if (params.getBoolean(BooleanParameterName.reverseT)) {
	    	strT = "1/T";
	    	if (params.getBoolean(BooleanParameterName.logScale)) {
	    		chartStr = "log(" + str + "(1/T))"; strFunc = "log(" + str + ")";
	    		series = new XYSeries(chartStr); 
	    		for(double i = params.getDouble(DoubleParameterName.T2); i >= params.getDouble(DoubleParameterName.T1); i-=10){
	    			series.add(1/i, Math.log(func.compute(params, i)));
	    		}
	    	} else {
	    		chartStr = str + "(1/T)";
	    		series = new XYSeries(chartStr); 
	    		for(double i = params.getDouble(DoubleParameterName.T2); i >= params.getDouble(DoubleParameterName.T1); i-=10){
	    			series.add(1/i, func.compute(params, i));
	    		}
	    	}
	    } else {
	    	if (params.getBoolean(BooleanParameterName.logScale)) {
	    		chartStr = "log(" + str + "(T))"; strFunc = "log(" + str + ")";
	    		series = new XYSeries(str + "(T)"); 
	    		for(double i = params.getDouble(DoubleParameterName.T1); i < params.getDouble(DoubleParameterName.T2); i+=10){
		    		series.add(i, Math.log(func.compute(params, i)));
	    		}
		    } else {
		    	chartStr = str + "(T)";
		    	series = new XYSeries(chartStr);
	    		for(double i = params.getDouble(DoubleParameterName.T1); i < params.getDouble(DoubleParameterName.T2); i+=10){
		    		series.add(i, func.compute(params, i));
		    	}
	    	}
	    }
	    
	    XYDataset xyDataset = new XYSeriesCollection(series);
	    JFreeChart chart = ChartFactory
	        .createXYLineChart(chartStr, strT, strFunc, xyDataset, PlotOrientation.VERTICAL, true, true, true);
	    XYPlot plot = (XYPlot) chart.getPlot();
	    NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
	    yAxis.setRange(series.getMinY(), series.getMaxY());
	    // FIXME: Due to the unexpected behavior of JFreeChart (not showing the Y axis values for too big numbers),
	    // the number of ticks on the vertical axis is set to 15.
	    yAxis.setTickUnit(new NumberTickUnit(yAxis.getRange().getLength() / 15, new DecimalFormat("##0.#####E0")));
	    
	    ChartPanel panel = new ChartPanel(chart);
	    
	    return panel;
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		recompute();
		setVisible(true);
	}
}
