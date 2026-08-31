package com.agrilink.repository;

import com.agrilink.entity.BuyerProfile;
import com.agrilink.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BuyerProfileRepository extends JpaRepository<BuyerProfile, Long> {

    Optional<BuyerProfile> findByUserId(Long userId);

    Optional<BuyerProfile> findByUser(User user);
}
