package OnlineAuctionPlatform.model;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;


@Entity
@Table(name = "disputes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Dispute extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auction_id")
    private Auction auction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "raised_by_id")
    private User raisedBy;

    private String reason;

    @Lob
    private String details;

    private String status = "OPEN"; // OPEN, RESOLVED, REJECTED

    private LocalDateTime raisedAt = LocalDateTime.now();

    private LocalDateTime resolvedAt;
}
