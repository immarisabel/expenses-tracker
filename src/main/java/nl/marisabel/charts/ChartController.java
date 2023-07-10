package nl.marisabel.charts;

import lombok.extern.log4j.Log4j2;
import nl.marisabel.categories.classes.CategoryEntity;
import nl.marisabel.categories.classes.CategoryServiceImp;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@Log4j2
public class ChartController {

 private final ChartService chartService;
 private final CategoryServiceImp categoryServiceImp;

 public ChartController(ChartService chartService, CategoryServiceImp categoryServiceImp) {
  this.chartService = chartService;
  this.categoryServiceImp = categoryServiceImp;
 }


 @GetMapping("/charts")
 public String showChartsLists(Model model) {

  YearMonth currentYearMonth = YearMonth.now();
  String month = currentYearMonth.format(DateTimeFormatter.ofPattern("MMyyyy"));
  log.info(".... link generated for categories chart charts/" + month);

  List<CategoryEntity> categories = categoryServiceImp.getCategories();

  model.addAttribute("chartsMonthLink", "/charts/month/" + month);

  model.addAttribute("categories", categories);
  return "charts/charts";
 }




}
