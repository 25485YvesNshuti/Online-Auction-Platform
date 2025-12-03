package OnlineAuctionPlatform.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SellerDashboardController {
    @GetMapping("/seller/dashboard")
    public String showSellerDashboard(Model model) {
        // TODO: Add seller-specific info to model
        return "seller/dashboard";
    }
}
