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

 /**
  * Handles the submission of issue.
  *
  * @param issue  The IssueModel object containing the submitted issue data.
  * @param result The BindingResult object to hold the validation result of the form submission.
  * @param model  The Model object to add attributes for the view.
  * @return The view name to display after the form submission.
  */
 @PostMapping("/createIssue")
 public String submitIssue(@ModelAttribute IssueModel issue, BindingResult result, Model model) {
  // Check if there are any errors in the result object
  if (result.hasErrors()) {
   return "redirect:/about"; // Redirect to "/about" if there are errors
  }

  // Set the repository and owner values
  String repo = "expenses-tracker";
  String owner = "immarisabel";

  // Set the assignees and labels for the issue
  List<String> assignees = Arrays.asList("immarisabel");
  List<String> labels = Arrays.asList("new request");
  issue.setAssignees(assignees);
  issue.setLabels(labels);

  try {
   // Send a POST request to create the issue using the GitHub service
   String response = githubService.post("https://api.github.com/repos/" + owner + "/" + repo + "/issues", new Gson().toJson(issue));

   if (response != null) {
    // If the response is not null, the issue was successfully created
    String message = "Issue was successfully created!";
    model.addAttribute("message", message); // Add the message attribute to the model
    return "about/about"; // Return the "about/about" view

   } else {
    // If the response is null, there was an error creating the issue
    String error = "There was an error creating the issue.";
    model.addAttribute("error", error); // Add the error attribute to the model
    return "about/about"; // Return the "about/about" view

   }
  } catch (WebClientResponseException.UnprocessableEntity e) {
   // Catch the specific exception for an unprocessable entity and handle it
   String error = "Nothing sent, message was blank";
   model.addAttribute("error", error); // Add the error attribute to the model
   return "about/about"; // Return the "about/about" view
  }
 }

}




