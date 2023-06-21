package nl.marisabel.frontend.about.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/about")
public class AboutController {
 @GetMapping
 public String getPage(){
  return "about";
 }
}
