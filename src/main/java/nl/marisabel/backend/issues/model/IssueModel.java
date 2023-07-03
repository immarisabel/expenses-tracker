package nl.marisabel.backend.issues.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class IssueModel {
  private String title;
  private String body;
  private List<String> assignees;
  private List<String> labels;

}
