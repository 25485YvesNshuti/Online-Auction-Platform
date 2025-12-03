package OnlineAuctionPlatform.service;

import OnlineAuctionPlatform.model.SellerProfile;
import OnlineAuctionPlatform.model.User;
import OnlineAuctionPlatform.repository.SellerProfileRepository;
import OnlineAuctionPlatform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.lang.NonNull;

import java.util.Optional;
import java.util.UUID;

@Service
public class AdminSellerService {
        public java.util.List<SellerProfile> getAllSellerProfiles() {
            return sellerProfileRepository.findAllWithUser();
        }
    @Autowired
    private SellerProfileRepository sellerProfileRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;

    public Optional<SellerProfile> getSellerDetails(@NonNull UUID sellerId) {
        java.util.List<SellerProfile> sellers = sellerProfileRepository.findAllWithUser();
        return sellers.stream().filter(s -> s.getId().equals(sellerId)).findFirst();
    }

    @Transactional
    public boolean approveSeller(@NonNull UUID sellerId) {
        SellerProfile profile = sellerProfileRepository.findById(sellerId).orElse(null);
        if (profile == null) return false;
        profile.setApplicationStatus("APPROVED");
        sellerProfileRepository.saveAndFlush(profile);
        User user = profile.getUser();
        user.setEnabled(true);
        user.setStatus("APPROVED");
        userRepository.saveAndFlush(user);
        String subject = "Seller Application Approved";
        String body = "Dear " + user.getFullName() + ",\n\n" +
            "Congratulations! Your seller application has been approved. You now have access to create and manage auctions on our platform.\n\n" +
            "Thank you for joining us.\n\nBest regards,\nOnline Auction Platform Team";
        emailService.sendEmail(user.getEmail(), subject, body);
        return true;
    }

    @Transactional
    public boolean rejectSeller(@NonNull UUID sellerId, String reason) {
        SellerProfile profile = sellerProfileRepository.findById(sellerId).orElse(null);
        if (profile == null) return false;
        profile.setApplicationStatus("REJECTED");
        profile.setRejectionReason(reason);
        sellerProfileRepository.saveAndFlush(profile);
        User user = profile.getUser();
        user.setEnabled(false);
        user.setStatus("REJECTED");
        userRepository.saveAndFlush(user);
        String subject = "Seller Application Rejected";
        String body = "Dear " + user.getFullName() + ",\n\n" +
            "We regret to inform you that your seller application has been rejected. Reason: " + reason + ".\n\n" +
            "If you have questions or wish to reapply, please contact our support team.\n\n0781114017\nBest regards,\nOnline Auction Platform Team";
        emailService.sendEmail(user.getEmail(), subject, body);
        return true;
    }

    @Transactional
    public boolean requestMoreInfo(@NonNull UUID sellerId) {
        SellerProfile profile = sellerProfileRepository.findById(sellerId).orElse(null);
        if (profile == null) return false;
        sellerProfileRepository.saveAndFlush(profile);
        User user = profile.getUser();
        String subject = "Additional Information Required";
        String body = "Dear " + user.getFullName() + ",\n\n" +
            "We are reviewing your seller application and require additional information or documents. Please log in to your account and provide the requested details.\n\n" +
            "If you need assistance, contact our support team.\n\nBest regards,\nOnline Auction Platform Team";
        emailService.sendEmail(user.getEmail(), subject, body);
        return true;
    }
}
