package com.agrilink.repository;

import com.agrilink.entity.FarmerProfile;
import com.agrilink.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FarmerProfileRepository extends JpaRepository<FarmerProfile, Long> {

    Optional<FarmerProfile> findByUserId(Long userId);

    Optional<FarmerProfile> findByUser(User user);
}
