
package OnlineAuctionPlatform.model;

import OnlineAuctionPlatform.model.enums.DocumentType;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;


@Entity
@Table(name = "seller_documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class SellerDocument extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_profile_id", nullable = false)
    private SellerProfile sellerProfile;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentType documentType;

    @Column(nullable = false)
    private String fileUrl;

    private String mimeType;
    private Long sizeBytes;
    private String checksum;

    private String verificationStatus = "PENDING"; // APPROVED, REJECTED, PENDING
    private String adminComment;
}
