package OnlineAuctionPlatform.model;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;


@Entity
@Table(name = "audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class AuditLog extends BaseEntity {

    private String actorUserId; // keep as string to avoid FK issues across services

    private String action; // e.g., AUCTION_CREATED, DOCUMENT_UPLOADED, PAYMENT_COMPLETED

    private String entity; // e.g., Auction, Payment

    private String entityId; // UUID string

    @Lob
    private String details;
}
