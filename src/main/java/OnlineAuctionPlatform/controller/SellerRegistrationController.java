package OnlineAuctionPlatform.controller;

import OnlineAuctionPlatform.dto.SellerRegistrationStep1DTO;
import OnlineAuctionPlatform.dto.SellerRegistrationStep2DTO;
import OnlineAuctionPlatform.dto.SellerRegistrationStep3DTO;
import OnlineAuctionPlatform.model.User;
import OnlineAuctionPlatform.service.SellerRegistrationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/seller/register")
public class SellerRegistrationController {
    @Autowired
    private SellerRegistrationService sellerRegistrationService;

    @GetMapping("/step1")
    public String showStep1Form(Model model) {
        model.addAttribute("sellerStep1", new SellerRegistrationStep1DTO());
        return "auth/register-seller-step1";
    }

    @PostMapping("/step1")
    public String handleStep1(@Valid @ModelAttribute("sellerStep1") SellerRegistrationStep1DTO dto,
                            BindingResult bindingResult,
                            Model model) {
        if (bindingResult.hasErrors()) {
            return "auth/register-seller-step1";
        }
        try {
            User user = sellerRegistrationService.registerSellerStep1(dto);

            model.addAttribute("userId", user.getId());
            return "redirect:/seller/register/step2?userId=" + user.getId();
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "auth/register-seller-step1";
        }
    }

    @GetMapping("/step2")
    public String showStep2Form(@RequestParam UUID userId, Model model) {
        model.addAttribute("sellerStep2", new SellerRegistrationStep2DTO());
        model.addAttribute("userId", userId);
        return "auth/seller_step2";
    }

    @PostMapping("/step2")
    public String handleStep2(@RequestParam UUID userId,
                            @ModelAttribute SellerRegistrationStep2DTO dto,
                            Model model) {
        try {
            sellerRegistrationService.registerSellerStep2(userId, dto);
            return "redirect:/seller/register/step3?userId=" + userId;
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("userId", userId);
            return "auth/seller_step2";
        }
    }

    @GetMapping("/step3")

    public String showStep3Form(@RequestParam UUID userId, Model model) {
        model.addAttribute("sellerStep3", new SellerRegistrationStep3DTO());
        model.addAttribute("userId", userId);
        return "auth/seller_step3";
    }

    @PostMapping("/step3")
    public String handleStep3(@RequestParam UUID userId,
                              @ModelAttribute SellerRegistrationStep3DTO dto,
                              Model model) {
        try {
            sellerRegistrationService.registerSellerStep3(userId, dto);
            model.addAttribute("success", "Application submitted successfully! Your account is under review.");
            return "auth/seller_success";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("userId", userId);
            return "auth/seller_step3";
        }
    }
}
