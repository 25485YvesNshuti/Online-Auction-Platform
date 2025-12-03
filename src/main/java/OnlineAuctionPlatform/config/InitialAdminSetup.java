package OnlineAuctionPlatform.config;

import OnlineAuctionPlatform.model.User;
import OnlineAuctionPlatform.repository.UserRepository;
import OnlineAuctionPlatform.repository.SellerProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class InitialAdminSetup {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private SellerProfileRepository sellerProfileRepository;

    @Bean
    public CommandLineRunner createAdminUser() {
        return args -> {
            boolean existsByEmail = userRepository.findByEmail("yvesnshuti25485@gmail.com").isPresent();
            boolean existsByPhone = userRepository.findByPhone("+250781114017").isPresent();
            if (!existsByEmail && !existsByPhone) {
                User admin = new User();
                admin.setFullName("Platform Admin");
                admin.setEmail("yvesnshuti25485@gmail.com");
                admin.setPhone("+250781114017");
                admin.setPassword(passwordEncoder.encode("Admin@2025"));
                admin.setRole(User.Role.ADMIN);
                admin.setEnabled(true);
                admin.setStatus("APPROVED");
                userRepository.save(admin);
            }
        };
    }



    @Bean
    public CommandLineRunner createNewAdmin() {
        return args -> {
            boolean existsByEmail = userRepository.findByEmail("nshutiyves70@gmail.com").isPresent();
            boolean existsByPhone = userRepository.findByPhone("+250788000001").isPresent();
            if (!existsByEmail && !existsByPhone) {
                User admin = new User();
                admin.setFullName("Platform Admin");
                admin.setEmail("nshutiyves70@gmail.com");
                admin.setPhone("+250788000001");
                admin.setPassword(passwordEncoder.encode("Yves@2025"));
                admin.setRole(User.Role.ADMIN);
                admin.setEnabled(true);
                admin.setStatus("APPROVED");
                userRepository.save(admin);
            }
        };
    }
}
