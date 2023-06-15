package nl.marisabel.frontend.dashboard;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

 @GetMapping
 public String dashboard(){
  return "dashboard/dashboard";
 }
}


// GET
// earnings overview lineal
// savings oveview lineal
// expenses overview lineal
// combined

// last months categories amount table or pie?
// note box

