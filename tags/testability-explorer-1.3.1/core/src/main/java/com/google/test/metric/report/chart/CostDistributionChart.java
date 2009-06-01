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

import com.google.test.metric.report.GradeCategories;
import com.google.test.metric.report.MultiHistogramDataModel;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StackedXYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.CategoryTableXYDataset;

import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.imageio.ImageIO;

/**
 * This chart shows the distribution of classes against the testability
 * score. A good project should have a big lump at the left, and a crappy
 * project should have a wide distribution across the graph.
 *
 * @author aeagle
 */
public class CostDistributionChart {
  private static final int WIDTH = 700;
  private static final int HEIGHT = 200;
  private List<Integer> costs;
  private final GradeCategories gradeCategories;

  public CostDistributionChart(GradeCategories gradeCategories) {
    this.gradeCategories = gradeCategories;
  }

  public void createChart(OutputStream out) {
    CategoryTableXYDataset dataset = new CategoryTableXYDataset();
    MultiHistogramDataModel model = gradeCategories.buildHistogramDataModel(costs);
    addToDataset(dataset, model, "Excellent", model.getExcellent().getBinRange());
    addToDataset(dataset, model, "Good", model.getGood().getBinRange());
    addToDataset(dataset, model, "Needs Work", model.getNeedsWork().getBinRange());

    NumberAxis xAxis = new NumberAxis("Cost to test");
    xAxis.setAutoRangeIncludesZero(false);
    LogarithmicAxis yAxis = new LogarithmicAxis("# classes");
    yAxis.setStrictValuesFlag(false);
    XYItemRenderer renderer = new StackedXYBarRenderer();
    renderer.setSeriesPaint(2, Color.RED);
    renderer.setSeriesPaint(1, Color.YELLOW);
    renderer.setSeriesPaint(0, Color.GREEN);
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

  private void addToDataset(CategoryTableXYDataset dataset, MultiHistogramDataModel model,
                            String seriesName, int[] dataSeries) {
    for (int binNum = 0; binNum < dataSeries.length; binNum++) {
      int value = dataSeries[binNum];
      dataset.add((binNum + 0.5) * model.getBinWidth(), value, seriesName);
    }
  }

  public void addValues(List<Integer> costs) {
    this.costs = costs;
  }
}
