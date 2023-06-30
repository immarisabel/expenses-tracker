package nl.marisabel.frontend.about.controller;

import nl.marisabel.frontend.about.model.IssueInfoModel;
import nl.marisabel.frontend.about.service.GithubService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/about")
public class AboutController {

 private final GithubService githubService;

 public AboutController(GithubService githubService) {
  this.githubService = githubService;
 }

 @GetMapping
 public String getPage(@ModelAttribute IssueInfoModel issueInfoModel, Model model){
  model.addAttribute("issuesList", githubService.get());
  return "about/about";
 }
}
