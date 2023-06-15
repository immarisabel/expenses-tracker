package nl.marisabel.frontend.dashboard;

import nl.marisabel.backend.categories.entity.CategoryEntity;
import nl.marisabel.backend.categories.service.CategoryService;
import nl.marisabel.backend.savings.entity.SavingsEntity;
import nl.marisabel.backend.savings.service.SavingsService;
import nl.marisabel.backend.transactions.entity.TransactionEntity;
import nl.marisabel.backend.transactions.service.TransactionService;
import nl.marisabel.frontend.charts.ChartService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/dashboard")
public class DashboardController {

 private final ChartService chartService;
 private final DashboardService dashboardService;

 public DashboardController(ChartService chartService, DashboardService dashboardService) {
  this.chartService = chartService;
  this.dashboardService = dashboardService;
 }

 @GetMapping
 public String dashboard(@RequestParam(value = "year", required = false) Integer year, Model model) {
  dashboardService.showChartWithMonths(year, model);
  dashboardService.loadCategorizedTransactions(model);
  return "dashboard/dashboard";
 }


}


