package nl.marisabel.backend.notes;

import nl.marisabel.backend.notes.entity.NoteEntity;
import nl.marisabel.backend.notes.repository.NoteRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/notes")
public class NoteController {

 private final NoteRepository noteRepository;

 public NoteController(NoteRepository noteRepository) {
  this.noteRepository = noteRepository;
 }

 private static final Long NOTE_ID = 1L;

 @GetMapping
 public String notes(Model model){
  Optional<NoteEntity> optionalNote = noteRepository.findById(NOTE_ID);
  model.addAttribute("note", optionalNote.isPresent() ? optionalNote.get().getNote() : "");
  return "dashboard/notes";
 }

 @PostMapping
 public String saveNote(@RequestParam("note") String noteText){
  NoteEntity note = new NoteEntity();
  note.setId(NOTE_ID);
  note.setNote(noteText);
  noteRepository.save(note);
  return "redirect:/notes"; // redirect back to GET
 }
}
