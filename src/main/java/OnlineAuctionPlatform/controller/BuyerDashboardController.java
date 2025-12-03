package OnlineAuctionPlatform.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BuyerDashboardController {
    @GetMapping("/dashboard")
    public String showBuyerDashboard(Model model) {
        // TODO: Add buyer-specific info to model
        return "buyer/dashboard";
    }
}
