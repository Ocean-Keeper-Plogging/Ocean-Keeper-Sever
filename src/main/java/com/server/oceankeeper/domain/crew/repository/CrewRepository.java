package com.server.oceankeeper.domain.crew;

import com.server.oceankeeper.domain.user.entitiy.OUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CrewRepository extends JpaRepository<Crews, Long>, respository.CustomCrewRepository {
    List<Crews> findByUser(OUser user);
}
