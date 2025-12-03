package OnlineAuctionPlatform.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SellerRegisterRequest {
    @NotBlank
    private String fullNameOrBusinessName;

    @NotBlank
    private String sellerType; // Individual | Corporate | Court-Authorized

    @NotBlank
    private String nationalIdOrPassport;

    private MultipartFile companyRegistrationCertificate; // If Corporate
    private String tinNumber; // Optional

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String phone;

    @NotBlank
    private String physicalAddress;
    private String secondaryContact; // Optional

    @NotBlank
    @Size(min = 8)
    private String password;

    @NotBlank
    private String confirmPassword;

    // Financial
    @NotBlank
    private String bankAccountNumber;
    @NotBlank
    private String bankName;
    private MultipartFile proofOfAccountOwnership;
    private String mobileMoneyNumber; // Optional, used for mobile payments

    // Legal
    private MultipartFile authorizationLetter;
    private MultipartFile noDisputeDeclaration;
    private MultipartFile courtSaleAuthorization; // If Court Sale

    // Auction Documents
    private MultipartFile ownershipCertificate;
    private MultipartFile valuationReport;
    private MultipartFile publicationNotice;
    private MultipartFile executionCaseDocuments; // If Applicable
}
