package nl.marisabel.controller;

import nl.marisabel.util.ChartService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/chart")
public class ChartController {

 private final ChartService chartService;

 public ChartController(ChartService chartService) {
  this.chartService = chartService;
 }


 @GetMapping
 public String showChart(Model model) {
  String[] creditOrDebit = chartService.getCreditOrDebitArray();
  int[] amounts = chartService.getAmountsArray();

  // Pass data to the view
  model.addAttribute("creditOrDebit", creditOrDebit);
  model.addAttribute("amounts", amounts);

  return "chart";
 }

}
