package OnlineAuctionPlatform.repository;

import OnlineAuctionPlatform.model.SellerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface SellerProfileRepository extends JpaRepository<SellerProfile, UUID> {
    SellerProfile findByUserId(UUID userId);

    @Query("SELECT s FROM SellerProfile s JOIN FETCH s.user")
    List<SellerProfile> findAllWithUser();
}
