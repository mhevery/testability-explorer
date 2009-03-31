/*
 * Copyright 2009 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.test.metric.report.chart;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.statistics.HistogramType;
import org.jfree.data.statistics.HistogramDataset;

import javax.imageio.ImageIO;
import java.io.OutputStream;
import java.io.IOException;
import java.util.List;

/**
 * This chart shows the distribution of classes against the testability
 * score. A good project should have a big lump at the left, and a crappy
 * project should have a wide distribution across the graph.
 *
 * @author aeagle
 */
public class CostDistributionChart {
  private final HistogramDataset dataset = new HistogramDataset();
  private static final int NUM_BINS = 20;
  private static final int WIDTH = 700;
  private static final int HEIGHT = 200;

  public CostDistributionChart() {
    dataset.setType(HistogramType.FREQUENCY);
  }

  public void createChart(OutputStream out) {
    NumberAxis xAxis = new NumberAxis("Cost to test");
    xAxis.setAutoRangeIncludesZero(false);
    LogarithmicAxis yAxis = new LogarithmicAxis("# classes");
    yAxis.setStrictValuesFlag(false);
    XYItemRenderer renderer = new XYBarRenderer();

    XYPlot plot = new XYPlot(dataset, xAxis, yAxis, renderer);
    JFreeChart chart = new JFreeChart(null,
            JFreeChart.DEFAULT_TITLE_FONT,
            plot, false);
    chart.setBackgroundPaint(null);
    try {
      ImageIO.write(chart.createBufferedImage(WIDTH, HEIGHT), "png", out);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void addValues(double[] vals) {
    if (vals.length > 0) {
      dataset.addSeries("Costs", vals, NUM_BINS);
    }
  }

  public void addValues(List<Integer> costs) {
    double[] vals = new double[costs.size()];
    for (int i=0; i<vals.length; i++) {
      vals[i] = costs.get(i);
    }
    addValues(vals);
  }
}
