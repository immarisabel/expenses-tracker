package nl.marisabel.backend.issues.controller;

import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import nl.marisabel.backend.issues.model.IssueInfoModel;
import nl.marisabel.backend.issues.model.IssueModel;
import nl.marisabel.backend.issues.service.GithubService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Arrays;
import java.util.List;

@Controller
@Log4j2
public class GithubController {
 private final GithubService githubService;

 public GithubController(GithubService githubService) {
  this.githubService = githubService;
 }

 @GetMapping("/issues")
 public String getIssues(@ModelAttribute IssueInfoModel issueInfoModel, Model model) {
  model.addAttribute("issues", githubService.get());
  return "about/issues";
 }

 @GetMapping("/createIssue")
 public String showForm(@ModelAttribute IssueInfoModel issueInfoModel, Model model) {
  model.addAttribute("issuesList", githubService.get());
  model.addAttribute("issue", new IssueModel());
  return "about/github-form";
 }

 @PostMapping("/createIssue")
 public String submitIssue(@ModelAttribute IssueModel issue, BindingResult result, Model model) {
  if (result.hasErrors()) {
   return "redirect:/about";
  }

  String repo = "expenses-tracker";
  String owner = "immarisabel";
  List<String> assignees = Arrays.asList("immarisabel");
  List<String> labels = Arrays.asList("new request");

  issue.setAssignees(assignees);
  issue.setLabels(labels);

  try {
   String response = githubService.post("https://api.github.com/repos/" + owner + "/" + repo + "/issues", new Gson().toJson(issue));
   if (response != null) {
    String message = "Issue was successfully created!";
    model.addAttribute("message", message);
    return "about/about";

   } else {
    String error = "There was an error creating the issue.";
    model.addAttribute("error", error);
    return "about/about";

   }
  } catch (WebClientResponseException.UnprocessableEntity e) {
   String error = "Nothing sent, message was blank";
   model.addAttribute("error", error);
   return "about/about";
  }
 }


}




