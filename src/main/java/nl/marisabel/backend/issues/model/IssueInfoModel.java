package nl.marisabel.backend.issues.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class IssueInfoModel {
 private String title;
 private String htmlUrl;
 private List<LabelInfoModel> labels;

 public IssueInfoModel(String title, String htmlUrl) {
  this.title = title;
  this.htmlUrl = htmlUrl;
  this.labels = new ArrayList<>();
 }
}


