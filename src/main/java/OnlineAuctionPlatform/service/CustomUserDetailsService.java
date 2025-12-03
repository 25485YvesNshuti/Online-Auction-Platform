package OnlineAuctionPlatform.service;

import OnlineAuctionPlatform.model.User;
import OnlineAuctionPlatform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username == null || username.trim().isEmpty()) {
            throw new UsernameNotFoundException("Username (email or phone) must not be empty.");
        }
        User user = userRepository.findByEmail(username).orElse(null);
        if (user == null) {
            user = userRepository.findByPhone(username).orElse(null);
        }
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email or phone: " + username);
        }
        UserBuilder builder = org.springframework.security.core.userdetails.User.withUsername(user.getEmail());
        builder.password(user.getPassword());
        builder.roles(user.getRole().name());
        return builder.build();
    }
}
