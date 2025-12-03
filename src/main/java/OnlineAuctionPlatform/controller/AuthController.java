package OnlineAuctionPlatform.controller;

import OnlineAuctionPlatform.dto.*;
import OnlineAuctionPlatform.model.User;
import OnlineAuctionPlatform.service.AuthService;
import OnlineAuctionPlatform.service.BuyerRegistrationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.UUID;
import jakarta.servlet.http.HttpSession;


@Controller
public class AuthController {
            @GetMapping("/")
            public String showLandingPage() {
                return "index";
            }

        @GetMapping("/profile")
        public String showProfile(Model model) {
            // TODO: Add user info to model
            return "user-management/profile";
        }

        @GetMapping("/user-list")
        public String showUserList(Model model) {
            // TODO: Add user list to model
            return "user-management/user-list";
        }

        @GetMapping("/user-details")
        public String showUserDetails(Model model) {
            // TODO: Add user details to model
            return "user-management/user-details";
        }
    @Autowired
    private AuthService authService;

    @Autowired
    private BuyerRegistrationService buyerRegistrationService;

    @Autowired
    private org.springframework.security.authentication.AuthenticationManager authenticationManager;

    // Removed duplicate /auth/register-buyer mapping to resolve ambiguous mapping error

    @PostMapping("/2fa/verify")
    public String handleTwoFactorVerify(@RequestParam(required = false) String digit1,
                                        @RequestParam(required = false) String digit2,
                                        @RequestParam(required = false) String digit3,
                                        @RequestParam(required = false) String digit4,
                                        @RequestParam(required = false) String digit5,
                                        @RequestParam(required = false) String digit6,
                                        @RequestParam(required = false) String appDigit1,
                                        @RequestParam(required = false) String appDigit2,
                                        @RequestParam(required = false) String appDigit3,
                                        @RequestParam(required = false) String appDigit4,
                                        @RequestParam(required = false) String appDigit5,
                                        @RequestParam(required = false) String appDigit6,
                                        @RequestParam(required = false) String email,
                                        Model model,
                                        HttpSession session,
                                        HttpServletResponse response) {
        String code = null;
        if (digit1 != null) {
            code = String.join("", digit1, digit2, digit3, digit4, digit5, digit6);
        } else if (appDigit1 != null) {
            code = String.join("", appDigit1, appDigit2, appDigit3, appDigit4, appDigit5, appDigit6);
        }
        String sessionEmail = (String) session.getAttribute("LOGIN_EMAIL");
        String effectiveEmail = sessionEmail != null ? sessionEmail : email;
        if (code != null && code.length() == 6 && effectiveEmail != null) {
            try {
                authService.verifyOtp(effectiveEmail, code);
                session.setAttribute("OTP_VERIFIED", true);
                User user = authService.findUserByEmailOrPhone(effectiveEmail);
                if (user == null) {
                    model.addAttribute("error", "User not found after OTP verification.");
                    return "auth/verify-otp";
                }
                User.Role role = user.getRole();
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        user, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name())));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

