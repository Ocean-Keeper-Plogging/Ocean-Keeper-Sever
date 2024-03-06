package com.server.oceankeeper.domain.terms.repository;

import com.server.oceankeeper.domain.terms.entity.Terms;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TermsRepository extends CrudRepository<Terms, Long>{
    Optional<Terms> findFirstByOrderByIdDesc();
}
