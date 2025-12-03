package OnlineAuctionPlatform.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class SellerRegistrationStep1DTO {
    @NotBlank
    private String businessName;
    @NotBlank
    private String sellerType;
    @NotBlank
    private String nationalIdOrPassport;
    private MultipartFile companyRegistrationCertificate;
    private String tinNumber;
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String phone;
    @NotBlank
    private String physicalAddress;
    private String secondaryContact;
    @NotBlank
    private String password;
    @NotBlank
    private String confirmPassword;
}
