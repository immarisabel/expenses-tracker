package nl.marisabel.frontend.about.controller;

import lombok.extern.log4j.Log4j2;
import nl.marisabel.frontend.about.model.IssueModel;
import nl.marisabel.frontend.about.service.GithubService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;
@Controller
@Log4j2
public class GithubController {
 private final GithubService githubService;

 public GithubController(GithubService githubService) {
  this.githubService = githubService;
 }

 @GetMapping("/createIssue")
 public String showForm(Model model) {
  model.addAttribute("issue", new IssueModel());
  return "createIssue";
 }

 @PostMapping("/createIssue")
 public String submitIssue(@ModelAttribute IssueModel issue, BindingResult result, Model model) {
  String repo="expenses-tracker";
  String owner ="immarisabel";
  if (result.hasErrors()) {
   return "createIssue";
  }

  RestTemplate restTemplate = new RestTemplate();

  HttpHeaders headers = new HttpHeaders();
  headers.set("Authorization", "token "+githubService.getToken());

  HttpEntity<IssueModel> request = new HttpEntity<>(issue, headers);

  ResponseEntity<String> response = restTemplate.exchange(
          "https://api.github.com/repos/"+owner+"/"+repo+"/issues",
          HttpMethod.POST,
          request,
          String.class);

  if (response.getStatusCodeValue() == 201) {
   return "issueResult";
  } else {
   // handle error...
   model.addAttribute("error", "There was an error creating the issue.");
   return "createIssue";
  }
 }
}




