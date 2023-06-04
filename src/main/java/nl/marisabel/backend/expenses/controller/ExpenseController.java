package nl.marisabel.backend.expenses.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import nl.marisabel.backend.categories.entity.CategoryEntity;
import nl.marisabel.backend.categories.repository.CategoryRepository;
import nl.marisabel.backend.expenses.entity.ExpenseEntity;
import nl.marisabel.backend.expenses.entity.ExpenseFormDto;
import nl.marisabel.backend.expenses.repository.ExpenseRepository;
import nl.marisabel.backend.expenses.service.ExpenseService;
import nl.marisabel.frontend.charts.ExpenseUploadResult;
import nl.marisabel.util.ExpensesCvsReaderING;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@Log4j2
public class ExpenseController {

    private final ExpensesCvsReaderING expensesCvsReader;
    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;
    private final ExpenseService expenseService;


    public ExpenseController(ExpensesCvsReaderING expensesCvsReader, ExpenseRepository expenseRepository, CategoryRepository categoryRepository, ExpenseService expenseService) {
        this.expensesCvsReader = expensesCvsReader;
        this.expenseRepository = expenseRepository;
        this.categoryRepository = categoryRepository;
        this.expenseService = expenseService;
    }


    @GetMapping("/expenses")
    public String showExpenses(@RequestParam(defaultValue = "0") int page, Model model, Pageable pageable) {
        int size = 25; // or whatever number you want
        Page<ExpenseEntity> expenses = expenseRepository.findAll(PageRequest.of(page, size));
        List<CategoryEntity> categories = categoryRepository.findAll();
        model.addAttribute("expenses", expenses);
        model.addAttribute("categories", categories);
        model.addAttribute("expenseForm", new ExpenseFormDto());
        return "expenses";
    }


    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        String message = "";
        try {
            if (file.isEmpty()) {
                message = "Please select a file to upload.";
            } else {
                ExpenseUploadResult result = expensesCvsReader.read(file.getInputStream());
                redirectAttributes.addFlashAttribute("result", result);
                message = "File uploaded successfully.";
            }
        } catch (Exception e) {
            message = "Error uploading file: " + e.getMessage();
        }

        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:/expenses";
    }



    @PostMapping("/expenses/updateCategory")
    public String batchUpdateCategory(
            @RequestParam("categoryId") Long categoryId,
            @RequestParam("selectedExpenseIds") String[] selectedExpenseIds,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request) {

        List<Long> expenseIds = Arrays.stream(selectedExpenseIds)
                .map(Long::valueOf)
                .collect(Collectors.toList());
        log.info("....expenses to update: " + expenseIds.size());

        List<ExpenseEntity> expenses = expenseRepository.findAllById(expenseIds);

        log.info(".... expenses updated: " + expenses);

        try {
            expenseService.batchUpdateCategory(categoryId, expenses);
            redirectAttributes.addFlashAttribute("successMessage", "Expenses updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating expenses: " + e.getMessage());
        }

        String redirectUrl = "redirect:/expenses/search";
        String queryString = request.getQueryString();
        if (queryString != null) {
            redirectUrl += "?" + queryString;
        }

        return redirectUrl;
    }





    @GetMapping("/expenses/search")
    public String searchExpenses(@RequestParam(value = "searchTerm", required = false) String searchTerm,
                                 @RequestParam(value = "searchTermInput", required = false) String searchTermInput,
                                 Model model) {
        if (searchTermInput != null) {
            searchTerm = searchTermInput;
        }        List<ExpenseEntity> searchResults = expenseService.searchExpenses(searchTerm);
        List<CategoryEntity> categories = categoryRepository.findAll();
        model.addAttribute("expenses", searchResults);
        model.addAttribute("searchCount", searchResults.size());
        model.addAttribute("categories", categories);
        model.addAttribute("searchTerm", searchTerm);
        return "expenses";
    }

    @GetMapping("/expenses/filter")
    public String filterExpensesByDate(@RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                       @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                       Model model) {
        List<ExpenseEntity> filteredResults = expenseService.filterExpensesByDate(startDate, endDate);
        model.addAttribute("expenses", filteredResults);
        model.addAttribute("filteredCount", filteredResults.size());
        return "expenses";
    }


}
