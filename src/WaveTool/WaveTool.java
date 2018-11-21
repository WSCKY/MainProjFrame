package WaveTool;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.general.SeriesChangeEvent;
import org.jfree.data.general.SeriesChangeListener;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class WaveTool extends ChartPanel implements SeriesChangeListener {
	private static final long serialVersionUID = 1L;

	private static JFreeChart freeChart = null;
	private static XYSeriesCollection DataSet = null;
	private static ArrayList<mySeries> SeriesList = new ArrayList<mySeries>();

	public WaveTool(String Title) {
		super(createChart(Title));
	}

	private static JFreeChart createChart(String title) {
		DataSet = new XYSeriesCollection();
		freeChart = ChartFactory.createXYLineChart(title, "Time", "", DataSet, PlotOrientation.VERTICAL, true, true, false);
		XYPlot xyplot = freeChart.getXYPlot();
		ValueAxis valueaxis = xyplot.getDomainAxis();
		valueaxis.setAutoRange(true);
//		valueaxis.setInverted(true);
		valueaxis.setFixedAutoRange(500);
		valueaxis.setTickLabelFont(new Font("Courier New", Font.BOLD, 12));
		valueaxis.setLabelFont(new Font("Courier New", Font.BOLD, 14));
		xyplot.getRenderer().setSeriesStroke(1, new BasicStroke(3.0f));
		valueaxis = xyplot.getRangeAxis();
		valueaxis.setAutoRange(true);
		NumberAxis numAxis = ((NumberAxis)valueaxis);
		numAxis.setAutoRangeIncludesZero(false);
		freeChart.getLegend().setVisible(false);
		return freeChart;
	}

	public void addNewSeries(String SeriesName) {
		mySeries series = new mySeries(SeriesName);
		series.addChangeListener(this);
		SeriesList.add(series);
		DataSet.addSeries(series);
	}
	public void removeSeries(int index) {
		if(SeriesList.size() > index) {
			DataSet.removeSeries(SeriesList.get(index));
			SeriesList.remove(index);
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
		SeriesList.clear();
	}
	public int getSeriesCount() {
		return SeriesList.size();
	}
	public void addDataToSeries(int index, double y) {
		if(SeriesList.size() > index) {
			mySeries s = SeriesList.get(index);
			s.add(s.indexer ++, y);
		}
	}
	public void setAutoRange(boolean auto) {
		freeChart.getXYPlot().getRangeAxis().setAutoRange(auto);
	}
	public void setLockZeroPoint(boolean flag) {
		NumberAxis numAxis = ((NumberAxis)freeChart.getXYPlot().getRangeAxis());
		numAxis.setAutoRangeIncludesZero(flag);
	}
	public void setDataPoints(int n) {
		freeChart.getXYPlot().getDomainAxis().setFixedAutoRange(n);
	}
	public void setAutoRangeMinimumSize(double size) {
		freeChart.getXYPlot().getRangeAxis().setAutoRangeMinimumSize(size);
	}
	public void setSeriesColor(int index, Color c) {
		freeChart.getXYPlot().getRenderer().setSeriesPaint(index, c);
	}
	public void setSeriesStroke(int index, float width) {
		freeChart.getXYPlot().getRenderer().setSeriesStroke(index, new BasicStroke(width));
	}

	private class mySeries extends XYSeries {
		private static final long serialVersionUID = 1L;
		int indexer = 0;
		public mySeries(Comparable<String> key) {
			super(key);
			// TODO Auto-generated constructor stub
		}
	}

	@Override
	public void seriesChanged(SeriesChangeEvent arg0) {
		// TODO Auto-generated method stub
	}
}