                if (role == User.Role.ADMIN) {
                    return "redirect:/admin/dashboard";
                } else if (role == User.Role.SELLER) {
                    return "redirect:/seller/dashboard";
                } else {
                    return "redirect:/dashboard";
                }
            } catch (Exception e) {
                model.addAttribute("error", e.getMessage());
                return "auth/verify-otp";
            }
        } else {
            model.addAttribute("error", "Invalid code. Please try again.");
            return "auth/verify-otp";
        }
    }

    @GetMapping("/auth/resend-otp")
    public String handleResendOtp(@RequestParam String email, Model model) {
        try {
            authService.resendOtp(email);
            model.addAttribute("email", email);
            model.addAttribute("resendSeconds", 22);
            model.addAttribute("success", "OTP resent to your email.");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "auth/verify-otp";
    }
    @GetMapping("/register-seller-step1")
    public String showSellerStep1Form(Model model, HttpSession session) {
        model.addAttribute("sellerStep1", new SellerRegistrationStep1DTO());
        session.removeAttribute("userId");
        return "auth/register-seller-step1";
    }

    @PostMapping("/register-seller-step1")
    public String handleSellerStep1(@Valid @ModelAttribute("sellerStep1") SellerRegistrationStep1DTO dto,
                                    BindingResult bindingResult,
                                    Model model,
                                    HttpSession session) {
        if (bindingResult.hasErrors()) {
            return "auth/register-seller-step1";
        }
        try {
            User user = authService.registerSellerStep1(dto);
            session.setAttribute("userId", user.getId());
            return "redirect:/register-seller-step2";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "auth/register-seller-step1";
        }
    }

    @GetMapping("/register-seller-step2")
    public String showSellerStep2Form(@RequestParam(value = "userId", required = false) UUID userIdParam, Model model, HttpSession session) {
        UUID userId = userIdParam;
        if (userId == null) {
            userId = (UUID) session.getAttribute("userId");
        } else {
            session.setAttribute("userId", userId);
        }
        if (userId == null) {
            return "redirect:/register-seller-step1";
        }
        model.addAttribute("sellerStep2", new SellerRegistrationStep2DTO());
        model.addAttribute("userId", userId);
        return "auth/register-seller-step2";
    }

    @PostMapping("/register-seller-step2")
    public String handleSellerStep2(@RequestParam(value = "userId", required = false) UUID userIdParam,
                                    @Valid @ModelAttribute SellerRegistrationStep2DTO dto,
                                    BindingResult bindingResult,
                                    Model model,
                                    HttpSession session) {
        UUID userId = userIdParam;
        if (userId == null) {
            userId = (UUID) session.getAttribute("userId");
        } else {
            session.setAttribute("userId", userId);
        }
        if (userId == null) {
            return "redirect:/register-seller-step1";
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("userId", userId);
            return "auth/register-seller-step2";
        }
        try {
            authService.registerSellerStep2(userId, dto);
            return "redirect:/register-seller-step3?userId=" + userId;
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("userId", userId);
            return "auth/register-seller-step2";
        }
    }

    @GetMapping("/register-seller-step3")
    public String showSellerStep3Form(@RequestParam(value = "userId", required = false) UUID userIdParam, Model model, HttpSession session) {
        UUID userId = userIdParam;
        if (userId == null) {
            userId = (UUID) session.getAttribute("userId");
        } else {
            session.setAttribute("userId", userId);
        }
        if (userId == null) {
            return "redirect:/register-seller-step1";
        }
        model.addAttribute("sellerStep3", new SellerRegistrationStep3DTO());
        model.addAttribute("userId", userId);
        return "auth/register-seller-step3";
    }

    @PostMapping("/register-seller-step3")
    public String handleSellerStep3(@RequestParam(value = "userId", required = false) UUID userIdParam,
                                    @Valid @ModelAttribute SellerRegistrationStep3DTO dto,
                                    BindingResult bindingResult,
                                    Model model,
                                    HttpSession session) {
        UUID userId = userIdParam;
        if (userId == null) {
            userId = (UUID) session.getAttribute("userId");
        } else {
            session.setAttribute("userId", userId);
        }
        if (userId == null) {
            return "redirect:/register-seller-step1";
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("userId", userId);
            return "auth/register-seller-step3";
        }
        try {
            authService.registerSellerStep3(userId, dto);
            model.addAttribute("submittedDate", java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            model.addAttribute("expectedApproval", java.time.LocalDate.now().plusDays(3).format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            session.removeAttribute("userId");
            return "auth/seller-submission-confirmation";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("userId", userId);
            return "auth/register-seller-step3";
        }
    }

    @GetMapping("/2fa/setup")
    public String showTwoFactorSetup(Model model) {
        return "auth/two-factor-setup";
    }

    @GetMapping("/2fa/verify")
    public String showTwoFactorVerify(Model model, @RequestParam(value = "email", required = false) String email) {
        if (email != null) model.addAttribute("email", email);
        return "auth/two-factor-auth";
    }

    @PostMapping(value = "/api/login", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public org.springframework.http.ResponseEntity<?> apiLogin(@RequestBody OnlineAuctionPlatform.dto.LoginRequest loginRequest, HttpSession session) {
        try {
            String loginResult = authService.login(loginRequest, session);
            if ("OTP_REQUIRED".equals(loginResult)) {
                return org.springframework.http.ResponseEntity.status(401).body(java.util.Map.of("status", "OTP_REQUIRED", "message", "OTP required"));
            }
            String role = authService.getJwtUtil().extractRole(loginResult);
            return org.springframework.http.ResponseEntity.ok(java.util.Map.of("token", loginResult, "role", role));
        } catch (Exception e) {
            return org.springframework.http.ResponseEntity.status(401).body(java.util.Map.of("error", e.getMessage()));
        }
    }


    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "auth/login";
    }

    @PostMapping("/login")
    public String handleLogin(@Valid @ModelAttribute LoginRequest loginRequest,
                            BindingResult bindingResult,
                            Model model,
                            HttpSession session) {
        if (bindingResult.hasErrors()) {
            return "auth/login";
        }
        try {
            String loginResult = authService.login(loginRequest, session);
            if ("OTP_REQUIRED".equals(loginResult)) {
                model.addAttribute("email", loginRequest.getEmailOrPhone());
                return "auth/verify-otp";
            }
            session.setAttribute("JWT_TOKEN", loginResult);
            session.setAttribute("LOGIN_EMAIL", loginRequest.getEmailOrPhone());

            String role = authService.getJwtUtil().extractRole(loginResult);
            if ("ADMIN".equals(role)) {
                return "redirect:/admin/dashboard";
            } else if ("SELLER".equals(role)) {
                return "redirect:/seller/dashboard";
            } else {
                return "redirect:/dashboard";
            }
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "auth/login";
        }
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm(Model model) {
        model.addAttribute("forgotPasswordRequest", new ForgotPasswordRequest());
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String handleForgotPassword(@Valid @ModelAttribute ForgotPasswordRequest forgotPasswordRequest,
                                    BindingResult bindingResult,
                                    Model model) {
        if (bindingResult.hasErrors()) {
            return "auth/forgot-password";
        }
        try {
            authService.forgotPassword(forgotPasswordRequest);
            model.addAttribute("email", forgotPasswordRequest.getEmail());
            model.addAttribute("success", "OTP sent to your email.");
            return "auth/verify-otp";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "auth/forgot-password";
        }
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(Model model) {
        model.addAttribute("resetPasswordRequest", new ResetPasswordRequest());
        return "auth/reset-password";
    }

    @PostMapping("/reset-password")
    public String handleResetPassword(@Valid @ModelAttribute ResetPasswordRequest resetPasswordRequest,
                                    BindingResult bindingResult,
                                    Model model) {
        if (bindingResult.hasErrors()) {
            return "auth/reset-password";
        }
        try {
            authService.resetPassword(resetPasswordRequest);
            return "auth/reset-password-success";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "auth/reset-password";
        }
    }

    @PostMapping("/otp")
    public String handleOtpVerification(
            @RequestParam String digit1,
            @RequestParam String digit2,
            @RequestParam String digit3,
            @RequestParam String digit4,
            @RequestParam String digit5,
            @RequestParam String digit6,
            @RequestParam String email,
            Model model) {
        String otp = digit1 + digit2 + digit3 + digit4 + digit5 + digit6;
        try {
            authService.verifyOtp(email, otp);
            model.addAttribute("resetPasswordRequest", new ResetPasswordRequest());
            model.addAttribute("email", email);
            return "auth/reset-password";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("email", email);
            return "auth/verify-otp";
        }
    }

    @GetMapping("/auth/register-buyer")
    public String showBuyerRegisterForm(Model model) {
        model.addAttribute("buyerRegistrationDTO", new BuyerRegistrationDTO());
        return "auth/register-buyer";
    }

    @PostMapping("/auth/register-buyer")
    public String handleBuyerRegister(
            @Valid @ModelAttribute("buyerRegistrationDTO") BuyerRegistrationDTO buyerRegistrationDTO,
            BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            return "auth/register-buyer";
        }
        try {
            buyerRegistrationService.registerBuyer(buyerRegistrationDTO);
            // Redirect to OTP verification page with email after successful registration
            return "redirect:/auth/verify-otp?email=" + buyerRegistrationDTO.getEmail();
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "auth/register-buyer";
        }
    }

    @GetMapping("/auth/verify-otp")
    public String showVerifyOtp(Model model, @RequestParam(value = "email", required = false) String email) {
        if (email != null) model.addAttribute("email", email);
        return "auth/verify-otp";
    }
}

