

package OnlineAuctionPlatform.service;

import OnlineAuctionPlatform.dto.*;
import OnlineAuctionPlatform.model.SellerProfile;
import OnlineAuctionPlatform.model.User;
import OnlineAuctionPlatform.repository.SellerProfileRepository;
import OnlineAuctionPlatform.repository.UserRepository;
import OnlineAuctionPlatform.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

        /**
         * Find a user by email or phone (helper for authentication/2FA).
         */
        public User findUserByEmailOrPhone(String emailOrPhone) {
            Optional<User> userOpt = userRepository.findByEmail(emailOrPhone);
            if (userOpt.isPresent()) return userOpt.get();
            userOpt = userRepository.findByPhone(emailOrPhone);
            return userOpt.orElse(null);
        }
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SellerProfileRepository sellerProfileRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmailService emailService;

    @Transactional
    public User registerBuyer(BuyerRegistrationDTO dto, java.util.List<String> errors) {
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            errors.add("Passwords do not match");
        }
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            errors.add("Email already registered");
        }
        if (userRepository.findByPhone(dto.getPhone()).isPresent()) {
            errors.add("Phone already registered");
        }
        if (!errors.isEmpty()) {
            return null;
        }
        User user = new User();
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(User.Role.BUYER);
        user.setStatus("ACTIVE");
        user.setEnabled(false); // Only enable after OTP verification

        String otp = generateOtp();
        user.setOtp(otp);
        user.setOtpExpiry(java.time.Instant.now().plusSeconds(600).toEpochMilli()); // 10 min

        userRepository.save(user);
        emailService.sendOtp(user.getEmail(), otp);
        return user;
    }

    @Transactional
    public void resendOtp(String email) throws Exception {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) throw new Exception("User not found");
        User user = userOpt.get();
        String otp = generateOtp();
        user.setOtp(otp);
        user.setOtpExpiry(Instant.now().plusSeconds(600).toEpochMilli()); // 10 min
        userRepository.save(user);
        emailService.sendOtp(user.getEmail(), otp);
    }

    @Transactional
    public void verifyOtp(String email, String otp) throws Exception {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) throw new Exception("User not found");
        User user = userOpt.get();
        if (user.getOtp() == null || !user.getOtp().equals(otp)) {
            throw new Exception("Invalid OTP");
        }
        if (user.getOtpExpiry() == null || user.getOtpExpiry() < Instant.now().toEpochMilli()) {
            throw new Exception("OTP expired");
        }
        user.setEnabled(true);
        user.setOtp(null);
        user.setOtpExpiry(null);
        userRepository.save(user);
    }
    @Transactional
    public User registerSellerStep1(SellerRegistrationStep1DTO dto) throws Exception {
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new Exception("Passwords do not match");
        }
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new Exception("Email already registered");
        }
        if (userRepository.findByPhone(dto.getPhone()).isPresent()) {
            throw new Exception("Phone already registered");
        }
        User user = new User();
        user.setFullName(dto.getBusinessName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(User.Role.SELLER);
        user.setStatus("PENDING");
        user.setEnabled(false);
        userRepository.save(user);

        SellerProfile profile = new SellerProfile();
        profile.setUser(user);
        profile.setBusinessName(dto.getBusinessName());
        profile.setSellerType(dto.getSellerType());
        profile.setNationalIdOrPassport(dto.getNationalIdOrPassport());
        if (dto.getCompanyRegistrationCertificate() != null && !dto.getCompanyRegistrationCertificate().isEmpty()) {
            profile.setCompanyRegistrationCertificateUrl(storeFile(dto.getCompanyRegistrationCertificate()));
        }
        profile.setTinNumber(dto.getTinNumber());
        profile.setPhysicalAddress(dto.getPhysicalAddress());
        profile.setSecondaryContact(dto.getSecondaryContact());
        sellerProfileRepository.save(profile);
        return user;
    }

    @Transactional
    public void registerSellerStep2(UUID userId, SellerRegistrationStep2DTO dto) throws Exception {
        SellerProfile profile = sellerProfileRepository.findByUserId(userId);
        if (profile == null) throw new Exception("Seller profile not found");
        profile.setBankAccountNumber(dto.getBankAccountNumber());
        profile.setBankName(dto.getBankName());
        if (dto.getProofOfAccountOwnership() != null && !dto.getProofOfAccountOwnership().isEmpty()) {
            profile.setProofOfAccountOwnershipUrl(storeFile(dto.getProofOfAccountOwnership()));
        }
        profile.setMobileMoneyNumber(dto.getMobileMoneyNumber());
        if (dto.getAuthorizationLetter() != null && !dto.getAuthorizationLetter().isEmpty()) {
            profile.setAuthorizationLetterUrl(storeFile(dto.getAuthorizationLetter()));
        }
        if (dto.getNoDisputeDeclaration() != null && !dto.getNoDisputeDeclaration().isEmpty()) {
            profile.setNoDisputeDeclarationUrl(storeFile(dto.getNoDisputeDeclaration()));
        }
        if (dto.getCourtSaleAuthorization() != null && !dto.getCourtSaleAuthorization().isEmpty()) {
            profile.setCourtSaleAuthorizationUrl(storeFile(dto.getCourtSaleAuthorization()));
        }
        sellerProfileRepository.save(profile);
    }

    @Transactional
    public void registerSellerStep3(UUID userId, SellerRegistrationStep3DTO dto) throws Exception {
        SellerProfile profile = sellerProfileRepository.findByUserId(userId);
        if (profile == null) throw new Exception("Seller profile not found");
        profile.setApplicationStatus("PENDING");
        profile.setSubmittedDate(LocalDate.now().toString());
        profile.setExpectedApprovalDate(LocalDate.now().plusDays(3).toString());
        sellerProfileRepository.save(profile);
    }

    public String login(LoginRequest request, jakarta.servlet.http.HttpSession session) throws Exception {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmailOrPhone());
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByPhone(request.getEmailOrPhone());
        }
        if (userOpt.isEmpty()) throw new Exception("User not found");
        User user = userOpt.get();
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new Exception("Invalid credentials");
        }
        if (!user.isEnabled()) throw new Exception("Account not enabled");
        Object otpVerified = session.getAttribute("OTP_VERIFIED");
        if (otpVerified != null && Boolean.TRUE.equals(otpVerified)) {
            session.removeAttribute("OTP_VERIFIED");
            return jwtUtil.generateToken(user.getEmail(), user.getRole().name(), user.getId().toString());
        }
        String otp = generateOtp();
        user.setOtp(otp);
        user.setOtpExpiry(java.time.Instant.now().plusSeconds(600).toEpochMilli());
        userRepository.save(user);
        emailService.sendOtp(user.getEmail(), otp);
        return "OTP_REQUIRED";
    }

        public JwtUtil getJwtUtil() {
        return jwtUtil;
    }
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) throws Exception {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isEmpty()) throw new Exception("User not found");
        User user = userOpt.get();
        String otp = generateOtp();
        user.setOtp(otp);
        user.setOtpExpiry(Instant.now().plusSeconds(600).toEpochMilli()); // 10 min
        userRepository.save(user);
        emailService.sendOtp(user.getEmail(), otp);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) throws Exception {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isEmpty()) throw new Exception("User not found");
        User user = userOpt.get();
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new Exception("Passwords do not match");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    private String generateOtp() {
        int otp = 100000 + (int)(Math.random() * 900000);
        return String.valueOf(otp);
    }

    private String storeFile(MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new Exception("No file provided");
        }
        String staticDir = System.getProperty("user.dir") + "/src/main/resources/static/uploads";
        java.nio.file.Path uploadDir = java.nio.file.Paths.get(staticDir);
        if (!java.nio.file.Files.exists(uploadDir)) {
            java.nio.file.Files.createDirectories(uploadDir);
        }
        String originalFilename = file.getOriginalFilename();
        String filename = java.util.UUID.randomUUID() + "_" + originalFilename;
        java.nio.file.Path destination = uploadDir.resolve(filename);
        file.transferTo(destination.toFile());
        return "/uploads/" + filename;
    }
}
