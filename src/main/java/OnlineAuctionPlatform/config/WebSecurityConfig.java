package OnlineAuctionPlatform.config;

import OnlineAuctionPlatform.security.JwtAuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import OnlineAuctionPlatform.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebSecurityConfig {
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/", "/register-buyer", "/auth/register-buyer", "/auth/verify-otp", "/auth/resend-otp",
                    "/register-seller-step1", "/register-seller-step2", "/register-seller-step3",
                    "/css/**", "/js/**", "/images/**", "/favicon.ico", "/static/**", "/error",
                    "/api/auth/login", "/api/auth/verify-otp", "/api/login", "/api/rest-login",
                    "/forgot-password", "/reset-password", "/auth/forgot-password", "/auth/reset-password",
                    "/otp", "/auth/otp", "/login", "/auth/login", "/2fa/**"
                ).permitAll()
                .requestMatchers("/admin/sellers/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            // Disable default formLogin so our controller handles /login
            .logout(logout -> logout.permitAll())
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/login", "/api/login", "/api/rest-login")
            );
        // Register JWT filter before UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}