package nl.marisabel.frontend.dashboard;

import lombok.extern.log4j.Log4j2;
import nl.marisabel.frontend.notes.NoteModel;
import nl.marisabel.util.NoteUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;


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
  dashboardService.loadCategorizedTransactions(model);

  String noteContent = NoteUtil.readNote();
  NoteModel noteModel = new NoteModel();
  noteModel.setNote(noteContent);
  model.addAttribute("noteModel", noteModel.getNote());

  log.info(noteContent);

  return "dashboard/dashboard";
 }



}


