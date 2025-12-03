package OnlineAuctionPlatform.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BuyerRegistrationDTO {
    @NotBlank
    private String fullName;

    @Email
    @NotBlank
    private String email;

    @NotBlank
        @jakarta.validation.constraints.Pattern(
            regexp = "^(\\+250|0)?[7-8][0-9]{8}$",
            message = "Invalid Rwandan phone number format"
        )
    private String phone;

    @NotBlank
    private String password;

    @NotBlank
    private String confirmPassword;
}
