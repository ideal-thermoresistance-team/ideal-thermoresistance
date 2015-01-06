package ideal_thermoresistance.gui;

import ideal_thermoresistance.functions.ElectronMobility;
import ideal_thermoresistance.functions.ElectronsConcentration;
import ideal_thermoresistance.functions.FermiLevel;
import ideal_thermoresistance.functions.Function;
import ideal_thermoresistance.functions.HoleMobility;
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
	private static final Function[] functions = {
		new ElectronsConcentration(),
		new Resistance(),
		new FermiLevel(),
		new ElectronMobility(),
		new HoleMobility()};
	private JPanel[] graph_panel = new JPanel[functions.length];
	Parameters params;
	
	public GraphPane(Parameters params) {
		this.params = params;
		setTitle("Graph");
		tp = new JTabbedPane();
		getContentPane().add(tp);
		for (int i = 0; i < functions.length; i++)
		{
			graph_panel[i] = initPanel();
			tp.addTab(functions[i].getLongName(), graph_panel[i]);
		}
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
		for (int i = 0; i < functions.length; i++)
		{
			graph_panel[i].removeAll();
			graph_panel[i].add(makePanel(functions[i], functions[i].getName()));
		}
	}
	
	private ChartPanel makePanel(Function func, String str) {
		XYSeries series;
		String chartStr = str + " = " + str + "(T)", strT = "T", strFunc = str;
		
	    if (params.getBoolean(BooleanParameterName.reverseT)) {
	    	strT = "1/T";
    		chartStr = str + "(1/T)";
    		series = new XYSeries(chartStr); 
    		for(double i = params.getDouble(DoubleParameterName.T2); i >= params.getDouble(DoubleParameterName.T1); i-=5){
    			if (params.getBoolean(BooleanParameterName.logScale))
    				series.add(1/i, Math.log10(func.compute(params, i)));
    			else
    				series.add(1/i, func.compute(params, i));
    		}
	    } else {
	    	chartStr = str + "(T)";
	    	series = new XYSeries(chartStr);
			for(double i = params.getDouble(DoubleParameterName.T1); i < params.getDouble(DoubleParameterName.T2); i+=5){
				if (params.getBoolean(BooleanParameterName.logScale))
					series.add(i, Math.log10(func.compute(params, i)));
				else
					series.add(i, func.compute(params, i));
	    	}
	    }
	    chartStr += ", " + func.getUnits();
	    
	    XYDataset xyDataset = new XYSeriesCollection(series);
	    JFreeChart chart = ChartFactory
	        .createXYLineChart(chartStr, strT, strFunc, xyDataset, PlotOrientation.VERTICAL, true, true, true);
	    XYPlot plot = (XYPlot) chart.getPlot();
	    
	    double len = series.getMaxY() - series.getMinY();
	    
//	    if (params.getBoolean(BooleanParameterName.logScale)) {
//	    	LogarithmicAxis ax = new LogarithmicAxis(str);
//	    	if (len > 0)
//	    		ax.setRange(series.getMinY(), series.getMaxY());
//	    	ax.setExpTickLabelsFlag(true);
//	    	ax.setTickUnit(new NumberTickUnit(len / 15, new DecimalFormat("0.###E0")));
//	    	plot.setRangeAxis(ax);
//	    }
//	    else {
	    	NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
		    // Due to the unexpected behavior of JFreeChart (not showing the Y axis values for too big numbers),
		    // the number of ticks on the vertical axis is set to 15.
	    	if (len > 0) {
	    		yAxis.setRange(series.getMinY(), series.getMaxY());
	    		yAxis.setTickUnit(new NumberTickUnit(len / 15, new DecimalFormat("0.###E0")));
	    	}
//	    }
	    
	    ChartPanel panel = new ChartPanel(chart);
	    
	    return panel;
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		recompute();
		setVisible(true);
	}
}
