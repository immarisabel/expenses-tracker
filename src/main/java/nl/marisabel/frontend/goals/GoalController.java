package nl.marisabel.frontend.goals;

import nl.marisabel.frontend.goals.GoalModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class GoalController {

    @GetMapping("/goals")
    public String getGoals(Model model) {


        List<GoalModel> goals = new ArrayList<>();
        goals.add(new GoalModel("Insurance", 150.00, 0));
        goals.add(new GoalModel("House", 150.00, 0));
        goals.add(new GoalModel("Clothes", 150.00, 0));
        double totalToAllocate = 150.00;
        double totalAllocated = 0;


        model.addAttribute("goals", goals);
        model.addAttribute("totalToAllocate", totalToAllocate);
        model.addAttribute("totalAllocated", totalAllocated);

        return "goals";
    }



}
