package nl.marisabel.frontend.savings;

import nl.marisabel.backend.savings.service.SavingsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class GoalController {

    private final SavingsService savingsService;

    public GoalController(SavingsService savingsService) {
        this.savingsService = savingsService;
    }


    @GetMapping("/savings/overview")
    public String showAllSavings(){
        return null;
    }

    // display charts
    // display goals list



}

