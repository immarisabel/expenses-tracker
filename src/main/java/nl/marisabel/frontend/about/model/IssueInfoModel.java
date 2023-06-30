package nl.marisabel.frontend.about.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IssueInfoModel {
 private String title;
 private String htmlUrl;

 public IssueInfoModel(String title, String htmlUrl) {
  this.title = title;
  this.htmlUrl = htmlUrl;
 }
}