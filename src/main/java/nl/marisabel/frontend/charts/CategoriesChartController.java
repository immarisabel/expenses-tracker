package nl.marisabel.frontend.charts;

import lombok.extern.log4j.Log4j2;
import nl.marisabel.backend.categories.entity.CategoryEntity;
import nl.marisabel.backend.categories.service.CategoryService;
import nl.marisabel.backend.transactions.entity.TransactionEntity;
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

    //TODO add cat link for transaction table
    //TODO add category name as header
    @GetMapping("/charts/categories/{categoryId}")
    public String showCategoryCharts(@PathVariable Long categoryId,
                                     @RequestParam(value = "year", required = false) Integer year,
                                     Model model) {

        // Load the category
        CategoryEntity category = categoryService.getCategory(categoryId);

        // Set up the current year
        int currentYear = Year.now().getValue();

        // If the category is not found, return an error view
        if (category == null) {
            return "error";
        }

        // If the year is not specified or 0, use the current year
        if (year == null || year == 0) {
            year = currentYear;
        }

        // Logging category and year information
        log.info("Category: " + category.getCategory());
        log.info(".... the year is " + year);

        // Get data for the specified category and year
        YearMonth startYearMonth = YearMonth.of(year, 1);
        YearMonth endYearMonth = YearMonth.of(year, 12);


        // Get data for the specified year
        Map<String, Double> monthlyCredits = chartService.getMonthlyCreditsByCategoryForCategory(year, category);
        Map<String, Double> monthlyDebits = chartService.getMonthlyDebitsByCategoryForCategory(year, category);


        // Initialize lists to store chart data
        List<String> labels = new ArrayList<>();
        List<Double> credits = new ArrayList<>();
        List<Double> debits = new ArrayList<>();

        // Populate the labels, credits, and debits lists
        for (int month = 1; month <= 12; month++) {
            String monthLabel = month < 10 ? "0" + month : String.valueOf(month);
            String monthKey = monthLabel + year; // Concatenate month and year to create the key
            double creditTotal = monthlyCredits.getOrDefault(monthKey, 0.0);
            double debitTotal = monthlyDebits.getOrDefault(monthKey, 0.0);

            labels.add(monthLabel);
            credits.add(creditTotal);
            debits.add(debitTotal);
        }



        // Add attributes to the model for use in the view
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

        // Return the name of the view template
        return "charts/chart-yearly-categories";
    }





// CORRECTLY DISPLAYING CHART ✔

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
        Map<String, Double> monthlyCreditsByCategory = chartService.getMonthlyCreditsByCategoryForMonth(yearMonth);
        Map<String, Double> monthlyDebitsByCategory = chartService.getMonthlyDebitsByCategoryForMonth(yearMonth);

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

        log.info(previousMonth.format(monthFormatter) + " | "+  month + " | "+ nextMonth.format(monthFormatter));

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

