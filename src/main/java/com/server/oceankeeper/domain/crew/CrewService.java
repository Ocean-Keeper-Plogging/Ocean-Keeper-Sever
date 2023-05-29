package com.server.oceankeeper.domain.crew;

import com.server.oceankeeper.domain.activity.entity.Activity;
import com.server.oceankeeper.domain.user.entitiy.OUser;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CrewService {
    private CrewRepository crewRepository;
    public void addCrew(Activity activity, OUser user, CrewRole host) {
        Crews crew = new Crews();

        crewRepository.save(crew);
    }

    public List<Crews> findByUser(OUser user) {
        return crewRepository.findByUser(user);
    }
}
