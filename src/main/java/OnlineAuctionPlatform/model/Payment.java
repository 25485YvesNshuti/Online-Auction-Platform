
package OnlineAuctionPlatform.model;

import OnlineAuctionPlatform.model.enums.PaymentType;
import OnlineAuctionPlatform.model.enums.PaymentMethod;
import OnlineAuctionPlatform.model.enums.PaymentStatus;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;


@Entity
@Table(name = "payments", indexes = {@Index(columnList = "transactionReference")})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Payment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auction_id")
    private Auction auction;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentType paymentType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Column(nullable = false)
    private Long amount;

    private LocalDateTime paymentDate = LocalDateTime.now();

    private String transactionReference;

    private String proofOfPaymentUrl;

    @Lob
    private String gatewayResponse;
}
