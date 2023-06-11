package nl.marisabel.backend.savings.controller;

import nl.marisabel.backend.savings.service.SavingsService;
import nl.marisabel.frontend.savings.GoalModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class SavingsController {

 private final SavingsService savingsService;


 public SavingsController(SavingsService savingsService) {
  this.savingsService = savingsService;
 }





 @GetMapping("/savings/allocate-savings")
 public String allocateSavingsInGoals(Model model) {

  //TODO load max value for goals and goals from database

  // DUMMY DATA
  List<GoalModel> goals = new ArrayList<>();
  goals.add(new GoalModel("Insurance", 150.00, 0, " ", false));
  goals.add(new GoalModel("House", 150.00, 0," ", false));
  goals.add(new GoalModel("Clothes", 150.00, 0," ", false ));

  //TODO add to totalToAllocate amount left over at end of the month
  //TODO total allocated will save to selected month on URL path in database under the goal's id
  double totalToAllocate = 150.00;
  double totalAllocated = 0;

  model.addAttribute("goals", goals);
  model.addAttribute("totalToAllocate", totalToAllocate);
  model.addAttribute("totalAllocated", totalAllocated);
  //TODO
  // if ((credit - debit)>0){
  // load in amount to be allocated controller.
  // load goals
  // then allocate amounts per goal
  // if(allocating > toAllocate) {
  // return error - you do not have enough
  // }  else{
  // store in transactions allocated }
  // }

  return "goals";
 }


 // edit goals - add amount and name, remove, edit amount and name


}

