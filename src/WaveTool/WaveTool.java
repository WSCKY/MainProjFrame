package WaveTool;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

public class WaveTool extends ChartPanel {
	private static final long serialVersionUID = 1L;

	private static JFreeChart freeChart = null;
	private static TimeSeriesCollection DataSet = null;
	private static ArrayList<TimeSeries> TimeSeriesList = new ArrayList<TimeSeries>();
	public WaveTool(String Title) {
		super(createChart(Title));
	}

	private static JFreeChart createChart(String title) {
		DataSet = new TimeSeriesCollection();
		freeChart = ChartFactory.createTimeSeriesChart(title, "Time", "", DataSet, true, true, false);
		XYPlot xyplot = freeChart.getXYPlot();
		ValueAxis valueaxis = xyplot.getDomainAxis();
		valueaxis.setAutoRange(true);
//		valueaxis.setInverted(true);
		valueaxis.setFixedAutoRange(5000);
		valueaxis.setTickLabelFont(new Font("Courier New", Font.BOLD, 12));
		valueaxis.setLabelFont(new Font("Courier New", Font.BOLD, 14));
		xyplot.getRenderer().setSeriesStroke(1, new BasicStroke(3.0f));
		DateAxis dateaxis = ((DateAxis)valueaxis);
		dateaxis.setDateFormatOverride(new SimpleDateFormat("HH-mm-ss"));
		valueaxis = xyplot.getRangeAxis();
		freeChart.getLegend().setVisible(false);
		return freeChart;
	}

	public void addNewSeries(String SeriesName) {
		TimeSeries series = new TimeSeries(SeriesName);
		TimeSeriesList.add(series);
		DataSet.addSeries(series);
	}
	public void removeSeries(int index) {
		if(TimeSeriesList.size() > index) {
			DataSet.removeSeries(TimeSeriesList.get(index));
			TimeSeriesList.remove(index);
		}
	}
	public void setTitle(String title) {
		freeChart.setTitle(title);
	}
	public void setValueAxisLabel(String label) {
		freeChart.getXYPlot().getRangeAxis().setLabel(label);
	}
	public void removeAllSeries() {
		DataSet.removeAllSeries();
		TimeSeriesList.clear();
	}
	public int getSeriesNumber() {
		return TimeSeriesList.size();
	}
	public void addDataToSeries(int index, double val) {
		if(TimeSeriesList.size() > index) {
			TimeSeriesList.get(index).add(new Millisecond(), val);
		}
	}
	public void setSeriesColor(int index, Color c) {
		freeChart.getXYPlot().getRenderer().setSeriesPaint(index, c);
	}
	public void setSeriesStroke(int index, float width) {
		freeChart.getXYPlot().getRenderer().setSeriesStroke(index, new BasicStroke(width));
	}
}
