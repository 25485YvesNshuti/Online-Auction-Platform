package OnlineAuctionPlatform.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "seller_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class SellerProfile extends BaseEntity {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // Identity
    private String businessName;
    private String sellerType; // Individual | Corporate | Court-Authorized
    private String nationalIdOrPassport;
    private String companyRegistrationCertificateUrl;
    private String tinNumber;

    // Contact
    private String physicalAddress;
    private String secondaryContact;

    // Financial
    private String bankAccountNumber;
    private String bankName;
    private String proofOfAccountOwnershipUrl;
    private String mobileMoneyNumber;

    // Legal
    private String authorizationLetterUrl;
    private String noDisputeDeclarationUrl;
    private String courtSaleAuthorizationUrl;

    // Auction Documents
    private String ownershipCertificateUrl;
    private String valuationReportUrl;
    private String publicationNoticeUrl;
    private String executionCaseDocumentsUrl;

    // Status
    private String applicationStatus = "PENDING";
    private String rejectionReason;
    private String submittedDate;
    private String expectedApprovalDate;
}
