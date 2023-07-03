package nl.marisabel.backend.issues.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.marisabel.backend.issues.model.IssueInfoModel;
import nl.marisabel.backend.issues.model.LabelInfoModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GithubService {

 @Value("${github.token}")
 private String TOKEN;

 private  final String REPO = "expenses-tracker";
 private  final String OWNER = "immarisabel";

 @Autowired
 private final WebClient webClient;
 private final ObjectMapper objectMapper;

 public GithubService(WebClient webClient, ObjectMapper objectMapper) {
  this.webClient = webClient;
  this.objectMapper = objectMapper;
 }

 public List<IssueInfoModel> get() {
  String url = "https://api.github.com/repos/" + OWNER + "/" + REPO + "/issues";
  String authToken = TOKEN;
  String response = webClient.get()
          .uri(url)
          .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken)
          .retrieve()
          .bodyToMono(String.class)
          .block();

  List<IssueInfoModel> issuesList = new ArrayList<>();
  try {
   JsonNode jsonNode = objectMapper.readTree(response);

   if (jsonNode.isArray()) {
    for (JsonNode issueNode : jsonNode) {
     String title = issueNode.get("title").asText();
     String htmlUrl = issueNode.get("html_url").asText();

     List<LabelInfoModel> labels = new ArrayList<>();
     JsonNode labelsNode = issueNode.get("labels");
     if (labelsNode != null && labelsNode.isArray()) {
      for (JsonNode labelNode : labelsNode) {
       String labelName = labelNode.get("name").asText();
       String labelColor = labelNode.get("color").asText();
       LabelInfoModel labelInfo = new LabelInfoModel(labelName, labelColor);
       labels.add(labelInfo);
      }
     }

     IssueInfoModel issueInfo = new IssueInfoModel(title, htmlUrl);
     issueInfo.setLabels(labels); // Set the labels in the IssueInfoModel
     issuesList.add(issueInfo);
    }
   } else {
    System.out.println("No issues found in the JSON response.");
   }
  } catch (Exception e) {
   e.printStackTrace();
  }


  return issuesList;
 }
 public String post(String url, String json) {
   return webClient.post()
           .uri(url)
           .header("Authorization", "token " + TOKEN)
           .header("Content-Type", "application/json")
           .body(BodyInserters.fromValue(json))
           .retrieve()
           .bodyToMono(String.class)
           .block();
  }

  public String buildIssueJson(String title, String body, List<String> assignees, List<String> labels) {
   String assigneeStr = assignees.stream().map(a -> "\"" + a + "\"").collect(Collectors.joining(", "));
   String labelStr = labels.stream().map(l -> "\"" + l + "\"").collect(Collectors.joining(", "));
   return "{\"title\":\"" + title + "\", \"body\":\"" + body + "\", \"assignees\": [" + assigneeStr + "], \"labels\": [" + labelStr + "]}";
  }



 public String getToken() {
  return TOKEN;
 }
}
