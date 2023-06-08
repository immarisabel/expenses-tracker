package nl.marisabel.frontend.charts;

import lombok.extern.log4j.Log4j2;
import nl.marisabel.backend.categories.service.CategoryService;
import nl.marisabel.frontend.charts.ChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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


    // TODO FULL CHART CATEGORIES
    // TODO chart-monthly-categories.html controller here
        //Server-Side Pagination:
            //Modify your backend controller to support pagination parameters, such as page number and page size.
            //On the frontend, make an AJAX request to the backend controller to fetch the data for each page.
            //Display the fetched data on the page, showing 12 months per page.
            //Implement pagination controls on the frontend to trigger the AJAX requests and update the displayed data.
            //This approach reduces the initial data load by fetching only the necessary data for each page.

    @GetMapping("/chart/{month}")
    public String showChartByMonthlyCategories(@PathVariable String month, Model model) {
        // Parse the month from the request

        log.info("Original YearMonth: " + month);

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

        return "chart-monthly-categories";
    }


}
