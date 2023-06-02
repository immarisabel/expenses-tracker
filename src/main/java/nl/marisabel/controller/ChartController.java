package nl.marisabel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/chart")
public class ChartController {


 @GetMapping
 public String showChart(Model model) {
  // Sample data
  List<String> labels = Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
  List<Integer> dataA = Arrays.asList(10, 15, 12, 8, 9, 7, 11, 14, 13, 16, 10, 12);
  List<Integer> dataB = Arrays.asList(8, 12, 10, 9, 7, 6, 9, 13, 11, 14, 9, 10);

  // Pass data to the view
  model.addAttribute("labels", labels);
  model.addAttribute("dataA", dataA);
  model.addAttribute("dataB", dataB);

  return "chart";
 }

}
