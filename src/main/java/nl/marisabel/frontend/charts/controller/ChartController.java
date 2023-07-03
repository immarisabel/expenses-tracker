package nl.marisabel.frontend.charts.controller;

import lombok.extern.log4j.Log4j2;
import nl.marisabel.backend.categories.entity.CategoryEntity;
import nl.marisabel.backend.categories.service.CategoryService;
import nl.marisabel.frontend.charts.service.ChartService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@Log4j2
public class ChartController {

 private final ChartService chartService;
 private final CategoryService categoryService;

 public ChartController(ChartService chartService, CategoryService categoryService) {
  this.chartService = chartService;
  this.categoryService = categoryService;
 }


 @GetMapping("/charts")
 public String showChartsLists(Model model) {

  YearMonth currentYearMonth = YearMonth.now();
  String month = currentYearMonth.format(DateTimeFormatter.ofPattern("MMyyyy"));
  log.info(".... link generated for categories chart charts/" + month);

  List<CategoryEntity> categories = categoryService.getCategories();

  model.addAttribute("chartsMonthLink", "/charts/month/" + month);

  model.addAttribute("categories", categories);
  return "charts/charts";
 }




}
