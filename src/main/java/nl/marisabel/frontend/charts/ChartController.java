package nl.marisabel.frontend.charts;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@Log4j2
public class ChartController {

 private final ChartService chartService;

 public ChartController(ChartService chartService) {
  this.chartService = chartService;
 }

 @GetMapping("/grandTotalChart")
 public String showChart(Model model) {
  int totalCredits = chartService.getTotalCredits();
  int totalDebits = chartService.getTotalDebits();

  log.info("CREDIT: "+ totalCredits + " DEBIT: " + totalDebits);

  // Pass data to the view
  model.addAttribute("totalCredits", totalCredits);
  model.addAttribute("totalDebits", totalDebits);

  return "chart";
 }



 @GetMapping("/chart")
 public String showChartWithMonths(Model model) {
  Map<String, Double> monthlyCredits = chartService.getMonthlyCredits();
  Map<String, Double> monthlyDebits = chartService.getMonthlyDebits();

 log.info(".... Debits Mapped from service" + monthlyDebits);
  log.info(".... Credits Mapped from service" + monthlyCredits);

  List<String> labels = new ArrayList<>();
  List<Double> credits = new ArrayList<>();
  List<Double> debits = new ArrayList<>();

  log.info(".... Debits List created" + debits);
  log.info(".... Credits List created" + credits);

  for (Map.Entry<String, Double> entry : monthlyCredits.entrySet()) {
   String month = entry.getKey();
   double creditTotal = monthlyCredits.getOrDefault(month, 0.0);
   double debitTotal = monthlyDebits.getOrDefault(month, 0.0);

   labels.add(month);
   credits.add(creditTotal);
   debits.add(debitTotal);
  }

  log.info("CREDITS: "+ monthlyCredits.size() + " | DEBITS: " + monthlyDebits.size());

  model.addAttribute("labels", labels);
  model.addAttribute("credits", credits);
  model.addAttribute("debits", debits);

  return "chart-months";
 }


 @GetMapping("/chart/{month}")
 public String showChartByMonthlyCategories(@PathVariable String month, Model model) {
  // Parse the month from the request
  DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMyyyy");
  YearMonth yearMonth = YearMonth.parse(month, monthFormatter);

  // Fetch the monthly totals for the given month
  // You will need to add a method in your service that accepts YearMonth and returns monthly totals
  Map<String, Double> monthlyCreditsByCategory = chartService.getMonthlyCreditsByCategory(yearMonth);
  Map<String, Double> monthlyDebitsByCategory = chartService.getMonthlyDebitsByCategory(yearMonth);

  List<String> labels = new ArrayList<>();
  List<Double> credits = new ArrayList<>();
  List<Double> debits = new ArrayList<>();
  List<Double> savings = new ArrayList<>();

  for (Map.Entry<String, Double> entry : monthlyCreditsByCategory.entrySet()) {
   String category = entry.getKey();
   double creditTotal = monthlyCreditsByCategory.getOrDefault(category, 0.0);
   double debitTotal = monthlyDebitsByCategory.getOrDefault(category, 0.0);

   labels.add(category);
   credits.add(creditTotal);
   debits.add(debitTotal);
   savings.add(creditTotal - debitTotal); // Compute savings for each category
  }

  model.addAttribute("labels", labels);
  model.addAttribute("credits", credits);
  model.addAttribute("debits", debits);
  model.addAttribute("savings", savings);


  return "chart-monthly-categories";
 }



}
