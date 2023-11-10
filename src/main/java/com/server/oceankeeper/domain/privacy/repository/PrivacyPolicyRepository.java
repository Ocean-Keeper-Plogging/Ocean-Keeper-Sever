package com.server.oceankeeper.domain.privacy.repository;

import com.server.oceankeeper.domain.privacy.entity.PrivacyPolicy;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PrivacyPolicyRepository extends CrudRepository<PrivacyPolicy, Long>{
    Optional<PrivacyPolicy> findFirstByOrderByIdDesc();
}
