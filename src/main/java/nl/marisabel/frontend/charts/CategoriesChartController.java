package nl.marisabel.frontend.charts;

import lombok.extern.log4j.Log4j2;
import nl.marisabel.backend.categories.entity.CategoryEntity;
import nl.marisabel.backend.categories.service.CategoryService;
import nl.marisabel.frontend.charts.ChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Log4j2
@Controller
public class CategoriesChartController {
    private final CategoryService categoryService;
    private final ChartService chartService;

    @Autowired
    public CategoriesChartController(CategoryService categoryService, ChartService chartService) {
        this.categoryService = categoryService;
        this.chartService = chartService;
    }

    @GetMapping("/charts/categories/{categoryId}")
    public String showCategoryCharts(@PathVariable Long categoryId,
                                     @RequestParam(value = "year", required = false) Integer year,
                                     Model model) {

        CategoryEntity category = categoryService.getCategory(categoryId);
        int currentYear = Year.now().getValue();

        if (category == null) {
            return "error";
        }

        if (year == null || year == 0) {
            year = currentYear;
        }

        log.info("Category: " + category.getCategory());
        log.info(".... the year is " + year);

        // Get data for the specified category and year
        YearMonth startYearMonth = YearMonth.of(year, 1);
        YearMonth endYearMonth = YearMonth.of(year, 12);

        Map<String, Double> monthlyCredits = new LinkedHashMap<>();
        Map<String, Double> monthlyDebits = new LinkedHashMap<>();

        for (int month = 1; month <= 12; month++) {
            YearMonth yearMonth = YearMonth.of(year, month);
            monthlyCredits.putAll(chartService.getMonthlyCreditsByCategory(yearMonth, category));
            monthlyDebits.putAll(chartService.getMonthlyDebitsByCategory(yearMonth, category));
        }

        List<String> labels = new ArrayList<>();
        List<Double> credits = new ArrayList<>();
        List<Double> debits = new ArrayList<>();

        for (Map.Entry<String, Double> entry : monthlyCredits.entrySet()) {
            String month = entry.getKey();
            double creditTotal = monthlyCredits.getOrDefault(month, 0.0);
            double debitTotal = monthlyDebits.getOrDefault(month, 0.0);

            labels.add(month);
            credits.add(creditTotal);
            debits.add(debitTotal);
        }

        model.addAttribute("labels", labels);
        model.addAttribute("credits", credits);
        model.addAttribute("debits", debits);
        model.addAttribute("category", category);
        model.addAttribute("currentYear", year);

        // Set previous and next year for pagination
        int previousYear = year - 1;
        int nextYear = year + 1;
        model.addAttribute("prevYear", previousYear);
        model.addAttribute("nextYear", nextYear);

        return "charts/chart-yearly-categories";
    }






    @GetMapping("/charts/month/{month}")
    public String showChartByMonthlyCategories(@PathVariable String month, Model model) {
        // Parse the month from the request
        log.info("Original YearMonth: " + month);

        if (month == null || month.isEmpty()) {
            YearMonth currentYearMonth = YearMonth.now();
            month = currentYearMonth.format(DateTimeFormatter.ofPattern("MMyyyy"));
        }

        // Original formatting
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMyyyy");
        YearMonth yearMonth = YearMonth.parse(month, monthFormatter);
        log.info("Parsed YearMonth: " + yearMonth);

        // Formatting for header
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        String formattedDate = yearMonth.format(formatter);
        log.info("Formatted Date: " + formattedDate);

        // Calculate the previous and next months
        YearMonth previousMonth = yearMonth.minusMonths(1);
        YearMonth nextMonth = yearMonth.plusMonths(1);

        // Fetch the monthly totals for the given month
        Map<String, Double> monthlyCreditsByCategory = chartService.getMonthlyCreditsByCategory(yearMonth);
        Map<String, Double> monthlyDebitsByCategory = chartService.getMonthlyDebitsByCategory(yearMonth);

        List<String> labels = new ArrayList<>();
        List<Double> credits = new ArrayList<>();
        List<Double> debits = new ArrayList<>();
        List<Double> savings = new ArrayList<>();

        for (Map.Entry<String, Double> entry : monthlyCreditsByCategory.entrySet()) {
            String category = entry.getKey();
            double creditTotal = entry.getValue();
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
        model.addAttribute("fulldate", formattedDate);
        model.addAttribute("month", month);
        model.addAttribute("previousMonth", previousMonth.format(monthFormatter));
        model.addAttribute("nextMonth", nextMonth.format(monthFormatter));

        return "charts/chart-monthly-categories";
    }


}
