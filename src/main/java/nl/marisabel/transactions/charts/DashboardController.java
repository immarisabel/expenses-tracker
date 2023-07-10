package nl.marisabel.transactions.charts;

import lombok.extern.log4j.Log4j2;
import nl.marisabel.utils.notes.NoteModel;
import nl.marisabel.utils.notes.NoteUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@Log4j2
@RequestMapping("/dashboard")
public class DashboardController {

 private final DashboardService dashboardService;

 public DashboardController(DashboardService dashboardService) {
  this.dashboardService = dashboardService;
 }

 @GetMapping
 public String dashboard(@RequestParam(value = "year", required = false) Integer year, Model model) {
  dashboardService.showChartForCurrentYear(model);
  dashboardService.loadCategorizedTransactionsPrevMonth(model);

  String noteContent = NoteUtil.readNote();
  NoteModel noteModel = new NoteModel();
  noteModel.setNote(noteContent);
  model.addAttribute("noteModel", noteModel);
  model.addAttribute("prevYearExpenses", dashboardService.calculateExpensesAmountOfPrevYear());
  model.addAttribute("prevYearIncome", dashboardService.calculateIncomeAmountOfPrevYear());
  model.addAttribute("lastFileDate", dashboardService.getLastUploadedFileDateSignature());
  model.addAttribute("totalSavings", dashboardService.getTotalSavings());
  return "dashboard/dashboard";
 }


}


