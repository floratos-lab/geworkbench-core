package org.geworkbench.util.function.plot;

import com.klg.jclass.chart.ChartDataViewSeries;
import com.klg.jclass.chart.EventTrigger;
import com.klg.jclass.chart.JCAxis;
import com.klg.jclass.chart.beans.SimpleChart;
import com.klg.jclass.chart.data.JCDefaultDataSource;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;

public class ChartDialog_bak extends JDialog {

    SimpleChart chart = new SimpleChart();
    JCDefaultDataSource dataSource;
    JPanel pnlChart = new JPanel();
    BorderLayout borderLayout1 = new BorderLayout();
    BorderLayout borderLayout2 = new BorderLayout();

    public ChartDialog_bak(double[] xvalues, double[] yvalues) {
        super(new Frame(), true);
        double[][] xValues2d = new double[1][];
        xValues2d[0] = xvalues;
        double[][] yValues2d = new double[1][];
        yValues2d[0] = yvalues;

        String[] seriesLabels = {"Series1"};
        String dataSourceName = "";
        String[] pointLabels = new String[xvalues.length];

        java.text.NumberFormat nf = new java.text.DecimalFormat();
        nf.setMaximumFractionDigits(2);
        for (int i = 0; i < pointLabels.length; i++) {
            pointLabels[i] = nf.format(xvalues[i]);
        }
        initialize(xValues2d, yValues2d, pointLabels, seriesLabels, dataSourceName);
    }

    public ChartDialog_bak(double[] xValues, double[][] yValues) {
        super(new Frame(), true);
        double[][] xValues2d = new double[yValues.length][];
        for (int i = 0; i < xValues2d.length; i++) {
            xValues2d[i] = xValues;
        }

        String[] seriesLabels = new String[yValues.length];
        for (int i = 0; i < yValues.length; i++) {
            seriesLabels[i] = "Series " + 1;
        }
        String dataSourceName = "";
        String[] pointLabels = new String[xValues.length];

        java.text.NumberFormat nf = new java.text.DecimalFormat();
        nf.setMaximumFractionDigits(2);
        for (int i = 0; i < pointLabels.length; i++) {
            pointLabels[i] = nf.format(xValues[i]);
        }
        initialize(xValues2d, yValues, pointLabels, seriesLabels, dataSourceName);
    }

    public ChartDialog_bak(double[] xValues, double[][] yValues, String[] seriesLabels) {
        super(new Frame(), true);
        double[][] xValues2d = new double[yValues.length][];
        for (int i = 0; i < xValues2d.length; i++) {
            xValues2d[i] = xValues;
        }

        String dataSourceName = "";
        String[] pointLabels = new String[xValues.length];

        java.text.NumberFormat nf = new java.text.DecimalFormat();
        nf.setMaximumFractionDigits(2);
        for (int i = 0; i < pointLabels.length; i++) {
            pointLabels[i] = nf.format(xValues[i]);
        }
        chart.setLegendVisible(true);

        initialize(xValues2d, yValues, pointLabels, seriesLabels, dataSourceName);

    }


    public ChartDialog_bak(double[] xvalues, double[][] yvalues, String[] pointLabels, String[] seriesLabels, String dataSourceName) {
        super(new Frame(), true);
        double[][] xValues2d = new double[1][];
        xValues2d[0] = xvalues;
        initialize(xValues2d, yvalues, pointLabels, seriesLabels, dataSourceName);
    }

    public ChartDialog_bak(double[][] xvalues, double[][] yvalues, String[] pointLabels, String[] seriesLabels, String dataSourceName) {
        super(new Frame(), true);
        initialize(xvalues, yvalues, pointLabels, seriesLabels, dataSourceName);
    }

    public void initialize(double[][] xvalues, double[][] yvalues, String[] pointLabels, String[] seriesLabels, String dataSourceName) {
        this.dataSource = new JCDefaultDataSource(xvalues, yvalues, pointLabels, seriesLabels, dataSourceName);

        try {
            initializeChart();
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void initializeChart() {

        chart.setBackground(java.awt.Color.white);
        chart.setXAxisAnnotationMethod(com.klg.jclass.chart.JCAxis.POINT_LABELS);
        chart.setYAxisGridVisible(true);
        chart.setYAxisLogarithmic(false);
        chart.setLegendVisible(true);
        chart.getChartArea().getXAxis(0).setAnnotationRotation(JCAxis.ROTATE_270);

        chart.setTrigger(0, new EventTrigger(InputEvent.BUTTON1_MASK, EventTrigger.PICK));
        chart.setTrigger(1, new EventTrigger(InputEvent.SHIFT_MASK, EventTrigger.ZOOM));
        chart.setTrigger(2, new EventTrigger(InputEvent.CTRL_MASK, EventTrigger.TRANSLATE));
        //chart.addPickListener(this);

        /*
            chart.setChartType(com.klg.jclass.chart.JCChart.BAR);
            chart.setYAxisGridVisible(true);
            chart.setYAxisLogarithmic(true);
            chart.setYAxisMinMax("0,0");
            chart.setYAxisNumSpacing("0");

              */
        chart.getDataView(0).setDataSource(dataSource);
        setPointSize(0);
    }

    public void setPointSize(int size) {
        java.util.List seriesList = chart.getDataView(0).getSeries();
        for (java.util.Iterator it = seriesList.iterator(); it.hasNext();) {
            ((ChartDataViewSeries) it.next()).getStyle().setSymbolSize(size);
        }
    }


    private void jbInit() throws Exception {
        this.getContentPane().setLayout(borderLayout1);
        pnlChart.setInputVerifier(null);
        pnlChart.setLayout(borderLayout2);
        chart.setLegendVisible(true);
        pnlChart.add(chart, BorderLayout.CENTER);
        this.getContentPane().add(pnlChart, BorderLayout.CENTER);

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        setSize(400, 400);
    }

    private void closeDialog(java.awt.event.WindowEvent evt) {
        setVisible(false);
        dispose();
    }

    public void setChart(SimpleChart chart) {
        this.chart = chart;
    }

    public SimpleChart getChart() {
        return chart;
    }


}
