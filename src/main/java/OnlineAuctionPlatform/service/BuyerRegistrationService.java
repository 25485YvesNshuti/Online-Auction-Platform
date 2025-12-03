package OnlineAuctionPlatform.service;

import OnlineAuctionPlatform.dto.BuyerRegistrationDTO;
import OnlineAuctionPlatform.model.User;
import OnlineAuctionPlatform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BuyerRegistrationService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthService authService;

    @Transactional
    public User registerBuyer(BuyerRegistrationDTO dto) throws Exception {
        java.util.List<String> errors = new java.util.ArrayList<>();
        User user = authService.registerBuyer(dto, errors);
        if (!errors.isEmpty()) {
            throw new Exception(String.join(", ", errors));
        }
        // OTP is generated and sent by AuthService
        return user;
    }
}
