package nl.marisabel.controller;

import nl.marisabel.util.ChartService;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/chart")
public class ChartController {

 private final ChartService chartService;

 public ChartController(ChartService chartService) {
  this.chartService = chartService;
 }

 @GetMapping
 public String showChart(Model model) {
  DefaultCategoryDataset dataset = chartService.generateExpenseIncomeDataset();

  JFreeChart chart = ChartFactory.createLineChart(
          "Expense and Income Chart",
          "Month",
          "Amount",
          dataset,
          PlotOrientation.VERTICAL,
          true,
          true,
          false
  );

  // Pass the chart object to the view
  model.addAttribute("chart", chart);

  return "chart";
 }
}
