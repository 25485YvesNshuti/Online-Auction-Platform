package OnlineAuctionPlatform.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class SellerRegistrationStep2DTO {
    // Financial
    private String bankAccountNumber;
    private String bankName;
    private MultipartFile proofOfAccountOwnership;
    private String mobileMoneyNumber;
    // Legal
    private MultipartFile authorizationLetter;
    private MultipartFile noDisputeDeclaration;
    private MultipartFile courtSaleAuthorization;
}