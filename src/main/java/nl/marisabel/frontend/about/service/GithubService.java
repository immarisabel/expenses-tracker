package nl.marisabel.frontend.about.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GithubService {
 @Value("${github.token}")
 private String TOKEN;


 @Autowired
 private final WebClient webClient;

 public GithubService(WebClient webClient) {
  this.webClient = webClient;
 }


 public String post(String url, String json) {
   return webClient.post()
           .uri(url)
           .header("Authorization", "token " + TOKEN)
           .header("Content-Type", "application/json")
           .body(BodyInserters.fromValue(json))
           .retrieve()
           .bodyToMono(String.class)
           .block(); // block() makes the call synchronous, use subscribe() for non-blocking
  }

  // JSON string for a new GitHub issue
// JSON string for a new GitHub issue
  public String buildIssueJson(String title, String body, List<String> assignees, List<String> labels) {
   String assigneeStr = assignees.stream().map(a -> "\"" + a + "\"").collect(Collectors.joining(", "));
   String labelStr = labels.stream().map(l -> "\"" + l + "\"").collect(Collectors.joining(", "));
   return "{\"title\":\"" + title + "\", \"body\":\"" + body + "\", \"assignees\": [" + assigneeStr + "], \"labels\": [" + labelStr + "]}";
  }



 public String getToken() {
  return TOKEN;
 }
}
