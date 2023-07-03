package nl.marisabel.backend.issues.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LabelInfoModel {
 private String name;
 private String color;

 public LabelInfoModel(String name, String color) {
  this.name = name;
  this.color = color;
 }

}
