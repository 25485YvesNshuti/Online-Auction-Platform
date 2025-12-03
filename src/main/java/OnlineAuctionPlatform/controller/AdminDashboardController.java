package OnlineAuctionPlatform.controller;

import OnlineAuctionPlatform.service.AdminSellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    @Autowired
    private AdminSellerService adminSellerService;

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        model.addAttribute("sellers", adminSellerService.getAllSellerProfiles());
        return "admin/dashboard";
    }
}
