package OnlineAuctionPlatform.service;

import OnlineAuctionPlatform.dto.*;
import OnlineAuctionPlatform.model.SellerProfile;
import OnlineAuctionPlatform.model.User;
import OnlineAuctionPlatform.repository.SellerProfileRepository;
import OnlineAuctionPlatform.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class SellerRegistrationService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SellerProfileRepository sellerProfileRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    // Step 1: Register basic info and create user
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

    private String storeFile(MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new Exception("No file provided");
        }
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".pdf")) {
            throw new Exception("Only PDF documents are allowed. Please upload a PDF file.");
        }
        java.nio.file.Path uploadDir = java.nio.file.Paths.get("uploads");
        if (!java.nio.file.Files.exists(uploadDir)) {
            java.nio.file.Files.createDirectories(uploadDir);
        }
        String filename = java.util.UUID.randomUUID() + "_" + originalFilename;
        java.nio.file.Path destination = uploadDir.resolve(filename);
        file.transferTo(destination.toFile());
        return filename;
    }
}
