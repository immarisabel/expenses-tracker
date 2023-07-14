package nl.marisabel.utils.notes;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@Log4j2
@RequestMapping("/note")
public class NoteController {

 @GetMapping
 public String noteForm(Model model) {

  String noteContent = NoteUtil.readNote();
  NoteModel noteModel = new NoteModel();
  noteModel.setNote(noteContent);
  model.addAttribute("noteModel", noteModel);
  log.info(noteContent);

  return "dashboard/notes";
 }

 @PostMapping("/save")
 public String saveNote(@ModelAttribute NoteModel noteModel, RedirectAttributes redirectAttributes) {
  try {
   NoteUtil.appendToFile(noteModel.getNote());
  } catch (IOException e) {
   e.printStackTrace();
   redirectAttributes.addFlashAttribute("message", "Error occurred while saving note!");
   return "redirect:/dashboard";
  }
  redirectAttributes.addFlashAttribute("message", "Note saved successfully!");
  return "redirect:/dashboard";
 }



}